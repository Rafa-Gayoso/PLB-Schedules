package controller;


import dao.implementation.EmpleadoDaoImpl;
import utils.FormatEmployeeName;
import utils.SMBUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import model.Empleado;
import model.Empresa;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppController {

    private final String CONFIG = "config_files";
    public void mergeExcelFiles(ArrayList<Empleado> listaEmpleados, Empresa empresa, ArrayList<File> files) {
        EmpleadoDaoImpl empleadoDao = new EmpleadoDaoImpl();
        ArrayList<String> cell_formulas = generateCellToFormula();

        try {
            File file;
            File dir = new File(CONFIG+"/"+empresa.getNombre());
            if(!dir.exists()){
                dir.mkdir();
            }
            for (Empleado empleado : listaEmpleados) {
                ArrayList<InputStream> list = new ArrayList<>();
                FileInputStream inputStream1 = new FileInputStream(files.get(0));
                String employeeFullName = FormatEmployeeName.formatEmployeeName(empleado);
                FileInputStream inputStream2 = new FileInputStream(files.get(1));

                list.add(inputStream1);
                list.add(inputStream2);
                XSSFWorkbook book = new XSSFWorkbook();
                XSSFSheet sheet;
                file = new File(dir.getAbsolutePath() + "/" + employeeFullName + ".xlsx");

                //FileInputStream obtains input bytes from the image file
                InputStream inputStream;
                if (empresa.getNombre().contains("Palobiofarma")) {
                    inputStream =  getClass().getResourceAsStream("/resources/images/palobiofarma.png");
                } else {
                    inputStream = getClass().getResourceAsStream("/resources/images/medibiofarma.png");
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
                int totalSheets = 0;
                for (InputStream fin : list) {
                    XSSFWorkbook b = new XSSFWorkbook(fin);
                    for (int i = 0; i < b.getNumberOfSheets(); i++) {
                        sheet = book.createSheet(b.getSheetName(i));
                        copySheets(book, sheet, b.getSheetAt(i));
                        totalSheets++;
                        if (book.getNumberOfSheets() == 1) {
                            calendar_year = book.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();
                            calendar_year = calendar_year.split(" ")[1];
                            location = book.getSheetAt(0).getRow(0).getCell(6).getStringCellValue();
                        }
                        if (totalSheets > 1) {
                            addEnterpriseLogoToSheet(book, sheet, pictureIdx);
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
                                        cell_formulas.get(totalSheets - 2) + "*" + empleado.getHoras_laborables() + ")/8");
                            }

                            cell = sheet.getRow(14).getCell(8);
                            if (totalSheets == 2 && cell != null) {
                                cell.setCellFormula("'" + book.getSheetAt(0).getSheetName() + "'!Z35-" + "(" +
                                        cell.getCellFormula() + ")/"+empleado.getHoras_laborables());
                            } else if (totalSheets > 2 && cell != null) {
                                cell.setCellFormula("'" + book.getSheetAt(totalSheets - 2).getSheetName() + "'!I15-" + "(" +
                                        cell.getCellFormula() + ")/"+empleado.getHoras_laborables());

                            }
                        }
                    }
                }
                FormulaEvaluator formulaEval = book.getCreationHelper().createFormulaEvaluator();
                setDataWorkerInScheduleModel(book, empresa, empleado);

                if(empleado.getHoras_laborables() < 8){
                    fixEmployeeWorkHours(book, empleado.getHoras_laborables());
                }
                CellValue c=formulaEval.evaluate(book.getSheetAt(0).getRow(34).getCell(25));
                int vacations = (int) c.getNumberValue();
                empleado.setVacations(vacations);
                empleadoDao.updateEntity(empleado);
                passWeekendToSheets(book, calendar_year);
                writeFile(book, file);
                SMBUtils.uploadFile(empresa.getNombre(), employeeFullName+".xlsx",dir.getAbsolutePath());
                inputStream1.close();
                inputStream2.close();
            }
            deleteDirectory(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region Private Methods
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
    private void copyRow(XSSFWorkbook newWorkbook,XSSFRow srcRow, XSSFRow destRow, Map<Integer, XSSFCellStyle> styleMap) {
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
    private void copyCell(XSSFWorkbook newWorkbook, XSSFCell oldCell, XSSFCell newCell, Map<Integer, XSSFCellStyle> styleMap) {
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
    private void writeFile(XSSFWorkbook book, File file) throws Exception {
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
    private void passWeekendToSheets(XSSFWorkbook workbook, String calendarYear) {
        XSSFSheet calendarSheet = workbook.getSheetAt(0);
        for (Row cells : calendarSheet) {
            XSSFRow row = (XSSFRow) cells;
            for (Cell value : row) {
                XSSFCell cell = (XSSFCell) value;
                if (cell.getCellType() != CellType.STRING && DateUtil.isCellDateFormatted(cell) ) {
                    Date date = cell.getDateCellValue();
                    if (date != null)
                        setStyleAndData(workbook, cell, date, calendarYear);
                }
            }
        }
    }
    private void setStyleAndData(XSSFWorkbook book,XSSFCell cell,Date date, String calendarYear){
        int month = date.getMonth() + 1;
        int day = date.getDate();
        if (date.getDay() == 0 || date.getDay() == 6 || !cell.getCellStyle().getFillForegroundColorColor().getARGBHex().equalsIgnoreCase("FFFFFFFF")) {
            passStyleToCell(book.getSheetAt(month), day, cell.getCellStyle(), month);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);
        book.getSheetAt(month).getRow(10).getCell(5).setCellValue(strDate.substring(0,6) + calendarYear);
    }
    private void passStyleToCell(XSSFSheet sheet, int day, CellStyle cellStyle, int month) {

        boolean match = false;
        int rowStart = 15;

        while (!match) {
            if (sheet.getRow(rowStart).getCell(1).getNumericCellValue() == day) {
                fixAdjacentCell(sheet.getRow(rowStart).getCell(1), sheet.getRow(rowStart), cellStyle);
                match = true;
            } else
                rowStart++;
        }
        if(month == 12 && day == 31){
            XSSFCellStyle style = sheet.getRow(38).getCell(1).getCellStyle();
            fixAdjacentCell(sheet.getRow(45).getCell(1), sheet.getRow(45), style);
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
    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
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
            String employeeFullName = FormatEmployeeName.formatEmployeeName(empleado);
            book.getSheetAt(i).getRow(7).getCell(5).setCellValue(employeeFullName);

            //set CIF empresa
            book.getSheetAt(i).getRow(8).getCell(5).setCellValue(empleado.getNif());

            //set Numero de afiliación
            book.getSheetAt(i).getRow(9).getCell(5).setCellValue(empleado.getNumero_afiliacion());
        }
    }
    private void fixEmployeeWorkHours(XSSFWorkbook book, int workHours){
        int cellNumber = 6;
        String originalFormula;
        String correctedFormula;
        for (int i = 1; i < 13; i++) {
            XSSFSheet currentSheet =  book.getSheetAt(i);
            //need to change the formula to calculate the remaining vacations day
            originalFormula = currentSheet.getRow(46).getCell(cellNumber).getCellFormula();
            correctedFormula =  originalFormula.replace(originalFormula.charAt(originalFormula.length()-1),Integer.toString(workHours).charAt(0));
            currentSheet.getRow(46).getCell(cellNumber).setCellFormula(correctedFormula);
        }
    }
    private void addEnterpriseLogoToSheet(XSSFWorkbook book, XSSFSheet sheet, int pictureIdx){
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
    }
    //endregion

}
