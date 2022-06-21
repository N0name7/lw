package ru.ignat.lw.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.FormatStringConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static ru.ignat.lw.controllers.CustomerAddController.isFelled;
import static ru.ignat.lw.StartClass.bdconnect;

public class VouchAddController {

    @FXML
    private ListView<String> ServiceBundl;

    @FXML
    private ListView<String> TownLV;

    @FXML
    private TextField VoucherBeginTF;

    @FXML
    private TextField VoucherCountTF;

    @FXML
    private TextField VoucherEndTF;

    @FXML
    private TextField VoucherNameTF;

    @FXML
    private TextField VoucherPriceTF;
    @FXML
    public void initialize() {
        String town_get_query = "select town_name from towns;";
        ObservableList<String> TownsList =FXCollections.observableArrayList();
        try{
            Statement getTownList = bdconnect.createStatement();
            ResultSet Towns = getTownList.executeQuery(town_get_query);
            while (Towns.next()){
                TownsList.add(Towns.getString(1));
            }
            TownLV.setItems(TownsList);
        }catch (SQLException e){
            e.printStackTrace();
        }
        String service_bundle_get_query = "select bundle_id,service_name,service_price from services_bundle,services where services_bundle.service_id=services.service_id;";
        ObservableList<String> ServiceBundleList =FXCollections.observableArrayList();
        try{
            Statement getServiceBundleList = bdconnect.createStatement();
            ResultSet ServiceBundles = getServiceBundleList.executeQuery(service_bundle_get_query);
            Integer preview_id = -1;
            String service = "";
            while (ServiceBundles.next()){
                Integer bundle_id = ServiceBundles.getInt(1);
                if(!Objects.equals(preview_id, bundle_id)){
                    preview_id=bundle_id;
                    if(!service.equals("")) {
                        ServiceBundleList.add(service);
                    }
                    service = "Набор усллуг № :"+bundle_id+"\n";
                }
                service=service+ServiceBundles.getString(2)+": " ;
                service=service+ServiceBundles.getString(3)+" р.;\n" ;
            }
            ServiceBundl.setItems(ServiceBundleList);
        }catch (SQLException e){
            e.printStackTrace();
        }
        String date_pattern = "yyyy.MM.dd";
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern(date_pattern);
        TextFormatter <DateTimeFormatter> text_formatter1 = new TextFormatter <DateTimeFormatter>(new FormatStringConverter <DateTimeFormatter>(date_formatter.toFormat()));
        VoucherBeginTF.setTextFormatter(text_formatter1);
        TextFormatter <DateTimeFormatter> text_formatter2 = new TextFormatter <DateTimeFormatter>(new FormatStringConverter <DateTimeFormatter>(date_formatter.toFormat()));
        VoucherEndTF.setTextFormatter(text_formatter2);

        VoucherCountTF.setTextFormatter(new TextFormatter<>(new FormatStringConverter <DecimalFormat>(new DecimalFormat())));
        VoucherPriceTF.setTextFormatter(new TextFormatter<>(new FormatStringConverter <DecimalFormat>(new DecimalFormat())));
    }
    @FXML
    void AddBtnClicked(MouseEvent event) {
        String voucher_name = VoucherNameTF.getText();
        String voucher_begins = date_form(VoucherBeginTF.getText()) ;
        String voucher_ends = date_form(VoucherEndTF.getText()) ;
        String voucher_price =math_form(VoucherPriceTF.getText()) ;
        String voucher_balance = math_form(VoucherCountTF.getText());

        if(isFelled(voucher_name)
                &&isFelled(voucher_begins)
                &&isFelled(voucher_ends)
                &&isFelled(voucher_price)
                &&isFelled(voucher_balance)
                &&!(TownLV.getSelectionModel().getSelectedItem()==null)
                &&!(ServiceBundl.getSelectionModel().getSelectedItem()==null)
                ){
            Integer town_id = TownLV.getSelectionModel().getSelectedIndex()+1;
            Integer bundle_id = ServiceBundl.getSelectionModel().getSelectedIndex();

            String addVoucherQuery ="INSERT INTO voucher (voucher_name, voucher_begins, voucher_ends, town_id, voucher_price, voucher_balance, services_bundle) VALUES ('"+voucher_name+"', '"+voucher_begins+"', '"+voucher_ends+"', "+town_id+", "+voucher_price+", "+voucher_balance+", "+bundle_id+");";
            try{
                Statement addVoucher= bdconnect.createStatement();
                addVoucher.executeUpdate(addVoucherQuery);
                Stage stage = (Stage) TownLV.getScene().getWindow();
                stage.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("Ошибка!");
            error.setHeaderText("Ошибка Ввода!");
            error.setContentText("Не все поля заполнены или не выбран город/набор услуг!");
            error.showAndWait();
        }
    }
    String math_form(String budget){
        char[] nums ;
        nums=budget.toCharArray();
        budget= "";
        for (char num : nums) {
            if (num != ' ') {
                budget = budget.concat(String.valueOf(num));
            }
        }
        budget=budget.replaceAll(",",".");
        return budget;
    }
    String date_form(String budget){
        budget=budget.replaceAll("\\.","-");
        return budget;
    }

}