package org.example.vetclinic.util;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
public class DatePickerUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static void setupDateInputPattern(DatePicker datePicker) {
        if (datePicker == null) {
            return;
        }
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date == null) {
                    return "";
                }
                return date.format(DATE_FORMATTER);
            }
            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                String digitsOnly = string.replaceAll("[^0-9]", "");
                if (digitsOnly.length() < 8) {
                    return null;
                }
                String formatted = formatDateString(digitsOnly);
                try {
                    return LocalDate.parse(formatted, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        };
        datePicker.setConverter(converter);
        final boolean[] isUpdating = {false};
        datePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating[0]) {
                return;
            }
            if (newValue == null) {
                return;
            }
            String digitsOnly = newValue.replaceAll("[^0-9]", "");
            if (digitsOnly.length() > 8) {
                digitsOnly = digitsOnly.substring(0, 8);
            }
            String formatted = formatDateString(digitsOnly);
            if (!formatted.equals(newValue)) {
                isUpdating[0] = true;
                datePicker.getEditor().setText(formatted);
                isUpdating[0] = false;
            }
        });
    }
    private static String formatDateString(String digits) {
        if (digits == null || digits.isEmpty()) {
            return "";
        }
        if (digits.length() > 8) {
            digits = digits.substring(0, 8);
        }
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i == 2 || i == 4) {
                formatted.append(".");
            }
            formatted.append(digits.charAt(i));
        }
        return formatted.toString();
    }
}
