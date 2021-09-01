import numpy as np
import argparse
import imutils
import time
import cv2
import os
from imutils.video import FPS
from imutils.video import VideoStream
import pickle

# Setting paths to the trained model
print("Loading face detector...\n")
protoPath = os.path.sep.join([r'C:\\Users\\Yash Umale\\Documents\\6th Sem\\Open Lab\\Python Files\\Project Files\\opencv-face-recognition\\face_detection_model', 'deploy.prototxt'])
modelPath = os.path.sep.join([r'C:\\Users\\Yash Umale\\Documents\\6th Sem\\Open Lab\\Python Files\\Project Files\\opencv-face-recognition\\face_detection_model', 'res10_300x300_ssd_iter_140000.caffemodel'])
detector = cv2.dnn.readNetFromCaffe(protoPath, modelPath)
print("Loaded face detector.\n")

# Load our serialized face embedding model
print("\nLoading face recognizer...\n")
embedder = cv2.dnn.readNetFromTorch(r'C:\\Users\\Yash Umale\\Documents\\6th Sem\\Open Lab\\Python Files\\Project Files\\opencv-face-recognition\\openface_nn4.small2.v1.t7')
print("Loaded Face Recognizer.\n")

# Load the SVM Model and LabelEncoder
recognizer = pickle.loads(open(r"C:\\Users\\Yash Umale\\Documents\\6th Sem\\Open Lab\\Python Files\\Project Files\\Trained Models\\recognizer.pickle", "rb").read())
le = pickle.loads(open(r'C:\\Users\\Yash Umale\\Documents\\6th Sem\\Open Lab\\Python Files\\Project Files\\Trained Models\\le.pickle', "rb").read())

# ------------------------------------------ Starting video stream over RTSP URL ------------------------------------------

rtsp_url = "rtsp://192.168.1.6:8080/h264_pcm.sdp"

vs = VideoStream(rtsp_url).start()
print("Test 0\n")
time.sleep(2.0)

# Start FPS Throughput Estimator
fps = FPS().start()

# Loop over frames
print("Test 1\n")
while True:
  frame = vs.read()
  frame = imutils.resize(frame, width = 600)
  (h, w) = frame.shape[:2]

  imageBlob = cv2.dnn.blobFromImage(cv2.resize(frame, (300, 300)), 1.0, (300, 300), (104.0, 177.0, 123.0), swapRB = False, crop = False)

  detector.setInput(imageBlob)
  detections = detector.forward()
  print("Test 2\n")

  for i in range(0, detections.shape[2]):
    confidence = detections[0, 0, i, 2]

    if (confidence > 0.6):
      box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
      (startX, startY, endX, endY) = box.astype("int")

      face = frame[startY: endY, startX: endX]
      (fH, fW) = face.shape[:2]

      if ((fW < 20) or (fH < 20)):
        continue
      
      faceBlob = cv2.dnn.blobFromImage(face, 1.0/255, (96, 96), (0, 0, 0), swapRB = True, crop = False)

      embedder.setInput(faceBlob)
      vec = embedder.forward()

      preds = recognizer.predict_proba(vec)[0]
      j = np.argmax(preds)
      proba = preds[j]
      name = le.classes_[j]

      text = "{}: {:.2f}%".format(name, proba * 100)
      y = startY - 10 if (startY - 10 > 10) else (startY + 10)
      
      print("Test 3\n")

      cv2.rectangle(frame, (startX, startY), (endX, endY), (0, 0, 255), 2)
      cv2.putText(frame, text, (startX, y), cv2.FONT_HERSHEY_SIMPLEX, 0.45, (0, 0, 255), 2)
  
  fps.update()
  cv2.imshow("Frame", frame)
  print("Test 4\n")

  key = cv2.waitKey(1) & 0xFF
  if (key == ord('q') or key == ord('Q')):
    break
  
fps.stop()

print("Elapsed time: {:.2f}".format(fps.elapsed()))
print("Approx. FPS: {:.2f}".format(fps.fps()))

cv2.destroyAllWindows()
vs.stop()