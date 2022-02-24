package utils;

import controller.PdfGeneratorController;
import controller.SchedulesGeneratorController;
import javafx.concurrent.Service;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.GetEmpService;
import services.GetEmployeeScheduleService;
import services.GetVacationsService;

public class CreateSplashScreen {

    public static Stage createPDFSplashScreen(GetVacationsService services){
        Stage stage = new Stage();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(CreateSplashScreen.class.getResource("/resources/fxml/PdfGenerator.fxml"));
            Parent parent = fxmlLoader.load();

            PdfGeneratorController pdfGenerator = fxmlLoader.getController();
            pdfGenerator.setData(services);


            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.setAlwaysOnTop(true);
            stage.setScene(scene);
        }catch(Exception e){

        }
        return stage;
    }

    public static Stage createEmployeeSplashScreen(GetEmployeeScheduleService services){
        Stage stage = new Stage();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(CreateSplashScreen.class.getResource("/resources/fxml/SchedulesGenerator.fxml"));
            Parent parent = fxmlLoader.load();

            SchedulesGeneratorController schedulesGenerator = fxmlLoader.getController();
            schedulesGenerator.setData(services);


            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.setAlwaysOnTop(true);
            stage.setScene(scene);
        }catch(Exception e){

        }
        return stage;
    }
}
