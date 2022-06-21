package ru.ignat.lw;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.*;

import static ru.ignat.lw.StartClass.bdconnect;

public class SaleAddController {

    @FXML
    private ComboBox<String> ClientCB;

    @FXML
    private ComboBox<String> EmployeeCB;

    @FXML
    private TableColumn<Voucher, Date> VoucherBegin;

    @FXML
    private TableColumn<Voucher, Date> VoucherEnd;

    @FXML
    private TableColumn<Voucher, Integer> VoucherID;

    @FXML
    private TableColumn<Voucher, String> VoucherName;

    @FXML
    private TableColumn<Voucher, Date> VoucherPrice;

    @FXML
    private TableColumn<Voucher, String> VoucherService;

    @FXML
    private TableColumn<Voucher, String> VoucherTown;

    @FXML
    private TableColumn<Voucher, Integer> VoucherCount;

    @FXML
    private TableView<Voucher> VouchrTable;
    @FXML
    public void initialize() {
        ObservableList <Voucher> voucherObservableList = FXCollections.observableArrayList();
        String voucher_get_query = "select voucher_id,voucher_name,voucher_begins,voucher_ends,town_name,voucher_price, services_bundle,voucher_balance from voucher,towns where voucher.town_id=towns.town_id and voucher_begins > CURDATE() and voucher_balance>0 order by voucher_id;";
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
        VoucherID.setCellValueFactory(new PropertyValueFactory <>("id"));
        VoucherName.setCellValueFactory(new PropertyValueFactory<>("name"));
        VoucherBegin.setCellValueFactory(new PropertyValueFactory<>("begin"));
        VoucherEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        VoucherTown.setCellValueFactory(new PropertyValueFactory<>("town"));
        VoucherService.setCellValueFactory(new PropertyValueFactory<>("service"));
        VoucherPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        VoucherCount.setCellValueFactory(new PropertyValueFactory<>("count"));

        VouchrTable.setItems(voucherObservableList);

        ObservableList <String> customerObservableList = FXCollections.observableArrayList();
        ObservableList <String> employeeObservableList = FXCollections.observableArrayList();

        String customer_get_query = "select customer_id,concat(customer_surname,' ',customer_name,' ',customer_patronymic),concat(s_dock,' ',№_dock) from customer;";
        String employee_get_query = "select employee_id,concat( employee_surname,' ',employee_name,' ',employee_patronymic) from employee;";
        try {
            Statement customer_get = bdconnect.createStatement();
            ResultSet customer = customer_get.executeQuery(customer_get_query);
            while (customer.next()) {
                String tmp = customer.getString(1)+") "+customer.getString(2)+"; "+customer.getString(3)+".";
                customerObservableList.add(tmp);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        try {
            Statement employee_get = bdconnect.createStatement();
            ResultSet employee = employee_get.executeQuery(employee_get_query);
            while (employee.next()) {
                String tmp = employee.getString(1)+") "+employee.getString(2)+".";
                employeeObservableList.add(tmp);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        ClientCB.setItems(customerObservableList);
        EmployeeCB.setItems(employeeObservableList);
    }
    @FXML
    void AddBtnClicked(MouseEvent event) {
        if (!(VouchrTable.getSelectionModel().getSelectedItem()==null)&&!(ClientCB.getSelectionModel().getSelectedItem()==null)&&!(EmployeeCB.getSelectionModel().getSelectedItem()==null)){
            try {
                CallableStatement get_price = bdconnect.prepareCall("call add_sale(?, ?, ?);");
                get_price.setInt(1, VouchrTable.getSelectionModel().getSelectedItem().getId());
                get_price.setInt(2, EmployeeCB.getSelectionModel().getSelectedIndex()+1);
                get_price.setInt(3, ClientCB.getSelectionModel().getSelectedIndex()+1);
                get_price.execute();
                Stage stage = (Stage) ClientCB.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {

            }
        }
        else{
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("Ошибка!");
            error.setHeaderText("Ошибка Ввода!");
            error.setContentText("Не выбраны путёвка/клиент/сотрудник!");
            error.showAndWait();
        }
    }


}