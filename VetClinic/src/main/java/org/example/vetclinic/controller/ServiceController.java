package org.example.vetclinic.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.vetclinic.database.ServiceCategoryDAO;
import org.example.vetclinic.database.ServiceDAO;
import org.example.vetclinic.model.Service;
import org.example.vetclinic.model.ServiceCategory;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;
public class ServiceController {
    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, String> serviceNameColumn;
    @FXML private TableColumn<Service, String> categoryNameColumn;
    @FXML private TableColumn<Service, String> priceColumn;
    @FXML private TableColumn<Service, String> descriptionColumn;
    private ObservableList<Service> serviceData = FXCollections.observableArrayList();
    private ObservableList<ServiceCategory> categoryData = FXCollections.observableArrayList();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private ServiceCategoryDAO categoryDAO = new ServiceCategoryDAO();
    @FXML
    private void initialize() {
        loadCategories();
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        serviceNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serviceNameColumn.setOnEditCommit(event -> {
            Service service = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (newName.isEmpty()) {
                showAlert("Ошибка", "Название услуги не может быть пустым", Alert.AlertType.ERROR);
                serviceTable.refresh();
                return;
            }
            service.setServiceName(newName);
            if (serviceDAO.updateService(service)) {
                showAlert("Успех", "Услуга обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить услугу", Alert.AlertType.ERROR);
                loadData();
            }
        });
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(categoryData.stream()
                .map(ServiceCategory::getCategoryName)
                .collect(Collectors.toList()))
        ));
        categoryNameColumn.setOnEditCommit(event -> {
            Service service = event.getRowValue();
            String newCategoryName = event.getNewValue();
            ServiceCategory selectedCategory = categoryData.stream()
                .filter(c -> c.getCategoryName().equals(newCategoryName))
                .findFirst()
                .orElse(null);
            if (selectedCategory == null && newCategoryName != null && !newCategoryName.isEmpty()) {
                showAlert("Ошибка", "Категория не найдена", Alert.AlertType.ERROR);
                serviceTable.refresh();
                return;
            }
            service.setServiceCategoryId(selectedCategory != null ? selectedCategory.getId() : null);
            service.setCategoryName(newCategoryName);
            if (serviceDAO.updateService(service)) {
                showAlert("Успех", "Услуга обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить услугу", Alert.AlertType.ERROR);
                loadData();
            }
        });
        priceColumn.setCellValueFactory(cellData -> {
            BigDecimal price = cellData.getValue().getPrice();
            return new SimpleStringProperty(price != null ? price.toString() : "0.00");
        });
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        priceColumn.setOnEditCommit(event -> {
            Service service = event.getRowValue();
            try {
                BigDecimal newPrice = new BigDecimal(event.getNewValue().trim());
                if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                    showAlert("Ошибка", "Цена не может быть отрицательной", Alert.AlertType.ERROR);
                    serviceTable.refresh();
                    return;
                }
                service.setPrice(newPrice);
                if (serviceDAO.updateService(service)) {
                    showAlert("Успех", "Услуга обновлена", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось обновить услугу", Alert.AlertType.ERROR);
                    loadData();
                }
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат цены", Alert.AlertType.ERROR);
                serviceTable.refresh();
            }
        });
        descriptionColumn.setCellValueFactory(cellData -> {
            String description = cellData.getValue().getDescription();
            return new SimpleStringProperty(description != null ? description : "");
        });
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            Service service = event.getRowValue();
            service.setDescription(event.getNewValue());
            if (serviceDAO.updateService(service)) {
                showAlert("Успех", "Услуга обновлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить услугу", Alert.AlertType.ERROR);
                loadData();
            }
        });
        serviceTable.setItems(serviceData);
        serviceTable.setEditable(true);
        loadData();
    }
    @FXML
    private void onAddButtonClick() {
        javafx.scene.control.Dialog<Service> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить услугу");
        dialog.setHeaderText(null);
        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Добавить", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);
        ComboBox<ServiceCategory> categoryComboBox = new ComboBox<>(categoryData);
        categoryComboBox.setPromptText("Выберите категорию (необязательно)");
        categoryComboBox.setPrefWidth(300);
        TextField serviceNameField = new TextField();
        serviceNameField.setPromptText("Название услуги");
        serviceNameField.setPrefWidth(300);
        TextField priceField = new TextField();
        priceField.setPromptText("Цена (например: 100.50)");
        priceField.setPrefWidth(300);
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Описание услуги (необязательно)");
        descriptionField.setPrefWidth(300);
        descriptionField.setPrefRowCount(3);
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Название услуги:"),
            serviceNameField,
            new javafx.scene.control.Label("Категория:"),
            categoryComboBox,
            new javafx.scene.control.Label("Цена:"),
            priceField,
            new javafx.scene.control.Label("Описание:"),
            descriptionField
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        serviceNameField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal.trim().isEmpty() || !isValidPrice(priceField.getText())));
        priceField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(serviceNameField.getText().trim().isEmpty() || !isValidPrice(newVal)));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String serviceName = serviceNameField.getText().trim();
                ServiceCategory selectedCategory = categoryComboBox.getValue();
                try {
                    BigDecimal price = new BigDecimal(priceField.getText().trim());
                    String description = descriptionField.getText().trim();
                    if (!serviceName.isEmpty() && price.compareTo(BigDecimal.ZERO) >= 0) {
                        return new Service(null, serviceName, 
                            selectedCategory != null ? selectedCategory.getId() : null, 
                            price, description.isEmpty() ? null : description);
                    }
                } catch (NumberFormatException e) {
                }
            }
            return null;
        });
        Optional<Service> result = dialog.showAndWait();
        result.ifPresent(newService -> {
            if (serviceDAO.addService(newService)) {
                loadData();
                showAlert("Успех", "Услуга добавлена", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить услугу", Alert.AlertType.ERROR);
            }
        });
    }
    @FXML
    private void onDeleteButtonClick() {
        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление услуги");
            alert.setContentText("Вы уверены, что хотите удалить услугу \"" + selectedService.getServiceName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (serviceDAO.deleteService(selectedService)) {
                    serviceData.remove(selectedService);
                    showAlert("Успех", "Услуга удалена", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить услугу", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите услугу для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) serviceTable.getScene().getWindow();
        stage.close();
    }
    private void loadData() {
        serviceData.clear();
        serviceData.addAll(serviceDAO.getAllServices());
    }
    private void loadCategories() {
        categoryData.clear();
        categoryData.addAll(categoryDAO.getAllServiceCategories());
    }
    private boolean isValidPrice(String priceStr) {
        try {
            BigDecimal price = new BigDecimal(priceStr.trim());
            return price.compareTo(BigDecimal.ZERO) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
