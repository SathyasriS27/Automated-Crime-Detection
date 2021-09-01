import cv2
from imutils import paths
import os

folder_path = ""
files = os.listdir(folder_path)

for i in files:
    vidcap = cv2.VideoCapture(i)

    sec = 0
    frameRate = 0.5                                         # it will capture image in each 0.5 second
    count = 1
    success = getFrame(sec, vidcap)
    while success:
        count = count + 1
        sec = sec + frameRate
        sec = round(sec, 2)
        success = getFrame(sec)

# Helper function 

def getFrame(sec, vidcap):
    vidcap.set(cv2.CAP_PROP_POS_MSEC, sec*1000)
    hasFrames, image = vidcap.read()
    if hasFrames:
        cv2.imwrite("image"+str(count)+".jpg", image)       # save frame as JPG file
    return hasFrames