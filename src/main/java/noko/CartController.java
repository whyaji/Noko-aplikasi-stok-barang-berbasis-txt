package noko;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import noko.item.ItemAnalytics;
import noko.item.ItemCart;

public class CartController implements Initializable {

    @FXML
    private HBox topBar;

    @FXML
    private Label titleMenu;

    @FXML
    private Button closeBtn;

    @FXML
    private TableView<ItemCart> cartTableView;

    @FXML
    private TableColumn<ItemCart, Integer> banyakColumn;

    @FXML
    private TableColumn<ItemCart, String> namaColumn;

    @FXML
    private TableColumn<ItemCart, Integer> hargaColumn;

    @FXML
    private TableColumn<ItemCart, Integer> totalColumn;

    @FXML
    private Label customerLbl;

    @FXML
    private Label currencyLbl;

    @FXML
    private Label totalPembayaranLbl;

    @FXML
    private Button editBtn;

    @FXML
    private Button bayarBtn;

    @FXML
    private Label dateLbl;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private static boolean isDetail;

    static ObservableList<ItemCart> selectedItemCarts;
    static Stage popup;
    private int jumlahItemTerjual;

    static int totalPembayaran;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        makeStageDragable();
        if (isDetail) {
            titleMenu.setText("Detail Cart Customer");
            String namaCustomer = AnalyticsController.selectedItemAnalytics.get(0).getCustomerNama();
            customerLbl.setText(namaCustomer);
            setBtnDetail();
            int totalPembayran = AnalyticsController.selectedItemAnalytics.get(0).getTotalPembayaran();
            totalPembayaranLbl.setText(String.valueOf(totalPembayran));
            setTableView(getItemCharList(AnalyticsController.selectedItemAnalytics.get(0)));
        } else {
            customerLbl.setText(MarketController.customerName);
            setBtnHandler();
            setTotalPembayaran();
            setTableView(MarketController.itemCartList);
        }
    }

    private void makeStageDragable() {
        topBar.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        topBar.setOnMouseDragged((event) -> {
            if (isDetail) {
                AnalyticsController.popup.setX(event.getScreenX() - xOffSet);
                AnalyticsController.popup.setY(event.getScreenY() - yOffSet);
            } else {
                MarketController.popup.setX(event.getScreenX() - xOffSet);
                MarketController.popup.setY(event.getScreenY() - yOffSet);
            }
        });
    }

    public static boolean isDetail() {
        return isDetail;
    }

    public static void setDetail(boolean isDetail) {
        CartController.isDetail = isDetail;
    }

    private ObservableList<ItemCart> getItemCharList(ItemAnalytics itemAnalytics) {
        ObservableList<ItemCart> itemCarts = FXCollections.observableArrayList();
        ItemCart itemCart;
        try {
            FileReader fileReader = new FileReader("cartlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String data = bufferedReader.readLine();

            while (data != null) {
                stringTokenizer = new StringTokenizer(data, ",");
                String customerUuid = stringTokenizer.nextToken();
                if(customerUuid.equals(itemAnalytics.getCustomerUuid())){
                    itemCart = new ItemCart();
                    itemCart.setUuidItem(stringTokenizer.nextToken());
                    stringTokenizer.nextToken();
                    stringTokenizer.nextToken();
                    if (dateLbl.getText().isEmpty()) {
                        dateLbl.setText(stringTokenizer.nextToken());
                    } else {
                        stringTokenizer.nextToken();
                    }
                    itemCart.setNama(stringTokenizer.nextToken());
                    itemCart.setHarga(Integer.parseInt(stringTokenizer.nextToken()));
                    itemCart.setBanyak(Integer.parseInt(stringTokenizer.nextToken()));
                    itemCart.setTotal(Integer.parseInt(stringTokenizer.nextToken()));
                    itemCarts.add(itemCart);
                }
                data = bufferedReader.readLine();
            }

            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
        }
        return itemCarts;
    }

    private void setBtnDetail() {
        closeBtn.setOnMouseClicked((event) -> {
            AnalyticsController.closePopup();
        });
        editBtn.setVisible(false);
        bayarBtn.setText("Kembali");
        bayarBtn.setOnMouseClicked((event) -> {
            AnalyticsController.closePopup();
        });
    }

    private void setBtnHandler() {
        closeBtn.setOnMouseClicked((event) -> {
            MarketController.closePopup();
        });
        editBtn.setOnMouseClicked((event) -> {
            editItemcart();
        });
        bayarBtn.setOnMouseClicked((event) -> {
            if (totalPembayaran != 0) {
                addToFileCharList();
                addToFileAnalytics();
                MarketController.closePopup();
                MarketController.confirmed = true;
            }
        });
    }

    private void addToFileAnalytics() {
        try {
            FileWriter fileWriter = new FileWriter("analyticlist.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(PrimaryController.account.getUuid() +",");
            bufferedWriter.write(MarketController.customerUuid + ",");
            bufferedWriter.write(java.time.LocalDate.now() + ",");
            bufferedWriter.write(MarketController.customerName + ",");
            bufferedWriter.write(jumlahItemTerjual + ",");
            bufferedWriter.write(String.valueOf(totalPembayaran));
            bufferedWriter.newLine();

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
        }
    }

    private void addToFileCharList() {
        jumlahItemTerjual = 0;
        try {
            FileWriter fileWriter = new FileWriter("cartlist.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (ItemCart itemCart : MarketController.itemCartList) {
                bufferedWriter.write(MarketController.customerUuid + ",");
                bufferedWriter.write(itemCart.getUuidItem() + ",");
                bufferedWriter.write(PrimaryController.account.getUuid() +",");
                bufferedWriter.write(MarketController.customerName + ",");
                bufferedWriter.write(java.time.LocalDate.now() + ",");
                bufferedWriter.write(itemCart.getNama() + ",");
                bufferedWriter.write(itemCart.getHarga() + ",");
                bufferedWriter.write(itemCart.getBanyak() + ",");
                bufferedWriter.write(String.valueOf(itemCart.getTotal()));
                bufferedWriter.newLine();
                jumlahItemTerjual += itemCart.getBanyak();
            }

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
        }
    }

    private void editItemcart() {
        selectedItemCarts = cartTableView.getSelectionModel().getSelectedItems();
        popupEditItemCart();
        cartTableView.refresh();
        setTotalPembayaran();
    }

    private void popupEditItemCart() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("edititemcart.fxml")));
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

    private void setTableView(ObservableList<ItemCart> itemCartList) {
        banyakColumn.setCellValueFactory(new PropertyValueFactory<>("banyak"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        hargaColumn.setCellValueFactory(new PropertyValueFactory<>("harga"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        cartTableView.setItems(itemCartList);
    }

    private void setTotalPembayaran() { 
        totalPembayaran = 0;
        for (int i = 0; i < MarketController.itemCartList.size(); i++) {
            totalPembayaran += MarketController.itemCartList.get(i).getTotal();
        }
        totalPembayaranLbl.setText(String.valueOf(totalPembayaran));
    }
}
