package controller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import dao.implementation.UserDaoImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.GetEmpService;
import model.Empleado;
import model.Usuario;
import utils.AESCypher;
import utils.FormatEmployeeName;
import utils.Roles;
import utils.SMBUtils;

public class LoginController implements Initializable {

    private final String DOWNLOAD_URL = "config_files" + File.separator + "Horarios" + File.separator;

    @FXML
    private ImageView clock;

    @FXML
    private ImageView medi;

    @FXML
    private ImageView palo;

    @FXML
    private JFXProgressBar progress;

    private static Usuario usu;
    private static Empleado empleado;
    private static ArrayList<Empleado> employees;
    private UserDaoImpl userDao;

    @FXML
    AnchorPane loginPane;

    @FXML
    AnchorPane splashPane;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXButton loginBtn;

    @FXML
    private JFXButton close;
    private double x,y;


    @FXML
    private Label lbl;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDao = new UserDaoImpl();
        lbl.setVisible(false);
        GetEmpService task = new GetEmpService();

        task.start();
        task.setOnRunning(event -> {
                    new RollIn(clock).setSpeed(0.8).play();
                    new RollIn(palo).setSpeed(0.8).play();
                    new RollIn(medi).setSpeed(0.8).play();
                    new FadeIn(lbl).setSpeed(0.5).play();
                    lbl.setVisible(true);
                }
        );
        progress.progressProperty().bind(task.progressProperty());
        lbl.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(event -> {
            splashPane.setVisible(false);
            employees = task.getValue();
            new FadeIn(loginPane).setSpeed(1).play();
        });


        loginBtn.setOnAction(event -> login());
    }


    public void close(){
        System.exit(0);
    }

    @FXML
    public void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    public void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() -x);
        stage.setY(event.getScreenY() -y);
    }


    private void login(){
        try{
            if(username.getText().trim().isEmpty() || pass.getText().trim().isEmpty()){
                System.err.println("VACÍO");
            }else{
                AESCypher aesCypher = new AESCypher();

                String encryptedPassword = aesCypher.encrypt(pass.getText());
                usu = userDao.getUsuarioByUsernameAndPassword(username.getText(), encryptedPassword);
                if(!Objects.isNull(usu)){


                    FXMLLoader loader = new FXMLLoader();
                    AnchorPane pane;

                    empleado = employees.stream().parallel()
                            .filter(e -> e.getCodUsuario() == usu.getUsarioId()).findAny().get();
                    if(usu.getRol() == Roles.ADMIN.getCode()){
                        pane = setLocation("/resources/fxml/MainMenu.fxml", loader);
                    }else {
                        pane = setLocation("/resources/fxml/EmployeeBoard.fxml", loader);
                        /*EmployeeSchedulePaneController test  = loader.getController();
                        String employeeFileName = FormatEmployeeName.getEmployeesFileName(empleado);
                        SMBUtils.downloadSmbFile(empleado.getNombre_empresa(),employeeFileName, empleado.getDireccionCronograma());
                        test.setData(empleado);*/

                    }

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
                    dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));

                    Scene scene = new Scene(pane);
                    dialogStage.setScene(scene);
                    dialogStage.setResizable(false);
                    dialogStage.show();

                    Stage stage  = (Stage) loginBtn.getScene().getWindow();
                    stage.close();
                }else{
                    System.err.println("USUARIO O CONTRASEña INCORRECTOS");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static Usuario getUsu() {
        return usu;
    }

    public static Empleado getEmpleado() {
        return empleado;
    }

    public static ArrayList<Empleado> getEmployees() {
        return employees;
    }

    private AnchorPane setLocation(String location, FXMLLoader loader) throws IOException {
        loader.setLocation(getClass().getResource(location));

        return loader.load();
    }

    @FXML
    void initSession(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER){
            login();
        }
    }


}

