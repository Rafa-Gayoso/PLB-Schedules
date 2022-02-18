package dao.implementation;

import dao.interfaces.Dao;
import model.Empleado;
import services.ServicesLocator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmpleadoDaoImpl implements Dao<Empleado> {

    @Override
    public List<Empleado> getEntities() {
        ArrayList<Empleado> list = new ArrayList<>();

        try(Connection connection = ServicesLocator.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("Select * from empleado");
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()){
                int cod = Objects.nonNull(resultSet.getInt(10)) ? resultSet.getInt(10) : 0;
                Empleado employee = new Empleado(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),
                        resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),
                        resultSet.getString(9), cod, resultSet.getString(11));
                list.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void insertEntity(Empleado entity) {


        try(Connection connection = ServicesLocator.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO empleado(nombre_empleado,primer_apellido,segundo_apellido," +
                    "nif,numero_afiliacion,horas_laborables,cod_empresa,direccion_cronograma,cod_usuario) VALUES(?,?,?,?,?,?,?,?,?)");
            statement.setString(1,entity.getNombre());
            statement.setString(2,entity.getPrimer_apellido());
            statement.setString(3,entity.getSegundo_apellido());
            statement.setString(4,entity.getNif());
            statement.setString(5,entity.getNumero_afiliacion());
            statement.setInt(6,entity.getHoras_laborables());
            statement.setInt(7,entity.getCod_empresa());
            statement.setString(8,"");
            statement.setInt(9,entity.getCodUsuario());
            statement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void updateEntity(Empleado entity) {
        try(Connection connection = ServicesLocator.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("update empleado set nombre_empleado = ?, primer_apellido = ?, segundo_apellido = ?, nif =?, " +
                    "numero_afiliacion = ?, horas_laborables = ?, cod_empresa =?, cod_usuario = ? where cod_empleado = ?");
            statement.setString(1,entity.getNombre());
            statement.setString(2,entity.getPrimer_apellido());
            statement.setString(3,entity.getSegundo_apellido());
            statement.setString(4,entity.getNif());
            statement.setString(5,entity.getNumero_afiliacion());
            statement.setInt(6,entity.getHoras_laborables());
            statement.setInt(7,entity.getCod_empresa());
            statement.setInt(8,entity.getCodUsuario());
            statement.setInt(9,entity.getCod_empleado());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEntity(Empleado entity) {
        try(Connection connection = ServicesLocator.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM empleado WHERE cod_empleado = ?");
            statement.setInt(1,entity.getCod_empleado());
            statement.execute();
            statement = connection.prepareStatement("DELETE FROM usuario WHERE usuario_id = ?");
            statement.setInt(1,entity.getCodUsuario());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Empleado getEntityById(int id) {
        Empleado employee = null;

        try(Connection connection = ServicesLocator.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("Select * from empleado WHERE cod_empelado = ?");
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()){
                int cod = Objects.nonNull(resultSet.getInt(10)) ? resultSet.getInt(10) : 0;
                employee = new Empleado(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),
                        resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),
                        resultSet.getString(9),cod,resultSet.getString(11));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employee;
    }

    public Empleado getExistEmployeeByNif(String nif){
        Empleado employee = null;
        try(Connection connection = ServicesLocator.getConnection()) {

            PreparedStatement prepare = connection.prepareStatement("Select * from empleado where nif = ?");//para consultas
            prepare.setString(1, nif);
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                employee = new Empleado(result.getInt(1), result.getString(2),
                        result.getString(3),result.getString(4),result.getString(5),
                        result.getString(6),result.getInt(7),result.getInt(8),
                        result.getString(9),result.getInt(10),result.getString(11));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public int getEmployeesCount(){
       int count = 0;

        try(Connection connection = ServicesLocator.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("Select Count(empleado.cod_empleado) from empleado");
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()){
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
}
