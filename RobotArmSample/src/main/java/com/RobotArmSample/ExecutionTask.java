package com.RobotArmSample;

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
