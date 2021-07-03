package noko;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.UUID;

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
import javafx.scene.layout.VBox;

public class RegistrasiController implements Initializable {

    @FXML
    private Label emailErrLbl;

    @FXML
    private TextField emailTField;

    @FXML
    private TextField userTField;

    @FXML
    private TextField passTField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmField;

    @FXML
    private TextField confirmTField;

    @FXML
    private Label userErrLbl;

    @FXML
    private Label passErr1Lbl;

    @FXML
    private Label passErr2Lbl;

    @FXML
    private Button signUpBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Label confirmErrLbl;

    @FXML
    private ImageView showHiddenPass;

    @FXML
    private ImageView showHiddenConfirm;

    @FXML
    private Label userLbl;

    @FXML
    private Label passLbl;
    
    @FXML
    private Label confLbl;
    
    @FXML
    private Label emailLbl;

    @FXML
    private VBox passErrVbox;

    @FXML
    private GridPane gridPaneReg;

    @FXML
    private HBox topBar;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private boolean showPass, showConfirm;
    private Image hiddenToggle, showToggle;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        makeStageDragable();
        setPassTfield();
        setToggleShowHidden();
        unShowingErrorLabel();
        setBtnHandler();
        setTextFieldHandler();
    }

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            LoginController.popup.setX(event.getScreenX() - xOffSet);
            LoginController.popup.setY(event.getScreenY() - yOffSet);
        });
    }

    private void setPassTfield() {
        showPass = false;
        showConfirm = false;

        passTField.setVisible(false);
        confirmTField.setVisible(false);

        replacePassField();
    }

    private void replacePassField() {
        gridPaneReg.getChildren().clear();
        gridPaneReg.add(emailLbl, 0, 0);
        gridPaneReg.add(emailTField, 1, 0);
        gridPaneReg.add(emailErrLbl, 1, 1);
        gridPaneReg.add(userLbl, 0, 2);
        gridPaneReg.add(userTField, 1, 2);
        gridPaneReg.add(userErrLbl, 1, 3);
        gridPaneReg.add(passLbl, 0, 4);
        if (showPass) {
            gridPaneReg.add(passTField, 1, 4);
        } else {
            gridPaneReg.add(passwordField, 1, 4);
        }
        gridPaneReg.add(showHiddenPass, 2, 4);
        gridPaneReg.add(passErrVbox, 1, 5);
        gridPaneReg.add(confLbl, 0, 6);
        if (showConfirm) {
            gridPaneReg.add(confirmTField, 1, 6);
        } else {
            gridPaneReg.add(confirmField, 1, 6);
        }
        gridPaneReg.add(showHiddenConfirm, 2, 6);
        gridPaneReg.add(confirmErrLbl, 1, 7);
    }

    private void setToggleShowHidden() {
        hiddenToggle = new Image(App.class.getResourceAsStream("icon/pass_hide_ic.png"));
        showToggle = new Image(App.class.getResourceAsStream("icon/pass_show_ic.png"));
    }

    private void unShowingErrorLabel() {
        emailErrLbl.setVisible(false);
        userErrLbl.setVisible(false);
        passErr1Lbl.setVisible(false);
        passErr2Lbl.setVisible(false);
        confirmErrLbl.setVisible(false);
        emailErrLbl.setText(null);
        userErrLbl.setText(null);
        passErr1Lbl.setText(null);
        passErr2Lbl.setText(null);
    }

    private void setBtnHandler() {
        closeBtn.setOnMouseClicked((event) -> {
            LoginController.closePopup();
        });
        signUpBtn.setOnMouseClicked((event) -> {
            signUpAction();
        });
    }

    private void setTextFieldHandler() {
        emailTField.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
                userTField.requestFocus();
            }
            normalTextField(emailTField);
        });
        userTField.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
                if (showPass) {
                    passTField.requestFocus();
                } else {
                    passwordField.requestFocus();
                }
            }
            normalTextField(userTField);
        });
        passTField.setOnKeyPressed((event) -> {
            passFieldEventPressed(event);
            normalTextField(passTField);
        });
        passwordField.setOnKeyPressed((event) -> {
            passFieldEventPressed(event);
            normalTextField(passwordField);
        });
        confirmTField.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                signUpAction();
            }
            normalTextField(confirmTField);
        });
        confirmField.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                signUpAction();
            }
            normalTextField(confirmField);
        });
        passTField.textProperty().addListener((observable, oldValue, newValue) ->{
            passwordField.setText(passTField.getText());
        });
        passwordField.textProperty().addListener((observable, oldValue, newValue) ->{
            passTField.setText(passwordField.getText());
        });
        confirmTField.textProperty().addListener((observable, oldValue, newValue) ->{
            confirmField.setText(confirmTField.getText());
        });
        confirmField.textProperty().addListener((observable, oldValue, newValue) ->{
            confirmTField.setText(confirmField.getText());
        });
        showHiddenPass.setOnMouseClicked((event) -> {
            if (showPass) {
                showPass = false;
                showHiddenPass.setImage(showToggle);
                passTField.setVisible(false);
                passwordField.setVisible(true);
                replacePassField();
            } else {
                showPass = true;
                showHiddenPass.setImage(hiddenToggle);
                passwordField.setVisible(false);
                passTField.setVisible(true);
                replacePassField();
            }
        });
        showHiddenConfirm.setOnMouseClicked((event) -> {
            if (showConfirm) {
                showConfirm = false;
                showHiddenConfirm.setImage(showToggle);
                confirmTField.setVisible(false);
                confirmField.setVisible(true);
                replacePassField();
            } else {
                showConfirm = true;
                showHiddenConfirm.setImage(hiddenToggle);
                confirmField.setVisible(false);
                confirmTField.setVisible(true);
                replacePassField();
            }
        });
    }

    private void passFieldEventPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
            if (showConfirm) {
                confirmTField.requestFocus();
            } else {
                confirmField.requestFocus();
            }
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

    private void signUpAction(){
        int requirement = getRequirement();
        if (requirement == 4) {
            try {
                FileWriter fileWriter = new FileWriter("userlist.txt", true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                Date date = new Date();
                UUID uuid = UUID.randomUUID();

                bufferedWriter.write(userTField.getText() + "," + passTField.getText() + "," + emailTField.getText());
                bufferedWriter.write(",*not set yet,*not set yet," + userTField.getText() + "," + date + "," + uuid);

                bufferedWriter.newLine();

                bufferedWriter.flush();
                fileWriter.close();
                bufferedWriter.close();
            } catch (Exception e) {
            }
            LoginController.closePopup();
        }
    }

    private int getRequirement() {
        int requirement = 0;
        if (checkEmail(emailTField.getText())) {
            emailErrLbl.setVisible(false);
            requirement++;
        } else {
            emailErrLbl.setVisible(true);
            redTextFieldLogin(emailTField);
        }

        if (checkUser(userTField.getText())) {
            userErrLbl.setVisible(false);
            requirement++;
        } else {
            userErrLbl.setVisible(true);
            redTextFieldLogin(userTField);
        }

        if (checkPass(passTField.getText())) {
            passErr1Lbl.setVisible(false);
            passErr2Lbl.setVisible(false);
            requirement++;
        } else {
            passErr1Lbl.setVisible(true);
            passErr2Lbl.setVisible(true);
            redTextFieldLogin(passTField);
            redTextFieldLogin(passwordField);
        }

        if (passTField.getText().equals(confirmTField.getText())) {
            confirmErrLbl.setVisible(false);
            requirement++;
        } else {
            confirmErrLbl.setVisible(true);
            redTextFieldLogin(confirmTField);
            redTextFieldLogin(confirmField);
        }
        return requirement;
    }

    private boolean checkEmail(String email) {
        boolean check = true;
        try {
            FileReader fileReader = new FileReader("userlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String userData = bufferedReader.readLine();

            while (userData != null) {
                stringTokenizer = new StringTokenizer(userData, ",");
                stringTokenizer.nextToken();
                stringTokenizer.nextToken();
                if (stringTokenizer.nextToken().equals(email)) {
                    check = false;
                    emailErrLbl.setText("Email sudah terdaftar");
                    break;
                }
                userData = bufferedReader.readLine();
            }

            fileReader.close();
            bufferedReader.close();
        } catch (Exception e) {
        }

        if (!email.contains("@")) {
            check = false;
            emailErrLbl.setText("Email tidak benar");
        }

        if (email.contains(" ")) {
            check = false;
            emailErrLbl.setText("Email tidak benar");
        }
        return check;
    }

    private boolean checkUser(String user) {
        boolean check = true;

        try {
            FileReader fileReader = new FileReader("userlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String userData = bufferedReader.readLine();

            while (userData != null) {
                stringTokenizer = new StringTokenizer(userData, ",");
                if (stringTokenizer.nextToken().equals(user)) {
                    check = false;
                    userErrLbl.setText("User tidak dapat digunakan");
                    break;
                }
                userData = bufferedReader.readLine();
            }

            fileReader.close();
            bufferedReader.close();
        } catch (Exception e) {
        }

        if (user.length() < 4) {
            check = false;
            userErrLbl.setText("User minimal 4 karakter");
        }

        for (int i = 0; i < user.length(); i++) {
            if (!(Character.isAlphabetic(user.charAt(i)) || Character.isDigit(user.charAt(i)) || user.charAt(i) == ' ')) {
                check = false;
                userErrLbl.setText("Symbol pada user tidak diterima");
                break;
            }
        }
        return check;
    }

    private boolean checkPass(String pass){
        boolean check = true;

        if (pass.length() < 8) {
            passErr1Lbl.setText("Minimal 8 Karakter");
            check = false;
        }
        
        if (!(pass.chars().anyMatch(Character::isUpperCase) && pass.chars().anyMatch(Character::isLowerCase) && pass.chars().anyMatch(Character::isDigit))) {
            check = false;
            passErr1Lbl.setText("Password harus berupa kombinasi");
            passErr2Lbl.setText("huruf kapital, normal, dan angka");
        }

        return check;
    }

}
