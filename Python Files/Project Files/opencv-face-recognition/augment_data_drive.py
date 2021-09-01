import numpy as np
import Augmentor
import os 
import cv2
import shutil
import firebase_admin
from firebase_admin import firestore
from firebase_admin import credentials
from imutils import paths
import math

# -------------------------------------- Main ---------------------------------------------

imagePaths = list(paths.list_images(r"/content/drive/MyDrive/Open Lab/New Datasets 1"))

name = firestoreName()
os.mkdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + name)
imgPath = "/content/drive/MyDrive/Open Lab/New Datasets 1/" + name
count = 0

for imagePath in imagePaths:

    # Perform data augmentation
    
    blurImage(imagePath, name, count)
    sharpenImage(imagePath, name, count)
    sepiaImage(imagePath, name, count)
    brightImage(imagePath, name, count)

    count += 1
    
usePipeline(imgPath, name)

print("\nDone with all images.\n")

destination = '/content/drive/MyDrive/Open Lab/Datasets 1/' + name
movePhotos(destination, name)
print('\nDeleted new datasets after moving.\n')


# ---------------------------- Functions for data augmentation ----------------------------

# Function to move photos from /output to /Datasets/Username
def movePhotos(destination, name):
  imagePaths = list(paths.list_images(r"/content/drive/MyDrive/Open Lab/New Datasets 1"))
  print(imagePaths)
  input("Press any key.")
  for imagePath in imagePaths:
    shutil.copy(imagePath, destination)
  
  print("Moved all files.\n")
  delPath = '/content/drive/MyDrive/Open Lab/New Datasets 1/' + name
  shutil.rmtree(delPath)


# Function to retrieve person's name
def firestoreName():
  cred = credentials.Certificate("/content/drive/MyDrive/Open Lab/Private Keys/human-pokedex-firebase-adminsdk-37ou3-147a3cdcff.json")
  firebase_admin.initialize_app(cred, {
  'projectId': 'human-pokedex',
  })
  db = firestore.client()
  users_ref = db.collection(u'New Users')
  docs = users_ref.stream()

  userName = ""

  for doc in docs:
    dict1 = doc.to_dict()
    userName = dict1['Username']
  
  return userName


# Function to blur the image
def blurImage(imagePath, userName, i):
    old_image = cv2.imread(imagePath)
    image = old_image.copy()
    blurredImage = cv2.blur(image, (3, 3))
    filename = "blurred_" + userName + "_" + str(i) + ".jpg"
    os.chdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + userName)
    cv2.imwrite(filename, blurredImage) 


# Function to sharpen the image
def sharpenImage(imagePath, userName, i):
    old_image = cv2.imread(imagePath)
    image = old_image.copy()
    kernel = np.array([[-1, -1, -1], [-1, 9, -1], [-1, -1, -1]])
    sharpImage = cv2.filter2D(image, -1, kernel)
    filename = "sharpened_" + userName + "_" + str(i) + ".jpg"
    os.chdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + userName)
    cv2.imwrite(filename, sharpImage)


# Function to add Sepia effect 
def sepiaImage(imagePath, userName, i):
    old_image = cv2.imread(imagePath)
    image = old_image.copy()
    kernel = np.array([[0.272, 0.534, 0.131], [0.349, 0.686, 0.168], [0.393, 0.769, 0.189]])
    sepiaImage = cv2.filter2D(image, -1, kernel)
    filename = "sepia_" + userName +  "_" + str(i) + ".jpg"
    os.chdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + userName)
    cv2.imwrite(filename, sepiaImage)


# Function to add brightness
def brightImage(imagePath, userName, i):
    old_image = cv2.imread(imagePath)
    image = old_image.copy()
    brightImage = cv2.convertScaleAbs(image, 3)
    filename = "bright_" + userName +  "_" + str(i) + ".jpg"
    os.chdir(r'/content/drive/MyDrive/Open Lab/Datasets 1/' + userName)
    cv2.imwrite(filename, brightImage)


# Tilt image to certain angles
def tiltedImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.rotate(1, 15, 15)
    old_image = cv2.imread(imagePath)


# Mirror image
def mirrorImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.flip_left_right(probability = 1)
    old_image = cv2.imread(imagePath)


# Shearing image
def shearImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.shear(probability = 1, max_shear_left = 15, max_shear_right = 15)
    old_image = cv2.imread(imagePath)


# Skewing image
def skewedImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.skew(probability = 1, magnitude = 0.7)
    old_image = cv2.imread(imagePath)


# Black and White 
def bwImage(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p.black_and_white(probability = 1, threshold = 255)
    old_image = cv2.imread(imagePath)


# Initialize pipeline
def usePipeline(imagePath, userName):
    p = Augmentor.Pipeline(imagePath)
    p1 = Augmentor.Pipeline(imagePath)
    p2 = Augmentor.Pipeline(imagePath)
    p3 = Augmentor.Pipeline(imagePath)
    p4 = Augmentor.Pipeline(imagePath)
    
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