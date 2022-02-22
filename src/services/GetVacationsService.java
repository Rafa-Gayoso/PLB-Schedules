package services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;
import model.Empleado;

import java.util.ArrayList;
import java.util.Map;

public class GetVacationsService extends Service<ArrayList<Map<String, ArrayList<String>>>> {
    private ArrayList<Empleado> employees;

    public GetVacationsService(ArrayList<Empleado> employees){
        this.employees = employees;
    }

    @Override
    protected Task createTask() {
        return new GetVacationsTask(employees);
    }
}
