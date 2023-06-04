package com.example.password.manager;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Launch extends Application {
    private final PasswordManager pm = new PasswordManager();
    private final LinkedHashMap<Integer, Password> linkedData = new LinkedHashMap<>();
    private final ObservableMap<Integer, Password> data = FXCollections.observableMap(linkedData);
    private TextField searchField;
    private final GridPane gridPane = new GridPane();
    private final GridPane gridPaneSearchAndButton = new GridPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private TextField masterKey;

    @Override
    public void start(Stage primaryStage) {
        BorderPane form = new BorderPane();
        Scene scene = new Scene(form, 725, 525);
        preferenceGrid();
        preferenceScroll(primaryStage);
        preferenceSearch();

        if (pm.isEmpty()) {
            setButtonImportWithMasterKey(primaryStage, gridPane);
        } else {
            setLabel();
            setDataTextField(data);
            setBottomLine(primaryStage);
        }

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(gridPaneSearchAndButton, scrollPane);

        form.setCenter(vbox);


        primaryStage.setMinWidth(750);
        primaryStage.setMaxWidth(750);
        primaryStage.setScene(scene);
        primaryStage.show();
        scrollPane.requestFocus();
    }

    private void preferenceGrid() {//настройка grid панелей
        gridPaneSearchAndButton.setHgap(10);
        gridPaneSearchAndButton.setVgap(10);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
    }

    private void preferenceScroll(Stage stage) {// настройка панели прокрутки
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setMaxHeight(stage.getMaxHeight());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(gridPane);
    }

    private void preferenceSearch() {// настройка стиля поля поиска
        searchField = new TextField();
        searchField.setMaxWidth(300);
        searchField.setPadding(new Insets(10));
        searchField.setPromptText("Поиск...");
        searchField.setOnKeyReleased(e -> search());
    }

    private void setButtonDeleteDublicate() {//добавление кнопки удаление дубликатов
        Button deleteDublicate = new Button("Удалить дубликаты");
        deleteDublicate.setPadding(new Insets(10));
        gridPaneSearchAndButton.add(deleteDublicate, 3, 0);

        deleteDublicate.setOnAction(e -> {
            pm.deleteDublicate();
            data.clear();
            repaintData();
            scrollPane.requestFocus();
        });
    }

    private void setButtonImport(Stage stage, GridPane gridPane) {// добавление кнопки иморта паролей
        Button setPasswordData = new Button("Импортировать пароли");

        gridPane.add(setPasswordData, 1, 0);
        setPasswordData.setPadding(new Insets(10));

        setPasswordData.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            List<File> selectedFile = fileChooser.showOpenMultipleDialog(stage);
            Consumer<File> consumer = c -> System.out.println("Выбран файл: " + c.getAbsolutePath());
            if (selectedFile != null && !masterKey.getText().isEmpty()) {
                selectedFile.forEach(consumer);
                FromFileToData f = new FromFileToData(pm);
                f.start(selectedFile, masterKey.getText());
                data.clear();
                repaintData();
                scrollPane.requestFocus();
            }
        });
    }

    private void setTextFieldMasterKey() {// добавление поля для мастер-ключа
        masterKey = new TextField();
        masterKey.setPromptText("Введите мастер-ключ");
        masterKey.setPadding(new Insets(10));
        gridPaneSearchAndButton.add(masterKey, 4, 0);
    }

    private void setButtonImportWithMasterKey(Stage stage, GridPane gridPane) {// добавление поля ввода для мастер-ключа и кнопки импорта паролей
        TextField textField = new TextField();
        textField.setPromptText("Введите мастер-ключ для шифрования паролей");
        textField.setMinWidth(283);

        Button button = new Button("Импортировать пароли");
        gridPane.add(button, 0, 18);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(textField, button);
        gridPane.add(vbox, 0, 18);

        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            List<File> selectedFile = fileChooser.showOpenMultipleDialog(stage);
            Consumer<File> consumer = c -> System.out.println("Выбран файл: " + c.getAbsolutePath());
            if (selectedFile != null && !textField.getText().isEmpty()) {
                selectedFile.forEach(consumer);
                FromFileToData f = new FromFileToData(pm);
                f.start(selectedFile, textField.getText());
                data.clear();
                repaintData();
                setBottomLine(stage);
                scrollPane.requestFocus();
            }
        });
    }

    private void setClearDatabase(Stage stage) { //добавление кноки, которая стриает все парали из базы данных
        Button clearDatabase = new Button("Cтереть все");
        clearDatabase.setPadding(new Insets(10));
        gridPaneSearchAndButton.add(clearDatabase, 2, 0);

        clearDatabase.setOnAction(e -> {
            pm.clearDatabase();
            gridPane.getChildren().clear();
            gridPaneSearchAndButton.getChildren().clear();
            setButtonImportWithMasterKey(stage, gridPane);
            scrollPane.requestFocus();
        });
    }

    private void setBottomLine(Stage stage) {// добавление строки с поиском и кнопками выше списка паролей
        setButtonImport(stage, gridPaneSearchAndButton);
        setClearDatabase(stage);
        setButtonDeleteDublicate();
        gridPaneSearchAndButton.add(searchField, 0, 0);
        setTextFieldMasterKey();
    }

    private void setDataTextField(ObservableMap<Integer, Password> data) { //вывод основного списка паролей
        if (data.isEmpty()) {
            data = pm.setData(data);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        int row = 1;

        for (Map.Entry<Integer, Password> d : data.entrySet()) {
            final Label number = new Label();

            final TextField field1 = new TextField(d.getValue().getService());
            final TextField field2 = new TextField(d.getValue().getLogin());
            final TextField field3 = new TextField(d.getValue().getPassword());
            Button saveButton = new Button("Save");
            Button deleteButton = new Button("Delete");

            gridPane.add(number, 0, row);
            gridPane.add(field1, 1, row);
            gridPane.add(field2, 2, row);
            gridPane.add(field3, 3, row);
            gridPane.add(saveButton, 4, row);
            gridPane.add(deleteButton, 5, row);

            number.setText(String.valueOf(row));

            AtomicBoolean check = new AtomicBoolean(true);

            final String oldPas = field3.getText();

            field3.setOnMouseClicked(event -> {
                if (check.get() && !masterKey.getText().isEmpty()) {
                    String decrypt = AES.decrypt(field3.getText(), masterKey.getText());
                    field3.setText(decrypt);
                    check.set(false);
                }
            });

            field3.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && !masterKey.getText().isEmpty()) {
                    field3.setText(oldPas);
                    check.set(true);
                }
            });

            saveButton.setOnAction(event -> {
                String fieldValue1 = field1.getText();
                String fieldValue2 = field2.getText();
                String fieldValue3 = field3.getText();
                int index = d.getKey();
                pm.changePassword(d.getValue().getId(), fieldValue1, fieldValue2, fieldValue3);
                changeData(index, new Password(d.getValue().getId(), fieldValue1, fieldValue2, fieldValue3));
                scrollPane.requestFocus();
            });

            deleteButton.setOnAction(event -> {
                removeNode(field1);
                removeData(d.getKey());
                Thread thread = new Thread(() -> pm.deletePassword(d.getValue().getId()));
                thread.start();
                scrollPane.requestFocus();
            });
            row++;
        }
    }

    private void removeData(int index) {//удаление пароля и его данных из списка, не из базы данных
        data.remove(index);
    }

    private void removeNode(Node nodeT) { //удаление строки с паролями и сдвиг строк вверх на 1, также сдвиг подсчета кол-во парелей на 1
        Integer index = GridPane.getRowIndex(nodeT);
        List<Node> nodesInRow = gridPane.getChildren().filtered(node -> GridPane.getRowIndex(node).equals(index));
        gridPane.getChildren().removeAll(nodesInRow);
        for (int i = 1; i < gridPane.getChildren().size(); i++) {

            Node node = gridPane.getChildren().get(i);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (rowIndex != null && rowIndex > 0 && rowIndex >= index) {
                GridPane.setRowIndex(node, rowIndex - 1); // сдвиг вверх на одну строку
                if (node.getStyleClass().toString().equals("label")) {
                    Label label = (Label) node;
                    label.setText(String.valueOf(rowIndex - 1));
                }
            }
        }
    }

    private void changeData(int index, Password password) {//изменение данных в списке парелей, не в базе данных
        data.replace(index, password);
    }

    private void repaintData() {//перерисовка основного списка паролей
        gridPane.getChildren().clear();
        setLabel();
        setDataTextField(data);
    }

    private void setLabel() {// добавление строки с заголовками столбцов
        Label header1 = new Label("Сайт");
        header1.setStyle("-fx-font: bold 10pt Arial");
        Label header2 = new Label("Логин");
        header2.setStyle("-fx-font: bold 10pt Arial");
        Label header3 = new Label("Пароль");
        header3.setStyle("-fx-font: bold 10pt Arial");

        gridPane.add(header1, 1, 0);
        gridPane.add(header2, 2, 0);
        gridPane.add(header3, 3, 0);

        GridPane.setHalignment(header1, HPos.CENTER);
        GridPane.setHalignment(header2, HPos.CENTER);
        GridPane.setHalignment(header3, HPos.CENTER);
    }

    private void search() {// метод поиска
        String searchText = searchField.getText().trim().toLowerCase();
        //если поле поиска пустое, вывести весь список паролей
        if (searchText.isEmpty()) {
            gridPane.getChildren().clear();
            setLabel();
            setDataTextField(data);
            return;
        }

        // Найти совпадения и создать фильтр
        ObservableMap<Integer, Password> filteredData = FXCollections.observableHashMap();
        for (Map.Entry<Integer, Password> password : data.entrySet()) {
            if (password.getValue().getService().toLowerCase().contains(searchText) ||
                    password.getValue().getLogin().toLowerCase().contains(searchText) ||
                    password.getValue().getPassword().toLowerCase().contains(searchText)) {
                filteredData.put(password.getKey(), data.get(password.getKey()));
            }
        }

        gridPane.getChildren().clear();
        //если что-то найдено вывести список найденого, иначе надпись
        if (!filteredData.isEmpty()) {
            setLabel();
            setDataTextField(filteredData);
        } else {
            Label label = new Label("Ничего не найдено");
            gridPane.add(label, 0, 0);
        }
    }
}
