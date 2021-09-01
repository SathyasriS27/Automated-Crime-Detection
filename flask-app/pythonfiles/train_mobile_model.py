# Importing all necessary packages

from imutils import paths
import numpy as np
import argparse
import imutils
import pickle
import time
import cv2
import os
from sklearn.preprocessing import LabelEncoder
from sklearn.svm import SVC

# Load face detector

print("Loading face detector...")

protoPath = os.path.sep.join([r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\face_detection_model', 'deploy.prototxt'])
modelPath = os.path.sep.join([r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\face_detection_model', 'res10_300x300_ssd_iter_140000.caffemodel'])

detector = cv2.dnn.readNetFromCaffe(protoPath, modelPath)


# Load face recognizer

print("\nLoading face recognizer...")
embedder = cv2.dnn.readNetFromTorch(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\openface_nn4.small2.v1.t7')

# Entering paths to our images dataset

print('\nQuantifying faces...')
imagePaths = list(paths.list_images(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\Datasets'))

knownEmbeddings = []
knownNames = []

# Total number of faces
total = 0

# Loop over all image paths

for (i, imagePath) in enumerate(imagePaths):
  print("Processing image {}/{}".format(i + 1, len(imagePaths)))                  # Extract the person name from the image path
  name = imagePath.split(os.path.sep)[-2]

  image = cv2.imread(imagePath)                                                   # Load the image
  image = imutils.resize(image, width=600)                                        # Resize to (600, 600)
  (h, w) = image.shape[:2]                                                        # Store image dimensions
  imageBlob = cv2.dnn.blobFromImage(                                              # Construct a blob from the image
  	cv2.resize(image, (300, 300)), 1.0, (300, 300),
  	(104.0, 177.0, 123.0), swapRB=False, crop=False)

  detector.setInput(imageBlob)                                                    # Face Detector to localize face in an image
  detections = detector.forward()       

  if len(detections) > 0:                                                         # Ensure at least one face was found
    i = np.argmax(detections[0, 0, :, 2])
    confidence = detections[0, 0, i, 2]        

    if (confidence > 0.1):                                                          # Filtering weak detections
      box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
      (startX, startY, endX, endY) = box.astype("int")
			
      face = image[startY:endY, startX:endX]
      (fH, fW) = face.shape[:2]
			
      if (fW < 20 or fH < 20):
        continue    

      faceBlob = cv2.dnn.blobFromImage(face, 1.0 / 255, (96, 96), (0, 0, 0), 
                                       swapRB=True, crop=False)                   # Create blob

      embedder.setInput(faceBlob)
      vec = embedder.forward()

      knownNames.append(name)                                                     # Append name to list
      knownEmbeddings.append(vec.flatten())                                       # Append flattened embedding to list
      total += 1                                 

# Save (pickle) the embeddings and the names

print('Serializing {} encodings:\n'.format(total))

data = {'embeddings': knownEmbeddings, 'names': knownNames}                       # Saved as a dictionary/ HashMap
f = open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\Output Embeddings\embeddings.pickle', 'wb')
f.write(pickle.dumps(data))                                                       # Stored as a ByteStream
f.close()
print("Serialized {} encodings.\n".format(total))

# Training the model

print("Loading face embeddings:\n")
data = pickle.loads(open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\Output Embeddings\embeddings.pickle', 'rb').read())
print("Loaded.\n\n")

print("Encoding labels:\n")
le = LabelEncoder()
labels = le.fit_transform(data['names'])
print("\nLabels encoded.\n")

print("Training model...\n")
recognizer = SVC(C = 1.0, kernel = "linear", probability = True)
recognizer.fit(data["embeddings"], labels)
print("\nModel trained.\n")

# Save the actual face recognition model
f = open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\Trained Models\recognizer.pickle', "wb")
f.write(pickle.dumps(recognizer))
f.close()

# Save the label encoder
f = open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\Trained Models\le.pickle', "wb")
f.write(pickle.dumps(le))
f.close()

# Recognize face with OpenCV 
# Load our serialized face detector

print("Loading face detector...\n")
protoPath = os.path.sep.join([r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\face_detection_model', 'deploy.prototxt'])
modelPath = os.path.sep.join([r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\face_detection_model', 'res10_300x300_ssd_iter_140000.caffemodel'])
detector = cv2.dnn.readNetFromCaffe(protoPath, modelPath)
print("Loaded face detector.\n")

# Load our serialized face embedding model
print("\nLoading face recognizer...\n")
embedder = cv2.dnn.readNetFromTorch(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\opencv-face-recognition\openface_nn4.small2.v1.t7')
print("Loaded Face Recognizer.\n")

# Load the SVM Model and LabelEncoder
recognizer = pickle.loads(open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\Trained Models\recognizer.pickle', "rb").read())
le = pickle.loads(open(r'C:\Users\Yash Umale\Documents\6th Sem\Open Lab\Python Files\Project Files\Trained Models\le.pickle', "rb").read())

# Initialize the video stream, then allow the camera sensor to warm up

print("Starting video stream...\n")

vs = VideoStream(src = 0).start()
time.sleep(2.0)

# Start FPS Throughput Estimator
fps = FPS().start()

# Loop over frames
while True:
  frame = vs.read()
  frame = imutils.resize(frame, width = 600)
  (h, w) = frame.shape[:2]

  imageBlob = cv2.dnn.blobFromImage(cv2.resize(frame, (300, 300)), 1.0, (300, 300), (104.0, 177.0, 123.0), swapRB = False, crop = False)

  detector.setInput(imageBlob)
  detections = detector.forward()

  for i in range(0, detections.shape[2]):
    confidence = detections[0, 0, i, 2]

    if (confidence > 0.2):
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

      cv2.rectangle(frame, (startX, startY), (endX, endY), (0, 0, 255), 2)
      cv2.putText(frame, text, (startX, y), cv2.FONT_HERSHEY_SIMPLEX, 0.45, (0, 0, 255), 2)
  
  fps.update()
  cv2.imshow("Frame", frame)

  key = cv2.waitKey(1) & 0xFF
  if (key == ord('q') or key == ord('Q')):
    break
  
fps.stop()

print("Elapsed time: {:.2f}".format(fps.elapsed()))
print("Approx. FPS: {:.2f}".format(fps.fps()))

cv2.destroyAllWindows()
vs.stop()