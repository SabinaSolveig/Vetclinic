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
import org.example.vetclinic.database.SpeciesDAO;
import org.example.vetclinic.model.Species;
import java.util.Optional;
public class SpeciesController {
    @FXML private TableView<Species> speciesTable;
    @FXML private TableColumn<Species, String> speciesNameColumn;
    private ObservableList<Species> speciesData = FXCollections.observableArrayList();
    private SpeciesDAO dao = new SpeciesDAO();
    @FXML
    private void initialize() {
        speciesNameColumn.setCellValueFactory(new PropertyValueFactory<>("speciesName"));
        speciesNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        speciesNameColumn.setOnEditCommit(event -> {
            Species species = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (newName.isEmpty()) {
                showAlert("Ошибка", "Название вида не может быть пустым", Alert.AlertType.ERROR);
                speciesTable.refresh();
                return;
            }
            species.setSpeciesName(newName);
            if (dao.updateSpecies(species)) {
                showAlert("Успех", "Вид животного обновлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить вид животного", Alert.AlertType.ERROR);
                loadData();
            }
        });
        speciesTable.setItems(speciesData);
        speciesTable.setEditable(true);
        loadData();
    }
    @FXML
    private void onAddButtonClick() {
        String newName = showInputDialog("Добавить вид", "Введите название вида:");
        if (newName != null && !newName.trim().isEmpty()) {
            Species newSpecies = new Species(null, newName.trim());
            if (dao.addSpecies(newSpecies)) {
                loadData();
                showAlert("Успех", "Вид животного добавлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить вид животного", Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void onDeleteButtonClick() {
        Species selectedSpecies = speciesTable.getSelectionModel().getSelectedItem();
        if (selectedSpecies != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление вида животного");
            alert.setContentText("Вы уверены, что хотите удалить вид \"" + selectedSpecies.getSpeciesName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (dao.deleteSpecies(selectedSpecies)) {
                    speciesData.remove(selectedSpecies);
                    showAlert("Успех", "Вид животного удален", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить вид животного", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите вид для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) speciesTable.getScene().getWindow();
        stage.close();
    }
    private void loadData() {
        speciesData.clear();
        speciesData.addAll(dao.getAllSpecies());
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
