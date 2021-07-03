package noko;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController implements Initializable{

    @FXML
    private TextField userLoginTField;

    @FXML
    private TextField passLoginTField;

    @FXML
    private Button loginBtn;

    @FXML
    private Label createAccountLbl;

    @FXML
    private Button closeBtn;

    @FXML
    private Button supportBtn;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Label errorLoginLbl;

    @FXML
    private HBox topBar;

    @FXML
    private CheckBox staySignedCB;

    @FXML
    private Label versionLbl;

    @FXML
    private ImageView showHiddenPass;

    @FXML
    private GridPane loginGridPane;
    
    @FXML
    private PasswordField passwordField;

    @FXML
    private HBox userHbox;

    @FXML
    private HBox passHbox;

    static Stage popup;
    static boolean showPass;
    private boolean normalTField;
    private Image hiddenToggle, showToggle;

    private double xOffSet = 0;
    private double yOffSet = 0;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        normalTField = true;
        makeStageDragable();
        setToggleShowHidden();
        setPassField();
        setListenerPass();
        setVersionApp();
        loginBtn.setDisable(true);
        errorLoginLbl.setVisible(false);
    }

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            App.stage.setX(event.getScreenX() - xOffSet);
            App.stage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void setToggleShowHidden() {
        hiddenToggle = new Image(App.class.getResourceAsStream("icon/pass_hide_ic.png"));
        showToggle = new Image(App.class.getResourceAsStream("icon/pass_show_ic.png"));
    }

    private void setPassField() {
        passLoginTField.setVisible(false);
        showPass = false;
        showHiddenPass.setImage(showToggle);
        replacePassField();
    }

    private void replacePassField() {
        loginGridPane.getChildren().clear();
        loginGridPane.add(userHbox, 0, 0);
        loginGridPane.add(userLoginTField, 1, 0);
        loginGridPane.add(passHbox, 0, 1);
        if (showPass) {
            loginGridPane.add(passLoginTField, 1, 1);
        } else {
            loginGridPane.add(passwordField, 1, 1);
        }
        loginGridPane.add(showHiddenPass, 2, 1);
    }

    private void setListenerPass() {
        passLoginTField.textProperty().addListener((observable, oldValue, newValue) ->{
            passwordField.setText(passLoginTField.getText());
            loginBtnChangeStyle();
        });
        passwordField.textProperty().addListener((observable, oldValue, newValue) ->{
            passLoginTField.setText(passwordField.getText());
            loginBtnChangeStyle();
        });
        passwordField.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                onClickLogin(null);
            } else {
                userPassAction();
            }
        });
        showHiddenPass.setOnMouseClicked((event) -> {
            if (showPass) {
                showPass = false;
                showHiddenPass.setImage(showToggle);
                passLoginTField.setVisible(false);
                passwordField.setVisible(true);
                replacePassField();
            } else {
                showPass = true;
                showHiddenPass.setImage(hiddenToggle);
                passwordField.setVisible(false);
                passLoginTField.setVisible(true);
                replacePassField();
            }
        });
    }

    private void loginBtnChangeStyle() {
        if (!userLoginTField.getText().equals("") && !passLoginTField.getText().equals("")) {
            loginBtn.setStyle("-fx-background-color: #56005e;-fx-background-radius: 20;-fx-text-fill: #ffffff;");
            loginBtn.setDisable(false);
        } else {
            loginBtn.setStyle("-fx-background-color: #ffffff;-fx-background-radius: 20;");
        }
    }

    @FXML
    void createAccountClicked(MouseEvent event) {
        openRegistration();
    }

    public void openRegistration() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registrasi.fxml")));
            Scene scene = new Scene(root);

            popup = new Stage();
            popup.setResizable(false);
            popup.initOwner(App.stage);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.setScene(scene);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.centerOnScreen();
            popup.showAndWait();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    static void closePopup(){
        popup.close();
    }

    @FXML
    void closeClicked(MouseEvent event) {
        App.stageClose();
    }

    @FXML
    void minimizeClicked(MouseEvent event) {
        App.appMinimize();
    }

    @FXML
    void supportClicked(MouseEvent event) {
        App.popUpVersion();
    }

    @FXML
    void userPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
            if (showPass) {
                passLoginTField.requestFocus();
            } else{
                passwordField.requestFocus();
            }
        }
        userPassAction();
    }

    @FXML
    void passPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            onClickLogin(null);
        } else {
            userPassAction();
        }
    }
    
    private void userPassAction() {
        if (!normalTField) {
            normalTextField(userLoginTField);
            normalTextField(passLoginTField);
            normalTextField(passwordField);
            errorLoginLbl.setVisible(false);
            normalTField = true;
        }
    }

    private void redTextFieldLogin(TextField textField){
        textField.getStyleClass().setAll("new-textfield-login-red");
        textField.setPrefHeight(36);
    }

    private void normalTextField(TextField textField){
        textField.getStyleClass().setAll("new-textfield-login");
        textField.setPrefHeight(36);
    }

    @FXML
    void onClickLogin(ActionEvent event) {
        String user = null, pass, email = null;
        boolean succes = false;
        try {
            FileReader fileReader = new FileReader("userlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String userData = bufferedReader.readLine();

            while (userData != null) {
                stringTokenizer = new StringTokenizer(userData, ",");
                user = stringTokenizer.nextToken();
                pass = stringTokenizer.nextToken();
                email = stringTokenizer.nextToken();
                if (user.equals(userLoginTField.getText()) || email.equals(userLoginTField.getText())) {
                    if (pass.equals(passLoginTField.getText())) {
                        succes = true;
                        break;
                    }
                }
                userData = bufferedReader.readLine();
            }

            fileReader.close();
            bufferedReader.close();

            if (succes) {
                Properties properties = new Properties();
                properties.put("name.user", user);
                properties.put("name.email", email);
                if (staySignedCB.isSelected()) {
                    properties.put("boolean.stayin", "true");
                } else {
                    properties.put("boolean.stayin", "false");
                }

                properties.store(new FileOutputStream("user.properties"), "last user signed in");

                App.newScene("primary");
            } else {
                errorLoginLbl.setVisible(true);
                redTextFieldLogin(userLoginTField);
                redTextFieldLogin(passLoginTField);
                redTextFieldLogin(passwordField);
                normalTField = false;
            }
        } catch (Exception e) {
            errorLoginLbl.setVisible(true);
            redTextFieldLogin(userLoginTField);
            redTextFieldLogin(passLoginTField);
            redTextFieldLogin(passwordField);
            normalTField = false;
        }
    }

    @FXML
    void versionClicked(MouseEvent event) throws IOException {
        App.popUpVersion();
    }

    private void setVersionApp(){
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("version.properties"));
            versionLbl.setText("v" + properties.getProperty("name.version"));
        } catch (Exception e) {
        }
    }
}
