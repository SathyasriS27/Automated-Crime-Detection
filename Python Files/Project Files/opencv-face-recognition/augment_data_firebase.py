import numpy as np
from imutils import paths as pts
import Augmentor
import cv2
import os
import firebase_admin
from firebase_admin import firestore
from firebase_admin import credentials
from skimage import io
import gdown
import requests
import pyrebase
import json
import shutil

# ----------------------------- Functions -----------------------------

# Function to extract path of new datasets uploaded
def extractPath(i):
  a = (str(i).split(',')[1:])
  b = str(a[-1]).rstrip('>')
  c = b.lstrip()
  return c

# Function to retrieve person's name
def firestoreName():
  users_ref = db.collection(u'New Users')
  docs = users_ref.stream()

  userName = ""

  for doc in docs:
    dict1 = doc.to_dict()
    userName = dict1['Username']
  
  return userName

# Function to blur the image
def blurImage(imagePath, userName, i):
    old_image = io.imread(imagePath)
    # old_image = cv2.imread(imagePath)
    image = old_image.copy()
    blurredImage = cv2.blur(image, (3, 3))
    filename = "blurred_" + userName + "_" + str(i) + ".jpg"
    destination = 'Datasets/' + userName + '/' + filename
    # os.chdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + userName)
    cv2.imwrite(filename, blurredImage) 
    return destination, filename

# Function to sharpen the image
def sharpenImage(imagePath, userName, i):
    old_image = io.imread(imagePath)
    image = old_image.copy()
    kernel = np.array([[-1, -1, -1], [-1, 9, -1], [-1, -1, -1]])
    sharpImage = cv2.filter2D(image, -1, kernel)
    filename = "sharpened_" + userName + "_" + str(i) + ".jpg"
    destination = 'Datasets/' + userName + '/' + filename
    # os.chdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + userName)
    cv2.imwrite(filename, sharpImage) 
    return destination, filename

# Function to add Sepia effect 
def sepiaImage(imagePath, userName, i):
    old_image = io.imread(imagePath)
    image = old_image.copy()
    kernel = np.array([[0.272, 0.534, 0.131], [0.349, 0.686, 0.168], [0.393, 0.769, 0.189]])
    sepiaImage = cv2.filter2D(image, -1, kernel)
    filename = "sepia_" + userName +  "_" + str(i) + ".jpg"
    destination = 'Datasets/' + userName + '/' + filename
    # os.chdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + userName)
    cv2.imwrite(filename, sepiaImage) 
    return destination, filename

# Function to add brightness
def brightImage(imagePath, userName, i):
    old_image = io.imread(imagePath)
    image = old_image.copy()
    brightImage = cv2.convertScaleAbs(image, 3)
    filename = "bright_" + userName +  "_" + str(i) + ".jpg"
    destination = 'Datasets/' + userName + '/' + filename
    cv2.imwrite(filename, brightImage) 
    return destination, filename

# Tilt image to certain angles
def tiltedImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.rotate(1, 15, 15)
    old_image = io.imread(imagePath)
    
# Mirror image
def mirrorImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.flip_left_right(probability = 1)
    old_image = io.imread(imagePath)

# Shearing image
def shearImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.shear(probability = 1, max_shear_left = 15, max_shear_right = 15)
    old_image = io.imread(imagePath)

# Skewing image
def skewedImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.skew(probability = 1, magnitude = 0.7)
    old_image = io.imread(imagePath)

# Black and White 
def bwImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.black_and_white(probability = 1, threshold = 255)
    old_image = io.imread(imagePath)
  
# Initialize pipeline
def usePipeline(imagePath, destination, userName):
    p = Augmentor.Pipeline(imagePath, imagePath)
    p1 = Augmentor.Pipeline(imagePath, imagePath)
    p2 = Augmentor.Pipeline(imagePath, imagePath)
    p3 = Augmentor.Pipeline(imagePath, imagePath)
    p4 = Augmentor.Pipeline(imagePath, imagePath)
    
    p.rotate(1, 15, 15)
    p1.flip_left_right(probability = 1)
    p2.shear(probability = 1, max_shear_left = 15, max_shear_right = 15)
    p3.skew(probability = 1, magnitude = 0.7)
    p4.black_and_white(probability = 1, threshold = 64)

    p.sample(25)
    p1.sample(25)
    p2.sample(25)
    p3.sample(25)
    p4.sample(25)

    uploadFiles(imagePath, destination, userName)
  
# Function to upload files to Firebase Storage
def uploadFiles(imagePath, destination, userName):
    imagePaths = list(pts.list_images(imagePath))
    for imgPath in imagePaths:
      fileName = imgPath.split('/')[-1]
      dest = destination + "/" + fileName
      storage.child(dest).put(imgPath)

# Function to delete collection
def deleteCollection():
    users_ref = db.collection(u'New Users')
    docs = users_ref.stream()

    for doc in docs:
      doc.reference.delete()

# Function to delete /New Datasets folder from Firebase Storage
def deleteFolder(folderPaths):
    for folderPath in folderPaths:
      storage.delete(folderPath)

# Function to create new datasets and store them in Firebase Storage
def newDatasets(imagePaths, paths):

  name = firestoreName()
  count = 0

  for imagePath in imagePaths:

      # Perform data augmentation
      
      destbl, filebl = blurImage(imagePath, name, count)
      destsh, filesh = sharpenImage(imagePath, name, count)
      destse, filese = sepiaImage(imagePath, name, count)
      destbr, filebr = brightImage(imagePath, name, count)

      storage.child(destbl).put(filebl)
      storage.child(destsh).put(filesh)
      storage.child(destse).put(filese)
      storage.child(destbr).put(filebr)

      os.remove(filebl)
      os.remove(filesh)
      os.remove(filese)
      os.remove(filebr)

      count += 1

  imgPath = str(os.getcwd()) + "/temp"

  imgPaths = []
  for j in paths:
    i = str(j)
    dest = imgPath + "/" + str((i.split('/'))[-1])
    storage.child(i).download(dest)
    imgPaths.append(dest)

  destination = 'Datasets/' + name
  usePipeline(imgPath, destination, name)

  # print("\nDone with all images.\n")

  # destination1 = '/content/drive/MyDrive/Open Lab/Datasets 1/' + name
  # movePhotos(destination1, name)
  deleteCollection()
  # print('\nDeleted new datasets after moving.\n')


# ----------------------------- Main -----------------------------

url = "https://drive.google.com/uc?export=download&id=1vIl_ircPdiLWTkuV2LSTYTJzFOy_cAGB"
r = requests.get(url, allow_redirects = True)
# output = 'serviceRequestKey.json'
data = r.json()

f = open('serviceRequestKey.json', 'w')
json.dump(data, f)
f.close()

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

firebase = pyrebase.initialize_app(config)
storage = firebase.storage()

cred = credentials.Certificate("serviceRequestKey.json")
firebase_admin.initialize_app(cred, {'projectId': 'human-pokedex'})
db = firestore.client()

# List containing paths of images in Firebase Storage (New Datasets/)
l = storage.list_files()
paths1 = []
for i in l:
  path = extractPath(i)
  if (path.split('/')[0] == "New Datasets"):
    paths1.append(path)
paths2 = paths1[1:]
paths2.append(paths2[0])
paths2.pop(0)
paths1 = paths1[2:]

# List of corresponding URLs for all paths in previous list
path_urls = []
for i in paths1:
  url = storage.child(str(i)).get_url(None)
  path_urls.append(url)

# List containing paths of images in Firebase Storage (Photos/)
lx = storage.list_files()
paths1x = []
for i in lx:
  pathx = extractPath(i)
  if (pathx.split('/')[0] == "Photos"):
    paths1x.append(pathx)
paths2x = paths1x[1:]
paths2x.append(paths2x[0])
paths2x.pop(0)
paths1x = paths1x[2:]

# List of corresponding URLs for all paths in previous list
pathx_urls = []
for i in paths1x:
  urlx = storage.child(str(i)).get_url(None)
  pathx_urls.append(urlx)

# Added the URLs of photos to Firestore
name = firestoreName()
db.collection(u'Users').document(u'Username ' + name).update({u'photoStored' : pathx_urls})

# Perform Data Augmentation and upload new dataset
# print(paths1)
input("Press any key to continue:\n")
imgPath = str(os.getcwd()) + "/temp"
os.mkdir(imgPath)
newDatasets(path_urls, paths1)
shutil.rmtree(imgPath)
deleteFolder(paths2)
# print("New Datasets folder deleted.\n")