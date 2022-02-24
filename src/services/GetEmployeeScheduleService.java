package services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Empleado;

import java.util.ArrayList;
import java.util.Map;

public class GetEmployeeScheduleService extends Service<Void> {

    private Empleado employee;

    public GetEmployeeScheduleService(Empleado employee){
        this.employee = employee;

    }

    @Override
    protected Task createTask() {
        return new GetEmployeeScheduleTask(employee);
    }
}
