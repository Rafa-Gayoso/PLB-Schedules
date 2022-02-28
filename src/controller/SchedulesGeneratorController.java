package controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import services.GetEmpService;

public class SchedulesGeneratorController {

    @FXML
    private JFXProgressBar progressBar;

    public void setData(Service service){
        progressBar.setProgress(service.getProgress());

    }
}
