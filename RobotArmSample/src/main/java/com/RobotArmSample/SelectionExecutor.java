package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import com.RobotArmSample.MQTT.Message;
import com.RobotArmSample.MQTT.ModuleType;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Stack;

///Выбор исполнителя
@Component
public class SelectionExecutor implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println( "Выбор исполнителя" );
        try {

            String nameExecutor = IotModules.SelectRobotArm();
            IotModules.InWatchCleent("выбран манипулятор\t"+ nameExecutor);
            delegateExecution.setVariable("nameExecutor", nameExecutor);
        }
        catch ( Exception ex){
            IotModules.InWatchCleent("Нет свободных манипуляторов");
            throw  new BpmnError("unavailableError");
        }
    }
}
