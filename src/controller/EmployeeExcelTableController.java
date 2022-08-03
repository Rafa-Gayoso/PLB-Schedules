package controller;

import com.jfoenix.controls.JFXButton;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import model.Empleado;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import model.TableExcelModel;
import utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class EmployeeExcelTableController /*implements Initializable */{


    private final String WEEKEND_COLOR = "FFFFFFCC";
    private final String AUTONOMIC_COLOR = "FF92D050";
    private final String LOCAL_COLOR = "FF00B0F0";
    private final String NATIONAL_COLOR = "FFFF0000";
    private final String REGULAR_COLOR = "FFFFFFFF";

    private ArrayList<String> specialText =  new ArrayList<>(
            Arrays.asList("BAJA", "VACACIONES", "MEDIO DIA","VACACIONES ANTERIORES"));
    private DateFormat inFormat;


    @FXML
    private TableView<TableExcelModel> excelTable;

    @FXML
    private TableColumn<TableExcelModel, String> dayColumn;

    @FXML
    private TableColumn<TableExcelModel, String> entryColumn;

    @FXML
    private TableColumn<TableExcelModel, String> exitColumn;

    @FXML
    private TableColumn<TableExcelModel, String> journalColumn;

    @FXML
    private ContextMenu contextMenu;

    @FXML
    private MenuItem validateBtn;


    @FXML
    private Label workedHoursLbl;

    @FXML
    private MenuItem unlockBtn;

    @FXML
    private MenuItem warningBtn;

    @FXML
    private MenuItem saveSchedule;

    @FXML
    private JFXButton saveBtn;
    private int totalJournal;

    public void setData(Empleado employee, int sheet){
        Locale spanishLocale=new Locale("es", "ES");
        this.totalJournal = employee.getHoras_laborables();
        String month = Month.of(sheet).getDisplayName(TextStyle.FULL, spanishLocale);
        String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);

        validateBtn.setOnAction(event -> lockSheet(employee,employeeFileName,sheet));
        unlockBtn.setOnAction(event -> unlockSheet(employee,employeeFileName,sheet));
        warningBtn.setOnAction(event -> SendMail.sendWarningEmail(employee, month));

        inFormat = new SimpleDateFormat( "hh:mm");

        dayColumn.setCellValueFactory(cellData -> cellData.getValue().dayProperty());

        entryColumn.setCellValueFactory(cellData -> cellData.getValue().entryHourProperty());

        entryColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        entryColumn.setOnEditCommit(event -> {
            try {
                String hourValue = event.getNewValue();
                TableExcelModel model = event.getRowValue();
                setDataToModel(hourValue, model, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        exitColumn.setCellValueFactory(cellData -> cellData.getValue().exitHourProperty());
        exitColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        exitColumn.setOnEditCommit(event -> {
            try {
                String hourValue = event.getNewValue();
                TableExcelModel model = event.getRowValue();
                setDataToModel(hourValue, model, false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        journalColumn.setCellValueFactory(cellData -> cellData.getValue().journalTimeProperty());
        journalColumn.setCellFactory(column -> new CustomCell<TableExcelModel>());

        excelTable.setRowFactory(tableView -> {
            TableRow<TableExcelModel> row = new TableRow<>();
            return row;
        });

        if(LoginController.getUsu().getRol() == Roles.EMPLEADO.getCode()){
           contextMenu.getItems().clear();
        }
        saveSchedule = new MenuItem("Guardar Horario de Trabajo");
        saveSchedule.setOnAction(actionEvent -> saveData(employee, employeeFileName, sheet, month));
        contextMenu.getItems().add(saveSchedule);

        saveBtn.setOnAction(actionEvent -> saveData(employee, employeeFileName, sheet, month));

        populateTable(employee, employeeFileName, sheet);

    }

    private void populateTable(Empleado employee, String employeeFileName, int sheetNumber) {
        ArrayList<TableExcelModel> models = new ArrayList<>();
        try {

            File file = new File(employee.getDireccionCronograma() + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);
            XSSFSheet sheet = b.getSheetAt(sheetNumber);

            XSSFCell totalMonthCell = sheet.getRow(48).getCell(6);
            XSSFCell currentWorkingMonthCell = sheet.getRow(46).getCell(6);

            FormulaEvaluator formulaEval = b.getCreationHelper().createFormulaEvaluator();
            CellValue monthHourValue=formulaEval.evaluate(totalMonthCell);

            int monthValue = (int) monthHourValue.getNumberValue();


            int monthCurrentValue =0;

            for(int i =15; i < 46; i++){
                XSSFRow row =sheet.getRow(i);
                if(row != null){
                    XSSFCell cell = row.getCell(1);
                    XSSFCell cellEntryHour = row.getCell(2);
                    XSSFCell cellExitHour = row.getCell(4);
                    if(cell != null){
                        int day = (int)cell.getNumericCellValue();
                        String color = cell.getCellStyle().getFillForegroundColorColor().getARGBHex();
                        String freeDay = "";

                        if((color != null && !color.equalsIgnoreCase("FFFFFFFF")) || cellEntryHour.getCellType() == CellType.BLANK){
                            freeDay = returnFreeDay(color);
                            models.add(new TableExcelModel(Integer.toString(day), freeDay,freeDay, freeDay));
                        }else if(cellEntryHour.getCellType() == CellType.STRING){
                            String cellValue = cellEntryHour.getStringCellValue();
                            models.add(new TableExcelModel(Integer.toString(day), cellValue,
                                cellValue,cellValue));
                            if(cellValue.equalsIgnoreCase("Medio Dia"))
                                monthCurrentValue += employee.getHoras_laborables() / 2;
                        }else if(cellEntryHour.getCellType() == CellType.NUMERIC){
                            LocalTime entry = LocalTime.of(cellEntryHour.getDateCellValue().getHours(),cellEntryHour.getDateCellValue().getMinutes());
                            LocalTime exit = LocalTime.of(cellExitHour.getDateCellValue().getHours(),cellExitHour.getDateCellValue().getMinutes());

                            String resultHour = subtractHours(entry, exit);
                            models.add(new TableExcelModel(Integer.toString(day), entry.toString(),
                                exit.toString(),resultHour));
                            monthCurrentValue += Integer.parseInt(resultHour.substring(0, 1));
                        }
                    }
                }
            }

            ObservableList<TableExcelModel> excelModels = FXCollections.observableArrayList(models);
            excelTable.setItems(excelModels);
            excelTable.setEditable(true);
            if(sheet.getProtect()){
                excelTable.setEditable(false);
                contextMenu.getItems().remove(contextMenu.getItems().size() - 1);
            }
            workedHoursLbl.setText(monthCurrentValue+"/"+monthValue);
            b.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String returnFreeDay(String colorHex){
        String freeDay = "FESTIVO NACIONAL";
        if(colorHex == null || colorHex.equalsIgnoreCase(REGULAR_COLOR)){
            return "";
        }
        if(colorHex.equalsIgnoreCase(WEEKEND_COLOR)){
            freeDay = "FIN DE SEMANA";
        }else if(colorHex.equalsIgnoreCase(LOCAL_COLOR)){
            freeDay = "FESTIVO LOCAL";
        }else if(colorHex.equalsIgnoreCase(AUTONOMIC_COLOR)) {
            freeDay = "FESTIVO AUTONÓMICO";
        }
        return freeDay;
    }

    private void saveData(Empleado employee, String employeeFileName, int sheetNumber, String month){
        try {
            File file = new File(employee.getDireccionCronograma() + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);

            CreationHelper createHelper = b.getCreationHelper();
            CellStyle cellStyle = b.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("h:mm"));

            XSSFSheet sheet = b.getSheetAt(sheetNumber);

            for(TableExcelModel model : excelTable.getItems()){
                int row = excelTable.getItems().indexOf(model)+15;
                XSSFCell cellEntryHour = sheet.getRow(row).getCell(2);
                XSSFCell cellExitHour = sheet.getRow(row).getCell(4);
                XSSFCell cellJournalTime = sheet.getRow(row).getCell(6);
                if(model.getIntegerJournalTime() != 0){
                    cellEntryHour.setCellStyle(cellStyle);
                    cellExitHour.setCellStyle(cellStyle);
                    cellEntryHour.setCellValue(DateUtil.convertTime(model.getEntryHour()));
                    cellExitHour.setCellValue(DateUtil.convertTime(model.getExitHour()));
                }else if(specialText.contains(model.getEntryHour().toUpperCase())){
                    cellEntryHour.setCellValue(model.getEntryHour());
                    cellExitHour.setCellValue(model.getEntryHour());
                    cellJournalTime.setCellType(CellType.STRING);
                    cellJournalTime.setCellValue(model.getEntryHour());
                }
            }

            OutputStream out = new FileOutputStream(file.getAbsolutePath(), false);
            b.write(out);
            b.close();
            out.close();
            inputStream1.close();
            SendMail.sendCompilationEmail(employee.getNombre(), month);
            SMBUtils.uploadFile(employee.getNombre_empresa(),employeeFileName,employee.getDireccionCronograma());
        }catch (Exception e){
            e.printStackTrace();
        }
        for(TableExcelModel model : excelTable.getItems()){
            System.out.println(model);
        }
    }

    private boolean validateHour(String hour){
        return hour.contains(":");
    }

    private void setDataToModel(String value, TableExcelModel model, boolean entry) throws ParseException {
        if(value.length() == 4 && value.charAt(0) != '0'){
            value = "0"+value;
        }
        if(entry){
            model.setEntryHour(value);
        }else{
            model.setExitHour(value);
        }

        if(validateHour(value)){
            String hour;
            LocalTime localTime = LocalTime.parse(value);
            if(model.getIntegerJournalTime() == 0.0){
                hour = "17:00";
            }
            else {
                LocalTime entyTrime = LocalTime.parse(model.getEntryHour());
                LocalTime exitTime = localTime.parse(model.getExitHour());
                hour = subtractHours(entyTrime, exitTime);
            }
            model.setJournalTime(hour);
        }else{

            model.setJournalTime(value);
            model.setExitHour(value);
            model.setEntryHour(value);

        }

    }

    private String subtractHours(LocalTime entyTrime, LocalTime exitTime) {
        String hour;
        Duration duration = Duration.between(entyTrime, exitTime);
        hour = formatDuration(duration);
        if(Integer.parseInt(String.valueOf(hour.charAt(0)))>4){
            duration = duration.minusHours(1);
            hour = formatDuration(duration);
        }
        return hour;
    }

    private void lockSheet(Empleado employee, String employeeFileName, int sheet){
        try {
            File file = new File(employee.getDireccionCronograma() + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);

            b.getSheetAt(sheet).protectSheet("VALIDADO");

            OutputStream out = new FileOutputStream(file.getAbsolutePath(), false);
            b.write(out);
            b.close();
            out.close();
            inputStream1.close();
            SMBUtils.uploadFile(employee.getNombre_empresa(),employeeFileName,employee.getDireccionCronograma());
            contextMenu.getItems().remove(contextMenu.getItems().size() - 1);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void unlockSheet(Empleado employee, String employeeFileName, int sheet){
        try {
            File file = new File(employee.getDireccionCronograma() + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);

            b.getSheetAt(sheet).protectSheet(null);

            OutputStream out = new FileOutputStream(file.getAbsolutePath(), false);
            b.write(out);
            b.close();
            out.close();
            inputStream1.close();
            SMBUtils.uploadFile(employee.getNombre_empresa(),employeeFileName,employee.getDireccionCronograma());
            contextMenu.getItems().add(saveSchedule);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String formatDuration(Duration duration){
        StringBuilder sb = new StringBuilder();
        sb.append(duration.toString().substring(2,3));
        if(duration.toString().length() == 4){
            sb.append(":00");
        }
        else
            sb.append(":"+duration.toString().substring(4,duration.toString().length()-1));
        return sb.toString();
    }

}
