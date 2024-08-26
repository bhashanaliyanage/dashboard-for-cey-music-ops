package com.example.song_finder_fx.Model;

import java.util.List;

public record ValidationResult(boolean isValid, List<String> errorMessages) {

}
