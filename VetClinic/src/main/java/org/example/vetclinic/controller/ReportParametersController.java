package org.example.vetclinic.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.ClientDAO;
import org.example.vetclinic.database.EmployeeDAO;
import org.example.vetclinic.model.Client;
import org.example.vetclinic.model.Employee;
import org.example.vetclinic.util.DatePickerUtils;
import java.time.LocalDate;
import java.util.List;
public class ReportParametersController {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<Employee> employeeFilter;
    @FXML private ComboBox<Client> clientFilter;
    @FXML private ComboBox<String> sortFieldComboBox;
    @FXML private ComboBox<String> sortDirectionComboBox;
    private boolean generateClicked = false;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer selectedEmployeeId;
    private Integer selectedClientId;
    private String sortField;
    private boolean ascending;
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private ClientDAO clientDAO = new ClientDAO();
    @FXML
    private void initialize() {
        DatePickerUtils.setupDateInputPattern(startDatePicker);
        DatePickerUtils.setupDateInputPattern(endDatePicker);
        LocalDate now = LocalDate.now();
        startDatePicker.setValue(now.withDayOfMonth(1));
        endDatePicker.setValue(now.withDayOfMonth(now.lengthOfMonth()));
        List<Employee> employees = employeeDAO.getAllEmployees();
        employeeFilter.getItems().add(null);
        employeeFilter.getItems().addAll(employees);
        employeeFilter.setConverter(new StringConverter<Employee>() {
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
        List<Client> clients = clientDAO.getClients();
        clientFilter.getItems().add(null);
        clientFilter.getItems().addAll(clients);
        clientFilter.setConverter(new StringConverter<Client>() {
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
        sortFieldComboBox.getItems().addAll(
            "Сотрудник",
            "Клиент",
            "Количество приемов",
            "Стоимость",
            "Среднее время приема"
        );
        sortFieldComboBox.setValue("Сотрудник");
        sortDirectionComboBox.getItems().addAll("По возрастанию", "По убыванию");
        sortDirectionComboBox.setValue("По возрастанию");
    }
    @FXML
    private void onGenerateReportButtonClick() {
        if (isInputValid()) {
            startDate = startDatePicker.getValue();
            endDate = endDatePicker.getValue();
            Employee selectedEmployee = employeeFilter.getValue();
            selectedEmployeeId = selectedEmployee != null ? selectedEmployee.getId() : null;
            Client selectedClient = clientFilter.getValue();
            selectedClientId = selectedClient != null ? selectedClient.getId() : null;
            String sortFieldName = sortFieldComboBox.getValue();
            switch (sortFieldName) {
                case "Сотрудник":
                    sortField = "EmployeeName";
                    break;
                case "Клиент":
                    sortField = "ClientName";
                    break;
                case "Количество приемов":
                    sortField = "VisitCount";
                    break;
                case "Стоимость":
                    sortField = "TotalCost";
                    break;
                case "Среднее время приема":
                    sortField = "AverageVisitDuration";
                    break;
                default:
                    sortField = "EmployeeName";
            }
            ascending = sortDirectionComboBox.getValue().equals("По возрастанию");
            generateClicked = true;
            closeDialog();
        }
    }
    @FXML
    private void onCancelButtonClick() {
        closeDialog();
    }
    private boolean isInputValid() {
        String errorMessage = "";
        if (startDatePicker.getValue() == null) {
            errorMessage += "Необходимо указать начальную дату периода!\n";
        }
        if (endDatePicker.getValue() == null) {
            errorMessage += "Необходимо указать конечную дату периода!\n";
        }
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                errorMessage += "Начальная дата не может быть позже конечной даты!\n";
            }
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
    private void closeDialog() {
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }
    public boolean isGenerateClicked() {
        return generateClicked;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public Integer getSelectedEmployeeId() {
        return selectedEmployeeId;
    }
    public Integer getSelectedClientId() {
        return selectedClientId;
    }
    public String getSortField() {
        return sortField;
    }
    public boolean isAscending() {
        return ascending;
    }
}
