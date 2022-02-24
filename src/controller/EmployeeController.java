package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ImageView img;


    private Empleado empleado;


    public void setData(Empleado empleado) {
        try{
            this.empleado = empleado;
            SMBUtils.downloadSmbPhoto(empleado.getNombre()+".png",PIC_DIR);
            File file = new File(PIC_DIR+File.separator+empleado.getNombre()+".png");
            if(!file.exists()){
                file = new File(PIC_DIR+File.separator+"profile.png");
            }
            Image image = new Image(new FileInputStream(file));

            img.setImage(image);
            nameLabel2.setText(empleado.getNombre());
        }catch(Exception e){

        }

    }

    public Empleado getEmpleado() {
        return empleado;
    }
}
