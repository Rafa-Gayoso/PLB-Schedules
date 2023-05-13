package services;

import controller.EmployeeSchedulePaneController;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Empleado;
import utils.FormatEmployeeName;
import utils.SMBUtils;

import java.io.File;
import java.util.ArrayList;

public class GetEmployeeScheduleTask extends Task<Stage> {

    private final String PIC_DIR = "config_files" + File.separator + "Employees";

    private Empleado employee;

    public GetEmployeeScheduleTask(Empleado employee){
        this.employee = employee;

    }
    @Override
    protected Stage call() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/EmployeeSchedulePane.fxml"));
        Parent parent = fxmlLoader.load();
        EmployeeSchedulePaneController test  = fxmlLoader.getController();
        String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
        SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
        String employeeFullName = new StringBuilder().append(employee.getNombre())
                .append(" ")
                .append(employee.getPrimer_apellido())
                .toString();
        SMBUtils.downloadSmbPhoto(employeeFullName+".png",PIC_DIR);
        test.setData(employee);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
        dialogStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));

        Scene scene = new Scene(parent);
        dialogStage.setScene(scene);
        return dialogStage;
    }
}
