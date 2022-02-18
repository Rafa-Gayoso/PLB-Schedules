package controller;

import com.jfoenix.controls.JFXButton;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EmployeeExcelTableController /*implements Initializable */{


    private final String WEEKEND_COLOR = "FFFFFFCC";
    private final String AUTONOMIC_COLOR = "FF92D050";
    private final String LOCAL_COLOR = "FF00B0F0";
    private final String NATIONAL_COLOR = "FFFF0000";
    private final String REGULAR_COLOR = "FFFFFFFF";
    private DateFormat inFormat;
    private int vacations;

    @FXML
    private JFXButton saveButton;

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
    private MenuItem unlockBtn;

    @FXML
    private MenuItem warningBtn;

    @FXML
    private Label vacationsLabel;

    public void setData(Empleado employee, int sheet){
        Locale spanishLocale=new Locale("es", "ES");
        String month = Month.of(sheet).getDisplayName(TextStyle.FULL, spanishLocale);
        String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);


        validateBtn.setOnAction(event -> lockSheet(employee,employeeFileName,sheet));
        unlockBtn.setOnAction(event -> unlockSheet(employee,employeeFileName,sheet));
        warningBtn.setOnAction(event -> SendMail.sendWarningEmail(employee, month));

        inFormat = new SimpleDateFormat( "hh:mm");

        saveButton.setOnAction(actionEvent -> saveData(employee, employeeFileName, sheet, month));

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

        populateTable(employee.getDireccionCronograma(), employeeFileName, sheet);

        if(LoginController.getUsu().getRol() == Roles.EMPLEADO.getCode()){
           contextMenu.getItems().clear();
        }

    }

    private void populateTable(String address, String employeeFileName, int sheetNumber) {
        ArrayList<TableExcelModel> models = new ArrayList<>();
        try {

            File file = new File(address + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);
            XSSFSheet sheet = b.getSheetAt(0);
            vacations = (int) sheet.getRow(34).getCell(25).getNumericCellValue();

            sheet = b.getSheetAt(sheetNumber);

            for(int i =15; i < 46; i++){
                XSSFRow row =sheet.getRow(i);
                if(row != null){
                    XSSFCell cell = row.getCell(1);
                    XSSFCell cellEntryHour = row.getCell(2);
                    XSSFCell cellExitHour = row.getCell(4);
                    XSSFCell cellJournalTime = row.getCell(6);
                    if(cell != null){
                        int day = (int)cell.getNumericCellValue();
                        String color = cell.getCellStyle().getFillForegroundColorColor().getARGBHex();
                        String freeDay = "";

                        if((color != null && !color.equalsIgnoreCase("FFFFFFFF")) || cellEntryHour.getCellType() == CellType.BLANK){
                            freeDay = returnFreeDay(color);
                            models.add(new TableExcelModel(Integer.toString(day), freeDay,freeDay, freeDay));
                        }else if(cellEntryHour.getCellType() == CellType.STRING){

                            models.add(new TableExcelModel(Integer.toString(day), cellEntryHour.getStringCellValue(),
                                    cellExitHour.getStringCellValue(),cellJournalTime.getStringCellValue()));
                            if( cellEntryHour.getStringCellValue().equalsIgnoreCase("Vacaciones")){
                                vacations--;
                            }
                        }else if(cellEntryHour.getCellType() == CellType.NUMERIC){

                            String entryTime = combineHoursAndMinutes(cellEntryHour.getDateCellValue().getHours(),cellEntryHour.getDateCellValue().getMinutes());
                            String exitTime = combineHoursAndMinutes(cellExitHour.getDateCellValue().getHours(),cellExitHour.getDateCellValue().getMinutes());

                            Date entryDate = inFormat.parse(entryTime);
                            Date exitDate = inFormat.parse(exitTime);

                            String resultHour = subtractHour(entryDate, exitDate);
                            models.add(new TableExcelModel(Integer.toString(day), entryTime,
                                    exitTime,resultHour.substring(1)));
                        }
                    }
                }
            }
            ObservableList<TableExcelModel> excelModels = FXCollections.observableArrayList(models);
            excelTable.setItems(excelModels);
            excelTable.setEditable(true);
            if(sheet.getProtect()){
                excelTable.setEditable(false);
                saveButton.setDisable(true);
            }
            b.close();
           vacationsLabel.setText("Vacaciones: "+vacations);

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
                }else if(model.getEntryHour().equalsIgnoreCase("Vacaciones") ||
                        model.getEntryHour().equalsIgnoreCase("Baja") ||
                        model.getEntryHour().equalsIgnoreCase("Vacaciones Anteriores")){
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

    private String formatDouble(double time){
        String value = Double.toString(time);
        String correctedValue = value.replace(".",":");
        if(correctedValue.length() == 3){
            return correctedValue + "0";
        }
        return correctedValue;
    }

    private boolean validateHour(String hour){
        return hour.contains(":");
    }

    private void setDataToModel(String value, TableExcelModel model, boolean entry) throws ParseException {
        if(entry){
            model.setEntryHour(value);
        }else{
            model.setExitHour(value);
        }

        if(validateHour(value)){
            double hour;
            if(model.getIntegerJournalTime() == 0.0){
                hour = calculateHourOnEntryOrExit(entry, model);
            }
            else{
                Date entryDate = inFormat.parse(model.getEntryHour());
                Date exitDate = inFormat.parse(model.getExitHour());
                String resultHour = subtractHour(entryDate, exitDate);

                hour = Double.parseDouble(resultHour.replace(":","."));
            }
            model.setJournalTime(formatDouble(hour));
        }else{
            model.setJournalTime(value);
            model.setExitHour(value);
            model.setEntryHour(value);
            if(value.equalsIgnoreCase("Vacaciones")){
                vacations--;
                vacationsLabel.setText("Vacaciones :"+vacations);
            }
        }

    }

    private double calculateHourOnEntryOrExit(boolean entry, TableExcelModel model) throws ParseException {
        Date defaultDate = inFormat.parse("16:00");
        Date date;
        String resultHour;
        if (!entry) {
            date = inFormat.parse(model.getExitHour());
            resultHour = subtractHour(date,defaultDate);
        }
        else{
            date = inFormat.parse(model.getEntryHour());
            resultHour = subtractHour(defaultDate,date);
        }
        return Double.parseDouble(resultHour.replace(":","."));

    }

    private String combineHoursAndMinutes(double hours, double minutes){
        int integerHours = (int) hours;
        int integerMinutes = (int) minutes;
        String minutesString = Integer.toString(integerMinutes).length() == 1 ? integerMinutes + "0": Integer.toString(integerMinutes);
        return integerHours+":"+minutesString;
    }

    private String subtractHour(Date entryDate, Date exitDate){
        LocalTime localExitTime = LocalTime.of(exitDate.getHours(), exitDate.getMinutes());

        LocalTime updatedTime = localExitTime.minusHours(entryDate.getHours() + 1).
                minusMinutes(entryDate.getMinutes());

        return updatedTime.toString();
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
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
