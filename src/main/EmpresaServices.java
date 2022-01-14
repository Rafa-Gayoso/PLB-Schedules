package main;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmpresaServices {

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
            String consulta = "Select empresa.cod_empresa from empresa where empresa.nombre_empresa = ?";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.setString(1,nombre);
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


    public void readPicture(int materialId, String filename) {
        // update sql
        String selectSQL = "SELECT logo FROM empresa WHERE cod_empresa=?";
        ResultSet rs = null;
        FileOutputStream fos = null;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ServicesLocator.getConnection();
            pstmt = conn.prepareStatement(selectSQL);
            pstmt.setInt(1, materialId);
            rs = pstmt.executeQuery();

            // write binary stream into file
            File file = new File(filename);
            fos = new FileOutputStream(file);

            System.out.println("Writing BLOB to file " + file.getAbsolutePath());
            while (rs.next()) {
                InputStream input = rs.getBinaryStream("logo");
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    fos.write(buffer);
                }
            }
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
                if (fos != null) {
                    fos.close();
                }

            } catch (SQLException | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private byte[] readFile(String file) {
        ByteArrayOutputStream bos = null;
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }

    public void updatePicture(int materialId, String filename) {
        // update sql
        String updateSQL = "UPDATE materials "
                + "SET picture = ? "
                + "WHERE id=?";

        try (Connection conn = ServicesLocator.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            // set parameters
            pstmt.setBytes(1, readFile(filename));
            pstmt.setInt(2, materialId);

            pstmt.executeUpdate();
            System.out.println("Stored the file in the BLOB column.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
