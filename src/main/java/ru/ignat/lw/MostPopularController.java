package ru.ignat.lw;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.ignat.lw.StartClass.bdconnect;

public class MostPopularController {

    @FXML
    private ListView<String> MostPopularLV;

    ObservableList<MostPopularVoucher> mostPopularVoucherObservableList = FXCollections.observableArrayList();
    ObservableList<String> MostPopularL = FXCollections.observableArrayList();
    @FXML
    public void initialize() {

        String voucher_id_get_query = "select voucher_id, (count(sales_id)) from sales group by voucher_id order by (count(sales_id)) DESC;";
        try {
            Statement voucher_id_get = bdconnect.createStatement();
            ResultSet voucher_id = voucher_id_get.executeQuery(voucher_id_get_query);
            voucher_id.next();
            Integer max = voucher_id.getInt(2);
            Integer max_id = voucher_id.getInt(1);
            mostPopularVoucherObservableList.add(new MostPopularVoucher(max_id,max));

            while (voucher_id.next()) {
                Integer id = voucher_id.getInt(1);
                Integer count = voucher_id.getInt(2);
                if (count.equals(max)) {
                    mostPopularVoucherObservableList.add(new MostPopularVoucher(id, count));
                } else {
                    break;
                }
            }
            for(int i = 0;i<mostPopularVoucherObservableList.size();i++){
            String get_most_popular_query = "select voucher_id,voucher_name,voucher_price, services_bundle from voucher where voucher_id=" + mostPopularVoucherObservableList.get(i).id + " ;";
            Statement get_most_popular = bdconnect.createStatement();
            ResultSet most_popular = get_most_popular.executeQuery(get_most_popular_query);
            most_popular.next();
            Integer voucherID = most_popular.getInt(1);
            String voucher_name = most_popular.getString(2);
            Double voucher_price = most_popular.getDouble(3);
            Integer bundle_id = most_popular.getInt(4);
            String get_bundle_query = "select service_name, (select sum(service_price) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ; ";
            PreparedStatement get_bundle = bdconnect.prepareStatement(get_bundle_query);
            get_bundle.setInt(1, bundle_id);
            get_bundle.setInt(2, bundle_id);
            String service = "";
            Double service_price = 0.0;
            Double price = 0.0;
            ResultSet bundle = get_bundle.executeQuery();
            while (bundle.next()) {
                service = service + bundle.getString(1) + ";";
                service_price = bundle.getDouble(2);
                price+=service_price;
            }
            Double final_price = voucher_price +price;
            String add =voucherID.toString()+ ") "+voucher_name +" Набор услуг: "+service+" Цена: "+final_price.toString();
            MostPopularL.add(add);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        MostPopularLV.setItems(MostPopularL);
    }
}
