package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.ClientDAO;
import org.example.vetclinic.database.EmployeeDAO;
import org.example.vetclinic.database.PetDAO;
import org.example.vetclinic.database.PreliminaryServiceSetDAO;
import org.example.vetclinic.database.ServiceDAO;
import org.example.vetclinic.model.Appointment;
import org.example.vetclinic.model.Client;
import org.example.vetclinic.model.Employee;
import org.example.vetclinic.model.Pet;
import org.example.vetclinic.model.PreliminaryServiceSet;
import org.example.vetclinic.model.Service;
import org.example.vetclinic.util.DatePickerUtils;
import org.example.vetclinic.util.TimeFieldUtils;
import org.example.vetclinic.util.StatusTranslator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
public class AppointmentController {
    @FXML private Label idLabel;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<Pet> petComboBox;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ComboBox<String> appointmentTimeComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea notesField;
    @FXML private TableView<PreliminaryServiceSet> preliminaryServicesTable;
    @FXML private TableColumn<PreliminaryServiceSet, String> serviceNameColumn;
    @FXML private TableColumn<PreliminaryServiceSet, String> quantityColumn;
    @FXML private TableColumn<PreliminaryServiceSet, String> priceColumn;
    @FXML private TableColumn<PreliminaryServiceSet, String> totalPriceColumn;
    @FXML private TableColumn<PreliminaryServiceSet, String> notesColumn;
    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private ObservableList<Pet> petData = FXCollections.observableArrayList();
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();
    private ObservableList<String> statusData = FXCollections.observableArrayList(
        StatusTranslator.translateAppointmentStatus("Scheduled"),
        StatusTranslator.translateAppointmentStatus("Completed"),
        StatusTranslator.translateAppointmentStatus("Cancelled"),
        StatusTranslator.translateAppointmentStatus("NoShow")
    );
    private ObservableList<PreliminaryServiceSet> preliminaryServiceData = FXCollections.observableArrayList();
    private ObservableList<Service> serviceData = FXCollections.observableArrayList();
    private Appointment appointment;
    private boolean saveClicked = false;
    private ClientDAO clientDAO = new ClientDAO();
    private PetDAO petDAO = new PetDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private PreliminaryServiceSetDAO preliminaryServiceSetDAO = new PreliminaryServiceSetDAO();
    @FXML
    private void initialize() {
        DatePickerUtils.setupDateInputPattern(appointmentDatePicker);
        TimeFieldUtils.setupTimeComboBox(appointmentTimeComboBox);
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
        statusComboBox.setItems(statusData);
        statusComboBox.setValue(StatusTranslator.translateAppointmentStatus("Scheduled"));
        statusComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String russianStatus) {
                return russianStatus;
            }
            @Override
            public String fromString(String russianStatus) {
                return StatusTranslator.translateAppointmentStatusFromRussian(russianStatus);
            }
        });
        serviceNameColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getServiceName();
            return new javafx.beans.property.SimpleStringProperty(name != null ? name : "");
        });
        quantityColumn.setCellValueFactory(cellData -> {
            Integer qty = cellData.getValue().getQuantity();
            return new javafx.beans.property.SimpleStringProperty(qty != null ? qty.toString() : "1");
        });
        priceColumn.setCellValueFactory(cellData -> {
            java.math.BigDecimal price = cellData.getValue().getServicePrice();
            return new javafx.beans.property.SimpleStringProperty(price != null ? price.toString() : "0.00");
        });
        totalPriceColumn.setCellValueFactory(cellData -> {
            java.math.BigDecimal total = cellData.getValue().getTotalPrice();
            return new javafx.beans.property.SimpleStringProperty(total != null ? total.toString() : "0.00");
        });
        notesColumn.setCellValueFactory(cellData -> {
            String notes = cellData.getValue().getNotes();
            return new javafx.beans.property.SimpleStringProperty(notes != null ? notes : "");
        });
        preliminaryServicesTable.setItems(preliminaryServiceData);
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
    @FXML
    private void onSaveButtonClick() {
        if (isInputValid()) {
            Integer id = appointment != null ? appointment.getId() : null;
            Client selectedClient = clientComboBox.getValue();
            Pet selectedPet = petComboBox.getValue();
            Employee selectedEmployee = employeeComboBox.getValue();
            LocalDate appointmentDate = appointmentDatePicker.getValue();
            LocalTime appointmentTime = null;
            String timeText = appointmentTimeComboBox.getValue();
            if (timeText != null && !timeText.trim().isEmpty()) {
                appointmentTime = TimeFieldUtils.parseTime(timeText);
                if (appointmentTime == null) {
                    showAlert("Ошибка", "Некорректный формат времени. Используйте формат HH:mm (например, 14:30)", Alert.AlertType.ERROR);
                    return;
                }
            }
            String russianStatus = statusComboBox.getValue();
            String status;
            if (russianStatus == null || russianStatus.isEmpty()) {
                status = "Scheduled";
            } else {
                status = StatusTranslator.translateAppointmentStatusFromRussian(russianStatus);
            }
            String notes = notesField.getText().trim();
            if (notes.isEmpty()) {
                notes = null;
            }
            appointment = new Appointment(id, selectedClient.getId(), selectedPet.getId(),
                                         selectedEmployee.getId(), appointmentDate, appointmentTime, status, notes);
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
        if (appointmentDatePicker.getValue() == null) {
            errorMessage += "Необходимо указать дату приема!\n";
        }
        if (appointmentTimeComboBox.getValue() == null || appointmentTimeComboBox.getValue().trim().isEmpty()) {
            errorMessage += "Необходимо указать время приема!\n";
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
    public Appointment getAppointment() {
        return appointment;
    }
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        if (appointment != null) {
            if (appointment.getId() != null) {
                idLabel.setText(appointment.getId().toString());
            } else {
                idLabel.setText("(новый)");
            }
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
            appointmentDatePicker.setValue(appointment.getAppointmentDate());
            if (appointment.getAppointmentTime() != null) {
                String timeStr = appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                appointmentTimeComboBox.setValue(timeStr);
            } else {
                appointmentTimeComboBox.setValue(null);
            }
            if (appointment.getStatus() != null) {
                statusComboBox.setValue(StatusTranslator.translateAppointmentStatus(appointment.getStatus()));
            } else {
                statusComboBox.setValue(StatusTranslator.translateAppointmentStatus("Scheduled"));
            }
            notesField.setText(appointment.getNotes() != null ? appointment.getNotes() : "");
            loadPreliminaryServices();
        } else {
            idLabel.setText("(новый)");
            clientComboBox.setValue(null);
            petComboBox.setValue(null);
            employeeComboBox.setValue(null);
            appointmentDatePicker.setValue(null);
            appointmentTimeComboBox.setValue(null);
            statusComboBox.setValue(StatusTranslator.translateAppointmentStatus("Scheduled"));
            notesField.setText("");
            preliminaryServiceData.clear();
        }
    }
    private void loadData() {
        clientData.clear();
        clientData.addAll(clientDAO.getClients());
        clientComboBox.setItems(clientData);
        employeeData.clear();
        employeeData.addAll(employeeDAO.getAllEmployees());
        employeeComboBox.setItems(employeeData);
        serviceData.clear();
        serviceData.addAll(serviceDAO.getAllServices());
    }
    @FXML
    private void onAddPreliminaryServiceButtonClick() {
        javafx.scene.control.Dialog<PreliminaryServiceSet> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Добавить предварительную услугу");
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
        TextArea notesField = new TextArea();
        notesField.setPromptText("Заметки (необязательно)");
        notesField.setPrefWidth(300);
        notesField.setPrefRowCount(3);
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(
            new javafx.scene.control.Label("Услуга:"),
            serviceComboBox,
            new javafx.scene.control.Label("Количество:"),
            quantityField,
            new javafx.scene.control.Label("Заметки:"),
            notesField
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
                    String notes = notesField.getText().trim();
                    if (selectedService != null && quantity > 0) {
                        Integer appointmentId = appointment != null ? appointment.getId() : null;
                        PreliminaryServiceSet pss = new PreliminaryServiceSet(null, appointmentId, 
                            selectedService.getId(), quantity, notes.isEmpty() ? null : notes,
                            selectedService.getServiceName(), selectedService.getPrice());
                        return pss;
                    }
                } catch (NumberFormatException e) {
                }
            }
            return null;
        });
        Optional<PreliminaryServiceSet> result = dialog.showAndWait();
        result.ifPresent(newPss -> {
            preliminaryServiceData.add(newPss);
            showAlert("Успех", "Предварительная услуга добавлена в список", Alert.AlertType.INFORMATION);
        });
    }
    @FXML
    private void onDeletePreliminaryServiceButtonClick() {
        PreliminaryServiceSet selectedPss = preliminaryServicesTable.getSelectionModel().getSelectedItem();
        if (selectedPss != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление предварительной услуги");
            alert.setContentText("Вы уверены, что хотите удалить услугу \"" + selectedPss.getServiceName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (selectedPss.getId() != null) {
                    if (preliminaryServiceSetDAO.deletePreliminaryServiceSet(selectedPss)) {
                        preliminaryServiceData.remove(selectedPss);
                        showAlert("Успех", "Предварительная услуга удалена", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Ошибка", "Не удалось удалить предварительную услугу", Alert.AlertType.ERROR);
                    }
                } else {
                    preliminaryServiceData.remove(selectedPss);
                    showAlert("Успех", "Предварительная услуга удалена из списка", Alert.AlertType.INFORMATION);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите услугу для удаления", Alert.AlertType.WARNING);
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
    private void loadPreliminaryServices() {
        preliminaryServiceData.clear();
        if (appointment != null && appointment.getId() != null) {
            preliminaryServiceData.addAll(preliminaryServiceSetDAO.getPreliminaryServiceSetsByAppointment(appointment.getId()));
        }
    }
    public List<PreliminaryServiceSet> getPreliminaryServiceSets() {
        return new ArrayList<>(preliminaryServiceData);
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
