package ru.ignat.lw.dataclasses;

public class Customer {
    Integer id;
    String fio,phone_number,document_number,discount;

    public Customer(Integer id, String fio, String phone_number, String document_number, String discount) {
        this.id = id;
        this.fio = fio;
        this.phone_number = phone_number;
        this.document_number = document_number;
        this.discount = discount;
    }

    public Integer getId() {
        return id;
    }

    public String getFio() {
        return fio;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getDocument_number() {
        return document_number;
    }

    public String getDiscount() {
        return discount;
    }
}
