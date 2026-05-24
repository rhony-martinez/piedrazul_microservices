package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.CitaClient;
import com.piedrazul.frontend.client.MedicoClient;
import com.piedrazul.frontend.dto.request.CrearCitaAutonomaRequest;
import com.piedrazul.frontend.dto.response.MedicoResponse;
import com.piedrazul.frontend.session.SessionManager;
import com.piedrazul.frontend.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgendarCitaAutonomaController {

    @FXML private ComboBox<MedicoResponse> cmbMedicos;
    @FXML private DatePicker dpFecha;
    @FXML private TableView<LocalDateTime> tablaSlots;
    @FXML private TableColumn<LocalDateTime, String> colSlot;
    @FXML private Button btnAgendar;
    @FXML private Button btnVolver;

    private final MedicoClient medicoClient = new MedicoClient();
    private final CitaClient citaClient = new CitaClient();

    private MedicoResponse medicoSeleccionado;
    private LocalDateTime slotSeleccionado;
    private Long pacienteId;

    @FXML
    public void initialize() {
        // Configurar columna de la tabla de slots
        colSlot.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue();
            String formato = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new javafx.beans.property.SimpleStringProperty(formato);
        });

        // Seleccionar slot de la tabla
        tablaSlots.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            slotSeleccionado = newVal;
            btnAgendar.setDisable(newVal == null);
        });

        // Cargar médicos
        cargarMedicos();

        // Eventos
        cmbMedicos.valueProperty().addListener((obs, oldVal, newVal) -> {
            medicoSeleccionado = newVal;
            cargarSlots();
        });

        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> cargarSlots());

        if (SessionManager.isLoggedIn()) {
            pacienteId = SessionManager.getPersonaId();
            if (pacienteId == null) {
                mostrarAlerta(
                        "Cuenta sin perfil vinculado",
                        "Tu usuario no tiene una persona asociada (claim persona_id ausente en el token). "
                                + "Pidele al administrador que vincule tu cuenta antes de agendar.",
                        Alert.AlertType.WARNING
                );
            }
        }
    }

    private void cargarMedicos() {
        try {
            List<MedicoResponse> medicos = medicoClient.obtenerMedicos();
            cmbMedicos.setItems(FXCollections.observableArrayList(medicos));
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los médicos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarSlots() {
        if (medicoSeleccionado == null || dpFecha.getValue() == null) {
            tablaSlots.setItems(FXCollections.observableArrayList());
            return;
        }

        try {
            List<LocalDateTime> slots = citaClient.obtenerSlotsDisponibles(medicoSeleccionado.getPersonaId());

            // Filtrar slots por la fecha seleccionada
            LocalDate fechaSeleccionada = dpFecha.getValue();
            List<LocalDateTime> slotsFiltrados = slots.stream()
                    .filter(slot -> slot.toLocalDate().equals(fechaSeleccionada))
                    .toList();

            tablaSlots.setItems(FXCollections.observableArrayList(slotsFiltrados));

            if (slotsFiltrados.isEmpty()) {
                mostrarAlerta("Información", "No hay horarios disponibles para la fecha seleccionada", Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los horarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAgendar() {
        if (slotSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un horario", Alert.AlertType.ERROR);
            return;
        }

        if (pacienteId == null) {
            mostrarAlerta("Error", "No se pudo identificar al paciente", Alert.AlertType.ERROR);
            return;
        }

        try {
            CrearCitaAutonomaRequest request = new CrearCitaAutonomaRequest(
                    pacienteId,
                    medicoSeleccionado.getPersonaId(),
                    pacienteId,
                    slotSeleccionado
            );

            citaClient.crearCitaAutonoma(request);

            mostrarAlerta("Éxito", "Cita agendada correctamente", Alert.AlertType.INFORMATION);
            volverAlDashboard();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agendar la cita: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVolver() {
        SceneManager.switchScene(
                "/view/dashboard/paciente-dashboard.fxml",
                btnVolver,
                "PIEDRAZUL - Menú principal"
        );
    }

    private void volverAlDashboard() {

    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}