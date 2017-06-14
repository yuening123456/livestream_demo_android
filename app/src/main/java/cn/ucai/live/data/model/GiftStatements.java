package cn.ucai.live.data.model;

public class GiftStatements {


    /**
     * id : 671
     * uname : 123yuening
     * anchor : 010aa
     * giftid : 1
     * giftnum : 1
     * gdate : 1497411120147
     * gname : 红包
     * gurl : hani_gift_1.png
     * gprice : 1
     */

    private int id;
    private String uname;
    private String anchor;
    private int giftid;
    private int giftnum;
    private String gdate;
    private String gname;
    private String gurl;
    private int gprice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public int getGiftid() {
        return giftid;
    }

    public void setGiftid(int giftid) {
        this.giftid = giftid;
    }

    public int getGiftnum() {
        return giftnum;
    }

    public void setGiftnum(int giftnum) {
        this.giftnum = giftnum;
    }

    public String getGdate() {
        return gdate;
    }

    public void setGdate(String gdate) {
        this.gdate = gdate;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGurl() {
        return gurl;
    }

    public void setGurl(String gurl) {
        this.gurl = gurl;
    }

    public int getGprice() {
        return gprice;
    }

    public void setGprice(int gprice) {
        this.gprice = gprice;
    }

    @Override
    public String toString() {
        return "GiftStatements{" +
                "id=" + id +
                ", uname='" + uname + '\'' +
                ", anchor='" + anchor + '\'' +
                ", giftid=" + giftid +
                ", giftnum=" + giftnum +
                ", gdate='" + gdate + '\'' +
                ", gname='" + gname + '\'' +
                ", gurl='" + gurl + '\'' +
                ", gprice=" + gprice +
                '}';
    }
}