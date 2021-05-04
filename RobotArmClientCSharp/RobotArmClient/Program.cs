using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Client.Connecting;
using MQTTnet.Client.Options;
using MQTTnet.Formatter;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RobotArmClient
{
    class Program
    {
        static async Task Main(string[] args)
        {

            var client = new MqttFactory().CreateMqttClient();


            var options = new MqttClientOptionsBuilder()
       .WithTcpServer("mqtt.eclipseprojects.io", 1883)
    //   .WithCredentials(txtUsername.Text, txtPassword.Text)
       .WithProtocolVersion(MqttProtocolVersion.V311)
       .Build();
            var auth = await client.ConnectAsync(options);

            if (auth.ResultCode != MqttClientConnectResultCode.Success)
            {
                throw new Exception(auth.ResultCode.ToString());
            }
            else
            {
                client.UseApplicationMessageReceivedHandler(e =>
                {
                    Console.WriteLine("### RECEIVED APPLICATION MESSAGE ###");
                    Console.WriteLine($"+ Topic = {e.ApplicationMessage.Topic}");
                    Console.WriteLine($"+ Payload = {Encoding.UTF8.GetString(e.ApplicationMessage.Payload)}");
                    Console.WriteLine($"+ QoS = {e.ApplicationMessage.QualityOfServiceLevel}");
                    Console.WriteLine($"+ Retain = {e.ApplicationMessage.Retain}");
                    Console.WriteLine();
                    Task.Run(() => client.PublishAsync("userM/line1/outWatch"));
                });

                var message = new MqttApplicationMessageBuilder()
                    .WithTopic("userM/line1/out")
                    .WithPayload("ok")
                    .WithExactlyOnceQoS()
                    .WithRetainFlag()
                    .Build();
                await client.PublishAsync(message);
            }
     
        }
    }
}
