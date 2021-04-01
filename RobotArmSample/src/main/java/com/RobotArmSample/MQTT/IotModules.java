package com.RobotArmSample.MQTT;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

public  class IotModules {
    static MqttAsyncClient myClient;
    static MqttDevice myCallback;
    static String serverUrl = "tcp://mqtt.eclipseprojects.io:1883";
////button////
    static String button_setModule1 = "userM/line1/sensor1/set/module1";//led
    static String button_setRequest = "userM/line1/sensor1/set/request";
    static String button_setStatus = "userM/line1/sensor1/set/status";

    static String button_getStatus = "userM/line1/sensor1/get/status";
    static String button_getData1 = "userM/line1/sensor1/get/data1";//button

    ////robotArmKuka///
    static String robotArmKuka_setModule1 = "userM/line1/robotArmKuka/set/module1";
    static String robotArmKuka_setRequest = "userM/line1/robotArmKuka/set/request";
    static String robotArmKuka_setStatus = "userM/line1/robotArmKuka/set/status";

    static String robotArmKuka_getStatus = "userM/line1/robotArmKuka/get/status";
    static String robotArmKuka_getData1 = "userM/line1/robotArmKuka/get/data1";

    ////watchingClient////
    static String watchingClient_setModule1 = "userM/line1/watchingClient/set/module1";
    static String watchingClient_setRequest = "userM/line1/watchingClient/set/request";
    static String watchingClient_setStatus = "userM/line1/watchingClient/set/status";


    static public void Begin() throws Exception{
        myClient = new MqttAsyncClient(serverUrl, UUID.randomUUID().toString());
        myCallback = new MqttDevice();
        myClient.setCallback(myCallback);
        IMqttToken token = myClient.connect();
        token.waitForCompletion();
    }
    ////////button////////
    static public void ButtonBegin() throws Exception{
        myClient.subscribe(button_getStatus, 0);
        myClient.subscribe(button_getData1, 0);
    }

    static public  void ButtonSetModule1(String message)throws Exception {
        myClient.publish(button_setModule1,new MqttMessage(message.getBytes()) );
    }

    static public  void ButtonSetRequest(String message)throws Exception {
        myClient.publish(button_setRequest,new MqttMessage(message.getBytes()) );
    }

    public static void ButtonSetStatus(String message)throws Exception {
        myClient.publish(button_setStatus,new MqttMessage(message.getBytes()) );
    }

    public static String ButtonGetStatus()throws Exception {
        for(int i = 0; i < 10; i++){
            myCallback.pastMessageClear();
            myClient.publish(button_setStatus,new MqttMessage(("OK").getBytes()));
            Thread.sleep(10);
            if(myCallback.pastMessage.getToken().equals(button_getStatus)){
                return myCallback.pastMessage.getMessage();
            }
        }
        return  "";
    }

    public static String ButtonGetData1()throws Exception {
        for(int i = 0; i < 10; i++){
            myCallback.pastMessageClear();
            myClient.publish(button_setRequest,new MqttMessage(("data1").getBytes()));
            Thread.sleep(10);
            if(myCallback.pastMessage.getToken().equals(button_getData1)){
                return myCallback.pastMessage.getMessage();
            }
        }
        return  "";
    }

    public static boolean ButtonGetData1Bool()throws Exception {
        for(int i = 0; i < 10; i++){
            myCallback.pastMessageClear();
            myClient.publish(button_setRequest,new MqttMessage(("data1").getBytes()));
            Thread.sleep(10);
            if(myCallback.pastMessage.getToken().equals(button_getData1)){
                 if( myCallback.pastMessage.getMessage().equals("1"))return  true;
            }
        }
        return  false;
    }

////////robotArmKuka////////

    static public void RobotArmKukaBegin() throws Exception{
        myClient.subscribe(robotArmKuka_getStatus, 0);
        myClient.subscribe(robotArmKuka_getData1, 0);
    }

    static public  void RobotArmKukaSetModule1(String message)throws Exception {
        myClient.publish(robotArmKuka_setModule1,new MqttMessage(message.getBytes()) );
    }

    static public  void RobotArmKukaSetRequest(String message)throws Exception {
        myClient.publish(robotArmKuka_setRequest,new MqttMessage(message.getBytes()) );
    }

    public static void RobotArmKukaSetStatus(String message)throws Exception {
        myClient.publish(robotArmKuka_setStatus,new MqttMessage(message.getBytes()) );
    }

    public static String RobotArmKukaGetStatus()throws Exception {
        for(int i = 0; i < 10; i++){
            myCallback.pastMessageClear();
            myClient.publish(robotArmKuka_setStatus,new MqttMessage(("OK").getBytes()));
            Thread.sleep(10);
            if(myCallback.pastMessage.getToken().equals(robotArmKuka_getStatus)){
                return myCallback.pastMessage.getMessage();
            }
        }
        return  "";
    }

    public static String RobotArmKukaGetData1()throws Exception {
        for(int i = 0; i < 10; i++){
            myCallback.pastMessageClear();
            myClient.publish(robotArmKuka_setRequest,new MqttMessage(("data1").getBytes()));
            Thread.sleep(10);
            if(myCallback.pastMessage.getToken().equals(robotArmKuka_getData1)){
                return myCallback.pastMessage.getMessage();
            }
        }
        return  "";
    }

    //////watchingClient//////

    static public  void WatchingClientSetModule1(String message)throws Exception {
        myClient.publish(watchingClient_setModule1,new MqttMessage(message.getBytes()) );
    }

    static public  void WatchingClientSetRequest(String message)throws Exception {
        myClient.publish(watchingClient_setRequest,new MqttMessage(message.getBytes()) );
    }

    public static void WatchingClientSetStatus(String message)throws Exception {
        myClient.publish(watchingClient_setStatus,new MqttMessage(message.getBytes()) );
    }


}


