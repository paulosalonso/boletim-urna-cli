package com.github.paulosalonso.election.output.file;

import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;

@Slf4j
public class FileCreator {

    private static final String STATE = "state";
    private static final String CITY = "city";
    private static final String ZONE = "zone";
    private static final String SECTION = "section";
    private static final String SUFFIX = "suffix";
    private static final String EXTENSION = "extension";
    private static final String FILE_NAME = "fileName";
    private static final String PATH = "path";
    private static final String BU_EXTENSION = "bu";
    private static final String JSON_EXTENSION = "json";

    private static final String FILE_NAME_PATTERN = "o00407-${city}${zone}${section}${suffix}.${extension}";
    private static final String FILE_PATH_PATTERN = "/${state}/${city}/${fileName}";

    private static final String ROOT_DIRECTORY_DOES_NOT_EXISTS = "Root directory does not exists or is not a directory [${path}]";
    private static final String ERROR_CREATING_DIRECTORY = "Error creating directory ${path}";
    private static final String ERROR_SAVING_FILE = "Error saving file [${path}]";

    private final String rootDirectory;

    public FileCreator(String rootDirectory) {
        final var file = new File(rootDirectory);

        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException(
                    MessageFormatter.format(ROOT_DIRECTORY_DOES_NOT_EXISTS, PATH, rootDirectory));
        }

        this.rootDirectory = rootDirectory;
    }

    public Path saveAsBuFile(PollingPlace pollingPlace, InputStream inputStream, String fileNameSuffix) {
        var fileAbsolutePath = getFilePath(pollingPlace, fileNameSuffix, BU_EXTENSION);

        createDirectoryIfNecessary(fileAbsolutePath);

        try (final var outputStream = new FileOutputStream(fileAbsolutePath.toFile())) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return fileAbsolutePath;
        } catch (Exception e) {
            throw new FileCreationException(format(ERROR_SAVING_FILE, PATH, fileAbsolutePath.toString()), e);
        }
    }

    public Path saveAsJsonFile(PollingPlace pollingPlace, String json, String fileNameSuffix) {
        var fileAbsolutePath = getFilePath(pollingPlace, fileNameSuffix, JSON_EXTENSION);

        createDirectoryIfNecessary(fileAbsolutePath);

        try (final var outputStream = new FileOutputStream(fileAbsolutePath.toFile())) {
            outputStream.write(json.getBytes());

            return fileAbsolutePath;
        } catch (Exception e) {
            throw new FileCreationException(format(ERROR_SAVING_FILE, PATH, fileAbsolutePath.toString()), e);
        }
    }

    private Path getFilePath(PollingPlace pollingPlace, String fileNameSuffix, String extension) {
        final var fileName = format(FILE_NAME_PATTERN,
                CITY, pollingPlace.getCityCode(),
                ZONE, pollingPlace.getZone(),
                SECTION, pollingPlace.getSection(),
                SUFFIX, fileNameSuffix,
                EXTENSION, extension);

        final var filePath = format(FILE_PATH_PATTERN,
                STATE, pollingPlace.getState(),
                CITY, pollingPlace.getCityName(),
                FILE_NAME, fileName);

        return Path.of(rootDirectory, filePath);
    }

    private void createDirectoryIfNecessary(Path filePath) {
        var directoryPath = removeFileName(filePath);

        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new FileCreationException(format(ERROR_CREATING_DIRECTORY, PATH, directoryPath.toString()), e);
        }
    }

    private Path removeFileName(Path path) {
        return path.getParent();
    }
}
