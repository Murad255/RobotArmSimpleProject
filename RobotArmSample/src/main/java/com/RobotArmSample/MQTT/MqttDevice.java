package com.RobotArmSample.MQTT;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class MqttDevice implements MqttCallback {

	Stack<Message> messages = new Stack<Message>();
	Map< String, Message> robotArmMessages = new HashMap< String, Message>();
	Map< String, Message> machineToolMessages = new HashMap< String, Message>();
	Map< String, Message> modulesMessages = new HashMap< String, Message>();

	private RuntimeService runtimeService;
	ProcessInstance pi;
	public Message pastMessage;

	public void pastMessageClear() throws Exception {
		pastMessage = new Message("","");
	}

	public void StackMessageClear(){messages.clear(); }

	public Stack<Message> getStackMessage(){
		return  messages;
	}

	public  Message findMessage(String Name){
		int len = messages.size();
		while (len>0){
			Message mes= messages.get(len-1);
			if(mes.getName().equals(Name)){
				messages.remove(mes);
				return mes;
			}
			len--;
		}
		return null;
	}


	//@inject
	//private  ProcessEngine processEngine;

	//public ProcessEngine getEngineProgrammatically(){
	//	return BpmPlatform.getDefaultProcessEngine();
	//}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(topic+"\t"+message);
		Message msg= new Message(message.toString(),topic);
		messages.push(msg);
		pastMessage=msg;
		try {
			if (msg.getToken().equals("userM/watcher/out")) {
				runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

				if (msg.getMessage().equals("start1")) {
					runtimeService.signalEventReceived("Signal_Press");
				}
				else if (msg.getMessage().equals("start3")) {
					runtimeService.signalEventReceived("Signal_2");
				}
				else if (msg.getMessage().equals("start2")) {
					ProcessInstance startedProcessInstance = runtimeService
							.createMessageCorrelation("MesEvent")
							//.processInstanceBusinessKey("businessKey")
							.setVariable("name1", "value1")
							.correlateStartMessage();
					runtimeService.startProcessInstanceByMessage("SignalClient1","Event_0t4q3d1");

					//MessageCorrelationResult result = runtimeService.createMessageCorrelation("Message_1")
							//.setVariable("payment_type", "creditCard")
						//	.correlateWithResult();
		//				runtimeService.messageEventReceived("Message_1mclyhy","MessageEventDefinition_04dbltg");
//					ProcessInstance startedProcessInstance = runtimeService
//							.createMessageCorrelation("SignalClient1")
//							//.processInstanceBusinessKey("businessKey")
//							.setVariable("name1", "value1")
//							.correlateStartMessage();
					//runtimeService.startProcessInstanceByMessage("SignalClient1","Event_0t4q3d1");
				}
			}
		}
		catch (Exception ex){

			System.out.println( ex.getMessage());
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	
}
