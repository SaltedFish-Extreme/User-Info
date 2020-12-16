package com.xianyu.myuser;

public class Users {
    public Integer Id;
    public String name;
    public String pwd;
    public String mail;
    public String phone;
    public Integer sex;

    public Users() {
    }

    public Users(Integer id, String name, String pwd, String mail, String phone, Integer sex) {
        Id = id;
        this.name = name;
        this.pwd = pwd;
        this.mail = mail;
        this.phone = phone;
        this.sex = sex;
    }
}
