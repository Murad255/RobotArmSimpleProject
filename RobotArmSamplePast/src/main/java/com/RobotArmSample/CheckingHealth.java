package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

///Проверка оборудования (отклик)
@Component
public class CheckingHealth implements JavaDelegate {


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println( "Hello World!" );
        IotModules.Begin();
        IotModules.ButtonBegin();
        IotModules.RobotArmKukaBegin();

        IotModules.ButtonSetModule1("1");
        Thread.sleep(500);
        IotModules.ButtonSetModule1("0");
        Thread.sleep(500);
        IotModules.ButtonSetModule1("1");
        Thread.sleep(500);
        IotModules.ButtonSetModule1("0");

        IotModules.ButtonSetRequest("changeEvent");
        Thread.sleep(50);
        String statusButton = IotModules.ButtonGetStatus();
        if(statusButton.length()>0){
            IotModules.WatchingClientSetStatus("Датчик подключен");
        }
        else{
            IotModules.WatchingClientSetStatus("Датчик не подключен");
            throw  new BpmnError("noConnectionError");
        }

    }
}
