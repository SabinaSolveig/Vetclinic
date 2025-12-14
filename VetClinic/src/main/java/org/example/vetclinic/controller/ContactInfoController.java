package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.ContactInfoDAO;
import org.example.vetclinic.database.ContactTypeDAO;
import org.example.vetclinic.model.ContactInfo;
import org.example.vetclinic.model.ContactType;
import java.util.Optional;
import java.util.stream.Collectors;
public class ContactInfoController {
    @FXML private Label clientNameLabel;
    @FXML private TableView<ContactInfo> contactInfoTable;
    @FXML private TableColumn<ContactInfo, String> contactTypeColumn;
    @FXML private TableColumn<ContactInfo, String> contactValueColumn;
    @FXML private TableColumn<ContactInfo, Boolean> isPrimaryColumn;
    @FXML private TableColumn<ContactInfo, String> notesColumn;
    private ObservableList<ContactInfo> contactInfoData = FXCollections.observableArrayList();
    private ObservableList<ContactType> contactTypeData = FXCollections.observableArrayList();
    private ContactInfoDAO contactInfoDAO = new ContactInfoDAO();
    private ContactTypeDAO contactTypeDAO = new ContactTypeDAO();
    private Integer clientId;
    private String clientName;
    @FXML
    private void initialize() {
        loadContactTypes();
        contactTypeColumn.setCellValueFactory(new PropertyValueFactory<>("contactTypeName"));
        contactTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(contactTypeData.stream()
                .map(ContactType::getContactTypeName)
                .collect(Collectors.toList()))
        ));
        contactTypeColumn.setOnEditCommit(event -> {
            ContactInfo contactInfo = event.getRowValue();
            String newContactTypeName = event.getNewValue();
            ContactType selectedType = contactTypeData.stream()
                .filter(t -> t.getContactTypeName().equals(newContactTypeName))
                .findFirst()
                .orElse(null);
            if (selectedType == null) {
                showAlert("Ошибка", "Вид контакта не найден", Alert.AlertType.ERROR);
                contactInfoTable.refresh();
                return;
            }
            contactInfo.setContactTypeId(selectedType.getId());
            contactInfo.setContactTypeName(newContactTypeName);
            if (contactInfoDAO.updateContactInfo(contactInfo)) {
                showAlert("Успех", "Контактная информация обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить контактную информацию", Alert.AlertType.ERROR);
                loadData();
            }
        });
        contactValueColumn.setCellValueFactory(new PropertyValueFactory<>("contactValue"));
        contactValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contactValueColumn.setOnEditCommit(event -> {
            ContactInfo contactInfo = event.getRowValue();
            String newValue = event.getNewValue().trim();
            if (newValue.isEmpty()) {
                showAlert("Ошибка", "Значение контакта не может быть пустым", Alert.AlertType.ERROR);
                contactInfoTable.refresh();
                return;
            }
            contactInfo.setContactValue(newValue);
            if (contactInfoDAO.updateContactInfo(contactInfo)) {
                showAlert("Успех", "Контактная информация обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить контактную информацию", Alert.AlertType.ERROR);
                loadData();
            }
        });
        isPrimaryColumn.setCellValueFactory(new PropertyValueFactory<>("isPrimary"));
        isPrimaryColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isPrimaryColumn));
        isPrimaryColumn.setOnEditCommit(event -> {
            ContactInfo contactInfo = event.getRowValue();
            contactInfo.setIsPrimary(event.getNewValue());
            if (contactInfoDAO.updateContactInfo(contactInfo)) {
                showAlert("Успех", "Контактная информация обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить контактную информацию", Alert.AlertType.ERROR);
                loadData();
            }
        });
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        notesColumn.setOnEditCommit(event -> {
            ContactInfo contactInfo = event.getRowValue();
            String newNotes = event.getNewValue().trim();
            if (newNotes.isEmpty()) {
                newNotes = null;
            }
            contactInfo.setNotes(newNotes);
            if (contactInfoDAO.updateContactInfo(contactInfo)) {
                showAlert("Успех", "Контактная информация обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить контактную информацию", Alert.AlertType.ERROR);
                loadData();
            }
        });
        contactInfoTable.setItems(contactInfoData);
        contactInfoTable.setEditable(true);
    }
    @FXML
    private void onAddButtonClick() {
        if (clientId == null) {
            showAlert("Ошибка", "Клиент не выбран", Alert.AlertType.ERROR);
            return;
        }
        javafx.scene.control.Dialog<ContactInfo> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить контактную информацию");
        dialog.setHeaderText(null);
        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Добавить", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);
        ComboBox<ContactType> contactTypeComboBox = new ComboBox<>(contactTypeData);
        contactTypeComboBox.setConverter(new StringConverter<ContactType>() {
            @Override
            public String toString(ContactType type) {
                return type == null ? "" : type.getContactTypeName();
            }
            @Override
            public ContactType fromString(String string) {
                return null;
            }
        });
        contactTypeComboBox.setPromptText("Выберите вид контакта");
        contactTypeComboBox.setPrefWidth(300);
        javafx.scene.control.TextField contactValueField = new javafx.scene.control.TextField();
        contactValueField.setPromptText("Значение контакта");
        contactValueField.setPrefWidth(300);
        CheckBox isPrimaryCheckBox = new CheckBox("Основной контакт");
        javafx.scene.control.TextArea notesArea = new javafx.scene.control.TextArea();
        notesArea.setPromptText("Заметки (необязательно)");
        notesArea.setPrefRowCount(3);
        notesArea.setPrefWidth(300);
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Вид контакта:"),
            contactTypeComboBox,
            new javafx.scene.control.Label("Значение:"),
            contactValueField,
            isPrimaryCheckBox,
            new javafx.scene.control.Label("Заметки:"),
            notesArea
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        contactTypeComboBox.valueProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal == null || contactValueField.getText().trim().isEmpty()));
        contactValueField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal.trim().isEmpty() || contactTypeComboBox.getValue() == null));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                ContactType selectedType = contactTypeComboBox.getValue();
                String contactValue = contactValueField.getText().trim();
                if (selectedType != null && !contactValue.isEmpty()) {
                    String notes = notesArea.getText().trim();
                    if (notes.isEmpty()) {
                        notes = null;
                    }
                    return new ContactInfo(null, "Client", clientId, selectedType.getId(),
                                          contactValue, isPrimaryCheckBox.isSelected(), notes,
                                          selectedType.getContactTypeName());
                }
            }
            return null;
        });
        Optional<ContactInfo> result = dialog.showAndWait();
        result.ifPresent(newContact -> {
            if (contactInfoDAO.addContactInfo(newContact)) {
                loadData();
                showAlert("Успех", "Контактная информация добавлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить контактную информацию", Alert.AlertType.ERROR);
            }
        });
    }
    @FXML
    private void onDeleteButtonClick() {
        ContactInfo selectedContact = contactInfoTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление контактной информации");
            alert.setContentText("Вы уверены, что хотите удалить контакт \"" + selectedContact.getContactValue() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (contactInfoDAO.deleteContactInfo(selectedContact)) {
                    contactInfoData.remove(selectedContact);
                    showAlert("Успех", "Контактная информация удалена", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить контактную информацию", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите контакт для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) contactInfoTable.getScene().getWindow();
        stage.close();
    }
    public void setClient(Integer clientId, String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
        if (clientNameLabel != null) {
            clientNameLabel.setText("Клиент: " + (clientName != null ? clientName : ""));
        }
        loadData();
    }
    private void loadData() {
        contactInfoData.clear();
        if (clientId != null) {
            contactInfoData.addAll(contactInfoDAO.getContactInfoByClient(clientId));
        }
    }
    private void loadContactTypes() {
        contactTypeData.clear();
        contactTypeData.addAll(contactTypeDAO.getAllContactTypes());
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
