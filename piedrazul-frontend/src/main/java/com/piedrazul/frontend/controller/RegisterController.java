package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.PersonaClient;
import com.piedrazul.frontend.client.UsuarioClient;
import com.piedrazul.frontend.dto.request.CrearPersonaRequest;
import com.piedrazul.frontend.dto.request.CrearUsuarioRequest;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class RegisterController {

    // PERSONA
    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDni;
    @FXML private TextField txtCorreo;

    // USUARIO
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRol;

    private final PersonaClient personaClient = new PersonaClient();
    private final UsuarioClient usuarioClient = new UsuarioClient();

    @FXML
    public void initialize() {
        cmbGenero.getItems().addAll("HOMBRE", "MUJER", "OTRO");
        cmbRol.getItems().addAll("PACIENTE", "MEDICO_TERAPISTA", "ADMINISTRADOR", "AGENDADOR");
    }

    @FXML
    private void handleRegister() {
        try {

            // 🔹 1. Crear Persona
            CrearPersonaRequest personaRequest = new CrearPersonaRequest();
            personaRequest.setPrimerNombre(txtPrimerNombre.getText());
            personaRequest.setSegundoNombre(txtSegundoNombre.getText());
            personaRequest.setPrimerApellido(txtPrimerApellido.getText());
            personaRequest.setSegundoApellido(txtSegundoApellido.getText());
            personaRequest.setGenero(cmbGenero.getValue());
            personaRequest.setFechaNacimiento(dateNacimiento.getValue().toString());
            personaRequest.setTelefono(txtTelefono.getText());
            personaRequest.setDni(txtDni.getText());
            personaRequest.setCorreo(txtCorreo.getText());

            Long personaId = personaClient.crearPersona(personaRequest);

            // 🔹 2. Crear Usuario
            CrearUsuarioRequest usuarioRequest = new CrearUsuarioRequest();
            usuarioRequest.setPersonaId(personaId);
            usuarioRequest.setUsername(txtUsername.getText());
            usuarioRequest.setPassword(txtPassword.getText());
            usuarioRequest.setRoles(List.of(cmbRol.getValue()));

            usuarioClient.crearUsuario(usuarioRequest);

            showAlert("Éxito", "Usuario registrado correctamente");
            goLogin();

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void goLogin() {
        try {
            Stage stage = (Stage) txtPrimerNombre.getScene().getWindow();
            stage.getScene().setRoot(
                    javafx.fxml.FXMLLoader.load(getClass().getResource("/view/auth_register/loginView.fxml"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}