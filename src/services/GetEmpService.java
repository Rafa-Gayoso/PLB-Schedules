package services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Empleado;

import java.util.ArrayList;


public class GetEmpService extends Service<ArrayList<Empleado>> {

	    /**
	     * Create and return the task for fetching the data. Note that this method
	     * is called on the background thread (all other code in this application is
	     * on the JavaFX Application Thread!).
	     */
	    @Override
	    protected Task createTask() {
	        return new GetEmpTask();
	    }
	}
