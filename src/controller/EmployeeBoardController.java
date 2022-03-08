package controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Empleado;
import services.GetEmployeeScheduleService;
import services.GetEmployeeScheduleTask;
import services.GetVacationsService;
import utils.CreateSplashScreen;
import utils.FormatEmployeeName;
import utils.SMBUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import utils.VacationsController;

public class EmployeeBoardController implements Initializable {

    private final String PIC_DIR = "config_files" + File.separator + "Employees";

    @FXML
    private ImageView profilePic;


    @FXML
    private JFXButton vacationsBtn;

    @FXML
    private JFXButton scheduleBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Empleado employee = LoginController.getEmpleado();
        try {
            SMBUtils.downloadSmbPhoto(employee.getNombre()+".png",PIC_DIR);
            File file = new File(PIC_DIR+File.separator+employee.getNombre()+".png");
            if(!file.exists()){
                file = new File(PIC_DIR+File.separator+"profile.png");
            }
            Image image = null;

            image = new Image(new FileInputStream(file));
            profilePic.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        vacationsBtn.setOnAction(action -> {
            try {
                ArrayList<Empleado> employees = new ArrayList<Empleado>();
                employees.add(employee);
                VacationsController.getVacationsDaysEmployees(employees);
                /*GetVacationsService services = new GetVacationsService(employees);
                services.start();

                Stage stage = CreateSplashScreen.createPDFSplashScreen(services);


                services.setOnRunning(event -> {
                    stage.show();
                });
                services.setOnSucceeded(event -> {
                    stage.close();
                });*/

            } catch (Exception e) {
                e.printStackTrace();
            }


        });


        scheduleBtn.setOnAction(action -> {


            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/EmployeeSchedulePane.fxml"));
                Parent parent = fxmlLoader.load();
                EmployeeSchedulePaneController test = fxmlLoader.getController();
                String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
                SMBUtils.downloadSmbFile(employee.getNombre_empresa(), employeeFileName, employee.getDireccionCronograma());
                test.setData(employee);
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
                dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));

                Scene scene = new Scene(parent);
                dialogStage.setScene(scene);
                dialogStage.show();
            } catch (Exception e) {

            }

        });

    }

}
