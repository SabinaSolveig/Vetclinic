package org.example.vetclinic.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.*;
import org.example.vetclinic.model.*;
import org.example.vetclinic.model.PreliminaryServiceSet;
import org.example.vetclinic.util.DatePickerUtils;
import org.example.vetclinic.util.TimeFieldUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
public class VisitController {
    @FXML private Label idLabel;
    @FXML private ComboBox<Appointment> appointmentComboBox;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<Pet> petComboBox;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private DatePicker visitDatePicker;
    @FXML private ComboBox<String> startTimeComboBox;
    @FXML private ComboBox<String> endTimeComboBox;
    @FXML private TextArea diagnosisField;
    @FXML private TextArea anamnesisField;
    @FXML private TextArea treatmentField;
    @FXML private TextArea recommendationsField;
    @FXML private TextField totalCostField;
    @FXML private TableView<VisitService> visitServicesTable;
    @FXML private TableColumn<VisitService, String> visitServiceNameColumn;
    @FXML private TableColumn<VisitService, String> visitServiceQuantityColumn;
    @FXML private TableColumn<VisitService, String> visitServicePriceColumn;
    @FXML private TableColumn<VisitService, String> visitServiceSumColumn;
    @FXML private TableColumn<VisitService, String> visitServiceDiscountColumn;
    @FXML private TableColumn<VisitService, String> visitServiceSumWithDiscountColumn;
    @FXML private TableView<VisitProduct> visitProductsTable;
    @FXML private TableColumn<VisitProduct, String> visitProductNameColumn;
    @FXML private TableColumn<VisitProduct, String> visitProductQuantityColumn;
    @FXML private TableColumn<VisitProduct, String> visitProductPriceColumn;
    @FXML private TableColumn<VisitProduct, String> visitProductSumColumn;
    @FXML private TableView<MaterialUsed> materialsUsedTable;
    @FXML private TableColumn<MaterialUsed, String> materialNameColumn;
    @FXML private TableColumn<MaterialUsed, String> materialQuantityColumn;
    private ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private ObservableList<Pet> petData = FXCollections.observableArrayList();
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();
    private ObservableList<Service> serviceData = FXCollections.observableArrayList();
    private ObservableList<Product> productData = FXCollections.observableArrayList();
    private ObservableList<VisitService> visitServiceData = FXCollections.observableArrayList();
    private ObservableList<VisitProduct> visitProductData = FXCollections.observableArrayList();
    private ObservableList<MaterialUsed> materialUsedData = FXCollections.observableArrayList();
    private Visit visit;
    private boolean saveClicked = false;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ClientDAO clientDAO = new ClientDAO();
    private PetDAO petDAO = new PetDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private ProductDAO productDAO = new ProductDAO();
    private VisitServiceDAO visitServiceDAO = new VisitServiceDAO();
    private VisitProductDAO visitProductDAO = new VisitProductDAO();
    private MaterialUsedDAO materialUsedDAO = new MaterialUsedDAO();
    @FXML
    private void initialize() {
        DatePickerUtils.setupDateInputPattern(visitDatePicker);
        TimeFieldUtils.setupTimeComboBox(startTimeComboBox);
        TimeFieldUtils.setupTimeComboBox(endTimeComboBox);
        appointmentComboBox.setConverter(new StringConverter<Appointment>() {
            @Override
            public String toString(Appointment appointment) {
                if (appointment == null) return "";
                return "Заявка #" + appointment.getId() + " (" + appointment.getAppointmentDate() + ")";
            }
            @Override
            public Appointment fromString(String string) {
                return null;
            }
        });
        clientComboBox.setConverter(new StringConverter<Client>() {
            @Override
            public String toString(Client client) {
                if (client == null) return "";
                return client.getLastName() + " " + client.getFirstName() + 
                       (client.getMiddleName() != null ? " " + client.getMiddleName() : "");
            }
            @Override
            public Client fromString(String string) {
                return null;
            }
        });
        petComboBox.setConverter(new StringConverter<Pet>() {
            @Override
            public String toString(Pet pet) {
                if (pet == null) return "";
                return pet.getName() + (pet.getClientName() != null ? " (" + pet.getClientName() + ")" : "");
            }
            @Override
            public Pet fromString(String string) {
                return null;
            }
        });
        employeeComboBox.setConverter(new StringConverter<Employee>() {
            @Override
            public String toString(Employee employee) {
                if (employee == null) return "";
                return employee.getLastName() + " " + employee.getFirstName() + 
                       (employee.getMiddleName() != null ? " " + employee.getMiddleName() : "");
            }
            @Override
            public Employee fromString(String string) {
                return null;
            }
        });
        clientComboBox.valueProperty().addListener((_, _, newClient) -> {
            updatePetsForClient(newClient);
        });
        appointmentComboBox.valueProperty().addListener((_, _, newAppointment) -> {
            if (newAppointment != null) {
                Client appointmentClient = clientData.stream()
                    .filter(c -> c.getId().equals(newAppointment.getClientId()))
                    .findFirst()
                    .orElse(null);
                clientComboBox.setValue(appointmentClient);
                updatePetsForClient(appointmentClient);
                Pet appointmentPet = petData.stream()
                    .filter(p -> p.getId().equals(newAppointment.getPetId()))
                    .findFirst()
                    .orElse(null);
                petComboBox.setValue(appointmentPet);
                Employee appointmentEmployee = employeeData.stream()
                    .filter(e -> e.getId().equals(newAppointment.getEmployeeId()))
                    .findFirst()
                    .orElse(null);
                employeeComboBox.setValue(appointmentEmployee);
                visitDatePicker.setValue(newAppointment.getAppointmentDate());
                if (newAppointment.getAppointmentTime() != null) {
                    String timeStr = newAppointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeComboBox.setValue(timeStr);
                }
            }
        });
        visitServicesTable.setEditable(true);
        visitServiceNameColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getServiceName();
            return new SimpleStringProperty(name != null ? name : "");
        });
        visitServiceNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(serviceData.stream()
                .map(s -> s.getServiceName() != null ? s.getServiceName() : "")
                .collect(java.util.stream.Collectors.toList()))
        ));
        visitServiceNameColumn.setOnEditCommit(event -> {
            VisitService vs = event.getRowValue();
            String newServiceName = event.getNewValue();
            Service selectedService = serviceData.stream()
                .filter(s -> s.getServiceName() != null && s.getServiceName().equals(newServiceName))
                .findFirst()
                .orElse(null);
            if (selectedService != null) {
                vs.setServiceId(selectedService.getId());
                vs.setServiceName(selectedService.getServiceName());
                vs.setPrice(selectedService.getPrice());
                updateVisitServiceCalculations(vs);
                calculateTotalCost();
            } else {
                visitServicesTable.refresh();
            }
        });
        visitServiceQuantityColumn.setCellValueFactory(cellData -> {
            Integer qty = cellData.getValue().getQuantity();
            return new SimpleStringProperty(qty != null ? qty.toString() : "1");
        });
        visitServiceQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        visitServiceQuantityColumn.setOnEditCommit(event -> {
            VisitService vs = event.getRowValue();
            try {
                Integer newQty = Integer.parseInt(event.getNewValue().trim());
                if (newQty <= 0) {
                    showAlert("Ошибка", "Количество должно быть больше 0", Alert.AlertType.ERROR);
                    visitServicesTable.refresh();
                    return;
                }
                vs.setQuantity(newQty);
                updateVisitServiceCalculations(vs);
                calculateTotalCost();
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат количества", Alert.AlertType.ERROR);
                visitServicesTable.refresh();
            }
        });
        visitServicePriceColumn.setCellValueFactory(cellData -> {
            BigDecimal price = cellData.getValue().getPrice();
            return new SimpleStringProperty(price != null ? price.toString() : "0.00");
        });
        visitServicePriceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        visitServicePriceColumn.setOnEditCommit(event -> {
            VisitService vs = event.getRowValue();
            try {
                BigDecimal newPrice = new BigDecimal(event.getNewValue().trim());
                if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                    showAlert("Ошибка", "Цена не может быть отрицательной", Alert.AlertType.ERROR);
                    visitServicesTable.refresh();
                    return;
                }
                vs.setPrice(newPrice);
                updateVisitServiceCalculations(vs);
                calculateTotalCost();
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат цены", Alert.AlertType.ERROR);
                visitServicesTable.refresh();
            }
        });
        visitServiceDiscountColumn.setCellValueFactory(cellData -> {
            BigDecimal discount = cellData.getValue().getDiscountSum();
            return new SimpleStringProperty(discount != null ? discount.toString() : "0.00");
        });
        visitServiceDiscountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        visitServiceDiscountColumn.setOnEditCommit(event -> {
            VisitService vs = event.getRowValue();
            try {
                BigDecimal newDiscount = new BigDecimal(event.getNewValue().trim());
                if (newDiscount.compareTo(BigDecimal.ZERO) < 0) {
                    showAlert("Ошибка", "Скидка не может быть отрицательной", Alert.AlertType.ERROR);
                    visitServicesTable.refresh();
                    return;
                }
                vs.setDiscountSum(newDiscount);
                updateVisitServiceCalculations(vs);
                calculateTotalCost();
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат скидки", Alert.AlertType.ERROR);
                visitServicesTable.refresh();
            }
        });
        visitServiceSumColumn.setCellValueFactory(cellData -> {
            BigDecimal sum = cellData.getValue().getSum();
            return new SimpleStringProperty(sum != null ? sum.toString() : "0.00");
        });
        visitServiceSumWithDiscountColumn.setCellValueFactory(cellData -> {
            BigDecimal sum = cellData.getValue().getSumWithDiscount();
            return new SimpleStringProperty(sum != null ? sum.toString() : "0.00");
        });
        visitServicesTable.setItems(visitServiceData);
        visitProductsTable.setEditable(true);
        visitProductNameColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getProductName();
            return new SimpleStringProperty(name != null ? name : "");
        });
        visitProductNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(productData.stream()
                .map(p -> p.getProductName() != null ? p.getProductName() : "")
                .collect(java.util.stream.Collectors.toList()))
        ));
        visitProductNameColumn.setOnEditCommit(event -> {
            VisitProduct vp = event.getRowValue();
            String newProductName = event.getNewValue();
            Product selectedProduct = productData.stream()
                .filter(p -> p.getProductName() != null && p.getProductName().equals(newProductName))
                .findFirst()
                .orElse(null);
            if (selectedProduct != null) {
                vp.setProductId(selectedProduct.getId());
                vp.setProductName(selectedProduct.getProductName());
                vp.setPrice(selectedProduct.getPrice());
                updateVisitProductCalculations(vp);
                calculateTotalCost();
            } else {
                visitProductsTable.refresh();
            }
        });
        visitProductQuantityColumn.setCellValueFactory(cellData -> {
            Integer qty = cellData.getValue().getQuantity();
            return new SimpleStringProperty(qty != null ? qty.toString() : "1");
        });
        visitProductQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        visitProductQuantityColumn.setOnEditCommit(event -> {
            VisitProduct vp = event.getRowValue();
            try {
                Integer newQty = Integer.parseInt(event.getNewValue().trim());
                if (newQty <= 0) {
                    showAlert("Ошибка", "Количество должно быть больше 0", Alert.AlertType.ERROR);
                    visitProductsTable.refresh();
                    return;
                }
                vp.setQuantity(newQty);
                updateVisitProductCalculations(vp);
                calculateTotalCost();
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат количества", Alert.AlertType.ERROR);
                visitProductsTable.refresh();
            }
        });
        visitProductPriceColumn.setCellValueFactory(cellData -> {
            BigDecimal price = cellData.getValue().getPrice();
            return new SimpleStringProperty(price != null ? price.toString() : "0.00");
        });
        visitProductPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        visitProductPriceColumn.setOnEditCommit(event -> {
            VisitProduct vp = event.getRowValue();
            try {
                BigDecimal newPrice = new BigDecimal(event.getNewValue().trim());
                if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                    showAlert("Ошибка", "Цена не может быть отрицательной", Alert.AlertType.ERROR);
                    visitProductsTable.refresh();
                    return;
                }
                vp.setPrice(newPrice);
                updateVisitProductCalculations(vp);
                calculateTotalCost();
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат цены", Alert.AlertType.ERROR);
                visitProductsTable.refresh();
            }
        });
        visitProductSumColumn.setCellValueFactory(cellData -> {
            BigDecimal sum = cellData.getValue().getSum();
            return new SimpleStringProperty(sum != null ? sum.toString() : "0.00");
        });
        visitProductsTable.setItems(visitProductData);
        materialsUsedTable.setEditable(true);
        materialNameColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getProductName();
            return new SimpleStringProperty(name != null ? name : "");
        });
        materialNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(productData.stream()
                .map(p -> p.getProductName() != null ? p.getProductName() : "")
                .collect(java.util.stream.Collectors.toList()))
        ));
        materialNameColumn.setOnEditCommit(event -> {
            MaterialUsed mu = event.getRowValue();
            String newProductName = event.getNewValue();
            Product selectedProduct = productData.stream()
                .filter(p -> p.getProductName() != null && p.getProductName().equals(newProductName))
                .findFirst()
                .orElse(null);
            if (selectedProduct != null) {
                mu.setProductId(selectedProduct.getId());
                mu.setProductName(selectedProduct.getProductName());
            } else {
                materialsUsedTable.refresh();
            }
        });
        materialQuantityColumn.setCellValueFactory(cellData -> {
            BigDecimal qty = cellData.getValue().getQuantity();
            return new SimpleStringProperty(qty != null ? qty.toString() : "0");
        });
        materialQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        materialQuantityColumn.setOnEditCommit(event -> {
            MaterialUsed mu = event.getRowValue();
            try {
                BigDecimal newQty = new BigDecimal(event.getNewValue().trim());
                if (newQty.compareTo(BigDecimal.ZERO) <= 0) {
                    showAlert("Ошибка", "Количество должно быть больше 0", Alert.AlertType.ERROR);
                    materialsUsedTable.refresh();
                    return;
                }
                mu.setQuantity(newQty);
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат количества", Alert.AlertType.ERROR);
                materialsUsedTable.refresh();
            }
        });
        materialsUsedTable.setItems(materialUsedData);
        visitServiceData.addListener((javafx.collections.ListChangeListener.Change<? extends VisitService> c) -> {
            calculateTotalCost();
        });
        visitProductData.addListener((javafx.collections.ListChangeListener.Change<? extends VisitProduct> c) -> {
            calculateTotalCost();
        });
        loadData();
    }
    private void updatePetsForClient(Client client) {
        petComboBox.getItems().clear();
        if (client != null && client.getId() != null) {
            petData.clear();
            petData.addAll(petDAO.getAllPets().stream()
                .filter(pet -> pet.getClientId() != null && pet.getClientId().equals(client.getId()))
                .collect(Collectors.toList()));
            petComboBox.setItems(petData);
        } else {
            petComboBox.setItems(FXCollections.observableArrayList());
        }
    }
    private void updateVisitServiceCalculations(VisitService vs) {
        if (vs.getQuantity() != null && vs.getPrice() != null) {
            BigDecimal sum = vs.getPrice().multiply(new BigDecimal(vs.getQuantity()));
            vs.setSum(sum);
        } else {
            vs.setSum(BigDecimal.ZERO);
        }
        BigDecimal discount = vs.getDiscountSum() != null ? vs.getDiscountSum() : BigDecimal.ZERO;
        BigDecimal sumWithDiscount = vs.getSum().subtract(discount);
        if (sumWithDiscount.compareTo(BigDecimal.ZERO) < 0) {
            sumWithDiscount = BigDecimal.ZERO;
        }
        vs.setSumWithDiscount(sumWithDiscount);
        visitServicesTable.refresh();
    }
    private void updateVisitProductCalculations(VisitProduct vp) {
        if (vp.getQuantity() != null && vp.getPrice() != null) {
            BigDecimal sum = vp.getPrice().multiply(new BigDecimal(vp.getQuantity()));
            vp.setSum(sum);
        } else {
            vp.setSum(BigDecimal.ZERO);
        }
        visitProductsTable.refresh();
    }
    private void calculateTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (VisitService vs : visitServiceData) {
            if (vs.getSumWithDiscount() != null) {
                total = total.add(vs.getSumWithDiscount());
            }
        }
        for (VisitProduct vp : visitProductData) {
            if (vp.getSum() != null) {
                total = total.add(vp.getSum());
            }
        }
        totalCostField.setText(total.toString());
    }
    @FXML
    private void onSaveButtonClick() {
        if (isInputValid()) {
            Integer id = visit != null ? visit.getId() : null;
            Appointment selectedAppointment = appointmentComboBox.getValue();
            Client selectedClient = clientComboBox.getValue();
            Pet selectedPet = petComboBox.getValue();
            Employee selectedEmployee = employeeComboBox.getValue();
            LocalDate visitDate = visitDatePicker.getValue();
            LocalTime startTime = null;
            String startTimeText = startTimeComboBox.getValue();
            if (startTimeText != null && !startTimeText.trim().isEmpty()) {
                startTime = TimeFieldUtils.parseTime(startTimeText);
                if (startTime == null) {
                    showAlert("Ошибка", "Некорректный формат времени начала. Используйте формат HH:mm", Alert.AlertType.ERROR);
                    return;
                }
            }
            LocalTime endTime = null;
            String endTimeText = endTimeComboBox.getValue();
            if (endTimeText != null && !endTimeText.trim().isEmpty()) {
                endTime = TimeFieldUtils.parseTime(endTimeText);
                if (endTime == null) {
                    showAlert("Ошибка", "Некорректный формат времени окончания. Используйте формат HH:mm", Alert.AlertType.ERROR);
                    return;
                }
            }
            String diagnosis = diagnosisField.getText().trim();
            if (diagnosis.isEmpty()) {
                diagnosis = null;
            }
            String anamnesis = anamnesisField.getText().trim();
            if (anamnesis.isEmpty()) {
                anamnesis = null;
            }
            String treatment = treatmentField.getText().trim();
            if (treatment.isEmpty()) {
                treatment = null;
            }
            String recommendations = recommendationsField.getText().trim();
            if (recommendations.isEmpty()) {
                recommendations = null;
            }
            BigDecimal totalCost = BigDecimal.ZERO;
            try {
                totalCost = new BigDecimal(totalCostField.getText().trim());
            } catch (Exception e) {
                calculateTotalCost();
                try {
                    totalCost = new BigDecimal(totalCostField.getText().trim());
                } catch (Exception ex) {
                    totalCost = BigDecimal.ZERO;
                }
            }
            visit = new Visit(id, 
                selectedAppointment != null ? selectedAppointment.getId() : null,
                selectedClient.getId(), selectedPet.getId(), selectedEmployee.getId(),
                visitDate, startTime, endTime, diagnosis, anamnesis, treatment, recommendations, totalCost);
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
        Stage stage = (Stage) clientComboBox.getScene().getWindow();
        stage.close();
    }
    private boolean isInputValid() {
        String errorMessage = "";
        if (clientComboBox.getValue() == null) {
            errorMessage += "Необходимо выбрать клиента!\n";
        }
        if (petComboBox.getValue() == null) {
            errorMessage += "Необходимо выбрать животное!\n";
        }
        if (employeeComboBox.getValue() == null) {
            errorMessage += "Необходимо выбрать сотрудника!\n";
        }
        if (visitDatePicker.getValue() == null) {
            errorMessage += "Необходимо указать дату приема!\n";
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
    public Visit getVisit() {
        return visit;
    }
    public List<VisitService> getVisitServices() {
        return new ArrayList<>(visitServiceData);
    }
    public List<VisitProduct> getVisitProducts() {
        return new ArrayList<>(visitProductData);
    }
    public List<MaterialUsed> getMaterialsUsed() {
        return new ArrayList<>(materialUsedData);
    }
    public void setVisitFromAppointment(Appointment appointment, List<PreliminaryServiceSet> preliminaryServices) {
        if (appointment == null) {
            return;
        }
        this.visit = null;
        idLabel.setText("(новый)");
        visitServiceData.clear();
        visitProductData.clear();
        materialUsedData.clear();
        appointmentComboBox.setValue(appointment);
        Client appointmentClient = clientData.stream()
            .filter(c -> c.getId().equals(appointment.getClientId()))
            .findFirst()
            .orElse(null);
        clientComboBox.setValue(appointmentClient);
        updatePetsForClient(appointmentClient);
        if (appointment.getPetId() != null) {
            Pet appointmentPet = petData.stream()
                .filter(p -> p.getId().equals(appointment.getPetId()))
                .findFirst()
                .orElse(null);
            petComboBox.setValue(appointmentPet);
        }
        if (appointment.getEmployeeId() != null) {
            Employee appointmentEmployee = employeeData.stream()
                .filter(e -> e.getId().equals(appointment.getEmployeeId()))
                .findFirst()
                .orElse(null);
            employeeComboBox.setValue(appointmentEmployee);
        }
        visitDatePicker.setValue(appointment.getAppointmentDate());
        if (appointment.getAppointmentTime() != null) {
            String timeStr = appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            startTimeComboBox.setValue(timeStr);
        }
        endTimeComboBox.setValue(null);
        diagnosisField.setText("");
        anamnesisField.setText("");
        treatmentField.setText("");
        recommendationsField.setText("");
        totalCostField.setText("0.00");
        if (preliminaryServices != null && !preliminaryServices.isEmpty()) {
            for (PreliminaryServiceSet pss : preliminaryServices) {
                if (pss.getServiceId() != null && pss.getServicePrice() != null) {
                    BigDecimal price = pss.getServicePrice();
                    BigDecimal quantity = new BigDecimal(pss.getQuantity() != null ? pss.getQuantity() : 1);
                    BigDecimal sum = price.multiply(quantity);
                    BigDecimal discountSum = BigDecimal.ZERO;
                    BigDecimal sumWithDiscount = sum.subtract(discountSum);
                    VisitService visitService = new VisitService(
                        null,
                        pss.getServiceId(),
                        pss.getQuantity(),
                        price,
                        sum,
                        discountSum,
                        sumWithDiscount,
                        pss.getServiceName()
                    );
                    visitServiceData.add(visitService);
                }
            }
            calculateTotalCost();
        }
    }
    public void setVisit(Visit visit) {
        this.visit = visit;
        if (visit != null) {
            if (visit.getId() != null) {
                idLabel.setText(visit.getId().toString());
            } else {
                idLabel.setText("(новый)");
            }
            if (visit.getAppointmentId() != null) {
                Appointment visitAppointment = appointmentData.stream()
                    .filter(a -> a.getId().equals(visit.getAppointmentId()))
                    .findFirst()
                    .orElse(null);
                appointmentComboBox.setValue(visitAppointment);
            }
            Client visitClient = clientData.stream()
                .filter(c -> c.getId().equals(visit.getClientId()))
                .findFirst()
                .orElse(null);
            clientComboBox.setValue(visitClient);
            updatePetsForClient(visitClient);
            if (visit.getPetId() != null) {
                Pet visitPet = petData.stream()
                    .filter(p -> p.getId().equals(visit.getPetId()))
                    .findFirst()
                    .orElse(null);
                petComboBox.setValue(visitPet);
            }
            if (visit.getEmployeeId() != null) {
                Employee visitEmployee = employeeData.stream()
                    .filter(e -> e.getId().equals(visit.getEmployeeId()))
                    .findFirst()
                    .orElse(null);
                employeeComboBox.setValue(visitEmployee);
            }
            visitDatePicker.setValue(visit.getVisitDate());
            if (visit.getStartTime() != null) {
                String timeStr = visit.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                startTimeComboBox.setValue(timeStr);
            } else {
                startTimeComboBox.setValue(null);
            }
            if (visit.getEndTime() != null) {
                String timeStr = visit.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                endTimeComboBox.setValue(timeStr);
            } else {
                endTimeComboBox.setValue(null);
            }
            diagnosisField.setText(visit.getDiagnosis() != null ? visit.getDiagnosis() : "");
            anamnesisField.setText(visit.getAnamnesis() != null ? visit.getAnamnesis() : "");
            treatmentField.setText(visit.getTreatment() != null ? visit.getTreatment() : "");
            recommendationsField.setText(visit.getRecommendations() != null ? visit.getRecommendations() : "");
            totalCostField.setText(visit.getTotalCost() != null ? visit.getTotalCost().toString() : "0.00");
            loadVisitServices();
            loadVisitProducts();
            loadMaterialsUsed();
        } else {
            idLabel.setText("(новый)");
            appointmentComboBox.setValue(null);
            clientComboBox.setValue(null);
            petComboBox.setValue(null);
            employeeComboBox.setValue(null);
            visitDatePicker.setValue(null);
            startTimeComboBox.setValue(null);
            endTimeComboBox.setValue(null);
            diagnosisField.setText("");
            anamnesisField.setText("");
            treatmentField.setText("");
            recommendationsField.setText("");
            totalCostField.setText("0.00");
            visitServiceData.clear();
            visitProductData.clear();
            materialUsedData.clear();
        }
    }
    private void loadData() {
        appointmentData.clear();
        appointmentData.addAll(appointmentDAO.getAllAppointments());
        appointmentComboBox.setItems(appointmentData);
        clientData.clear();
        clientData.addAll(clientDAO.getClients());
        clientComboBox.setItems(clientData);
        employeeData.clear();
        employeeData.addAll(employeeDAO.getAllEmployees());
        employeeComboBox.setItems(employeeData);
        serviceData.clear();
        serviceData.addAll(serviceDAO.getAllServices());
        productData.clear();
        productData.addAll(productDAO.getAllProducts());
    }
    private void loadVisitServices() {
        visitServiceData.clear();
        if (visit != null && visit.getId() != null) {
            visitServiceData.addAll(visitServiceDAO.getVisitServicesByVisit(visit.getId()));
        }
        calculateTotalCost();
    }
    private void loadVisitProducts() {
        visitProductData.clear();
        if (visit != null && visit.getId() != null) {
            visitProductData.addAll(visitProductDAO.getVisitProductsByVisit(visit.getId()));
        }
        calculateTotalCost();
    }
    private void loadMaterialsUsed() {
        materialUsedData.clear();
        if (visit != null && visit.getId() != null) {
            materialUsedData.addAll(materialUsedDAO.getMaterialsUsedByVisit(visit.getId()));
        }
    }
    @FXML
    private void onAddVisitServiceButtonClick() {
        javafx.scene.control.Dialog<VisitService> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить услугу");
        dialog.setHeaderText(null);
        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Добавить", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);
        ComboBox<Service> serviceComboBox = new ComboBox<>(serviceData);
        serviceComboBox.setPromptText("Выберите услугу");
        serviceComboBox.setPrefWidth(300);
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                if (service == null) return "";
                return service.getServiceName() + (service.getPrice() != null ? " (" + service.getPrice() + " руб.)" : "");
            }
            @Override
            public Service fromString(String string) {
                return null;
            }
        });
        TextField quantityField = new TextField();
        quantityField.setPromptText("Количество");
        quantityField.setText("1");
        quantityField.setPrefWidth(300);
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Услуга:"),
            serviceComboBox,
            new javafx.scene.control.Label("Количество:"),
            quantityField
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        serviceComboBox.valueProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal == null || !isValidQuantity(quantityField.getText())));
        quantityField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(serviceComboBox.getValue() == null || !isValidQuantity(newVal)));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Service selectedService = serviceComboBox.getValue();
                try {
                    Integer quantity = Integer.parseInt(quantityField.getText().trim());
                    BigDecimal price = selectedService.getPrice();
                    BigDecimal sum = price.multiply(new BigDecimal(quantity));
                    Client selectedClient = clientComboBox.getValue();
                    Integer discountPercent = selectedClient != null ? selectedClient.getDiscountPercent() : null;
                    if (discountPercent == null) {
                        discountPercent = 0;
                    }
                    BigDecimal discount = BigDecimal.ZERO;
                    if (discountPercent > 0) {
                        discount = sum.multiply(new BigDecimal(discountPercent)).divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                    }
                    BigDecimal sumWithDiscount = sum.subtract(discount).max(BigDecimal.ZERO);
                    if (selectedService != null && quantity > 0) {
                        Integer visitId = visit != null ? visit.getId() : null;
                        return new VisitService(visitId, selectedService.getId(), quantity, price, sum, discount, sumWithDiscount, selectedService.getServiceName());
                    }
                } catch (NumberFormatException e) {
                }
            }
            return null;
        });
        Optional<VisitService> result = dialog.showAndWait();
        result.ifPresent(newVs -> {
            VisitService existingVs = visitServiceData.stream()
                .filter(vs -> vs.getServiceId() != null && vs.getServiceId().equals(newVs.getServiceId()))
                .findFirst()
                .orElse(null);
            if (existingVs != null) {
                Integer newQuantity = existingVs.getQuantity() + newVs.getQuantity();
                existingVs.setQuantity(newQuantity);
                updateVisitServiceCalculations(existingVs);
                calculateTotalCost();
                showAlert("Успех", "Количество услуги увеличено", Alert.AlertType.INFORMATION);
            } else {
                visitServiceData.add(newVs);
                calculateTotalCost();
                showAlert("Успех", "Услуга добавлена в список", Alert.AlertType.INFORMATION);
            }
        });
    }
    @FXML
    private void onDeleteVisitServiceButtonClick() {
        VisitService selectedVs = visitServicesTable.getSelectionModel().getSelectedItem();
        if (selectedVs != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление услуги");
            alert.setContentText("Вы уверены, что хотите удалить услугу \"" + selectedVs.getServiceName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (selectedVs.getVisitId() != null && selectedVs.getServiceId() != null) {
                    if (visitServiceDAO.deleteVisitService(selectedVs.getVisitId(), selectedVs.getServiceId())) {
                        visitServiceData.remove(selectedVs);
                        calculateTotalCost();
                        showAlert("Успех", "Услуга удалена", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Ошибка", "Не удалось удалить услугу", Alert.AlertType.ERROR);
                    }
                } else {
                    visitServiceData.remove(selectedVs);
                    calculateTotalCost();
                    showAlert("Успех", "Услуга удалена из списка", Alert.AlertType.INFORMATION);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите услугу для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onAddVisitProductButtonClick() {
        javafx.scene.control.Dialog<VisitProduct> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить товар");
        dialog.setHeaderText(null);
        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Добавить", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);
        ComboBox<Product> productComboBox = new ComboBox<>(productData);
        productComboBox.setPromptText("Выберите товар");
        productComboBox.setPrefWidth(300);
        productComboBox.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                if (product == null) return "";
                return product.getProductName() + (product.getPrice() != null ? " (" + product.getPrice() + " руб.)" : "");
            }
            @Override
            public Product fromString(String string) {
                return null;
            }
        });
        TextField quantityField = new TextField();
        quantityField.setPromptText("Количество");
        quantityField.setText("1");
        quantityField.setPrefWidth(300);
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Товар:"),
            productComboBox,
            new javafx.scene.control.Label("Количество:"),
            quantityField
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        productComboBox.valueProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal == null || !isValidQuantity(quantityField.getText())));
        quantityField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(productComboBox.getValue() == null || !isValidQuantity(newVal)));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Product selectedProduct = productComboBox.getValue();
                try {
                    Integer quantity = Integer.parseInt(quantityField.getText().trim());
                    BigDecimal price = selectedProduct.getPrice();
                    BigDecimal sum = price.multiply(new BigDecimal(quantity));
                    if (selectedProduct != null && quantity > 0) {
                        Integer visitId = visit != null ? visit.getId() : null;
                        return new VisitProduct(visitId, selectedProduct.getId(), quantity, price, sum, selectedProduct.getProductName());
                    }
                } catch (NumberFormatException e) {
                }
            }
            return null;
        });
        Optional<VisitProduct> result = dialog.showAndWait();
        result.ifPresent(newVp -> {
            VisitProduct existingVp = visitProductData.stream()
                .filter(vp -> vp.getProductId() != null && vp.getProductId().equals(newVp.getProductId()))
                .findFirst()
                .orElse(null);
            if (existingVp != null) {
                Integer newQuantity = existingVp.getQuantity() + newVp.getQuantity();
                existingVp.setQuantity(newQuantity);
                updateVisitProductCalculations(existingVp);
                calculateTotalCost();
                showAlert("Успех", "Количество товара увеличено", Alert.AlertType.INFORMATION);
            } else {
                visitProductData.add(newVp);
                calculateTotalCost();
                showAlert("Успех", "Товар добавлен в список", Alert.AlertType.INFORMATION);
            }
        });
    }
    @FXML
    private void onDeleteVisitProductButtonClick() {
        VisitProduct selectedVp = visitProductsTable.getSelectionModel().getSelectedItem();
        if (selectedVp != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление товара");
            alert.setContentText("Вы уверены, что хотите удалить товар \"" + selectedVp.getProductName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (selectedVp.getVisitId() != null && selectedVp.getProductId() != null) {
                    if (visitProductDAO.deleteVisitProduct(selectedVp.getVisitId(), selectedVp.getProductId())) {
                        visitProductData.remove(selectedVp);
                        calculateTotalCost();
                        showAlert("Успех", "Товар удален", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Ошибка", "Не удалось удалить товар", Alert.AlertType.ERROR);
                    }
                } else {
                    visitProductData.remove(selectedVp);
                    calculateTotalCost();
                    showAlert("Успех", "Товар удален из списка", Alert.AlertType.INFORMATION);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите товар для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onAddMaterialButtonClick() {
        javafx.scene.control.Dialog<MaterialUsed> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить материал");
        dialog.setHeaderText(null);
        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Добавить", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);
        ComboBox<Product> productComboBox = new ComboBox<>(productData);
        productComboBox.setPromptText("Выберите товар");
        productComboBox.setPrefWidth(300);
        productComboBox.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                if (product == null) return "";
                return product.getProductName() + (product.getPrice() != null ? " (" + product.getPrice() + " руб.)" : "");
            }
            @Override
            public Product fromString(String string) {
                return null;
            }
        });
        TextField quantityField = new TextField();
        quantityField.setPromptText("Количество");
        quantityField.setText("1");
        quantityField.setPrefWidth(300);
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Товар:"),
            productComboBox,
            new javafx.scene.control.Label("Количество:"),
            quantityField
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        productComboBox.valueProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(newVal == null || !isValidDecimal(quantityField.getText())));
        quantityField.textProperty().addListener((_, _, newVal) -> 
            addButton.setDisable(productComboBox.getValue() == null || !isValidDecimal(newVal)));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Product selectedProduct = productComboBox.getValue();
                try {
                    BigDecimal quantity = new BigDecimal(quantityField.getText().trim());
                    if (selectedProduct != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
                        Integer visitId = visit != null ? visit.getId() : null;
                        MaterialUsed newMu = new MaterialUsed(visitId, selectedProduct.getId(), quantity, selectedProduct.getProductName());
                        return newMu;
                    }
                } catch (NumberFormatException e) {
                }
            }
            return null;
        });
        Optional<MaterialUsed> result = dialog.showAndWait();
        result.ifPresent(newMu -> {
            MaterialUsed existingMu = materialUsedData.stream()
                .filter(mu -> mu.getProductId() != null && mu.getProductId().equals(newMu.getProductId()))
                .findFirst()
                .orElse(null);
            if (existingMu != null) {
                BigDecimal newQuantity = existingMu.getQuantity().add(newMu.getQuantity());
                existingMu.setQuantity(newQuantity);
                materialsUsedTable.refresh();
                showAlert("Успех", "Количество материала увеличено", Alert.AlertType.INFORMATION);
            } else {
                materialUsedData.add(newMu);
                showAlert("Успех", "Материал добавлен в список", Alert.AlertType.INFORMATION);
            }
        });
    }
    @FXML
    private void onDeleteMaterialButtonClick() {
        MaterialUsed selectedMu = materialsUsedTable.getSelectionModel().getSelectedItem();
        if (selectedMu != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление материала");
            alert.setContentText("Вы уверены, что хотите удалить материал \"" + (selectedMu.getProductName() != null ? selectedMu.getProductName() : "товар #" + selectedMu.getProductId()) + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (selectedMu.getVisitId() != null && selectedMu.getProductId() != null) {
                    if (materialUsedDAO.deleteMaterialUsed(selectedMu.getVisitId(), selectedMu.getProductId())) {
                        materialUsedData.remove(selectedMu);
                        showAlert("Успех", "Материал удален", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Ошибка", "Не удалось удалить материал", Alert.AlertType.ERROR);
                    }
                } else {
                    materialUsedData.remove(selectedMu);
                    showAlert("Успех", "Материал удален из списка", Alert.AlertType.INFORMATION);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите материал для удаления", Alert.AlertType.WARNING);
        }
    }
    private boolean isValidQuantity(String quantityStr) {
        try {
            Integer quantity = Integer.parseInt(quantityStr.trim());
            return quantity > 0;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean isValidDecimal(String decimalStr) {
        try {
            BigDecimal value = new BigDecimal(decimalStr.trim());
            return value.compareTo(BigDecimal.ZERO) >= 0;
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
