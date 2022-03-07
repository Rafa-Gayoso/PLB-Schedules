package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.print.PageOrientation;
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
  private static final String PDF_Directory =
      System.getProperty("user.home") + "/Desktop" + File.separator;
  private static ArrayList<String> months;
  private static int usedVacationsDays = 0;

  public static void exportEmployees(ArrayList<Map<String, ArrayList<String>>> vacations,
      ArrayList<Empleado> employees) {
    Document document = new Document(PageSize.A4.rotate());
    try {
      String fileName = "Reporte de Vacaciones.pdf";
      if (employees.size() == 1) {
        fileName = "Reporte de Vacaciones de "
            + employees.get(0).getNombre() + ".pdf";
      }
      FileOutputStream fileOutputStream = new FileOutputStream(PDF_Directory + fileName);

      PdfWriter.getInstance(document, fileOutputStream);

      document.open();

      Paragraph header = new Paragraph("Reporte de Vacaciones");
      header.setAlignment(Element.ALIGN_CENTER);
      document.add(header);

      Paragraph header2 = new Paragraph("Días reportados de Vacaciones");

      document.add(header2);

      Locale spanishLocale = new Locale("es", "ES");
      months = new ArrayList<>();

      for (Month month : Month.values()) {
        months.add(month.getDisplayName(TextStyle.FULL, spanishLocale));
      }
      months.add(0, "Empleado");
      PdfPTable table = new PdfPTable(13);
      if (employees.size() > 1) {
        table = new PdfPTable(14);
      }
      setTableStyle(table);
      addTableHeader(table, 1, 13, employees.size());
      addRows(table, vacations, employees, 1, 13);

      document.add(table);

      if (employees.size() == 1) {
        document.add(new Paragraph("Días de vacaciones totales "
            + employees.get(0).getVacations()));
        document.add(new Paragraph("Días de vacaciones utilizados "
            + usedVacationsDays));
        document.add(new Paragraph("Días de vacaciones restantes "
            + (employees.get(0).getVacations() - usedVacationsDays)));
      }
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

  private static void addTableHeader(PdfPTable table, int firstMonth, int lastMonth,
      int employees) {

    PdfPCell header = new PdfPCell();
    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
    header.setBorderWidth(2);
    header.setPhrase(new Phrase(months.get(0)));
    table.addCell(header);
    for (int i = firstMonth; i < lastMonth; i++) {
      header = new PdfPCell();
      header.setBackgroundColor(BaseColor.LIGHT_GRAY);
      header.setBorderWidth(2);
      header.setPhrase(new Phrase(months.get(i).substring(0, 3).toUpperCase() + "."));
      table.addCell(header);
    }
    if (employees > 1) {
      header = new PdfPCell();
      header.setBackgroundColor(BaseColor.LIGHT_GRAY);
      header.setBorderWidth(2);
      header.setPhrase(new Phrase("Total"));
      table.addCell(header);
    }

  }

  private static void addRows(PdfPTable table, ArrayList<Map<String, ArrayList<String>>> vacations,
      ArrayList<Empleado> employees, int firstMonth, int lastMonth) {
    for (Empleado em : employees) {
      table.addCell(em.getNombre());
      int total = 0;
      for (int i = firstMonth; i < lastMonth; i++) {
        ArrayList<String> days = vacations.get(employees.indexOf(em)).get(months.get(i));
        table.addCell(String.join(",", days));
        total += days.size();
      }

      //
      if (employees.size() == 1) {
        usedVacationsDays += total;
      }else{
        table.addCell(String.valueOf(total));
      }
    }
  }

  private static void setTableStyle(PdfPTable table) {
    table.setWidthPercentage(100); //Width 100%
    table.setSpacingBefore(10f); //Space before table
    table.setSpacingAfter(10f); //Space after table
  }

}
