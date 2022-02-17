package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import model.Empleado;
import services.GetTabsTaskService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeesScheduleManagerController implements Initializable {

    @FXML
    private TabPane tabPane;

    @FXML
    private StackPane stackPane;

    private List<Empleado> employees;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadingData();
    }

    public void loadingData(){
        GetTabsTaskService service = new GetTabsTaskService();
        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        veil.setPrefSize(400, 440);
        ProgressIndicator p = new ProgressIndicator();
        p.setMaxSize(140, 140);
        p.setStyle(" -fx-progress-color: orange;");

        p.progressProperty().bind(service.progressProperty());
        veil.visibleProperty().bind(service.runningProperty());
        p.visibleProperty().bind(service.runningProperty());

        service.setOnSucceeded(event -> tabPane.getTabs().addAll(service.getValue()));
        stackPane.getChildren().addAll(veil, p);
        service.start();

    }

    public void setData(){
        try {
            employees = LoginController.getEmployees();
            for(Empleado employee : employees){
                Tab tab = new Tab(employee.getNombre());
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/EmployeeSchedulePane.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                EmployeeSchedulePaneController test  = fxmlLoader.getController();
                test.setData(employee);
                tab.setContent(anchorPane);
                tabPane.getTabs().add(tab);
            }
        }catch (Exception E){
            E.printStackTrace();
        }
    }


    private List<Empleado> retrievesEmployees(){
        return LoginController.getEmployees();
    }
}
