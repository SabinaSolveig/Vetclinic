package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.vetclinic.database.BreedDAO;
import org.example.vetclinic.database.ClientDAO;
import org.example.vetclinic.database.SpeciesDAO;
import org.example.vetclinic.model.Breed;
import org.example.vetclinic.model.Client;
import org.example.vetclinic.model.Pet;
import org.example.vetclinic.model.Species;
import org.example.vetclinic.util.DatePickerUtils;
import java.time.LocalDate;
public class PetController {
    @FXML private Label idLabel;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private TextField nameField;
    @FXML private ComboBox<Species> speciesComboBox;
    @FXML private ComboBox<Breed> breedComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField ageField;
    @FXML private RadioButton maleRadioButton;
    @FXML private RadioButton femaleRadioButton;
    @FXML private TextArea notesField;
    private ToggleGroup genderGroup = new ToggleGroup();
    private Pet pet;
    private boolean saveClicked = false;
    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private ObservableList<Species> speciesData = FXCollections.observableArrayList();
    private ObservableList<Breed> breedData = FXCollections.observableArrayList();
    private ObservableList<Breed> allBreedsData = FXCollections.observableArrayList();
    private ClientDAO clientDAO = new ClientDAO();
    private SpeciesDAO speciesDAO = new SpeciesDAO();
    private BreedDAO breedDAO = new BreedDAO();
    @FXML
    private void initialize() {
        DatePickerUtils.setupDateInputPattern(birthDatePicker);
        maleRadioButton.setToggleGroup(genderGroup);
        femaleRadioButton.setToggleGroup(genderGroup);
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
        speciesComboBox.setConverter(new StringConverter<Species>() {
            @Override
            public String toString(Species species) {
                return species == null ? "" : species.getSpeciesName();
            }
            @Override
            public Species fromString(String string) {
                return null;
            }
        });
        breedComboBox.setConverter(new StringConverter<Breed>() {
            @Override
            public String toString(Breed breed) {
                return breed == null ? "" : breed.getBreedName();
            }
            @Override
            public Breed fromString(String string) {
                return null;
            }
        });
        speciesComboBox.valueProperty().addListener((_, _, newSpecies) -> {
            updateBreedsForSpecies(newSpecies);
        });
        loadData();
    }
    private void updateBreedsForSpecies(Species species) {
        breedComboBox.getItems().clear();
        if (species != null) {
            breedData.clear();
            breedData.addAll(breedDAO.getBreedsBySpecies(species.getId()));
            breedComboBox.setItems(breedData);
        } else {
            breedComboBox.setItems(FXCollections.observableArrayList());
        }
    }
    @FXML
    private void onSaveButtonClick() {
        if (isInputValid()) {
            Integer id = pet != null ? pet.getId() : null;
            Client selectedClient = clientComboBox.getValue();
            String name = nameField.getText().trim();
            Species selectedSpecies = speciesComboBox.getValue();
            Breed selectedBreed = breedComboBox.getValue();
            LocalDate birthDate = birthDatePicker.getValue();
            Integer age = null;
            String ageText = ageField.getText().trim();
            if (!ageText.isEmpty()) {
                try {
                    age = Integer.parseInt(ageText);
                    if (age < 0) {
                        showAlert("Ошибка", "Возраст не может быть отрицательным", Alert.AlertType.ERROR);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Возраст должен быть числом", Alert.AlertType.ERROR);
                    return;
                }
            }
            String gender = null;
            if (maleRadioButton.isSelected()) {
                gender = "M";
            } else if (femaleRadioButton.isSelected()) {
                gender = "F";
            }
            String notes = notesField.getText().trim();
            if (notes.isEmpty()) {
                notes = null;
            }
            pet = new Pet(id, selectedClient.getId(), name,
                         selectedSpecies != null ? selectedSpecies.getId() : null,
                         selectedBreed != null ? selectedBreed.getId() : null,
                         birthDate, age, gender, notes);
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
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    private boolean isInputValid() {
        String errorMessage = "";
        if (clientComboBox.getValue() == null) {
            errorMessage += "Необходимо выбрать клиента!\n";
        }
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "Кличка не может быть пустой!\n";
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
    public Pet getPet() {
        return pet;
    }
    public void setSelectedClient(Client client) {
        if (client != null) {
            clientComboBox.setValue(client);
        }
    }
    public void setPet(Pet pet) {
        this.pet = pet;
        if (pet != null) {
            if (pet.getId() != null) {
                idLabel.setText(pet.getId().toString());
            } else {
                idLabel.setText("(новый)");
            }
            Client petClient = clientData.stream()
                .filter(c -> c.getId().equals(pet.getClientId()))
                .findFirst()
                .orElse(null);
            clientComboBox.setValue(petClient);
            nameField.setText(pet.getName() != null ? pet.getName() : "");
            if (pet.getSpeciesId() != null) {
                Species petSpecies = speciesData.stream()
                    .filter(s -> s.getId().equals(pet.getSpeciesId()))
                    .findFirst()
                    .orElse(null);
                speciesComboBox.setValue(petSpecies);
                updateBreedsForSpecies(petSpecies);
            }
            if (pet.getBreedId() != null) {
                Breed petBreed = breedData.stream()
                    .filter(b -> b.getId().equals(pet.getBreedId()))
                    .findFirst()
                    .orElse(null);
                breedComboBox.setValue(petBreed);
            }
            birthDatePicker.setValue(pet.getBirthDate());
            if (pet.getAge() != null) {
                ageField.setText(pet.getAge().toString());
            } else {
                ageField.setText("");
            }
            if ("M".equals(pet.getGender())) {
                maleRadioButton.setSelected(true);
            } else if ("F".equals(pet.getGender())) {
                femaleRadioButton.setSelected(true);
            }
            notesField.setText(pet.getNotes() != null ? pet.getNotes() : "");
        } else {
            idLabel.setText("(новый)");
            clientComboBox.setValue(null);
            nameField.setText("");
            speciesComboBox.setValue(null);
            breedComboBox.setValue(null);
            birthDatePicker.setValue(null);
            ageField.setText("");
            genderGroup.selectToggle(null);
            notesField.setText("");
        }
    }
    private void loadData() {
        clientData.clear();
        clientData.addAll(clientDAO.getClients());
        clientComboBox.setItems(clientData);
        speciesData.clear();
        speciesData.addAll(speciesDAO.getAllSpecies());
        speciesComboBox.setItems(speciesData);
        allBreedsData.clear();
        allBreedsData.addAll(breedDAO.getAllBreeds());
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
