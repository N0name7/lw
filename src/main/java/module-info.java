module ru.ignat.lw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ru.ignat.lw to javafx.fxml;
    exports ru.ignat.lw;
    exports ru.ignat.lw.dataclasses;
    opens ru.ignat.lw.dataclasses to javafx.fxml;
    exports ru.ignat.lw.controllers;
    opens ru.ignat.lw.controllers to javafx.fxml;
}