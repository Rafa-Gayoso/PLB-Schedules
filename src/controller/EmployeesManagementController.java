package controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import model.Empleado;
import services.ServicesLocator;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeesManagementController implements Initializable {


    private TrayNotification notification;

    @FXML
    private TableView<Empleado> employeesTable;

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
    private JFXButton insertBtn;

    @FXML
    private MenuItem deleteItem;

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notification = new TrayNotification();

        employeesTable.setEditable(true);

        nombreCol.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );
        nombreCol.setCellFactory(TextFieldTableCell.forTableColumn());

        nombreCol.setOnEditCommit(event -> {
            Empleado employee = event.getRowValue();
            employee.setNombre(event.getNewValue());
            updateEmployee(employee);
        });

        primerApCol.setCellValueFactory(
                new PropertyValueFactory<>("primer_apellido")
        );
        primerApCol.setCellFactory(TextFieldTableCell.forTableColumn());
        primerApCol.setOnEditCommit(event -> {
            Empleado employee = event.getRowValue();
            employee.setPrimer_apellido(event.getNewValue());
            updateEmployee(employee);
        });

        segundoApCol.setCellValueFactory(
                new PropertyValueFactory<>("segundo_apellido")
        );
        segundoApCol.setCellFactory(TextFieldTableCell.forTableColumn());
        segundoApCol.setOnEditCommit(event -> {
            Empleado employee = event.getRowValue();
            employee.setSegundo_apellido(event.getNewValue());
            updateEmployee(employee);
        });

        nifCol.setCellValueFactory(
                new PropertyValueFactory<>("nif")
        );
        nifCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nifCol.setOnEditCommit(event -> {
            Empleado employee = event.getRowValue();
            employee.setNif(event.getNewValue());
            updateEmployee(employee);
        });

        numCol.setCellValueFactory(
                new PropertyValueFactory<>("numero_afiliacion")
        );
        numCol.setCellFactory(TextFieldTableCell.forTableColumn());
        numCol.setOnEditCommit(event -> {
            Empleado employee = event.getRowValue();
            employee.setNumero_afiliacion(event.getNewValue());
            updateEmployee(employee);
        });


        empresaCol.setCellValueFactory(
                new PropertyValueFactory<>("nombre_empresa")
        );
        empresaCol.setCellFactory(TextFieldTableCell.forTableColumn());
        empresaCol.setOnEditCommit(event -> {
            Empleado employee = event.getRowValue();
            int cod_empresa = ServicesLocator.getEnterprise().getEmpresaCodByName(event.getNewValue());
            employee.setCod_empresa(cod_empresa);
            updateEmployee(employee);
        });

        horasColum.setCellValueFactory(
                new PropertyValueFactory<>("horas_laborables")
        );
        horasColum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        horasColum.setOnEditCommit(event -> {
            Empleado employee = event.getRowValue();
            employee.setHoras_laborables(event.getNewValue());
            updateEmployee(employee);
        });
        populateTable();
        deleteItem.setOnAction(event -> deleteEmployee());
        insertBtn.setOnAction(event -> openModal());
    }

    private void populateTable() {
        ObservableList<Empleado> employees = FXCollections.observableArrayList(ServicesLocator.getEmployee().listadoEmpleadosModelo());
        employeesTable.setItems(employees);
    }

    private void deleteEmployee() {
        Empleado empleado = employeesTable.getSelectionModel().getSelectedItem();
        if(empleado != null){
            ServicesLocator.getEmployee().deleteEmployee(empleado);
            setNotificationData("Empleando eliminado del sistema con éxito", "Empleado eliminado");
            notification.showAndDismiss(Duration.millis(5000));
            notification.setAnimationType(AnimationType.POPUP);
            populateTable();
        }

    }

    private void updateEmployee(Empleado employee) {
        ServicesLocator.getEmployee().updateEmployee(employee);
        setNotificationData("Información de empleado editada", "Cambios realizados con éxito");

        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);
    }

    private void setNotificationData(String message,String title){
        notification.setMessage(message);
        notification.setTitle(title);
        notification.setNotificationType(NotificationType.SUCCESS);
    }

    @FXML
    void deleteEmployee(KeyEvent event) {
        KeyCode code = event.getCode();
        if(code == KeyCode.DELETE || code == KeyCode.SUBTRACT ){
            deleteEmployee();
        }

    }

    private void openModal(){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddEmployeeDialog.fxml"));
                Parent parent = fxmlLoader.load();

                AddEmployeeDialogController dialogController = fxmlLoader.getController();
                dialogController.setAppMainObservableList(employeesTable.getItems());

                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                scene.setFill(Color.TRANSPARENT);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.initModality(Modality.APPLICATION_MODAL);
                //stage.setAlwaysOnTop(true);
                stage.setScene(scene);
                stage.show(); }
            catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void openInsertModal(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ADD){
            openModal();
        }
    }
}
