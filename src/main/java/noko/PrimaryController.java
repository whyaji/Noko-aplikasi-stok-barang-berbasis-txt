package noko;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import noko.user.UserAccount;

public class PrimaryController implements Initializable {

    @FXML
    private Button minimizeBtn;

    @FXML
    private Button supportBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private HBox profileBtn;

    @FXML
    private HBox marketBtn;

    @FXML
    private HBox storeHouseBtn;

    @FXML
    private HBox anayticsBtn;

    @FXML
    private HBox topBar;

    @FXML
    private BorderPane menuPane;

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
        App.appMaximized();
    }

    static UserAccount account;

    private double xOffSet = 0;
    private double yOffSet = 0;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        makeStageDragable();
        setUserFromProperties();
        setHandlerMenuButton();
        setDefaultMenuPane();
    }

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            App.stage.setX(event.getScreenX() - xOffSet);
            App.stage.setY(event.getScreenY() - yOffSet);
            if (App.isAppMaximized()) {
                App.appMaximized();
            }
        });
    }

    void setUserFromProperties() {
        account = new UserAccount(findUserLine());
    }

    private String findUserLine() {
        String userLine = null, user = null;
        boolean succes = false;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("user.properties"));
            
            FileReader fileReader = new FileReader("userlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String userData = bufferedReader.readLine();

            while (userData != null) {
                stringTokenizer = new StringTokenizer(userData, ",");
                userLine = userData;
                user = stringTokenizer.nextToken();
                if (user.equals(properties.getProperty("name.user"))) {
                    succes = true;
                    break;
                }
                userData = bufferedReader.readLine();
            }

            fileReader.close();
            bufferedReader.close();
        } catch (Exception e) {
        }
        if (succes) {
            return userLine;
        } 
        return null;
    }

    private void setHandlerMenuButton() {
        setPressedMenuButton(profileBtn, "profile");
        setPressedMenuButton(marketBtn, "market");
        setPressedMenuButton(storeHouseBtn, "storehouse");
        setPressedMenuButton(anayticsBtn, "analytics");
    }

    private void setPressedMenuButton(HBox hBoxButton, String fxmlName) {
        hBoxButton.setOnMousePressed((event) -> {
            hBoxButton.getStyleClass().setAll("button-menu-press");
        });

        HBox[] hBoxs = {profileBtn, marketBtn, storeHouseBtn, anayticsBtn};

        hBoxButton.setOnMouseReleased((event) -> {
            if (hBoxButton.isHover()) {
                hBoxButton.getStyleClass().setAll("button-menu-release");
                setMenu(fxmlName);
                for (HBox hBox : hBoxs) {
                    if (hBox != hBoxButton) {
                        hBox.getStyleClass().setAll("button-menu");
                    }
                }
            } else {
                hBoxButton.getStyleClass().setAll("button-menu");
            }
        });
    }

    private void setMenu(String fxmlName) {
        Pane view = null;
        try {
            URL fileUrl = App.class.getResource(fxmlName + ".fxml");
            if (fileUrl == null) {
                throw new Exception("file not found");
            }
            new FXMLLoader();
            view = FXMLLoader.load(fileUrl);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        menuPane.setCenter(view);
    }

    private void setDefaultMenuPane() {
        setMenu("profile");
        profileBtn.getStyleClass().setAll("button-menu-release");
    }
}