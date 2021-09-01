import os


class Directorios():
    anomalous_training = "Anomalous_training.txt"
    anomalous_testing = "Anomalous_testing.txt"
    normal_training = "Normales_training.txt"
    normal_testing = "Normales_testing.txt"

    def __init__(self, cwd=".."):
        # ------------------------GLOBAL PATHS---------------------
        self.data_set = os.path.join(cwd, "data_set")
        self.data_preparation = os.path.join(cwd, "data_preparation")
        self.testing = os.path.join(cwd, "testing")
        # -------------------MODELS-----------------------------------
        self.models = os.path.join(cwd, "models")
        self.resnet_model = os.path.join(self.models, "resnet")
        # ----------------RESNET CHECKPOINTS -----------------------------
        self.checkpoints_resnet = os.path.join(
            self.resnet_model, "checkpoints")
        # ----------------------------TXT----------------------------
        self.data_txt = os.path.join(cwd, "data_txt")
        self.anomalous_training_data_txt = os.path.join(
            self.data_txt, self.anomalous_training)
        self.anomalous_testing_data_txt = os.path.join(
            self.data_txt, self.anomalous_testing)
        self.normal_training_data_txt = os.path.join(
            self.data_txt, self.normal_training)
        self.normal_testing_data_txt = os.path.join(
            self.data_txt, self.normal_testing)
        # -------------------------DATA SET----------------------------
        self.anomalous_data_set = os.path.join(self.data_set, "Anomalous")
        self.normal_data_set = os.path.join(self.data_set, "Normal")
        # ------------------DATA TRAINING VALIDATION---------------------
        self.data_training_validation = os.path.join(
            cwd, "data_training_validation")
        self.data_temporal_normal = os.path.join(
            self.data_training_validation, "temporal_normal")
        self.data_temporal_anomalous = os.path.join(
            self.data_training_validation, "temporal_anomalous")
        self.data_training = os.path.join(
            self.data_training_validation, "training")
        self.data_validation = os.path.join(
            self.data_training_validation, "validation")
        self.data_training_anomalous = os.path.join(
            self.data_training, "anomalous")
        self.data_training_normal = os.path.join(self.data_training, "normal")
        self.data_validation_anomalous = os.path.join(
            self.data_validation, "anomalous")
        self.data_validation_normal = os.path.join(
            self.data_validation, "normal")
        # -------------------DATA BATCHS EPOCHS----------------------------
        self.batch_data = "batchs_data"
        self.epoch_data = "epochs_data"


# ----------------------------------------------

"""
import os

# ------------------------GLOBAL PATHS---------------------

cwd = ".."  # current path

data_set = os.path.join(cwd, "data_set")


data_preparation = os.path.join(cwd, "data_preparation")


testing = os.path.join(cwd, "testing")

# -------------------MODELS-----------------------------------

models = os.path.join(cwd, "models")

resnet_model = os.path.join(models, "resnet")

# ----------------RESNET CHECKPOINTS -----------------------------

checkpoints_resnet = os.path.join(resnet_model, "checkpoints")

# ----------------------------TXT----------------------------
anomalous_training = "Anomalous_training.txt"

anomalous_testing = "Anomalous_testing.txt"

normal_training = "Normales_training.txt"

normal_testing = "Normales_testing.txt"

data_txt = os.path.join(cwd, "data_txt")

anomalous_training_data_txt = os.path.join(data_txt, anomalous_training)

anomalous_testing_data_txt = os.path.join(data_txt, anomalous_testing)

normal_training_data_txt = os.path.join(data_txt, normal_training)

normal_testing_data_txt = os.path.join(data_txt, normal_testing)

# -------------------------DATA SET----------------------------

anomalous_data_set = os.path.join(data_set, "Anomalous")

normal_data_set = os.path.join(data_set, "Normal")

# ------------------DATA TRAINING VALIDATION---------------------

data_training_validation = os.path.join(cwd, "data_training_validation")


data_training = os.path.join(data_training_validation, "training")

data_validation = os.path.join(data_training_validation, "validation")

data_training_anomalous = os.path.join(data_training, "anomalous")

data_training_normal = os.path.join(data_training, "normal")

data_validation_anomalous = os.path.join(data_validation, "anomalous")

data_validation_normal = os.path.join(data_validation, "normal")

# ----------------------------------------------


"""
