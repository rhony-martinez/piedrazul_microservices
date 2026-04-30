package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.AuthClient;
import com.piedrazul.frontend.dto.response.LoginResponse;
import com.piedrazul.frontend.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

            // 🔥 aquí viene lo importante
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
        System.out.println("Ir a registro (siguiente paso)");
    }

    private void redirectByRole(LoginResponse response) {

        String rol = response.getRoles().getFirst();

        switch (rol) {
            case "PACIENTE":
                loadView("/view/dashboard/paciente-dashboard.fxml",txtUsername);
                break;
            case "MEDICO_TERAPISTA":
                loadView("/view/dashboard/medico-dashboard.fxml",txtUsername);
                break;
            case "ADMINISTRADOR":
                loadView("/view/dashboard/admin-dashboard.fxml",txtUsername);
                break;
            default:
                throw new RuntimeException("Rol no soportado: " + rol);
        }
    }

    private void loadView(String fxml, Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}