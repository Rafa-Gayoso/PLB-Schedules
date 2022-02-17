package dao.implementation;

import dao.interfaces.Dao;
import model.Usuario;
import services.ServicesLocator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoImpl implements Dao<Usuario> {
    @Override
    public List<Usuario> getEntities() {
        return null;
    }

    @Override
    public void insertEntity(Usuario entity) {
        try(Connection connection = ServicesLocator.getConnection()) {
            String query = "insert into usuario(nombre_usuario,contrasenna,rol_id) values(?,?,?)";
            PreparedStatement prepared = createPrepareStatement(entity, connection, query);
            prepared.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEntity(Usuario entity) {
        try(Connection connection = ServicesLocator.getConnection()) {
            String query = "update usuario set nombre_empleado = ?, primer_apellido = ?, segundo_apellido = ?, nif =?," +
                    " numero_afiliacion = ?, horas_laborables = ?, cod_empresa =? " +
                    "where cod_empleado = ?";
            PreparedStatement prepared =createPrepareStatement(entity, connection, query);
            prepared.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEntity(Usuario entity) {
        try(Connection connection = ServicesLocator.getConnection()) {
            String query = "delete from empleado where cod_empleado = ?";
            PreparedStatement prepare = connection.prepareStatement(query);
            prepare.setInt(1,entity.getUsarioId());
            prepare.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Usuario getEntityById(int id) {
        return null;
    }

    private PreparedStatement createPrepareStatement(Usuario usuario, Connection connection, String consulta) throws SQLException {
        PreparedStatement prepared = connection.prepareStatement(consulta);//para consultas
        prepared.setString(1, usuario.getUsername());
        prepared.setString(2, usuario.getPassword());
        prepared.setInt(3,usuario.getRol());
        return prepared;
    }

    public Usuario getUsuarioByUsernameAndPassword(String username, String password){
        Usuario usuario = null;
        try(Connection connection = ServicesLocator.getConnection()) {
            String consulta = "Select usuario.* from usuario where usuario.nombre_usuario = ? and usuario.contrasenna = ?";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.setString(1, username);
            prepare.setString(2, password);
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                usuario = new Usuario(result.getInt(1), result.getString(2),
                        result.getString(3),result.getInt(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }
}
