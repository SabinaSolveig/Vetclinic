package org.example.vetclinic.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.ClientDAO;
import org.example.vetclinic.database.SpeciesDAO;
import org.example.vetclinic.database.BreedDAO;
import org.example.vetclinic.model.Client;
import org.example.vetclinic.model.Species;
import org.example.vetclinic.model.Breed;
import org.example.vetclinic.util.DatePickerUtils;
import java.time.LocalDate;
import java.util.List;
public class ClientsPetsReportParametersController {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<Client> clientFilter;
    @FXML private ComboBox<Species> speciesFilter;
    @FXML private ComboBox<Breed> breedFilter;
    @FXML private ComboBox<String> sortFieldComboBox;
    @FXML private ComboBox<String> sortDirectionComboBox;
    private boolean generateClicked = false;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer selectedClientId;
    private Integer selectedSpeciesId;
    private Integer selectedBreedId;
    private String sortField;
    private boolean ascending;
    private ClientDAO clientDAO = new ClientDAO();
    private SpeciesDAO speciesDAO = new SpeciesDAO();
    private BreedDAO breedDAO = new BreedDAO();
    @FXML
    private void initialize() {
        DatePickerUtils.setupDateInputPattern(startDatePicker);
        DatePickerUtils.setupDateInputPattern(endDatePicker);
        LocalDate now = LocalDate.now();
        startDatePicker.setValue(now.withDayOfMonth(1));
        endDatePicker.setValue(now.withDayOfMonth(now.lengthOfMonth()));
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
        List<Species> species = speciesDAO.getAllSpecies();
        speciesFilter.getItems().add(null);
        speciesFilter.getItems().addAll(species);
        speciesFilter.setConverter(new StringConverter<Species>() {
            @Override
            public String toString(Species species) {
                if (species == null) return "Все виды";
                return species.getSpeciesName();
            }
            @Override
            public Species fromString(String string) {
                return null;
            }
        });
        List<Breed> breeds = breedDAO.getAllBreeds();
        breedFilter.getItems().add(null);
        breedFilter.getItems().addAll(breeds);
        breedFilter.setConverter(new StringConverter<Breed>() {
            @Override
            public String toString(Breed breed) {
                if (breed == null) return "Все породы";
                return breed.getBreedName();
            }
            @Override
            public Breed fromString(String string) {
                return null;
            }
        });
        sortFieldComboBox.getItems().addAll(
            "Клиент",
            "Питомец",
            "Вид",
            "Порода",
            "Количество приемов",
            "Общая стоимость",
            "Средний чек"
        );
        sortFieldComboBox.setValue("Клиент");
        sortDirectionComboBox.getItems().addAll("По возрастанию", "По убыванию");
        sortDirectionComboBox.setValue("По возрастанию");
    }
    @FXML
    private void onGenerateReportButtonClick() {
        if (isInputValid()) {
            startDate = startDatePicker.getValue();
            endDate = endDatePicker.getValue();
            Client selectedClient = clientFilter.getValue();
            selectedClientId = selectedClient != null ? selectedClient.getId() : null;
            Species selectedSpecies = speciesFilter.getValue();
            selectedSpeciesId = selectedSpecies != null ? selectedSpecies.getId() : null;
            Breed selectedBreed = breedFilter.getValue();
            selectedBreedId = selectedBreed != null ? selectedBreed.getId() : null;
            String sortFieldName = sortFieldComboBox.getValue();
            sortField = sortFieldName != null ? sortFieldName : "Клиент";
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
    public Integer getSelectedClientId() {
        return selectedClientId;
    }
    public Integer getSelectedSpeciesId() {
        return selectedSpeciesId;
    }
    public Integer getSelectedBreedId() {
        return selectedBreedId;
    }
    public String getSortField() {
        return sortField;
    }
    public boolean isAscending() {
        return ascending;
    }
}
