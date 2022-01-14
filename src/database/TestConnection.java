package database;

import eu.mihosoft.scaledfx.ScalableContentPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestConnection extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ScalableContentPane scale = new ScalableContentPane();
        Parent root = FXMLLoader.load(getClass().getResource("../main/EmployesManagement.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("../view/SeleccionEmpresa.fxml"));
        scale.setContent(root);
        root.requestFocus();
        primaryStage.setTitle("Hello World");
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(scale));
        primaryStage.show();
    }
}
