package com.osterloh.scada.domain.model;

public enum MachineStatus {

    STOPPED(0),
    RUNNING(1),
    MAINTENANCE(2),
    FAILURE(3);

    private final int code;

    MachineStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MachineStatus fromCode(int code) {
        for (MachineStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}
