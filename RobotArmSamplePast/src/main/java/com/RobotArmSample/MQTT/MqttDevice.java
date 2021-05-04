package com.RobotArmSample.MQTT;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Stack;

public class MqttDevice implements MqttCallback {

	Stack<Message> stackMessage = new Stack<Message>();
	public Message pastMessage;

	public void pastMessageClear(){
		pastMessage = new Message("","");
	}
	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(topic+"\t"+message);
		Message msg= new Message(message.toString(),topic);
		stackMessage.push(msg);
		pastMessage=msg;
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	
}
