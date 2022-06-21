package ru.ignat.lw;

import java.sql.Date;

public class Voucher {
    Integer count,id;
    Double price;
    String name,town,service;
    Date begin,end;

    public Voucher(Double price, int count, int id, String name, String town, String service, Date begin, Date end) {
        this.price = price;
        this.count = count;
        this.id = id;
        this.name = name;
        this.town = town;
        this.service = service;
        this.begin = begin;
        this.end = end;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTown() {
        return town;
    }

    public String getService() {
        return service;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }
}
