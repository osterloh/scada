package com.osterloh.scada.application.port.out;

import com.osterloh.scada.domain.model.Machine;

public interface MachineStatePort {

    void saveCurrentStatus(Machine machine);
    Machine findCurrentStatus(String machineId);
}
