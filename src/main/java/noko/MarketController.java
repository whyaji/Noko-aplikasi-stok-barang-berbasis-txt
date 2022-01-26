package noko;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import noko.item.Item;
import noko.item.ItemCart;
import noko.listener.AppListener;

public class MarketController implements Initializable {

    @FXML
    private TextField searchTfield;

    @FXML
    private Button searchBtn;

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
    private Button addToCartBtn;

    @FXML
    private TextField customerNameTfield;

    @FXML
    private Button setCancelBtn;

    @FXML
    private TextField totalBayarTfield;

    @FXML
    private Button lihatKeranjangBtn;

    @FXML
    private GridPane gridPaneKategori;

    @FXML
    private Label kategoriSelected;

    @FXML
    private GridPane gridPaneStore;

    @FXML
    private Label totalPembayaranLbl;

    @FXML
    private ChoiceBox<String> sortChoiceBox;

    static String customerUuid;
    static String customerName;

    static ObservableList<ItemCart> itemCartList = FXCollections.observableArrayList();
    static Item choosenItem;

    private AppListener appListener;
    private String scope, kategoriScope;

    private List<Item> items = new ArrayList<>();

    static boolean confirmed;
    static Stage popup;

    private List<String> kategoriList = new ArrayList<>();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        totalPembayaranLbl.setText(totalPembayaranLbl.getText() + "(" + App.CURRENCY + ")");
        setSortChoiceBox();
        setVisibleElemen(false);
        setUpKategori();
        setBtnHandler();
        scope = "";
        dynamicGridPane();
    }

    private void setSortChoiceBox() {
        String[] sortList = { "Terbaru", "Harga Terendah", "Harga Tertinggi", "Nama" };
        sortChoiceBox.getItems().setAll(sortList);
        sortChoiceBox.setOnAction(this::getSort);
        sortChoiceBox.getSelectionModel().selectFirst();
    }

    private void getSort(ActionEvent event) {
        dynamicGridPane();
    }

    private void setVisibleElemen(boolean visibility) {
        totalPembayaranLbl.setVisible(visibility);
        totalBayarTfield.setVisible(visibility);
        lihatKeranjangBtn.setVisible(visibility);
        addToCartBtn.setVisible(visibility);
    }

    private void setUpKategori() {
        kategoriScope = "All";
        kategoriSelected.setText(kategoriScope);
        confirmed = true;
        gridPaneKategori.getChildren().clear();
        kategoriList.clear();
    }

    private void setBtnHandler() {
        totalBayarTfield.setEditable(false);
        searchBtn.setOnMouseClicked((event) -> {
            searchAction();
        });
        searchTfield.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                searchAction();
            }
        });
        setCancelBtn.setOnMouseClicked((event) -> {
            setCancelBtnHandler();
        });
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
            if (!jumlahTfield.getText().isEmpty()) {
                if (choosenItem.getJumlah() != 0) {
                    jumlahTfield.setEditable(true);
                    if (Integer.parseInt(newValue) > choosenItem.getJumlah()) {
                        jumlahTfield.setText(String.valueOf(choosenItem.getJumlah()));
                    }
                    if (Integer.parseInt(newValue) < 1) {
                        jumlahTfield.setText("1");
                    }
                } else {
                    jumlahTfield.setEditable(false);
                    jumlahTfield.setText("0");
                }
                totalLbl.setText(String.valueOf(Integer.parseInt(jumlahTfield.getText()) * choosenItem.getHarga()));
            }
        });
        jumlahTfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (jumlahTfield.getText().isEmpty()) {
                    if (choosenItem.getJumlah() != 0) {
                        jumlahTfield.setText("1");
                    } else {
                        jumlahTfield.setEditable(false);
                        jumlahTfield.setText("0");
                    }
                }
            }
        });
        plusBtn.setOnMouseClicked((event) -> {
            jumlahTfield.setText(String.valueOf(Integer.parseInt(jumlahTfield.getText()) + 1));
        });
        minusBtn.setOnMouseClicked((event) -> {
            jumlahTfield.setText(String.valueOf(Integer.parseInt(jumlahTfield.getText()) - 1));
        });
        addToCartBtn.setOnMouseClicked((event) -> {
            if (choosenItem.getJumlah() != 0) {
                totalBayarTfield.setText(String.valueOf(totalAllItem(totalBayarTfield.getText())));
                addObservaleListItem();
                dynamicGridPane();
                if (choosenItem.getJumlah() > 0) {
                    jumlahTfield.setEditable(true);
                    jumlahTfield.setText("1");
                }
            }
        });
        lihatKeranjangBtn.setOnMouseClicked((event) -> {
            lihatCart();
        });
    }

    private void dynamicGridPane() {
        items.clear();
        items.addAll(getData());
        sortItems(sortChoiceBox.getValue());
        if (confirmed) {
            setUpAllButtonKategori();
        }
        if (items.size() > 0) {
            selectedItemDisplay(true);
            setChoosenItem(items.get(0));
            appListener = new AppListener() {
                @Override
                public void onClickListener(Item item) {
                    setChoosenItem(item);
                }
            };
        } else {
            setChoosenItem(null);
            selectedItemDisplay(false);
        }
        gridPaneStore.getChildren().clear();
        int column = 0, row = 0;
        try {
            for (int i = 0; i < items.size(); i++) {
                for (ItemCart itemCart : itemCartList) {
                    if (itemCart.getUuidItem().equals(items.get(i).getUuidItem())) {
                        items.get(i).setJumlah(items.get(i).getJumlah() - itemCart.getBanyak());
                    }
                }

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(App.class.getResource("item.fxml"));

                AnchorPane anchorPane = fxmlLoader.load();
                anchorPane.setStyle("-fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0.1), 10.0, 0.0, 0.0, 10.0);");

                ItemController itemController = fxmlLoader.getController();
                itemController.setData(items.get(i), appListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                gridPaneStore.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (Exception e) {
        }
        confirmed = false;
    }

    private void sortItems(String value) {
        switch (value) {// "Terbaru", "Harga Terendah", "Harga Tertinggi", "Nama"
            case "Terbaru":
                sortTerbaru();
                break;
            case "Harga Terendah":
                sortTerendah();
                break;
            case "Harga Tertinggi":
                sortTertinggi();
                break;
            case "Nama":
                sortNama();
                break;
            default:
                break;
        }
    }

    private void sortNama() {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (items.get(j).getNama().compareToIgnoreCase(items.get(min).getNama()) < 0) {
                    min = j;
                }
            }

            Item temp = items.get(min);
            items.set(min, items.get(i));
            items.set(i, temp);
        }
    }

    private void sortTertinggi() {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (items.get(j).getHarga() > items.get(min).getHarga()) {
                    min = j;
                }
            }

            Item temp = items.get(min);
            items.set(min, items.get(i));
            items.set(i, temp);
        }
    }

    private void sortTerendah() {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (items.get(j).getHarga() < items.get(min).getHarga()) {
                    min = j;
                }
            }

            Item temp = items.get(min);
            items.set(min, items.get(i));
            items.set(i, temp);
        }
    }

    private void sortTerbaru() {
        Collections.reverse(items);
    }

    private List<Item> getData() {
        List<Item> items = new ArrayList<>();
        Item item;

        try {
            FileReader fileReader = new FileReader("itemlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringTokenizer stringTokenizer;
            String itemData = bufferedReader.readLine();
            String uuidUser;

            while (itemData != null) {
                stringTokenizer = new StringTokenizer(itemData, ",");
                item = new Item();
                item.setUuidItem(stringTokenizer.nextToken());
                uuidUser = stringTokenizer.nextToken();
                item.setNama(stringTokenizer.nextToken());
                item.setHarga(Integer.parseInt(stringTokenizer.nextToken()));
                item.setJumlah(Integer.parseInt(stringTokenizer.nextToken()));
                item.setKategori(stringTokenizer.nextToken());
                item.setImage(stringTokenizer.nextToken());
                itemData = bufferedReader.readLine();
                if (uuidUser.equals(PrimaryController.account.getUuid())) {
                    if (confirmed) {
                        if (kategoriList.isEmpty()) {
                            kategoriList.add("All");
                        }
                        kategoriList.add(item.getKategori());
                    }

                    boolean containInName = item.getNama().toLowerCase().contains(scope.toLowerCase());
                    boolean containInKategori = kategoriScope.equals("All")
                            || item.getKategori().toLowerCase().contains(kategoriScope.toLowerCase());

                    if ((containInKategori && containInName) || (scope.equals("") && kategoriScope.equals("All"))) {
                        items.add(item);
                    }
                }
            }

            fileReader.close();
            bufferedReader.close();
        } catch (Exception e) {
        }
        return items;
    }

    private void setCancelBtnHandler() {
        totalBayarTfield.setText("");
        if (!customerNameTfield.getText().isEmpty()) {
            if (setCancelBtn.getText().equals("Set")) {
                setCancelBtn.setText("Cancel");
                setVisibleElemen(true);
                customerNameTfield.setEditable(false);
                customerUuid = UUID.randomUUID().toString();
                customerName = customerNameTfield.getText();
            } else {
                setCancelBtn.setText("Set");
                setVisibleElemen(false);
                customerNameTfield.setEditable(true);
                jumlahTfield.setText("");
            }
            itemCartList.clear();
            dynamicGridPane();
        }
    }

    private void addObservaleListItem() {
        Integer jumlah = Integer.parseInt(jumlahTfield.getText());
        for (int i = 0; i < itemCartList.size(); i++) {
            if (itemCartList.get(i).getUuidItem().equals(choosenItem.getUuidItem())) {
                Integer newJumlah = itemCartList.get(i).getBanyak() + jumlah;
                itemCartList.get(i).setBanyak(newJumlah);
                itemCartList.get(i).setTotal(newJumlah * itemCartList.get(i).getHarga());
                return;
            }
        }
        ItemCart itemCart = new ItemCart();
        itemCart.setUuidItem(choosenItem.getUuidItem());
        itemCart.setBanyak(jumlah);
        itemCart.setNama(choosenItem.getNama());
        itemCart.setHarga(choosenItem.getHarga());
        itemCart.setTotal(jumlah * choosenItem.getHarga());
        itemCart.setJumlah(choosenItem.getJumlah());
        itemCart.setImage(choosenItem.getImage());
        itemCartList.add(itemCart);
    }

    private void lihatCart() {
        confirmed = false;
        CartController.setDetail(false);
        popupCart();
        totalBayarTfield.setText(String.valueOf(CartController.totalPembayaran));
        if (confirmed) {
            editFileItemList();
            setCancelBtnHandler();
        }
        dynamicGridPane();
    }

    private void editFileItemList() {
        try {
            FileReader fileReader = new FileReader("itemlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter("itemlist-temp.txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            StringTokenizer stringTokenizer;
            String data = bufferedReader.readLine();
            boolean exist;

            while (data != null) {
                exist = false;
                stringTokenizer = new StringTokenizer(data, ",");
                String uuidItem = stringTokenizer.nextToken();
                for (ItemCart itemCart : itemCartList) {
                    if (uuidItem.equals(itemCart.getUuidItem())) {
                        exist = true;
                        bufferedWriter.write(uuidItem + ",");
                        bufferedWriter.write(stringTokenizer.nextToken() + ",");
                        bufferedWriter.write(stringTokenizer.nextToken() + ",");
                        bufferedWriter.write(stringTokenizer.nextToken() + ",");
                        int jumlah = Integer.parseInt(stringTokenizer.nextToken()) - itemCart.getBanyak();
                        bufferedWriter.write(jumlah + ",");
                        bufferedWriter.write(stringTokenizer.nextToken() + ",");
                        bufferedWriter.write(stringTokenizer.nextToken());
                        break;
                    }
                }
                if (!exist) {
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

    private void setChoosenItem(Item item) {
        choosenItem = item;
        if (item != null) {
            namaLbl.setText(item.getNama());
            hargaLbl.setText(App.CURRENCY + " " + item.getHarga());
            File fileImage = new File("image/" + item.getImage());
            Image image = new Image(fileImage.toURI().toString());
            displayImage.setImage(image);
            if (item.getJumlah() > 0) {
                jumlahTfield.setEditable(true);
                jumlahTfield.setText("1");
            } else if (item.getJumlah() == 0) {
                jumlahTfield.setEditable(false);
                jumlahTfield.setText("0");
            }
            currencyLbl.setText(App.CURRENCY);
            totalLbl.setText(String.valueOf(Integer.parseInt(jumlahTfield.getText()) * item.getHarga()));
        } else {
            namaLbl.setText("");
            hargaLbl.setText("");
            displayImage.setImage(null);
        }
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
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.centerOnScreen();
            popup.showAndWait();
        } catch (Exception e) {
        }
    }

    static void closePopup() {
        popup.close();
    }

    private int totalAllItem(String totalStr) {
        int total;
        if (totalStr.isEmpty()) {
            total = 0;
        } else {
            total = Integer.parseInt(totalStr);
        }
        total += Integer.parseInt(jumlahTfield.getText()) * choosenItem.getHarga();
        return total;
    }

    private void setUpAllButtonKategori() {
        List<String> kategoriListTemp = new ArrayList<>();
        for (int i = 0; i < kategoriList.size(); i++) {
            if (!kategoriListTemp.contains(kategoriList.get(i))) {
                kategoriListTemp.add(kategoriList.get(i));
                addButtonKategori(kategoriList.get(i), kategoriListTemp.size());
            }
        }
        kategoriList = kategoriListTemp;
    }

    private void addButtonKategori(String newKategori, int row) {
        Button button = new Button(newKategori);
        button.getStyleClass().setAll("button-in-profile");
        button.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(165, 30);
        gridPaneKategori.add(button, 0, row);
        GridPane.setMargin(button, new Insets(10));
        button.setOnMouseClicked((event) -> {
            kategoriSelected.setText(newKategori);
            kategoriScope = newKategori;
            dynamicGridPane();
        });
    }

    private void searchAction() {
        scope = searchTfield.getText();
        dynamicGridPane();
    }

    private void selectedItemDisplay(boolean visibility) {
        tileSelectedItem.setVisible(visibility);
    }

}
