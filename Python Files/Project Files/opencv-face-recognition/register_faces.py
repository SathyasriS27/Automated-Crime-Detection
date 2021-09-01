# Importing all necessary packages

from imutils import paths
import numpy as np
import argparse
import imutils
import pickle
import time
import cv2
from sklearn.preprocessing import LabelEncoder
from sklearn.svm import SVC
import os

# Load face detector

print("Loading face detector...")

protoPath = os.path.sep.join(['/content/drive/MyDrive/Open Lab/face_detection_model', 'deploy.prototxt'])
modelPath = os.path.sep.join(['/content/drive/MyDrive/Open Lab/face_detection_model', 'res10_300x300_ssd_iter_140000.caffemodel'])

detector = cv2.dnn.readNetFromCaffe(protoPath, modelPath)


# Load face recognizer

print("\nLoading face recognizer...")
embedder = cv2.dnn.readNetFromTorch('/content/drive/MyDrive/Open Lab/openface_nn4.small2.v1.t7')

# Entering paths to our images dataset

print('\nQuantifying faces...')
# Make sure this path only contains the new photos
imagePaths = list(paths.list_images('/content/drive/MyDrive/Open Lab/New Dataset'))

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

print ('Serializing {} encodings:\n'.format(total))

data = {'embeddings': knownEmbeddings, 'names': knownNames}                       # Saved as a dictionary/ HashMap
data_org = pickle.loads(open('/content/drive/MyDrive/Open Lab/Output Embeddings/embeddings.pickle', 'rb').read())
data_org['embeddings'].append(data['embeddings'])
data_org['names'].append(data['names'])

f = open('/content/drive/MyDrive/Open Lab/Output Embeddings/embeddings.pickle', 'wb')
f.write(pickle.dumps(data_org))                                                       # Stored as a ByteStream
f.close()

print("Loading face embeddings:\n")
data = pickle.loads(open('/content/drive/MyDrive/Open Lab/Output Embeddings/embeddings.pickle', 'rb').read())
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
f = open('/content/drive/MyDrive/Open Lab/Trained Models/recognizer.pickle', "wb")
f.write(pickle.dumps(recognizer))
f.close()

# Save the label encoder
f = open('/content/drive/MyDrive/Open Lab/Trained Models/le.pickle', "wb")
f.write(pickle.dumps(le))
f.close()