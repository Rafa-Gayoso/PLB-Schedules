package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import model.Empleado;

import java.time.LocalDateTime;


public class EmployeeSchedulePaneController{


    @FXML
    private TabPane tabPane;


    private int vacations;


    public void setData(Empleado employee) {
        vacations = employee.getVacations();

        try {
            LocalDateTime dateTime = LocalDateTime.now();
            int month = dateTime.getMonth().getValue();
            //String TEST = empleado.getDireccionCronograma();
            int i = 1;
            for (Tab tab : tabPane.getTabs()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/EmployeeExcelTable.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                EmployeeExcelTableController test = fxmlLoader.getController();
                test.setData(employee, i);
                //anchorPane.getChildren().add(label);
                tab.setContent(anchorPane);
                if (i > month) tab.setDisable(true);
                i++;
            }

        } catch (Exception E) {
            E.printStackTrace();
        }

    }



}