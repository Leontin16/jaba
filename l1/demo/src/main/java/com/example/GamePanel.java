package com.example;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GamePanel extends Pane {
    private final Canvas canvas = new Canvas(600, 500);
    private ControlPanel controlPanel;
    private volatile boolean running = false, paused = false;
    private int score = 0, shots = 0;
    private double nearY = 250, farY = 250;
    private boolean arrowActive = false;
    private double arrowX = 0, arrowY = 0;
    private Thread gameThread;

    private static final double NEAR_X = 300, FAR_X = 480, NEAR_SIZE = 50, FAR_SIZE = 25;
    private static final double NEAR_SPEED = 2, FAR_SPEED = 4;

    public GamePanel() { getChildren().add(canvas); render(); }

    public void setControlPanel(ControlPanel cp) { controlPanel = cp; }

    private void render() {
        Platform.runLater(() -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setStroke(Color.LIGHTGRAY);
            gc.setLineWidth(2);
            gc.strokeLine(NEAR_X, 0, NEAR_X, canvas.getHeight());
            gc.strokeLine(FAR_X, 0, FAR_X, canvas.getHeight());

            double px = 30, py = canvas.getHeight() / 2;
            gc.setFill(Color.BLUE);
            gc.fillPolygon(new double[]{px, px, px + 40}, new double[]{py - 20, py + 20, py}, 3);

            if (arrowActive) {
                gc.setFill(Color.BLACK);
                gc.fillRect(arrowX, arrowY - 2, 25, 4);
                gc.fillPolygon(new double[]{arrowX + 25, arrowX + 25, arrowX + 40},
                               new double[]{arrowY - 6, arrowY + 6, arrowY}, 3);
            }

            gc.setFill(Color.RED);
            gc.fillOval(NEAR_X - NEAR_SIZE/2, nearY - NEAR_SIZE/2, NEAR_SIZE, NEAR_SIZE);
            gc.fillOval(FAR_X - FAR_SIZE/2, farY - FAR_SIZE/2, FAR_SIZE, FAR_SIZE);
        });
    }

    private void updateTargets() {
        nearY += NEAR_SPEED;
        farY += FAR_SPEED;
        if (nearY > canvas.getHeight()) nearY = 0;
        if (farY > canvas.getHeight()) farY = 0;
    }

    public void startGame() {
        running = false;
        if (gameThread != null) try { gameThread.join(100); } catch (InterruptedException ignored) {}
        score = 0; shots = 0;
        nearY = canvas.getHeight() / 2;
        farY = canvas.getHeight() / 2;
        arrowActive = false;
        paused = false;
        controlPanel.updateScore(0);
        controlPanel.updateShots(0);
        running = true;
        gameThread = new Thread(() -> {
            while (running) {
                if (!paused) {
                    updateTargets();
                    checkCollisions();
                    render();
                }
                try { Thread.sleep(16); } catch (InterruptedException ignored) {}
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public void stopGame() { running = false; }
    public void togglePause() { paused = !paused; }

    public void shoot() {
        if (!running || paused || arrowActive) return;
        shots++;
        controlPanel.updateShots(shots);
        arrowX = 70;
        arrowY = canvas.getHeight() / 2 - 2;
        arrowActive = true;
        new Thread(() -> {
            while (arrowActive && arrowX < canvas.getWidth()) {
                arrowX += 8;
                try { Thread.sleep(16); } catch (InterruptedException ignored) {}
            }
            arrowActive = false;
        }).start();
    }

    private void checkCollisions() {
        if (!arrowActive) return;
        double dxNear = arrowX - NEAR_X, dyNear = arrowY - nearY;
        double dxFar = arrowX - FAR_X, dyFar = arrowY - farY;
        if (dxNear * dxNear + dyNear * dyNear < (NEAR_SIZE/2) * (NEAR_SIZE/2)) {
            score += 1;
            arrowActive = false;
            Platform.runLater(() -> controlPanel.updateScore(score));
        } else if (dxFar * dxFar + dyFar * dyFar < (FAR_SIZE/2) * (FAR_SIZE/2)) {
            score += 2;
            arrowActive = false;
            Platform.runLater(() -> controlPanel.updateScore(score));
        }
    }
}