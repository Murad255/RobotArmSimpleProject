package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import com.RobotArmSample.MQTT.ModuleType;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

///Проверка оборудования (отклик)
@Component
public class CheckingHealth implements JavaDelegate {


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println( "Проверка оборудования" );
        IotModules.Begin();


        boolean statusButton = IotModules.waitStatusСonfirmed(IotModules.button1Name,700);
        if(statusButton==false) throw  new BpmnError("noConnectionError");
        IotModules.InDevicesData(ModuleType.modules,IotModules.button1Name,"<WorkMode>changeUpEvent</WorkMode>");

        IotModules.InDevicesData(IotModules.button1Name,"<Led>1</Led>");
        Thread.sleep(250);
        IotModules.InDevicesData(IotModules.button1Name,"<Led>0</Led>");
        Thread.sleep(250);
        IotModules.InDevicesData(IotModules.button1Name,"<Led>1</Led>");
        Thread.sleep(250);
        IotModules.InDevicesData(IotModules.button1Name,"<Led>0</Led>");
        Thread.sleep(250);
    }
}
