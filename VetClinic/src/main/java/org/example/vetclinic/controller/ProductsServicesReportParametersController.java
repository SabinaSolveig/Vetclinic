package org.example.vetclinic.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.EmployeeDAO;
import org.example.vetclinic.model.Employee;
import org.example.vetclinic.util.DatePickerUtils;
import java.time.LocalDate;
import java.util.List;
public class ProductsServicesReportParametersController {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<Employee> employeeFilter;
    @FXML private ComboBox<String> itemTypeFilter;
    @FXML private ComboBox<String> sortFieldComboBox;
    @FXML private ComboBox<String> sortDirectionComboBox;
    private boolean generateClicked = false;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer selectedEmployeeId;
    private String selectedItemType;
    private String sortField;
    private boolean ascending;
    private EmployeeDAO employeeDAO = new EmployeeDAO();
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
        itemTypeFilter.getItems().addAll("Все", "Товар", "Услуга");
        itemTypeFilter.setValue("Все");
        sortFieldComboBox.getItems().addAll(
            "Врач",
            "Тип",
            "Товар/Услуга",
            "Количество",
            "Стоимость"
        );
        sortFieldComboBox.setValue("Врач");
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
            selectedItemType = itemTypeFilter.getValue();
            if (selectedItemType != null && selectedItemType.equals("Все")) {
                selectedItemType = null;
            }
            String sortFieldName = sortFieldComboBox.getValue();
            switch (sortFieldName) {
                case "Врач":
                    sortField = "Врач";
                    break;
                case "Тип":
                    sortField = "Тип";
                    break;
                case "Товар/Услуга":
                    sortField = "Товар/Услуга";
                    break;
                case "Количество":
                    sortField = "Количество";
                    break;
                case "Стоимость":
                    sortField = "Стоимость";
                    break;
                default:
                    sortField = "Врач";
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
    public boolean isGenerateReportClicked() {
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
    public String getSelectedItemType() {
        return selectedItemType;
    }
    public String getSortField() {
        return sortField;
    }
    public boolean isAscending() {
        return ascending;
    }
}
