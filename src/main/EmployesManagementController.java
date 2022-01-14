package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployesManagementController implements Initializable {

    private MainMenuController mainMenuController;
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


    private ObservableList<Empleado> employes;
    private ObservableList<String> empresas;

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
                new PropertyValueFactory<Empleado, Integer>("cod_empleado")
        );

        nombreCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("nombre")
        );

        primerApCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("primer_apellido")
        );
        segundoApCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("segundo_apellido")
        );
        nifCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("nif")
        );
        numCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("numero_afiliacion")
        );
        empresaCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("nombre_empresa")
        );

        horasColum.setCellValueFactory(
                new PropertyValueFactory<Empleado, Integer>("horas_laborables")
        );


        btnInsert.setDisable(false);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);


        empresas = FXCollections.observableArrayList(ServicesLocator.getEmpresa().nombreEmpresas());
        comboEmpresa.setItems(empresas);

        populateTable();

        employesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showEmployeeDetails(newValue));

        btnInsert.setOnAction(event -> insertEmployee());
        btnUpdate.setOnAction(event -> updateEmployee());
        btnDelete.setOnAction(event -> deleteEmployee());

        notification = new TrayNotification();
    }


    private void populateTable() {
        employes = FXCollections.observableArrayList(ServicesLocator.getEmpleado().listadoEmpleadosModelo());
        employesTable.setItems(employes);

    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
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
            //ObservableList<String> empresa = FXCollections.observableArrayList(empleado.getCod_empresa());
            comboEmpresa.getSelectionModel().select(empleado.getNombre_empresa());
            horasLaborables.setText(String.valueOf(empleado.getHoras_laborables()));
        } else {
            btnInsert.setDisable(false);
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
        }
    }


    void deleteEmployee() {
        Empleado empleado = employesTable.getSelectionModel().getSelectedItem();
        ServicesLocator.getEmpleado().deleteEmpleado(empleado);
        resetValues();
        populateTable();

        notification.setMessage("Empleando eliminado del sistema con éxito");
        notification.setTitle("Empleado eliminado");
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);
    }


    void insertEmployee() {

        if (nombreTextField.getText().equalsIgnoreCase("") || nombreTextField.getText().trim().equalsIgnoreCase("")
                || primApellidoTextField.getText().equalsIgnoreCase("") || primApellidoTextField.getText().trim().equalsIgnoreCase("")
                || nifTextfield.getText().equalsIgnoreCase("") || nifTextfield.getText().trim().equalsIgnoreCase("")
                || numTextfield.getText().equalsIgnoreCase("") || numTextfield.getText().trim().equalsIgnoreCase("")
                || horasLaborables.getText().equalsIgnoreCase("") || horasLaborables.getText().trim().equalsIgnoreCase("")
                || comboEmpresa.getSelectionModel().getSelectedIndex() == -1) {

            notification.setMessage("Debe rellenar todos los campos");
            notification.setTitle("Campos vacíos");
            notification.setNotificationType(NotificationType.ERROR);
        } else {
            Empleado empleado = new Empleado();
            empleado.setNombre(nombreTextField.getText());
            empleado.setPrimer_apellido(primApellidoTextField.getText());
            empleado.setSegundo_apellido(segApellidoTextfield.getText());
            empleado.setNif(nifTextfield.getText());
            empleado.setNumero_afiliacion(numTextfield.getText());
            int cod_empresa = ServicesLocator.getEmpresa().getEmpresaCodByName(comboEmpresa.getSelectionModel().getSelectedItem());
            empleado.setCod_empresa(cod_empresa);
            empleado.setHoras_laborables(Integer.parseInt(horasLaborables.getText()));
            ServicesLocator.getEmpleado().insertarEmpleado(empleado);
            resetValues();
            populateTable();

            notification.setMessage("Empleando insertado al sistema con éxito");
            notification.setTitle("Empleado insertado");
            notification.setNotificationType(NotificationType.SUCCESS);
        }
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);

    }


    void updateEmployee() {

        if (nombreTextField.getText().equalsIgnoreCase("") || nombreTextField.getText().trim().equalsIgnoreCase("")
                || primApellidoTextField.getText().equalsIgnoreCase("") || primApellidoTextField.getText().trim().equalsIgnoreCase("")
                || nifTextfield.getText().equalsIgnoreCase("") || nifTextfield.getText().trim().equalsIgnoreCase("")
                || numTextfield.getText().equalsIgnoreCase("") || numTextfield.getText().trim().equalsIgnoreCase("")
                || horasLaborables.getText().equalsIgnoreCase("") || horasLaborables.getText().trim().equalsIgnoreCase("")
                || comboEmpresa.getSelectionModel().getSelectedIndex() == -1) {

            notification.setMessage("Debe rellenar todos los campos");
            notification.setTitle("Campos vacíos");
            notification.setNotificationType(NotificationType.ERROR);


        } else {
            Empleado empleado = employesTable.getSelectionModel().getSelectedItem();
            empleado.setNombre(nombreTextField.getText());
            empleado.setPrimer_apellido(primApellidoTextField.getText());
            empleado.setSegundo_apellido(segApellidoTextfield.getText());
            empleado.setNif(nifTextfield.getText());
            empleado.setNumero_afiliacion(numTextfield.getText());
            int cod_empresa = ServicesLocator.getEmpresa().getEmpresaCodByName(comboEmpresa.getSelectionModel().getSelectedItem());
            empleado.setCod_empresa(cod_empresa);
            empleado.setHoras_laborables(Integer.parseInt(horasLaborables.getText()));
            ServicesLocator.getEmpleado().updateEmpleado(empleado);
            resetValues();
            populateTable();

            notification.setMessage("Cambios realizados con éxito");
            notification.setTitle("Información de empleado editada");
            notification.setNotificationType(NotificationType.SUCCESS);
        }
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);

    }


    private void resetValues() {
        nombreTextField.setText("");
        primApellidoTextField.setText("");
        segApellidoTextfield.setText("");
        nifTextfield.setText("");
        numTextfield.setText("");
        comboEmpresa.getSelectionModel().select(-1);
        horasLaborables.setText("8");
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
}
