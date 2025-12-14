package org.example.vetclinic.util;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
public class TimeFieldUtils {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static List<String> generateTimeList() {
        List<String> timeList = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            timeList.add(String.format("%02d:00", hour));
            timeList.add(String.format("%02d:30", hour));
        }
        return timeList;
    }

    public static void setupTimeComboBox(ComboBox<String> timeComboBox) {
        if (timeComboBox == null) {
            return;
        }
        timeComboBox.getItems().addAll(generateTimeList());
        timeComboBox.setEditable(true);
        timeComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String time) {
                if (time == null || time.trim().isEmpty()) {
                    return "";
                }
                if (time.matches("\\d{2}:\\d{2}")) {
                    return time;
                }
                String digitsOnly = time.replaceAll("[^0-9]", "");
                return formatTimeString(digitsOnly);
            }
            @Override
            public String fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                String digitsOnly = string.replaceAll("[^0-9]", "");
                return formatTimeString(digitsOnly);
            }
        });
        final boolean[] isUpdating = {false};
        timeComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating[0]) {
                return;
            }
            if (newValue == null) {
                return;
            }
            String digitsOnly = newValue.replaceAll("[^0-9]", "");
            if (digitsOnly.length() > 4) {
                digitsOnly = digitsOnly.substring(0, 4);
            }
            String formatted = formatTimeString(digitsOnly);
            if (!formatted.equals(newValue)) {
                isUpdating[0] = true;
                timeComboBox.getEditor().setText(formatted);
                isUpdating[0] = false;
            }
        });
    }
    public static void setupTimeTextField(TextField timeField) {
        if (timeField == null) {
            return;
        }
        final boolean[] isUpdating = {false};
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating[0]) {
                return;
            }
            if (newValue == null) {
                return;
            }
            String digitsOnly = newValue.replaceAll("[^0-9]", "");
            if (digitsOnly.length() > 4) {
                digitsOnly = digitsOnly.substring(0, 4);
            }
            String formatted = formatTimeString(digitsOnly);
            if (!formatted.equals(newValue)) {
                isUpdating[0] = true;
                timeField.setText(formatted);
                isUpdating[0] = false;
            }
        });
    }
    private static String formatTimeString(String digits) {
        if (digits == null || digits.isEmpty()) {
            return "";
        }
        if (digits.length() > 4) {
            digits = digits.substring(0, 4);
        }
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i == 2) {
                formatted.append(":");
            }
            formatted.append(digits.charAt(i));
        }
        return formatted.toString();
    }
    public static LocalTime parseTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        String digitsOnly = timeString.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 4) {
            return null;
        }
        String formatted = formatTimeString(digitsOnly);
        try {
            return LocalTime.parse(formatted, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
