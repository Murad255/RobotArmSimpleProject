package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SendEboutGood implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        IotModules.InWatchCleent("кнопка была нажата");

    }
}
