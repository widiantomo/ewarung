package id.web.kmis.e_warung.warung.master_child.penjualan;

public class Model_N {

    private String sKbarang;
    private String sNbarang;
    private int sHarga;
    private int sJumlah;
    private int sTotal;

    public Model_N(String sKbarang, String sNbarang, int sHarga, int sJumlah) {
        this.sNbarang = sNbarang;
        this.sHarga = sHarga;
        this.sJumlah = sJumlah;
        this.sKbarang = sKbarang;
    }

    public void setJumlah(int sJumlah) {
        this.sJumlah = sJumlah;
    }

    public String getsNbarang() {
        return sNbarang;
    }

    public int getsHarga() {
        return sHarga;
    }

    public int getsJumlah() {
        return sJumlah;
    }

    public int getsTotal() {
        sTotal = sHarga * sJumlah;
        return sTotal;
    }

    public String getsKbarang() {
        return sKbarang;
    }

}

