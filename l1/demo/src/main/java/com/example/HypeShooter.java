package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HypeShooter extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Меткий стрелок");
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setWidth(800);
        stage.setHeight(500);
        stage.setResizable(false);
        stage.centerOnScreen();

        GamePanel gp = new GamePanel();
        ControlPanel controlPanel = new ControlPanel(gp);
        gp.setControlPanel(controlPanel);

        BorderPane root = new BorderPane();
        root.setCenter(gp);
        root.setRight(controlPanel);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}