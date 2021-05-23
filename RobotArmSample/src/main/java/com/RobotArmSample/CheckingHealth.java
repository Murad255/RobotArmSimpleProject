package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import com.RobotArmSample.MQTT.ModuleType;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

///Проверка оборудования (отклик)
@Component
public class CheckingHealth implements JavaDelegate {

    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println( "Проверка оборудования" );
        IotModules.Begin();


        boolean statusButton = IotModules.waitStatusСonfirmed(IotModules.button1Name,1700);
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
        runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

        //MessageCorrelationResult result = runtimeService.createMessageCorrelation("Message_1")
        //.setVariable("payment_type", "creditCard")
        //	.correlateWithResult();
        //				runtimeService.messageEventReceived("Message_1mclyhy","MessageEventDefinition_04dbltg");
					ProcessInstance startedProcessInstance = runtimeService
							.createMessageCorrelation("mesEvent")
							//.processInstanceBusinessKey("businessKey")
							.setVariable("name1", "value1")
							.correlateStartMessage();
        runtimeService.startProcessInstanceByMessage("SignalClient1","Event_0t4q3d1");
    }
}
