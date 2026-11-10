package com.osterloh.scada.adapter.in.opcua;

import com.osterloh.scada.application.port.out.MachineStatePort;
import com.osterloh.scada.domain.model.Machine;
import com.osterloh.scada.domain.model.MachineStatus;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class OpcUaPollingService {

    private final OpcUaClient client;
    private final OpcUaNodeResolver nodeResolver;
    private final MachineStatePort machineStatePort;

    @Value("${opcua.namespace-index}")
    private int namespaceIndex;

    private static final List<String> MACHINES = List.of("MaquinaSolda1", "MaquinaSolda2");

    public OpcUaPollingService(OpcUaClient client, OpcUaNodeResolver nodeResolver, MachineStatePort machineStatePort) {
        this.client = client;
        this.nodeResolver = nodeResolver;
        this.machineStatePort = machineStatePort;
    }

    @Scheduled(fixedRate = 1000)
    public void readMachineData() {
        for (String machineName : MACHINES) {
            try {
                readDataOneMachine(machineName);
            } catch (Exception e) {
                System.err.println("Erro ao ler dados de " + machineName + ": " + e.getMessage());
            }
        }
    }

    private void readDataOneMachine(String machineName) throws Exception {
        int statusCode = (int) readValue(machineName, "Status");
        double temperature = (double) readValue(machineName, "Temperatura");
        double setpoint = (double) readValue(machineName, "SetpointVelocidade");
        boolean alarm = (boolean) readValue(machineName, "AlarmeTemperatura");
        int goodParts = (int) readValue(machineName, "PecasBoas");
        int defectiveParts = (int) readValue(machineName, "PecasDefeito");

        Machine machine = new Machine(machineName);
        machine.update(
                MachineStatus.fromCode(statusCode),
                temperature,
                (int) setpoint,
                alarm,
                goodParts,
                defectiveParts,
                Instant.now()
        );

        log.info("Dados lidos: {}", machine.toString());
        machineStatePort.saveCurrentStatus(machine);
    }

    private Object readValue(String machineName, String browseName) throws Exception {
        NodeId nodeId = nodeResolver.resolve(client, machineName, browseName, namespaceIndex);
        DataValue value = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
        return value.getValue().getValue();
    }
}
