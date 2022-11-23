package com.github.paulosalonso.election.service;

import com.github.paulosalonso.election.tools.text.MessageFormatter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class ProgressMonitorService {

    private static final String PROGRESS = "progress";
    private static final String PROGRESS_SIGN = "-";
    private static final String EMPTY = "empty";
    private static final String EMPTY_SIGN = " ";
    private static final String PERCENT = "percent";
    private static final String CURRENT = "current";
    private static final String TOTAL = "total";
    private static final String ELAPSED_TIME = "elapsedTime";
    private static final String REMAINING_TIME = "remainingTime";

    private static final String PROGRESS_VIEW_PATTERN = "[${progress}>${empty}] ${percent}% (${current}/${total}) Elapsed time: ${elapsedTime} Remaining time: ${remainingTime}";

    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static <T> void runMonitoringProgress(List<T> input, Consumer<T> consumer) {
        final var startedAt = System.currentTimeMillis();
        final var totalSize = input.size();

        for (var i = 0; i < input.size(); i++) {
            final var value = input.get(i);

            consumer.accept(value);

            int current = i + 1;

            final var progress =  (int) (((float) current / (float) totalSize) * 100);

            final var elapsedMillis = System.currentTimeMillis() - startedAt;
            final var averageMillis = elapsedMillis / current;
            final var remainingMillis = averageMillis * (totalSize - current);

            final var elapsedTime = LocalTime.ofNanoOfDay(Duration.ofMillis(elapsedMillis).toNanos());
            final var remainingTime = LocalTime.ofNanoOfDay(Duration.ofMillis(remainingMillis).toNanos());

            log.info(MessageFormatter.format(PROGRESS_VIEW_PATTERN,
                    PROGRESS, PROGRESS_SIGN.repeat(progress),
                    EMPTY, EMPTY_SIGN.repeat(100 - progress),
                    PERCENT, Integer.toString(progress),
                    CURRENT, Integer.toString(current),
                    TOTAL, Integer.toString(totalSize),
                    ELAPSED_TIME, elapsedTime.format(LOCAL_TIME_FORMATTER),
                    REMAINING_TIME, remainingTime.format(LOCAL_TIME_FORMATTER)));
        }
    }
}
