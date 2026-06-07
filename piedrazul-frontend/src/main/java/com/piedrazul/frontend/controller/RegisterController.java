package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.MedicoClient;
import com.piedrazul.frontend.client.PacienteClient;
import com.piedrazul.frontend.client.PersonaClient;
import com.piedrazul.frontend.client.UsuarioClient;
import com.piedrazul.frontend.dto.request.CrearPersonaRequest;
import com.piedrazul.frontend.dto.request.CrearUsuarioRequest;
import com.piedrazul.frontend.util.ApiClientException;
import com.piedrazul.frontend.util.ApiErrorParser;
import com.piedrazul.frontend.util.FormFieldHelper;
import com.piedrazul.frontend.util.PersonaFormSupport;
import com.piedrazul.frontend.session.SessionManager;
import com.piedrazul.frontend.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[\\w.]{3,50}$");

    private static final String ROL_MEDICO_TERAPISTA = "MEDICO_TERAPISTA";

    private static final List<String> CODIGOS_ESPECIALIDAD = List.of(
            "GENERAL",
            "TERAPEUTA_NEURAL",
            "QUIROPRACTICO",
            "FISIOTERAPEUTA"
    );

    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDni;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private Label lblEspecialidadesTitulo;
    @FXML private VBox panelEspecialidades;
    @FXML private VBox boxEspecialidades;
    @FXML private Label errEspecialidades;
    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private Label lblFormError;
    @FXML private Button btnVolver;

    @FXML private Label errPrimerNombre;
    @FXML private Label errSegundoNombre;
    @FXML private Label errPrimerApellido;
    @FXML private Label errSegundoApellido;
    @FXML private Label errGenero;
    @FXML private Label errFechaNacimiento;
    @FXML private Label errTelefono;
    @FXML private Label errDni;
    @FXML private Label errCorreo;
    @FXML private Label errUsername;
    @FXML private Label errPassword;
    @FXML private Label errRol;

    private final PersonaClient personaClient = new PersonaClient();
    private final UsuarioClient usuarioClient = new UsuarioClient();
    private final PacienteClient pacienteClient = new PacienteClient();
    private final MedicoClient medicoClient = new MedicoClient();

    private final Map<String, CheckBox> especialidadChecks = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        if (!configurarContextoNavegacion()) {
            return;
        }

        cmbGenero.getItems().addAll("HOMBRE", "MUJER", "OTRO");
        cmbRol.getItems().addAll("PACIENTE", "MEDICO_TERAPISTA", "ADMINISTRADOR", "AGENDADOR");
        cmbRol.setValue("PACIENTE");

        txtTelefono.setTextFormatter(PersonaFormSupport.digitsOnlyFormatter(10));
        txtDni.setTextFormatter(PersonaFormSupport.digitsOnlyFormatter(12));

        PersonaFormSupport.bindNameNormalization(txtPrimerNombre);
        PersonaFormSupport.bindNameNormalization(txtSegundoNombre);
        PersonaFormSupport.bindNameNormalization(txtPrimerApellido);
        PersonaFormSupport.bindNameNormalization(txtSegundoApellido);

        bindClearOnChange(txtPrimerNombre, errPrimerNombre);
        bindClearOnChange(txtSegundoNombre, errSegundoNombre);
        bindClearOnChange(txtPrimerApellido, errPrimerApellido);
        bindClearOnChange(txtSegundoApellido, errSegundoApellido);
        bindClearOnChange(cmbGenero, errGenero);
        bindClearOnChange(dateNacimiento, errFechaNacimiento);
        bindClearOnChange(txtTelefono, errTelefono);
        bindClearOnChange(txtDni, errDni);
        bindClearOnChange(txtCorreo, errCorreo);
        bindClearOnChange(txtUsername, errUsername);
        bindClearOnChange(txtPassword, errPassword);
        bindClearOnChange(cmbRol, errRol);

        inicializarEspecialidades();
        configurarVisibilidadEspecialidades(cmbRol.getValue());
        cmbRol.valueProperty().addListener((obs, anterior, nuevo) ->
                configurarVisibilidadEspecialidades(nuevo)
        );
    }

    private void inicializarEspecialidades() {
        for (String codigo : CODIGOS_ESPECIALIDAD) {
            CheckBox checkBox = new CheckBox(etiquetaEspecialidad(codigo));
            checkBox.setUserData(codigo);
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) ->
                    limpiarErrorEspecialidades()
            );
            especialidadChecks.put(codigo, checkBox);
            boxEspecialidades.getChildren().add(checkBox);
        }
    }

    private void configurarVisibilidadEspecialidades(String rol) {
        boolean esMedicoTerapeuta = ROL_MEDICO_TERAPISTA.equals(rol);

        lblEspecialidadesTitulo.setVisible(esMedicoTerapeuta);
        lblEspecialidadesTitulo.setManaged(esMedicoTerapeuta);

        panelEspecialidades.setVisible(esMedicoTerapeuta);
        panelEspecialidades.setManaged(esMedicoTerapeuta);

        especialidadChecks.values().forEach(cb -> cb.setDisable(!esMedicoTerapeuta));

        if (!esMedicoTerapeuta) {
            limpiarEspecialidadesSeleccionadas();
            limpiarErrorEspecialidades();
        }
    }

    private void limpiarErrorEspecialidades() {
        CheckBox referencia = especialidadChecks.isEmpty()
                ? null
                : especialidadChecks.get(CODIGOS_ESPECIALIDAD.get(0));
        if (referencia != null) {
            FormFieldHelper.clearFieldError(referencia, boxEspecialidades, errEspecialidades);
        }
    }

    private void limpiarEspecialidadesSeleccionadas() {
        especialidadChecks.values().forEach(cb -> cb.setSelected(false));
    }

    private List<String> obtenerEspecialidadesSeleccionadas() {
        List<String> seleccionadas = new ArrayList<>();
        for (Map.Entry<String, CheckBox> entry : especialidadChecks.entrySet()) {
            if (entry.getValue().isSelected()) {
                seleccionadas.add(entry.getKey());
            }
        }
        return seleccionadas;
    }

    private static String etiquetaEspecialidad(String codigo) {
        return switch (codigo) {
            case "TERAPEUTA_NEURAL" -> "Terapeuta Neural";
            case "QUIROPRACTICO" -> "Quiropráctico";
            case "FISIOTERAPEUTA" -> "Fisioterapeuta";
            default -> "Medicina General";
        };
    }

    @FXML
    private void handleRegister() {
        clearFormError();

        if (!validateForm()) {
            return;
        }

        Long personaId = null;

        try {
            CrearPersonaRequest personaRequest = new CrearPersonaRequest();
            personaRequest.setPrimerNombre(PersonaFormSupport.normalizedName(txtPrimerNombre));
            personaRequest.setSegundoNombre(PersonaFormSupport.normalizedNameOrNull(txtSegundoNombre));
            personaRequest.setPrimerApellido(PersonaFormSupport.normalizedName(txtPrimerApellido));
            personaRequest.setSegundoApellido(PersonaFormSupport.normalizedNameOrNull(txtSegundoApellido));
            personaRequest.setGenero(cmbGenero.getValue());
            personaRequest.setFechaNacimiento(dateNacimiento.getValue().toString());
            personaRequest.setTelefono(PersonaFormSupport.trim(txtTelefono));
            personaRequest.setDni(PersonaFormSupport.trim(txtDni));
            personaRequest.setCorreo(PersonaFormSupport.trimOrNull(txtCorreo));

            personaId = personaClient.crearPersona(personaRequest);
            String rol = cmbRol.getValue();

            if ("PACIENTE".equals(rol)) {
                pacienteClient.crearPaciente(personaId);
            } else if (ROL_MEDICO_TERAPISTA.equals(rol)) {
                medicoClient.crearMedico(
                        personaId,
                        "MEDICO",
                        obtenerEspecialidadesSeleccionadas()
                );
            }

            CrearUsuarioRequest usuarioRequest = new CrearUsuarioRequest();
            usuarioRequest.setPersonaId(personaId);
            usuarioRequest.setUsername(PersonaFormSupport.trim(txtUsername));
            usuarioRequest.setPassword(txtPassword.getText());
            usuarioRequest.setEmail(PersonaFormSupport.trimOrNull(txtCorreo));
            usuarioRequest.setFirstName(PersonaFormSupport.normalizedName(txtPrimerNombre));
            usuarioRequest.setLastName(PersonaFormSupport.normalizedName(txtPrimerApellido));
            usuarioRequest.setRoles(List.of(rol));

            usuarioClient.crearUsuario(usuarioRequest);

            showAlert("Éxito", "Usuario registrado correctamente", Alert.AlertType.INFORMATION);
            volverDespuesDeRegistro();

        } catch (ApiClientException e) {
            System.err.println("=== ERROR ApiClientException ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();

            revertirRegistro(personaId);
            mapServerError(e.getParsedError());
        } catch (Exception e) {
            System.err.println("=== ERROR GENERAL ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();

            revertirRegistro(personaId);
            mapServerError(ApiErrorParser.parse(e.getMessage()));
        }
    }

    private void revertirRegistro(Long personaId) {
        if (personaId != null) {
            personaClient.compensarRegistroFallido(personaId);
        }
    }

    @FXML
    private void handleVolver() {
        volverSinRegistrar();
    }

    private boolean configurarContextoNavegacion() {
        if (SessionManager.isRegisterFromAdminPanel()) {
            if (!SessionManager.isLoggedIn() || !SessionManager.hasRole("ADMINISTRADOR")) {
                SessionManager.endRegisterFromAdminPanel();
                SceneManager.showLogin("/view/auth_register/loginView.fxml", lblTitulo);
                return false;
            }

            lblTitulo.setText("REGISTRO DE USUARIO");
            lblSubtitulo.setText("Panel de administración — complete los datos del nuevo usuario.");
            lblSubtitulo.setVisible(true);
            lblSubtitulo.setManaged(true);
            btnVolver.setText("Volver al menú principal");
            return true;
        }

        lblSubtitulo.setVisible(false);
        lblSubtitulo.setManaged(false);
        btnVolver.setText("Volver al login");
        return true;
    }

    private void volverSinRegistrar() {
        if (SessionManager.isRegisterFromAdminPanel()) {
            SessionManager.endRegisterFromAdminPanel();
            SceneManager.showDashboard(
                    "/view/dashboard/administrador-dashboard.fxml",
                    btnVolver,
                    "PIEDRAZUL - Administrador"
            );
            return;
        }

        SessionManager.endRegisterFromAdminPanel();
        SceneManager.showLogin("/view/auth_register/loginView.fxml", btnVolver);
    }

    private void volverDespuesDeRegistro() {
        if (SessionManager.isRegisterFromAdminPanel()) {
            SessionManager.endRegisterFromAdminPanel();
            SceneManager.showDashboard(
                    "/view/dashboard/administrador-dashboard.fxml",
                    btnVolver,
                    "PIEDRAZUL - Administrador"
            );
            return;
        }

        SessionManager.endRegisterFromAdminPanel();
        SceneManager.showLogin("/view/auth_register/loginView.fxml", btnVolver);
    }

    private boolean validateForm() {
        PersonaFormSupport.normalizeNameFields(
                txtPrimerNombre,
                txtSegundoNombre,
                txtPrimerApellido,
                txtSegundoApellido
        );

        boolean valid = true;

        valid &= PersonaFormSupport.requireName(txtPrimerNombre, errPrimerNombre, "Ingrese el primer nombre");
        valid &= PersonaFormSupport.optionalName(txtSegundoNombre, errSegundoNombre);
        valid &= PersonaFormSupport.requireName(txtPrimerApellido, errPrimerApellido, "Ingrese el primer apellido");
        valid &= PersonaFormSupport.optionalName(txtSegundoApellido, errSegundoApellido);

        if (cmbGenero.getValue() == null) {
            FormFieldHelper.showFieldError(cmbGenero, errGenero, "Seleccione un género");
            valid = false;
        }

        if (dateNacimiento.getValue() == null) {
            FormFieldHelper.showFieldError(dateNacimiento, errFechaNacimiento,
                    "Seleccione la fecha de nacimiento");
            valid = false;
        } else if (dateNacimiento.getValue().isAfter(LocalDate.now())) {
            FormFieldHelper.showFieldError(dateNacimiento, errFechaNacimiento,
                    "La fecha no puede ser futura");
            valid = false;
        }

        String telefono = PersonaFormSupport.trim(txtTelefono);
        if (telefono.isEmpty()) {
            FormFieldHelper.showFieldError(txtTelefono, errTelefono, "Ingrese el teléfono");
            valid = false;
        } else if (telefono.length() != 10) {
            FormFieldHelper.showFieldError(txtTelefono, errTelefono,
                    "El teléfono debe tener 10 dígitos");
            valid = false;
        }

        String dni = PersonaFormSupport.trim(txtDni);
        if (dni.isEmpty()) {
            FormFieldHelper.showFieldError(txtDni, errDni, "Ingrese el DNI");
            valid = false;
        } else if (dni.length() < 6) {
            FormFieldHelper.showFieldError(txtDni, errDni,
                    "El DNI debe tener al menos 6 dígitos");
            valid = false;
        }

        String correo = PersonaFormSupport.trimOrNull(txtCorreo);
        if (correo != null && !EMAIL_PATTERN.matcher(correo).matches()) {
            FormFieldHelper.showFieldError(txtCorreo, errCorreo,
                    "Ingrese un correo válido. Ej: nombre@ejemplo.com");
            valid = false;
        }

        String username = PersonaFormSupport.trim(txtUsername);
        if (username.isEmpty()) {
            FormFieldHelper.showFieldError(txtUsername, errUsername, "Ingrese un usuario");
            valid = false;
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            FormFieldHelper.showFieldError(txtUsername, errUsername,
                    "Use 3-50 caracteres: letras, números, punto o guion bajo");
            valid = false;
        }

        String password = txtPassword.getText() == null ? "" : txtPassword.getText();
        if (password.isBlank()) {
            FormFieldHelper.showFieldError(txtPassword, errPassword, "Ingrese una contraseña");
            valid = false;
        } else if (password.length() < 6) {
            FormFieldHelper.showFieldError(txtPassword, errPassword,
                    "La contraseña debe tener al menos 6 caracteres");
            valid = false;
        }

        if (cmbRol.getValue() == null) {
            FormFieldHelper.showFieldError(cmbRol, errRol, "Seleccione un rol");
            valid = false;
        }

        if (ROL_MEDICO_TERAPISTA.equals(cmbRol.getValue())
                && obtenerEspecialidadesSeleccionadas().isEmpty()) {
            CheckBox referencia = especialidadChecks.get(CODIGOS_ESPECIALIDAD.get(0));
            FormFieldHelper.showFieldError(
                    referencia,
                    boxEspecialidades,
                    errEspecialidades,
                    "Seleccione al menos una especialidad"
            );
            valid = false;
        }

        return valid;
    }

    private void mapServerError(ApiErrorParser.ParsedApiError parsed) {
        if (parsed == null) {
            showFormError("No se pudo completar el registro. Intente nuevamente.");
            return;
        }

        boolean mapped = false;
        for (Map.Entry<String, String> entry : parsed.fieldErrors().entrySet()) {
            mapped |= applyFieldError(entry.getKey(), humanizeFieldError(entry.getKey(), entry.getValue()));
        }

        if (mapped) {
            return;
        }

        String message = parsed.message() == null ? "" : parsed.message().toLowerCase();

        if (message.contains("dni")) {
            FormFieldHelper.showFieldError(txtDni, errDni, parsed.message());
        } else if (message.contains("username") || message.contains("usuario")) {
            FormFieldHelper.showFieldError(txtUsername, errUsername, humanizeFieldError("username", parsed.message()));
        } else if (message.contains("email") || message.contains("correo")) {
            FormFieldHelper.showFieldError(txtCorreo, errCorreo, humanizeFieldError("email", parsed.message()));
        } else if (message.contains("password") || message.contains("contrase")) {
            FormFieldHelper.showFieldError(txtPassword, errPassword, humanizeFieldError("password", parsed.message()));
        } else {
            showFormError(parsed.message());
        }
    }

    private boolean applyFieldError(String field, String message) {
        return switch (field) {
            case "primerNombre" -> {
                FormFieldHelper.showFieldError(txtPrimerNombre, errPrimerNombre, message);
                yield true;
            }
            case "segundoNombre" -> {
                FormFieldHelper.showFieldError(txtSegundoNombre, errSegundoNombre, message);
                yield true;
            }
            case "primerApellido" -> {
                FormFieldHelper.showFieldError(txtPrimerApellido, errPrimerApellido, message);
                yield true;
            }
            case "segundoApellido" -> {
                FormFieldHelper.showFieldError(txtSegundoApellido, errSegundoApellido, message);
                yield true;
            }
            case "genero" -> {
                FormFieldHelper.showFieldError(cmbGenero, errGenero, message);
                yield true;
            }
            case "fechaNacimiento" -> {
                FormFieldHelper.showFieldError(dateNacimiento, errFechaNacimiento, message);
                yield true;
            }
            case "telefono" -> {
                FormFieldHelper.showFieldError(txtTelefono, errTelefono, message);
                yield true;
            }
            case "dni" -> {
                FormFieldHelper.showFieldError(txtDni, errDni, message);
                yield true;
            }
            case "correo", "email" -> {
                FormFieldHelper.showFieldError(txtCorreo, errCorreo, message);
                yield true;
            }
            case "username" -> {
                FormFieldHelper.showFieldError(txtUsername, errUsername, message);
                yield true;
            }
            case "password" -> {
                FormFieldHelper.showFieldError(txtPassword, errPassword, message);
                yield true;
            }
            case "roles", "rol" -> {
                FormFieldHelper.showFieldError(cmbRol, errRol, message);
                yield true;
            }
            default -> false;
        };
    }

    private String humanizeFieldError(String field, String raw) {
        if (raw == null || raw.isBlank()) {
            return "Revise este campo.";
        }
        if ("email".equals(field) && raw.toLowerCase().contains("obligatorio")) {
            return "El correo es opcional. Reinicie usuarios-service si ve este mensaje.";
        }
        if (raw.toLowerCase().contains("email invalido")) {
            return "Ingrese un correo válido. Ej: nombre@ejemplo.com";
        }
        if ("username".equals(field) && raw.contains("3")) {
            return "Use entre 3 y 50 caracteres: letras, números, punto o guion bajo";
        }
        if ("password".equals(field)) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        return raw;
    }

    private void showFormError(String message) {
        lblFormError.setText(message);
        lblFormError.setVisible(true);
        lblFormError.setManaged(true);
    }

    private void clearFormError() {
        lblFormError.setText("");
        lblFormError.setVisible(false);
        lblFormError.setManaged(false);
    }

    private void bindClearOnChange(Control control, Label errorLabel) {
        FormFieldHelper.bindClearOnChange(control, errorLabel);
        if (control instanceof TextInputControl) {
            ((TextInputControl) control).textProperty().addListener(
                    (obs, oldVal, newVal) -> clearFormError()
            );
        } else if (control instanceof ComboBox<?>) {
            ((ComboBox<?>) control).valueProperty().addListener(
                    (obs, oldVal, newVal) -> clearFormError()
            );
        } else if (control instanceof DatePicker datePicker) {
            datePicker.valueProperty().addListener(
                    (obs, oldVal, newVal) -> clearFormError()
            );
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
