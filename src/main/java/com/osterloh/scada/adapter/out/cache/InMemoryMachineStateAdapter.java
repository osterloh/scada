package com.osterloh.scada.adapter.out.cache;

import com.osterloh.scada.application.port.out.MachineStatePort;
import com.osterloh.scada.domain.model.Machine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryMachineStateAdapter implements MachineStatePort {

    private final Map<String, Machine> currentStatus = new ConcurrentHashMap<>();

    @Override
    public void saveCurrentStatus(Machine machine) {
        currentStatus.put(machine.getId(), machine);
    }

    @Override
    public Machine findCurrentStatus(String machineId) {
        return currentStatus.get(machineId);
    }
}
