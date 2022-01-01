package noko.item;

public class ItemAnalytics {
    private String akunUuid, customerUuid, customerNama, date;
    private Integer totalPembayaran, banyakItem;
    
    public String getAkunUuid() {
        return akunUuid;
    }
    public void setAkunUuid(String akunUuid) {
        this.akunUuid = akunUuid;
    }
    public String getCustomerUuid() {
        return customerUuid;
    }
    public void setCustomerUuid(String customerUuid) {
        this.customerUuid = customerUuid;
    }
    public String getCustomerNama() {
        return customerNama;
    }
    public void setCustomerNama(String customerNama) {
        this.customerNama = customerNama;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Integer getTotalPembayaran() {
        return totalPembayaran;
    }
    public void setTotalPembayaran(Integer totalPembayaran) {
        this.totalPembayaran = totalPembayaran;
    }
    public Integer getBanyakItem() {
        return banyakItem;
    }
    public void setBanyakItem(Integer banyakItem) {
        this.banyakItem = banyakItem;
    }
}
