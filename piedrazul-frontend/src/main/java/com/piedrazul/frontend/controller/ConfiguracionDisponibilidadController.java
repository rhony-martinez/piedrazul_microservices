package com.piedrazul.frontend.controller;

import com.piedrazul.frontend.client.DisponibilidadClient;
import com.piedrazul.frontend.client.MedicoClient;
import com.piedrazul.frontend.dto.response.MedicoResponse;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ConfiguracionDisponibilidadController {

    @FXML
    private ComboBox<MedicoResponse> cmbMedicos;
    @FXML private ComboBox<String> cmbDiaSemana;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;
    @FXML
    private TextField txtIntervalo;

    private final MedicoClient medicoClient = new MedicoClient();
    private final DisponibilidadClient disponibilidadClient = new DisponibilidadClient();

    @FXML
    public void initialize() {
        cmbDiaSemana.getItems().addAll(
                "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"
        );

        cargarMedicos();
    }

    private void cargarMedicos() {
        cmbMedicos.getItems().addAll(medicoClient.obtenerMedicos());
    }

    @FXML
    private void handleAgregar() {

        MedicoResponse medico = cmbMedicos.getValue();

        if (medico == null) {
            throw new RuntimeException("Debe seleccionar un médico");
        }

        Long medicoId = medico.getPersonaId();
        String dia = cmbDiaSemana.getValue();

        String horaInicio = txtHoraInicio.getText();
        String horaFin = txtHoraFin.getText();
        Integer intervalo = Integer.parseInt(txtIntervalo.getText());

        disponibilidadClient.crearDisponibilidad(
                medicoId, dia, horaInicio, horaFin, intervalo
        );
    }
}
