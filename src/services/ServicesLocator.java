package services;

import database.DatabaseConnection;

import java.sql.Connection;

public class ServicesLocator {

	private static EmpleadoServices empleado;
	private static EmpresaServices empresa;

	public static EmpleadoServices getEmployee(){
		if (empleado == null){
			empleado = new EmpleadoServices();
		}
		return empleado;
	}
	public static EmpresaServices getEnterprise(){
		if (empresa == null){
			empresa = new EmpresaServices();
		}
		return empresa;
	}


	public static Connection getConnection(){
		DatabaseConnection connection = new DatabaseConnection();
		return connection.getConnection();
	}
}
