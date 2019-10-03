package id.web.kmis.e_warung.warung.master_child.penjualan;

public class Model_J {

    private String sGambar;
    private String sKode;
    private String sNama;
    private String sHjual;
    private String sQty;

    public Model_J(String sGambar, String sKode, String sNama, String sHjual, String sQty) {
        this.sGambar = sGambar;
        this.sKode = sKode;
        this.sNama = sNama;
        this.sHjual = sHjual;
        this.sQty = sQty;
    }

    public String getjGambar() {
        return sGambar;
    }

    public String getjKode() {
        return sKode;
    }

    public String getjNama() {
        return sNama;
    }

    public String getjHjual() {
        return sHjual;
    }

    public String getjQty() {
        return sQty;
    }
}

