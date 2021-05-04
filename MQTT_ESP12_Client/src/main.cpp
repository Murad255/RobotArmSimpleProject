#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <AsyncMqttClient.h>
//#include <OneWire.h>
//#include <DallasTemperature.h>
const int butPin = 16;

//const char *ssid = "Keenetic-6756"; // Имя  точки доступа
//const char *pass = "03403blo";		// Пароль от точки доступа

const char *ssid = "Robotavr";		 // Имя  точки доступа
const char *pass = "Qaz1234Wsx5678"; // Пароль от точки доступа

const char *mqtt_server = "mqtt.eclipseprojects.io"; // Имя сервера MQTT
const int mqtt_port = 1883;							 // Порт для подключения к серверу MQTT
const char *mqtt_user = "";							 // Логи от сервер
const char *mqtt_pass = "";							 // Пароль от сервера
const char *deviceName = "button1";
const char *moduleType = "modules";
#define BUFFER_SIZE 100

bool LedState = false;
bool pinState = false;

WiFiClient wclient;
PubSubClient client(wclient, mqtt_server, mqtt_port);
void PrintStatus();
String inDevices = "userM/devices/in";
String outDevices = "userM/devices/out";

enum EventRequest
{
	byRequest,
	change,
	changeUp,
	continueTransmin
};

EventRequest receiveEvent = change;

// Функция получения данных от сервера
void callback(const MQTT::Publish &pub);
void PrintButtonStatus(bool pinState);
void TempSend();
String findText(String str, String findContext);
void PrintMessage(String str, int status = 0);

void setup()
{
	pinMode(LED_BUILTIN, 1);

	Serial.begin(9600);
	delay(10);
	Serial.println();
	Serial.println();
	pinMode(butPin, 0);
}

void loop()
{
	// подключаемся к wi-fi
	if (WiFi.status() != WL_CONNECTED)
	{
		Serial.print("Connecting to ");
		Serial.print(ssid);
		Serial.println("...");
		WiFi.begin(ssid, pass);

		if (WiFi.waitForConnectResult() != WL_CONNECTED)
			return;
		Serial.println("WiFi connected");
	}

	// подключаемся к MQTT серверу
	if (WiFi.status() == WL_CONNECTED)
	{
		if (!client.connected())
		{
			Serial.println("Connecting to MQTT server");
			if (client.connect(MQTT::Connect("arduinoClient2")
								   .set_auth(mqtt_user, mqtt_pass)))
			{
				Serial.println("Connected to MQTT server");
				client.set_callback(callback);
				client.subscribe(inDevices);
				digitalWrite(LED_BUILTIN, 1);
				delay(50);
				digitalWrite(LED_BUILTIN, 0);
				delay(100);
				digitalWrite(LED_BUILTIN, 1);
				delay(50);
				digitalWrite(LED_BUILTIN, 0);
			}
			else
			{
				Serial.println("Could not connect to MQTT server");
			}
		}

		if (client.connected())
		{
			client.loop();
			TempSend();
		}
	}
}

// Функция отправки показаний
void TempSend()
{
	static bool pastPinState = false;
	pinState = !digitalRead(butPin);
	if (receiveEvent == continueTransmin)
	{
		//client.publish(getData1, String(pinState));
		Serial.println(pinState);
		delay(100);
	}
	else if ((receiveEvent == change) && (pastPinState != pinState))
	{
		delay(20);
		if (pastPinState != pinState)
		{
			pastPinState = pinState;
			PrintButtonStatus(pinState);
			Serial.println(pinState);
		}
	}
	else if ((receiveEvent == changeUp) && (pastPinState != pinState))
	{
		delay(20);
		if (pastPinState != pinState)
		{

			pastPinState = pinState;
			if (pinState)
			{
				PrintButtonStatus(pinState);
				Serial.println(pinState);
			}
		}
	}
}

// Функция получения данных от сервера
void callback(const MQTT::Publish &pub)
{
	Serial.print(pub.topic());
	Serial.print(" => ");
	Serial.println(pub.payload_string());

	String payload = pub.payload_string();
	payload.trim();
	String name = findText(payload, "Name");
	String ModuleType = findText(payload, "ModuleType");
	String Request = findText(payload, "Request");

	if (name.equals("all")&&(ModuleType.equals(moduleType)||ModuleType.equals("all")))
	{
		if (Request.equals("status"))
		{
			PrintStatus();
		}
		PrintStatus();
		return;
	}

	String data = findText(payload, "Data");
	String led = findText(payload, "Led");
	String WorkMode = findText(payload, "WorkMode");

	Serial.println("Name:\t" + name);
	Serial.println("led:\t" + led);
	if (name.equals(deviceName) && (ModuleType.equals(moduleType)||ModuleType.equals("all")))
	{
		if (led.length() > 0)
		{
			Serial.println("Led:\t" + led);
			digitalWrite(LED_BUILTIN, led.toInt());
			PrintStatus();
		}
		else if (Request.equals("status"))
		{
			PrintStatus();
			Serial.println("PrintStatus OK");

		}
		else if (WorkMode.equals("changeEvent"))
		{
			receiveEvent = change;
			Serial.println("changeEvent OK");
			PrintMessage("changeEvent");
		}
		else if (WorkMode.equals("changeUpEvent"))
		{
			receiveEvent = changeUp;
			Serial.println("changeUpEvent OK");
			PrintMessage("changeUpEvent");
		}
		else if (WorkMode.equals("continueTransmin"))
		{
			receiveEvent = continueTransmin;
			Serial.println("continueTransmin OK");
			PrintMessage("continueTransmin");
		}
		else
		{
			//PrintStatus();
			Serial.println("undefined mes");
		}
	}
}

int UID = 0;

void PrintMessage(String str, int status)
{
	String data = "<Module>\
	<Name>" + String(deviceName) +
				  "</Name>\
	<ModuleType>" +
				  moduleType + "</ModuleType>\
	<UID>" + String(UID) +
				  "</UID>\
	<Data>" + str +
				  "</Data>\
	<Status>" + String(status) +
				  "</Status>\
	</Module>";
	client.publish(outDevices, data);

	UID++;
}

void PrintButtonStatus(bool pinState)
{
	String data = "<Module>\
	<Name>" + String(deviceName) +
				  "</Name>\
	<ModuleType>" +
				  moduleType + "</ModuleType>\
	<UID>" + String(UID) +
				  "</UID>\
	<Data><Pin16>" +
				  String(pinState) +
				  "</Pin16></Data>\
	<Status>0</Status>\
	</Module>";
	client.publish(outDevices, data);

	UID++;
}

void PrintStatus()
{
	String data = "<Module>\
	<Name>" + String(deviceName) +
				  "</Name>\
	<ModuleType>" +
				  moduleType + "</ModuleType>\
	<UID>" + String(UID) +
				  "</UID>\
	<Status>0</Status>\
	</Module>";
	client.publish(outDevices, data);

	UID++;
}

String findText(String str, String findContext)
{

	int find1 = str.indexOf("<" + findContext + ">");
	int find2 = str.indexOf("</" + findContext + ">");
	if ((find1 < 1) || (find2 < 1))
		return "";
	String findStr = "";
	for (int i = find1 + ("<" + findContext + ">").length(); i < find2; i++)
	{
		findStr += str[i];
	}

	return findStr;
}