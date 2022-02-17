package utils;

import model.Empleado;

public class FormatEmployeeName {

    public static String formatEmployeeName(Empleado employee){
        return employee.getNombre() + " " + employee.getPrimer_apellido();
    }

    public static String getEmployeesFileName(Empleado employee){
        return formatEmployeeName(employee) + ".xlsx";
    }
}
