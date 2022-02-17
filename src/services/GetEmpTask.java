package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import dao.implementation.EmpleadoDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.Empleado;
import services.ServicesLocator;

public class GetEmpTask extends Task<ArrayList<Empleado>> {
 
    @Override
    protected ArrayList<Empleado> call(){
        ArrayList<Empleado> empData = new ArrayList<>();
          int progress =0;

        int totalProgress = new EmpleadoDaoImpl().getEmployeesCount();
        updateProgress(progress, totalProgress);
        updateMessage("Cargando datos de trabajadores ("+(progress+1)+"/"+totalProgress+")");
        try(Connection connection = ServicesLocator.getConnection()) {
            PreparedStatement prepare = connection.prepareStatement("Select empleado.* from empleado");
            prepare.execute();
            //para consultas

            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas

                int cod = Objects.nonNull(result.getInt(10)) ? result.getInt(10) : 0;
                Empleado employee = new Empleado(result.getInt(1), result.getString(2),
                        result.getString(3),result.getString(4),result.getString(5),
                        result.getString(6),result.getInt(7),result.getInt(8),
                        result.getString(9),cod, result.getString(11));
                empData.add(employee);
                progress++;
                updateProgress(progress,totalProgress);
                updateMessage("Cargando datos de trabajadores ("+(progress+1)+"/"+totalProgress+")");
            }
            updateProgress(progress,totalProgress);
            updateMessage("Cargando datos de trabajadores ("+(progress+1)+"/"+totalProgress+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return empData;
    }
}
