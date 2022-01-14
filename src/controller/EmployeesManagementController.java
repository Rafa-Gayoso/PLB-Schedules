package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import model.Empleado;
import services.ServicesLocator;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeesManagementController implements Initializable {
    private final String EMPTY_FIELD_MESSAGE = "Debe rellenar todos los campos vacíos";
    private final String EMPTY_FIELD_TITLE = "Campos vacíos";

    private TrayNotification notification;

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
    private TableView<Empleado> employesTable;

    @FXML
    private TableColumn<Empleado, Integer> codCol;

    @FXML
    private TableColumn<Empleado, String> nombreCol;

    @FXML
    private TableColumn<Empleado, String> primerApCol;

    @FXML
    private TableColumn<Empleado, String> segundoApCol;

    @FXML
    private TableColumn<Empleado, String> nifCol;

    @FXML
    private TableColumn<Empleado, String> numCol;

    @FXML
    private TableColumn<Empleado, String> empresaCol;

    @FXML
    private TableColumn<Empleado, Integer> horasColum;

    @FXML
    private JFXTextField horasLaborables;

    @FXML
    private JFXComboBox<String> comboEmpresa;

    @FXML
    private JFXButton btnInsert;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXButton btnDelete;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        horasLaborables.setText("8");

        horasLaborables.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[4-8]*$")) ? change : null));

        codCol.setCellValueFactory(
                new PropertyValueFactory<>("cod_empleado")
        );

        nombreCol.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        primerApCol.setCellValueFactory(
                new PropertyValueFactory<>("primer_apellido")
        );
        segundoApCol.setCellValueFactory(
                new PropertyValueFactory<>("segundo_apellido")
        );
        nifCol.setCellValueFactory(
                new PropertyValueFactory<>("nif")
        );
        numCol.setCellValueFactory(
                new PropertyValueFactory<>("numero_afiliacion")
        );
        empresaCol.setCellValueFactory(
                new PropertyValueFactory<>("nombre_empresa")
        );

        horasColum.setCellValueFactory(
                new PropertyValueFactory<>("horas_laborables")
        );

        btnInsert.setDisable(false);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);


        ObservableList<String> empresas = FXCollections.observableArrayList(ServicesLocator.getEnterprise().nombreEmpresas());
        comboEmpresa.setItems(empresas);

        populateTable();

        employesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showEmployeeDetails(newValue));

        btnInsert.setOnAction(event -> insertEmployee());
        btnUpdate.setOnAction(event -> updateEmployee());
        btnDelete.setOnAction(event -> deleteEmployee());

        notification = new TrayNotification();
    }

    private void populateTable() {
        ObservableList<Empleado> employes = FXCollections.observableArrayList(ServicesLocator.getEmployee().listadoEmpleadosModelo());
        employesTable.setItems(employes);

    }

    public void showEmployeeDetails(Empleado empleado) {
        btnInsert.setDisable(true);
        btnDelete.setDisable(false);
        btnUpdate.setDisable(false);
        if (empleado != null) {
            nombreTextField.setText(empleado.getNombre());
            primApellidoTextField.setText(empleado.getPrimer_apellido());
            segApellidoTextfield.setText(empleado.getSegundo_apellido());
            nifTextfield.setText(empleado.getNif());
            numTextfield.setText(empleado.getNumero_afiliacion());
            comboEmpresa.getSelectionModel().select(empleado.getNombre_empresa());
            horasLaborables.setText(String.valueOf(empleado.getHoras_laborables()));
        } else {
            btnInsert.setDisable(false);
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
        }
    }

    private void deleteEmployee() {
        Empleado empleado = employesTable.getSelectionModel().getSelectedItem();
        ServicesLocator.getEmployee().deleteEmployee(empleado);
        resetValues();

        String DELETED_MESSAGE = "Empleando eliminado del sistema con éxito";
        String DELETED_TITLE = "Empleado eliminado";
        setNotificationData(DELETED_MESSAGE, DELETED_TITLE,NotificationType.SUCCESS);
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);
    }

    private void insertEmployee() {
        boolean validated = validateData();
        if (!validated) {
            setNotificationData(EMPTY_FIELD_MESSAGE, EMPTY_FIELD_TITLE,NotificationType.ERROR);
        } else {
            Empleado empleado = new Empleado();
            setEmployeeData(empleado);
            ServicesLocator.getEmployee().insertEmployee(empleado);
            resetValues();
            String INSERTED_MESSAGE = "Empleando insertado al sistema con éxito";
            String INSERTED_TITLE = "Empleado insertado";
            setNotificationData(INSERTED_MESSAGE, INSERTED_TITLE,NotificationType.SUCCESS);
        }
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);
    }

    private void updateEmployee() {
        boolean validated = validateData();
        if (!validated) {
            setNotificationData(EMPTY_FIELD_MESSAGE, EMPTY_FIELD_TITLE,NotificationType.ERROR );
        } else {
            Empleado empleado = employesTable.getSelectionModel().getSelectedItem();
            setEmployeeData(empleado);
            ServicesLocator.getEmployee().updateEmployee(empleado);
            resetValues();

            String UPDATED_MESSAGE = "Cambios realizados con éxito";
            String UPDATED_TITLE = "Información de empleado editada";
            setNotificationData(UPDATED_MESSAGE, UPDATED_TITLE,NotificationType.SUCCESS);
            notification.setNotificationType(NotificationType.SUCCESS);
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

    private void resetValues() {
        nombreTextField.setText("");
        primApellidoTextField.setText("");
        segApellidoTextfield.setText("");
        nifTextfield.setText("");
        numTextfield.setText("");
        comboEmpresa.getSelectionModel().select(-1);
        horasLaborables.setText("8");
        populateTable();
    }

    @FXML
    void resetAllValues(KeyEvent event) {
        if (event.getCode().getName().equalsIgnoreCase("Esc")) {
            btnInsert.setDisable(false);
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
            nombreTextField.setText("");
            primApellidoTextField.setText("");
            segApellidoTextfield.setText("");
            nifTextfield.setText("");
            numTextfield.setText("");
            comboEmpresa.getSelectionModel().select(-1);
            horasLaborables.setText("8");
        }
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

    private void setNotificationData(String message,String title, NotificationType type ){
        notification.setMessage(message);
        notification.setTitle(title);
        notification.setNotificationType(type);
    }

}
