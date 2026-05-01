package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.AuthClient;
import com.piedrazul.frontend.dto.response.LoginResponse;
import com.piedrazul.frontend.session.SessionManager;
import com.piedrazul.frontend.util.SceneManager;
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

            SessionManager.setCurrentUser(response);

            showAlert("Éxito", response.getMessage());

            System.out.println("Roles: " + response.getRoles());

            redirectByRole(response);

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
        SceneManager.showRegister(
                "/view/auth_register/register-View.fxml",
                txtUsername
        );
    }

    private void redirectByRole(LoginResponse response) {
        String rol = response.getRoles().getFirst();

        switch (rol) {
            case "PACIENTE":
                SceneManager.showDashboard(
                        "/view/dashboard/paciente-dashboard.fxml",
                        txtUsername,
                        "PIEDRAZUL - Menú principal"
                );
                break;

            case "MEDICO_TERAPISTA":
                SceneManager.showDashboard(
                        "/view/dashboard/medico-dashboard.fxml",
                        txtUsername,
                        "PIEDRAZUL - Menú principal"
                );
                break;

            case "ADMINISTRADOR":
                SceneManager.showDashboard(
                        "/view/dashboard/administrador-dashboard.fxml",
                        txtUsername,
                        "PIEDRAZUL - Menú principal"
                );
                break;

            case "AGENDADOR":
                SceneManager.showDashboard(
                        "/view/dashboard/agendador-dashboard.fxml",
                        txtUsername,
                        "PIEDRAZUL - Menú principal"
                );
                break;

            default:
                throw new RuntimeException("Rol no soportado: " + rol);
        }
    }
}