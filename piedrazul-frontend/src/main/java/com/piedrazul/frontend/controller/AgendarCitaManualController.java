package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.CitaClient;
import com.piedrazul.frontend.client.ConfiguracionClient;
import com.piedrazul.frontend.client.MedicoClient;
import com.piedrazul.frontend.client.PacienteClient;
import com.piedrazul.frontend.client.PersonaClient;
import com.piedrazul.frontend.dto.request.CrearCitaAutonomaRequest;
import com.piedrazul.frontend.dto.response.MedicoResponse;
import com.piedrazul.frontend.dto.response.PacienteListItem;
import com.piedrazul.frontend.dto.response.PacienteResponse;
import com.piedrazul.frontend.dto.response.PersonaResponse;
import com.piedrazul.frontend.session.SessionManager;
import com.piedrazul.frontend.util.EspecialidadLabels;
import com.piedrazul.frontend.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AgendarCitaManualController {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MAX_MOTIVO_CARACTERES = 500;

    private static final List<String> ESPECIALIDADES = List.of(
            "GENERAL", "TERAPEUTA_NEURAL", "QUIROPRACTICO", "FISIOTERAPEUTA"
    );

    @FXML private Label lblBienvenida;
    @FXML private Label lblPasoActual;
    @FXML private Label lblSlotsGuia;
    @FXML private Label lblSlotsEstado;
    @FXML private Label lblEstadoGeneral;
    @FXML private Label errPaciente;
    @FXML private Label errEspecialidad;
    @FXML private Label errMedico;
    @FXML private Label errFecha;
    @FXML private Label errSlot;
    @FXML private Label errMotivo;
    @FXML private ComboBox<PacienteListItem> cmbPacientes;
    @FXML private ComboBox<String> cmbEspecialidad;
    @FXML private ComboBox<MedicoResponse> cmbMedicos;
    @FXML private DatePicker dpFecha;
    @FXML private TableView<LocalDateTime> tablaSlots;
    @FXML private TableColumn<LocalDateTime, String> colSlot;
    @FXML private TextArea txtMotivoAgendamiento;
    @FXML private Button btnAgendar;
    @FXML private Button btnVolver;

    private final MedicoClient medicoClient = new MedicoClient();
    private final CitaClient citaClient = new CitaClient();
    private final PacienteClient pacienteClient = new PacienteClient();
    private final PersonaClient personaClient = new PersonaClient();
    private final ConfiguracionClient configuracionClient = new ConfiguracionClient();

    private List<MedicoResponse> todosLosMedicos = List.of();
    private Set<LocalDate> fechasFestivas = new HashSet<>();
    private PacienteListItem pacienteSeleccionado;
    private String especialidadSeleccionada;
    private MedicoResponse medicoSeleccionado;
    private LocalDateTime slotSeleccionado;
    private Long agendadorId;

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn() || !SessionManager.hasRole("AGENDADOR")) {
            SessionManager.clear();
            SceneManager.showLogin("/view/auth_register/loginView.fxml", btnAgendar);
            return;
        }

        agendadorId = SessionManager.getPersonaId();
        String username = SessionManager.getUsername();
        if (username != null && !username.isBlank()) {
            lblBienvenida.setText("Agendamiento Manual - " + username);
        }

        colSlot.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().format(FORMATO_HORA)
                ));

        tablaSlots.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            slotSeleccionado = newVal;
            btnAgendar.setDisable(newVal == null);
            ocultarError(errSlot);
            if (newVal != null) {
                ocultarEstadoSlots();
            }
            actualizarGuia();
        });

        cmbPacientes.valueProperty().addListener((obs, oldVal, newVal) -> {
            pacienteSeleccionado = newVal;
            ocultarError(errPaciente);
            limpiarEstadoGeneral();
            cargarSlots();
            actualizarGuia();
        });

        cmbEspecialidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            especialidadSeleccionada = newVal;
            ocultarError(errEspecialidad);
            medicoSeleccionado = null;
            cmbMedicos.getSelectionModel().clearSelection();
            limpiarEstadoGeneral();
            aplicarFiltroMedicos();
            cargarSlots();
            actualizarGuia();
        });

        cmbMedicos.valueProperty().addListener((obs, oldVal, newVal) -> {
            medicoSeleccionado = newVal;
            ocultarError(errMedico);
            limpiarEstadoGeneral();
            cargarSlots();
            actualizarGuia();
        });

        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
            ocultarError(errFecha);
            limpiarEstadoGeneral();
            cargarSlots();
            actualizarGuia();
        });

        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean esFestivo = !empty && fechasFestivas.contains(date);
                setDisable(empty || date.isBefore(LocalDate.now()) || esFestivo);
                if (esFestivo) {
                    setStyle("-fx-background-color: #fde8e8;");
                }
            }
        });

        cmbEspecialidad.setItems(FXCollections.observableArrayList(ESPECIALIDADES));
        cargarFestivos();
        cargarPacientes();
        cargarMedicos();
        actualizarGuia();
    }

    private void cargarFestivos() {
        try {
            fechasFestivas = new HashSet<>(configuracionClient.obtenerFestivos());
        } catch (Exception e) {
            fechasFestivas = new HashSet<>();
        }
    }

    private void cargarPacientes() {
        try {
            List<PacienteResponse> pacientes = pacienteClient.obtenerPacientes();
            Map<Long, PersonaResponse> personasPorId = personaClient.listarPersonas().stream()
                    .collect(Collectors.toMap(PersonaResponse::getId, p -> p, (a, b) -> a));

            List<PacienteListItem> items = pacientes.stream()
                    .map(p -> {
                        PersonaResponse persona = personasPorId.get(p.getPersonaId());
                        String nombre = persona != null
                                ? persona.getPrimerNombre() + " " + persona.getPrimerApellido()
                                : "Paciente #" + p.getPersonaId();
                        return new PacienteListItem(p.getPersonaId(), nombre.trim());
                    })
                    .sorted(Comparator.comparing(PacienteListItem::getNombreCompleto))
                    .toList();

            cmbPacientes.setItems(FXCollections.observableArrayList(items));

            if (items.isEmpty()) {
                mostrarEstadoGeneral(
                        "No hay pacientes registrados en el sistema.",
                        "agendar-status-warning"
                );
            }
        } catch (Exception e) {
            mostrarEstadoGeneral(
                    "No se pudieron cargar los pacientes: " + e.getMessage(),
                    "agendar-status-error"
            );
        }
    }

    private void cargarMedicos() {
        try {
            todosLosMedicos = medicoClient.obtenerMedicos();
            aplicarFiltroMedicos();
            if (todosLosMedicos.isEmpty()) {
                mostrarEstadoGeneral(
                        "No hay médicos disponibles en este momento.",
                        "agendar-status-warning"
                );
            }
        } catch (Exception e) {
            mostrarEstadoGeneral(
                    "No se pudieron cargar los médicos: " + e.getMessage(),
                    "agendar-status-error"
            );
        }
    }

    private void aplicarFiltroMedicos() {
        if (especialidadSeleccionada == null || especialidadSeleccionada.isBlank()) {
            cmbMedicos.setItems(FXCollections.observableArrayList());
            cmbMedicos.setDisable(true);
            return;
        }

        cmbMedicos.setDisable(false);
        List<MedicoResponse> filtrados = todosLosMedicos.stream()
                .filter(m -> medicoAtiendeEspecialidad(m, especialidadSeleccionada))
                .collect(Collectors.toList());

        cmbMedicos.setItems(FXCollections.observableArrayList(filtrados));

        if (filtrados.isEmpty()) {
            mostrarEstadoGeneral(
                    "No hay médicos disponibles para " + etiquetaEspecialidad(especialidadSeleccionada) + ".",
                    "agendar-status-warning"
            );
        }
    }

    private boolean medicoAtiendeEspecialidad(MedicoResponse medico, String especialidad) {
        if (medico.getEspecialidades() == null || medico.getEspecialidades().isEmpty()) {
            return "GENERAL".equals(especialidad);
        }
        return medico.getEspecialidades().stream()
                .anyMatch(e -> e.equalsIgnoreCase(especialidad));
    }

    private void cargarSlots() {
        tablaSlots.getSelectionModel().clearSelection();
        slotSeleccionado = null;
        btnAgendar.setDisable(true);
        ocultarError(errSlot);
        ocultarEstadoSlots();

        if (pacienteSeleccionado == null || especialidadSeleccionada == null
                || medicoSeleccionado == null || dpFecha.getValue() == null) {
            tablaSlots.setItems(FXCollections.observableArrayList());
            actualizarGuia();
            return;
        }

        try {
            List<LocalDateTime> slots = citaClient.obtenerSlotsDisponibles(
                    medicoSeleccionado.getPersonaId(),
                    pacienteSeleccionado.getPersonaId()
            );
            LocalDate fechaSeleccionada = dpFecha.getValue();
            List<LocalDateTime> slotsFiltrados = slots.stream()
                    .filter(slot -> slot.toLocalDate().equals(fechaSeleccionada))
                    .toList();

            tablaSlots.setItems(FXCollections.observableArrayList(slotsFiltrados));

            if (slotsFiltrados.isEmpty()) {
                mostrarEstadoSlots(
                        "No hay horarios disponibles para esta fecha.",
                        "agendar-status-warning"
                );
                lblSlotsGuia.setText("No se encontraron horarios para la fecha seleccionada.");
            } else {
                lblSlotsGuia.setText(
                        "Seleccione la hora (" + slotsFiltrados.size() + " disponible"
                                + (slotsFiltrados.size() == 1 ? "" : "s") + ")."
                );
            }

        } catch (Exception e) {
            tablaSlots.setItems(FXCollections.observableArrayList());
            mostrarEstadoGeneral(
                    "Error al consultar horarios: " + e.getMessage(),
                    "agendar-status-error"
            );
        }

        actualizarGuia();
    }

    private void actualizarGuia() {
        lblPasoActual.getStyleClass().setAll("agendar-step-indicator");

        if (pacienteSeleccionado == null) {
            lblPasoActual.setText("Paso 1: Seleccione el paciente");
        } else if (especialidadSeleccionada == null) {
            lblPasoActual.setText("Paso 2: Seleccione la especialidad");
        } else if (medicoSeleccionado == null) {
            lblPasoActual.setText("Paso 3: Seleccione el médico");
        } else if (dpFecha.getValue() == null) {
            lblPasoActual.setText("Paso 4: Seleccione la fecha");
        } else if (slotSeleccionado == null) {
            lblPasoActual.setText("Paso 5: Seleccione la hora");
        } else {
            lblPasoActual.setText("Paso 6: Confirme la cita");
        }
    }

    @FXML
    private void handleAgendar() {
        limpiarValidaciones();

        boolean valido = true;

        if (pacienteSeleccionado == null) {
            mostrarError(errPaciente, "Debe seleccionar un paciente.");
            marcarInputInvalido(cmbPacientes, true);
            valido = false;
        }

        if (especialidadSeleccionada == null || especialidadSeleccionada.isBlank()) {
            mostrarError(errEspecialidad, "Debe seleccionar una especialidad.");
            marcarInputInvalido(cmbEspecialidad, true);
            valido = false;
        }

        if (medicoSeleccionado == null) {
            mostrarError(errMedico, "Debe seleccionar un médico.");
            marcarInputInvalido(cmbMedicos, true);
            valido = false;
        }

        if (dpFecha.getValue() == null) {
            mostrarError(errFecha, "Debe seleccionar una fecha.");
            marcarInputInvalido(dpFecha, true);
            valido = false;
        }

        if (slotSeleccionado == null) {
            mostrarError(errSlot, "Debe seleccionar un horario.");
            valido = false;
        }

        String motivo = motivoAgendamientoNormalizado();
        if (motivo != null && motivo.length() > MAX_MOTIVO_CARACTERES) {
            mostrarError(errMotivo, "El motivo no puede superar " + MAX_MOTIVO_CARACTERES + " caracteres.");
            marcarTextAreaInvalida(true);
            valido = false;
        }

        if (!valido) {
            actualizarGuia();
            return;
        }

        try {
            CrearCitaAutonomaRequest request = new CrearCitaAutonomaRequest(
                    pacienteSeleccionado.getPersonaId(),
                    medicoSeleccionado.getPersonaId(),
                    agendadorId,
                    slotSeleccionado,
                    especialidadSeleccionada,
                    motivo
            );

            citaClient.crearCitaManual(request);

            mostrarEstadoGeneral(
                    "¡Cita agendada correctamente!",
                    "agendar-status-success"
            );
            btnAgendar.setDisable(true);

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(1.5)
            );
            pause.setOnFinished(e -> volverAlDashboard());
            pause.play();

        } catch (Exception e) {
            mostrarEstadoGeneral(
                    mensajeAmigable(e.getMessage()),
                    "agendar-status-error"
            );
            cargarSlots();
        }
    }

    @FXML
    private void handleVolver() {
        volverAlDashboard();
    }

    private void volverAlDashboard() {
        SceneManager.switchScene(
                "/view/dashboard/agendador-dashboard.fxml",
                btnVolver,
                "PIEDRAZUL - Dashboard Agendador"
        );
    }

    private void limpiarValidaciones() {
        ocultarError(errPaciente);
        ocultarError(errEspecialidad);
        ocultarError(errMedico);
        ocultarError(errFecha);
        ocultarError(errSlot);
        ocultarError(errMotivo);
        marcarTextAreaInvalida(false);
        marcarInputInvalido(cmbPacientes, false);
        marcarInputInvalido(cmbEspecialidad, false);
        marcarInputInvalido(cmbMedicos, false);
        marcarInputInvalido(dpFecha, false);
        limpiarEstadoGeneral();
    }

    private void mostrarError(Label label, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void ocultarError(Label label) {
        label.setText("");
        label.setVisible(false);
        label.setManaged(false);
    }

    private String motivoAgendamientoNormalizado() {
        if (txtMotivoAgendamiento == null) return null;
        String texto = txtMotivoAgendamiento.getText();
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    private void marcarTextAreaInvalida(boolean invalida) {
        if (invalida) {
            if (!txtMotivoAgendamiento.getStyleClass().contains("agendar-textarea-error")) {
                txtMotivoAgendamiento.getStyleClass().add("agendar-textarea-error");
            }
        } else {
            txtMotivoAgendamiento.getStyleClass().remove("agendar-textarea-error");
        }
    }

    private void marcarInputInvalido(Control control, boolean invalido) {
        if (invalido) {
            if (!control.getStyleClass().contains("agendar-input-error")) {
                control.getStyleClass().add("agendar-input-error");
            }
        } else {
            control.getStyleClass().remove("agendar-input-error");
        }
    }

    private void mostrarEstadoSlots(String mensaje, String estilo) {
        lblSlotsEstado.setText(mensaje);
        lblSlotsEstado.getStyleClass().setAll("agendar-status-banner", estilo);
        lblSlotsEstado.setVisible(true);
        lblSlotsEstado.setManaged(true);
    }

    private void ocultarEstadoSlots() {
        lblSlotsEstado.setText("");
        lblSlotsEstado.setVisible(false);
        lblSlotsEstado.setManaged(false);
    }

    private void mostrarEstadoGeneral(String mensaje, String estilo) {
        lblEstadoGeneral.setText(mensaje);
        lblEstadoGeneral.getStyleClass().setAll("agendar-status-banner", estilo);
        lblEstadoGeneral.setVisible(true);
        lblEstadoGeneral.setManaged(true);
    }

    private void limpiarEstadoGeneral() {
        lblEstadoGeneral.setText("");
        lblEstadoGeneral.setVisible(false);
        lblEstadoGeneral.setManaged(false);
    }

    private static String etiquetaEspecialidad(String codigo) {
        return EspecialidadLabels.etiqueta(codigo);
    }

    private static String mensajeAmigable(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return "No se pudo completar el agendamiento.";
        }
        if (mensaje.contains("ya tiene una cita agendada")) {
            return "El paciente o médico ya tiene una cita en ese horario. Elija otro.";
        }
        if (mensaje.contains("ya no está disponible")) {
            return "El horario ya no está disponible. Seleccione otro.";
        }
        if (mensaje.contains("no atiende la especialidad")) {
            return "El médico no atiende esa especialidad.";
        }
        return mensaje;
    }
}