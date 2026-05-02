package com.piedrazul.citas.domain.model;

import com.piedrazul.citas.domain.valueobjects.MedicoId;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class DisponibilidadSnapshot {
    private final MedicoId medicoId;
    private final Map<DayOfWeek, List<TimeRange>> horariosSemanales;
    private final Set<LocalDateTime> bloqueosEspecificos;
    private final LocalDateTime actualizadoEn;
    private Integer intervaloMinutos;

    public DisponibilidadSnapshot(MedicoId medicoId, Integer intervaloMinutos) {
        this.medicoId = medicoId;
        this.intervaloMinutos = intervaloMinutos;
        this.horariosSemanales = new HashMap<>();
        this.bloqueosEspecificos = new HashSet<>();
        this.actualizadoEn = LocalDateTime.now();
    }

    public Integer getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(Integer intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    public void agregarHorarioSemanal(DayOfWeek dia, TimeRange rango) {
        horariosSemanales.computeIfAbsent(dia, k -> new ArrayList<>()).add(rango);
    }

    public void agregarBloqueo(LocalDateTime fechaHora) {
        bloqueosEspecificos.add(fechaHora);
    }

    public void removerBloqueo(LocalDateTime fechaHora) {
        bloqueosEspecificos.remove(fechaHora);
    }

    public boolean estaDisponible(MedicoId medicoId, LocalDateTime fechaHora) {
        if (!this.medicoId.equals(medicoId)) return false;

        DayOfWeek dia = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();

        // Verificar si está bloqueado específicamente
        if (bloqueosEspecificos.contains(fechaHora)) {
            return false;
        }

        // Verificar si está en horario semanal
        List<TimeRange> rangos = horariosSemanales.get(dia);
        if (rangos == null || rangos.isEmpty()) {
            return false;
        }

        return rangos.stream().anyMatch(rango -> rango.contiene(hora));
    }

    public void reemplazarHorariosDelDia(DayOfWeek dia, List<TimeRange> nuevosRangos) {
        horariosSemanales.put(dia, new ArrayList<>(nuevosRangos));
    }

    public void agregarHorarioSemanalSinDuplicados(DayOfWeek dia, TimeRange nuevo) {
        List<TimeRange> existentes = horariosSemanales
                .computeIfAbsent(dia, k -> new ArrayList<>());

        boolean yaExiste = existentes.stream()
                .anyMatch(r -> r.getStart().equals(nuevo.getStart())
                        && r.getEnd().equals(nuevo.getEnd()));

        if (!yaExiste) {
            existentes.add(nuevo);
        }
    }

    // Getters
    public MedicoId getMedicoId() { return medicoId; }
    public Map<DayOfWeek, List<TimeRange>> getHorariosSemanales() { return horariosSemanales; }
    public Set<LocalDateTime> getBloqueosEspecificos() { return bloqueosEspecificos; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}