package org.example.vetclinic.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.example.vetclinic.model.Client;
import java.util.regex.Pattern;
public class ClientController {
    @FXML private Label idLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField addressField;
    @FXML private TextField discountPercentField;
    @FXML private TextArea notesField;
    private Client client;
    private boolean saveClicked = false;
    @FXML
    private void initialize() {
        setupNumericField(discountPercentField);
    }
    private void setupNumericField(TextField field) {
        Pattern pattern = Pattern.compile("\\d*");
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (pattern.matcher(newText).matches()) {
                return change;
            }
            return null;
        });
        field.setTextFormatter(formatter);
    }
    @FXML
    private void onSaveButtonClick() {
        if (isInputValid()) {
            Integer id = client != null ? client.getId() : null;
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            if (middleName.isEmpty()) {
                middleName = null;
            }
            String address = addressField.getText().trim();
            if (address.isEmpty()) {
                address = null;
            }
            Integer discountPercent = null;
            String discountPercentText = discountPercentField.getText().trim();
            if (!discountPercentText.isEmpty()) {
                try {
                    discountPercent = Integer.parseInt(discountPercentText);
                    if (discountPercent < 0 || discountPercent > 100) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка ввода");
                        alert.setHeaderText("Некорректное значение");
                        alert.setContentText("Процент скидки должен быть от 0 до 100!");
                        alert.showAndWait();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка ввода");
                    alert.setHeaderText("Некорректное значение");
                    alert.setContentText("Процент скидки должен быть числом!");
                    alert.showAndWait();
                    return;
                }
            }
            String notes = notesField.getText().trim();
            if (notes.isEmpty()) {
                notes = null;
            }
            client = new Client(id, firstName, lastName, middleName, address, discountPercent, notes);
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
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }
    private boolean isInputValid() {
        String errorMessage = "";
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            errorMessage += "Имя не может быть пустым!\n";
        }
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            errorMessage += "Фамилия не может быть пустой!\n";
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
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            if (client.getId() != null) {
                idLabel.setText(client.getId().toString());
            } else {
                idLabel.setText("(новый)");
            }
            firstNameField.setText(client.getFirstName() != null ? client.getFirstName() : "");
            lastNameField.setText(client.getLastName() != null ? client.getLastName() : "");
            middleNameField.setText(client.getMiddleName() != null ? client.getMiddleName() : "");
            addressField.setText(client.getAddress() != null ? client.getAddress() : "");
            if (client.getDiscountPercent() != null) {
                discountPercentField.setText(client.getDiscountPercent().toString());
            } else {
                discountPercentField.setText("");
            }
            notesField.setText(client.getNotes() != null ? client.getNotes() : "");
        } else {
            idLabel.setText("(новый)");
            firstNameField.setText("");
            lastNameField.setText("");
            middleNameField.setText("");
            addressField.setText("");
            discountPercentField.setText("");
            notesField.setText("");
        }
    }
}
