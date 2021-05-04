import paho.mqtt.client as mqtt
import threading
import socket
from socket import *
import time
import xml.etree.ElementTree as ET
import datetime

CTRL_msg_contents = (b"<Manipulator><Status>1</Status></Manipulator>")
OLD_CTRL_msg_contents = (b"<XML></XML>")
FeedbackOld = (b"<XML></XML>")
    
KukaCurUID = '0'
KukaCurProgram = '0'
KukaCurArgument = '0'
KukaCurStatus = '0'
KukaIncUID = '1'
KukaIncProgram = '0'
KukaIncArgument = '0'
KukaIncStatus = '0'

#serverUrl = '172.16.1.33'
inDevices = "userM/devices/in"
outDevices = "userM/devices/out"
serverUrl = "mqtt.eclipseprojects.io"


NewMessage = False

MQTTconnect = False

#функция для поиска текста по XML тегу
def  findText( str,  findContext):
    str.replace(' ', '')
    find1 = str.find('<' + findContext + '>')
    find2 = str.find('</' + findContext + '>')
    if(find1==-1) or (find2==-1):
        return ''
    findStr = '' 
    for  i in range( find1 + len('<' + findContext + '>'),  find2):
        findStr += str[i]
    return findStr

#___________________Поток 1, прием MQTT_________________________
def daemonThread1():
    while True:
        
        def on_connect(client, userdata, flags, rc):
            print("Connected with result code "+str(rc))
            client.subscribe(inDevices)
            
        def on_message(client, userdata, msg):
            global CTRL_msg_contents
            global NewMessage
            CTRL_msg_contents = msg.payload
            print ("Receive message: \n" + (CTRL_msg_contents.decode("utf-8")) + "\n")
            KukaMessage = CTRL_msg_contents.decode("utf-8")
            try:
                if KukaMessage.find('Module'):
                    UID = findText(KukaMessage,"UID")
                    Task = findText(KukaMessage,"Task")
                    Name = findText(KukaMessage,"Name")
                    ModuleType = findText(KukaMessage,"ModuleType")
                    if (ModuleType=="1")and(Name=="RobotKuka" or "all"):
                        KukaMessage+= "<Manipulator><UID>"+UID+"</UID><Task>"+Task+"</Task><Status>0</Status></Manipulator>"
                        CTRL_msg_contents = KukaMessage.encode("utf-8")
            except:
                print("Receive message Error")    
                
            print ("MQTT to TCP \n" + (CTRL_msg_contents.decode("utf-8")) + "\n")
            NewMessage = True

        def on_disconnect(client, userdata, rc):
            logging.info("(MQTT) Disconnecting reason: "+str(rc))
            print("MQTT Disconnected with rc:" +str(rc))

        KUKAlw = "<Manipulator><Online>0</Online></Manipulator>"               
        client = mqtt.Client()
        client.on_connect = on_connect
        client.on_message = on_message
        client.on_disconnect = on_disconnect
        client.will_set(outDevices, KUKAlw, qos=1, retain=False)
        
        try:
            client.connect(serverUrl, 1883, 60)
##            break
        except (TimeoutError):
            print("(MQTT CONN) Connection attempt timed out")
        client.loop_forever()

        

#___________________Поток 2, TCP клиент (отправляем на Kuka)_______________________
def daemonThread2():
    while True:

        global CTRL_msg_contents
        global OLD_CTRL_msg_contents
        global KukaIncStatus
        global tcp_socket

        global NewMessage

        if NewMessage == True and CTRL_msg_contents:
            data = (CTRL_msg_contents.decode("utf-8"))
##            Incroot = ET.fromstring(data) #парсим входящее на Куку

            if data: 
            
                try:    
                    tcp_socket.sendall(data.encode("utf-8")) #передаём данные в куку
                except (ConnectionAbortedError):
                    print("(TCP SEND)Connection Aborted.")
                    publish.single(outDevices, "<Manipulator><Online>0</Online><Code>9</Code></Manipulator>", hostname=serverUrl)
                    tcp_socket.close()
                    time.sleep(1)
                except (TimeoutError):
                    print("(TCP SEND)Timeout Error - TCP connection.")
                    publish.single(outDevices, "<Manipulator><Online>0</Online><Code>10</Code></Manipulator>", hostname=serverUrl)
                    tcp_socket.close()
                    time.sleep(1)
                except (ConnectionRefusedError):
                    print("(TCP SEND)Connection refused by Kuka - Please re-activate MonRo Protocol.")
                    publish.single(outDevices, "<Manipulator><Online>0</Online><Code>11</Code></Manipulator>", hostname=serverUrl)
                    tcp_socket.close()
                    time.sleep(1)
                except (ConnectionResetError):
                    print("(TCP SEND)Existing connection was forcibly closed by the remote host")
                    publish.single(outDevices, "<Manipulator><Online>0</Online><Code>12</Code></Manipulator>", hostname=serverUrl)
                    tcp_socket.close()
                    time.sleep(1)
                except (OSError):
                    print("(TCP SEND) No socket object")
                    publish.single(outDevices, "<Manipulator><Online>0</Online><Code>13</Code></Manipulator>", hostname=serverUrl)
                    tcp_socket = socket(AF_INET, SOCK_STREAM)
                    tcp_socket.connect(addr)
                    
                NewMessage = False
                
                
#___________________Поток 3, TCP to MQTT Feedback_________________________   
def daemonThread3():
    while True:
        
        global KukaIncStatus
        global FeedbackOld
        global tcp_socket

        time.sleep(0.01)
        
        try:
            Feedback = tcp_socket.recv(512)
        except (ConnectionAbortedError):
            print("(TCP RECV)Connection Aborted.")
            publish.single(outDevices, "<Manipulator><Online>0</Online><Code>5</Code></Manipulator>", hostname=serverUrl)
            tcp_socket.close()
            time.sleep(1)
        except (TimeoutError):
            print("(TCP RECV)Timeout Error - TCP connection.")
            publish.single(outDevices, "<Manipulator><Online>0</Online><Code>6</Code></Manipulator>", hostname=serverUrl)
            tcp_socket.close()
            time.sleep(1)
        except (ConnectionRefusedError):
            print("(TCP RECV)Connection refused by Kuka - Please re-activate MonRo Protocol.")
            publish.single(outDevices, "<Manipulator><Online>0</Online><Code>7</Code></Manipulator>", hostname=serverUrl)
            tcp_socket.close()
            time.sleep(1)
        except (ConnectionResetError):
            print("(TCP RECV)An existing connection was forcibly closed by the remote host")
            publish.single(outDevices, "<Manipulator><Online>0</Online><Code>8</Code></Manipulator>", hostname=serverUrl)
            tcp_socket.close()
            Connected = False
            time.sleep(1)
        except (OSError):
            print("(TCP RECV) No socket object")
            publish.single(outDevices, "<Manipulator><Online>0</Online><Code>9</Code></Manipulator>", hostname=serverUrl)
            tcp_socket = socket(AF_INET, SOCK_STREAM)
            tcp_socket.connect(addr)
            
        if Feedback:
            print(datetime.datetime.now())
            print("Получено от Kuka:")   
            print(Feedback)

            import paho.mqtt.publish as publish
            
            try:
                publish.single(outDevices, Feedback, hostname=serverUrl)
            except (TimeoutError):
                print('(MQTT SEND)Timeout Error, message not sent')
                
            FeedbackOld = Feedback
            KukaIncStatus = "0"


#_______________________Отпускаем потоки с миром__________________________
   
if __name__ == '__main__':

    while True:
            try:      
                host = '172.31.1.100'
                port = 54600
                addr = (host,port)

                tcp_socket = socket(AF_INET, SOCK_STREAM)
                tcp_socket.connect(addr)
                
                break
            except (ConnectionAbortedError):
                print("(TCP CONN)Connection Aborted.")
                publish.single(outDevices, "<Manipulator><Online>0</Online><Code>1</Code></Manipulator>", hostname=serverUrl)
                tcp_socket.close()
                time.sleep(1)
            except (TimeoutError):
                print("(TCP CONN)Timeout Error - TCP connection.")
                publish.single(outDevices, "<Manipulator><Online>0</Online><Code>2</Code></Manipulator>", hostname=serverUrl)
                tcp_socket.close()
                time.sleep(1)
            except (ConnectionRefusedError):
                print("(TCP CONN)Connection refused by Kuka - Please re-activate MonRo Protocol.")
                publish.single(outDevices, "<Manipulator><Online>0</Online><Code>3</Code></Manipulator>", hostname=serverUrl)
                tcp_socket.close()
                time.sleep(1)
            except (ConnectionResetError):
                print("(TCP CONN)An existing connection was forcibly closed by the remote host")
                publish.single(outDevices, "<Manipulator><Online>0</Online><Code>4</Code></Manipulator>", hostname=serverUrl)
                tcp_socket.close()
                time.sleep(1)


    
    daemonThread1 = threading.Thread(target = daemonThread1)
    daemonThread2 = threading.Thread(target = daemonThread2)
    daemonThread3 = threading.Thread(target = daemonThread3)
    daemonThread1.setDaemon(True)
    daemonThread2.setDaemon(True)
    daemonThread3.setDaemon(True)
    daemonThread1.start()
    daemonThread2.start()
    daemonThread3.start()

