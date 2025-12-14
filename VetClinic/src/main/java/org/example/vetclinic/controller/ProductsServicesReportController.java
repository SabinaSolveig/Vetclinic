package org.example.vetclinic.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.vetclinic.database.ProductsServicesReportDAO;
import org.example.vetclinic.model.ProductsServicesReport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class ProductsServicesReportController {
    @FXML private Label periodLabel;
    @FXML private TableView<ProductsServicesReport> reportTable;
    @FXML private TableView<ProductsServicesReport> summaryTable;
    private ProductsServicesReportDAO reportDAO = new ProductsServicesReportDAO();
    @FXML
    private void initialize() {
    }
    public void setReportParameters(LocalDate startDate, LocalDate endDate, 
                                   Integer employeeId, String itemType,
                                   String sortField, boolean ascending) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        periodLabel.setText("Период: с " + startDate.format(formatter) + " по " + endDate.format(formatter));
        List<ProductsServicesReport> reportData = reportDAO.getProductsServicesReport(
            startDate, endDate, employeeId, itemType, sortField, ascending);
        reportTable.getItems().setAll(reportData);
        List<ProductsServicesReport> summaryData = reportDAO.getProductsServicesReportSummary(
            startDate, endDate, employeeId, itemType);
        summaryTable.getItems().setAll(summaryData);
    }
    @FXML
    private void onCloseButtonClick() {
        Stage stage = (Stage) periodLabel.getScene().getWindow();
        stage.close();
    }
}
