package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Empleado;

public class EmployeeController {

    @FXML
    private Label name;


    public void setData(Empleado empleado){
        name = new Label(empleado.getNombre());
    }
}
