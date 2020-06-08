package com.example.gui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author 李磊
 * @datetime 2020/6/8 20:17
 * @description javafx 兼容jdk11 不兼容jdk13
 */
public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("点击");
        button.setOnAction(event -> button.setText("李磊"));
        BorderPane pane = new BorderPane();
        pane.setCenter(button);
        Scene scene = new Scene(pane, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("标题");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}