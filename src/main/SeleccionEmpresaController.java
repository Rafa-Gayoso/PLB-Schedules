package main;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SeleccionEmpresaController implements Initializable {

    @FXML
    private JFXComboBox<String> empresaComboBox;

    private ArrayList<FileInputStream> listFiles;
    private ArrayList<Empresa> listaEmpresas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listFiles = new ArrayList<>();
        listaEmpresas = ServicesLocator.getEmpresa().listadoEmpresas();
        ArrayList<String> nombresEmpresas = new ArrayList<>();
        for (Empresa empresa: listaEmpresas) {
            nombresEmpresas.add(empresa.getNombre());
        }
        empresaComboBox.setItems(FXCollections.observableArrayList(nombresEmpresas));
    }

    public void mergeExcel(javafx.event.ActionEvent actionEvent) throws IOException {
        ArrayList<Empleado> lista = ServicesLocator.getEmpleado().listadoEmpleadosXEmpresa(listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getNombre());
        System.out.println(lista.size());

            listFiles = new ArrayList<>();
            File inputStream1 = new File("2021.xlsx");
            FileInputStream inputStream2 = new FileInputStream("Horary Model.xlsx");

            //listFiles.add(inputStream1);
            listFiles.add(inputStream2);
            //list.add(inputStream2);
            if (listFiles.size() < 2) {
                System.out.println("ERROR");
            }

                //Controller.mergeExcelFiles(lista, listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()), inputStream1);


        /*for (int i = 0; i < lista.size(); i++) {
            System.out.println("i: " + i);
            Controller.mergeExcelFiles(new File(empresaComboBox.getSelectionModel().getSelectedItem().getNombre() + lista.get(i).getNombre()  + ".xlsx"), listFiles);
        }*/
    }
}
