package noko;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import noko.user.CheckRequirement;
import noko.user.UserAccount;

public class ProfileController implements Initializable {

    @FXML
    private HBox topBar;

    @FXML
    private Button signOutBtn;

    @FXML
    private TextField userTfield;

    @FXML
    private TextField emailTfield;

    @FXML
    private TextField firstNameTfield;

    @FXML
    private TextField lastNameTfield;

    @FXML
    private TextField companyTfield;

    @FXML
    private TextField passTfield;

    @FXML
    private Button editBtn;

    @FXML
    private Button discardBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Label joinedLbl;

    @FXML
    private Label editLbl;

    @FXML
    private Label emailErrLbl;

    @FXML
    private Label userErrLbl;

    @FXML
    private Label passErr1Lbl;

    @FXML
    private Label passErr2Lbl;

    private UserAccount account;

    static Stage popup;
    static boolean confirmed;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        account = PrimaryController.account;
        setBtnVisible(false);
        setFieldEditable(false);
        setBtnHandler();
        setUserFromFile();
        unShowingErrorLabel();
    }

    private void unShowingErrorLabel() {
        emailErrLbl.setVisible(false);
        userErrLbl.setVisible(false);
        passErr1Lbl.setVisible(false);
        passErr2Lbl.setVisible(false);
    }

    private void setFieldEditable(boolean bool) {
        userTfield.setEditable(bool);
        emailTfield.setEditable(bool);
        firstNameTfield.setEditable(bool);
        lastNameTfield.setEditable(bool);
        companyTfield.setEditable(bool);
        passTfield.setEditable(bool);
    }

    private void setUserFromFile() {
        userTfield.setText(account.getUser());
        emailTfield.setText(account.getEmail());
        firstNameTfield.setText(account.getFirstName());
        lastNameTfield.setText(account.getLastName());
        companyTfield.setText(account.getCompany());

        passHidden();
        joinedLbl.setText("Joined " + account.getJoinDate());
    }

    private void passHidden() {
        StringBuilder star = new StringBuilder();
        for (int i = 0; i < account.getPass().length(); i++) {
            star.append("*");
        }
        passTfield.setText(star.toString());
    }

    private void setBtnVisible(boolean bool) {
        editLbl.setVisible(bool);
        discardBtn.setVisible(bool);
        saveBtn.setVisible(bool);
    }

    private void setBtnHandler() {
        signOutBtn.setOnMouseClicked((event) -> {
            signOutHandler();
        });
        editBtn.setOnMouseClicked((event) -> {
            editProfile();
        });
        saveBtn.setOnMouseClicked((event) -> {
            saveEditProfile();
            passHidden();
        });
        discardBtn.setOnMouseClicked((event) -> {
            discardEditProfile();
            passHidden();
        });
    }

    private void discardEditProfile() {
        setBtnVisible(false);
        editBtn.setVisible(true);
        setUserFromFile();
    }

    private void saveEditProfile() {
        String newData = userTfield.getText() + "," + passTfield.getText() + "," + emailTfield.getText() + ","
        + firstNameTfield.getText() + "," + lastNameTfield.getText() + "," + companyTfield.getText() + ","
        + account.getJoinDate() + "," + account.getUuid();

        if (newData.equals(account.getUserLine())) {
            discardEditProfile();
            return;
        }

        CheckRequirement check = new CheckRequirement(userTfield.getText(), emailTfield.getText(), passTfield.getText());
        if (!check.checkEmail(account.getEmail())) {
            emailErrLbl.setVisible(true);
            return;
        }
        if (!check.checkUser(account.getUser())) {
            userErrLbl.setVisible(true);
            return;
        }
        if (!check.checkPass()) {
            passErr1Lbl.setVisible(true);
            passErr2Lbl.setVisible(true);
            return;
        }
        editAccountData(newData);
        setBtnVisible(false);
        setFieldEditable(false);
        unShowingErrorLabel();
        editBtn.setVisible(true);
    }

    private void editAccountData(String newData) {
        editUserProperties();
        editUserFile("userlist", newData);
        editPrimaryAccount(newData);
    }

    private void editPrimaryAccount(String newData) {
        PrimaryController.account = new UserAccount(newData);
        account = PrimaryController.account;
    }

    private void editUserFile(String fileName, String newData) {
        try {
            FileReader fileReader = new FileReader(fileName + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter(fileName + "-temp" + ".txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String data = bufferedReader.readLine();
            while (data != null) {
                if (data.equals(account.getUserLine())) {
                    bufferedWriter.write(newData);
                } else {
                    bufferedWriter.write(data);
                }
                bufferedWriter.newLine();
                data = bufferedReader.readLine();
    
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
        }
        File file = new File(fileName + ".txt");
        File fileTemp = new File(fileName + "-temp" + ".txt");
        file.delete();
        fileTemp.renameTo(file);
    }

    private void editUserProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("user.properties"));
            properties.setProperty("name.user", userTfield.getText());
            properties.setProperty("name.email", emailTfield.getText());
            properties.store(new FileOutputStream("user.properties"), "last user signed in");
        } catch (Exception e) {
        }
    }

    private void editProfile() {
        confirmed = false;
        popupConfirmPass();

        if (confirmed) {
            setBtnVisible(true);
            editBtn.setVisible(false);
            setFieldEditable(true);
            passTfield.setText(account.getPass());
        }
    }

    private void popupConfirmPass() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("confirmpass.fxml")));
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
        }
    }

    static void closePopup(){
        popup.close();
    }

    private void signOutHandler() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("user.properties"));
            properties.setProperty("boolean.stayin", "false");
            properties.store(new FileOutputStream("user.properties"), "Last user sigened out");
            App.newScene("login");
        } catch (Exception e) {
        }
    }

}
