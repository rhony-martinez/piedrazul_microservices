package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.auth.KeycloakAuthClient;
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

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showAlert("Datos faltantes", "Ingresa usuario y contrasena.", Alert.AlertType.WARNING);
            return;
        }

        try {
            SessionManager.login(username, password);

            String rol = SessionManager.getPrimaryRole();
            if (rol == null) {
                SessionManager.clear();
                showAlert("Sin rol asignado",
                        "Tu usuario no tiene roles del sistema. Contacta al administrador.",
                        Alert.AlertType.ERROR);
                return;
            }

            redirectByRole(rol);

        } catch (KeycloakAuthClient.AuthException e) {
            showAlert("Error de autenticacion", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error inesperado", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onGoRegister() {
        SceneManager.showRegister(
                "/view/auth_register/register-View.fxml",
                txtUsername
        );
    }

    private void redirectByRole(String rol) {
        switch (rol) {
            case "PACIENTE" -> SceneManager.showDashboard(
                    "/view/dashboard/paciente-dashboard.fxml",
                    txtUsername,
                    "PIEDRAZUL - Menu principal"
            );
            case "MEDICO_TERAPISTA" -> SceneManager.showDashboard(
                    "/view/dashboard/medico-dashboard.fxml",
                    txtUsername,
                    "PIEDRAZUL - Menu principal"
            );
            case "ADMINISTRADOR" -> SceneManager.showDashboard(
                    "/view/dashboard/administrador-dashboard.fxml",
                    txtUsername,
                    "PIEDRAZUL - Menu principal"
            );
            case "AGENDADOR" -> SceneManager.showDashboard(
                    "/view/dashboard/agendador-dashboard.fxml",
                    txtUsername,
                    "PIEDRAZUL - Menu principal"
            );
            default -> {
                SessionManager.clear();
                showAlert("Rol no soportado",
                        "El rol '" + rol + "' no tiene una pantalla asignada.",
                        Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
