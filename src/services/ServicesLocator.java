package services;

import database.DatabaseConnection;

import java.sql.Connection;

public class ServicesLocator {


	private static UsuarioServices usuario;


	public static UsuarioServices getUsuario(){
		if (usuario == null){
			usuario = new UsuarioServices();
		}
		return usuario;
	}

	public static Connection getConnection(){
		DatabaseConnection connection = new DatabaseConnection();
		return connection.getConnection();
	}
}
