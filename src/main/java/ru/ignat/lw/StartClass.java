package ru.ignat.lw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.io.IOException;

public class StartClass extends Application {
    public static Stage st;
    public static Connection bdconnect;
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        String SQL = "jdbc:mysql://141.8.192.151/f0687773_lwbd?serverTimezone=Europe/Moscow&noAccessToProcedureBodies=true";
        String login = "f0687773_lwbd";
        String pass = "20020801";
        bdconnect = DriverManager.getConnection(SQL,login,pass);
        FXMLLoader fxmlLoader = new FXMLLoader(StartClass.class.getResource("fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        st=stage;
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}