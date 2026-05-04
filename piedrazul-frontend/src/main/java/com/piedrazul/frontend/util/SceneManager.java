package com.piedrazul.frontend.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class SceneManager {

    private static final double LOGIN_WIDTH = 900;
    private static final double LOGIN_HEIGHT = 620;
    private static final double LOGIN_MIN_WIDTH = 850;
    private static final double LOGIN_MIN_HEIGHT = 580;

    private static final double DASHBOARD_MIN_WIDTH = 1000;
    private static final double DASHBOARD_MIN_HEIGHT = 650;

    private SceneManager() {
        // Evita crear objetos de esta clase
    }

    public static void showLogin(String fxmlPath, Node node) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));

            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setMaximized(false);
            stage.setScene(scene);
            stage.setTitle("Piedrazul - Login");

            stage.setMinWidth(LOGIN_MIN_WIDTH);
            stage.setMinHeight(LOGIN_MIN_HEIGHT);
            stage.setWidth(LOGIN_WIDTH);
            stage.setHeight(LOGIN_HEIGHT);

            stage.show();

            Platform.runLater(stage::centerOnScreen);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDashboard(String fxmlPath, Node node, String title) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));

            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle(title);

            stage.setMinWidth(DASHBOARD_MIN_WIDTH);
            stage.setMinHeight(DASHBOARD_MIN_HEIGHT);

            stage.show();

            Platform.runLater(() -> {
                stage.setMaximized(true);
                stage.centerOnScreen();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showRegister(String fxmlPath, Node node) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));

            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setMaximized(false);
            stage.setScene(scene);
            stage.setTitle("Piedrazul - Registro");

            stage.setMinWidth(LOGIN_MIN_WIDTH);
            stage.setMinHeight(LOGIN_MIN_HEIGHT);
            stage.setWidth(LOGIN_WIDTH);
            stage.setHeight(LOGIN_HEIGHT);

            stage.show();

            Platform.runLater(stage::centerOnScreen);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchScene(String fxmlPath, Node node, String title) {
        try {
            URL resource = SceneManager.class.getResource(fxmlPath);

            if (resource == null) {
                throw new RuntimeException("FXML no encontrado: " + fxmlPath);
            }

            Parent root = FXMLLoader.load(resource);

            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

            Platform.runLater(stage::centerOnScreen);

        } catch (Exception e) {
            System.out.println("ERROR CAMBIANDO ESCENA: " + fxmlPath);
            e.printStackTrace();
        }
    }
}