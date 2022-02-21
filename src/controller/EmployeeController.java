package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Empleado;
import utils.MyListener;

public class EmployeeController {

    @FXML
    private Label nameLabel2;

    @FXML
    private ImageView img;

    @FXML
    private void click(MouseEvent mouseEvent) {
        myListener.onClickListener(empleado);
    }

    private Empleado empleado;
    private MyListener myListener;

    public void setData(Empleado empleado, MyListener myListener) {
        this.empleado = empleado;
        this.myListener = myListener;
        nameLabel2.setText(empleado.getNombre());

    }
}
