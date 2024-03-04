package com.example.song_finder_fx;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputEvent;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeSpinner extends SpinnerValueFactory<Integer> {
    private StringConverter<Integer> converter;

    public TimeSpinner() {
        super();
        this.converter = new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                int hours = object / 3600;
                int minutes = (object % 3600) / 60;
                int seconds = object % 60;
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }

            @Override
            public Integer fromString(String string) {
                String[] parts = string.split(":");
                return Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
            }
        };
        setConverter(converter);
    }

    @Override
    public void decrement(int steps) {

    }

    @Override
    public void increment(int steps) {

    }
}
