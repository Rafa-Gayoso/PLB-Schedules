package utils;

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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MonthsReport {
    private static final String PDF_Directory =
            "config_files" + File.separator;
    private static ArrayList<String> months;

    public static void exportEmployees(ArrayList<Map<String, Boolean>> validatedMonths,
                                       ArrayList<Empleado> employees) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            String fileName = "Reporte de Meses Validados.pdf";
            if (employees.size() == 1) {
                fileName = "Reporte de Meses Validados de "
                        + employees.get(0).getNombre() + ".pdf";
            }
            FileOutputStream fileOutputStream = new FileOutputStream(PDF_Directory + fileName);

            PdfWriter.getInstance(document, fileOutputStream);

            document.open();

            Paragraph header = new Paragraph("Reporte de Meses Validados");
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Locale spanishLocale = new Locale("es", "ES");
            months = new ArrayList<>();

            for (Month month : Month.values()) {
                months.add(month.getDisplayName(TextStyle.FULL, spanishLocale));
            }
            months.add(0, "Empleado");
            PdfPTable table = new PdfPTable(13);

            setTableStyle(table);
            addTableHeader(table);
            addRows(table, validatedMonths, employees);

            document.add(table);

            document.close();

            File file = new File(PDF_Directory + fileName);

            //first check if Desktop is supported by Platform or not
            Desktop desktop = Desktop.getDesktop();

            if (file.exists()) {
                desktop.open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addTableHeader(PdfPTable table) {

        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(months.get(0)));
        table.addCell(header);
        for (int i = 1; i < 13; i++) {
            header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(months.get(i).substring(0, 3).toUpperCase() + "."));
            table.addCell(header);
        }
    }

    private static void addRows(PdfPTable table, ArrayList<Map<String, Boolean>> monthsValidator,
                                ArrayList<Empleado> employees) {
        for (Empleado em : employees) {
            table.addCell(em.getNombre());
            for (int i = 1; i < 13; i++) {
                boolean validatedMonth = monthsValidator.get(employees.indexOf(em)).get(months.get(i));

                String answer = validatedMonth ? "Sí" : "No";
                table.addCell(answer);
            }
        }
    }

    private static void setTableStyle(PdfPTable table) {
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table
    }

}
