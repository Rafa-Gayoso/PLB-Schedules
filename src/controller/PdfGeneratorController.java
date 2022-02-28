package controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import services.GetVacationsService;

public class PdfGeneratorController {

    @FXML
    private JFXProgressBar progressBar;

    public void setData(GetVacationsService service){
        progressBar.setProgress(service.getProgress());

    }
}
