package utils;

import controller.LoginController;
import javafx.application.Platform;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import model.TableExcelModel;

public class CustomCell<T> extends TableCell<TableExcelModel, String> {

    private final String WEEKEND_COLOR = "#FFFFCC";
    private final String AUTONOMIC_COLOR = "#92D050";
    private final String LOCAL_COLOR = "#00B0F0";
    private final String NATIONAL_COLOR = "#FF0000";

    TextField textField = new TextField();
    Text text = new Text();

    public CustomCell() {
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                commitEdit(textField.getText());
            }
        });

        textField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                System.out.println("Textfield on focus");
            } else {
                commitEdit(textField.getText());
            }
        });
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.setText(text.getText());
            Platform.runLater(() -> textField.requestFocus());
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText("");
            setStyle("");
        } else {
            if (item.equalsIgnoreCase("FIN DE SEMANA")) {
                setStyle("-fx-background-color:" + WEEKEND_COLOR);
                setEditable(false);
            } else if (item.equalsIgnoreCase("FESTIVO AUTONÓMICO")) {

                setStyle("-fx-background-color:" + AUTONOMIC_COLOR);
                setEditable(false);
            } else if (item.equalsIgnoreCase("FESTIVO LOCAL")) {

                setStyle("-fx-background-color:" + LOCAL_COLOR);
                setEditable(false);
            } else if (item.equalsIgnoreCase("FESTIVO NACIONAL")) {
                setStyle("-fx-background-color:" + NATIONAL_COLOR);
                setEditable(false);
            }
            else {
                setStyle("");
                setEditable(true);
            }

            text.setText(item);
            setGraphic(text);


        }
    }

    public void setStyleToCell(){
        setStyle("-fx-background-color: red");
    }

}
