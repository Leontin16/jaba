package com.example;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ControlPanel extends VBox {

    private Label scoreLabel = new Label("Счёт: 0");
    private Label shotsLabel = new Label("Выстрелов: 0");

    public ControlPanel(GamePanel gp) {
        Button startBtn = new Button("Начало игры");
        Button stopBtn  = new Button("Остановить игру");
        Button pauseBtn = new Button("Пауза");
        Button shootBtn = new Button("Выстрел");

        startBtn.setOnAction(e -> gp.startGame());
        stopBtn.setOnAction(e -> gp.stopGame());
        pauseBtn.setOnAction(e -> gp.togglePause());
        shootBtn.setOnAction(e -> gp.shoot());

        getChildren().addAll(scoreLabel, shotsLabel, startBtn, stopBtn, pauseBtn, shootBtn);
        setSpacing(10);
        setPadding(new Insets(10));
    }

    public void updateScore(int score) {
        scoreLabel.setText("Счёт: " + score);
    }

    public void updateShots(int shots) {
        shotsLabel.setText("Выстрелов: " + shots);
    }
}