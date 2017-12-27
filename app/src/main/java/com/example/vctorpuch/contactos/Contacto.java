package com.example.vctorpuch.contactos;

public class Contacto {
    private int id;
    private String name;
    private String number;
    private byte[] image;

    public Contacto(String name, String number, byte[] image, int id) {
        this.name = name;
        this.number = number;
        this.image = image;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
