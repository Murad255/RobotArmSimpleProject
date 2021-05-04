package com.RobotArmSample.MQTT;

public class Message {

    String token;
    String message;
    String moduleName;

    String name;
    ModuleType moduleType;
    int UID;
    int status;
    String data;

    public Message(String message,String token) throws Exception {
        this.message = message;
        this.token = token;
        if(isModule()){
            name = IotModules.findText(message,"Name");
            moduleType =  ModuleType.valueOf(IotModules.findText(message,"ModuleType"));
            UID = Integer.parseInt(IotModules.findText(message,"UID"));
            data = IotModules.findText(message,"Data");
        }
    }

    public  boolean isModule(){
        try {
            if (message.length() > 0) {
                int find1 = message.indexOf("<Module>");
                int find2 = message.indexOf("</Module>");
                if(find2-find1>0) return  true;
                else  return false;
            }
            else return  false;
        }
        catch (Exception ex){
            return  false;
        }
    }

    public String getName() {
        return name;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public int getUID() {
        return UID;
    }

    public int getStatus() {
        return status;
    }

    public String getData() {
        return data;
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
