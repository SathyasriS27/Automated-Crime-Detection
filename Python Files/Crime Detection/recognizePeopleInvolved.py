# import the necessary packages
from tensorflow.keras.models import load_model
from collections import deque
import numpy as np
import argparse
import pickle
import cv2
from imutils.video import VideoStream
from imutils.video import FPS
from imutils.video import VideoStream
from imutils.video import FPS
import numpy as np
import argparse
import imutils
import pickle
import time
import cv2
import os
from keras.models import model_from_json
from tf_models.inception_resnet_v1 import *
import json
from keras.preprocessing.image import load_img, save_img, img_to_array
from keras.applications.imagenet_utils import preprocess_input
import firebase_admin
from firebase_admin import firestore
from firebase_admin import credentials
import datetime
import pyrebase
import requests
import threading
from google.cloud import storage
import concurrent.futures
from multiprocessing import Process
import os


ap = argparse.ArgumentParser()
ap.add_argument("-l", "--label", required = True, help = "Labels")
args = vars(ap.parse_args())

url = "https://drive.google.com/uc?export=download&id=1vIl_ircPdiLWTkuV2LSTYTJzFOy_cAGB"
r = requests.get(url, allow_redirects = True)
# output = 'serviceRequestKey.json'
data = r.json()

f = open('serviceRequestKey.json', 'w')
json.dump(data, f)
f.close()

# Load our serialized face detector

protoPath = os.path.sep.join([r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\face_detection_model', 'deploy.prototxt'])
modelPath = os.path.sep.join([r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\face_detection_model', 'res10_300x300_ssd_iter_140000.caffemodel'])
detector = cv2.dnn.readNetFromCaffe(protoPath, modelPath)

# Load our serialized face embedding model
embedder = InceptionResNetV1()
embedder.load_weights('facenet_weights.h5')

# Load the SVM Model and LabelEncoder
recognizer = pickle.loads(open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\New TF Model Test\Trained Models\recognizer.pickle', "rb").read())
le = pickle.loads(open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\New TF Model Test\Trained Models\le.pickle', "rb").read())

config = {
    "apiKey": "AIzaSyBqFROlkrLs0fMirkUoV4Sutn8AkTlBPlQ",
    "authDomain": "human-pokedex.firebaseapp.com",
    "projectId": "human-pokedex",
    "databaseURL": "",
    "storageBucket": "human-pokedex.appspot.com",
    "messagingSenderId": "466324270281",
    "appId": "1:466324270281:web:d61e64e5c20932db15b118",
    "measurementId": "G-V0DRGWJ51J",
    "serviceAccount": "serviceRequestKey.json"
}

def updateFirestore(label, camNo, involvedPeople, db, unrecognized):
    date_list = str(datetime.datetime.now())
    date = str(date_list.split(' ')[0])
    time = str(date_list.split(' ')[1])
    urls = [] 
    # print("Involved People: ", involvedPeople)
    # print("Label: ", label)
    # print("Unrecognized: ", unrecognized)
    
    # for i in unrecognized:
    #     unrec_split = unrecognized[i].split("\\")
    #     destPath = '/Unrecognized Faces/' + date + '/' + urec_split[-1]
    #     storage.child(destPath).put(unrecognized[i])
    #     url = storage.child(destPath).get_url()
    #     urls.append(url)      
    
    db.collection(u'CCTV').document(str(camNo)).collection(date).document(time).set({
        u'offenceLabel' : label,
        u'involvedPeople' : involvedPeople,
        # u'unrecognizedFaces' : urls
    })

def preprocess_image_img(img):
    img = cv2.resize(img, (160, 160))
    img = img_to_array(img)
    img = np.expand_dims(img, axis=0)
    img = preprocess_input(img)
    return img

def recognizePeopleInvolved(vs, db, label, camNo):
  frame = vs.read()
  frame = imutils.resize(frame, width = 600)
  (h, w) = frame.shape[:2]
  unrecognized_list = []
  names = []
  # unrecognized_list_URL = []
  unrecognizedPath = r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Crime Detection\Unrecognized faces'
  # date = str(datetime.datetime.now()).split(' ')[0]
  filePath = unrecognizedPath

  imageBlob = cv2.dnn.blobFromImage(cv2.resize(frame, (300, 300)), 1.0, (300, 300), (104.0, 177.0, 123.0), swapRB = False, crop = False)

  detector.setInput(imageBlob)
  detections = detector.forward()

  k = 0
  for i in range(0, detections.shape[2]):
    confidence = detections[0, 0, i, 2]

    if (confidence > 0.7):
      box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
      (startX, startY, endX, endY) = box.astype("int")

      face = frame[startY: endY, startX: endX]
      (fH, fW) = face.shape[:2]

      if ((fW < 20) or (fH < 20)):
        continue
      
      # faceBlob = cv2.dnn.blobFromImage(face, 1.0/255, (96, 96), (0, 0, 0), swapRB = True, crop = False)

      # embedder.setInput(faceBlob)
      vec = embedder.predict(preprocess_image_img(face))[0, :].reshape(1, -1)
      # print(vec)

      preds = recognizer.predict_proba(vec)[0]
      # print(preds)
      j = np.argmax(preds)
      proba = preds[j]
      name = le.classes_[j]
    
    else:
      name = "unrecognized_" + str(k)
      k += 1
      box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
      (startX, startY, endX, endY) = box.astype("int")

      face = frame[startY: endY, startX: endX]
      (fH, fW) = face.shape[:2]

      if ((fW < 20) or (fH < 20)):
        continue
      
      
      time1 = str(datetime.datetime.now()).split(' ')[1]
      fileName = time1 + ".jpg"
      cv2.imwrite(fileName, face)
      filePath1 = filePath + "\\" + fileName
      unrecognized_list.append(filePath1)
    
    names.append(name)
  
  updateFirestore(label, camNo, names, db, unrecognized_list)

# Main Program

vs = VideoStream(src = 0).start()
offence_labels = ['Abuse', 'Fighting', 'Vandalism', 'Robbery', 'Assault']
writer = None
(W, H) = (None, None)
db = firestore.client()
cameraNo = "A Block"
recognizePeopleInvolved(vs, db, cameraNo, args[label])