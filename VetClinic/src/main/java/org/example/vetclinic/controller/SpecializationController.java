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
import org.example.vetclinic.database.SpecializationDAO;
import org.example.vetclinic.model.Specialization;
import java.util.Optional;
public class SpecializationController {
    @FXML private TableView<Specialization> specializationTable;
    @FXML private TableColumn<Specialization, String> specializationNameColumn;
    @FXML private TableColumn<Specialization, String> descriptionColumn;
    private ObservableList<Specialization> specializationData = FXCollections.observableArrayList();
    private SpecializationDAO dao = new SpecializationDAO();
    @FXML
    private void initialize() {
        specializationNameColumn.setCellValueFactory(new PropertyValueFactory<>("specializationName"));
        specializationNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        specializationNameColumn.setOnEditCommit(event -> {
            Specialization specialization = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (newName.isEmpty()) {
                showAlert("Ошибка", "Название специальности не может быть пустым", Alert.AlertType.ERROR);
                specializationTable.refresh();
                return;
            }
            specialization.setSpecializationName(newName);
            if (dao.updateSpecialization(specialization)) {
                showAlert("Успех", "Специальность обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить специальность", Alert.AlertType.ERROR);
                loadData();
            }
        });
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            Specialization specialization = event.getRowValue();
            String newDescription = event.getNewValue().trim();
            if (newDescription.isEmpty()) {
                newDescription = null;
            }
            specialization.setDescription(newDescription);
            if (dao.updateSpecialization(specialization)) {
                showAlert("Успех", "Специальность обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить специальность", Alert.AlertType.ERROR);
                loadData();
            }
        });
        specializationTable.setItems(specializationData);
        specializationTable.setEditable(true);
        loadData();
    }
    @FXML
    private void onAddButtonClick() {
        String newName = showInputDialog("Добавить специальность", "Введите название специальности:");
        if (newName != null && !newName.trim().isEmpty()) {
            Specialization newSpecialization = new Specialization(null, newName.trim(), null);
            if (dao.addSpecialization(newSpecialization)) {
                loadData();
                showAlert("Успех", "Специальность добавлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить специальность", Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void onDeleteButtonClick() {
        Specialization selectedSpecialization = specializationTable.getSelectionModel().getSelectedItem();
        if (selectedSpecialization != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление специальности");
            alert.setContentText("Вы уверены, что хотите удалить специальность \"" + selectedSpecialization.getSpecializationName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (dao.deleteSpecialization(selectedSpecialization)) {
                    specializationData.remove(selectedSpecialization);
                    showAlert("Успех", "Специальность удалена", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить специальность", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите специальность для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) specializationTable.getScene().getWindow();
        stage.close();
    }
    private void loadData() {
        specializationData.clear();
        specializationData.addAll(dao.getAllSpecializations());
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
