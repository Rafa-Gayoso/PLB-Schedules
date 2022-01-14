package services;

import model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmpleadoServices {

    public ArrayList<Empleado> listadoEmpleados(){
        ArrayList<Empleado> lista= new ArrayList<>();
        try{
            Connection conexion = ServicesLocator.getConnection();
            String consulta = "Select empleado.* from empleado";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                Empleado a = new Empleado(result.getInt(1), result.getString(2),
                        result.getString(3),result.getString(4),result.getString(5),
                        result.getString(6),result.getInt(7),result.getInt(8));
                lista.add(a);
            }
            conexion.close();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<Empleado> listadoEmpleadosXEmpresa(String nombreEmpresa) {
        ArrayList<Empleado> list = new ArrayList<>();

        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "Select empleado.* from empleado join empresa on empleado.cod_empresa = empresa.cod_empresa where empresa.nombre_empresa = " + "'" + nombreEmpresa + "'";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public ArrayList<Empleado> listadoEmpleadosModelo(){
        ArrayList<Empleado> list = new ArrayList<>();

        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "Select empleado.* from empleado";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertarEmpleado(Empleado empleado){
        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "insert into empleado(nombre_empleado,primer_apellido,segundo_apellido,nif,numero_afiliacion,horas_laborables,cod_empresa) values(?,?,?,?,?,?,?)";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.setString(1,empleado.getNombre());
            prepare.setString(2,empleado.getPrimer_apellido());
            prepare.setString(3,empleado.getSegundo_apellido());
            prepare.setString(4,empleado.getNif());
            prepare.setString(5,empleado.getNumero_afiliacion());
            prepare.setInt(6,empleado.getHoras_laborables());
            prepare.setInt(7,empleado.getCod_empresa());
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmpleado(Empleado empleado){
        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "update empleado set nombre_empleado = ?, primer_apellido = ?, segundo_apellido = ?, nif =?," +
                    " numero_afiliacion = ?, horas_laborables = ?, cod_empresa =? " +
                    "where cod_empleado = ?";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.setString(1,empleado.getNombre());
            prepare.setString(2,empleado.getPrimer_apellido());
            prepare.setString(3,empleado.getSegundo_apellido());
            prepare.setString(4,empleado.getNif());
            prepare.setString(5,empleado.getNumero_afiliacion());
            prepare.setInt(6,empleado.getHoras_laborables());
            prepare.setInt(7,empleado.getCod_empresa());
            prepare.setInt(8,empleado.getCod_empleado());
            prepare.execute();


            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmpleado(Empleado empleado){
        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "delete from empleado where cod_empleado = ?";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.setInt(1,empleado.getCod_empleado());
            prepare.execute();


            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
