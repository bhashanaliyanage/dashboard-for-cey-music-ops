package com.example.song_finder_fx.Controller;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {
    public static void main(String[] args) {
        // System.out.println("Formatted start time: " + formatTime("00:02:00"));
        boolean status = validateTime("05:20:00");
        System.out.println("status = " + status);
    }

    public static boolean validateTime(String input) {
        try {
            // Parse the input string as a LocalTime using the "HH:mm:ss" format
            LocalTime.parse(input);
            return true; // Valid time format
        } catch (DateTimeParseException e) {
            return false; // Invalid time format
        }
    }

    public static String formatTime(String input) {
        if (!validateTime(input)) {
            try {
                String[] parts;

                if (input.contains(".")) {
                    // Split by dot
                    parts = input.split("\\.");
                } else if (input.contains(":")) {
                    // Split by colon
                    parts = input.split(":");
                } else {
                    return ""; // Invalid format
                }

                int hours = 0, minutes, seconds;

                if (parts.length == 3) {
                    hours = Integer.parseInt(parts[0]);
                    minutes = Integer.parseInt(parts[1]);
                    seconds = Integer.parseInt(parts[2]);

                    LocalTime time = LocalTime.of(hours, minutes, seconds);
                    return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                } else if (parts.length == 2) {
                    minutes = Integer.parseInt(parts[0]);
                    seconds = Integer.parseInt(parts[1]);

                    LocalTime time = LocalTime.of(hours, minutes, seconds);
                    return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                }
            } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
                return "";
            }
        } else {
            return input;
        }
        return input;
    }

    // Extract YouTube ID from URL
    public static String extractYoutubeID(String url) {
        // Regular expression to match YouTube video URLs
        String pattern = "^https?://(?:www\\.|m\\.)?(?:youtube\\.com|youtu\\.be)/(?:watch\\?v=|embed/|v/|watch\\?.*v=|)([^&?/]+).*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1); // Extract the video ID
        } else {
            return null; // No valid video ID found
        }
    }

    public static boolean validateTrimTimes(String trimStart, String trimEnd) {
        // return validateTime(trimStart) && validateTime(trimEnd);

        if (!validateTime(trimStart) || !validateTime(trimEnd)) {
            return false;
        }

        LocalTime start = LocalTime.parse(trimStart);
        LocalTime end = LocalTime.parse(trimEnd);

        Duration duration = Duration.between(start, end);
        return duration.getSeconds() >= 50;
    }


    public static @NotNull String getDaysAgo(LocalDate localDate) {
        // Get the current system date
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in days
        long daysDifference = ChronoUnit.DAYS.between(localDate, currentDate);

        // Determine the appropriate label based on the difference
        String label;
        if (daysDifference == 0) {
            label = "Today";
        } else if (daysDifference == 1) {
            label = "Yesterday";
        } else {
            label = Math.abs(daysDifference) + " Days Ago";
        }
        return label;
    }
}
