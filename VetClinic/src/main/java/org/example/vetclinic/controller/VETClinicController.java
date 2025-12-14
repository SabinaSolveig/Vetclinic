package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.vetclinic.VETClinicApplication;
import org.example.vetclinic.database.AppointmentDAO;
import org.example.vetclinic.database.ClientDAO;
import org.example.vetclinic.database.EmployeeDAO;
import org.example.vetclinic.database.PetDAO;
import org.example.vetclinic.database.PreliminaryServiceSetDAO;
import org.example.vetclinic.database.VisitDAO;
import org.example.vetclinic.database.VisitServiceDAO;
import org.example.vetclinic.database.VisitProductDAO;
import org.example.vetclinic.database.MaterialUsedDAO;
import org.example.vetclinic.database.PaymentDAO;
import org.example.vetclinic.model.Appointment;
import org.example.vetclinic.model.Client;
import org.example.vetclinic.model.Employee;
import org.example.vetclinic.model.Pet;
import org.example.vetclinic.model.Visit;
import org.example.vetclinic.model.VisitService;
import org.example.vetclinic.model.VisitProduct;
import org.example.vetclinic.model.MaterialUsed;
import org.example.vetclinic.model.Payment;
import org.example.vetclinic.model.PreliminaryServiceSet;
import org.example.vetclinic.util.StatusTranslator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
public class VETClinicController {
    private final ClientDAO dao = new ClientDAO();
    private final PetDAO petDAO = new PetDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PreliminaryServiceSetDAO preliminaryServiceSetDAO = new PreliminaryServiceSetDAO();
    private final VisitDAO visitDAO = new VisitDAO();
    private final VisitServiceDAO visitServiceDAO = new VisitServiceDAO();
    private final VisitProductDAO visitProductDAO = new VisitProductDAO();
    private final MaterialUsedDAO materialUsedDAO = new MaterialUsedDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    @FXML private TableView<Client> clientTable;
    @FXML private TableView<Pet> animalTable;
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableView<Visit> visitTable;
    @FXML private TableView<Payment> paymentTable;
    @FXML private javafx.scene.control.TextField clientSearchField;
    @FXML private ComboBox<Client> appointmentClientFilter;
    @FXML private ComboBox<Employee> appointmentEmployeeFilter;
    @FXML private ComboBox<String> appointmentStatusFilter;
    @FXML private ComboBox<String> appointmentSortField;
    @FXML private ComboBox<String> appointmentSortDirection;
    @FXML private ComboBox<Employee> visitEmployeeFilter;
    @FXML private ComboBox<Client> visitClientFilter;
    @FXML private ComboBox<Pet> visitPetFilter;
    @FXML private ComboBox<String> visitSortField;
    @FXML private ComboBox<String> visitSortDirection;
    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private ObservableList<Pet> allPetData = FXCollections.observableArrayList();
    private ObservableList<Pet> filteredPetData = FXCollections.observableArrayList();
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
    private ObservableList<Visit> visitData = FXCollections.observableArrayList();
    private ObservableList<Payment> paymentData = FXCollections.observableArrayList();
    @FXML
    private void initialize() {
        setupAppointmentFilters();
        setupVisitFilters();
        loadData();
        clientTable.setItems(clientData);
        animalTable.setItems(filteredPetData);
        employeeTable.setItems(employeeData);
        appointmentTable.setItems(appointmentData);
        visitTable.setItems(visitData);
        paymentTable.setItems(paymentData);
        clientTable.getSelectionModel().selectedItemProperty().addListener((_, _, newClient) -> {
            filterPetsByClient(newClient);
            reloadClientRelatedData(newClient);
        });
    }
    private void setupAppointmentFilters() {
        appointmentClientFilter.getItems().clear();
        appointmentClientFilter.getItems().add(null);
        appointmentClientFilter.getItems().addAll(clientData);
        appointmentClientFilter.setConverter(new javafx.util.StringConverter<Client>() {
            @Override
            public String toString(Client client) {
                if (client == null) return "Все клиенты";
                return client.getLastName() + " " + client.getFirstName() + 
                       (client.getMiddleName() != null ? " " + client.getMiddleName() : "");
            }
            @Override
            public Client fromString(String string) {
                return null;
            }
        });
        appointmentEmployeeFilter.getItems().clear();
        appointmentEmployeeFilter.getItems().add(null);
        appointmentEmployeeFilter.getItems().addAll(employeeData);
        appointmentEmployeeFilter.setConverter(new javafx.util.StringConverter<Employee>() {
            @Override
            public String toString(Employee employee) {
                if (employee == null) return "Все сотрудники";
                return employee.getLastName() + " " + employee.getFirstName() + 
                       (employee.getMiddleName() != null ? " " + employee.getMiddleName() : "");
            }
            @Override
            public Employee fromString(String string) {
                return null;
            }
        });
        appointmentStatusFilter.getItems().clear();
        appointmentStatusFilter.getItems().add(null);
        appointmentStatusFilter.getItems().addAll(
            StatusTranslator.translateAppointmentStatus("Scheduled"),
            StatusTranslator.translateAppointmentStatus("Completed"),
            StatusTranslator.translateAppointmentStatus("Cancelled"),
            StatusTranslator.translateAppointmentStatus("NoShow")
        );
        appointmentStatusFilter.setConverter(new javafx.util.StringConverter<String>() {
            @Override
            public String toString(String russianStatus) {
                if (russianStatus == null) return "Все статусы";
                return russianStatus;
            }
            @Override
            public String fromString(String russianStatus) {
                if (russianStatus == null || russianStatus.equals("Все статусы")) {
                    return null;
                }
                return StatusTranslator.translateAppointmentStatusFromRussian(russianStatus);
            }
        });
        appointmentSortField.getItems().clear();
        appointmentSortField.getItems().addAll(
            "Дата",
            "Время",
            "Клиент",
            "Животное",
            "Сотрудник",
            "Статус"
        );
        appointmentSortField.setValue("Дата");
        appointmentSortDirection.getItems().clear();
        appointmentSortDirection.getItems().addAll("По возрастанию", "По убыванию");
        appointmentSortDirection.setValue("По убыванию");
        appointmentClientFilter.valueProperty().addListener((_, _, _) -> filterAppointments());
        appointmentEmployeeFilter.valueProperty().addListener((_, _, _) -> filterAppointments());
        appointmentStatusFilter.valueProperty().addListener((_, _, _) -> filterAppointments());
        appointmentSortField.valueProperty().addListener((_, _, _) -> filterAppointments());
        appointmentSortDirection.valueProperty().addListener((_, _, _) -> filterAppointments());
    }
    private void filterAppointments() {
        Client selectedClient = appointmentClientFilter.getValue();
        Employee selectedEmployee = appointmentEmployeeFilter.getValue();
        String selectedRussianStatus = appointmentStatusFilter.getValue();
        String selectedStatus = null;
        if (selectedRussianStatus != null) {
            selectedStatus = StatusTranslator.translateAppointmentStatusFromRussian(selectedRussianStatus);
        }
        String sortField = appointmentSortField.getValue();
        String sortDirection = appointmentSortDirection.getValue();
        Integer clientId = selectedClient != null ? selectedClient.getId() : null;
        Integer employeeId = selectedEmployee != null ? selectedEmployee.getId() : null;
        String sqlSortField = null;
        if (sortField != null) {
            switch (sortField) {
                case "Дата":
                    sqlSortField = "a.AppointmentDate";
                    break;
                case "Время":
                    sqlSortField = "a.AppointmentTime";
                    break;
                case "Клиент":
                    sqlSortField = "ClientName";
                    break;
                case "Животное":
                    sqlSortField = "PetName";
                    break;
                case "Сотрудник":
                    sqlSortField = "EmployeeName";
                    break;
                case "Статус":
                    sqlSortField = "a.Status";
                    break;
                default:
                    sqlSortField = "a.AppointmentDate";
            }
        } else {
            sqlSortField = "a.AppointmentDate";
        }
        boolean ascending = sortDirection != null && sortDirection.equals("По возрастанию");
        List<Appointment> filteredAppointments = appointmentDAO.getAppointmentsWithFilters(
            clientId, employeeId, selectedStatus, sqlSortField, ascending);
        appointmentData.setAll(filteredAppointments);
    }
    private void setupVisitFilters() {
        if (visitEmployeeFilter != null) {
            visitEmployeeFilter.getItems().clear();
            visitEmployeeFilter.getItems().add(null);
            visitEmployeeFilter.getItems().addAll(employeeData);
            visitEmployeeFilter.setConverter(new javafx.util.StringConverter<Employee>() {
                @Override
                public String toString(Employee employee) {
                    if (employee == null) return "Все врачи";
                    return employee.getLastName() + " " + employee.getFirstName() + 
                           (employee.getMiddleName() != null ? " " + employee.getMiddleName() : "");
                }
                @Override
                public Employee fromString(String string) {
                    return null;
                }
            });
        }
        if (visitClientFilter != null) {
            visitClientFilter.getItems().clear();
            visitClientFilter.getItems().add(null);
            visitClientFilter.getItems().addAll(clientData);
            visitClientFilter.setConverter(new javafx.util.StringConverter<Client>() {
                @Override
                public String toString(Client client) {
                    if (client == null) return "Все клиенты";
                    return client.getLastName() + " " + client.getFirstName() + 
                           (client.getMiddleName() != null ? " " + client.getMiddleName() : "");
                }
                @Override
                public Client fromString(String string) {
                    return null;
                }
            });
        }
        if (visitPetFilter != null) {
            visitPetFilter.getItems().clear();
            visitPetFilter.getItems().add(null);
            visitPetFilter.getItems().addAll(allPetData);
            visitPetFilter.setConverter(new javafx.util.StringConverter<Pet>() {
                @Override
                public String toString(Pet pet) {
                    if (pet == null) return "Все питомцы";
                    return pet.getName() + " (" + pet.getClientName() + ")";
                }
                @Override
                public Pet fromString(String string) {
                    return null;
                }
            });
        }
        if (visitSortField != null) {
            visitSortField.getItems().clear();
            visitSortField.getItems().addAll(
                "Дата",
                "Время начала",
                "Клиент",
                "Питомец",
                "Врач",
                "Стоимость"
            );
            visitSortField.setValue("Дата");
        }
        if (visitSortDirection != null) {
            visitSortDirection.getItems().clear();
            visitSortDirection.getItems().addAll("По возрастанию", "По убыванию");
            visitSortDirection.setValue("По убыванию");
        }
        if (visitEmployeeFilter != null) {
            visitEmployeeFilter.valueProperty().addListener((_, _, _) -> filterVisits());
        }
        if (visitClientFilter != null) {
            visitClientFilter.valueProperty().addListener((_, _, _) -> {
                Client selectedClient = visitClientFilter.getValue();
                if (visitPetFilter != null) {
                    visitPetFilter.getItems().clear();
                    visitPetFilter.getItems().add(null);
                    if (selectedClient != null) {
                        visitPetFilter.getItems().addAll(allPetData.stream()
                            .filter(p -> p.getClientId() != null && p.getClientId().equals(selectedClient.getId()))
                            .collect(java.util.stream.Collectors.toList()));
                    } else {
                        visitPetFilter.getItems().addAll(allPetData);
                    }
                }
                filterVisits();
            });
        }
        if (visitPetFilter != null) {
            visitPetFilter.valueProperty().addListener((_, _, _) -> filterVisits());
        }
        if (visitSortField != null) {
            visitSortField.valueProperty().addListener((_, _, _) -> filterVisits());
        }
        if (visitSortDirection != null) {
            visitSortDirection.valueProperty().addListener((_, _, _) -> filterVisits());
        }
    }
    private void filterVisits() {
        Employee selectedEmployee = visitEmployeeFilter != null ? visitEmployeeFilter.getValue() : null;
        Client selectedClient = visitClientFilter != null ? visitClientFilter.getValue() : null;
        Pet selectedPet = visitPetFilter != null ? visitPetFilter.getValue() : null;
        String sortField = visitSortField != null ? visitSortField.getValue() : null;
        boolean ascending = visitSortDirection != null && "По возрастанию".equals(visitSortDirection.getValue());
        Integer employeeId = selectedEmployee != null ? selectedEmployee.getId() : null;
        Integer clientId = selectedClient != null ? selectedClient.getId() : null;
        Integer petId = selectedPet != null ? selectedPet.getId() : null;
        List<Visit> filteredVisits = visitDAO.getVisitsWithFilters(employeeId, clientId, petId, sortField, ascending);
        visitData.setAll(filteredVisits);
    }
    private void reloadClientRelatedData(Client client) {
        if (client == null || client.getId() == null) {
            return;
        }
        List<Pet> allPets = petDAO.getAllPets();
        allPetData.setAll(allPets);
        filterPetsByClient(client);
        List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
        appointmentData.setAll(allAppointments);
        filterVisits();
        List<Payment> allPayments = paymentDAO.getAllPayments();
        paymentData.setAll(allPayments);
    }
    private void filterPetsByClient(Client client) {
        filteredPetData.clear();
        if (client != null && client.getId() != null) {
            allPetData.stream()
                .filter(pet -> pet.getClientId() != null && pet.getClientId().equals(client.getId()))
                .forEach(filteredPetData::add);
        }
    }
    @FXML private void onAddClientButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Client-view.fxml"));
            AnchorPane root = loader.load();
            ClientController controller = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Данные клиента");
            dialogStage.showAndWait();
            if (controller.isSaveClicked()){
                Client client = controller.getClient();
                clientData.add(client);
                if (dao.addClient(client)){
                    loadData();
                };

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void onDeleteClientButtonClick(){
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление клиента");
            alert.setContentText("Вы уверены, что хотите удалить клиента " + selectedClient + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                dao.deleteClient(selectedClient);
                clientData.remove(selectedClient);
                filteredPetData.clear();
            }
        } else {
            showAlert("Предупреждение", "Выберите клиента для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML private void onChangeClientButtonClick(){
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            try {
                FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Client-view.fxml"));
                AnchorPane root = loader.load();
                ClientController controller = loader.getController();
                controller.setClient(selectedClient);
                Stage dialogStage = new Stage();
                Scene scene = new Scene(root);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
                dialogStage.setScene(scene);
                dialogStage.setTitle("Редактирование клиента");
                dialogStage.showAndWait();
                if (controller.isSaveClicked()){
                    Client editedClient = controller.getClient();
                    if (dao.updateClient(editedClient)) {
                        for (int i = 0; i < clientData.size(); i++) {
                            if (clientData.get(i).getId() != null && 
                                clientData.get(i).getId().equals(selectedClient.getId())) {
                                clientData.set(i, editedClient);
                                break;
                            }
                        }
                        filterPetsByClient(editedClient);
                    } else {
                        showAlert("Ошибка", "Не удалось обновить клиента в базе данных", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Произошла ошибка при редактировании клиента: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Предупреждение", "Выберите клиента для редактирования", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onAddPetButtonClick() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showAlert("Предупреждение", "Выберите владельца", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Pet-view.fxml"));
            AnchorPane root = loader.load();
            PetController controller = loader.getController();
            controller.setSelectedClient(selectedClient);
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Данные животного");
            dialogStage.showAndWait();
            if (controller.isSaveClicked()) {
                Pet pet = controller.getPet();
                if (petDAO.addPet(pet)) {
                    loadData();
                    filterPetsByClient(selectedClient);
                } else {
                    showAlert("Ошибка", "Не удалось добавить животное в базу данных", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при добавлении животного: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onEditPetButtonClick() {
        Pet selectedPet = animalTable.getSelectionModel().getSelectedItem();
        if (selectedPet != null) {
            try {
                FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Pet-view.fxml"));
                AnchorPane root = loader.load();
                PetController controller = loader.getController();
                List<Pet> allPets = petDAO.getAllPets();
                Pet fullPet = allPets.stream()
                    .filter(p -> p.getId().equals(selectedPet.getId()))
                    .findFirst()
                    .orElse(selectedPet);
                controller.setPet(fullPet);
                Stage dialogStage = new Stage();
                Scene scene = new Scene(root);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
                dialogStage.setScene(scene);
                dialogStage.setTitle("Редактирование животного");
                dialogStage.showAndWait();
                if (controller.isSaveClicked()) {
                    Pet editedPet = controller.getPet();
                    if (petDAO.updatePet(editedPet)) {
                        loadData();
                        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
                        filterPetsByClient(selectedClient);
                    } else {
                        showAlert("Ошибка", "Не удалось обновить животное в базе данных", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Произошла ошибка при редактировании животного: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Предупреждение", "Выберите животное для редактирования", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onDeletePetButtonClick() {
        Pet selectedPet = animalTable.getSelectionModel().getSelectedItem();
        if (selectedPet != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление животного");
            alert.setContentText("Вы уверены, что хотите удалить животное \"" + selectedPet.getName() + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (petDAO.deletePet(selectedPet)) {
                    loadData();
                    Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
                    filterPetsByClient(selectedClient);
                } else {
                    showAlert("Ошибка", "Не удалось удалить животное из базы данных", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите животное для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onAddEmployeeButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Employee-view.fxml"));
            AnchorPane root = loader.load();
            EmployeeController controller = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Данные сотрудника");
            dialogStage.showAndWait();
            if (controller.isSaveClicked()) {
                Employee employee = controller.getEmployee();
                if (employeeDAO.addEmployee(employee)) {
                    loadData();
                } else {
                    showAlert("Ошибка", "Не удалось добавить сотрудника в базу данных", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при добавлении сотрудника: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onEditEmployeeButtonClick() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Employee-view.fxml"));
                AnchorPane root = loader.load();
                EmployeeController controller = loader.getController();
                List<Employee> allEmployees = employeeDAO.getAllEmployees();
                Employee fullEmployee = allEmployees.stream()
                    .filter(e -> e.getId().equals(selectedEmployee.getId()))
                    .findFirst()
                    .orElse(selectedEmployee);
                controller.setEmployee(fullEmployee);
                Stage dialogStage = new Stage();
                Scene scene = new Scene(root);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
                dialogStage.setScene(scene);
                dialogStage.setTitle("Редактирование сотрудника");
                dialogStage.showAndWait();
                if (controller.isSaveClicked()) {
                    Employee editedEmployee = controller.getEmployee();
                    if (employeeDAO.updateEmployee(editedEmployee)) {
                        loadData();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить сотрудника в базе данных", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Произошла ошибка при редактировании сотрудника: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Предупреждение", "Выберите сотрудника для редактирования", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onDeleteEmployeeButtonClick() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление сотрудника");
            String employeeName = selectedEmployee.getLastName() + " " + selectedEmployee.getFirstName();
            alert.setContentText("Вы уверены, что хотите удалить сотрудника \"" + employeeName + "\"?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (employeeDAO.deleteEmployee(selectedEmployee)) {
                    loadData();
                } else {
                    showAlert("Ошибка", "Не удалось удалить сотрудника из базы данных", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите сотрудника для удаления", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onManageSpecializationsButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Specialization-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление специальностями");
            dialogStage.setMinWidth(600);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления специальностями: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onAddAppointmentButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Appointment-view.fxml"));
            AnchorPane root = loader.load();
            AppointmentController controller = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Создание заявки на прием");
            dialogStage.showAndWait();
            if (controller.isSaveClicked()) {
                Appointment appointment = controller.getAppointment();
                if (appointmentDAO.addAppointment(appointment)) {
                    List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                    Appointment savedAppointment = allAppointments.stream()
                        .filter(a -> a.getClientId().equals(appointment.getClientId()) &&
                                   a.getPetId().equals(appointment.getPetId()) &&
                                   a.getAppointmentDate().equals(appointment.getAppointmentDate()) &&
                                   a.getAppointmentTime().equals(appointment.getAppointmentTime()))
                        .max((a1, a2) -> a1.getId().compareTo(a2.getId()))
                        .orElse(null);
                    if (savedAppointment != null) {
                        List<PreliminaryServiceSet> preliminaryServices = controller.getPreliminaryServiceSets();
                        for (PreliminaryServiceSet pss : preliminaryServices) {
                            if (pss.getId() == null) {
                                pss.setAppointmentId(savedAppointment.getId());
                                preliminaryServiceSetDAO.addPreliminaryServiceSet(pss);
                            }
                        }
                        loadData();
                    } else {
                        loadData();
                    }
                } else {
                    showAlert("Ошибка", "Не удалось добавить заявку на прием в базу данных. Проверьте консоль для подробностей.", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при создании заявки на прием: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onCreateVisitFromAppointmentButtonClick() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert("Предупреждение", "Выберите заявку на прием для оформления приема врача", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Visit-view.fxml"));
            AnchorPane root = loader.load();
            VisitController controller = loader.getController();
            List<PreliminaryServiceSet> preliminaryServices = preliminaryServiceSetDAO.getPreliminaryServiceSetsByAppointment(selectedAppointment.getId());
            controller.setVisitFromAppointment(selectedAppointment, preliminaryServices);
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Создание приема врача из заявки");
            dialogStage.setMinWidth(1000);
            dialogStage.setMinHeight(800);
            dialogStage.showAndWait();
            if (controller.isSaveClicked()) {
                Visit visit = controller.getVisit();
                if (visitDAO.addVisit(visit)) {
                    List<Visit> allVisits = visitDAO.getAllVisits();
                    Visit savedVisit = allVisits.stream()
                        .filter(v -> v.getClientId().equals(visit.getClientId()) &&
                                   v.getPetId().equals(visit.getPetId()) &&
                                   v.getVisitDate().equals(visit.getVisitDate()))
                        .max((v1, v2) -> v1.getId().compareTo(v2.getId()))
                        .orElse(null);
                    if (savedVisit != null) {
                        List<VisitService> visitServices = controller.getVisitServices();
                        for (VisitService vs : visitServices) {
                            vs.setVisitId(savedVisit.getId());
                            visitServiceDAO.addVisitService(vs);
                        }
                        List<VisitProduct> visitProducts = controller.getVisitProducts();
                        for (VisitProduct vp : visitProducts) {
                            vp.setVisitId(savedVisit.getId());
                            visitProductDAO.addVisitProduct(vp);
                        }
                        List<MaterialUsed> materialsUsed = controller.getMaterialsUsed();
                        for (MaterialUsed mu : materialsUsed) {
                            mu.setVisitId(savedVisit.getId());
                            materialUsedDAO.addMaterialUsed(mu);
                        }
                        BigDecimal totalCost = BigDecimal.ZERO;
                        for (VisitService vs : visitServices) {
                            if (vs.getSumWithDiscount() != null) {
                                totalCost = totalCost.add(vs.getSumWithDiscount());
                            }
                        }
                        for (VisitProduct vp : visitProducts) {
                            if (vp.getSum() != null) {
                                totalCost = totalCost.add(vp.getSum());
                            }
                        }
                        savedVisit.setTotalCost(totalCost);
                        visitDAO.updateVisit(savedVisit);
                        if (savedVisit.getAppointmentId() != null) {
                            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                            Appointment appointment = allAppointments.stream()
                                .filter(a -> a.getId().equals(savedVisit.getAppointmentId()))
                                .findFirst()
                                .orElse(null);
                            if (appointment != null) {
                                appointment.setStatus("Completed");
                                appointmentDAO.updateAppointment(appointment);
                            }
                        }
                        loadData();
                    } else {
                        loadData();
                    }
                } else {
                    showAlert("Ошибка", "Не удалось добавить прием врача в базу данных", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при создании приема врача из заявки: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onEditAppointmentButtonClick() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            try {
                FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Appointment-view.fxml"));
                AnchorPane root = loader.load();
                AppointmentController controller = loader.getController();
                List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                Appointment fullAppointment = allAppointments.stream()
                    .filter(a -> a.getId().equals(selectedAppointment.getId()))
                    .findFirst()
                    .orElse(selectedAppointment);
                controller.setAppointment(fullAppointment);
                Stage dialogStage = new Stage();
                Scene scene = new Scene(root);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
                dialogStage.setScene(scene);
                dialogStage.setTitle("Редактирование заявки на прием");
                dialogStage.showAndWait();
                if (controller.isSaveClicked()) {
                    Appointment editedAppointment = controller.getAppointment();
                    if (appointmentDAO.updateAppointment(editedAppointment)) {
                        List<PreliminaryServiceSet> preliminaryServices = controller.getPreliminaryServiceSets();
                        List<PreliminaryServiceSet> existingServices = preliminaryServiceSetDAO.getPreliminaryServiceSetsByAppointment(editedAppointment.getId());
                        for (PreliminaryServiceSet existing : existingServices) {
                            boolean found = preliminaryServices.stream()
                                .anyMatch(pss -> pss.getId() != null && pss.getId().equals(existing.getId()));
                            if (!found) {
                                preliminaryServiceSetDAO.deletePreliminaryServiceSet(existing);
                            }
                        }
                        for (PreliminaryServiceSet pss : preliminaryServices) {
                            if (pss.getId() == null) {
                                pss.setAppointmentId(editedAppointment.getId());
                                preliminaryServiceSetDAO.addPreliminaryServiceSet(pss);
                            } else {
                                pss.setAppointmentId(editedAppointment.getId());
                                preliminaryServiceSetDAO.updatePreliminaryServiceSet(pss);
                            }
                        }
                        loadData();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить заявку на прием в базе данных", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Произошла ошибка при редактировании заявки на прием: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Предупреждение", "Выберите заявку для редактирования", Alert.AlertType.WARNING);
        }
    }
    private void loadData() {
        List<Client> loadedClients = dao.getClients();
        clientData.setAll(loadedClients);
        List<Pet> loadedPets = petDAO.getAllPets();
        allPetData.setAll(loadedPets);
        List<Employee> loadedEmployees = employeeDAO.getAllEmployees();
        employeeData.setAll(loadedEmployees);
        if (appointmentClientFilter != null) {
            Client selectedClient = appointmentClientFilter.getValue();
            appointmentClientFilter.getItems().clear();
            appointmentClientFilter.getItems().add(null);
            appointmentClientFilter.getItems().addAll(clientData);
            appointmentClientFilter.setValue(selectedClient);
        }
        if (appointmentEmployeeFilter != null) {
            Employee selectedEmployee = appointmentEmployeeFilter.getValue();
            appointmentEmployeeFilter.getItems().clear();
            appointmentEmployeeFilter.getItems().add(null);
            appointmentEmployeeFilter.getItems().addAll(employeeData);
            appointmentEmployeeFilter.setValue(selectedEmployee);
        }
        if (visitEmployeeFilter != null) {
            Employee selectedEmployee = visitEmployeeFilter.getValue();
            visitEmployeeFilter.getItems().clear();
            visitEmployeeFilter.getItems().add(null);
            visitEmployeeFilter.getItems().addAll(employeeData);
            visitEmployeeFilter.setValue(selectedEmployee);
        }
        if (visitClientFilter != null) {
            Client selectedClientFilter = visitClientFilter.getValue();
            visitClientFilter.getItems().clear();
            visitClientFilter.getItems().add(null);
            visitClientFilter.getItems().addAll(clientData);
            visitClientFilter.setValue(selectedClientFilter);
        }
        if (visitPetFilter != null) {
            Pet selectedPet = visitPetFilter.getValue();
            visitPetFilter.getItems().clear();
            visitPetFilter.getItems().add(null);
            Client filterClient = visitClientFilter != null ? visitClientFilter.getValue() : null;
            if (filterClient != null) {
                visitPetFilter.getItems().addAll(allPetData.stream()
                    .filter(p -> p.getClientId() != null && p.getClientId().equals(filterClient.getId()))
                    .collect(java.util.stream.Collectors.toList()));
            } else {
                visitPetFilter.getItems().addAll(allPetData);
            }
            visitPetFilter.setValue(selectedPet);
        }
        filterAppointments();
        filterVisits();
        List<Payment> loadedPayments = paymentDAO.getAllPayments();
        paymentData.setAll(loadedPayments);
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        filterPetsByClient(selectedClient);
    }
    @FXML
    private void onClientSearchButtonClick() {
        performClientSearch();
    }
    @FXML
    private void onClientSearchAction() {
        performClientSearch();
    }
    @FXML
    private void onClientSearchResetButtonClick() {
        clientSearchField.clear();
        loadData();
    }
    private void performClientSearch() {
        String searchText = clientSearchField.getText();
        List<Client> foundClients = dao.searchClients(searchText);
        clientData.setAll(foundClients);
        if (foundClients.size() == 1) {
            clientTable.getSelectionModel().select(foundClients.get(0));
        } else if (foundClients.isEmpty()) {
            filteredPetData.clear();
        }
    }
    @FXML
    private void onAppointmentFilterResetButtonClick() {
        appointmentClientFilter.setValue(null);
        appointmentEmployeeFilter.setValue(null);
        appointmentStatusFilter.setValue(null);
        appointmentSortField.setValue("Дата");
        appointmentSortDirection.setValue("По убыванию");
        filterAppointments();
    }
    @FXML
    private void onVisitFilterResetButtonClick() {
        if (visitEmployeeFilter != null) {
            visitEmployeeFilter.setValue(null);
        }
        if (visitClientFilter != null) {
            visitClientFilter.setValue(null);
        }
        if (visitPetFilter != null) {
            visitPetFilter.setValue(null);
        }
        if (visitSortField != null) {
            visitSortField.setValue("Дата");
        }
        if (visitSortDirection != null) {
            visitSortDirection.setValue("По убыванию");
        }
        filterVisits();
    }
    @FXML
    private void onManageSpeciesButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Species-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление видами животных");
            dialogStage.setMinWidth(500);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления видами: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onManageBreedsButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Breed-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление породами животных");
            dialogStage.setMinWidth(600);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления породами: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onManageContactTypesButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("ContactType-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление видами контактной информации");
            dialogStage.setMinWidth(600);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления видами контактной информации: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onContactInfoButtonClick() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showAlert("Предупреждение", "Выберите клиента для просмотра контактной информации", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("ContactInfo-view.fxml"));
            VBox root = loader.load();
            org.example.vetclinic.controller.ContactInfoController controller = loader.getController();
            String clientName = selectedClient.getLastName() + " " + selectedClient.getFirstName();
            if (selectedClient.getMiddleName() != null && !selectedClient.getMiddleName().isEmpty()) {
                clientName += " " + selectedClient.getMiddleName();
            }
            controller.setClient(selectedClient.getId(), clientName);
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Контактная информация клиента");
            dialogStage.setMinWidth(800);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы контактной информации: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onManageProductCategoriesButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("ProductCategory-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление категориями товаров");
            dialogStage.setMinWidth(500);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления категориями товаров: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onManageServiceCategoriesButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("ServiceCategory-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление категориями услуг");
            dialogStage.setMinWidth(500);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления категориями услуг: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onManageProductsButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Product-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление товарами");
            dialogStage.setMinWidth(800);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления товарами: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onManageServicesButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Service-view.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Управление услугами");
            dialogStage.setMinWidth(900);
            dialogStage.setMinHeight(500);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при открытии формы управления услугами: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onAddVisitButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Visit-view.fxml"));
            AnchorPane root = loader.load();
            VisitController controller = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Создание приема врача");
            dialogStage.setMinWidth(1000);
            dialogStage.setMinHeight(800);
            dialogStage.showAndWait();
            if (controller.isSaveClicked()) {
                Visit visit = controller.getVisit();
                if (visitDAO.addVisit(visit)) {
                    List<Visit> allVisits = visitDAO.getAllVisits();
                    Visit savedVisit = allVisits.stream()
                        .filter(v -> v.getClientId().equals(visit.getClientId()) &&
                                   v.getPetId().equals(visit.getPetId()) &&
                                   v.getVisitDate().equals(visit.getVisitDate()))
                        .max((v1, v2) -> v1.getId().compareTo(v2.getId()))
                        .orElse(null);
                    if (savedVisit != null) {
                        List<VisitService> visitServices = controller.getVisitServices();
                        for (VisitService vs : visitServices) {
                            vs.setVisitId(savedVisit.getId());
                            visitServiceDAO.addVisitService(vs);
                        }
                        List<VisitProduct> visitProducts = controller.getVisitProducts();
                        for (VisitProduct vp : visitProducts) {
                            vp.setVisitId(savedVisit.getId());
                            visitProductDAO.addVisitProduct(vp);
                        }
                        List<MaterialUsed> materialsUsed = controller.getMaterialsUsed();
                        for (MaterialUsed mu : materialsUsed) {
                            mu.setVisitId(savedVisit.getId());
                            materialUsedDAO.addMaterialUsed(mu);
                        }
                        BigDecimal totalCost = BigDecimal.ZERO;
                        for (VisitService vs : visitServices) {
                            if (vs.getSumWithDiscount() != null) {
                                totalCost = totalCost.add(vs.getSumWithDiscount());
                            }
                        }
                        for (VisitProduct vp : visitProducts) {
                            if (vp.getSum() != null) {
                                totalCost = totalCost.add(vp.getSum());
                            }
                        }
                        savedVisit.setTotalCost(totalCost);
                        visitDAO.updateVisit(savedVisit);
                        if (savedVisit.getAppointmentId() != null) {
                            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                            Appointment appointment = allAppointments.stream()
                                .filter(a -> a.getId().equals(savedVisit.getAppointmentId()))
                                .findFirst()
                                .orElse(null);
                            if (appointment != null) {
                                appointment.setStatus("Completed");
                                appointmentDAO.updateAppointment(appointment);
                            }
                        }
                        loadData();
                    } else {
                        loadData();
                    }
                } else {
                    showAlert("Ошибка", "Не удалось добавить прием врача в базу данных", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при создании приема врача: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onEditVisitButtonClick() {
        Visit selectedVisit = visitTable.getSelectionModel().getSelectedItem();
        if (selectedVisit != null) {
            try {
                FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Visit-view.fxml"));
                AnchorPane root = loader.load();
                VisitController controller = loader.getController();
                List<Visit> allVisits = visitDAO.getAllVisits();
                Visit fullVisit = allVisits.stream()
                    .filter(v -> v.getId().equals(selectedVisit.getId()))
                    .findFirst()
                    .orElse(selectedVisit);
                controller.setVisit(fullVisit);
                Stage dialogStage = new Stage();
                Scene scene = new Scene(root);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
                dialogStage.setScene(scene);
                dialogStage.setTitle("Редактирование приема врача");
                dialogStage.setMinWidth(1000);
                dialogStage.setMinHeight(800);
                dialogStage.showAndWait();
                if (controller.isSaveClicked()) {
                    Visit editedVisit = controller.getVisit();
                    if (visitDAO.updateVisit(editedVisit)) {
                        List<VisitService> visitServices = controller.getVisitServices();
                        List<VisitService> existingServices = visitServiceDAO.getVisitServicesByVisit(editedVisit.getId());
                        for (VisitService existing : existingServices) {
                            boolean found = visitServices.stream()
                                .anyMatch(vs -> vs.getServiceId().equals(existing.getServiceId()));
                            if (!found) {
                                visitServiceDAO.deleteVisitService(existing);
                            }
                        }
                        for (VisitService vs : visitServices) {
                            vs.setVisitId(editedVisit.getId());
                            boolean exists = existingServices.stream()
                                .anyMatch(es -> es.getServiceId().equals(vs.getServiceId()));
                            if (exists) {
                                visitServiceDAO.updateVisitService(vs);
                            } else {
                                visitServiceDAO.addVisitService(vs);
                            }
                        }
                        List<VisitProduct> visitProducts = controller.getVisitProducts();
                        List<VisitProduct> existingProducts = visitProductDAO.getVisitProductsByVisit(editedVisit.getId());
                        for (VisitProduct existing : existingProducts) {
                            boolean found = visitProducts.stream()
                                .anyMatch(vp -> vp.getProductId().equals(existing.getProductId()));
                            if (!found) {
                                visitProductDAO.deleteVisitProduct(existing);
                            }
                        }
                        for (VisitProduct vp : visitProducts) {
                            vp.setVisitId(editedVisit.getId());
                            boolean exists = existingProducts.stream()
                                .anyMatch(ep -> ep.getProductId().equals(vp.getProductId()));
                            if (exists) {
                                visitProductDAO.updateVisitProduct(vp);
                            } else {
                                visitProductDAO.addVisitProduct(vp);
                            }
                        }
                        List<MaterialUsed> materialsUsed = controller.getMaterialsUsed();
                        List<MaterialUsed> existingMaterials = materialUsedDAO.getMaterialsUsedByVisit(editedVisit.getId());
                        for (MaterialUsed existing : existingMaterials) {
                            boolean found = materialsUsed.stream()
                                .anyMatch(mu -> mu.getProductId() != null && mu.getProductId().equals(existing.getProductId()));
                            if (!found) {
                                materialUsedDAO.deleteMaterialUsed(existing);
                            }
                        }
                        for (MaterialUsed mu : materialsUsed) {
                            mu.setVisitId(editedVisit.getId());
                            boolean exists = existingMaterials.stream()
                                .anyMatch(em -> em.getProductId() != null && mu.getProductId() != null && em.getProductId().equals(mu.getProductId()));
                            if (exists) {
                                materialUsedDAO.updateMaterialUsed(mu);
                            } else {
                                materialUsedDAO.addMaterialUsed(mu);
                            }
                        }
                        BigDecimal totalCost = BigDecimal.ZERO;
                        for (VisitService vs : visitServices) {
                            if (vs.getSumWithDiscount() != null) {
                                totalCost = totalCost.add(vs.getSumWithDiscount());
                            }
                        }
                        for (VisitProduct vp : visitProducts) {
                            if (vp.getSum() != null) {
                                totalCost = totalCost.add(vp.getSum());
                            }
                        }
                        editedVisit.setTotalCost(totalCost);
                        visitDAO.updateVisit(editedVisit);
                        if (editedVisit.getAppointmentId() != null) {
                            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                            Appointment appointment = allAppointments.stream()
                                .filter(a -> a.getId().equals(editedVisit.getAppointmentId()))
                                .findFirst()
                                .orElse(null);
                            if (appointment != null) {
                                appointment.setStatus("Completed");
                                appointmentDAO.updateAppointment(appointment);
                            }
                        }
                        loadData();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить прием врача в базе данных", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Произошла ошибка при редактировании приема врача: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Предупреждение", "Выберите прием для редактирования", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onPayVisitButtonClick() {
        Visit selectedVisit = visitTable.getSelectionModel().getSelectedItem();
        if (selectedVisit == null) {
            showAlert("Предупреждение", "Выберите прием для оплаты", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Payment-view.fxml"));
            AnchorPane root = loader.load();
            PaymentController controller = loader.getController();
            List<Visit> allVisits = visitDAO.getAllVisits();
            Visit fullVisit = allVisits.stream()
                .filter(v -> v.getId().equals(selectedVisit.getId()))
                .findFirst()
                .orElse(selectedVisit);
            controller.setPaymentFromVisit(fullVisit);
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Оплата приема врача");
            dialogStage.showAndWait();
            if (controller.isSaveClicked()) {
                Payment payment = controller.getPayment();
                if (paymentDAO.addPayment(payment)) {
                    fullVisit.setPaymentStatus("Оплачено");
                    loadData();
                } else {
                    showAlert("Ошибка", "Не удалось добавить оплату в базу данных", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при создании оплаты: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onAddPaymentButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Payment-view.fxml"));
            AnchorPane root = loader.load();
            PaymentController controller = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Создание оплаты");
            dialogStage.showAndWait();
            if (controller.isSaveClicked()) {
                Payment payment = controller.getPayment();
                if (paymentDAO.addPayment(payment)) {
                    if (payment.getVisitId() != null) {
                        List<Visit> allVisits = visitDAO.getAllVisits();
                        Visit visit = allVisits.stream()
                            .filter(v -> v.getId().equals(payment.getVisitId()))
                            .findFirst()
                            .orElse(null);
                        if (visit != null) {
                            visit.setPaymentStatus("Оплачено");
                        }
                    }
                    loadData();
                } else {
                    showAlert("Ошибка", "Не удалось добавить оплату в базу данных", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при создании оплаты: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onEditPaymentButtonClick() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            try {
                FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("Payment-view.fxml"));
                AnchorPane root = loader.load();
                PaymentController controller = loader.getController();
                List<Payment> allPayments = paymentDAO.getAllPayments();
                Payment fullPayment = allPayments.stream()
                    .filter(p -> p.getId().equals(selectedPayment.getId()))
                    .findFirst()
                    .orElse(selectedPayment);
                controller.setPayment(fullPayment);
                Stage dialogStage = new Stage();
                Scene scene = new Scene(root);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
                dialogStage.setScene(scene);
                dialogStage.setTitle("Редактирование оплаты");
                dialogStage.showAndWait();
                if (controller.isSaveClicked()) {
                    Payment editedPayment = controller.getPayment();
                    if (paymentDAO.updatePayment(editedPayment)) {
                        loadData();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить оплату в базе данных", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Произошла ошибка при редактировании оплаты: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Предупреждение", "Выберите оплату для редактирования", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void onVisitsReportMenuItemClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("ReportParameters-view.fxml"));
            AnchorPane root = loader.load();
            ReportParametersController paramsController = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Параметры отчета о приемах");
            dialogStage.showAndWait();
            if (paramsController.isGenerateClicked()) {
                FXMLLoader reportLoader = new FXMLLoader(VETClinicApplication.class.getResource("VisitsReport-view.fxml"));
                BorderPane reportRoot = reportLoader.load();
                VisitsReportController reportController = reportLoader.getController();
                reportController.loadReport(
                    paramsController.getStartDate(),
                    paramsController.getEndDate(),
                    paramsController.getSelectedEmployeeId(),
                    paramsController.getSelectedClientId(),
                    paramsController.getSortField(),
                    paramsController.isAscending()
                );
                Stage reportStage = new Stage();
                Scene reportScene = new Scene(reportRoot);
                reportStage.initModality(Modality.WINDOW_MODAL);
                reportStage.initOwner(VETClinicApplication.getPrimaryStage());
                reportStage.setScene(reportScene);
                reportStage.setTitle("Отчет о приемах за период");
                reportStage.setMinWidth(1000);
                reportStage.setMinHeight(700);
                reportStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при формировании отчета: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onProductsServicesReportMenuItemClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("ProductsServicesReportParameters-view.fxml"));
            AnchorPane root = loader.load();
            ProductsServicesReportParametersController paramsController = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Параметры отчета по товарам и услугам");
            dialogStage.showAndWait();
            if (paramsController.isGenerateReportClicked()) {
                FXMLLoader reportLoader = new FXMLLoader(VETClinicApplication.class.getResource("ProductsServicesReport-view.fxml"));
                BorderPane reportRoot = reportLoader.load();
                ProductsServicesReportController reportController = reportLoader.getController();
                reportController.setReportParameters(
                    paramsController.getStartDate(),
                    paramsController.getEndDate(),
                    paramsController.getSelectedEmployeeId(),
                    paramsController.getSelectedItemType(),
                    paramsController.getSortField(),
                    paramsController.isAscending()
                );
                Stage reportStage = new Stage();
                Scene reportScene = new Scene(reportRoot);
                reportStage.initModality(Modality.WINDOW_MODAL);
                reportStage.initOwner(VETClinicApplication.getPrimaryStage());
                reportStage.setScene(reportScene);
                reportStage.setTitle("Отчет по товарам и услугам");
                reportStage.setMinWidth(1000);
                reportStage.setMinHeight(700);
                reportStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при формировании отчета: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onClientsPetsReportMenuItemClick() {
        try {
            FXMLLoader loader = new FXMLLoader(VETClinicApplication.class.getResource("ClientsPetsReportParameters-view.fxml"));
            AnchorPane root = loader.load();
            ClientsPetsReportParametersController paramsController = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(VETClinicApplication.getPrimaryStage());
            dialogStage.setScene(scene);
            dialogStage.setTitle("Параметры отчета по клиентам и питомцам");
            dialogStage.showAndWait();
            if (paramsController.isGenerateReportClicked()) {
                FXMLLoader reportLoader = new FXMLLoader(VETClinicApplication.class.getResource("ClientsPetsReport-view.fxml"));
                BorderPane reportRoot = reportLoader.load();
                ClientsPetsReportController reportController = reportLoader.getController();
                reportController.setReportParameters(
                    paramsController.getStartDate(),
                    paramsController.getEndDate(),
                    paramsController.getSelectedClientId(),
                    paramsController.getSelectedSpeciesId(),
                    paramsController.getSelectedBreedId(),
                    paramsController.getSortField(),
                    paramsController.isAscending()
                );
                Stage reportStage = new Stage();
                Scene reportScene = new Scene(reportRoot);
                reportStage.initModality(Modality.WINDOW_MODAL);
                reportStage.initOwner(VETClinicApplication.getPrimaryStage());
                reportStage.setScene(reportScene);
                reportStage.setTitle("Отчет по клиентам и питомцам");
                reportStage.setMinWidth(1200);
                reportStage.setMinHeight(700);
                reportStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при формировании отчета: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onExitMenuItemClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение выхода");
        alert.setHeaderText("Выход из приложения");
        alert.setContentText("Вы уверены, что хотите выйти из приложения?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            javafx.application.Platform.exit();
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
