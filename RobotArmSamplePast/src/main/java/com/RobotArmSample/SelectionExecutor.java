package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

///Выбор исполнителя
@Component
public class SelectionExecutor implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String statusKuka = IotModules.RobotArmKukaGetStatus();
        if(statusKuka.equals("OK")) return;
        else throw  new BpmnError("unavailableError");

    }
}
