package com.github.paulosalonso.election.output.file;

import com.github.paulosalonso.election.output.http.client.tse.model.PollingPlace;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.paulosalonso.election.tools.text.MessageFormatter.format;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class FileCreator {

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

    private static final String ROOT_DIRECTORY_DOES_NOT_EXISTS = "Root directory does not exists [${path}]";
    private static final String ERROR_CREATING_DIRECTORY = "Error creating directory ${path}";
    private static final String ERROR_SAVING_FILE = "Error saving file [${path}]";

    private static Path rootPath = Path.of(System.getProperty("java.io.tmpdir"), "eleicoes_2022");

    /**
     * Set a root path to save all files.
     *
     * @param path Path where all files will be saved
     */
    public static void setRootPath(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException(
                    format(ROOT_DIRECTORY_DOES_NOT_EXISTS, PATH, path.toString()));
        }

        FileCreator.rootPath = path;
    }

    /**
     * Save inputStream content as a BU file.
     * <br/>
     * File will be saved in the following path: /${rootPath}/${state}/${city}/o00407-${city}${zone}${section}${suffix}.bu
     *
     * See {@link #setRootPath(Path) setRootPath(path)} for more information about output directory.
     *
     * @param pollingPlace Polling place to extract information to make filename
     * @param inputStream Bulletin content
     * @param fileNameSuffix A custom suffix for file name
     *
     */
    public static Path saveAsBuFile(PollingPlace pollingPlace, InputStream inputStream, String fileNameSuffix) {
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

    /**
     * Save inputStream content as a JSON file.
     * <br/>
     * File will be saved in the following path: /${rootPath}/${state}/${city}/o00407-${city}${zone}${section}${suffix}.json
     *
     * See {@link #setRootPath(Path) setRootPath(path)} for more information about output directory.
     *
     * @param pollingPlace Polling place to extract information to make filename
     * @param bulletin Bulletin content
     * @param fileNameSuffix A custom suffix for file name
     *
     */
    public static Path saveAsJsonFile(PollingPlace pollingPlace, String json, String fileNameSuffix) {
        var fileAbsolutePath = getFilePath(pollingPlace, fileNameSuffix, JSON_EXTENSION);

        createDirectoryIfNecessary(fileAbsolutePath);

        try (final var outputStream = new FileOutputStream(fileAbsolutePath.toFile())) {
            outputStream.write(json.getBytes());

            return fileAbsolutePath;
        } catch (Exception e) {
            throw new FileCreationException(format(ERROR_SAVING_FILE, PATH, fileAbsolutePath.toString()), e);
        }
    }

    private static Path getFilePath(PollingPlace pollingPlace, String fileNameSuffix, String extension) {
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

        return Path.of(rootPath.toString(), filePath);
    }

    private static void createDirectoryIfNecessary(Path filePath) {
        var directoryPath = removeFileName(filePath);

        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new FileCreationException(format(ERROR_CREATING_DIRECTORY, PATH, directoryPath.toString()), e);
        }
    }

    private static Path removeFileName(Path path) {
        return path.getParent();
    }
}
