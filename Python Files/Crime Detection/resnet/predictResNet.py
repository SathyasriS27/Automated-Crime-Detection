from keras.models import load_model
from keras.preprocessing import image
import numpy as np
from keras.models import Sequential
from keras.layers import Conv2D
from keras.layers import MaxPooling2D
from keras.layers import Flatten
from keras.layers import Dense

from time import time

import rutas_resnet as rt

t1 = time()
classifier = load_model(os.path.join(
    rt.checkpoints_resnet, 'ResNet50_model_weights.h5'))
#test_image = image.load_img('testing_dataset/normal_116.jpeg', target_size = (300,300))
#test_image = image.load_img('normal/0.jpg', target_size = (255,255))
test_image = image.load_img('anomalous/403.jpg', target_size=(255, 255))
test_image = image.img_to_array(test_image)
test_image = np.expand_dims(test_image, axis=0)
t2 = time()
result = classifier.predict(test_image)
print(result)
print(type(result))
print(time()-t2)
print(time()-t1)
print('\n')
t3 = time()
test_image = image.load_img('normal/0.jpg', target_size=(255, 255))
#test_image = image.load_img('anomalous/403.jpg', target_size = (255,255))
test_image = image.img_to_array(test_image)
test_image = np.expand_dims(test_image, axis=0)
print(result)
print(result[0][0])
print(type(result))
result = list(result)
print(result)
print(result[0][0])
print(type(result))
print(time()-t3)
print(time()-t1)
print('\n')
