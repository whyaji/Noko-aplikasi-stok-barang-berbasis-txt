package noko;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import noko.item.ItemCart;

public class EditItemCartController implements Initializable {

    @FXML
    private HBox topBar;

    @FXML
    private Label titleMenu;

    @FXML
    private Button closeBtn;

    @FXML
    private VBox tileSelectedItem;

    @FXML
    private Label namaLbl;

    @FXML
    private ImageView displayImage;

    @FXML
    private Label hargaLbl;

    @FXML
    private Button minusBtn;

    @FXML
    private TextField jumlahTfield;

    @FXML
    private Button plusBtn;

    @FXML
    private Label currencyLbl;

    @FXML
    private Label totalLbl;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button saveBtn;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private ItemCart itemCart;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        makeStageDragable();
        setElemenItem();
        setBtnHandler();
    }

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            CartController.popup.setX(event.getScreenX() - xOffSet);
            CartController.popup.setY(event.getScreenY() - yOffSet);
        });
    }

    private void setElemenItem() {
        currencyLbl.setText(App.CURRENCY);
        itemCart = CartController.selectedItemCarts.get(0);
        namaLbl.setText(itemCart.getNama());
        hargaLbl.setText(String.valueOf(itemCart.getHarga()));
        jumlahTfield.setText(String.valueOf(itemCart.getBanyak()));
        totalLbl.setText(String.valueOf(itemCart.getTotal()));
        File fileImage = new File("image/" + itemCart.getImage());
        Image image = new Image(fileImage.toURI().toString());
        displayImage.setImage(image);
    }

    private void setBtnHandler() {
        jumlahTfield.setOnKeyPressed((event) -> {
            try {
                char c = event.getText().charAt(0);
                if (Character.isDigit(c) || Character.isISOControl(c) || event.getCode().equals(KeyCode.BACK_SPACE)) {
                    jumlahTfield.setEditable(true);
                } else {
                    jumlahTfield.setEditable(false);
                }
            } catch (Exception e) {
            }
        });
        jumlahTfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Integer.parseInt(newValue) > itemCart.getJumlah()) {
                jumlahTfield.setText(String.valueOf(itemCart.getJumlah()));
            }
            if (Integer.parseInt(newValue) < 1) {
                jumlahTfield.setText("0");
            }
            totalLbl.setText(String.valueOf(Integer.parseInt(jumlahTfield.getText()) * itemCart.getHarga()));
        });
        jumlahTfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (jumlahTfield.getText().isEmpty()) {
                    jumlahTfield.setText("0");
                } 
            }
        });
        plusBtn.setOnMouseClicked((event) -> {
            jumlahTfield.setText(String.valueOf(Integer.parseInt(jumlahTfield.getText()) + 1));
        });
        minusBtn.setOnMouseClicked((event) -> {
            jumlahTfield.setText(String.valueOf(Integer.parseInt(jumlahTfield.getText()) - 1));
        });
        closeBtn.setOnMouseClicked((event) -> {
            CartController.closePopup();
        });
        cancelBtn.setOnMouseClicked((event) -> {
            CartController.closePopup();
        });
        saveBtn.setOnMouseClicked((event) -> {
            itemCart.setBanyak(Integer.parseInt(jumlahTfield.getText()));
            itemCart.setTotal(Integer.parseInt(totalLbl.getText()));
            try {
                CartController.selectedItemCarts.get(0).setNama(itemCart.getNama());
                CartController.selectedItemCarts.get(0).setBanyak(itemCart.getBanyak());
                CartController.selectedItemCarts.get(0).setHarga(itemCart.getHarga());
                CartController.selectedItemCarts.get(0).setJumlah(itemCart.getJumlah());
                CartController.selectedItemCarts.get(0).setImage(itemCart.getImage());
                CartController.selectedItemCarts.get(0).setTotal(itemCart.getTotal());
                if (CartController.selectedItemCarts.get(0).getBanyak() == 0) {
                    CartController.selectedItemCarts.forEach(MarketController.itemCartList::remove);
                } 
            } catch (Exception e) {
            }
            CartController.closePopup();
        });
    }

}
