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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MonthValidator {

    private static Map<String, Boolean> getValidMonths(Empleado employee) {
        Map<String, Boolean> monthValidated = new HashMap<>();
        try {
            String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
            SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
            File file = new File(employee.getDireccionCronograma() + File.separator + employeeFileName);
            FileInputStream inputStream1 = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook b = new XSSFWorkbook(inputStream1);
            for(int i =1; i < b.getNumberOfSheets(); i++){
                boolean validMonth = b.getSheetAt(i).isSheetLocked();
                Locale spanishLocale=new Locale("es", "ES");
                String month = Month.of(i).getDisplayName(TextStyle.FULL, spanishLocale);
                monthValidated.put(month, validMonth);
            }
            inputStream1.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return monthValidated;
    }

    public static void getMonthValidatedByEmployees(ArrayList<Empleado> employees){
        ArrayList<Map<String, Boolean>> monthsValidates = new ArrayList<>();

        for(Empleado employee : employees){
            Map<String, Boolean> vacationsMap = getValidMonths(employee);
            monthsValidates.add(vacationsMap);
        }

        MonthsReport.exportEmployees(monthsValidates, employees);
    }
}
