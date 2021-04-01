#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <DallasTemperature.h>

#define butPin 16

const char *ssid = "Keenetic-6756"; // Имя  точки доступа
const char *pass = "03403blo";		// Пароль от точки доступа

const char *mqtt_server = "mqtt.eclipseprojects.io"; // Имя сервера MQTT
const int mqtt_port = 1883;							 // Порт для подключения к серверу MQTT
const char *mqtt_user = "Login";					 // Логи от сервер
const char *mqtt_pass = "Pass";						 // Пароль от сервера

#define BUFFER_SIZE 100

bool LedState = false;
bool pinState = false;
void TempSend();
WiFiClient wclient;
PubSubClient client(wclient, mqtt_server, mqtt_port);

String setModule1 = "userM/line1/sensor1/set/module1"; //led
String setRequest = "userM/line1/sensor1/set/request";
String setStatus = "userM/line1/sensor1/set/status";

String getStatus = "userM/line1/sensor1/get/status";
String getData1 = "userM/line1/sensor1/get/data1"; //button

enum EventRequest
{
	byRequest,
	change,
	changeUp,
	continueTransmin
};

EventRequest receiveEvent = byRequest;

// Функция получения данных от сервера
void callback(const MQTT::Publish &pub);

void setup()
{
	pinMode(LED_BUILTIN, 1);
	digitalWrite(LED_BUILTIN, 1);
	delay(500);
	digitalWrite(LED_BUILTIN, 0);
	delay(500);
	digitalWrite(LED_BUILTIN, 1);
	delay(500);
	digitalWrite(LED_BUILTIN, 0);
	delay(500);
	//sensors.begin();
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
			// if (client.connect(MQTT::Connect("arduinoClient2")
			// 					   .set_auth(mqtt_user, mqtt_pass)))
			if (client.connect(MQTT::Connect("arduinoClient2")
								   .set_auth(mqtt_user, mqtt_pass)))
			{
				Serial.println("Connected to MQTT server");
				client.set_callback(callback);

				client.subscribe(setModule1);
				client.subscribe(setRequest);
				client.subscribe(setStatus);
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
		client.publish(getData1, String(pinState));
		Serial.println(pinState);
		delay(100);
	}
	else if ((receiveEvent == change) && (pastPinState != pinState))
	{
		delay(20);
		if (pastPinState != pinState)
		{

			pastPinState = pinState;
			client.publish(getData1, String(pinState));
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
				client.publish(getData1, String(pinState));
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

	if (String(pub.topic()) == setStatus)
	{
		client.publish(getStatus, "OK");
		Serial.println("OK");
	}
	else if (String(pub.topic()) == setModule1) // включаем или выключаем светодиод
	{
		int stled = payload.toInt();
		digitalWrite(LED_BUILTIN, stled);
	}
	else if (String(pub.topic()) == setRequest)
	{
		//int stled = payload.toInt();
		client.publish(getData1, String(pinState));
		Serial.println(pinState);
		if (payload == "changeEvent")
		{
			receiveEvent = change;
			client.publish(getStatus, "changeEvent OK");
			Serial.println("changeEvent OK");
		}
		else if (payload == "changeUpEvent")
		{
			receiveEvent = changeUp;
			client.publish(getStatus, "changeUpEvent OK");
			Serial.println("changeUpEvent OK");
		}
		else if (payload == "continueTransmin")
		{
			receiveEvent = continueTransmin;
			client.publish(getStatus, "continueTransmin OK");
			Serial.println("continueTransmin OK");
		}
		else if (payload == "data1")
		{
			client.publish(getData1, String(pinState));
			Serial.println(pinState);
		}
		else
		{
			client.publish(getStatus, "OK");
			Serial.println("OK");
		}
	}
}