#!/usr/bin/env python 
#-*- coding: utf-8 -*- 
from concurrent import futures
from google.protobuf import json_format
import time
import logging
import pickle
import jieba
import grpc
import VoiceToText_pb2
import VoiceToText_pb2_grpc
import json

path = 'C:/Users/user/Desktop/grpc/VoiceToText'
jieba.load_userdict(path + '/userdict.txt')
jieba.suggest_freq(('我','關'), True)	 # 斷詞建議

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

class Text(VoiceToText_pb2_grpc.TextServicer): 
	def ShowText(self, request, context):
		words = "-".join(jieba.cut(request.text))	# jieba處理字串
		print(u'原字串為: ' + request.text)
		print(u'斷詞後的字串為: ' + words)	# 顯示斷詞完畢的結果
		json_command = {"state":"關", "location": "三樓", "target": "窗戶"}
		print(json.dumps(json_command, encoding="UTF-8", ensure_ascii=False))
		return VoiceToText_pb2.command_list(data = json.dumps(json_command
		, encoding="UTF-8", ensure_ascii=False))	# 結果顯示於 client 端

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    VoiceToText_pb2_grpc.add_TextServicer_to_server(Text(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    logging.basicConfig()
    serve()
