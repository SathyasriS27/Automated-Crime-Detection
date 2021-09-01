import os

# ------------------------GLOBAL PATHS---------------------
# cwd = os.getcwd()  # current path
# cwd = "/home/mauss/Documentos/TESIS/src"  # current path
cwd = "../.."  # current path
data_set = os.path.join(
    cwd,
    "data_set"
)
data_txt = os.path.join(
    cwd,
    "data_txt"
)
data_preparation = os.path.join(
    cwd,
    "data_preparation"
)
data_train_val = os.path.join(
    cwd,
    "data_training_validation"
)
models = os.path.join(
    cwd,
    "models"
)
testing = os.path.join(
    cwd,
    "testing"
)
# -------------------MODELS-----------------------------------
inception_v3_model = os.path.join(
    models,
    "inception_v3"
)
resnet_model = os.path.join(
    models,
    "resnet"
)

# ----------------INCEOTION MODEL ---------------------------
tf_files_inception_v3_model = os.path.join(
    inception_v3_model,
    "tf_files"
)

# ----------------RESNET MODEL -----------------------------
checkpoints_resnet = os.path.join(
    resnet_model,
    "checkpoints"
)


# ----------------------------TXT----------------------------
a_training = "Anomalous_training.txt"
a_testing = "Anomalous_testing.txt"
n_training = "Normales_training.txt"
n_testing = "Normales_testing.txt"

a_tr_data_txt = os.path.join(
    data_txt,
    a_training
)
a_ts_data_txt = os.path.join(
    data_txt,
    a_testing
)
n_tr_data_txt = os.path.join(
    data_txt,
    n_training
)
n_ts_data_txt = os.path.join(
    data_txt,
    n_testing
)
# -------------------------DATA SET----------------------------
anomalous_data_set = os.path.join(
    data_set,
    "Anomalous"
)
print(anomalous_data_set)
normal_data_set = os.path.join(
    data_set,
    "Normal"
)
print(normal_data_set)
# ------------------DATA TRAINING VALIDATION---------------------
inception_v3_data_training = os.path.join(
    data_train_val,
    "inception_data_training"
)
resnet_data_training = os.path.join(
    data_train_val,
    "resnet_data_training"
)
resnet_data_validation = os.path.join(
    data_train_val,
    "resnet_data_validation"
)
inception_v3_data_tr_a = os.path.join(
    inception_v3_data_training,
    "anomalous"
)
inception_v3_data_tr_n = os.path.join(
    inception_v3_data_training,
    "normal"
)
resnet_data_tr_a = os.path.join(
    resnet_data_training,
    "anomalous"
)
resnet_data_tr_n = os.path.join(
    resnet_data_training,
    "normal"
)
resnet_data_val_a = os.path.join(
    resnet_data_validation,
    "anomalous"
)
resnet_data_val_n = os.path.join(
    resnet_data_validation,
    "normal"
)
# ----------------------------------------------
