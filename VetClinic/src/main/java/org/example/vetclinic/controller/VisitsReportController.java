package org.example.vetclinic.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.vetclinic.database.VisitsReportDAO;
import org.example.vetclinic.model.VisitsReport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class VisitsReportController {
    @FXML private Label periodLabel;
    @FXML private TableView<VisitsReport> reportTable;
    @FXML private TableView<VisitsReport> summaryTable;
    private ObservableList<VisitsReport> reportData = FXCollections.observableArrayList();
    private ObservableList<VisitsReport> summaryData = FXCollections.observableArrayList();
    private VisitsReportDAO reportDAO = new VisitsReportDAO();
    @FXML
    private void initialize() {
        reportTable.setItems(reportData);
        summaryTable.setItems(summaryData);
    }
    public void loadReport(LocalDate startDate, LocalDate endDate, 
                          Integer employeeId, Integer clientId,
                          String sortField, boolean ascending) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        periodLabel.setText("Период: с " + startDate.format(formatter) + " по " + endDate.format(formatter));
        List<VisitsReport> report = reportDAO.getVisitsReport(startDate, endDate, employeeId, clientId, sortField, ascending);
        reportData.setAll(report);
        List<VisitsReport> summary = reportDAO.getVisitsReportSummary(startDate, endDate, employeeId, clientId);
        summaryData.setAll(summary);
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) periodLabel.getScene().getWindow();
        stage.close();
    }
}
