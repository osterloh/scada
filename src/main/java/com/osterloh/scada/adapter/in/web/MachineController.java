package com.osterloh.scada.adapter.in.web;

import com.osterloh.scada.application.port.out.MachineStatePort;
import com.osterloh.scada.domain.model.Machine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MachineController {

    private final MachineStatePort machineStatePort;

    public MachineController(MachineStatePort machineStatePort) {
        this.machineStatePort = machineStatePort;
    }

    @GetMapping("/machine/{machineId}")
    public Machine getMachine(@PathVariable String machineId) {
        return machineStatePort.findCurrentStatus(machineId);
    }
}
