package controller;

import animatefx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import model.Empleado;
import utils.SMBUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class EmployeeController {

    private final String PIC_DIR = "config_files" + File.separator + "Employees";

    @FXML
    private Label nameLabel2;

    @FXML
    private AnchorPane employeePane;

    @FXML
    private ImageView img;


    private Empleado empleado;


    public void setData(Empleado empleado) {
        try{
            this.empleado = empleado;
            SMBUtils.downloadSmbPhoto(empleado.getNombre()+".png", PIC_DIR);
            File file = new File(PIC_DIR+File.separator+empleado.getNombre()+".png");
            if(!file.exists()){
                file = new File(PIC_DIR+File.separator+"profile.png");
            }
            Image image = new Image(new FileInputStream(file));
            img.setImage(image);
            Circle clip = new Circle(200);
            img.setClip(clip);

            nameLabel2.setText(empleado.getNombre());

            /*img.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
                if (newValue) {
                    new Hinge(employeePane).setSpeed(0.5).play();
                }
            });*/
        }catch(Exception e){

        }

    }

    public Empleado getEmpleado() {
        return empleado;
    }

    @FXML
    void hoverEntered(MouseEvent event) {
        //new ZoomIn(employeePane).setSpeed(0.8).play();
    }

    @FXML
    void hoverExited(MouseEvent event) {
       // new ZoomOut((Node) event.getSource()).setSpeed(0.8).play();
    }
}
