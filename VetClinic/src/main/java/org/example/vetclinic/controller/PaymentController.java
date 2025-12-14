package org.example.vetclinic.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.VisitDAO;
import org.example.vetclinic.model.Payment;
import org.example.vetclinic.model.Visit;
import org.example.vetclinic.util.DatePickerUtils;
import org.example.vetclinic.util.TimeFieldUtils;
import org.example.vetclinic.util.StatusTranslator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class PaymentController {
    @FXML private Label idLabel;
    @FXML private ComboBox<Visit> visitComboBox;
    @FXML private DatePicker paymentDatePicker;
    @FXML private ComboBox<String> paymentTimeComboBox;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea notesField;
    private Payment payment;
    private boolean saveClicked = false;
    private VisitDAO visitDAO = new VisitDAO();
    @FXML
    private void initialize() {
        DatePickerUtils.setupDateInputPattern(paymentDatePicker);
        visitComboBox.setConverter(new StringConverter<Visit>() {
            @Override
            public String toString(Visit visit) {
                if (visit == null) return "";
                String info = "Прием #" + visit.getId();
                if (visit.getVisitDate() != null) {
                    info += " (" + visit.getVisitDate();
                    if (visit.getStartTime() != null) {
                        info += " " + visit.getStartTime();
                    }
                    info += ")";
                }
                if (visit.getClientName() != null) {
                    info += " - " + visit.getClientName();
                }
                return info;
            }
            @Override
            public Visit fromString(String string) {
                return null;
            }
        });
        statusComboBox.getItems().addAll(
            StatusTranslator.translatePaymentStatus("Pending"),
            StatusTranslator.translatePaymentStatus("Completed"),
            StatusTranslator.translatePaymentStatus("Cancelled"),
            StatusTranslator.translatePaymentStatus("Refunded")
        );
        statusComboBox.setValue(StatusTranslator.translatePaymentStatus("Pending"));
        statusComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String russianStatus) {
                return russianStatus;
            }
            @Override
            public String fromString(String russianStatus) {
                return StatusTranslator.translatePaymentStatusFromRussian(russianStatus);
            }
        });
        paymentMethodComboBox.getItems().addAll("Наличные", "Карта", "Банковский перевод", "Электронный платеж");
        paymentMethodComboBox.setValue("Наличные");
        visitComboBox.valueProperty().addListener((_, _, newVisit) -> {
            if (newVisit != null && newVisit.getTotalCost() != null && 
                (amountField.getText().isEmpty() || amountField.getText().equals("0.00"))) {
                amountField.setText(newVisit.getTotalCost().toString());
            }
        });
        loadData();
    }
    @FXML
    private void onSaveButtonClick() {
        if (isInputValid()) {
            Integer id = payment != null ? payment.getId() : null;
            Visit selectedVisit = visitComboBox.getValue();
            LocalDate date = paymentDatePicker.getValue();
            LocalTime time = null;
            String timeText = paymentTimeComboBox.getValue();
            if (timeText != null && !timeText.trim().isEmpty()) {
                time = TimeFieldUtils.parseTime(timeText);
                if (time == null) {
                    showAlert("Ошибка", "Некорректный формат времени. Используйте формат HH:mm", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                time = LocalTime.now();
            }
            LocalDateTime paymentDateTime = LocalDateTime.of(date != null ? date : LocalDate.now(), time);
            BigDecimal amount = null;
            try {
                amount = new BigDecimal(amountField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    showAlert("Ошибка", "Сумма не может быть отрицательной", Alert.AlertType.ERROR);
                    return;
                }
            } catch (Exception e) {
                showAlert("Ошибка", "Некорректный формат суммы", Alert.AlertType.ERROR);
                return;
            }
            Integer paymentMethodId = null;
            String russianStatus = statusComboBox.getValue();
            String status;
            if (russianStatus == null || russianStatus.isEmpty()) {
                status = "Pending";
            } else {
                status = StatusTranslator.translatePaymentStatusFromRussian(russianStatus);
            }
            String notes = notesField.getText().trim();
            if (notes.isEmpty()) {
                notes = null;
            }
            payment = new Payment(id, selectedVisit.getId(), paymentDateTime, amount, paymentMethodId, status, notes);
            saveClicked = true;
            closeDialog();
        }
    }
    @FXML
    private void onCancelButtonClick() {
        closeDialog();
    }
    public boolean isSaveClicked() {
        return saveClicked;
    }
    private void closeDialog() {
        Stage stage = (Stage) visitComboBox.getScene().getWindow();
        stage.close();
    }
    private boolean isInputValid() {
        String errorMessage = "";
        if (visitComboBox.getValue() == null) {
            errorMessage += "Необходимо выбрать прием!\n";
        }
        if (paymentDatePicker.getValue() == null) {
            errorMessage += "Необходимо указать дату оплаты!\n";
        }
        if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
            errorMessage += "Необходимо указать сумму оплаты!\n";
        }
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText("Пожалуйста, исправьте следующие поля:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
    public Payment getPayment() {
        return payment;
    }
    public void setPaymentFromVisit(Visit visit) {
        if (visit == null) {
            return;
        }
        this.payment = null;
        idLabel.setText("(новый)");
        visitComboBox.setValue(visit);
        paymentDatePicker.setValue(LocalDate.now());
        String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        paymentTimeComboBox.setValue(timeStr);
        if (visit.getTotalCost() != null) {
            amountField.setText(visit.getTotalCost().toString());
        } else {
            amountField.setText("0.00");
        }
        paymentMethodComboBox.setValue("Наличные");
        statusComboBox.setValue(StatusTranslator.translatePaymentStatus("Completed"));
        notesField.setText("");
    }
    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            if (payment.getId() != null) {
                idLabel.setText(payment.getId().toString());
            } else {
                idLabel.setText("(новый)");
            }
            List<Visit> allVisits = visitDAO.getAllVisits();
            Visit paymentVisit = allVisits.stream()
                .filter(v -> v.getId().equals(payment.getVisitId()))
                .findFirst()
                .orElse(null);
            visitComboBox.setValue(paymentVisit);
            if (payment.getPaymentDate() != null) {
                paymentDatePicker.setValue(payment.getPaymentDate().toLocalDate());
                String timeStr = payment.getPaymentDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                paymentTimeComboBox.setValue(timeStr);
            } else {
                paymentDatePicker.setValue(LocalDate.now());
                String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                paymentTimeComboBox.setValue(timeStr);
            }
            amountField.setText(payment.getAmount() != null ? payment.getAmount().toString() : "0.00");
            if (payment.getPaymentMethodName() != null) {
                paymentMethodComboBox.setValue(payment.getPaymentMethodName());
            } else {
                paymentMethodComboBox.setValue("Наличные");
            }
            if (payment.getStatus() != null) {
                statusComboBox.setValue(StatusTranslator.translatePaymentStatus(payment.getStatus()));
            } else {
                statusComboBox.setValue(StatusTranslator.translatePaymentStatus("Pending"));
            }
            notesField.setText(payment.getNotes() != null ? payment.getNotes() : "");
        } else {
            idLabel.setText("(новый)");
            visitComboBox.setValue(null);
            paymentDatePicker.setValue(LocalDate.now());
            String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            paymentTimeComboBox.setValue(timeStr);
            amountField.setText("0.00");
            paymentMethodComboBox.setValue("Наличные");
            statusComboBox.setValue(StatusTranslator.translatePaymentStatus("Pending"));
            notesField.setText("");
        }
    }
    private void loadData() {
        List<Visit> visits = visitDAO.getAllVisits();
        visitComboBox.getItems().setAll(visits);
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
