package main;

import model.Empleado;
import model.Empresa;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

public class Controller {

    private final String PALOBIOFARMA = "config_files" + File.separator + "palobiofarma.png";
    private final String MEDIBIOFARMA = "config_files" + File.separator + "medibiofarma.png";

    private void copySheets(XSSFWorkbook newWorkbook, XSSFSheet newSheet, XSSFSheet sheet) {
        //copySheets(newWorkbook, newSheet, sheet, true);
        int newRownumber = newSheet.getLastRowNum() + 1;
        int maxColumnNum = 0;
        Map<Integer, XSSFCellStyle> styleMap = new HashMap<>();

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);

            newSheet.addMergedRegion(region);
        }

        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            XSSFRow srcRow = sheet.getRow(i);
            XSSFRow destRow = newSheet.createRow(i + newRownumber);
            if (srcRow != null) {
                copyRow(newWorkbook, srcRow, destRow, styleMap);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
        }
    }

    public void copyRow(XSSFWorkbook newWorkbook,XSSFRow srcRow, XSSFRow destRow, Map<Integer, XSSFCellStyle> styleMap) {
        destRow.setHeight(srcRow.getHeight());
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            if (j > 0) {
                XSSFCell oldCell = srcRow.getCell(j);
                XSSFCell newCell = destRow.getCell(j);
                if (oldCell != null) {
                    if (newCell == null) {
                        newCell = destRow.createCell(j);
                    }
                    copyCell(newWorkbook, oldCell, newCell, styleMap);
                }
            }

        }
    }

    public void copyCell(XSSFWorkbook newWorkbook, XSSFCell oldCell, XSSFCell newCell, Map<Integer, XSSFCellStyle> styleMap) {
        if (styleMap != null) {
            int stHashCode = oldCell.getCellStyle().hashCode();
            XSSFCellStyle newCellStyle = styleMap.get(stHashCode);
            if (newCellStyle == null) {
                newCellStyle = newWorkbook.createCellStyle();
                newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                styleMap.put(stHashCode, newCellStyle);
            }
            newCell.setCellStyle(newCellStyle);
        }
        switch (oldCell.getCellType()) {
            case STRING:
                newCell.setCellValue(oldCell.getRichStringCellValue());
                break;
            case NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case BLANK:
                newCell.setCellType(CellType.BLANK);
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    protected void writeFile(XSSFWorkbook book, File file) throws Exception {

        OutputStream out = new FileOutputStream(file.getAbsolutePath(), false);
        book.write(out);
        book.close();
        //out.flush();
        out.close();
    }

    private ArrayList<String> generateCellToFormula() {
        ArrayList<String> cells = new ArrayList<>();
        cells.add("!H11");
        cells.add("!P11");
        cells.add("!X11");
        cells.add("!H21");
        cells.add("!P21");
        cells.add("!X21");
        cells.add("!H30");
        cells.add("!P30");
        cells.add("!X30");
        cells.add("!H40");
        cells.add("!P40");
        cells.add("!X40");
        return cells;
    }

    private void passWeekendToSheets(XSSFWorkbook workbook) {

        XSSFSheet calendarSheet = workbook.getSheetAt(0);

        for (Row cells : calendarSheet) {
            XSSFRow row = (XSSFRow) cells;

            for (Cell value : row) {
                XSSFCell cell = (XSSFCell) value;

                if (cell.getCellType() != CellType.STRING) {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();

                        if (date != null) {
                            if (date.getDay() == 0 || date.getDay() == 6 || !cell.getCellStyle().getFillForegroundColorColor().getARGBHex().equalsIgnoreCase("FFFFFFFF")) {
                                //System.out.println("Color: " + cell.getCellStyle().getFillForegroundColorColor().getARGBHex());
                                int mouth = date.getMonth();
                                int day = date.getDate();

                                pass(workbook.getSheetAt(mouth + 1), day, cell.getCellStyle());
                            }

                            if ((date.getMonth() == 0 || date.getMonth() == 2 || date.getMonth() == 4 || date.getMonth() == 6 || date.getMonth() == 7 || date.getMonth() == 9 || date.getMonth() == 11) && date.getDate() == 31) {
                                workbook.getSheetAt(date.getMonth() + 1).getRow(10).getCell(5).setCellValue(date);
                            } else if ((date.getMonth() == 3 || date.getMonth() == 5 || date.getMonth() == 8 || date.getMonth() == 10) && date.getDate() == 30) {
                                workbook.getSheetAt(date.getMonth() + 1).getRow(10).getCell(5).setCellValue(date);
                            } else if (date.getMonth() == 1) {
                                if (date.getYear() % 4 == 0 && date.getDate() == 29) {
                                    workbook.getSheetAt(date.getMonth() + 1).getRow(10).getCell(5).setCellValue(date);
                                } else if (date.getYear() % 4 != 0 && date.getDate() == 28) {
                                    workbook.getSheetAt(date.getMonth() + 1).getRow(10).getCell(5).setCellValue(date);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void pass(XSSFSheet sheet, int day, CellStyle cellStyle) {

        boolean match = false;
        int rowStart = 15;

        while (!match) {
            if (sheet.getRow(rowStart).getCell(1).getNumericCellValue() == day) {
                fixAdjacentCell(sheet.getRow(rowStart).getCell(1), sheet.getRow(rowStart), cellStyle);
                match = true;
            } else
                rowStart++;
        }
    }

    private void fixAdjacentCell(XSSFCell cell, XSSFRow row, CellStyle style) {
        cell.setCellStyle(style);
        cell.setCellType(CellType.NUMERIC);
        row.getCell(2).setCellStyle(style);
        row.getCell(4).setCellStyle(style);
        row.getCell(6).setCellStyle(style);
        row.getCell(6).setCellFormula(null);
        row.getCell(6).setCellValue(" ");

    }

    public void mergeExcelFiles(ArrayList<Empleado> listaEmpleados, Empresa empresa, ArrayList<File> files, String ruta) {
        ArrayList<String> cell_formulas = generateCellToFormula();

        try {
            File file;
            File dir = new File(ruta+"/"+empresa.getNombre());
            if(!dir.exists()){
                dir.mkdir();
            }
            for (Empleado listaEmpleado : listaEmpleados) {
                ArrayList<InputStream> list = new ArrayList<>();
                FileInputStream inputStream1 = new FileInputStream(files.get(0));
                String employeeFullName = formatEmployeeName(listaEmpleado);
                FileInputStream inputStream2 = new FileInputStream(files.get(1));

                list.add(inputStream1);
                list.add(inputStream2);
                XSSFWorkbook book = new XSSFWorkbook();
                XSSFSheet sheet;
                file = new File(dir.getAbsolutePath() + "/" + employeeFullName + ".xlsx");

                //FileInputStream obtains input bytes from the image file

                FileInputStream inputStream;
                if (empresa.getNombre().contains("Palobiofarma")) {
                    inputStream = new FileInputStream(new File(PALOBIOFARMA));
                } else {
                    inputStream = new FileInputStream(new File(MEDIBIOFARMA));
                }
                //Get the contents of an InputStream as a byte[].
                byte[] bytes = IOUtils.toByteArray(inputStream);
                //Adds a picture to the workbook
                int pictureIdx = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                //close the input stream
                inputStream.close();

                //to control the sheet where paste the picture
                String calendar_year = "";
                String location = "";
                int total_sheets = 0;
                try {
                    for (InputStream fin : list) {
                        XSSFWorkbook b = new XSSFWorkbook(fin);
                        for (int i = 0; i < b.getNumberOfSheets(); i++) {
                            sheet = book.createSheet(b.getSheetName(i));
                            copySheets(book, sheet, b.getSheetAt(i));
                            total_sheets++;
                            if (book.getNumberOfSheets() == 1) {
                                calendar_year = book.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();
                                calendar_year = calendar_year.split(" ")[1];
                                location = book.getSheetAt(0).getRow(0).getCell(6).getStringCellValue();
                            }
                            if (total_sheets > 1) {
                                //LOGO Creation
                                //Returns an object that handles instantiating concrete classes
                                CreationHelper helper = book.getCreationHelper();

                                //Creates the top-level drawing patriarch.
                                Drawing drawing = sheet.createDrawingPatriarch();

                                //Create an anchor that is attached to the worksheet
                                ClientAnchor anchor = helper.createClientAnchor();
                                //set top-left corner for the image
                                anchor.setCol1(1);
                                anchor.setRow1(1);

                                //Creates a picture
                                Picture pict = drawing.createPicture(anchor, pictureIdx);
                                //Reset the image to the original size
                                pict.resize(3, 3);

                                //Push date
                                Cell cell = sheet.getRow(53).getCell(6);
                                if (cell != null) {
                                    cell.setCellValue(Integer.parseInt(calendar_year));
                                    cell.setCellType(CellType.NUMERIC);
                                }

                                cell = sheet.getRow(53).getCell(1);
                                if (cell != null) {
                                    cell.setCellValue("En " + location + " a");
                                    cell.setCellType(CellType.STRING);
                                }

                                cell = sheet.getRow(48).getCell(6);
                                if (cell != null) {
                                    //-2 para qiue coincida el numero de la lista con el numero de la hoja
                                    cell.setCellFormula("('" + book.getSheetAt(0).getSheetName() + "'" + "" +
                                            cell_formulas.get(total_sheets - 2) + "*" + listaEmpleado.getHoras_laborables() + ")/8");
                                }

                                cell = sheet.getRow(14).getCell(8);
                                if (total_sheets == 2 && cell != null) {
                                    cell.setCellFormula("'" + book.getSheetAt(0).getSheetName() + "'!Z35-" + "(" + cell.getCellFormula() + ")/8");
                                } else if (total_sheets > 2) {
                                    cell.setCellFormula("'" + book.getSheetAt(total_sheets - 2).getSheetName() + "'!I15-" + "(" + cell.getCellFormula() + ")/8");
                                }
                            }
                        }
                    }
                    setDataWorkerInScheduleModel(book, empresa, listaEmpleado);
                    passWeekendToSheets(book);
                    writeFile(book, file);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                inputStream1.close();
                inputStream2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataWorkerInScheduleModel(XSSFWorkbook book, Empresa empresa, Empleado empleado) {

        for (int i = 1; i < 13; i++) {
            //set name empresa
            if(empresa.getNombre().contains("Palobiofarma")){
                String [] nombre = empresa.getNombre().split(" ");
                String nombre_empresa = String.join(" ",nombre[0],nombre[1]);
                book.getSheetAt(i).getRow(7).getCell(2).setCellValue(nombre_empresa);
            }
            else{
                book.getSheetAt(i).getRow(7).getCell(2).setCellValue(empresa.getNombre());
            }

            //set CIF empresa
            book.getSheetAt(i).getRow(8).getCell(2).setCellValue(empresa.getNif());

            //set Centro de trabajo
            book.getSheetAt(i).getRow(9).getCell(2).setCellValue(empresa.getCentro_de_trabajo());

            book.getSheetAt(i).getRow(10).getCell(2).setCellValue(empresa.getC_c_c());

            //empleado
            String employeeFullName = formatEmployeeName(empleado);
            book.getSheetAt(i).getRow(7).getCell(5).setCellValue(employeeFullName);

            //set CIF empresa
            book.getSheetAt(i).getRow(8).getCell(5).setCellValue(empleado.getNif());

            //set Centro de trabajo
            book.getSheetAt(i).getRow(9).getCell(5).setCellValue(empleado.getNumero_afiliacion());
        }
    }

    private String formatEmployeeName(Empleado employee){

        return employee.getNombre() +" "+employee.getPrimer_apellido();
    }


}
