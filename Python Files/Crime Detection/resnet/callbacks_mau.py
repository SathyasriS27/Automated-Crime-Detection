from keras.callbacks import Callback
import warnings  # ---
import numpy as np
from datetime import datetime
import os
import shutil as st

class BatchData(Callback):
    def __init__(self, filepath_batch, filepath_epoch,model_name):
        super(BatchData, self).__init__()
        self.filepath_batch = filepath_batch
        self.filepath_epoch = filepath_epoch
        self.model_name = model_name
        self.list_batchs = None
        self.list_epochs = None
        self.file_batch = None
        self.dia = None
        #self.file_epoch = None

    def on_train_begin(self, logs=None):
        logs = logs or {}
        self.dia = str(datetime.now())
        os.mkdir(os.path.join(self.filepath_batch,f"{self.model_name}_"+self.dia))
        #self.list_epochs = []
        #self.file_epoch = open(f"{self.filepath_epoch}/dia_{datetime.now()}.txt", "w")

    def on_epoch_begin(self, epoch, logs=None):
        logs = logs or {}
        self.list_batchs = []
        self.file_batch = open(f"{self.filepath_batch}/{self.model_name}_{self.dia}/epoch_{epoch}.txt", "w")

    def on_epoch_end(self, epoch, logs=None):
        logs = logs or {}
        # self.list_epochs.append(logs)
        print(logs)
        for i in self.list_batchs:
            acc = i.get("accuracy")
            if acc == None:
                i.get("acc")
            self.file_batch.write((str(acc)+"\t"+str(i.get("loss"))+"\n"))
        self.file_batch.close()

    def on_batch_begin(self, batch, logs=None):
        logs = logs or {}
        pass

    def on_batch_end(self, batch, logs=None):
        logs = logs or {}
        self.list_batchs.append(logs)

    def on_train_end(self, logs=None):
        logs = logs or {}
        #for i in self.list_epochs:
        #    self.file_epoch.write(
        #        (str(i.get("accuracy"))+"\t"+str(i.get("val_accuracy"))+"\t"+str(i.get("loss"))+"\t"+str(i.get("val_loss"))+"\n"))
        #self.file_epoch.close()
