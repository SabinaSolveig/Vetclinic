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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.vetclinic.database.ProductCategoryDAO;
import org.example.vetclinic.database.ProductDAO;
import org.example.vetclinic.model.Product;
import org.example.vetclinic.model.ProductCategory;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;
public class ProductController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> categoryNameColumn;
    @FXML private TableColumn<Product, String> priceColumn;
    @FXML private TableColumn<Product, String> stockQuantityColumn;
    private ObservableList<Product> productData = FXCollections.observableArrayList();
    private ObservableList<ProductCategory> categoryData = FXCollections.observableArrayList();
    private ProductDAO productDAO = new ProductDAO();
    private ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
    @FXML
    private void initialize() {
        loadCategories();
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productNameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            String newName = event.getNewValue().trim();
            if (newName.isEmpty()) {
                showAlert("Ошибка", "Название товара не может быть пустым", Alert.AlertType.ERROR);
                productTable.refresh();
                return;
            }
            product.setProductName(newName);
            if (productDAO.updateProduct(product)) {
                showAlert("Успех", "Товар обновлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить товар", Alert.AlertType.ERROR);
                loadData();
            }
        });
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(categoryData.stream()
                .map(ProductCategory::getCategoryName)
                .collect(Collectors.toList()))
        ));
        categoryNameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            String newCategoryName = event.getNewValue();
            ProductCategory selectedCategory = categoryData.stream()
                .filter(c -> c.getCategoryName().equals(newCategoryName))
                .findFirst()
                .orElse(null);
            if (selectedCategory == null && newCategoryName != null && !newCategoryName.isEmpty()) {
                showAlert("Ошибка", "Категория не найдена", Alert.AlertType.ERROR);
                productTable.refresh();
                return;
            }
            product.setProductCategoryId(selectedCategory != null ? selectedCategory.getId() : null);
            product.setCategoryName(newCategoryName);
            if (productDAO.updateProduct(product)) {
                showAlert("Успех", "Товар обновлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось обновить товар", Alert.AlertType.ERROR);
                loadData();
            }
        });
        priceColumn.setCellValueFactory(cellData -> {
            BigDecimal price = cellData.getValue().getPrice();
            return new SimpleStringProperty(price != null ? price.toString() : "0.00");
        });
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        priceColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            try {
                BigDecimal newPrice = new BigDecimal(event.getNewValue().trim());
                if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                    showAlert("Ошибка", "Цена не может быть отрицательной", Alert.AlertType.ERROR);
                    productTable.refresh();
                    return;
                }
                product.setPrice(newPrice);
                if (productDAO.updateProduct(product)) {
                    showAlert("Успех", "Товар обновлен", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось обновить товар", Alert.AlertType.ERROR);
                    loadData();
                }
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат цены", Alert.AlertType.ERROR);
                productTable.refresh();
            }
        });
        stockQuantityColumn.setCellValueFactory(cellData -> {
            Integer quantity = cellData.getValue().getStockQuantity();
            return new SimpleStringProperty(quantity != null ? quantity.toString() : "0");
        });
        stockQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        stockQuantityColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            try {
                Integer newQuantity = Integer.parseInt(event.getNewValue().trim());
                if (newQuantity < 0) {
                    showAlert("Ошибка", "Количество не может быть отрицательным", Alert.AlertType.ERROR);
                    productTable.refresh();
                    return;
                }
                product.setStockQuantity(newQuantity);
                if (productDAO.updateProduct(product)) {
                    showAlert("Успех", "Товар обновлен", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось обновить товар", Alert.AlertType.ERROR);
                    loadData();
                }
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат количества", Alert.AlertType.ERROR);
                productTable.refresh();
            }
        });
        productTable.setItems(productData);
        productTable.setEditable(true);
        loadData();
    }
    @FXML
    private void onAddButtonClick() {
        javafx.scene.control.Dialog<Product> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить товар");
        dialog.setHeaderText(null);
        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Добавить", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);
        ComboBox<ProductCategory> categoryComboBox = new ComboBox<>(categoryData);
        categoryComboBox.setPromptText("Выберите категорию (необязательно)");
        categoryComboBox.setPrefWidth(300);
        TextField productNameField = new TextField();
        productNameField.setPromptText("Название товара");
        productNameField.setPrefWidth(300);
        TextField priceField = new TextField();
        priceField.setPromptText("Цена (например: 100.50)");
        priceField.setPrefWidth(300);
        TextField stockQuantityField = new TextField();
        stockQuantityField.setPromptText("Количество на складе");
        stockQuantityField.setPrefWidth(300);
        stockQuantityField.setText("0");
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Название товара:"),
            productNameField,
            new javafx.scene.control.Label("Категория:"),
            categoryComboBox,
            new javafx.scene.control.Label("Цена:"),
            priceField,
            new javafx.scene.control.Label("Количество на складе:"),
            stockQuantityField
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        productNameField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal.trim().isEmpty() || !isValidPrice(priceField.getText()) || !isValidQuantity(stockQuantityField.getText())));
        priceField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(productNameField.getText().trim().isEmpty() || !isValidPrice(newVal) || !isValidQuantity(stockQuantityField.getText())));
        stockQuantityField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(productNameField.getText().trim().isEmpty() || !isValidPrice(priceField.getText()) || !isValidQuantity(newVal)));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String productName = productNameField.getText().trim();
                ProductCategory selectedCategory = categoryComboBox.getValue();
                try {
                    BigDecimal price = new BigDecimal(priceField.getText().trim());
                    Integer stockQuantity = Integer.parseInt(stockQuantityField.getText().trim());
                    if (!productName.isEmpty() && price.compareTo(BigDecimal.ZERO) >= 0 && stockQuantity >= 0) {
                        return new Product(null, productName, 
                            selectedCategory != null ? selectedCategory.getId() : null, 
                            price, stockQuantity);
                    }
                } catch (NumberFormatException e) {
                }
            }
            return null;
        });
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(newProduct -> {
            if (productDAO.addProduct(newProduct)) {
                loadData();
                showAlert("Успех", "Товар добавлен", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Ошибка", "Не удалось добавить товар", Alert.AlertType.ERROR);
            }
        });
    }
    @FXML
    private void onDeleteButtonClick() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление товара");
            alert.setContentText("Вы уверены, что хотите удалить товар \"" + selectedProduct.getProductName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (productDAO.deleteProduct(selectedProduct)) {
                    productData.remove(selectedProduct);
                    showAlert("Успех", "Товар удален", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Ошибка", "Не удалось удалить товар", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите товар для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) productTable.getScene().getWindow();
        stage.close();
    }
    private void loadData() {
        productData.clear();
        productData.addAll(productDAO.getAllProducts());
    }
    private void loadCategories() {
        categoryData.clear();
        categoryData.addAll(categoryDAO.getAllProductCategories());
    }
    private boolean isValidPrice(String priceStr) {
        try {
            BigDecimal price = new BigDecimal(priceStr.trim());
            return price.compareTo(BigDecimal.ZERO) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean isValidQuantity(String quantityStr) {
        try {
            Integer quantity = Integer.parseInt(quantityStr.trim());
            return quantity >= 0;
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
