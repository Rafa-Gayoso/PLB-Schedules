package services;

import javafx.concurrent.Task;
import model.Empleado;
import utils.VacationType;
import utils.VacationsController;

import java.util.ArrayList;
import java.util.Map;

public class GetVacationsTask extends Task<ArrayList<Map<String, ArrayList<VacationType>>>> {


    private ArrayList<Empleado> employees;

    public GetVacationsTask(ArrayList<Empleado> employees){
        this.employees = employees;

    }

    @Override
    protected ArrayList<Map<String, ArrayList<VacationType>>> call(){
        return VacationsController.getVacationsDaysEmployees(employees);
    }
}
