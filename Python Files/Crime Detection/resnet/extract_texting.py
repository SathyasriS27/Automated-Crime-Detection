import os
import shutil as st
import cv2
import numpy as np

def checkear(tramos, fps, frame):
    res = False
    i = 0
    while i < len(tramos) and not res:
        res = res or (int(tramos[i].inicio)*fps <= frame <=
                      (int(tramos[i].inicio) + int(tramos[i].duracion))*fps)
        i += 1
    return res

""" 
def video_extract(video, src, num_vid=0, conta_a=0, conta_n=0, n_frames_a=6, n_frames_n=4, aug=False):

    frame = 0
    n_frames_n, n_frames_a = 30//n_frames_n, 30//n_frames_a
    fps = 30

    cap = cv2.VideoCapture(src)
    # length = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    if bool(video.type):
        # print('Total Frame Count:', length )
        while True:
            check, img = cap.read()
            res = 'Tipo: %i Video: %i Processed: %i Imagenes_N: %i Imagenes_A: %i' % (
                0, num_vid, frame, conta_n, conta_a)
            if check:
                if checkear(video.tramosAnomalos, fps, frame) and not checkear(video.tramos_no_usar, fps, frame):
                    if frame % n_frames_a == 0:
                        # img = cv2.resize(img, (1920 // factor, 1080 // factor))
                        img = cv2.resize(img, (255, 255))
                        pat = os.path.join(rt.inception_v3_data_tr_a,
                                           str(conta_a) + ".jpg")
                        cv2.imwrite(
                            pat,
                            img
                        )
                        if aug:
                            conta_a += daug.img_aug(
                                pat,
                                img,
                                rt.inception_v3_data_tr_a,
                                conta_a
                            )
                        conta_a += 1
                        t = 1
                        res = 'Tipo: %s Video: %i Processed: %i Imagenes_N: %i Imagenes_A: %i' % (
                            t, num_vid, frame, conta_n, conta_a)
                        print(res, end="\r")
                else:
                    if frame % n_frames_n == 0:
                        # img = cv2.resize(img, (1920 // factor, 1080 // factor))
                        img = cv2.resize(img, (255, 255))
                        pat = os.path.join(
                            rt.inception_v3_data_tr_n, str(conta_n) + ".jpg")
                        t = 0
                        cv2.imwrite(
                            pat,
                            img
                        )
                        if aug:
                            conta_n += daug.img_aug(
                                pat,
                                img,
                                rt.inception_v3_data_tr_n,
                                conta_n
                            )
                        conta_n += 1
                        t = 1
                        res = 'Tipo: %i Video: %i Processed: %i Imagenes_N: %i Imagenes_A: %i' % (
                            t, num_vid, frame, conta_n, conta_a)
                        print(res, end="\r")
                frame += 1

            else:
                print(res)
                break
    else:
        while True:
            check, img = cap.read()
            res = 'Tipo: %i Video: %i Processed: %i Imagenes_N: %i Imagenes_A: %i' % (
                0, num_vid, frame, conta_n, conta_a)
            if check:
                # if fps*ini <= frame <= fps*fin and frame > 0:
                if frame % n_frames_n == 0:
                    img = cv2.resize(img, (255, 255))
                    pat = os.path.join(
                        rt.inception_v3_data_tr_n, str(conta_n) + ".jpg")
                    t = 0
                    cv2.imwrite(
                        pat,
                        img
                    )
                    if aug:
                        conta_n += daug.img_aug(
                            pat,
                            img,
                            rt.inception_v3_data_tr_n,
                            conta_n
                        )

                    conta_n += 1

                    t = 1

                    res = 'Tipo: %i Video: %i Processed: %i Imagenes_N: %i Imagenes_A: %i' % (
                        0, num_vid, frame, conta_n, conta_a)
                    print(res, end="\r")
                frame += 1
            else:
                print(res)
                break
    cap.release()
    return conta_a, conta_n
 """

""" if __name__ == '__main__':
    video_extract('p2.avi', 2, 8) """
