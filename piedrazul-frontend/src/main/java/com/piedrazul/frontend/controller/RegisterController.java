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
import com.piedrazul.frontend.util.NameNormalizer;
import com.piedrazul.frontend.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class RegisterController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}\\s'-]+$");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[\\w.]{3,50}$");
    private static final Pattern DIGITS_ONLY = Pattern.compile("\\d*");

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
    @FXML private Label lblFormError;

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

    @FXML
    public void initialize() {
        cmbGenero.getItems().addAll("HOMBRE", "MUJER", "OTRO");
        cmbRol.getItems().addAll("PACIENTE", "MEDICO_TERAPISTA", "ADMINISTRADOR", "AGENDADOR");
        cmbRol.setValue("PACIENTE");

        txtTelefono.setTextFormatter(digitsOnlyFormatter(10));
        txtDni.setTextFormatter(digitsOnlyFormatter(12));

        bindNameNormalization(txtPrimerNombre);
        bindNameNormalization(txtSegundoNombre);
        bindNameNormalization(txtPrimerApellido);
        bindNameNormalization(txtSegundoApellido);

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
            personaRequest.setPrimerNombre(normalizedName(txtPrimerNombre));
            personaRequest.setSegundoNombre(normalizedNameOrNull(txtSegundoNombre));
            personaRequest.setPrimerApellido(normalizedName(txtPrimerApellido));
            personaRequest.setSegundoApellido(normalizedNameOrNull(txtSegundoApellido));
            personaRequest.setGenero(cmbGenero.getValue());
            personaRequest.setFechaNacimiento(dateNacimiento.getValue().toString());
            personaRequest.setTelefono(trim(txtTelefono));
            personaRequest.setDni(trim(txtDni));
            personaRequest.setCorreo(trimOrNull(txtCorreo));

            personaId = personaClient.crearPersona(personaRequest);
            String rol = cmbRol.getValue();

            if ("PACIENTE".equals(rol)) {
                pacienteClient.crearPaciente(personaId);
            } else if ("MEDICO_TERAPISTA".equals(rol)) {
                medicoClient.crearMedico(personaId, "MEDICO", List.of("GENERAL"));
            }

            CrearUsuarioRequest usuarioRequest = new CrearUsuarioRequest();
            usuarioRequest.setPersonaId(personaId);
            usuarioRequest.setUsername(trim(txtUsername));
            usuarioRequest.setPassword(txtPassword.getText());
            usuarioRequest.setEmail(trimOrNull(txtCorreo));
            usuarioRequest.setFirstName(normalizedName(txtPrimerNombre));
            usuarioRequest.setLastName(normalizedName(txtPrimerApellido));
            usuarioRequest.setRoles(List.of(rol));

            usuarioClient.crearUsuario(usuarioRequest);

            showAlert("Éxito", "Usuario registrado correctamente", Alert.AlertType.INFORMATION);
            goLogin();

        } catch (ApiClientException e) {
            revertirRegistro(personaId);
            mapServerError(e.getParsedError());
        } catch (Exception e) {
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
    private void goLogin() {
        SceneManager.showLogin("/view/auth_register/loginView.fxml", txtPrimerNombre);
    }

    private boolean validateForm() {
        normalizeNameFields();

        boolean valid = true;

        valid &= requireName(txtPrimerNombre, errPrimerNombre, "Ingrese el primer nombre");
        valid &= optionalName(txtSegundoNombre, errSegundoNombre);
        valid &= requireName(txtPrimerApellido, errPrimerApellido, "Ingrese el primer apellido");
        valid &= optionalName(txtSegundoApellido, errSegundoApellido);

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

        String telefono = trim(txtTelefono);
        if (telefono.isEmpty()) {
            FormFieldHelper.showFieldError(txtTelefono, errTelefono, "Ingrese el teléfono");
            valid = false;
        } else if (telefono.length() != 10) {
            FormFieldHelper.showFieldError(txtTelefono, errTelefono,
                    "El teléfono debe tener 10 dígitos");
            valid = false;
        }

        String dni = trim(txtDni);
        if (dni.isEmpty()) {
            FormFieldHelper.showFieldError(txtDni, errDni, "Ingrese el DNI");
            valid = false;
        } else if (dni.length() < 6) {
            FormFieldHelper.showFieldError(txtDni, errDni,
                    "El DNI debe tener al menos 6 dígitos");
            valid = false;
        }

        String correo = trimOrNull(txtCorreo);
        if (correo != null && !EMAIL_PATTERN.matcher(correo).matches()) {
            FormFieldHelper.showFieldError(txtCorreo, errCorreo,
                    "Ingrese un correo válido. Ej: nombre@ejemplo.com");
            valid = false;
        }

        String username = trim(txtUsername);
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

        return valid;
    }

    private boolean requireName(TextField field, Label errorLabel, String blankMessage) {
        String value = normalizedName(field);
        if (value.isEmpty()) {
            FormFieldHelper.showFieldError(field, errorLabel, blankMessage);
            return false;
        }
        if (!NAME_PATTERN.matcher(value).matches()) {
            FormFieldHelper.showFieldError(field, errorLabel,
                    "Solo letras, espacios, apóstrofes o guiones");
            return false;
        }
        return true;
    }

    private boolean optionalName(TextField field, Label errorLabel) {
        String value = normalizedName(field);
        if (value.isEmpty()) {
            return true;
        }
        if (!NAME_PATTERN.matcher(value).matches()) {
            FormFieldHelper.showFieldError(field, errorLabel,
                    "Solo letras, espacios, apóstrofes o guiones");
            return false;
        }
        return true;
    }

    private void bindNameNormalization(TextField field) {
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                applyNameNormalization(field);
            }
        });
    }

    private void normalizeNameFields() {
        applyNameNormalization(txtPrimerNombre);
        applyNameNormalization(txtSegundoNombre);
        applyNameNormalization(txtPrimerApellido);
        applyNameNormalization(txtSegundoApellido);
    }

    private void applyNameNormalization(TextField field) {
        String current = field.getText() == null ? "" : field.getText();
        String normalized = NameNormalizer.normalize(current);
        if (!normalized.equals(current)) {
            field.setText(normalized);
        }
    }

    private String normalizedName(TextField field) {
        return NameNormalizer.normalize(field.getText());
    }

    private String normalizedNameOrNull(TextField field) {
        return NameNormalizer.normalizeOrNull(field.getText());
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
            case "primerApellido" -> {
                FormFieldHelper.showFieldError(txtPrimerApellido, errPrimerApellido, message);
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

    private TextFormatter<String> digitsOnlyFormatter(int maxLength) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String next = change.getControlNewText();
            if (!DIGITS_ONLY.matcher(next).matches()) {
                return null;
            }
            if (next.length() > maxLength) {
                return null;
            }
            return change;
        };
        return new TextFormatter<>(filter);
    }

    private String trim(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String trimOrNull(TextField field) {
        String value = trim(field);
        return value.isEmpty() ? null : value;
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
