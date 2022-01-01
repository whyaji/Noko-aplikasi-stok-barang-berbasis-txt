package noko;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import javafx.stage.FileChooser.ExtensionFilter;
import noko.item.Item;

public class TambahBarangController implements Initializable {

    @FXML
    private HBox topBar;

    @FXML
    private Button closeBtn;

    @FXML
    private TextField namaTfield;

    @FXML
    private TextField hargaTfield;

    @FXML
    private TextField jumlahTfield;

    @FXML
    private ImageView displayImage;

    @FXML
    private TextField pathTfield;

    @FXML
    private Button uploadBtn;

    @FXML
    private Button tambahkanBtn;

    @FXML
    private Button cancelBtn;
    
    @FXML
    private TextField kategoriTfield;

    private Item editItem;

    @FXML
    private Label titleMenu;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        makeStageDragable();
        setBtnHandler();
        setTFieldHandler();
        pathTfield.setEditable(false);
        nameFileCopied = "";

        if (StoreHouseController.editMode) {
            titleMenu.setText("Edit Barang");
            tambahkanBtn.setText("Update");
            editItem = StoreHouseController.choosenItem;
            namaTfield.setText(editItem.getNama());
            hargaTfield.setText(String.valueOf(editItem.getHarga()));
            jumlahTfield.setText(String.valueOf(editItem.getJumlah()));
            kategoriTfield.setText(editItem.getKategori());
            pathTfield.setText(System.getProperty("user.dir") + "\\image\\" + editItem.getImage());
            File fileImage = new File("image/" + editItem.getImage());
            Image image = new Image(fileImage.toURI().toString());
            displayImage.setImage(image);
        }
    }

    private void setTFieldHandler() {
        namaTfield.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
                hargaTfield.requestFocus();
            }
            normalTextField(namaTfield);
        });
        hargaTfield.setOnKeyPressed((event) -> {
            onlyIntegerEvent(event, hargaTfield, jumlahTfield);
        });
        jumlahTfield.setOnKeyPressed((event) -> {
            onlyIntegerEvent(event, jumlahTfield, kategoriTfield);
        });
        namaTfield.setOnKeyPressed((event) -> {
            normalTextField(namaTfield);
        });
        pathTfield.textProperty().addListener((observable, oldValue, newValue) ->{
            normalTextField(pathTfield);
        });
    }

    private void onlyIntegerEvent(KeyEvent event, TextField textField, TextField textField2) {
        if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
            textField2.requestFocus();
        }
        try {
            char c = event.getText().charAt(0);
            if (Character.isDigit(c) || Character.isISOControl(c) || event.getCode().equals(KeyCode.BACK_SPACE)) {
                textField.setEditable(true);
                normalTextField(textField);
            } else {
                textField.setEditable(false);
            }
        } catch (Exception e) {
        }
    }

    private double xOffSet = 0;
    private double yOffSet = 0;

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            StoreHouseController.popup.setX(event.getScreenX() - xOffSet);
            StoreHouseController.popup.setY(event.getScreenY() - yOffSet);
        });
    }

    private void setBtnHandler() {
        cancelBtn.setOnMouseClicked((event) -> {
            StoreHouseController.closePopup();
        });
        closeBtn.setOnMouseClicked((event) -> {
            StoreHouseController.closePopup();
        });
        uploadBtn.setOnMouseClicked((event) -> {
            chooserImage();
        });
        displayImage.setOnMouseClicked((event) -> {
            chooserImage();
        });
        tambahkanBtn.setOnMouseClicked((event) -> {
            if (StoreHouseController.editMode) {
                editBarangConfirm();
            }else{
                tambahBarangConfirm();
            }
        });
    }

    private void editBarangConfirm() {
        if (tambahRequirement()) {
            if (selectedImage != null) {
                if (!selectedImage.getName().equals(editItem.getNama())) {
                    copyImageToProject();
                }
            }
            editDataInFile();
            StoreHouseController.confirmed = true;
            StoreHouseController.closePopup();
        }
    }

    private void editDataInFile() {
        try {
            FileReader fileReader = new FileReader("itemlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter("itemlist-temp.txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String data = bufferedReader.readLine();
            while (data != null) {
                if (data.equals(editItem.getItemLine(PrimaryController.account.getUuid()))) {
                    bufferedWriter.write(editItem.getUuidItem() + ",");
                    bufferedWriter.write(PrimaryController.account.getUuid() + ",");
                    bufferedWriter.write(namaTfield.getText() + ",");
                    bufferedWriter.write(hargaTfield.getText() + ",");
                    bufferedWriter.write(jumlahTfield.getText() + ",");
                    bufferedWriter.write(kategoriTfield.getText() + ",");
                    if (selectedImage != null) {
                        if (!selectedImage.getName().equals(editItem.getNama())) {
                            bufferedWriter.write(nameFileCopied);
                            deleteOldFileImage();
                        } else {
                            bufferedWriter.write(editItem.getImage());
                        }
                    } else {
                        bufferedWriter.write(editItem.getImage());
                    }
                    
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
        File file = new File("itemlist.txt");
        File fileTemp = new File("itemlist-temp.txt");
        file.delete();
        fileTemp.renameTo(file);
    }

    private void deleteOldFileImage() {
        try {
            File file = new File("image/" + editItem.getImage());
            file.delete();
        } catch (Exception e) {
        }
    }

    private void tambahBarangConfirm() {
        if (tambahRequirement()) {
            if (selectedImage != null) {
                copyImageToProject();
            }
            dataToFile();
            StoreHouseController.confirmed = true;
            StoreHouseController.closePopup();
        }
    }

    private boolean tambahRequirement(){
        boolean check = true;
        TextField[] textFields = {namaTfield, hargaTfield, jumlahTfield, kategoriTfield, pathTfield};
        for (TextField textField : textFields) {
            check = checkTfield(textField) && check;
        }
        return check;
    }

    private boolean checkTfield(TextField textField) {
        if (textField.getText().equals("")) {
            textField.getStyleClass().setAll("textfield-login-red");
            textField.setPrefHeight(39.2);
            return false;
        }
        return true;
    }

    private void normalTextField(TextField textField){
        textField.getStyleClass().setAll("textfield-login");
        textField.setPrefHeight(39.2);
    }

    private void dataToFile() {
        try {
            FileWriter fileWriter = new FileWriter("itemlist.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            UUID uuid = UUID.randomUUID();

            bufferedWriter.write(uuid + ",");
            bufferedWriter.write(PrimaryController.account.getUuid() + ",");
            bufferedWriter.write(namaTfield.getText() + ",");
            bufferedWriter.write(hargaTfield.getText() + ",");
            bufferedWriter.write(jumlahTfield.getText() + ",");
            bufferedWriter.write(kategoriTfield.getText() + ",");
            bufferedWriter.write(nameFileCopied);
            bufferedWriter.newLine();

            bufferedWriter.flush();
            fileWriter.close();
            bufferedWriter.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void copyImageToProject() {
        String fileExtension = getFileExtension(selectedImage);
        UUID uuid = UUID.randomUUID();
        nameFileCopied = uuid + "." + fileExtension;
        try {
            Path from = Paths.get(selectedImage.toURI());
            Path to = Paths.get("image/" + nameFileCopied);
            Files.copy(from, to);
        } catch (Exception e) {
        }
    }

    String getFileExtension(File file) {
        String fileName = file.getName();
        String extension = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }
        return extension;
    }

    private File selectedImage;
    private String nameFileCopied;

    private void chooserImage() {
        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("Upload Image");
        imageChooser.getExtensionFilters().add(new ExtensionFilter("Image files", "*.jpg","*.jpeg", "*.png"));
        selectedImage = imageChooser.showOpenDialog(StoreHouseController.popup);

        if (selectedImage != null) {
            ifFileExist();
        }
    }

    private void ifFileExist() {
        pathTfield.setText(selectedImage.getAbsolutePath());
        Image image = new Image(selectedImage.toURI().toString());
        displayImage.setImage(image);
    }

}
