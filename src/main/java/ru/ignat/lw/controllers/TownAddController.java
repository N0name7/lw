package ru.ignat.lw.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Statement;

import static ru.ignat.lw.controllers.CustomerAddController.isFelled;
import static ru.ignat.lw.StartClass.bdconnect;

public class TownAddController {

    @FXML
    private TextField TownNameTF;

    @FXML
    void AddBtnClicked(MouseEvent event) {
        String TownName = TownNameTF.getText();
        if(isFelled(TownName)){
            String addVoucherQuery ="INSERT INTO towns (`town_name`) VALUES ('"+TownName+"');";
            try{
                Statement addVoucher= bdconnect.createStatement();
                addVoucher.executeUpdate(addVoucherQuery);
                Stage stage = (Stage) TownNameTF.getScene().getWindow();
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

}
