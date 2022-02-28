package controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import dao.implementation.EmpleadoDaoImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Empleado;
import services.GetVacationsService;
import utils.CreateSplashScreen;
import utils.FormatEmployeeName;
import utils.SMBUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import utils.VacationsController;


public class EmployeeManagementBoardController implements Initializable {

    private final String PALOBIOFARMA = "config_files" + File.separator + "palobiofarma.png";
    private final String MEDIBIOFARMA = "config_files" + File.separator + "medibiofarma.png";
    private final String PDF_Directory = System.getProperty("user.home") + "/Desktop" + File.separator +
            "Empleados de Palobiofarma y Medibiofarma.pdf";

    @FXML
    private JFXButton btnInsert;

    @FXML
    private JFXButton vacationsBtn;

    @FXML
    private GridPane grid;

    private List<Empleado> employees = new ArrayList<>();
    private EmpleadoDaoImpl employeeDao;
    public static int row, column;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        column = 0;
        row = 1;
        employeeDao = new EmpleadoDaoImpl();
        employees.addAll(LoginController.getEmployees());


        try {
            for (Empleado employee : employees) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/Employee.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                final ContextMenu editMenu = new ContextMenu();

                editMenu.getItems().addAll(createMenuItems(employee));

                anchorPane.setOnContextMenuRequested(
                        ae -> {
                            // Popup menu at the location of the right click.
                            editMenu.show(anchorPane, ae.getScreenX(), ae.getScreenY());
                        });

                EmployeeController itemController = fxmlLoader.getController();
                itemController.setData(employee);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)

                GridPane.setMargin(anchorPane, new Insets(20));
            }
            btnInsert.setOnAction(event -> employeeDataManagement(null,grid));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteEmployee(Empleado employee) {
        for(Node pane : grid.getChildren()){
            EmployeeController controller = (EmployeeController) pane.getUserData();
            if(controller.getEmpleado().equals(employee)){
                employeeDao.deleteEntity(employee);
                LoginController.getEmployees().remove(employee);
                grid.getChildren().remove(pane);
                column--;
                if(column <= 0){
                    row--;
                    column = 3;
                }
                break;
            }
        }
    }

    private void employeeDataManagement(Empleado employee, GridPane grid) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/AddEmployeeDialog.fxml"));
            Parent parent = fxmlLoader.load();

            AddEmployeeDialogController dialogController = fxmlLoader.getController();
            dialogController.setData(employee, grid);
            dialogController.setDao(employeeDao);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private List<MenuItem> createMenuItems(Empleado employee){
        List<MenuItem> items = new ArrayList<MenuItem>();
        MenuItem update = new MenuItem("Modificar Datos");
        MenuItem delete = new MenuItem("Eliminar empleado");
        MenuItem schedule = new MenuItem("Ver Horario");
        MenuItem vacations = new MenuItem("Ver Vacaciones");

        update.setOnAction(e-> employeeDataManagement(employee, grid));
        delete.setOnAction(e-> deleteEmployee(employee));
        schedule.setOnAction(e-> showSchedule(employee));
        vacations.setOnAction(e-> {
            ArrayList<Empleado> employees = new ArrayList<>();
            employees.add(employee);
            GetVacationsService task = new GetVacationsService(employees);
            task.start();

            Stage stage = CreateSplashScreen.createPDFSplashScreen(task);


            task.setOnRunning(event -> {
                stage.show();
            });

            task.setOnSucceeded(event -> {
                stage.close();
            });
        });

        items.add(schedule);
        items.add(update);
        items.add(delete);
        items.add(vacations);

        return items;
    }

    private void showSchedule(Empleado employee){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/EmployeeSchedulePane.fxml"));
            Parent parent = fxmlLoader.load();
            EmployeeSchedulePaneController test  = fxmlLoader.getController();
            String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
            SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
            test.setData(employee);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
            dialogStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));

            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);
            dialogStage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
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

            for(Empleado employee: LoginController.getEmployees()){
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

    @FXML
    void vacationsReport(ActionEvent event) {
        VacationsController.getVacationsDaysEmployees(LoginController.getEmployees());
        /*GetVacationsService task = new GetVacationsService(LoginController.getEmployees());
        task.start();

        Stage stage = CreateSplashScreen.createPDFSplashScreen(task);


        task.setOnRunning(e -> {
            stage.show();
        });

        task.setOnSucceeded(e -> {
            stage.close();
        });

        task.setOnFailed(e -> {
            stage.close();
        });

        task.setOnCancelled(e -> {
            stage.close();
        });*/
    }


}
