package com.app.hardik.studypdf;

//Model Class for storing information of Uploaded Pdf

public class Upload {
    public String name;
    public String url;
    public String price;
    public String parent;
    public String encryptname;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String name, String url, String price,String parent,String encryptname) {
        this.name = name;
        this.url = url;
        this.price = price;
        this.parent = parent;
        this.encryptname = encryptname;
    }

    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }
    public String getPrice(){
        return price;
    }
    public String getParent() {
        return parent;
    }
    public String getEncryptname() {
        return encryptname;
    }
}
