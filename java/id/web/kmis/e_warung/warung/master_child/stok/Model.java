package id.web.kmis.e_warung.warung.master_child.stok;

public class Model {

    private String sNo;
    private String product;
    private String category;
    private String price;
    private String hjual;

    public Model(String sNo, String product, String category, String price, String hjual) {
        this.sNo = sNo;
        this.product = product;
        this.category = category;
        this.price = price;
        this.hjual = hjual;
    }

    public String getsNo() {
        return sNo;
    }

    public String getProduct() {
        return product;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public String getHjual() {
        return hjual;
    }

    public void setHjual(String hjual) {
        this.hjual = hjual;
    }
}

