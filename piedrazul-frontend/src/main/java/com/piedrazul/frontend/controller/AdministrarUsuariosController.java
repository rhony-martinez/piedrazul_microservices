package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.PersonaClient;
import com.piedrazul.frontend.client.UsuarioClient;
import com.piedrazul.frontend.dto.UsuarioAdminRow;
import com.piedrazul.frontend.dto.request.ActualizarPersonaRequest;
import com.piedrazul.frontend.dto.response.PersonaResponse;
import com.piedrazul.frontend.dto.response.UsuarioResponse;
import com.piedrazul.frontend.session.SessionManager;
import com.piedrazul.frontend.util.ApiClientException;
import com.piedrazul.frontend.util.ApiErrorParser;
import com.piedrazul.frontend.util.FormFieldHelper;
import com.piedrazul.frontend.util.NameNormalizer;
import com.piedrazul.frontend.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter;

import java.time.LocalDate;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdministrarUsuariosController {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s'-]+$");
    private static final Pattern DIGITS_ONLY = Pattern.compile("\\d*");

    @FXML private TextField txtBuscar;
    @FXML private TableView<UsuarioAdminRow> tablaUsuarios;
    @FXML private TableColumn<UsuarioAdminRow, String> colUsername;
    @FXML private TableColumn<UsuarioAdminRow, String> colNombre;
    @FXML private TableColumn<UsuarioAdminRow, String> colDni;

    @FXML private Label lblUsuarioSeleccionado;
    @FXML private Label lblEstadoCarga;
    @FXML private Label lblFormError;
    @FXML private Label lblEstadoGuardado;

    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDni;
    @FXML private TextField txtGenero;
    @FXML private TextField txtCorreo;

    @FXML private Label errPrimerNombre;
    @FXML private Label errSegundoNombre;
    @FXML private Label errPrimerApellido;
    @FXML private Label errSegundoApellido;
    @FXML private Label errFechaNacimiento;
    @FXML private Label errTelefono;

    @FXML private Button btnGuardar;
    @FXML private Button btnRecargar;

    private final UsuarioClient usuarioClient = new UsuarioClient();
    private final PersonaClient personaClient = new PersonaClient();

    private final ObservableList<UsuarioAdminRow> todosLosUsuarios = FXCollections.observableArrayList();
    private FilteredList<UsuarioAdminRow> usuariosFiltrados;

    private Long personaIdSeleccionada;

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn() || !SessionManager.hasRole("ADMINISTRADOR")) {
            SessionManager.clear();
            SceneManager.showLogin("/view/auth_register/loginView.fxml", lblEstadoCarga);
            return;
        }

        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colDni.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDni() != null ? c.getValue().getDni() : ""
        ));

        usuariosFiltrados = new FilteredList<>(todosLosUsuarios, p -> true);
        tablaUsuarios.setItems(usuariosFiltrados);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro(newVal));

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, row) -> {
            if (row != null) {
                cargarPersonaEnFormulario(row.getPersonaId(), row.getUsername());
            }
        });

        txtTelefono.setTextFormatter(digitsOnlyFormatter(10));
        dateNacimiento.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || (date != null && date.isAfter(LocalDate.now())));
            }
        });

        bindClearOnChange(txtPrimerNombre, errPrimerNombre);
        bindClearOnChange(txtSegundoNombre, errSegundoNombre);
        bindClearOnChange(txtPrimerApellido, errPrimerApellido);
        bindClearOnChange(txtSegundoApellido, errSegundoApellido);
        bindClearOnChange(dateNacimiento, errFechaNacimiento);
        bindClearOnChange(txtTelefono, errTelefono);

        deshabilitarFormularioEdicion();
        cargarUsuarios();
    }

    @FXML
    private void handleRecargar() {
        cargarUsuarios();
    }

    @FXML
    private void handleGuardar() {
        ocultarMensajes();

        if (personaIdSeleccionada == null) {
            mostrarError("Seleccione un usuario de la tabla.");
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        try {
            ActualizarPersonaRequest request = new ActualizarPersonaRequest();
            request.setPrimerNombre(normalizedName(txtPrimerNombre));
            request.setSegundoNombre(normalizedNameOrNull(txtSegundoNombre));
            request.setPrimerApellido(normalizedName(txtPrimerApellido));
            request.setSegundoApellido(normalizedNameOrNull(txtSegundoApellido));
            request.setFechaNacimiento(dateNacimiento.getValue());
            request.setTelefono(trim(txtTelefono));

            PersonaResponse actualizada =
                    personaClient.actualizarPersona(personaIdSeleccionada, request);

            actualizarFilaSeleccionada(actualizada);
            mostrarExito("Datos actualizados correctamente.");

        } catch (ApiClientException e) {
            mapServerError(e.getParsedError());
        } catch (Exception e) {
            mostrarError(e.getMessage() != null ? e.getMessage() : "No se pudo guardar los cambios.");
        }
    }

    @FXML
    private void handleVolver() {
        SceneManager.showDashboard(
                "/view/dashboard/administrador-dashboard.fxml",
                btnGuardar,
                "PIEDRAZUL - Administrador"
        );
    }

    private void cargarUsuarios() {
        ocultarMensajes();
        mostrarCarga("Cargando usuarios...");
        deshabilitarFormularioEdicion();
        personaIdSeleccionada = null;

        try {
            List<UsuarioResponse> usuarios = usuarioClient.listarUsuarios();
            Map<Long, PersonaResponse> personasPorId = personaClient.listarPersonas().stream()
                    .filter(p -> p.getId() != null)
                    .collect(Collectors.toMap(
                            PersonaResponse::getId,
                            p -> p,
                            (a, b) -> a
                    ));

            List<UsuarioAdminRow> filas = new ArrayList<>();
            for (UsuarioResponse usuario : usuarios) {
                if (usuario.getPersonaId() == null) {
                    continue;
                }
                PersonaResponse persona = personasPorId.get(usuario.getPersonaId());
                String nombre = persona != null ? persona.getNombreCompleto() : "Sin datos de persona";
                String dni = persona != null ? persona.getDni() : "";
                filas.add(new UsuarioAdminRow(
                        usuario.getUsername(),
                        usuario.getPersonaId(),
                        nombre,
                        dni
                ));
            }

            filas.sort(Comparator.comparing(UsuarioAdminRow::getUsername, String.CASE_INSENSITIVE_ORDER));
            todosLosUsuarios.setAll(filas);
            aplicarFiltro(txtBuscar.getText());
            ocultarCarga();

            if (filas.isEmpty()) {
                mostrarError("No hay usuarios registrados en el sistema.");
            }

        } catch (Exception e) {
            ocultarCarga();
            todosLosUsuarios.clear();
            mostrarError("No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    private void cargarPersonaEnFormulario(Long personaId, String username) {
        ocultarMensajes();
        personaIdSeleccionada = personaId;

        try {
            PersonaResponse persona = personaClient.obtenerPorId(personaId);
            lblUsuarioSeleccionado.setText("Editando: " + username + " (persona #" + personaId + ")");

            txtPrimerNombre.setText(persona.getPrimerNombre());
            txtSegundoNombre.setText(persona.getSegundoNombre());
            txtPrimerApellido.setText(persona.getPrimerApellido());
            txtSegundoApellido.setText(persona.getSegundoApellido());
            dateNacimiento.setValue(persona.getFechaNacimiento());
            txtTelefono.setText(persona.getTelefono());
            txtDni.setText(persona.getDni());
            txtGenero.setText(persona.getGenero());
            txtCorreo.setText(persona.getCorreo());

            habilitarFormularioEdicion();

        } catch (Exception e) {
            deshabilitarFormularioEdicion();
            personaIdSeleccionada = null;
            mostrarError("No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void actualizarFilaSeleccionada(PersonaResponse persona) {
        UsuarioAdminRow seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        int index = todosLosUsuarios.indexOf(seleccionado);
        if (index < 0) {
            return;
        }

        UsuarioAdminRow actualizado = new UsuarioAdminRow(
                seleccionado.getUsername(),
                seleccionado.getPersonaId(),
                persona.getNombreCompleto(),
                persona.getDni()
        );
        todosLosUsuarios.set(index, actualizado);
        tablaUsuarios.getSelectionModel().select(actualizado);
    }

    private void aplicarFiltro(String texto) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        usuariosFiltrados.setPredicate(row -> {
            if (filtro.isEmpty()) {
                return true;
            }
            return contiene(row.getUsername(), filtro)
                    || contiene(row.getNombreCompleto(), filtro)
                    || contiene(row.getDni(), filtro);
        });
    }

    private boolean contiene(String valor, String filtro) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(filtro);
    }

    private boolean validarFormulario() {
        boolean valido = true;

        valido &= requireName(txtPrimerNombre, errPrimerNombre, "Ingrese el primer nombre");
        valido &= optionalName(txtSegundoNombre, errSegundoNombre);
        valido &= requireName(txtPrimerApellido, errPrimerApellido, "Ingrese el primer apellido");
        valido &= optionalName(txtSegundoApellido, errSegundoApellido);

        if (dateNacimiento.getValue() == null) {
            FormFieldHelper.showFieldError(dateNacimiento, errFechaNacimiento,
                    "Seleccione la fecha de nacimiento");
            valido = false;
        } else if (dateNacimiento.getValue().isAfter(LocalDate.now())) {
            FormFieldHelper.showFieldError(dateNacimiento, errFechaNacimiento,
                    "La fecha no puede ser futura");
            valido = false;
        }

        String telefono = trim(txtTelefono);
        if (telefono.isEmpty()) {
            FormFieldHelper.showFieldError(txtTelefono, errTelefono, "Ingrese el teléfono");
            valido = false;
        } else if (telefono.length() != 10) {
            FormFieldHelper.showFieldError(txtTelefono, errTelefono,
                    "El teléfono debe tener 10 dígitos");
            valido = false;
        }

        return valido;
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

    private void habilitarFormularioEdicion() {
        setCamposEditables(true);
        btnGuardar.setDisable(false);
    }

    private void deshabilitarFormularioEdicion() {
        setCamposEditables(false);
        btnGuardar.setDisable(true);
        lblUsuarioSeleccionado.setText("Seleccione un usuario de la tabla");
        limpiarFormulario();
    }

    private void setCamposEditables(boolean editable) {
        txtPrimerNombre.setDisable(!editable);
        txtSegundoNombre.setDisable(!editable);
        txtPrimerApellido.setDisable(!editable);
        txtSegundoApellido.setDisable(!editable);
        dateNacimiento.setDisable(!editable);
        txtTelefono.setDisable(!editable);
    }

    private void limpiarFormulario() {
        txtPrimerNombre.clear();
        txtSegundoNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        dateNacimiento.setValue(null);
        txtTelefono.clear();
        txtDni.clear();
        txtGenero.clear();
        txtCorreo.clear();
    }

    private void mapServerError(ApiErrorParser.ParsedApiError parsed) {
        if (parsed == null || parsed.message() == null) {
            mostrarError("No se pudo guardar los cambios.");
            return;
        }
        mostrarError(parsed.message());
    }

    private void mostrarCarga(String mensaje) {
        lblEstadoCarga.setText(mensaje);
        lblEstadoCarga.getStyleClass().setAll("status-label", "status-warning");
        lblEstadoCarga.setVisible(true);
        lblEstadoCarga.setManaged(true);
    }

    private void ocultarCarga() {
        lblEstadoCarga.setVisible(false);
        lblEstadoCarga.setManaged(false);
    }

    private void mostrarError(String mensaje) {
        lblFormError.setText(mensaje);
        lblFormError.setVisible(true);
        lblFormError.setManaged(true);
    }

    private void mostrarExito(String mensaje) {
        lblEstadoGuardado.setText(mensaje);
        lblEstadoGuardado.setVisible(true);
        lblEstadoGuardado.setManaged(true);
    }

    private void ocultarMensajes() {
        lblFormError.setVisible(false);
        lblFormError.setManaged(false);
        lblEstadoGuardado.setVisible(false);
        lblEstadoGuardado.setManaged(false);
        ocultarCarga();
    }

    private void bindClearOnChange(Control control, Label errorLabel) {
        FormFieldHelper.bindClearOnChange(control, errorLabel);
        if (control instanceof TextInputControl textInput) {
            textInput.textProperty().addListener((obs, o, n) -> ocultarMensajesParcial());
        } else if (control instanceof DatePicker datePicker) {
            datePicker.valueProperty().addListener((obs, o, n) -> ocultarMensajesParcial());
        }
    }

    private void ocultarMensajesParcial() {
        lblFormError.setVisible(false);
        lblFormError.setManaged(false);
        lblEstadoGuardado.setVisible(false);
        lblEstadoGuardado.setManaged(false);
    }

    private TextFormatter<String> digitsOnlyFormatter(int maxLength) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String next = change.getControlNewText();
            if (!DIGITS_ONLY.matcher(next).matches() || next.length() > maxLength) {
                return null;
            }
            return change;
        };
        return new TextFormatter<>(filter);
    }

    private String trim(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String normalizedName(TextField field) {
        return NameNormalizer.normalize(field.getText());
    }

    private String normalizedNameOrNull(TextField field) {
        return NameNormalizer.normalizeOrNull(field.getText());
    }
}
