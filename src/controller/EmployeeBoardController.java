package controller;

import com.jfoenix.controls.JFXButton;
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

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EmployeeBoardController implements Initializable {

    @FXML
    private ImageView profilePic;


    @FXML
    private JFXButton vacationsBtn;

    @FXML
    private JFXButton scheduleBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Empleado employee = LoginController.getEmpleado();

        vacationsBtn.setOnAction(action->{
            try{
                ArrayList<Empleado> employees = new ArrayList<Empleado>();
                employees.add(employee);
                GetVacationsService services = new GetVacationsService(employees);
                services.start();

                Stage stage = CreateSplashScreen.createPDFSplashScreen(services);


                services.setOnRunning(event -> {
                    stage.show();
                });
                services.setOnSucceeded(event -> {
                    stage.close();
                });

            }catch(Exception e){
                e.printStackTrace();
            }


        });


        scheduleBtn.setOnAction(action->{

            GetEmployeeScheduleService services = new GetEmployeeScheduleService(employee);
            services.start();

            Stage stage = CreateSplashScreen.createEmployeeSplashScreen(services);


            services.setOnRunning(event -> {
                stage.show();
            });
            services.setOnSucceeded(event -> {
                stage.close();
            });
            /*try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/EmployeeSchedulePane.fxml"));
                Parent parent = fxmlLoader.load();
                EmployeeSchedulePaneController test  = fxmlLoader.getController();
                String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
                SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
                test.setData(employee);
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
                dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));

                Scene scene = new Scene(parent);
                dialogStage.setScene(scene);
                dialogStage.show();
            }catch(Exception e){

            }*/


            /*services.start();

            Stage stage = CreateSplashScreen.createEmployeeSplashScreen(services);


            services.setOnRunning(event -> {
                System.out.println("Corriendo");
                stage.show();
            });
            services.setOnSucceeded(event -> {
                System.out.println("TERMINADO");
                stage.close();
            });
            services.setOnFailed(event -> System.out.println("Error"));*/


        });

    }



}
