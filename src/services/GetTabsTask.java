package services;

import controller.EmployeeSchedulePaneController;
import controller.LoginController;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import model.Empleado;
import utils.FormatEmployeeName;
import utils.SMBUtils;

import java.util.ArrayList;

public class GetTabsTask extends Task<ArrayList<Tab>> {

    @Override
    protected ArrayList<Tab> call() throws Exception {
        ArrayList<Tab> tabs = new ArrayList<>();
        int progress =0;
        int totalProgress = LoginController.getEmployees().size();
        updateProgress(progress, totalProgress);
        updateMessage("Cargando datos de trabajadores ("+(progress+1)+"/"+totalProgress+")");


        try {
            for(Empleado employee : LoginController.getEmployees()){
                String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
                SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
                Tab tab = new Tab(employee.getNombre());
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/EmployeeSchedulePane.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                EmployeeSchedulePaneController test  = fxmlLoader.getController();
                test.setData(employee);
                tab.setContent(anchorPane);
                tabs.add(tab);
                progress++;
                updateProgress(progress, totalProgress);
            }
        }catch (Exception E){
            E.printStackTrace();
        }

        updateProgress(progress,totalProgress);
        updateMessage("Cargando datos de trabajadores ("+(progress+1)+"/"+totalProgress+")");
        return tabs;
    }

}