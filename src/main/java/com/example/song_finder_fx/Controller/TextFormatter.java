package com.example.song_finder_fx.Controller;

import org.jetbrains.annotations.NotNull;

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
        try {
            String[] parts = input.split("\\.");
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);

                LocalTime time = LocalTime.of(hours, minutes, seconds);
                return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            } else if (parts.length == 2) {
                int hours = 0; // Default to 0 hours
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);

                LocalTime time = LocalTime.of(hours, minutes, seconds);
                return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            }
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            return "";
        }
        return "";
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
        return validateTime(trimStart) && validateTime(trimEnd);
    }


    public static @NotNull String getDaysAgo(LocalDate localDate) {
        // Get the current system date
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in days
        long daysDifference = ChronoUnit.DAYS.between(localDate, currentDate);

        // Determine the appropriate label based on the difference
        String label;
        if (daysDifference == 0) {
            label = "today";
        } else if (daysDifference == 1) {
            label = "yesterday";
        } else {
            label = Math.abs(daysDifference) + " days ago";
        }
        return label;
    }
}
