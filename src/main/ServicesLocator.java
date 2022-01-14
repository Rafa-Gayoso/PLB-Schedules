package main;

public class ServicesLocator {

	private static EmpleadoServices empleado;
	private static EmpresaServices empresa;

	



	public static EmpleadoServices getEmpleado(){
		if (empleado == null){
			empleado = new EmpleadoServices();
		}
		return empleado;
	}
	public static EmpresaServices getEmpresa(){
		if (empresa == null){
			empresa = new EmpresaServices();
		}
		return empresa;
	}


	//metodo q crea la conexion con la base de datos.
	public static java.sql.Connection getConnection(){
		Connection connection = null;
		connection = new Connection();
		return connection.getConnection();
	}
}
