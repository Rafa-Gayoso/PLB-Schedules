package services;

import model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmpleadoServices {

    public ArrayList<Empleado> listadoEmpleadosXEmpresa(String nombreEmpresa) {
        ArrayList<Empleado> list = new ArrayList<>();

        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "Select empleado.* from empleado join empresa on empleado.cod_empresa = empresa.cod_empresa where empresa.nombre_empresa = " + "'" + nombreEmpresa + "'";
            createStatementEnterprise(list, connection, consulta);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void createStatementEnterprise(ArrayList<Empleado> list, Connection connection, String consulta) throws SQLException {
        PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
        prepare.execute();
        ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
        while (result.next()){ //para varias filas
            Empleado a = new Empleado(result.getInt(1), result.getString(2),
                    result.getString(3),result.getString(4),result.getString(5),
                    result.getString(6),result.getInt(7),result.getInt(8));
            list.add(a);
        }

        connection.close();
    }


    public ArrayList<Empleado> listadoEmpleadosModelo(){
        ArrayList<Empleado> list = new ArrayList<>();

        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "Select empleado.* from empleado";
            createStatementEnterprise(list, connection, consulta);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertEmployee(Empleado empleado){
        try {
            Connection connection = ServicesLocator.getConnection();
            String query = "insert into empleado(nombre_empleado,primer_apellido,segundo_apellido,nif,numero_afiliacion,horas_laborables,cod_empresa) values(?,?,?,?,?,?,?)";
            PreparedStatement prepared = createPrepareStatement(empleado, connection, query);
            prepared.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PreparedStatement createPrepareStatement(Empleado empleado, Connection connection, String consulta) throws SQLException {
        PreparedStatement prepared = connection.prepareStatement(consulta);//para consultas
        prepared.setString(1,empleado.getNombre());
        prepared.setString(2,empleado.getPrimer_apellido());
        prepared.setString(3,empleado.getSegundo_apellido());
        prepared.setString(4,empleado.getNif());
        prepared.setString(5,empleado.getNumero_afiliacion());
        prepared.setInt(6,empleado.getHoras_laborables());
        prepared.setInt(7,empleado.getCod_empresa());
        return prepared;
    }

    public void updateEmployee(Empleado empleado){
        try {
            Connection connection = ServicesLocator.getConnection();
            String query = "update empleado set nombre_empleado = ?, primer_apellido = ?, segundo_apellido = ?, nif =?," +
                    " numero_afiliacion = ?, horas_laborables = ?, cod_empresa =? " +
                    "where cod_empleado = ?";
            PreparedStatement prepared =createPrepareStatement(empleado, connection, query);
            prepared.setInt(8,empleado.getCod_empleado());
            prepared.execute();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmployee(Empleado empleado){
        try {
            Connection connection = ServicesLocator.getConnection();
            String query = "delete from empleado where cod_empleado = ?";
            PreparedStatement prepare = connection.prepareStatement(query);
            prepare.setInt(1,empleado.getCod_empleado());
            prepare.execute();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
