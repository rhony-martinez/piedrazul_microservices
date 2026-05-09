package com.piedrazul.frontend.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piedrazul.frontend.dto.response.CitaResponse;
import com.piedrazul.frontend.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

import com.piedrazul.frontend.client.MedicoClient;
import com.piedrazul.frontend.dto.response.MedicoResponse;

public class HistorialCitasController {

    @FXML
    private ComboBox<MedicoResponse> cmbMedicos;
    @FXML
    private DatePicker dpFecha;
    @FXML private Button btnVolver;

    // CAMBIO IMPORTANTE
    @FXML private TableView<CitaResponse> tablaCitas;

    @FXML private TableColumn<CitaResponse, String> colId;
    @FXML private TableColumn<CitaResponse, String> colFecha;
    @FXML private TableColumn<CitaResponse, String> colPaciente;
    @FXML private TableColumn<CitaResponse, String> colMedico;
    @FXML private TableColumn<CitaResponse, String> colTipo;
    @FXML private TableColumn<CitaResponse, String> colEstado;
    @FXML private TableColumn<CitaResponse, String> colMotivo;

    @FXML
    public void initialize() {

        cargarMedicos();

        // AHORA LAS COLUMNAS USAN DATOS REALES
        colId.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId())
        );

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaHora())
        );

        colPaciente.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPacienteNombre() != null
                                ? data.getValue().getPacienteNombre()
                                : String.valueOf(data.getValue().getPacienteId())
                )
        );

        colMedico.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getMedicoNombre() != null
                                ? data.getValue().getMedicoNombre()
                                : String.valueOf(data.getValue().getMedicoId())
                )
        );

        // (si no tienes tipo en backend, lo dejamos fijo por ahora)
        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty("General")
        );

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado())
        );

        colMotivo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getMotivoCancelacion() != null
                                ? data.getValue().getMotivoCancelacion()
                                : "-"
                )
        );
    }

    private void cargarMedicos() {

        try {

            MedicoClient medicoClient = new MedicoClient();

            List<MedicoResponse> medicos = medicoClient.obtenerMedicos();

            cmbMedicos.setItems(
                    FXCollections.observableArrayList(medicos)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarCitas() {

        try {

            MedicoResponse medicoSeleccionado = cmbMedicos.getValue();
            String fecha = "";

            if (dpFecha.getValue() != null) {
                fecha = dpFecha.getValue().toString();
            }

            String url = "http://localhost:8083/api/citas/historial";

            if (medicoSeleccionado != null) {
                url += "?medicoId=" + medicoSeleccionado.getPersonaId();
            }

            if (!fecha.isEmpty()) {
                url += (url.contains("?") ? "&" : "?") + "fecha=" + fecha;
            }

            System.out.println("URL: " + url);

            HttpURLConnection conn = (HttpURLConnection) new java.net.URI(url).toURL().openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            System.out.println("RESPONSE: " + response);

            // CONVERTIR JSON → OBJETOS
            Gson gson = new Gson();
            Type listType = new TypeToken<List<CitaResponse>>() {}.getType();

            List<CitaResponse> citas = gson.fromJson(response.toString(), listType);

            // CARGAR TABLA
            tablaCitas.setItems(FXCollections.observableArrayList(citas));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void volver() {
        SceneManager.switchScene(
                "/view/dashboard/agendador-dashboard.fxml",
                btnVolver,
                "Dashboard"
        );
    }
}