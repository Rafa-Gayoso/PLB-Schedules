package controller;

import com.jfoenix.controls.JFXButton;
import dao.implementation.EmpleadoDaoImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Empleado;
import utils.MyListener;
import javafx.event.EventHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class EmployeeBoardController implements Initializable {


    @FXML
    private ScrollPane scroll;

    @FXML
    private JFXButton btnInsert;


    @FXML
    private GridPane grid;

    private List<Empleado> employees = new ArrayList<>();
    private Image image;
    private MyListener myListener;
    private EmpleadoDaoImpl employeeDao;





    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeDao = new EmpleadoDaoImpl();
        employees.addAll(LoginController.getEmployees());
        btnInsert.setOnAction(event -> employeeDataManagement(null,grid, myListener));
        int column = 0;
        int row = 1;
        try {
            for (Empleado employee : employees) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/Employee.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                final ContextMenu editMenu = new ContextMenu();
                MenuItem update = new MenuItem("Modificar Datos");
                MenuItem delete = new MenuItem("Eliminar empleado");
                update.setOnAction(e-> employeeDataManagement(employee, grid,myListener));
                delete.setOnAction(e-> deleteEmployee(employee));
                editMenu.getItems().add(update);
                editMenu.getItems().add(delete);
                anchorPane.setOnContextMenuRequested(
                        ae -> {
                            // Popup menu at the location of the right click.
                            editMenu.show(anchorPane, ae.getScreenX(), ae.getScreenY());
                        });

                EmployeeController itemController = fxmlLoader.getController();
                itemController.setData(employee);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)


                GridPane.setMargin(anchorPane, new Insets(20));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteEmployee(Empleado employee) {
        for(Node pane : grid.getChildren()){
            EmployeeController controller = (EmployeeController) pane.getUserData();
                if(controller.getEmpleado().equals(employee)){
                    employeeDao.deleteEntity(employee);
                    LoginController.getEmployees().remove(employee);
                    grid.getChildren().remove(pane);
                    break;
                }
        }
    }

    private void employeeDataManagement(Empleado employee, GridPane grid, MyListener myListener) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/AddEmployeeDialog.fxml"));
            Parent parent = fxmlLoader.load();

            AddEmployeeDialogController dialogController = fxmlLoader.getController();
            dialogController.setData(employee, grid);
            dialogController.setDao(employeeDao);


            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }




}
