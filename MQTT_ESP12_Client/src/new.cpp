
#include <avr/eeprom.h>
#define LedPin 13
#define readPin 8
#define eepromB 600

void setup()
{
	// put your setup code here, to run once:
	Serial.begin(9600);
	//pinMode (2, OUTPUT);
	//pinMode (3, OUTPUT);
	pinMode(4, OUTPUT);
	pinMode(LedPin, 1);
	pinMode(14, INPUT);
	pinMode(8, 0);
	// eeprom_read_byte(eepromB);
	unsigned char str;
	str = 0;

	while (1)
	{

		if (str == 0)
		{
			//обработчик принятых данных
			if (Serial.available() > 0)
			{
				str = (unsigned int)(Serial.read() - '0');

				//eeprom_update_word(eepromB, b);

				Serial.print("New mode = ");

				Serial.println(str);
			}
		}

		Serial.print("mode = ");
		Serial.println(str); //eeprom_read_byte(eepromB));

		switch (str)
		{
		case 5:
			Serial.println("mod5");
			if (digitalRead(readPin) > 0)
			{
				digitalWrite(LedPin, HIGH);
				Serial.println("HIGH");
			}
			else
			{
				digitalWrite(LedPin, LOW);
				Serial.println("LOW");
			}
			break;
		case 6:
			Serial.println("hello");

			break;
		default:
			Serial.println("no hello");

			break;
		}

		//delay(100);
	}
}

void loop()
{
}