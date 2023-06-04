package com.example.password.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FromFileToData {
    List<String> data = new ArrayList<>();
    List<Password> pas = new ArrayList<>();
    PasswordManager ps;

    public FromFileToData(PasswordManager ps) {
        this.ps = ps;
    }

    public void start(List<File> files,String key) {
        for (File file : files) {
            reader(file);
            setPassword(key);
            data.clear();
        }

        for (Password pa : pas) {
            ps.setPassword(pa.getService(), pa.getLogin(), pa.getPassword());
        }
    }

    void reader(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void setPassword(String key) {
        int idUrl = 0;
        int idUsername = 0;
        int idPassword = 0;
        for (String datum : data) {
            String[] f = datum.split(",");

            if (datum.contains("url") && datum.contains("password")) {
                for (int i = 0; i < f.length; i++) {
                    if (f[i].replace("\"", "").equals("url"))
                        idUrl = i;
                    if (f[i].replace("\"", "").equals("username"))
                        idUsername = i;
                    if (f[i].replace("\"", "").equals("password"))
                        idPassword = i;
                }
                continue;
            }

            String service = "";
            if (f.length > idUrl && !f[idUrl].isEmpty())
                service = f[idUrl].replace("\"", "");
            String login = "";
            if (f.length > idUsername && !f[idUsername].isEmpty())
                login = f[idUsername].replace("\"", "");
            String password = "";
            if (f.length > idPassword && !f[idPassword].isEmpty())
                password = AES.encrypt(f[idPassword].replace("\"", ""),key);

            if (!service.isEmpty() || !login.isEmpty() || !password.isEmpty()) {
                Password passwords = new Password(service, login, password);
                pas.add(passwords);
            }
        }
    }
}
