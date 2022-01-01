package noko;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import noko.item.Item;
import noko.listener.AppListener;

public class ItemController {

    @FXML
    private ImageView itemImage;

    @FXML
    private Label namaBarangLbl;

    @FXML
    private Label hargaBarangLbl;

    @FXML
    private Label soldLbl;

    @FXML
    private void itemClick(MouseEvent mouseEvent) {
        appListener.onClickListener(item);
    }

    private AppListener appListener;
    private Item item;

    void setData(Item item, AppListener appListener){
        this.item = item;
        this.appListener = appListener;
        namaBarangLbl.setText(item.getNama());
        hargaBarangLbl.setText(App.CURRENCY + " " + item.getHarga());
        if (item.getJumlah() == 0) {
            soldLbl.setText("SOLD");
        } else {
            soldLbl.setText(item.getJumlah() + "pcs");
        }
        File fileImage = new File("image/" + item.getImage());
        Image image = new Image(fileImage.toURI().toString());
        itemImage.setImage(image);
    }
}
