module ru.ignat.lw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ru.ignat.lw to javafx.fxml;
    exports ru.ignat.lw;
}