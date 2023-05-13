package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.stage.Stage;
import model.Empleado;
import utils.FormatEmployeeName;
import utils.SMBUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import utils.VacationsController;

public class EmployeeBoardController implements Initializable {

    private final String PIC_DIR = "config_files" + File.separator + "Employees";

    @FXML
    private ImageView profilePic;

    @FXML
    private ImageView enterprisePic;

    @FXML
    private JFXButton vacationsBtn;

    @FXML
    private JFXButton scheduleBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Empleado employee = LoginController.getEmpleado();
        try {

            Image image = downloadEmployeePhoto(employee, PIC_DIR);
            InputStream inputStream;
            if (employee.getNombre_empresa().contains("Palobiofarma")) {
                inputStream =  getClass().getResourceAsStream("/resources/images/palo.png");
            } else {
                inputStream = getClass().getResourceAsStream("/resources/images/medi.png");
            }
            enterprisePic.setImage(new Image(inputStream));
            profilePic.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        vacationsBtn.setOnAction(action -> {
            try {
                ArrayList<Empleado> employees = new ArrayList<Empleado>();
                employees.add(employee);
                VacationsController.getVacationsDaysEmployees(employees);
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

    static Image downloadEmployeePhoto(Empleado employee, String pic_dir) throws FileNotFoundException {
        String employeeFullName = new StringBuilder().append(employee.getNombre())
                .append(" ")
                .append(employee.getPrimer_apellido())
                .toString();
        SMBUtils.downloadSmbPhoto(employeeFullName+".png", pic_dir);
        File file = new File(pic_dir +File.separator+employeeFullName+".png");
        if(!file.exists()){
            file = new File(pic_dir +File.separator+"profile.png");
        }
       return new Image(new FileInputStream(file));
    }

}
