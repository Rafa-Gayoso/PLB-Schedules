package services;

import javafx.concurrent.Task;
import model.Empleado;
import utils.VacationsController;

import java.util.ArrayList;
import java.util.Map;

public class GetVacationsTask extends Task<ArrayList<Map<String, ArrayList<String>>>> {


    private ArrayList<Empleado> employees;

    public GetVacationsTask(ArrayList<Empleado> employees){
        this.employees = employees;

    }

    @Override
    protected ArrayList<Map<String, ArrayList<String>>>call(){
        return VacationsController.getVacationsDaysEmployees(employees);
        /*ArrayList<Map<String, ArrayList<String>>> vacations = new ArrayList<>();
        int progress =0;
        int totalProgress = employees.size();
        updateProgress(progress, totalProgress);
        updateMessage("Cargando datos de trabajadores ("+(progress+1)+"/"+totalProgress+")");
        for(Empleado employee : employees){
            Map<String, ArrayList<String>> vacationsMap = VacationsController.getVacationsDays(employee);
            vacations.add(vacationsMap);
        }
        updateProgress(progress,totalProgress);
        updateMessage("Cargando datos de trabajadores ("+(progress+1)+"/"+totalProgress+")");
        VacationsReport.exportEmployees(vacations, employees);
        return vacations;*/
    }
}
