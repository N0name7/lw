package ru.ignat.lw.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import static ru.ignat.lw.StartClass.bdconnect;

public class CustomerAddController {

    @FXML
    private TextField CkientDocSTF;

    @FXML
    private TextField ClientDocNumTF;

    @FXML
    private TextField ClientFamilyTF;

    @FXML
    private TextField ClientLastNameTF;

    @FXML
    private TextField ClientNameTF;

    @FXML
    private TextField ClientPhoneNumTF;

    @FXML
    private ListView<String> DiscountLV;

    ObservableList<String> DiscountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        String discount_get = "SELECT discount_type,discount_count FROM discount;";
        try{
            Statement getDiscountList = bdconnect.createStatement();
            ResultSet Discount = getDiscountList.executeQuery(discount_get);
            while (Discount.next()){
                String discount_type = Discount.getString(1);
                Integer discount_count = Discount.getInt(2);
                String discount = discount_type+": "+discount_count.toString()+" %;";
                DiscountList.add(discount);
            }
            DiscountLV.setItems(DiscountList);
        }catch (SQLException e){
            e.printStackTrace();
        }
        Pattern phone_number = Pattern.compile("^(\\+\\d{1}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");
    }

    @FXML
    void AddBtnClicked(MouseEvent event) {
        String customer_surname = ClientFamilyTF.getText();
        String customer_name = ClientNameTF.getText();
        String customer_patronymic = ClientLastNameTF.getText();
        String phone_number = ClientPhoneNumTF.getText();
        String s_dock = CkientDocSTF.getText();
        String num_dock = ClientDocNumTF.getText();

        if(isFelled(customer_surname)
                &&isFelled(customer_name)
                &&isFelled(customer_patronymic)
                &&isFelled(phone_number)
                &&isFelled(s_dock)
                &&isFelled(num_dock)
                &&!(DiscountLV.getSelectionModel().getSelectedItem()==null)){
            Integer discount_id = DiscountLV.getSelectionModel().getSelectedIndex()+1;

            String addCustomerQuery ="INSERT customer (customer_surname,customer_name,customer_patronymic,phone_number,s_dock,№_dock,discount_id) VALUES(?,?,?,?,?,?,?)";
            try{
                PreparedStatement addCustomer= bdconnect.prepareStatement(addCustomerQuery);
                addCustomer.setString(1,customer_surname);
                addCustomer.setString(2,customer_name);
                addCustomer.setString(3,customer_patronymic);
                addCustomer.setString(4,phone_number);
                addCustomer.setString(5,s_dock);
                addCustomer.setString(6,num_dock);
                addCustomer.setInt(7,discount_id);
                addCustomer.executeUpdate();
                Stage stage = (Stage) DiscountLV.getScene().getWindow();
                stage.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("Ошибка!");
            error.setHeaderText("Ошибка Ввода!");
            error.setContentText("Не все поля заполнены или не выбрана скидка!");
            error.showAndWait();
        }
    }
    public static boolean isFelled(String TF){
        return !(TF.isEmpty()||TF.isBlank());
    }

}
