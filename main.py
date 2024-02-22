import sys
import os
from Adafruit_IO import MQTTClient
import random
import time
from dotenv import load_dotenv, dotenv_values
from ai import *
from uart import *

load_dotenv()

AIO_FEED_IDs = os.getenv("ids")
AIO_USERNAME = os.getenv("username")
AIO_KEY = os.getenv("key")

def connected(client):
    print("Ket noi thanh cong ...")
    for topic in AIO_FEED_IDs:
        client.subscribe(topic)

def subscribe(client , userdata , mid , granted_qos):
    print("Subscribe thanh cong ...")

def disconnected(client):
    print("Ngat ket noi ...")
    sys.exit (1)

def message(client , feed_id , payload):
    print("Nhan du lieu: " + payload + " , feed id: " + feed_id)

client = MQTTClient(AIO_USERNAME , AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()
counter = 10
counter_ai = 5
sensor_type = 0

while True:
    # counter = counter - 1
    # if counter <= 0:
        # counter = 10
        # print("Random data is publishing")
        # if sensor_type == 0:
        #     print("Temperature...")
        #     temp = random.randint(10,30)
        #     client.publish("sensor1", temp)
        #     sensor_type = 1
        # elif sensor_type == 1:
        #     print("Humidity...")
        #     humid = random.randint(50,70)
        #     client.publish("sensor2", humid)
        #     sensor_type = 2
        # elif sensor_type == 2:
        #     print("Light...")
        #     light = random.randint(100,500)
        #     client.publish("sensor3", light)
        #     sensor_type = 0

    counter_ai = counter_ai - 1
    if counter_ai <= 0:
        counter_ai = 5
        ai_result = image_detector()
        print("Output: ", ai_result)
        client.publish("ai", ai_result)

    readSerial(client)
    time.sleep(1)
    pass