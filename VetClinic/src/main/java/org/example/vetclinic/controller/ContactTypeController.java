package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.vetclinic.database.ContactTypeDAO;
import org.example.vetclinic.model.ContactType;
import java.util.Optional;
public class ContactTypeController {
    @FXML private TableView<ContactType> contactTypeTable;
    @FXML private TableColumn<ContactType, String> contactTypeNameColumn;
    @FXML private TableColumn<ContactType, String> descriptionColumn;
    private ObservableList<ContactType> contactTypeData = FXCollections.observableArrayList();
    private ContactTypeDAO dao = new ContactTypeDAO();
    @FXML
    private void initialize() {
        contactTypeNameColumn.setCellValueFactory(new PropertyValueFactory<>("contactTypeName"));
        contactTypeNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contactTypeNameColumn.setOnEditCommit(event -> {
            ContactType contactType = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (newName.isEmpty()) {
                showAlert("Ошибка", "Название вида контактной информации не может быть пустым", Alert.AlertType.ERROR);
                contactTypeTable.refresh();
                return;
            }
            contactType.setContactTypeName(newName);
            if (dao.updateContactType(contactType)) {
                showAlert("Успех", "Вид контактной информации обновлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить вид контактной информации", Alert.AlertType.ERROR);
                loadData();
            }
        });
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            ContactType contactType = event.getRowValue();
            String newDescription = event.getNewValue().trim();
            if (newDescription.isEmpty()) {
                newDescription = null;
            }
            contactType.setDescription(newDescription);
            if (dao.updateContactType(contactType)) {
                showAlert("Успех", "Вид контактной информации обновлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить вид контактной информации", Alert.AlertType.ERROR);
                loadData();
            }
        });
        contactTypeTable.setItems(contactTypeData);
        contactTypeTable.setEditable(true);
        loadData();
    }
    @FXML
    private void onAddButtonClick() {
        String newName = showInputDialog("Добавить вид контактной информации", "Введите название вида:");
        if (newName != null && !newName.trim().isEmpty()) {
            ContactType newContactType = new ContactType(null, newName.trim(), null);
            if (dao.addContactType(newContactType)) {
                loadData();
                showAlert("Успех", "Вид контактной информации добавлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить вид контактной информации", Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void onDeleteButtonClick() {
        ContactType selectedContactType = contactTypeTable.getSelectionModel().getSelectedItem();
        if (selectedContactType != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление вида контактной информации");
            alert.setContentText("Вы уверены, что хотите удалить вид \"" + selectedContactType.getContactTypeName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (dao.deleteContactType(selectedContactType)) {
                    contactTypeData.remove(selectedContactType);
                    showAlert("Успех", "Вид контактной информации удален", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить вид контактной информации", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите вид для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) contactTypeTable.getScene().getWindow();
        stage.close();
    }
    private void loadData() {
        contactTypeData.clear();
        contactTypeData.addAll(dao.getAllContactTypes());
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private String showInputDialog(String title, String message) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
