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
import java.util.*;

public class VacationsReport {
  private static final String PDF_Directory =
          "config_files" + File.separator;
  private static ArrayList<String> months;

  public static void exportEmployees(ArrayList<Map<String, ArrayList<VacationType>>> vacations,
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
      addTableHeader(table, employees.size());
      double usedVacationsDays = addRows(table, vacations, employees);

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

  private static void addTableHeader(PdfPTable table,
                                     int employees) {

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
    if (employees > 1) {
      header = new PdfPCell();
      header.setBackgroundColor(BaseColor.LIGHT_GRAY);
      header.setBorderWidth(2);
      header.setPhrase(new Phrase("Total"));
      table.addCell(header);
    }

  }

  private static double addRows(PdfPTable table, ArrayList<Map<String, ArrayList<VacationType>>> vacations,
                                    ArrayList<Empleado> employees) {
    double usedVacationsDays = 0;
    for (Empleado em : employees) {
      table.addCell(em.getNombre());
      double total = 0;
      for (int i = 1; i < 13; i++) {
        ArrayList<VacationType> days = vacations.get(employees.indexOf(em)).get(months.get(i));
        StringBuilder daysString = new StringBuilder();
        for (VacationType day : days) {
          daysString.append(day.getVacationDay());

          if(day.getDayWorked() == 0.5)
            daysString.append("(1/2)");

          daysString.append(" ");

          total += day.getDayWorked();
        }

        if(daysString.length() > 1)
          daysString.deleteCharAt(daysString.length() - 1);

        table.addCell(daysString
                .toString().replace(" ", ","));
      }
      if (employees.size() == 1) {
        usedVacationsDays += total;
      }else{
        table.addCell(String.valueOf(total));
      }
    }

    return usedVacationsDays;
  }

  private static void setTableStyle(PdfPTable table) {
    table.setWidthPercentage(100); //Width 100%
    table.setSpacingBefore(10f); //Space before table
    table.setSpacingAfter(10f); //Space after table
  }

}
