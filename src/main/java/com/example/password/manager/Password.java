package com.example.password.manager;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Password {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty service = new SimpleStringProperty();
    private final SimpleStringProperty login = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();

    public Password(int id, String service, String login, String password) {
        setId(id);
        setService(service);
        setLogin(login);
        setPassword(password);
    }

    public Password(String service, String login, String password) {
        setService(service);
        setLogin(login);
        setPassword(password);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getService() {
        return service.get();
    }

    public void setService(String service) {
        this.service.set(service);
    }

    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    @Override
    public String toString() {
        return "Password{" +
                "id=" + id.getValue() +
                ", service=" + service.getValue() +
                ", login=" + login.getValue() +
                ", password=" + password.getValue() +
                '}';
    }
}
