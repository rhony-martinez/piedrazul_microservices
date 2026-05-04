package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.DisponibilidadClient;
import com.piedrazul.frontend.client.MedicoClient;
import com.piedrazul.frontend.dto.DisponibilidadRow;
import com.piedrazul.frontend.dto.response.MedicoResponse;
import com.piedrazul.frontend.util.SceneManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ConfiguracionDisponibilidadController {

    @FXML private ComboBox<MedicoResponse> cmbMedicos;
    @FXML private ComboBox<String> cmbDiaSemana;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;
    @FXML private TextField txtIntervalo;

    @FXML private TableView<DisponibilidadRow> tablaDisponibilidad;
    @FXML private TableColumn<DisponibilidadRow, String> colMedico;
    @FXML private TableColumn<DisponibilidadRow, String> colDia;
    @FXML private TableColumn<DisponibilidadRow, String> colHoraInicio;
    @FXML private TableColumn<DisponibilidadRow, String> colHoraFin;
    @FXML private TableColumn<DisponibilidadRow, Integer> colIntervalo;

    private final ObservableList<DisponibilidadRow> data = FXCollections.observableArrayList();

    private final MedicoClient medicoClient = new MedicoClient();
    private final DisponibilidadClient disponibilidadClient = new DisponibilidadClient();

    @FXML
    public void initialize() {

        // Columnas
        colMedico.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMedico()));
        colDia.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDia()));
        colHoraInicio.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getHoraInicio()));
        colHoraFin.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getHoraFin()));
        colIntervalo.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getIntervalo()));

        tablaDisponibilidad.setItems(data);

        // Días
        cmbDiaSemana.getItems().addAll(
                "LUNES", "MARTES", "MIERCOLES",
                "JUEVES", "VIERNES", "SABADO", "DOMINGO"
        );

        // Médicos
        cmbMedicos.getItems().addAll(medicoClient.obtenerMedicos());
    }

    private void cargarMedicos() {
        cmbMedicos.getItems().addAll(medicoClient.obtenerMedicos());
    }

    @FXML
    private void handleAgregar() {

        try {
            if (cmbMedicos.getValue() == null) {
                mostrarAlerta("Error", "Seleccione un médico", Alert.AlertType.ERROR);
                return;
            }

            Long medicoId = cmbMedicos.getValue().getPersonaId();
            String dia = cmbDiaSemana.getValue();
            String horaInicio = txtHoraInicio.getText();
            String horaFin = txtHoraFin.getText();
            Integer intervalo = Integer.parseInt(txtIntervalo.getText());

            disponibilidadClient.crearDisponibilidad(
                    medicoId, dia, horaInicio, horaFin, intervalo
            );

            //  Agregar a tabla
            data.add(new DisponibilidadRow(
                    cmbMedicos.getValue().toString(),
                    dia,
                    horaInicio,
                    horaFin,
                    intervalo
            ));

            //  Alerta éxito
            mostrarAlerta("Éxito", "Disponibilidad registrada correctamente", Alert.AlertType.INFORMATION);

            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVolver() {
        SceneManager.showDashboard(
                "/view/dashboard/administrador-dashboard.fxml",
                cmbMedicos,
                "Dashboard Administrador"
        );
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        cmbDiaSemana.setValue(null);
        txtHoraInicio.clear();
        txtHoraFin.clear();
        txtIntervalo.clear();
    }
}
