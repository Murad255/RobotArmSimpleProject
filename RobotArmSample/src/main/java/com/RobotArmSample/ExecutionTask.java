package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import com.RobotArmSample.MQTT.ModuleType;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

///Выполнение операции (нажать кнопку)
@Component
public class ExecutionTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println( "Выполнение операции (нажать кнопку)!" );
        String nameExecutor =  (String) delegateExecution.getVariable("nameExecutor");
        IotModules.InDevicesData(ModuleType.robotArm,nameExecutor,"<Task><Program>163</Program></Task>");
        IotModules.waitingStatusСonfirmed(nameExecutor);
        Thread.sleep(5);
        IotModules.InDevicesData(ModuleType.robotArm,"noArm","<Task><Program>163</Program></Task>");

    }
}
