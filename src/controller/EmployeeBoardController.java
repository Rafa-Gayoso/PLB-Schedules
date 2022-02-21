package controller;

import dao.implementation.EmpleadoDaoImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Empleado;
import utils.MyListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class EmployeeBoardController implements Initializable {
    @FXML
    private VBox chosenFruitCard;

    @FXML
    private Label fruitNameLable;

    @FXML
    private ImageView fruitImg;

    @FXML
    private ScrollPane scroll;

    @FXML
    private GridPane grid;

    private List<Empleado> employees = new ArrayList<>();
    private Image image;
    private MyListener myListener;



    private void setChosenFruit(Empleado empleado) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employees.addAll(new EmpleadoDaoImpl().getEntities());
        if (employees.size() > 0) {
            setChosenFruit(employees.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Empleado empleado) {
                    setChosenFruit(empleado);
                }
            };
        }
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < employees.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/Employee.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                EmployeeController itemController = fxmlLoader.getController();
                itemController.setData(employees.get(i),myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)
                //set grid width
                /*grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);*/

                GridPane.setMargin(anchorPane, new Insets(30));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
