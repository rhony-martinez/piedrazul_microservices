package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.piedrazul.frontend.util.SceneManager;

public class AdministradorDashboardController {

    @FXML
    private Label lblBienvenida;

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.showLogin("/view/auth_register/loginView.fxml",lblBienvenida);
        }
        String username = SessionManager.getCurrentUser().getUsername();
        lblBienvenida.setText("Bienvenido " + username);
    }

    @FXML
    private void handleLogout() {
        SessionManager.clear();

        SceneManager.showLogin(
                "/view/auth_register/loginView.fxml",
                lblBienvenida
        );
    }

    @FXML
    private void handleConfiguracionHorarios() {
        SceneManager.switchScene("/view/admin/configuracion-disponibilidad.fxml",lblBienvenida,"Configuración de Parámetros");
    }
}
