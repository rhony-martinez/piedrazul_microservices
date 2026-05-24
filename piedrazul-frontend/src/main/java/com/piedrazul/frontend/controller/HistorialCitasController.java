package com.piedrazul.frontend.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piedrazul.frontend.dto.response.CitaResponse;
import com.piedrazul.frontend.http.AuthenticatedHttpClient;
import com.piedrazul.frontend.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.lang.reflect.Type;
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

    // Reemplaza TODO tu método initialize() por este:

    @FXML
    public void initialize() {

        // Cargar CSS, maximizar y poner pantalla completa
        tablaCitas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                // Cargar dashboard.css
                String css = getClass()
                        .getResource("/view/css/dashboard.css")
                        .toExternalForm();

                if (!newScene.getStylesheets().contains(css)) {
                    newScene.getStylesheets().add(css);
                }

                // Cuando el Stage esté disponible
                newScene.windowProperty().addListener((o, oldWindow, newWindow) -> {
                    if (newWindow instanceof javafx.stage.Stage stage) {

                        // Maximizar ventana
                        stage.setMaximized(true);

                        // Opcional: ocupar toda la pantalla real
                        // stage.setFullScreen(true);

                        // Forzar tamaño de pantalla
                        javafx.geometry.Rectangle2D screenBounds =
                                javafx.stage.Screen.getPrimary().getVisualBounds();

                        stage.setX(screenBounds.getMinX());
                        stage.setY(screenBounds.getMinY());
                        stage.setWidth(screenBounds.getWidth());
                        stage.setHeight(screenBounds.getHeight());
                    }
                });
            }
        });

        // Ajustar columnas automáticamente
        tablaCitas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        // Cargar médicos
        cargarMedicos();

        // Configuración de columnas
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

            String url = AuthenticatedHttpClient.baseUrl() + "/api/citas/historial";

            if (medicoSeleccionado != null) {
                url += "?medicoId=" + medicoSeleccionado.getPersonaId();
            }

            if (!fecha.isEmpty()) {
                url += (url.contains("?") ? "&" : "?") + "fecha=" + fecha;
            }

            AuthenticatedHttpClient.Response resp = AuthenticatedHttpClient.get(url);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<CitaResponse>>() {}.getType();
            List<CitaResponse> citas = gson.fromJson(resp.getBody(), listType);

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