package com.osterloh.scada.domain.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Machine {

    private final String id;
    private MachineStatus status;
    private double temperature;
    private int setpointVelocity;
    private boolean alarmTemperature;
    private int goodParts;
    private int defectiveParts;
    private Instant lastUpdate;

    public Machine(String id) {
        this.id = id;
    }

    public void update(MachineStatus status, double temperature, int setpointVelocity, boolean alarmTemperature,
                   int goodParts, int defectiveParts, Instant lastUpdate) {
        this.status = status;
        this.temperature = temperature;
        this.setpointVelocity = setpointVelocity;
        this.alarmTemperature = alarmTemperature;
        this.goodParts = goodParts;
        this.defectiveParts = defectiveParts;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "status=" + status +
                ", temperature=" + temperature +
                ", setpointVelocity=" + setpointVelocity +
                ", alarmTemperature=" + alarmTemperature +
                ", goodParts=" + goodParts +
                ", defectiveParts=" + defectiveParts +
                '}';
    }
}
