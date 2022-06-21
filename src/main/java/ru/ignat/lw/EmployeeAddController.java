package ru.ignat.lw;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import static ru.ignat.lw.CustomerAddController.isFelled;
import static ru.ignat.lw.StartClass.bdconnect;

public class EmployeeAddController {

    @FXML
    private TextField FamilyTF;

    @FXML
    private TextField LastNameTF;

    @FXML
    private TextField LoginTF;

    @FXML
    private TextField NameTF;

    @FXML
    private PasswordField PasswordTF;

    @FXML
    void AddBtnClicked(MouseEvent event) {
        try {
            String sqlCommand = "select employee_login from employee";
            Statement state = bdconnect.createStatement();
            ResultSet queryRes = state.executeQuery(sqlCommand);
            ArrayList <String> employee_lp = new ArrayList <>();
            while (queryRes.next()) {
                employee_lp.add(queryRes.getString(1));
            }

            String name = NameTF.getText();
            String family = FamilyTF.getText();
            String last_name = LastNameTF.getText();
            String login =LoginTF.getText();
            String password = PasswordTF.getText();

            if(isFelled(name)
                    &&isFelled(family)
                    &&isFelled(last_name)
                    &&isFelled(login)
                    &&isFelled(password)
            ) {
                if (employee_lp.indexOf(login) > 0) {
                    LoginTF.setText("");
                    Alert error = new Alert(Alert.AlertType.INFORMATION);
                    error.setTitle("Ошибка!");
                    error.setHeaderText("Ошибка Ввода!");
                    error.setContentText("Пользователь с таким логином уже существует!");
                    error.showAndWait();
                } else {
                    String addVoucherQuery = "INSERT INTO employee (`employee_name`, `employee_surname`, `employee_patronymic`, `employee_login`, `employee_pass`) VALUES ('" + name + "', '" + family + "', '" + last_name + "', '" + login + "', '" + password + "');";
                    try {
                        Statement addVoucher = bdconnect.createStatement();
                        addVoucher.executeUpdate(addVoucherQuery);

                        Stage stage = (Stage) FamilyTF.getScene().getWindow();
                        stage.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                }else{
                    Alert error = new Alert(Alert.AlertType.INFORMATION);
                    error.setTitle("Ошибка!");
                    error.setHeaderText("Ошибка Ввода!");
                    error.setContentText("Не все поля заполнены!");
                    error.showAndWait();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
