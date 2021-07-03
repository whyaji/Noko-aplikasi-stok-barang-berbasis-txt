package noko.item;

public class Item {
    private String uuidItem, nama, kategori, image;
    private Integer harga, jumlah;
    
    public String getUuidItem() {
        return uuidItem;
    }

    public void setUuidItem(String uuidItem) {
        this.uuidItem = uuidItem;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getHarga() {
        return harga;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }

    public Integer getJumlah() {
        return jumlah;
    }

    public void setJumlah(Integer jumlah) {
        this.jumlah = jumlah;
    }

    public String getItemLine(String userUuid){
        return uuidItem + "," + userUuid + "," + nama + "," + harga + "," + jumlah + "," + kategori + "," + image;
    }
}
