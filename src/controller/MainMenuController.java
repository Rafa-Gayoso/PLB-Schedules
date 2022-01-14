package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXProgressBar;
import eu.mihosoft.scaledfx.ScalableContentPane;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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

    @FXML
    private JFXButton aboutBtn;

    private File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        //File button
        fileMenu.requestFocus();
        AnchorPane popupPane = new AnchorPane();
        VBox vBox = new VBox();
        JFXListView<JFXButton> list = new JFXListView<JFXButton>();

        JFXButton btnLoadCalendar = new JFXButton("Cargar Calendario");
        ImageView view = new ImageView(new Image("/resources/images/calendario.png"));
        view.setFitWidth(25);
        view.setFitHeight(25);
        btnLoadCalendar.setGraphic(view);
        btnLoadCalendar.setCursor(Cursor.HAND);
        JFXButton btnSchedule = new JFXButton("Generar Horarios");
        ImageView view1 = new ImageView(new Image(getClass().getResourceAsStream("/resources/images/excel.png")));
        view1.setFitWidth(25);
        view1.setFitHeight(25);
        btnSchedule.setGraphic(view1);
        btnSchedule.setCursor(Cursor.HAND);
        JFXButton close = new JFXButton("Cerrar");
        ImageView view2 = new ImageView(new Image(getClass().getResourceAsStream("/resources/images/logout.png")));
        view2.setFitWidth(25);
        view2.setFitHeight(25);
        close.setGraphic(view2);
        close.setCursor(Cursor.HAND);


        vBox.getChildren().add(btnLoadCalendar);
        vBox.getChildren().add(btnSchedule);
        vBox.getChildren().add(close);
        popupPane.getChildren().add(vBox);
        JFXPopup popup = new JFXPopup(popupPane);

        JFXPopup popupEnterprise = new JFXPopup(enterprisePopUp());
        //file button
        fileMenu.setOnAction(event -> {
            popup.show(fileMenu, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, fileMenu.getLayoutX(), fileMenu.getLayoutY() + 50);
        });
        btnLoadCalendar.setOnAction(event -> {
            try {
                loadSchedule(event);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            popup.hide();
        });
        btnSchedule.setOnAction(event -> {
            popupEnterprise.show(btnSchedule,JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, btnSchedule.getLayoutX()+140, btnSchedule.getLayoutY()-30);
            //mergeExcel(event);
            //popup.hide();

        });
        close.setOnAction(event -> {
            closeApplication(event);
            popup.hide();
        });
        notification = new TrayNotification();
        listFiles = new ArrayList<>();
        getSlides();
        createSlideShow();
        //createSlideShow();
        resultLabel.setText(" ");
        progressBar.setProgress(0);




    }

    private AnchorPane enterprisePopUp(){
        AnchorPane popupPane = new AnchorPane();
        VBox vBox = new VBox();
        JFXListView<JFXButton> list = new JFXListView<JFXButton>();

        JFXButton btnPaloMataro = new JFXButton("Palobiofarma Mataró S.L");
        ImageView view = new ImageView(new Image("/resources/images/palobiofarma.png"));
        view.setFitWidth(25);
        view.setFitHeight(25);
        btnPaloMataro.setGraphic(view);
        btnPaloMataro.setCursor(Cursor.HAND);
        JFXButton btnPaloPamplona  = new JFXButton("Palobiofarma Pamplona S.L");
        ImageView view1 = new ImageView(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));
        view1.setFitWidth(25);
        view1.setFitHeight(25);
        btnPaloPamplona.setGraphic(view1);
        btnPaloPamplona.setCursor(Cursor.HAND);
        JFXButton btnMedi = new JFXButton("Medibiofarma");
        ImageView view2 = new ImageView(new Image(getClass().getResourceAsStream("/resources/images/medibiofarma.png")));
        view2.setFitWidth(25);
        view2.setFitHeight(25);
        btnMedi.setGraphic(view2);
        btnMedi.setCursor(Cursor.HAND);


        vBox.getChildren().add(btnPaloMataro);
        vBox.getChildren().add(btnPaloPamplona);
        vBox.getChildren().add(btnMedi);

        btnPaloMataro.setOnAction(event -> {
            DirectoryChooser fc = new DirectoryChooser();

            //Set extension filter for text files
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");



            //dc = new DirectoryChooser();
            File f = fc.showDialog(new Stage());
            mergeExcel(event,1, f.getAbsolutePath());
        });

        btnPaloPamplona.setOnAction(event -> {
            DirectoryChooser fc = new DirectoryChooser();

            //Set extension filter for text files
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");



            //dc = new DirectoryChooser();
            File f = fc.showDialog(new Stage());
            mergeExcel(event,2, f.getAbsolutePath());
        });

        btnMedi.setOnAction(event -> {
            DirectoryChooser fc = new DirectoryChooser();

            //Set extension filter for text files
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");



            //dc = new DirectoryChooser();
            File f = fc.showDialog(new Stage());
            mergeExcel(event,3, f.getAbsolutePath());
        });
        popupPane.getChildren().add(vBox);

        return popupPane;
    }


    private void createSlideShow() {

        /*images = new ArrayList<>();
        images.add(new Image("/resources/medibiofarma.png"));
        images.add(new Image("/resources/palobiofarma.jpg"));


        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),event -> {
            SequentialTransition sequentialTransition = new SequentialTransition();

            //slideShow.setImage(null);
            slideShow.setImage(images.get(count));
            FadeTransition ft = getFadeTransition(slideShow,0.0,1.0,2000);
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
            FadeTransition fadeOut = getFadeTransition(slideShow, 1.0, 0.0, 2000);
            sequentialTransition.getChildren().addAll(ft, pauseTransition, fadeOut);
            count++;
            if(count == 2){
                count = 0;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();*/
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
        //root.getChildren().clear();
        //slideshow.setAutoReverse(true);
        slideshow.play();


    }


    public FadeTransition getFadeTransition(ImageView imageView, double fromValue, double toValue, int durationInMilliseconds) {

        FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), imageView);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);

        return ft;

    }

    @FXML
    void loadSchedule(ActionEvent event) throws FileNotFoundException {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
        file = fc.showOpenDialog(stage);

        if (file == null) {


            notification.setMessage("Falló la importación del calendario");
            notification.setTitle("Importación de calendario");
            notification.setNotificationType(NotificationType.ERROR);
            notification.showAndDismiss(Duration.millis(5000));
            notification.setAnimationType(AnimationType.POPUP);
        }
        else{
            listFiles.add(file);


            notification.setMessage("Se ha importado el calendario");
            notification.setTitle("Importacion de calendario");
            notification.setNotificationType(NotificationType.SUCCESS);
            notification.showAndDismiss(Duration.millis(5000));
            notification.setAnimationType(AnimationType.POPUP);
        }


    }

    public void mergeExcel(ActionEvent actionEvent, int cod_empresa, String ruta) {

        try {

            Empresa empresa = ServicesLocator.getEmpresa().getEmpresaByCod(cod_empresa);
            ArrayList<Empleado> lista = ServicesLocator.getEmpleado().listadoEmpleadosXEmpresa(empresa.getNombre());
            System.out.println(lista.size());

            String [] nombre = this.file.getName().split(" ");
            int year = Integer.parseInt(nombre[0]);
            File file = null;
            if(year%4 !=0 ){
                file = new File(REGULAR);
            }else{
                file = new File(LEAP_YEAR);
            }

            listFiles.add(file);
            if (listFiles.size() < 2) {
                System.out.println("ERROR");
            }

            File foto1 = new File(PALOBIOFARMA);
            listFiles.add(foto1);

            File foto2 = new File(MEDIBIOFARMA);
            listFiles.add(foto2);

            if (file == null) {
                notification.setMessage("Debe importar el calendario a analizar");
                notification.setTitle("Importacion de calendario");
                notification.setNotificationType(NotificationType.ERROR);
                notification.showAndDismiss(Duration.millis(5000));
                notification.setAnimationType(AnimationType.POPUP);
            } else {

                Task<Void> longTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Controller controller = new Controller();
                        controller.mergeExcelFiles(lista, empresa, listFiles, ruta);
                        return null;
                    }
                };

                longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        progressBar.setProgress(100);
                        resultLabel.setText("   DONE!!!");
                        notification.setMessage("Modelos de horarios creados");
                        notification.setTitle("Control de horario");
                        notification.setNotificationType(NotificationType.SUCCESS);
                        notification.showAndDismiss(Duration.millis(5000));
                        notification.setAnimationType(AnimationType.POPUP);

                    }
                });

                longTask.setOnRunning(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        resultLabel.setText("Working on it");
                        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS); //el progressbar este esta al berro
                    }
                });

                new Thread(longTask).start();
            }

            //listFiles.remove(listFiles.size()-1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void showCalendarMenu(ActionEvent event) {

    }



    @FXML
    void closeApplication(ActionEvent event) {
        System.exit(0);
    }

    private ImageView[] getSlides() {
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
        //Image image3 = new Image(SlideShowTest.class.getResource("pic3").toExternalForm());
        //Image image4 = new Image(SlideShowTest.class.getResource("pic4").toExternalForm());


        return slides;
    }

    @FXML
    void showEmployesData(ActionEvent event) {
        try {
            System.out.println("Panel de edicion de empleados" + "\n" + "-------------------------");
            ScalableContentPane scale = new ScalableContentPane();
            FXMLLoader loader = new FXMLLoader();
            Parent root = FXMLLoader.load(getClass().getResource("/view/EmployesManagement.fxml"));
            loader.setLocation(MainMenuController.class.getResource("/view/EmployesManagement.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            scale.setContent(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Gestionar Empleados");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));
            dialogStage.setAlwaysOnTop(true);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //setController
            EmployesManagementController controller = loader.getController();
            controller.setMainMenuController(this);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showHelp(ActionEvent event) throws IOException {
        File file = new File(HELP);

        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        //let's try to open PDF file
        if(file.exists()){
            desktop.open(file);
        }
    }

    public AnchorPane getRoot() {
        return root;
    }

    /*public ArrayList<FileInputStream> getFiles() {
        return this.listFiles;
    }*/
}
