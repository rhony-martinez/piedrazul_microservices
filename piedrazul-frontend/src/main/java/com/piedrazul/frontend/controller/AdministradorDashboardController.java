package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdministradorDashboardController {

    @FXML
    private Label lblBienvenida;

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn()) {
            loadView("/view/auth_register/loginView.fxml");
        }
        String username = SessionManager.getCurrentUser().getUsername();
        lblBienvenida.setText("Bienvenido " + username);
    }

    @FXML
    private void handleLogout() {
        SessionManager.clear();
        loadView("/view/auth_register/loginView.fxml");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
