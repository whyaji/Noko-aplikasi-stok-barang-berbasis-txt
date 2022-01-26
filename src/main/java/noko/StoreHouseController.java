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
import javafx.scene.control.ScrollPane;
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
import noko.listener.AppListener;

public class StoreHouseController implements Initializable {

    @FXML
    private Button tambahBarangBtn;

    @FXML
    private ScrollPane scrollPaneStore;

    @FXML
    private GridPane gridPaneStore;

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
    private Label jumlahLbl;

    @FXML
    private Label kategoriLbl;

    @FXML
    private Button editBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private GridPane gridPaneKategori;

    @FXML
    private Label kategoriSelected;

    @FXML
    private ChoiceBox<String> sortChoiceBox;

    private List<String> kategoriList = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private AppListener appListener;

    static Stage popup;
    static boolean confirmed;

    static Item choosenItem;
    static boolean editMode;

    private String scope, kategoriScope;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        scope = "";
        setBtnHandler();
        setSortChoiceBox();
        setUpKategori();
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

    private void setBtnHandler() {
        tambahBarangBtn.setOnMouseClicked((event) -> {
            tambahBarang();
        });
        searchBtn.setOnMouseClicked((event) -> {
            searchAction();
        });
        searchTfield.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                searchAction();
            }
        });
        editBtn.setOnMouseClicked((event) -> {
            editDataSelected();
        });
        deleteBtn.setOnMouseClicked((event) -> {
            deleteDataSelected();
        });
    }

    private void setUpKategori() {
        confirmed = true;
        kategoriScope = "All";
        kategoriSelected.setText(kategoriScope);
        gridPaneKategori.getChildren().clear();
        kategoriList.clear();
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

    private void setChoosenItem(Item item) {
        choosenItem = item;
        if (item != null) {
            namaLbl.setText(item.getNama());
            hargaLbl.setText(App.CURRENCY + " " + String.valueOf(item.getHarga()));
            jumlahLbl.setText(String.valueOf(item.getJumlah()));
            kategoriLbl.setText(item.getKategori());
            File fileImage = new File("image/" + item.getImage());
            Image image = new Image(fileImage.toURI().toString());
            displayImage.setImage(image);
        } else {
            namaLbl.setText("");
            hargaLbl.setText("");
            jumlahLbl.setText("");
            kategoriLbl.setText("");
            displayImage.setImage(null);
        }
    }

    private void selectedItemDisplay(boolean visibility) {
        tileSelectedItem.setVisible(visibility);
        editBtn.setVisible(visibility);
        deleteBtn.setVisible(visibility);
    }

    private void deleteDataSelected() {
        confirmed = false;
        editFromFile();

        if (confirmed) {
            setUpKategori();
            dynamicGridPane();
        }
    }

    private void editFromFile() {
        try {
            FileReader fileReader = new FileReader("itemlist.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter("itemlist-temp.txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String data = bufferedReader.readLine();
            while (data != null) {
                if (!data.equals(choosenItem.getItemLine(PrimaryController.account.getUuid()))) {
                    bufferedWriter.write(data);
                    bufferedWriter.newLine();
                }

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
        deleteOldFileImage();
        confirmed = true;
    }

    private void deleteOldFileImage() {
        try {
            File file = new File("image/" + choosenItem.getImage());
            file.delete();
        } catch (Exception e) {
        }
    }

    private void editDataSelected() {
        editMode = true;

        confirmed = false;
        popupTambahBarang();

        if (confirmed) {

            setUpKategori();
            dynamicGridPane();
        }
        editMode = false;
    }

    private void searchAction() {
        scope = searchTfield.getText();
        dynamicGridPane();
    }

    private void tambahBarang() {
        confirmed = false;
        popupTambahBarang();

        if (confirmed) {

            setUpKategori();
            dynamicGridPane();
        }
    }

    private void popupTambahBarang() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("tambahbarang.fxml")));
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

}
