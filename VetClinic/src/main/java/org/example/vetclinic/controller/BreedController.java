package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.vetclinic.database.BreedDAO;
import org.example.vetclinic.database.SpeciesDAO;
import org.example.vetclinic.model.Breed;
import org.example.vetclinic.model.Species;
import java.util.Optional;
import java.util.stream.Collectors;
public class BreedController {
    @FXML private TableView<Breed> breedTable;
    @FXML private TableColumn<Breed, String> breedNameColumn;
    @FXML private TableColumn<Breed, String> speciesNameColumn;
    private ObservableList<Breed> breedData = FXCollections.observableArrayList();
    private ObservableList<Species> speciesData = FXCollections.observableArrayList();
    private BreedDAO breedDAO = new BreedDAO();
    private SpeciesDAO speciesDAO = new SpeciesDAO();
    @FXML
    private void initialize() {
        loadSpecies();
        breedNameColumn.setCellValueFactory(new PropertyValueFactory<>("breedName"));
        breedNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        breedNameColumn.setOnEditCommit(event -> {
            Breed breed = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (newName.isEmpty()) {
                showAlert("Ошибка", "Название породы не может быть пустым", Alert.AlertType.ERROR);
                breedTable.refresh();
                return;
            }
            breed.setBreedName(newName);
            if (breedDAO.updateBreed(breed)) {
                showAlert("Успех", "Порода обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить породу", Alert.AlertType.ERROR);
                loadData();
            }
        });
        speciesNameColumn.setCellValueFactory(new PropertyValueFactory<>("speciesName"));
        speciesNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(speciesData.stream()
                .map(Species::getSpeciesName)
                .collect(Collectors.toList()))
        ));
        speciesNameColumn.setOnEditCommit(event -> {
            Breed breed = event.getRowValue();
            String newSpeciesName = event.getNewValue();
            Species selectedSpecies = speciesData.stream()
                .filter(s -> s.getSpeciesName().equals(newSpeciesName))
                .findFirst()
                .orElse(null);
            if (selectedSpecies == null) {
                showAlert("Ошибка", "Вид не найден", Alert.AlertType.ERROR);
                breedTable.refresh();
                return;
            }
            breed.setSpeciesId(selectedSpecies.getId());
            breed.setSpeciesName(newSpeciesName);
            if (breedDAO.updateBreed(breed)) {
                showAlert("Успех", "Порода обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить породу", Alert.AlertType.ERROR);
                loadData();
            }
        });
        breedTable.setItems(breedData);
        breedTable.setEditable(true);
        loadData();
    }
    @FXML
    private void onAddButtonClick() {
        javafx.scene.control.Dialog<Breed> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить породу");
        dialog.setHeaderText(null);
        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Добавить", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);
        ComboBox<Species> speciesComboBox = new ComboBox<>(speciesData);
        speciesComboBox.setPromptText("Выберите вид");
        speciesComboBox.setPrefWidth(300);
        javafx.scene.control.TextField breedNameField = new javafx.scene.control.TextField();
        breedNameField.setPromptText("Название породы");
        breedNameField.setPrefWidth(300);
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Вид:"),
            speciesComboBox,
            new javafx.scene.control.Label("Название породы:"),
            breedNameField
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        speciesComboBox.valueProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal == null || breedNameField.getText().trim().isEmpty()));
        breedNameField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal.trim().isEmpty() || speciesComboBox.getValue() == null));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Species selectedSpecies = speciesComboBox.getValue();
                String breedName = breedNameField.getText().trim();
                if (selectedSpecies != null && !breedName.isEmpty()) {
                    return new Breed(null, selectedSpecies.getId(), breedName, selectedSpecies.getSpeciesName());
                }
            }
            return null;
        });
        Optional<Breed> result = dialog.showAndWait();
        result.ifPresent(newBreed -> {
            if (breedDAO.addBreed(newBreed)) {
                loadData();
                showAlert("Успех", "Порода добавлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить породу", Alert.AlertType.ERROR);
            }
        });
    }
    @FXML
    private void onDeleteButtonClick() {
        Breed selectedBreed = breedTable.getSelectionModel().getSelectedItem();
        if (selectedBreed != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление породы");
            alert.setContentText("Вы уверены, что хотите удалить породу \"" + selectedBreed.getBreedName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (breedDAO.deleteBreed(selectedBreed)) {
                    breedData.remove(selectedBreed);
                    showAlert("Успех", "Порода удалена", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить породу", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите породу для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) breedTable.getScene().getWindow();
        stage.close();
    }
    private void loadData() {
        breedData.clear();
        breedData.addAll(breedDAO.getAllBreeds());
    }
    private void loadSpecies() {
        speciesData.clear();
        speciesData.addAll(speciesDAO.getAllSpecies());
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
