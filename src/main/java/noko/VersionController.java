package noko;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class VersionController implements Initializable {

    @FXML
    private Label versionLabel;

    @FXML
    private Label authorLbl;

    @FXML
    private Label nimLbl;

    @FXML
    private Label assignmentLbl;

    @FXML
    private Label projactLbl;

    @FXML
    private HBox topBar;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            App.popup.setX(event.getScreenX() - xOffSet);
            App.popup.setY(event.getScreenY() - yOffSet);
        });
    }

    @FXML
    void okayClicked(MouseEvent event) {
        App.closePopup();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        makeStageDragable();
        setUpVersionProperties();
        
    }

    private void setUpVersionProperties() {
        try {
            Properties properties = new Properties();
            properties.load(App.class.getResourceAsStream("version/version.properties"));
            versionLabel.setText("v" + properties.getProperty("name.version"));
            authorLbl.setText("Author: " + properties.getProperty("name.author"));
            nimLbl.setText("NIM: " + properties.getProperty("nim.mahasiswa"));
            assignmentLbl.setText("Assignment: " + properties.getProperty("name.assignment"));
            projactLbl.setText("Project Name: " + properties.getProperty("name.project"));
        } catch (Exception e) {
        }
    }

}
