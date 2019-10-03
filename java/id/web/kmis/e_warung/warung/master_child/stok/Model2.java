package id.web.kmis.e_warung.warung.master_child.stok;

public class Model2 {

    private String msNo;
    private String msTglp;
    private String msSupp;
    private String msNopo;
    private String msKode;
    private String msNama;
    private String msJumlah;
    private String msSatuan;
    private String msHjual;

    public Model2(String msNo, String msTglp, String msSupp, String msNopo, String msKode, String msNama, String msJumlah, String msSatuan, String msHjual) {
        this.msNo = msNo;
        this.msTglp = msTglp;
        this.msSupp = msSupp;
        this.msNopo = msNopo;
        this.msKode = msKode;
        this.msNama = msNama;
        this.msJumlah = msJumlah;
        this.msSatuan = msSatuan;
        this.msHjual = msHjual;
    }

    public String getmsNo() {
        return msNo;
    }

    public String getmsTglp() {
        return msTglp;
    }

    public String getmsSupp() {
        return msSupp;
    }

    public String getmsNopo() {
        return msNopo;
    }

    public String getmsKode() {
        return msKode;
    }

    public String getmsNama() {
        return msNama;
    }

    public String getmsJumlah() {
        return msJumlah;
    }

    public String getmsSatuan() {
        return msSatuan;
    }

    public String getmsHjual() {
        return msHjual;
    }


}

