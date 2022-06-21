package ru.ignat.lw.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.ignat.lw.StartClass;
import ru.ignat.lw.dataclasses.Customer;
import ru.ignat.lw.dataclasses.Sale;
import ru.ignat.lw.dataclasses.Voucher;

import java.io.IOException;
import java.sql.*;

import static ru.ignat.lw.StartClass.bdconnect;
import static ru.ignat.lw.StartClass.st;

public class MainController{



    @FXML
    private Tab Client;

    @FXML
    private TableColumn<Customer, String> ClientDiscount;

    @FXML
    private TableColumn<Customer, String> ClientDockNum;

    @FXML
    private TableColumn<Customer, String> ClientFIO;

    @FXML
    private TableColumn<Customer, Integer> ClientID;

    @FXML
    private TableColumn<Customer, String> ClientPhoneNum;

    @FXML
    private TableView<Customer> ClientTable;

    @FXML
    private TextField ClientSearch;

    @FXML
    private Tab Sale;

    @FXML
    private TableColumn<ru.ignat.lw.dataclasses.Sale, String> SaleClienFIO;

    @FXML
    private TableColumn<Sale, Integer> SaleCountVoucher;

    @FXML
    private TableColumn<Sale, Date> SaleDate;

    @FXML
    private TableColumn<Sale, String> SaleEmployeeFIO;

    @FXML
    private TableColumn<Sale, Integer> SaleID;

    @FXML
    private TableColumn<Sale, String> SaleVoucherName;

    public TableColumn<Sale,Double> PriceWithOutDiscount;

    public TableColumn<Sale,Double> PriceWithDiscount;

    @FXML
    private TableView<Sale> SaleTable;
    @FXML
    private TextField SaleSearch;


    @FXML
    private Button LostByDiscount;

    @FXML
    private Button MostBying;

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

    ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
    ObservableList<Sale> saleObservableList = FXCollections.observableArrayList();
    ObservableList<Voucher> voucherObservableList = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        String customer_get_query = "select customer_id,concat(customer_surname,' ',customer_name,' ',customer_patronymic),phone_number,concat(s_dock,' ',№_dock),discount_count,discount_type from customer,discount where customer.discount_id = discount.discount_id;";
        String sale_get_query = "select sales_id,concat( employee_surname,' ',employee_name,' ',employee_patronymic),voucher_name, concat(customer_surname,' ',customer_name,' ',customer_patronymic),sale_balance ,sale_time,sales.voucher_id,sales.customer_id from sales,employee,customer,voucher where sales.employee_id=employee.employee_id and sales.voucher_id=voucher.voucher_id and sales.customer_id=customer.customer_id;";
        String voucher_get_query = "select voucher_id,voucher_name,voucher_begins,voucher_ends,town_name,voucher_price, services_bundle,voucher_balance from voucher,towns where voucher.town_id=towns.town_id order by voucher_id;";
        try {
            Statement customer_get = bdconnect.createStatement();
            Statement sale_get = bdconnect.createStatement();
            Statement voucher_get = bdconnect.createStatement();

            ResultSet customer = customer_get.executeQuery(customer_get_query);
            ResultSet sale = sale_get.executeQuery(sale_get_query);
            ResultSet voucher = voucher_get.executeQuery(voucher_get_query);



            while (customer.next()){
                Integer id =customer.getInt(1);
                String fio = customer.getString(2);
                String phone_num = customer.getString(3);
                String num_doc = customer.getString(4);
                String discount = customer.getString(6)+": "+customer.getInt(5)+" %";
                customerObservableList.add(new Customer(id,fio,phone_num,num_doc,discount));
            }

            ClientID.setCellValueFactory(new PropertyValueFactory<>("id"));
            ClientFIO.setCellValueFactory(new PropertyValueFactory<>("fio"));
            ClientPhoneNum.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
            ClientDockNum.setCellValueFactory(new PropertyValueFactory<>("document_number"));
            ClientDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

            ClientTable.setItems(customerObservableList);

            FilteredList<Customer> filtered_customer = new FilteredList <>(customerObservableList,b->true);

            ClientSearch.textProperty().addListener((observable,oldValue,newValue)->{
                filtered_customer.setPredicate(customerSearchModel->{

                    if(newValue.isEmpty()||newValue.isBlank()|| newValue == null){
                        return true;
                    }
                    String searchKeyword = newValue.toLowerCase();
                    if(customerSearchModel.getId().toString().toLowerCase().contains(searchKeyword)){
                        return true;
                    } else if (customerSearchModel.getFio().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else if (customerSearchModel.getPhone_number().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else if (customerSearchModel.getDocument_number().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else if (customerSearchModel.getDiscount().toLowerCase().contains(searchKeyword)) {
                        return true;
                    }else
                        return false;
                });
            });
            SortedList<Customer> sortedCustomer = new SortedList<>(filtered_customer);

            sortedCustomer.comparatorProperty().bind(ClientTable.comparatorProperty());

            ClientTable.setItems(sortedCustomer);

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
            while (sale.next()) {
                Integer id = sale.getInt(1);
                String employee_fio = sale.getString(2);
                String voucher_name = sale.getString(3);
                String customer_fio = sale.getString(4);
                Integer sale_balance = sale.getInt(5);
                Date time = sale.getDate(6);
                Integer v_id = sale.getInt(7);
                Integer c_id = sale.getInt(8);
                Double price_without_discount = 0.0;
                Double price_with_discount = 0.0;

                CallableStatement get_price = bdconnect.prepareCall("call calculate(?,?);");
                get_price.setInt(1, v_id);
                get_price.setInt(2, c_id);
                ResultSet price = get_price.executeQuery();
                price.next();
                price_without_discount = price.getDouble(1);
                price_with_discount = price.getDouble(2);

                saleObservableList.add(new Sale(id, sale_balance, employee_fio, customer_fio, voucher_name, time, price_without_discount, price_with_discount));
            }
                SaleID.setCellValueFactory(new PropertyValueFactory<>("id"));
                SaleEmployeeFIO.setCellValueFactory(new PropertyValueFactory<>("fio_employee"));
                SaleVoucherName.setCellValueFactory(new PropertyValueFactory<>("voucher_name"));
                SaleClienFIO.setCellValueFactory(new PropertyValueFactory<>("fio_customer"));
                SaleCountVoucher.setCellValueFactory(new PropertyValueFactory<>("count"));
                SaleDate.setCellValueFactory(new PropertyValueFactory<>("sale_date"));
                PriceWithDiscount.setCellValueFactory(new PropertyValueFactory<>("price_with_discount"));
                PriceWithOutDiscount.setCellValueFactory(new PropertyValueFactory<>("price_without_discount"));

                FilteredList<Sale> filtered_sale = new FilteredList <>(saleObservableList,b->true);

                SaleSearch.textProperty().addListener((observable,oldValue,newValue)->{
                    filtered_sale.setPredicate(customerSearchModel->{

                        if(newValue.isEmpty()||newValue.isBlank()|| newValue == null){
                            return true;
                        }
                        String searchKeyword = newValue.toLowerCase();
                        if(customerSearchModel.getId().toString().toLowerCase().contains(searchKeyword)){
                            return true;
                        } else if (customerSearchModel.getFio_employee().toLowerCase().contains(searchKeyword)) {
                            return true;
                        } else if (customerSearchModel.getCount().toString().toLowerCase().contains(searchKeyword)) {
                            return true;
                        } else if (customerSearchModel.getFio_customer().toLowerCase().contains(searchKeyword)) {
                            return true;
                        } else if (customerSearchModel.getVoucher_name().toLowerCase().contains(searchKeyword)) {
                            return true;
                        }else if (customerSearchModel.getSale_date().toString().toLowerCase().contains(searchKeyword)) {
                            return true;
                        }else if (customerSearchModel.getPrice_without_discount().toString().toLowerCase().contains(searchKeyword)) {
                            return true;
                        }else if (customerSearchModel.getPrice_with_discount().toString().toLowerCase().contains(searchKeyword)) {
                            return true;
                        }else
                            return false;
                    });
                });
                SortedList<Sale> sortedSale = new SortedList<>(filtered_sale);

                sortedSale.comparatorProperty().bind(SaleTable.comparatorProperty());
                SaleTable.setItems(sortedSale);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void ClientAddBtnClicked(MouseEvent event) throws IOException {
        Stage dialogStage = new Stage();//создаём новое окно
        dialogStage.setTitle("Добавление клиента в базу.");//задаём название окна
        dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
        dialogStage.initOwner(st);// указываем главное окно
        //создаём новую сцену и добавляем в неё панель со всем содержимым
        FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/customer-add-page.fxml"));//загружается содержимое окна из FXML файла
        Scene scene = new Scene(loader.load());
        dialogStage.setScene(scene);
        //показываем окно
        dialogStage.showAndWait();
        customerObservableList.clear();
        try { String customer_get_query = "select customer_id,concat(customer_surname,' ',customer_name,' ',customer_patronymic),phone_number,concat(s_dock,' ',№_dock),discount_count,discount_type from customer,discount where customer.discount_id = discount.discount_id;";
            Statement customer_get = bdconnect.createStatement();
            ResultSet customer = customer_get.executeQuery(customer_get_query);
            while (customer.next()){
                Integer id =customer.getInt(1);
                String fio = customer.getString(2);
                String phone_num = customer.getString(3);
                String num_doc = customer.getString(4);
                String discount = customer.getString(6)+": "+customer.getInt(5)+" %";
                customerObservableList.add(new Customer(id,fio,phone_num,num_doc,discount));
            }
            ClientTable.setItems(customerObservableList);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    void ClientRemoveBtnClicked(MouseEvent event) {
        Customer selected = ClientTable.getSelectionModel().getSelectedItem();
        if(selected == null){

        }else{
            String remove ="DELETE FROM customer WHERE (customer_id = ?);";
            try{
                PreparedStatement removeCustomer= bdconnect.prepareStatement(remove);
                removeCustomer.setInt(1,selected.getId());
                removeCustomer.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            customerObservableList.clear();
            try { String customer_get_query = "select customer_id,concat(customer_surname,' ',customer_name,' ',customer_patronymic),phone_number,concat(s_dock,' ',№_dock),discount_count,discount_type from customer,discount where customer.discount_id = discount.discount_id;";
                Statement customer_get = bdconnect.createStatement();
                ResultSet customer = customer_get.executeQuery(customer_get_query);
                while (customer.next()){
                    Integer id =customer.getInt(1);
                    String fio = customer.getString(2);
                    String phone_num = customer.getString(3);
                    String num_doc = customer.getString(4);
                    String discount = customer.getString(6)+": "+customer.getInt(5)+" %";
                    customerObservableList.add(new Customer(id,fio,phone_num,num_doc,discount));
                }

                ClientID.setCellValueFactory(new PropertyValueFactory<>("id"));
                ClientFIO.setCellValueFactory(new PropertyValueFactory<>("fio"));
                ClientPhoneNum.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
                ClientDockNum.setCellValueFactory(new PropertyValueFactory<>("document_number"));
                ClientDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

                ClientTable.setItems(customerObservableList);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void SaleAddBtnClicked(MouseEvent event) {
        try {
            Stage dialogStage = new Stage();//создаём новое окно
            dialogStage.setTitle("Добавление продажи в базу.");//задаём название окна
            dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
            dialogStage.initOwner(st);// указываем главное окно
            //создаём новую сцену и добавляем в неё панель со всем содержимым
            FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/sale-add-page.fxml"));//загружается содержимое окна из FXML файла
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            //показываем окно
            dialogStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
        String sale_get_query = "select sales_id,concat( employee_surname,' ',employee_name,' ',employee_patronymic),voucher_name, concat(customer_surname,' ',customer_name,' ',customer_patronymic),sale_balance ,sale_time,sales.voucher_id,sales.customer_id from sales,employee,customer,voucher where sales.employee_id=employee.employee_id and sales.voucher_id=voucher.voucher_id and sales.customer_id=customer.customer_id;";
        saleObservableList.clear();
        try {
            Statement sale_get = bdconnect.createStatement();

            ResultSet sale = sale_get.executeQuery(sale_get_query);

            while (sale.next()) {
                Integer id = sale.getInt(1);
                String employee_fio = sale.getString(2);
                String voucher_name = sale.getString(3);
                String customer_fio = sale.getString(4);
                Integer sale_balance = sale.getInt(5);
                Date time = sale.getDate(6);
                Integer v_id = sale.getInt(7);
                Integer c_id = sale.getInt(8);
                Double price_without_discount = 0.0;
                Double price_with_discount = 0.0;

                CallableStatement get_price = bdconnect.prepareCall("call calculate(?,?);");
                get_price.setInt(1, v_id);
                get_price.setInt(2, c_id);
                ResultSet price = get_price.executeQuery();
                price.next();
                price_without_discount = price.getDouble(1);
                price_with_discount = price.getDouble(2);

                saleObservableList.add(new Sale(id, sale_balance, employee_fio, customer_fio, voucher_name, time, price_without_discount, price_with_discount));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    void SaleRemoveBtnClicked(MouseEvent event) {
        Sale selected = SaleTable.getSelectionModel().getSelectedItem();
        if(selected == null){

        }else {
            try {
                CallableStatement get_price = bdconnect.prepareCall("call remove_sale(?);");
                get_price.setInt(1, SaleTable.getSelectionModel().getSelectedItem().getId());
                get_price.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String sale_get_query = "select sales_id,concat( employee_surname,' ',employee_name,' ',employee_patronymic),voucher_name, concat(customer_surname,' ',customer_name,' ',customer_patronymic),sale_balance ,sale_time,sales.voucher_id,sales.customer_id from sales,employee,customer,voucher where sales.employee_id=employee.employee_id and sales.voucher_id=voucher.voucher_id and sales.customer_id=customer.customer_id;";
           saleObservableList.clear();
            try {
               Statement sale_get = bdconnect.createStatement();

               ResultSet sale = sale_get.executeQuery(sale_get_query);

               while (sale.next()) {
                   Integer id = sale.getInt(1);
                   String employee_fio = sale.getString(2);
                   String voucher_name = sale.getString(3);
                   String customer_fio = sale.getString(4);
                   Integer sale_balance = sale.getInt(5);
                   Date time = sale.getDate(6);
                   Integer v_id = sale.getInt(7);
                   Integer c_id = sale.getInt(8);
                   Double price_without_discount = 0.0;
                   Double price_with_discount = 0.0;

                   CallableStatement get_price = bdconnect.prepareCall("call calculate(?,?);");
                   get_price.setInt(1, v_id);
                   get_price.setInt(2, c_id);
                   ResultSet price = get_price.executeQuery();
                   price.next();
                   price_without_discount = price.getDouble(1);
                   price_with_discount = price.getDouble(2);

                   saleObservableList.add(new Sale(id, sale_balance, employee_fio, customer_fio, voucher_name, time, price_without_discount, price_with_discount));
               }
           }catch (SQLException e){
               e.printStackTrace();
           }
        }
        voucherObservableList.clear();
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

    @FXML
    void VoucherAddBtnClicked(MouseEvent event) {
        try {
            Stage dialogStage = new Stage();//создаём новое окно
            dialogStage.setTitle("Добавление путёвки в базу.");//задаём название окна
            dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
            dialogStage.initOwner(st);// указываем главное окно
            //создаём новую сцену и добавляем в неё панель со всем содержимым
            FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/vouch-add-page.fxml"));//загружается содержимое окна из FXML файла
            Scene scene = null;
            scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            //показываем окно
            dialogStage.showAndWait();
        } catch (IOException e) {
        e.printStackTrace();
        }
        voucherObservableList.clear();
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

    @FXML
    void VoucherRemoveBtnClicked(MouseEvent event) {
        Voucher selected = VoucherTable.getSelectionModel().getSelectedItem();
        if(selected == null){

        }else{
            String remove ="DELETE FROM voucher WHERE (voucher_id = ?);";
            try{
                PreparedStatement removeVoucher= bdconnect.prepareStatement(remove);
                removeVoucher.setInt(1,selected.getId());
                removeVoucher.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            voucherObservableList.clear();
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
    @FXML
    void LostByDiscountClicked(MouseEvent event) {
        Double lost =0.0;
        for(int i =0;i<saleObservableList.size();i++){
            Double tmp =saleObservableList.get(i).getPrice_without_discount()-saleObservableList.get(i).getPrice_with_discount();
            lost=lost+tmp;
        }
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Потери");
        error.setHeaderText("Недополученная из-за скидок прибыль");
        error.setContentText("Недополученная из-за скидок прибыль равна: "+lost+" р. ");
        error.showAndWait();
    }

    @FXML
    void MostByingClicked(MouseEvent event) {
        try {
            Stage dialogStage = new Stage();//создаём новое окно
            dialogStage.setTitle("Самые продоваемые путёвки.");//задаём название окна
            dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
            dialogStage.initOwner(st);// указываем главное окно
            //создаём новую сцену и добавляем в неё панель со всем содержимым
            FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/vouch-popular-page.fxml"));//загружается содержимое окна из FXML файла
            Scene scene = null;
            scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            //показываем окно
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    void addDiscount(MouseEvent event) {
        try{
            Stage dialogStage = new Stage();//создаём новое окно
            dialogStage.setTitle("Добавление скидки в базу.");//задаём название окна
            dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
            dialogStage.initOwner(st);// указываем главное окно
            //создаём новую сцену и добавляем в неё панель со всем содержимым
            FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/discount-add-page.fxml"));//загружается содержимое окна из FXML файла
            Scene scene = null;
            scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            //показываем окно
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void addEmployee(MouseEvent event) {
        try{
            Stage dialogStage = new Stage();//создаём новое окно
            dialogStage.setTitle("Добавление сотрудника в базу.");//задаём название окна
            dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
            dialogStage.initOwner(st);// указываем главное окно
            //создаём новую сцену и добавляем в неё панель со всем содержимым
            FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/employee-add-page.fxml"));//загружается содержимое окна из FXML файла
            Scene scene = null;
            scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            //показываем окно
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void addService(MouseEvent event) {
        try{
            Stage dialogStage = new Stage();//создаём новое окно
            dialogStage.setTitle("Добавление услуги в базу.");//задаём название окна
            dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
            dialogStage.initOwner(st);// указываем главное окно
            //создаём новую сцену и добавляем в неё панель со всем содержимым
            FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/service-add-page.fxml"));//загружается содержимое окна из FXML файла
            Scene scene = null;
            scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            //показываем окно
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AddTown(MouseEvent event) {
        try{
            Stage dialogStage = new Stage();//создаём новое окно
            dialogStage.setTitle("Добавление города в базу.");//задаём название окна
            dialogStage.initModality(Modality.WINDOW_MODAL);//задаём зависимость от основного окна
            dialogStage.initOwner(st);// указываем главное окно
            //создаём новую сцену и добавляем в неё панель со всем содержимым
            FXMLLoader loader = new FXMLLoader(StartClass.class.getResource("fxml/town-add-page.fxml"));//загружается содержимое окна из FXML файла
            Scene scene = null;
            scene = new Scene(loader.load());
            dialogStage.setScene(scene);
            //показываем окно
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
