package ru.ignat.lw.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ru.ignat.lw.StartClass;
import ru.ignat.lw.dataclasses.User;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import static ru.ignat.lw.StartClass.bdconnect;
import static ru.ignat.lw.StartClass.st;

public class LoginController {

    @FXML
    private TextField LoginTF;

    @FXML
    private PasswordField PasswordTF;



    @FXML
    void LoginBtnClicked(MouseEvent event) {
        try {
            login();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void LoginFiled(KeyEvent event) {
        if (event.getCode()== KeyCode.ENTER & !LoginTF.getText().equals("")){
            PasswordTF.requestFocus();
        }
    }

    @FXML
    void PasswordFiled(KeyEvent event) {

        if (event.getCode()== KeyCode.ENTER & !PasswordTF.getText().equals("")){
            try {
                login();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
    void login() throws SQLException, IOException {
        String sqlCommand = "select employee_login,employee_pass from employee";
        Statement state = bdconnect.createStatement();
        ResultSet queryRes = state.executeQuery(sqlCommand);
        ArrayList<User> employee_lp = new ArrayList<>();
        while (queryRes.next()){
            employee_lp.add(new User(queryRes.getString(1),queryRes.getString(2)));
        }
        User Entered = new User(LoginTF.getText(),PasswordTF.getText());
        if(PasswordTF.getText().equals("admin")&LoginTF.getText().equals("admin")){
            FXMLLoader fxmlLoader = new FXMLLoader(StartClass.class.getResource("fxml/main-page-admin.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            st.setTitle("Администратор турфирмы");
            st.setScene(scene);
        } else if (PasswordTF.getText().equals("customer")&LoginTF.getText().equals("customer")) {
            FXMLLoader fxmlLoader = new FXMLLoader(StartClass.class.getResource("fxml/main-page-customer.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            st.setTitle("Клиент турфирмы");
            st.setScene(scene);
        } else if (search(employee_lp,Entered)) {
            FXMLLoader fxmlLoader = new FXMLLoader(StartClass.class.getResource("fxml/main-page-employee.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            st.setTitle("Работник турфирмы");
            st.setScene(scene);
        }else{
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("Ошибка!");
            error.setHeaderText("Ошибка Ввода!");
            error.setContentText("Данный пользователь не существует проверьте правильность ввода данных учётной записи");
            error.showAndWait();
        }
    }
    boolean search(ArrayList<User> list,User searched_item){
        for (User user : list) {
            if (Objects.equals(user.login, searched_item.login) & Objects.equals(user.password, searched_item.password)) {
                return true;
            }
        }
        return false;
    }
}