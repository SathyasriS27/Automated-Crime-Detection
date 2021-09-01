from keras.models import load_model
from keras.preprocessing import image
import numpy as np
from keras.models import Sequential
from keras.layers import Conv2D
from keras.layers import MaxPooling2D
from keras.layers import Flatten
from keras.layers import Dense
# ----------------------------------------------
import sys
import os
import cv2
# import math
import leertxt
import extract_texting

import pygame
from pygame.locals import *
# from time import time
import shutil as st

import rutas_resnet as rt

SCREEN_WIDTH = 1250
SCREEN_HEIGHT = 550+50

img_pos_x = 0
img_pos_y = 0
img_size_x = 700
img_size_y = 500

text_pos_x = img_pos_x + img_size_x + 30
text_pos_y = 50

gf_pos_x = text_pos_x + 100
gf_pos_y = img_size_y

white = (255, 255, 255)
red = (255, 0, 0)
green = (0, 255, 0)
blue = (0, 0, 255)


paigeim = int(sys.argv[1])


def grafica(screen, data, pos_x, pos_y, franja):
    ancho = 100
    pos1 = pos_x
    pos_x += 50
    factor = 300
    if data[0][0] == "anomalous":

        texto1, texto2, data1, data2 = data[0][0], data[1][0], data[0][1], data[1][1]
        rect1 = rx1, ry1, rw1, rh1 = pos_x, pos_y, ancho, -data[0][1]*factor
        pos_x += ancho+75
        rect2 = rx2, ry2, rw2, rh2 = pos_x, pos_y, ancho, -data[1][1]*factor
    else:
        texto1, texto2, data1, data2 = data[1][0], data[0][0], data[1][1], data[0][1]
        rect1 = rx1, ry1, rw1, rh1 = pos_x, pos_y, ancho, -data[1][1]*factor
        pos_x += ancho + 75
        rect2 = rx2, ry2, rw2, rh2 = pos_x, pos_y, ancho, -data[0][1]*factor
    start_pos1 = pos1, pos_y-factor
    end_pos1 = pos_x+ancho+50, pos_y-factor
    start_pos2 = pos1, pos_y
    end_pos2 = pos_x+ancho+50, pos_y
    start_pos1 = pos1, pos_y-factor
    end_pos1 = pos_x+ancho+50, pos_y-factor
    start_pos2 = pos1, pos_y
    end_pos2 = pos_x+ancho+50, pos_y
    start_pos3 = pos1, pos_y - factor*franja
    end_pos3 = pos_x+ancho+50, pos_y - factor*franja
    pygame.draw.line(screen, white, start_pos3, end_pos3, 1)
    Texto = style_1.render(
        'franja', True, white)
    screen.blit(Texto, (start_pos3[0]-60, start_pos3[1]))

    pygame.draw.line(screen, white, start_pos1, end_pos1, 1)
    Texto = style_1.render(
        '100%', True, white)
    screen.blit(Texto, (start_pos1[0]-60, start_pos1[1]))
    pygame.draw.line(screen, white, start_pos2, end_pos2, 1)
    Texto = style_1.render(
        '  0%', True, white)
    screen.blit(Texto, (start_pos2[0]-60, start_pos2[1]))
    width = 0
    pygame.draw.rect(screen, red, rect1, width)
    pygame.draw.rect(screen, green, rect2, width)
    Texto = style_1.render(
        texto1, True, white)
    screen.blit(Texto, (rx1, ry1+10))
    Texto = style_1.render(
        texto2, True, white)
    screen.blit(Texto, (rx2, ry2+10))


classifier = load_model(os.path.join(
    rt.checkpoints_resnet, "ResNet50_model_weights.h5"))
fps = 30
f_n, f_a = leertxt.leer(
    rt.n_ts_data_txt,
    rt.a_ts_data_txt
)
fp, fn, tp, tn, na = 0, 0, 0, 0, 0

path = "screens"

if paigeim:
    pygame.init()
    style_1 = pygame.font.SysFont("Arial", 30)
    screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
    pygame.display.set_caption("Testing Anomalous Detection - ResNet 50")
    for video in f_a:
        print(vars(video))
        try:
            os.mkdir(path)
        except:
            st.rmtree(path)
            os.mkdir(path)
        try:
            video_capture = cv2.VideoCapture(
                os.path.join(rt.anomalous_data_set, video.name))
            i = 0
            while True:  # fps._numFrames < 120

                for event in pygame.event.get():
                    if event.type == pygame.QUIT:
                        sys.exit()

                frame = video_capture.read()[1]  # get current frame
                # frameId = video_capture.get(1)  # current frame number
                f_name = os.path.join(
                    path, str(i)+"alpha.png")
                cv2.imwrite(filename=f_name,
                            img=frame)  # write frame image to file

                if not extract_texting.checkear(video.tramos_no_usar, fps, i):

                    if i % 10 == 0:
                        test_image = image.load_img(
                            f_name, target_size=(255, 255))
                        test_image = image.img_to_array(test_image)
                        test_image = np.expand_dims(test_image, axis=0)
                        result = classifier.predict(test_image)

                        screen.fill([0, 0, 0])
                        print(result)

                        data = [["anomalous", result[0][0]],
                                ["normal", result[0][1]]]
                        print(data)

                        Texto = style_1.render(
                            '%s (score = %.5f)' % (data[0][0], data[0][1]), True, white)
                        screen.blit(Texto, (text_pos_x, text_pos_y))
                        Texto = style_1.render(
                            '%s (score = %.5f)' % (data[1][0], data[1][1]), True, white)
                        screen.blit(Texto, (text_pos_x, text_pos_y+30))
                        Texto = style_1.render(
                            'frame = %d' % i, True, white)
                        screen.blit(Texto, (text_pos_x, text_pos_y+2*30))
                        grafica(screen, data, gf_pos_x, gf_pos_y, 0.60)
                        if data[0][1] >= 0.60:
                            pygame.draw.rect(
                                screen, red, (0, img_size_y+40, SCREEN_WIDTH, 60), 0)
                            if extract_texting.checkear(video.tramosAnomalos, fps, i):
                                tp += 1
                            else:
                                fp += 1
                        elif data[1][1] >= 0.60:
                            pygame.draw.rect(
                                screen, green, (0, img_size_y+40, SCREEN_WIDTH, 60), 0)
                            if extract_texting.checkear(video.tramosAnomalos, fps, i):
                                fn += 1
                            else:
                                tn += 1
                        else:
                            pygame.draw.rect(
                                screen, blue, (0, img_size_y+40, SCREEN_WIDTH, 60), 0)
                            na += 1
                    # print("\n")
                    res = 'fp: %i fn: %i tp: %i tn: %i na: %i total: %i' % (
                        fp, fn, tp, tn, na, fp + fn + tp + tn + na)
                    print(res, end="\r")
                    try:
                        imagen = pygame.image.load(f_name)
                        imagen = pygame.transform.scale(
                            imagen, (img_size_x, img_size_y))
                        screen.blit(imagen, (img_pos_x, img_pos_y))
                    except:
                        print(
                            "ERROR PYGAME ANOMALOUS+++++++++++++++++++++++++++++++++++")
                        break
                    pygame.display.flip()  # -------------------------------------
                    """ if writer is None:
                        # initialize our video writer
                        fourcc = cv2.VideoWriter_fourcc(*"XVID")
                        writer = cv2.VideoWriter("recognized.avi", fourcc, 30,
                                                (frame.shape[1], frame.shape[0]), True) """
                i += 1
                # write the output frame to disk
                # writer.write(frame)

                """ cv2.namedWindow('image', cv2.WINDOW_NORMAL)
                cv2.resizeWindow('image', 900, 900)
                cv2.imshow("image", frame)  # show frame in window
                cv2.waitKey(1)  # wait 1ms -> 0 until key input """
            # writer.release()
            video_capture.release()
            # cv2.destroyAllWindows()
        except:
            print("ERROR CV2 ANOMALOUS-----------------------------------")

    for video in f_n:
        print(vars(video))
        try:
            os.mkdir(path)
        except:
            st.rmtree(path)
            os.mkdir(path)
        try:
            video_capture = cv2.VideoCapture(
                os.path.join(rt.normal_data_set, video.name))
            i = 0
            while True:  # fps._numFrames < 120

                for event in pygame.event.get():
                    if event.type == pygame.QUIT:
                        sys.exit()

                frame = video_capture.read()[1]  # get current frame
                # frameId = video_capture.get(1)  # current frame number
                f_name = os.path.join(
                    path, str(i)+"alpha.png")
                cv2.imwrite(filename=f_name,
                            img=frame)  # write frame image to file
                if i % 10 == 0:
                    test_image = image.load_img(
                        f_name, target_size=(255, 255))
                    test_image = image.img_to_array(test_image)
                    test_image = np.expand_dims(test_image, axis=0)
                    result = classifier.predict(test_image)
                    print(result)
                    screen.fill([0, 0, 0])

                    data = [["anomalous", result[0][0]],
                            ["normal", result[0][1]]]

                    Texto = style_1.render(
                        '%s (score = %.5f)' % (data[0][0], data[0][1]), True, white)
                    screen.blit(Texto, (text_pos_x, text_pos_y))
                    Texto = style_1.render(
                        '%s (score = %.5f)' % (data[1][0], data[1][1]), True, white)
                    screen.blit(Texto, (text_pos_x, text_pos_y+30))
                    Texto = style_1.render(
                        'frame = %d' % i, True, white)
                    screen.blit(Texto, (text_pos_x, text_pos_y+2*30))
                    grafica(screen, data, gf_pos_x, gf_pos_y, 0.60)
                    if data[0][1] >= 0.60:
                        pygame.draw.rect(
                            screen, red, (0, img_size_y+40, SCREEN_WIDTH, 60), 0)
                        fp += 1
                    elif data[1][1] >= 0.60:
                        pygame.draw.rect(
                            screen, green, (0, img_size_y+40, SCREEN_WIDTH, 60), 0)
                        tn += 1
                    else:
                        pygame.draw.rect(
                            screen, blue, (0, img_size_y+40, SCREEN_WIDTH, 60), 0)
                        na += 1
                # print("\n")
                res = 'fp: %i fn: %i tp: %i tn: %i na: %i total: %i' % (
                    fp, fn, tp, tn, na, fp + fn + tp + tn + na)
                print(res, end="\r")
                try:
                    imagen = pygame.image.load(f_name)
                    imagen = pygame.transform.scale(
                        imagen, (img_size_x, img_size_y))
                    screen.blit(imagen, (img_pos_x, img_pos_y))
                except:
                    print(
                        "ERROR PYGAME NORMAL+++++++++++++++++++++++++++++++++++")
                    break
                pygame.display.flip()  # -------------------------------------
                """ if writer is None:
                    # initialize our video writer
                    fourcc = cv2.VideoWriter_fourcc(*"XVID")
                    writer = cv2.VideoWriter("recognized.avi", fourcc, 30,
                                            (frame.shape[1], frame.shape[0]), True) """
                i = i + 1
                # write the output frame to disk
                # writer.write(frame)

                """ cv2.namedWindow('image', cv2.WINDOW_NORMAL)
                cv2.resizeWindow('image', 900, 900)
                cv2.imshow("image", frame)  # show frame in window
                cv2.waitKey(1)  # wait 1ms -> 0 until key input """
            # writer.release()
            video_capture.release()
            # cv2.destroyAllWindows()
        except:
            print("ERROR CV2 NORMALES-----------------------------------")


# ------------------------------------------------------------------------------------------------------
