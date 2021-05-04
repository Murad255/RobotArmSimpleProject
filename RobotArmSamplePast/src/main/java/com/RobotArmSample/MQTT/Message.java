package com.RobotArmSample.MQTT;

class Message {

    String token;
    String message;
    //String deviceName;

    public Message(String message,String token){
        this.message = message;
        this.token = token;
    }

    public String getMessage(){
        return message;
    }

    public String getToken(){
        return token;
    }

    public String getDeviceName(){
        String[] subStr = token.split("/"); // Разделения строки str с помощью метода split()
        return subStr[subStr.length-1];
    }

}
