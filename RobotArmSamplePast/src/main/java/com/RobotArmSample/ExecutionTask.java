package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

///Выполнение операции (нажать кнопку)
@Component
public class ExecutionTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {



    }
}
