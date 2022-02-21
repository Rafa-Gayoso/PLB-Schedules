package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.Empleado;

public class EmployeeController {

    @FXML
    private Label nameLabel2;

    @FXML
    private ImageView img;


    private Empleado empleado;


    public void setData(Empleado empleado) {
        this.empleado = empleado;
        nameLabel2.setText(empleado.getNombre());
    }

    public Empleado getEmpleado() {
        return empleado;
    }
}
