package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.implementation.EmpleadoDaoImpl;
import dao.implementation.EmpresaDaoImpl;
import dao.implementation.UserDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Empleado;
import model.Empresa;
import model.Usuario;
import utils.AESCypher;
import utils.SMBUtils;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddEmployeeDialogController /*implements Initializable*/ {

    private final String ADDRESS = "config_files" + File.separator + "Horarios" + File.separator;
    private final String PIC_DIR = "config_files" + File.separator + "Employees";

    private EmpresaDaoImpl empresaDao;
    private EmpleadoDaoImpl dao;

    private File file;

    @FXML
    private JFXTextField nombreTextField;

    @FXML
    private JFXButton btnPhoto;


    @FXML
    private JFXTextField primApellidoTextField;

    @FXML
    private JFXTextField segApellidoTextfield;

    @FXML
    private JFXTextField nifTextfield;

    @FXML
    private JFXTextField numTextfield;

    @FXML
    private JFXButton btnInsert;

    @FXML
    private TextField horasLaborables;

    @FXML
    private JFXComboBox<String> comboEmpresa;


    @FXML
    private JFXTextField email;

    @FXML
    private GridPane grid;

    private UserDaoImpl userDao;


    public void setData(Empleado employee, GridPane grid){
        this.grid = grid;
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        userDao = new UserDaoImpl();
        empresaDao = new EmpresaDaoImpl();
        ObservableList<String> empresas = FXCollections.observableArrayList(
                empresaDao.getEntities().stream().map(Empresa::getNombre).collect(Collectors.toList())
        );
        comboEmpresa.setItems(empresas);
        email.setText(null);

        horasLaborables.setText("8");
        horasLaborables.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[4-8]*$")) ? change : null));

        setValidator(nombreTextField,requiredFieldValidator);

        setValidator(primApellidoTextField,requiredFieldValidator);

        setValidator(email,requiredFieldValidator);

        setValidator(nifTextfield,requiredFieldValidator);

        setValidator(numTextfield,requiredFieldValidator);

        setValidator(comboEmpresa,requiredFieldValidator);
        requiredFieldValidator.setMessage("Campo Requerido");

        btnInsert.setOnAction(this::insertEmployee);

        btnInsert.disableProperty().bind((
                nombreTextField.textProperty().isNotEmpty()
                        .and(primApellidoTextField.textProperty().isNotEmpty())
                        .and(email.textProperty().isNotEmpty())
                        .and(nifTextfield.textProperty().isNotEmpty())
                        .and(numTextfield.textProperty().isNotEmpty())
        ).not());

        if(!Objects.isNull(employee)){
            nombreTextField.setText(employee.getNombre());
            primApellidoTextField.setText(employee.getPrimer_apellido());
            segApellidoTextfield.setText(employee.getSegundo_apellido());
            email.setText(employee.getEmail());
            nifTextfield.setText(employee.getNif());
            numTextfield.setText(employee.getNumero_afiliacion());
            horasLaborables.setText(String.valueOf(employee.getHoras_laborables()));
            comboEmpresa.getSelectionModel().select(employee.getNombre_empresa());
            btnInsert.setOnAction(event -> updateEmployee(employee));
        }

        btnPhoto.setOnAction(event ->{
            Stage stage = new Stage();
            FileChooser fc = new FileChooser();

            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
            file = fc.showOpenDialog(stage);

            if (file == null) {
                file = new File("/resources/images/profile.png");
            }
        });


    }

    private void setValidator(JFXTextField textField, RequiredFieldValidator validator){
        textField.getValidators().add(validator);
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                textField.validate();
            }
        });
    }

    private void setValidator(JFXComboBox jfxComboBox, RequiredFieldValidator validator){
        jfxComboBox.getValidators().add(validator);
        jfxComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                jfxComboBox.validate();
            }
        });
    }

    private void insertEmployee(ActionEvent event) {
        boolean validated = validateData();
        Empleado employee = dao.getExistEmployeeByNif(nifTextfield.getText());
        if (file == null){
            file = new File("/resources/images/profile.png");
        }
        if (!validated) {

        } else if(!Objects.isNull(employee)){

        } else{
            try{


                employee = new Empleado();
                setEmployeeData(employee);
                SMBUtils.uploadPhoto(employee.getNombre()+".png",file.getAbsolutePath());
                AESCypher aesCypher = new AESCypher();

                String encryptedPassword = aesCypher.encrypt(employee.getNif());
                userDao.insertEntity(new Usuario(employee.getNombre(), encryptedPassword, 2));
                int codUsuario = userDao.getUsuarioByUsernameAndPassword(employee.getNombre(), encryptedPassword).getUsarioId();
                employee.setCodUsuario(codUsuario);

                dao.insertEntity(employee);

                employee.setCod_empleado(dao.getExistEmployeeByNif(employee.getNif()).getCod_empleado());

                LoginController.getEmployees().add(employee);

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/Employee.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                EmployeeController itemController = fxmlLoader.getController();
                itemController.setData(employee);

                int row = getRowCount(grid)-1;
                int column = getColumnCount(grid)-1;
                if(column == 3) column = 0;
                grid.add(anchorPane, column, row);
                GridPane.setMargin(anchorPane, new Insets(20));

                closeStage();
            }catch(Exception e){

            }
        }
    }

    private void updateEmployee(Empleado employee) {
        boolean validated = validateData();

        if (!validated) {

        }  else{
                setEmployeeData(employee);
                dao.updateEntity(employee);
                closeStage();
        }
    }

    private void setEmployeeData(Empleado empleado) {
        empleado.setNombre(nombreTextField.getText());
        empleado.setPrimer_apellido(primApellidoTextField.getText());
        empleado.setSegundo_apellido(segApellidoTextfield.getText());
        empleado.setNif(nifTextfield.getText());
        empleado.setNumero_afiliacion(numTextfield.getText());
        int cod_empresa = empresaDao.getEmpresaCodByName(comboEmpresa.getSelectionModel().getSelectedItem());
        empleado.setCod_empresa(cod_empresa);
        empleado.setNombre_empresa(comboEmpresa.getSelectionModel().getSelectedItem());
        empleado.setHoras_laborables(Integer.parseInt(horasLaborables.getText()));
        empleado.setEmail(email.getText());
        empleado.setDireccionCronograma(ADDRESS + empleado.getNombre_empresa());

        empleado.setVacations(LoginController.getEmployees().stream().filter(e ->
                e.getNombre_empresa().equals(empleado.getNombre_empresa())).findFirst().get().getVacations());
    }

    private boolean validateData() {
        boolean validated = true;
        if (comboEmpresa.getSelectionModel().getSelectedIndex() == -1) {
            validated = false;
        }
        return validated;
    }

    public void setDao(EmpleadoDaoImpl dao){
        this.dao = dao;
    }

    private void closeStage() {

        Stage stage  = (Stage) btnInsert.getScene().getWindow();
        stage.close();
    }

    @FXML
    void closeModal(KeyEvent event) {
        if(event.getCode() == KeyCode.ESCAPE){
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        }

    }

    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }

    private int getColumnCount(GridPane pane) {
        int numRows = pane.getColumnConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getColumnIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }
}
