# coding=UTF-8
from __future__ import print_function
import logging
import grpc
import VoiceToText_pb2
import VoiceToText_pb2_grpc
from google.protobuf import json_format
import pickle
import json

def run():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = VoiceToText_pb2_grpc.TextStub(channel) # 建立存根
		print("Hello There!")
        response = stub.ShowText(VoiceToText_pb2.TextRequest(text = "幫我關窗戶")) # 呼叫 server 端函數
    json_command = pickle.loads(response.data)
    print("Message From Server:\n")
    print(json.dumps(json_command))

if __name__ == '__main__':
    logging.basicConfig()
    run()
