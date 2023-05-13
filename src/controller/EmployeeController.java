package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import model.Empleado;
import utils.SMBUtils;

import java.io.File;
import java.io.FileInputStream;

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
            String employeeFullName = new StringBuilder().append(empleado.getNombre())
                    .append(" ")
                    .append(empleado.getPrimer_apellido())
                    .toString();
            SMBUtils.downloadSmbPhoto(employeeFullName+".png", PIC_DIR);
            File file = new File(PIC_DIR+File.separator+employeeFullName+".png");
            if(!file.exists()){
                file = new File(PIC_DIR+File.separator+"profile.png");
            }
            Image image = new Image(new FileInputStream(file));
            img.setImage(image);
            Circle clip = new Circle(200);
            img.setClip(clip);

            nameLabel2.setText(empleado.getNombre());
        }catch(Exception e){

        }

    }
    public Empleado getEmpleado() {
        return empleado;
    }

}
