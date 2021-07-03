package noko;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Wahyu Patriaji
 * @version 1.5.1
 */
public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    private static Scene scene;
    static Stage stage;
    static final String CURRENCY = "Rp";

    @Override
    public void start(Stage primaryStage) throws IOException {
        new File("image").mkdir();
        stage = primaryStage;
        if (checkStaySigned()) {
            newScene("primary");
        } else {
            newScene("login");
        }
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    private static boolean checkStaySigned() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("user.properties"));
            if ("true".equals(properties.getProperty("boolean.stayin"))) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    static void newScene(String fxml) throws IOException {
        scene = new Scene(loadFXML(fxml));
        scene.getStylesheets().add(App.class.getResource("value/style.css").toExternalForm());
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon/app_ic.png")));
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    static void stageClose(){
        stage.close();
        System.exit(0);
    }

    static void appMinimize(){
        stage.setIconified(true);
    }

    static void appMaximized(){
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    static boolean isAppMaximized(){
        return stage.isMaximized();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    static Stage popup;

    static void popUpVersion() {
        try {
            Parent root = loadFXML("version");
            Scene scene = new Scene(root);

            popup = new Stage();
            popup.setResizable(false);
            popup.initOwner(App.stage);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.setScene(scene);
            popup.centerOnScreen();
            popup.show();
            popup.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    closePopup();
                }
            });
        } catch (Exception e) {
        }
    }

    static void closePopup(){
        popup.close();
    }
}