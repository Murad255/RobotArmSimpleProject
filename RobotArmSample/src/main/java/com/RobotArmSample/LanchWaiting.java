package com.RobotArmSample;

import com.RobotArmSample.MQTT.IotModules;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.MessageCorrelationResultWithVariables;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class LanchWaiting implements JavaDelegate {

    //@inject
    private ProcessEngine processEngine;
    private RuntimeService runtimeService;
    public ProcessEngine getEngineProgrammatically(){
    	return BpmPlatform.getDefaultProcessEngine();
    }
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println( "Ожидание команды запуска!" );
        IotModules.WatсherAvailable();

        runtimeService= ProcessEngines.getDefaultProcessEngine().getRuntimeService();
        runtimeService.signalEventReceived("SignalClient");//,"Event_0lmll94");

    }

}
