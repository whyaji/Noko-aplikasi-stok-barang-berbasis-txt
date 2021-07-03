package noko.item;

public class ItemCart extends Item {
    Integer total, banyak;

    public Integer getTotal() {
        return total;
    }
    public Integer getBanyak() {
        return banyak;
    }
    public void setBanyak(Integer banyak) {
        this.banyak = banyak;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
}