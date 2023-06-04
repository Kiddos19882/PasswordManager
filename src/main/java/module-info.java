module com.example.password.manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.password.manager to javafx.fxml;
    exports com.example.password.manager;
}