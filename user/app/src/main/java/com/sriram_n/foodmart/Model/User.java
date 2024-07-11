package com.sriram_n.foodmart.Model;

public class User {
    private String Name;
    private String Phone;
    private String IsStaff;
    private String Email;
    private String Address;
    private String Image;

    public User() {
    }

    public User(String name, String email, String phone, String address) {
        Name = name;
        IsStaff = "false";
        Email = email;
        Phone = phone;
        Address = address;

    }

    public User(String img) {
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String img) {
        Image = img;
    }
}
