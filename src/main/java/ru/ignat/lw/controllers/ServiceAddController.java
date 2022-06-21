package ru.ignat.lw.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.FormatStringConverter;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import static ru.ignat.lw.controllers.CustomerAddController.isFelled;
import static ru.ignat.lw.StartClass.bdconnect;

public class ServiceAddController {

    @FXML
    private TextField ServiceNameTF;

    @FXML
    private TextField ServicePageTF;

    public void initialize() {
        ServicePageTF.setTextFormatter(new TextFormatter <>(new FormatStringConverter <DecimalFormat>(new DecimalFormat())));
    }

    @FXML
    void AddBtnClicked(MouseEvent event) {
        String ServiceName = ServiceNameTF.getText();
        String ServicePrice = math_form(ServicePageTF.getText());
        if(isFelled(ServiceName)&&isFelled(ServicePrice)){
            String addVoucherQuery ="INSERT INTO services (`service_name`, `service_price`) VALUES ('"+ServiceName+"', "+ServicePrice+");";
            try{
                Statement addVoucher= bdconnect.createStatement();
                addVoucher.executeUpdate(addVoucherQuery);
                Stage stage = (Stage) ServicePageTF.getScene().getWindow();
                stage.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("Ошибка!");
            error.setHeaderText("Ошибка Ввода!");
            error.setContentText("Не все поля заполнены!");
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
}
