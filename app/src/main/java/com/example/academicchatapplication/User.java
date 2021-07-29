package com.example.academicchatapplication;

public class User {
    private String userid;
    private String username;
    private String useremail;
    private String userpass;
    private String userimage;
    private String usertype;
    private String usercode;
    private String userstatus;

    public User() {
    }

    public User(String userid, String username, String useremail, String userpass, String userimage, String usertype, String usercode, String userstatus) {
        this.userid = userid;
        this.username = username;
        this.useremail = useremail;
        this.userpass = userpass;
        this.userimage = userimage;
        this.usertype = usertype;
        this.usercode = usercode;
        this.userstatus = userstatus;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() { return useremail; }

    public void setUseremail(String useremail) { this.useremail = useremail; }

    public String getUserpass() { return userpass; }

    public void setUserpass(String userpass) { this.userpass = userpass; }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsertype() { return usertype; }

    public void setUsertype(String usertype) { this.usertype = usertype; }

    public String getUsercode() { return usercode; }

    public void setUsercode(String usercode) { this.usercode = usercode; }

    public String getUserstatus() { return userstatus; }

    public void setUserstatus(String userstatus) { this.userstatus = userstatus; }
}