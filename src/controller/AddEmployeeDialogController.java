package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Empleado;
import services.ServicesLocator;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import java.net.URL;
import java.util.ResourceBundle;

public class AddEmployeeDialogController implements Initializable {
    private final String EMPTY_FIELD_MESSAGE = "Debe rellenar todos los campos vacíos";
    private final String EMPTY_FIELD_TITLE = "Campos vacíos";
    private TrayNotification notification;

    private ObservableList<Empleado> appMainObservableList;

    @FXML
    private JFXTextField nombreTextField;

    @FXML
    private JFXTextField primApellidoTextField;

    @FXML
    private JFXTextField segApellidoTextfield;

    @FXML
    private JFXTextField nifTextfield;

    @FXML
    private JFXTextField numTextfield;

    @FXML
    private JFXButton btnInsert;

    @FXML
    private JFXTextField horasLaborables;

    @FXML
    private JFXComboBox<String> comboEmpresa;


    @FXML
    private JFXButton btnClose;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> empresas = FXCollections.observableArrayList(ServicesLocator.getEnterprise().nombreEmpresas());
        comboEmpresa.setItems(empresas);

        horasLaborables.setText("8");
        horasLaborables.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[4-8]*$")) ? change : null));

        notification = new TrayNotification();

        btnInsert.setOnAction(this::insertEmployee);

    }

    private void insertEmployee(javafx.event.ActionEvent event) {
        boolean validated = validateData();
        if (!validated) {
            setNotificationData(EMPTY_FIELD_MESSAGE, EMPTY_FIELD_TITLE, NotificationType.ERROR);
        } else {
            Empleado empleado = new Empleado();
            setEmployeeData(empleado);
            ServicesLocator.getEmployee().insertEmployee(empleado);
            resetValues();
            String INSERTED_MESSAGE = "Empleando insertado al sistema con éxito";
            String INSERTED_TITLE = "Empleado insertado";
            setNotificationData(INSERTED_MESSAGE, INSERTED_TITLE,NotificationType.SUCCESS);
            ObservableList<Empleado> employees = FXCollections.observableArrayList(ServicesLocator.getEmployee().listadoEmpleadosModelo());
            appMainObservableList.setAll(employees);
            closeStage(event);
        }
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);
    }

    private void setEmployeeData(Empleado empleado) {
        empleado.setNombre(nombreTextField.getText());
        empleado.setPrimer_apellido(primApellidoTextField.getText());
        empleado.setSegundo_apellido(segApellidoTextfield.getText());
        empleado.setNif(nifTextfield.getText());
        empleado.setNumero_afiliacion(numTextfield.getText());
        int cod_empresa = ServicesLocator.getEnterprise().getEmpresaCodByName(comboEmpresa.getSelectionModel().getSelectedItem());
        empleado.setCod_empresa(cod_empresa);
        empleado.setHoras_laborables(Integer.parseInt(horasLaborables.getText()));
    }

    private boolean validateData() {
        boolean validated = true;
        if (nombreTextField.getText().equalsIgnoreCase("") || nombreTextField.getText().trim().equalsIgnoreCase("")
                || primApellidoTextField.getText().equalsIgnoreCase("") || primApellidoTextField.getText().trim().equalsIgnoreCase("")
                || nifTextfield.getText().equalsIgnoreCase("") || nifTextfield.getText().trim().equalsIgnoreCase("")
                || numTextfield.getText().equalsIgnoreCase("") || numTextfield.getText().trim().equalsIgnoreCase("")
                || horasLaborables.getText().equalsIgnoreCase("") || horasLaborables.getText().trim().equalsIgnoreCase("")
                || comboEmpresa.getSelectionModel().getSelectedIndex() == -1) {
            validated = false;
        }
        return validated;
    }

    private void resetValues() {
        nombreTextField.setText("");
        primApellidoTextField.setText("");
        segApellidoTextfield.setText("");
        nifTextfield.setText("");
        numTextfield.setText("");
        comboEmpresa.getSelectionModel().select(-1);
        horasLaborables.setText("8");
        //populateTable();
    }

    private void setNotificationData(String message,String title, NotificationType type ){
        notification.setMessage(message);
        notification.setTitle(title);
        notification.setNotificationType(type);
    }

    public void setAppMainObservableList(ObservableList<Empleado> employeeObservableList) {
        this.appMainObservableList = employeeObservableList;

    }

    private void closeStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void closeModal(KeyEvent event) {
        if(event.getCode() == KeyCode.ESCAPE){
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        }

    }
}
