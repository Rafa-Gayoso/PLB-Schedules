package services;

import model.Empresa;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmpresaServices {
    @SuppressWarnings("unused")
    public ArrayList<Empresa> listadoEmpresas(){
        ArrayList<Empresa> lista= new ArrayList<>();
        try{
            Connection conexion = ServicesLocator.getConnection();
            String consulta = "Select empresa.* from empresa";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                Empresa a = new Empresa(result.getInt(1), result.getString(2),
                        result.getString(3),result.getString(4),result.getString(5));
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
    public Empresa getEmpresaByCod(int cod){
        Empresa empresa = null;
        try{
            Connection conexion = ServicesLocator.getConnection();
            String consulta = "Select empresa.* from empresa where empresa.cod_empresa = ?";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.setInt(1,cod);
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                empresa = new Empresa(result.getInt(1),result.getString(2),result.getString(3),
                        result.getString(4),result.getString(5));
            }
            conexion.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return empresa;
    }


    public String getEmpresaNombreByCod(int cod){
        String empresa = null;
        try{
            Connection conexion = ServicesLocator.getConnection();
            String consulta = "Select empresa.nombre_empresa from empresa where empresa.cod_empresa = ?";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.setInt(1,cod);
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
               empresa = result.getString(1);
            }
            conexion.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return empresa;
    }

    public int getEmpresaCodByName(String nombre){
        int empresa = -1;
        try{
            Connection conexion = ServicesLocator.getConnection();
            String consulta = "Select empresa.cod_empresa from empresa where empresa.nombre_empresa LIKE ?";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.setString(1,"%"+nombre+"%");
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                empresa = result.getInt(1);
            }
            conexion.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return empresa;
    }

    public ArrayList<String> nombreEmpresas(){
        ArrayList<String> lista= new ArrayList<>();
        try{
            Connection conexion = ServicesLocator.getConnection();
            String consulta = "Select empresa.nombre_empresa from empresa";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                lista.add(result.getString(1));
            }
            conexion.close();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista;
    }
}
