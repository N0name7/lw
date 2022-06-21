package ru.ignat.lw.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import ru.ignat.lw.dataclasses.Voucher;

import java.sql.*;

import static ru.ignat.lw.StartClass.bdconnect;

public class MainControllerCustomer {

    @FXML
    private Tab Voucher;

    @FXML
    private TableColumn<ru.ignat.lw.dataclasses.Voucher, Date> VoucherBegin;

    @FXML
    private TableColumn<Voucher, Integer> VoucherCount;

    @FXML
    private TableColumn<Voucher, Date> VoucherEnd;

    @FXML
    private TableColumn<Voucher, Integer> VoucherID;

    @FXML
    private TableColumn<Voucher, String> VoucherName;

    @FXML
    private TableColumn<Voucher, Double> VoucherPrice;

    @FXML
    private TableColumn<Voucher, String> VoucherService;

    @FXML
    private TableColumn<Voucher, String> VoucherTown;

    @FXML
    private TableView<Voucher> VoucherTable;

    @FXML
    private TextField VoucherSearch;

    @FXML
    private CheckBox HotVaucher;

    ObservableList<Voucher> voucherObservableList = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        String voucher_get_query = "select voucher_id,voucher_name,voucher_begins,voucher_ends,town_name,voucher_price, services_bundle,voucher_balance from voucher,towns where voucher.town_id=towns.town_id order by voucher_id;";
        try {
            Statement voucher_get = bdconnect.createStatement();

            ResultSet voucher = voucher_get.executeQuery(voucher_get_query);

            while (voucher.next()){
                Integer id = voucher.getInt(1);
                String name = voucher.getString(2);
                Date begin = voucher.getDate(3);
                Date end = voucher.getDate(4);
                String town = voucher.getString(5);
                Double price = voucher.getDouble(6);
                int bundle_id = voucher.getInt(7);
                Integer balance = voucher.getInt(8);
                String get_bundle_query = "select service_name, (select sum(service_price) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ; ";
                PreparedStatement get_bundle = bdconnect.prepareStatement(get_bundle_query);
                get_bundle.setInt(1,bundle_id);
                get_bundle.setInt(2,bundle_id);
                String service="";
                Double service_price = 0.0;
                ResultSet bundle = get_bundle.executeQuery();
                while (bundle.next()){
                    service=service+bundle.getString(1)+";\n" ;
                    service_price=bundle.getDouble(2);
                }
                price+=service_price;
                voucherObservableList.add(new Voucher(price,balance,id,name,town,service,begin,end));
            }
            VoucherID.setCellValueFactory(new PropertyValueFactory<>("id"));
            VoucherName.setCellValueFactory(new PropertyValueFactory<>("name"));
            VoucherBegin.setCellValueFactory(new PropertyValueFactory<>("begin"));
            VoucherEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
            VoucherTown.setCellValueFactory(new PropertyValueFactory<>("town"));
            VoucherService.setCellValueFactory(new PropertyValueFactory<>("service"));
            VoucherPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            VoucherCount.setCellValueFactory(new PropertyValueFactory<>("count"));

            VoucherTable.setItems(voucherObservableList);

            FilteredList<Voucher> filtered_voucher = new FilteredList <>(voucherObservableList,b->true);
            VoucherSearch.textProperty().addListener((observable,oldValue,newValue)->{
                filtered_voucher.setPredicate(voucherSearchModel->{

                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                        return true;
                    }
                    String searchKeyword = newValue.toLowerCase();
                    if(voucherSearchModel.getId().toString().toLowerCase().contains(searchKeyword)){
                        return true;
                    } else if(voucherSearchModel.getName().toLowerCase().contains(searchKeyword)){
                        return true;
                    } else if(voucherSearchModel.getBegin().toString().toLowerCase().contains(searchKeyword)){
                        return true;
                    }else if(voucherSearchModel.getEnd().toString().toLowerCase().contains(searchKeyword)){
                        return true;
                    }else if(voucherSearchModel.getTown().toLowerCase().contains(searchKeyword)){
                        return true;
                    } else if(voucherSearchModel.getService().toLowerCase().contains(searchKeyword)){
                        return true;
                    } else if(voucherSearchModel.getPrice().toString().toLowerCase().contains(searchKeyword)){
                        return true;
                    }else if(voucherSearchModel.getCount().toString().toLowerCase().contains(searchKeyword)){
                        return true;
                    }else {
                        return false;
                    }
                });
            });
            SortedList<Voucher> sortedVoucher = new SortedList<>(filtered_voucher);

            sortedVoucher.comparatorProperty().bind(VoucherTable.comparatorProperty());

            VoucherTable.setItems(sortedVoucher);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void HotVaucherChecked(MouseEvent event) {
        voucherObservableList.clear();
        if (HotVaucher.isSelected()) {
            String getHotVoucherQuery = "select voucher_id,voucher_name,voucher_begins,voucher_ends,town_name,voucher_price, services_bundle,voucher_balance from voucher,towns where voucher.town_id=towns.town_id and (DAYOFMONTH(voucher_begins) - DAYOFMONTH(now()))<=5 and month(voucher_begins)=month(now())and year(voucher_begins)=year(now()) order by voucher_id";
            try {
                Statement voucher_get = bdconnect.createStatement();
                ResultSet voucher = voucher_get.executeQuery(getHotVoucherQuery);
                while (voucher.next()) {
                    Integer id = voucher.getInt(1);
                    String name = voucher.getString(2);
                    Date begin = voucher.getDate(3);
                    Date end = voucher.getDate(4);
                    String town = voucher.getString(5);
                    Double price = voucher.getDouble(6);
                    int bundle_id = voucher.getInt(7);
                    Integer balance = voucher.getInt(8);
                    String get_bundle_query = "select service_name, (select sum(service_price) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ; ";
                    PreparedStatement get_bundle = bdconnect.prepareStatement(get_bundle_query);
                    get_bundle.setInt(1, bundle_id);
                    get_bundle.setInt(2, bundle_id);
                    String service = "";
                    Double service_price = 0.0;
                    ResultSet bundle = get_bundle.executeQuery();
                    while (bundle.next()) {
                        service = service + bundle.getString(1) + ";\n";
                        service_price = bundle.getDouble(2);
                    }
                    price += service_price;
                    voucherObservableList.add(new Voucher(price, balance, id, name, town, service, begin, end));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            VoucherTable.setItems(voucherObservableList);
        }else{
            String voucher_get_query = "select voucher_id,voucher_name,voucher_begins,voucher_ends,town_name,voucher_price, services_bundle,voucher_balance from voucher,towns where voucher.town_id=towns.town_id order by voucher_id;";
            try {
                Statement voucher_get = bdconnect.createStatement();
                ResultSet voucher = voucher_get.executeQuery(voucher_get_query);
                while (voucher.next()){
                    Integer id = voucher.getInt(1);
                    String name = voucher.getString(2);
                    Date begin = voucher.getDate(3);
                    Date end = voucher.getDate(4);
                    String town = voucher.getString(5);
                    Double price = voucher.getDouble(6);
                    int bundle_id = voucher.getInt(7);
                    Integer balance = voucher.getInt(8);
                    String get_bundle_query = "select service_name, (select sum(service_price) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ) from services,services_bundle where services_bundle.service_id = services.service_id and services_bundle.bundle_id= ? ; ";
                    PreparedStatement get_bundle = bdconnect.prepareStatement(get_bundle_query);
                    get_bundle.setInt(1,bundle_id);
                    get_bundle.setInt(2,bundle_id);
                    String service="";
                    Double service_price = 0.0;
                    ResultSet bundle = get_bundle.executeQuery();
                    while (bundle.next()){
                        service=service+bundle.getString(1)+";\n" ;
                        service_price=bundle.getDouble(2);
                    }
                    price+=service_price;
                    voucherObservableList.add(new Voucher(price,balance,id,name,town,service,begin,end));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
