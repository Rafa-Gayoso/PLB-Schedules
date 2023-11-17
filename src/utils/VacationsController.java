package utils;

import model.Empleado;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class VacationsController {

    private static final ArrayList<String> specialText = new ArrayList<>(Arrays.asList("VACACIONES", "MEDIO DIA", "VACACIONES ANTERIORES"));

    private static Map<String, ArrayList<VacationType>> getVacationsDays(Empleado employee){
        Map<String, ArrayList<VacationType>> vacations = new HashMap<>();
        try {
            String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
            SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
            File file = new File(employee.getDireccionCronograma() + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);
            for(int i =1; i < b.getNumberOfSheets(); i++){
                ArrayList<VacationType> days = new ArrayList<>();
                XSSFSheet sheet = b.getSheetAt(i);
                for(int j =15; j < 46; j++){
                    XSSFRow row =sheet.getRow(j);
                    if(row != null){
                        XSSFCell cell = row.getCell(1);
                        XSSFCell cellEntryHour = row.getCell(2);
                        if(cell != null){
                            int day = (int)cell.getNumericCellValue();
                            if(cellEntryHour.getCellType() != null && cellEntryHour.getCellType() == CellType.STRING &&
                                    specialText.contains(cellEntryHour.getStringCellValue().toUpperCase())){
                                int pos = specialText.indexOf(cellEntryHour.getStringCellValue().toUpperCase());
                                if(pos == 0)
                                    days.add(new VacationType(String.valueOf(day), 1));
                                else
                                    days.add(new VacationType(String.valueOf(day), 0.5));
                            }
                        }
                    }
                }
                Locale spanishLocale=new Locale("es", "ES");
                String month = Month.of(i).getDisplayName(TextStyle.FULL, spanishLocale);
                vacations.put(month,days);
            }
            inputStream1.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return vacations;
    }

    public static ArrayList<Map<String, ArrayList<VacationType>>> getVacationsDaysEmployees(ArrayList<Empleado> employees){
        ArrayList<Map<String, ArrayList<VacationType>>> vacations = new ArrayList<>();

        for(Empleado employee : employees){
            Map<String, ArrayList<VacationType>> vacationsMap = getVacationsDays(employee);
            vacations.add(vacationsMap);
        }

        VacationsReport.exportEmployees(vacations, employees);
        return vacations;
    }
}
