package controller;

import dao.implementation.EmpleadoDaoImpl;
import dao.implementation.EmpresaDaoImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.Empleado;
import model.Empresa;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EmployeesBoardController implements Initializable {

    @FXML
    private GridPane employeesGrid;

    @FXML
    private PieChart pieChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EmpleadoDaoImpl dao = new EmpleadoDaoImpl();
        EmpresaDaoImpl empresaDao = new EmpresaDaoImpl();
        List<Empleado> employees = dao.getEntities();
        List<String> enterprisesNames = empresaDao.nombreEmpresas();
        for(String name : enterprisesNames){
            PieChart.Data slice1 = new PieChart.Data(name, employees.stream().filter(e -> e.getNombre_empresa().
                    equals(name)).collect(Collectors.toList()).size());
            pieChart.getData().add(slice1);
        }

        int column =0;
        int row = 1;
        try{
            for (Empleado employee : employees){
                FXMLLoader fxml = new FXMLLoader();
                fxml.setLocation(getClass().getResource("/resources/fxml/Employee.fxml"));
                Pane pane = fxml.load();
                EmployeeController employeeController = new EmployeeController();
                employeeController.setData(employee);

                if (column == 2){
                    column =0;
                    ++row;
                }

                employeesGrid.add(pane, column++,row);
                GridPane.setMargin(pane, new Insets(20));
            }
        }catch(Exception e){
            e.printStackTrace();
        }





    }
}
