package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.Empleado;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
public class VacationsReport {

    private static final String PALOBIOFARMA = "config_files" + File.separator + "palobiofarma.png";
    private static final String MEDIBIOFARMA = "config_files" + File.separator + "medibiofarma.png";
    private static final String PDF_Directory = System.getProperty("user.home") + "/Desktop" + File.separator;
    private static ArrayList<String> months;

    public static void exportEmployees(ArrayList<Map<String, ArrayList<String>>> vacations, ArrayList<Empleado> employees) {
        Document document = new Document();
        try{
            String fileName = "Reporte de Vacaciones.pdf";
            if(employees.size() == 1){
                fileName = "Reporte de Vacaciones de "
                        +employees.get(0).getNombre()+".pdf";
            }
            FileOutputStream fileOutputStream = new FileOutputStream(PDF_Directory+fileName);

            PdfWriter.getInstance(document, fileOutputStream);

            document.open();
            Locale spanishLocale=new Locale("es", "ES");
            months = new ArrayList<>();

            for(Month month : Month.values()){
                months.add(month.getDisplayName(TextStyle.FULL, spanishLocale));
            }
            months.add(0, "Empleado");
            PdfPTable table = new PdfPTable(8);
            setTableStyle(table);
            addTableHeader(table, 1, 7);
            addRows(table, vacations, employees, 1, 7);

            PdfPTable table2 = new PdfPTable(8);
            setTableStyle(table2);
            addTableHeader(table2, 7, 13);
            addRows(table2, vacations, employees, 7, 13);


            document.add(table);
            document.newPage();
            document.add(table2);
            document.close();

            File file = new File(PDF_Directory+fileName);


            //first check if Desktop is supported by Platform or not
            Desktop desktop = Desktop.getDesktop();

            if(file.exists()) {
                desktop.open(file);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void addTableHeader(PdfPTable table, int firstMonth, int lastMonth) {

        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(months.get(0)));
        table.addCell(header);
        for(int i = firstMonth; i < lastMonth; i++){
            header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(months.get(i).substring(0,3).toUpperCase()+"."));
            table.addCell(header);
        }
        header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase("Total"));
        table.addCell(header);
    }

    private static void addRows(PdfPTable table, ArrayList<Map<String, ArrayList<String>>> vacations,
                                ArrayList<Empleado> employees, int firstMonth, int lastMonth) {
        for(Empleado em : employees){
            table.addCell(em.getNombre());
            int total = 0;
            for(int i = firstMonth; i < lastMonth; i++){
                ArrayList<String> days = vacations.get(employees.indexOf(em)).get(months.get(i));
                table.addCell(String.join(",",days));
                total += days.size();
            }
            table.addCell(String.valueOf(total));
        }
    }

    private static void setTableStyle(PdfPTable table){
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table
    }

}
