package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.AuthClient;
import com.piedrazul.frontend.dto.response.LoginResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private final AuthClient authClient = new AuthClient();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            LoginResponse response = authClient.login(username, password);

            showAlert("Éxito", response.getMessage());

            // 🔥 aquí viene lo importante
            System.out.println("Roles: " + response.getRoles());

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onGoRegister() {
        System.out.println("Ir a registro (siguiente paso)");
    }
}