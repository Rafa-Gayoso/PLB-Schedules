package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
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

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeesManagementController implements Initializable {
    private final String PALOBIOFARMA = "config_files" + File.separator + "palobiofarma.png";
    private final String MEDIBIOFARMA = "config_files" + File.separator + "medibiofarma.png";
    private final String PDF_Directory = System.getProperty("user.home") + "/Desktop" + File.separator +
            "Empleados de Palobiofarma y Medibiofarma.pdf";

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
            populateTable();
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

    @FXML
    void exportEmployees() {
        Document document = new Document();
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(PDF_Directory));
            document.open();
            document.addAuthor("Palobiofarma S.L");
            document.addCreationDate();
            document.addTitle("Listado de empleados Palobiofarma y Medibiofarma");
            //document.addSubject("An example to show how attributes can be added to pdf files.");
            PdfPTable table = new PdfPTable(6); // 6 columns.
            table.setWidthPercentage(100); //Width 100%
            table.setSpacingBefore(10f); //Space before table
            table.setSpacingAfter(10f); //Space after table

            //Add Image
            Image image1 = Image.getInstance(PALOBIOFARMA);

            //Fixed Positioning
            image1.setAbsolutePosition(35f, 780f);

            //Scale to new height and new width of image
            image1.scaleAbsolute(100, 55);

            //Add to document
            document.add(image1);

            Image image2 = Image.getInstance(MEDIBIOFARMA);
            image2.setAbsolutePosition(415f, 780f);

            //Scale to new height and new width of image
            image2.scaleAbsolute(150, 55);

            //Add to document
            document.add(image2);
            document.add(new Paragraph(" "));
            //Set Column widths
            float[] columnWidths = {1.5f, 2.1f, 1.3f, 1.8f, 0.5f, 1.7f};
            table.setWidths(columnWidths);

            table.addCell(createCell("Nombre"));
            table.addCell(createCell("Apellidos"));
            table.addCell(createCell("NIF/NIE"));
            table.addCell(createCell("Número de Afiliación"));
            table.addCell(createCell("H"));
            table.addCell(createCell("Empresa"));

            for(Empleado employee: employeesTable.getItems()){
                table.addCell(createCell(employee.getNombre()));
                table.addCell(createCell(employee.getPrimer_apellido() + " "+ employee.getSegundo_apellido()));
                table.addCell(createCell(employee.getNif()));
                table.addCell(createCell(employee.getNumero_afiliacion()));
                table.addCell(createCell(Integer.toString(employee.getHoras_laborables())));
                table.addCell(createCell(employee.getNombre_empresa()));
            }
            File file = new File(PDF_Directory);

            //first check if Desktop is supported by Platform or not
            Desktop desktop = Desktop.getDesktop();

            if(file.exists()) {
                desktop.open(file);
            }
            document.add(table);
            document.close();
            writer.close();


        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private PdfPCell createCell(String text){
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setPaddingLeft(7);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setUseBorderPadding(true);
        return cell;
    }
}
