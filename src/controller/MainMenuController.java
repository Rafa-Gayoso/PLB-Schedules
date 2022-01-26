package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import eu.mihosoft.scaledfx.ScalableContentPane;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import javafx.util.Duration;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import main.Controller;
import model.Empleado;
import model.Empresa;
import services.ServicesLocator;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    private final String LEAP_YEAR = "config_files" + File.separator + "Schedule Model Leap Year.xlsx";
    private final String REGULAR = "config_files" + File.separator + "Schedule Model Regular Year.xlsx";
    private final String HELP = "config_files" + File.separator + "Control de horarios Palobiofarma S,L & Medibiofarma.pdf";
    private final String PALOBIOFARMA = "config_files" + File.separator + "palobiofarma.png";
    private final String MEDIBIOFARMA = "config_files" + File.separator + "medibiofarma.png";

    private TrayNotification notification;

    @FXML
    private JFXButton fileMenu;

    @FXML
    private Label resultLabel;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private AnchorPane root;

    private ArrayList<File> listFiles;

    ImageView[] slides;

    private File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //File button
        fileMenu.requestFocus();

        //file button
        fileMenu.setOnAction(event -> generateSchedule());


        notification = new TrayNotification();
        listFiles = new ArrayList<>();
        getSlides();
        createSlideShow();
        resultLabel.setText("");

        progressBar.setProgress(0);
    }

    private void createSlideShow() {

        SequentialTransition slideshow = new SequentialTransition();
        for (ImageView slide : slides) {

            SequentialTransition sequentialTransition = new SequentialTransition();

            FadeTransition fadeIn = getFadeTransition(slide, 0.0, 1.0, 2000);
            PauseTransition stayOn = new PauseTransition(Duration.millis(2000));
            FadeTransition fadeOut = getFadeTransition(slide, 1.0, 0.0, 2000);

            sequentialTransition.getChildren().addAll(fadeIn, stayOn, fadeOut);
            slide.setOpacity(0);
            this.root.getChildren().add(slide);
            slideshow.getChildren().add(sequentialTransition);

        }

        slideshow.play();
    }
    
    public FadeTransition getFadeTransition(ImageView imageView, double fromValue, double toValue, int durationInMilliseconds) {

        FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), imageView);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);
        return ft;
    }

    @FXML
    void generateSchedule(){
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
        file = fc.showOpenDialog(stage);

        if (file == null) {
            notification.setMessage("Falló la importación del calendario");
            notification.setTitle("Importación de calendario");
            notification.setNotificationType(NotificationType.ERROR);
        }
        else{
            listFiles.add(file);
            if(file.getName().contains("Mat")){
                mergeExcel(1, System.getProperty("user.home") + "/Desktop");

            }else{
                mergeExcel(2, System.getProperty("user.home") + "/Desktop");
                mergeExcel(3, System.getProperty("user.home") + "/Desktop");
            }

            notification.setMessage("Se ha importado el calendario");
            notification.setTitle("Importacion de calendario");
            notification.setNotificationType(NotificationType.SUCCESS);
        }
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);
        listFiles = new ArrayList<>();
    }

    public void mergeExcel(int cod_empresa, String ruta) {

        try {

            Empresa empresa = ServicesLocator.getEnterprise().getEmpresaByCod(cod_empresa);
            ArrayList<Empleado> lista = ServicesLocator.getEmployee().listadoEmpleadosXEmpresa(empresa.getNombre());
            System.out.println(lista.size());

            String [] nombre = this.file.getName().split(" ");
            int year = Integer.parseInt(nombre[0]);
            File file;
            if(year%4 !=0 ){
                file = new File(REGULAR);
            }else{
                file = new File(LEAP_YEAR);
            }

            listFiles.add(file);
            /*if (listFiles.size() < 2) {
                System.out.println("ERROR");
            }*/

            File foto1 = new File(PALOBIOFARMA);
            listFiles.add(foto1);

            File foto2 = new File(MEDIBIOFARMA);
            listFiles.add(foto2);


            Task<Void> longTask = new Task<Void>() {
                @Override
                protected Void call() {
                    Controller controller = new Controller();
                    controller.mergeExcelFiles(lista, empresa, listFiles, ruta);
                    return null;
                }
            };

            longTask.setOnSucceeded(event -> {
                progressBar.setProgress(100);
                notification.setMessage("Modelos de horarios creados");
                notification.setTitle("Control de horario");
                notification.setNotificationType(NotificationType.SUCCESS);
                notification.showAndDismiss(Duration.millis(5000));
                notification.setAnimationType(AnimationType.POPUP);

            });

            longTask.setOnRunning(event -> {
                resultLabel.setText("Generando horarios de los trabajadores");
                progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS); //el progressbar este esta al berro
            });

            /*longTask.setOnSucceeded(event -> {
                progressBar.setProgress(100);
                resultLabel.setText("Horarios generados satisfactoriamente");
                notification.setMessage("Modelos de horarios creados");
                notification.setTitle("Control de horario");
                notification.setNotificationType(NotificationType.SUCCESS);

            });*/

            new Thread(longTask).start();




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSlides() {
        slides = new ImageView[100];
        Image image1 = null;
        Image image2 = null;
        try{
            image1 = new Image(new FileInputStream(PALOBIOFARMA));
            image2 = new Image(new FileInputStream(MEDIBIOFARMA));
        }catch(Exception e){
            e.printStackTrace();
        }
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                slides[i] = new ImageView(image1);
            } else {
                slides[i] = new ImageView(image2);
            }

            slides[i].setFitHeight(200);
            slides[i].setFitWidth(794);
        }
    }

    @FXML
    void showEmployeesData() {
        try {
            ScalableContentPane scale = new ScalableContentPane();
            FXMLLoader loader = new FXMLLoader();
            Parent root = FXMLLoader.load(getClass().getResource("/view/EmployesManagement.fxml"));
            loader.setLocation(MainMenuController.class.getResource("/view/EmployesManagement.fxml"));
            AnchorPane page = loader.load();
            scale.setContent(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Gestionar Empleados");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setMinHeight(dialogStage.getMinHeight());
            dialogStage.setMinWidth(dialogStage.getMinWidth());
            //dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));
            //dialogStage.setAlwaysOnTop(true);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showHelp() throws IOException {
        File file = new File(HELP);

        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        //let's try to open PDF file
        if(file.exists()) {
            desktop.open(file);
        }
    }

    @FXML
    void executeKeyboardShortcuts(KeyEvent event) throws IOException{
        KeyCode code = event.getCode();
        if(code == KeyCode.A){
            showHelp();
        }
        else if(code == KeyCode.G){
            generateSchedule();
        }
        else if(code == KeyCode.E){
            showEmployeesData();
        }
    }
}
