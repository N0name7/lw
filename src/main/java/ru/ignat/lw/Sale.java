package ru.ignat.lw;

import java.sql.Date;

public class Sale{
        Integer id,count;
        String fio_employee,fio_customer, voucher_name;
        Date sale_date;

        Double price_without_discount,price_with_discount;

        public Sale(int id, int count, String fio_employee, String fio_customer, String voucher_name, Date sale_date, Double price_without_discount, Double price_with_discount) {
                this.id = id;
                this.count = count;
                this.fio_employee = fio_employee;
                this.fio_customer = fio_customer;
                this.voucher_name = voucher_name;
                this.sale_date = sale_date;
                this.price_without_discount = price_without_discount;
                this.price_with_discount = price_with_discount;
        }

        public Integer getId() {
                return id;
        }

        public Integer getCount() {
                return count;
        }

        public String getFio_employee() {
                return fio_employee;
        }

        public String getFio_customer() {
                return fio_customer;
        }

        public String getVoucher_name() {
                return voucher_name;
        }

        public Date getSale_date() {
                return sale_date;
        }

        public Double getPrice_without_discount() {
                return price_without_discount;
        }

        public Double getPrice_with_discount() {
                return price_with_discount;
        }
}
