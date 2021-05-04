package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

///Проверка результата
@Component
public class ResultCheck implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        boolean checkButton = IotModules.ButtonGetData1Bool();
        IotModules.WatchingClientSetStatus(" ");
        Thread.sleep(500);
        IotModules.WatchingClientSetStatus("click");


        delegateExecution.setVariable("checkButton", checkButton);
    }
}
