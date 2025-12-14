package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.SpecializationDAO;
import org.example.vetclinic.model.Employee;
import org.example.vetclinic.model.Specialization;
import org.example.vetclinic.util.DatePickerUtils;
import java.time.LocalDate;
public class EmployeeController {
    @FXML private Label idLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField middleNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<Specialization> specializationComboBox;
    @FXML private DatePicker hireDatePicker;
    @FXML private DatePicker dismissalDatePicker;
    @FXML private CheckBox activeCheckBox;
    private ObservableList<Specialization> specializationData = FXCollections.observableArrayList();
    private Employee employee;
    private boolean saveClicked = false;
    private SpecializationDAO specializationDAO = new SpecializationDAO();
    @FXML
    private void initialize() {
        DatePickerUtils.setupDateInputPattern(birthDatePicker);
        DatePickerUtils.setupDateInputPattern(hireDatePicker);
        DatePickerUtils.setupDateInputPattern(dismissalDatePicker);
        specializationComboBox.setConverter(new StringConverter<Specialization>() {
            @Override
            public String toString(Specialization specialization) {
                return specialization == null ? "" : specialization.getSpecializationName();
            }
            @Override
            public Specialization fromString(String string) {
                return null;
            }
        });
        loadSpecializations();
    }
    @FXML
    private void onSaveButtonClick() {
        if (isInputValid()) {
            Integer id = employee != null ? employee.getId() : null;
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            if (middleName.isEmpty()) {
                middleName = null;
            }
            LocalDate birthDate = birthDatePicker.getValue();
            Specialization selectedSpecialization = specializationComboBox.getValue();
            LocalDate hireDate = hireDatePicker.getValue();
            if (hireDate == null) {
                hireDate = LocalDate.now();
            }
            LocalDate dismissalDate = dismissalDatePicker.getValue();
            Boolean active = activeCheckBox.isSelected();
            employee = new Employee(id, firstName, lastName, middleName,
                                   birthDate,
                                   selectedSpecialization != null ? selectedSpecialization.getId() : null,
                                   hireDate, dismissalDate, active);
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
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }
    private boolean isInputValid() {
        String errorMessage = "";
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            errorMessage += "Имя не может быть пустым!\n";
        }
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            errorMessage += "Фамилия не может быть пустой!\n";
        }
        if (hireDatePicker.getValue() == null) {
            errorMessage += "Дата приема не может быть пустой!\n";
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
    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            if (employee.getId() != null) {
                idLabel.setText(employee.getId().toString());
            } else {
                idLabel.setText("(новый)");
            }
            firstNameField.setText(employee.getFirstName() != null ? employee.getFirstName() : "");
            lastNameField.setText(employee.getLastName() != null ? employee.getLastName() : "");
            middleNameField.setText(employee.getMiddleName() != null ? employee.getMiddleName() : "");
            birthDatePicker.setValue(employee.getBirthDate());
            if (employee.getSpecializationId() != null) {
                Specialization empSpecialization = specializationData.stream()
                    .filter(s -> s.getId().equals(employee.getSpecializationId()))
                    .findFirst()
                    .orElse(null);
                specializationComboBox.setValue(empSpecialization);
            }
            hireDatePicker.setValue(employee.getHireDate());
            dismissalDatePicker.setValue(employee.getDismissalDate());
            activeCheckBox.setSelected(employee.getActive() != null ? employee.getActive() : true);
        } else {
            idLabel.setText("(новый)");
            firstNameField.setText("");
            lastNameField.setText("");
            middleNameField.setText("");
            birthDatePicker.setValue(null);
            specializationComboBox.setValue(null);
            hireDatePicker.setValue(LocalDate.now());
            dismissalDatePicker.setValue(null);
            activeCheckBox.setSelected(true);
        }
    }
    private void loadSpecializations() {
        specializationData.clear();
        specializationData.addAll(specializationDAO.getAllSpecializations());
        specializationComboBox.setItems(specializationData);
    }
}
