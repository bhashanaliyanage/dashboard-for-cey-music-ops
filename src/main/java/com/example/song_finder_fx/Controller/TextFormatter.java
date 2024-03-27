package com.example.song_finder_fx.Controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TextFormatter {
    public static void main(String[] args) {
        String input1 = "0.5.2"; // Example input
        String input2 = "5.2";   // Example input

        System.out.println("Formatted start time: " + formatTime("input1"));
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
}
