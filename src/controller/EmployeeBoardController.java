package controller;

import com.jfoenix.controls.JFXButton;
import dao.implementation.EmpleadoDaoImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Empleado;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.FormatEmployeeName;
import utils.SMBUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;


public class EmployeeBoardController implements Initializable {

    @FXML
    private JFXButton btnInsert;


    @FXML
    private GridPane grid;

    private List<Empleado> employees = new ArrayList<>();
    private EmpleadoDaoImpl employeeDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeDao = new EmpleadoDaoImpl();
        employees.addAll(LoginController.getEmployees());
        btnInsert.setOnAction(event -> employeeDataManagement(null,grid));
        int column = 0;
        int row = 1;
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
            Map<String, ArrayList<String>> vacationsMap =  getVacationsDays(employee);
            for (Map.Entry<String, ArrayList<String>> entry : vacationsMap.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        });

        items.add(schedule);
        items.add(update);
        items.add(delete);
        items.add(vacations);

        return items;
    }

    private Map<String, ArrayList<String>> getVacationsDays(Empleado employee){
        Map<String, ArrayList<String>> vacations = new HashMap<>();
        try {
            String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
            SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
            File file = new File(employee.getDireccionCronograma() + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);
            for(int i =1; i < b.getNumberOfSheets(); i++){
                ArrayList<String> days = new ArrayList<>();
                XSSFSheet sheet = b.getSheetAt(i);
                for(int j =15; j < 46; j++){
                    XSSFRow row =sheet.getRow(j);
                    if(row != null){
                        XSSFCell cell = row.getCell(1);
                        XSSFCell cellEntryHour = row.getCell(2);
                        if(cell != null){
                            int day = (int)cell.getNumericCellValue();
                            if(cellEntryHour.getCellType() == CellType.STRING){
                                if(cellEntryHour.getStringCellValue().equalsIgnoreCase("Vacaciones")){
                                    days.add(String.valueOf(day));
                                }
                            }
                        }
                    }
                }
                Locale spanishLocale=new Locale("es", "ES");
                String month = Month.of(i).getDisplayName(TextStyle.FULL, spanishLocale);
                vacations.put(month,days);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return vacations;
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
}
