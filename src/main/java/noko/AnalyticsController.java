package noko;

import java.io.BufferedReader;
import java.io.FileReader;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import noko.item.ItemAnalytics;

public class AnalyticsController implements Initializable {

    @FXML
    private Label varianItemLbl;

    @FXML
    private Label totalCustomerLbl;

    @FXML
    private Label itemTerjualLbl;

    @FXML
    private Label curencyLbl;

    @FXML
    private Label totalPemasukanLbl;

    @FXML
    private TableView<ItemAnalytics> historyTable;

    @FXML
    private TableColumn<ItemAnalytics, String> tanggalColumn;

    @FXML
    private TableColumn<ItemAnalytics, String> namaColumn;

    @FXML
    private TableColumn<ItemAnalytics, Integer> banyakColumn;

    @FXML
    private TableColumn<ItemAnalytics, Integer> totalColumn;

    @FXML
    private Button detailBtn;

    private int varianItem, totalCustomer, itemTerjual, totalPemasukan;
    
    private ObservableList<ItemAnalytics> itemAnalytics = FXCollections.observableArrayList();
    static ObservableList<ItemAnalytics> selectedItemAnalytics;

    static Stage popup;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setVarianItem();
        setCustomer();
        setTableAnalytics();
        setBtnHandler();
    }

    private void setBtnHandler() {
        detailBtn.setOnMouseClicked((event) -> {
            selectedItemAnalytics = historyTable.getSelectionModel().getSelectedItems();
            CartController.setDetail(true);
            popupCart();
        });
    }

    private void popupCart() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("cart.fxml")));
            Scene scene = new Scene(root);
        
            popup = new Stage();
            popup.setResizable(false);
            popup.initOwner(App.stage);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.setScene(scene);
            popup.centerOnScreen();
            popup.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    closePopup();
                }
            });
            popup.showAndWait();
        } catch (Exception e) {
        }
    }

    static void closePopup(){
        popup.close();
    }

    private void setTableAnalytics() {
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("customerNama"));
        banyakColumn.setCellValueFactory(new PropertyValueFactory<>("banyakItem"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPembayaran"));
        historyTable.setItems(itemAnalytics);
    }

    private void setCustomer() {
        totalCustomer = 0;
        itemTerjual = 0;
        totalPemasukan = 0;

        ItemAnalytics itemAnalytic;
        try {
            FileReader fileReader = new FileReader("analyticlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String data = bufferedReader.readLine();

            while (data != null) {
                stringTokenizer = new StringTokenizer(data, ",");
                String akunUuid = stringTokenizer.nextToken();
                if (akunUuid.equals(PrimaryController.account.getUuid())) {
                    totalCustomer++;
                    itemAnalytic = new ItemAnalytics();
                    itemAnalytic.setAkunUuid(akunUuid);
                    itemAnalytic.setCustomerUuid(stringTokenizer.nextToken());
                    itemAnalytic.setDate(stringTokenizer.nextToken());
                    itemAnalytic.setCustomerNama(stringTokenizer.nextToken());
                    itemAnalytic.setBanyakItem(Integer.parseInt(stringTokenizer.nextToken()));
                    itemAnalytic.setTotalPembayaran(Integer.parseInt(stringTokenizer.nextToken()));

                    itemTerjual += itemAnalytic.getBanyakItem();
                    totalPemasukan += itemAnalytic.getTotalPembayaran();

                    itemAnalytics.add(itemAnalytic);
                }
                data = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
        }

        totalCustomerLbl.setText(String.valueOf(totalCustomer));
        itemTerjualLbl.setText(String.valueOf(itemTerjual));
        totalPemasukanLbl.setText(String.valueOf(totalPemasukan));
    }

    private void setVarianItem() {
        varianItem = 0;
        try {
            FileReader fileReader = new FileReader("itemlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String itemData = bufferedReader.readLine();

            while (itemData != null) {
                stringTokenizer = new StringTokenizer(itemData, ",");
                stringTokenizer.nextToken();
                if (stringTokenizer.nextToken().equals(PrimaryController.account.getUuid())) {
                    varianItem++;
                }
                itemData = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (Exception e) {
        }
        varianItemLbl.setText(String.valueOf(varianItem));
    }
}
