package noko;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ConfirmPassController implements Initializable {

    @FXML
    private HBox topBar;

    @FXML
    private Button closeBtn;

    @FXML
    private GridPane gridPaneConf;

    @FXML
    private TextField passTfield;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView showHiddenPass;

    @FXML
    private Label passErrLbl;

    @FXML
    private Button confirmBtn;

    @FXML
    private Button cancelBtn;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            ProfileController.popup.setX(event.getScreenX() - xOffSet);
            ProfileController.popup.setY(event.getScreenY() - yOffSet);
        });
    }
    
    private void setBtnHandler() {
        passTfield.setOnKeyPressed((event) -> {
            passPressed(event);
        });
        passwordField.setOnKeyPressed((event) -> {
            passPressed(event);
        });
        passTfield.textProperty().addListener((observable, oldValue, newValue) ->{
            passwordField.setText(passTfield.getText());
        });
        passwordField.textProperty().addListener((observable, oldValue, newValue) ->{
            passTfield.setText(passwordField.getText());
        });
        closeBtn.setOnMouseClicked((event) -> {
            ProfileController.closePopup();
        });
        cancelBtn.setOnMouseClicked((event) -> {
            ProfileController.closePopup();
        });
        confirmBtn.setOnMouseClicked((event) -> {
            onConfirmClicked();
        });
        showHiddenPass.setOnMouseClicked((event) -> {
            if (showPass) {
                showPass = false;
                showHiddenPass.setImage(showToggle);
                passTfield.setVisible(false);
                passwordField.setVisible(true);
                replacePassField();
            } else {
                showPass = true;
                showHiddenPass.setImage(hiddenToggle);
                passwordField.setVisible(false);
                passTfield.setVisible(true);
                replacePassField();
            }
        });
    }

    private void passPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            onConfirmClicked();
        }
        normalTextField(passTfield);
        normalTextField(passwordField);
        passErrLbl.setVisible(false);
    }

    private void onConfirmClicked() {
        if (passTfield.getText().equals(PrimaryController.account.getPass())) {
            ProfileController.confirmed = true;
            ProfileController.closePopup();
        } else {
            redTextFieldLogin(passTfield);
            redTextFieldLogin(passwordField);
            passErrLbl.setVisible(true);
        }
    }

    private void redTextFieldLogin(TextField textField){
        textField.getStyleClass().setAll("textfield-login-red");
        textField.setPrefHeight(50);
    }

    private void normalTextField(TextField textField){
        textField.getStyleClass().setAll("textfield-login");
        textField.setPrefHeight(50);
    }
    

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setToggleShowHidden();
        setPassTfield();
        passErrLbl.setVisible(false);
        makeStageDragable();
        setBtnHandler();
    }

    private boolean showPass;

    private void setPassTfield() {
        showPass = false;
        passTfield.setVisible(false);
        replacePassField();
    }

    private void replacePassField() {
        gridPaneConf.getChildren().clear();
        if (showPass) {
            gridPaneConf.add(passTfield, 0, 1);
        } else {
            gridPaneConf.add(passwordField, 0, 1);
        }
        gridPaneConf.add(showHiddenPass, 1, 1);
    }

    Image hiddenToggle, showToggle;

    private void setToggleShowHidden() {
        hiddenToggle = new Image(App.class.getResourceAsStream("icon/pass_hide_ic.png"));
        showToggle = new Image(App.class.getResourceAsStream("icon/pass_show_ic.png"));
    }

}
