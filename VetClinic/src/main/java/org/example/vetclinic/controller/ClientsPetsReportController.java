package org.example.vetclinic.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.vetclinic.database.ClientsPetsReportDAO;
import org.example.vetclinic.model.ClientsPetsReport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class ClientsPetsReportController {
    @FXML private Label periodLabel;
    @FXML private TableView<ClientsPetsReport> reportTable;
    @FXML private TableView<ClientsPetsReport> summaryTable;
    private ClientsPetsReportDAO reportDAO = new ClientsPetsReportDAO();
    @FXML
    private void initialize() {
        summaryTable.setEditable(false);
    }
    public void setReportParameters(LocalDate startDate, LocalDate endDate, 
                                   Integer clientId, Integer speciesId, Integer breedId,
                                   String sortField, boolean ascending) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        periodLabel.setText("Период: с " + startDate.format(formatter) + " по " + endDate.format(formatter));
        List<ClientsPetsReport> reportData = reportDAO.getClientsPetsReport(
            startDate, endDate, clientId, speciesId, breedId, sortField, ascending);
        reportTable.getItems().setAll(reportData);
        ClientsPetsReport summary = reportDAO.getClientsPetsReportSummary(
            startDate, endDate, clientId, speciesId, breedId);
        List<ClientsPetsReport> summaryData = new ArrayList<>();
        if (summary != null) {
            ClientsPetsReport row1 = new ClientsPetsReport(null, "Общее количество клиентов", null, 
                summary.getTotalClients() != null ? summary.getTotalClients().toString() : "0", 
                null, null, null, null, null, null, null, null);
            summaryData.add(row1);
            ClientsPetsReport row2 = new ClientsPetsReport(null, "Общее количество питомцев", null, 
                summary.getTotalPets() != null ? summary.getTotalPets().toString() : "0", 
                null, null, null, null, null, null, null, null);
            summaryData.add(row2);
            ClientsPetsReport row3 = new ClientsPetsReport(null, "Общее количество приемов", null, 
                summary.getTotalVisits() != null ? summary.getTotalVisits().toString() : "0", 
                null, null, null, null, null, null, null, null);
            summaryData.add(row3);
            ClientsPetsReport row4 = new ClientsPetsReport(null, "Общая стоимость", null, 
                summary.getTotalCostSum() != null ? summary.getTotalCostSum().toString() : "0.00", 
                null, null, null, null, null, null, null, null);
            summaryData.add(row4);
            ClientsPetsReport row5 = new ClientsPetsReport(null, "Средний чек (общий)", null, 
                summary.getAverageCheckOverall() != null ? summary.getAverageCheckOverall().toString() : "0.00", 
                null, null, null, null, null, null, null, null);
            summaryData.add(row5);
            ClientsPetsReport row6 = new ClientsPetsReport(null, "Средний чек на питомца", null, 
                summary.getAverageCheckPerPet() != null ? summary.getAverageCheckPerPet().toString() : "0.00", 
                null, null, null, null, null, null, null, null);
            summaryData.add(row6);
        }
        summaryTable.getItems().setAll(summaryData);
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) periodLabel.getScene().getWindow();
        stage.close();
    }
}
