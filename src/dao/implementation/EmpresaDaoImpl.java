package dao.implementation;

import dao.interfaces.Dao;
import model.Empresa;
import services.ServicesLocator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDaoImpl implements Dao<Empresa> {

    @Override
    public List<Empresa> getEntities() {
        ArrayList<Empresa> lista= new ArrayList<>();
        try(Connection connection = ServicesLocator.getConnection()) {
            String consulta = "Select empresa.* from empresa";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                Empresa a = new Empresa(result.getInt(1), result.getString(2),
                        result.getString(3),result.getString(4),result.getString(5));
                lista.add(a);
            }

        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void insertEntity(Empresa entity) {

    }

    @Override
    public void updateEntity(Empresa entity) {

    }

    @Override
    public void deleteEntity(Empresa entity) {

    }

    @Override
    public Empresa getEntityById(int id) {
        Empresa empresa = null;
        try(Connection connection = ServicesLocator.getConnection()) {
            String consulta = "Select empresa.* from empresa where empresa.cod_empresa = ?";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.setInt(1,id);
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                empresa = new Empresa(result.getInt(1),result.getString(2),result.getString(3),
                        result.getString(4),result.getString(5));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return empresa;
    }

    public String getEmpresaNombreByCod(int cod){
        String empresa = null;
        try(Connection connection = ServicesLocator.getConnection()) {
            String consulta = "Select empresa.nombre_empresa from empresa where empresa.cod_empresa = ?";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.setInt(1,cod);
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                empresa = result.getString(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return empresa;
    }

    public int getEmpresaCodByName(String nombre){
        int empresa = -1;
        try(Connection connection = ServicesLocator.getConnection()) {
            String consulta = "Select empresa.cod_empresa from empresa where empresa.nombre_empresa LIKE ?";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.setString(1,"%"+nombre+"%");
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                empresa = result.getInt(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return empresa;
    }

    public ArrayList<String> nombreEmpresas(){
        ArrayList<String> lista= new ArrayList<>();
        try(Connection connection = ServicesLocator.getConnection()) {
            String consulta = "Select empresa.nombre_empresa from empresa";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                lista.add(result.getString(1));
            }

        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista;
    }
}
