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
import org.example.vetclinic.database.ServiceCategoryDAO;
import org.example.vetclinic.model.ServiceCategory;
import java.util.Optional;
public class ServiceCategoryController {
    @FXML private TableView<ServiceCategory> categoryTable;
    @FXML private TableColumn<ServiceCategory, String> categoryNameColumn;
    private ObservableList<ServiceCategory> categoryData = FXCollections.observableArrayList();
    private ServiceCategoryDAO dao = new ServiceCategoryDAO();
    @FXML
    private void initialize() {
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryNameColumn.setOnEditCommit(event -> {
            ServiceCategory category = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (newName.isEmpty()) {
                showAlert("Ошибка", "Название категории не может быть пустым", Alert.AlertType.ERROR);
                categoryTable.refresh();
                return;
            }
            category.setCategoryName(newName);
            if (dao.updateServiceCategory(category)) {
                showAlert("Успех", "Категория услуг обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить категорию услуг", Alert.AlertType.ERROR);
                loadData();
            }
        });
        categoryTable.setItems(categoryData);
        categoryTable.setEditable(true);
        loadData();
    }
    @FXML
    private void onAddButtonClick() {
        String newName = showInputDialog("Добавить категорию", "Введите название категории услуг:");
        if (newName != null && !newName.trim().isEmpty()) {
            ServiceCategory newCategory = new ServiceCategory(null, newName.trim());
            if (dao.addServiceCategory(newCategory)) {
                loadData();
                showAlert("Успех", "Категория услуг добавлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить категорию услуг", Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void onDeleteButtonClick() {
        ServiceCategory selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление категории услуг");
            alert.setContentText("Вы уверены, что хотите удалить категорию \"" + selectedCategory.getCategoryName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (dao.deleteServiceCategory(selectedCategory)) {
                    categoryData.remove(selectedCategory);
                    showAlert("Успех", "Категория услуг удалена", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить категорию услуг", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите категорию для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) categoryTable.getScene().getWindow();
        stage.close();
    }
    private void loadData() {
        categoryData.clear();
        categoryData.addAll(dao.getAllServiceCategories());
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
