from scipy.interpolate import make_interp_spline, BSpline
import numpy as np
import rutas_data_preparation as rt
import matplotlib.pyplot as plt
import os
#import seaborn as sns

cwd = ".."
paths = rt.Directorios(os.path.join(cwd, cwd))



def suavizar(x, y):
    if len(x) > 2:
        T = np.array(x)
        # 300 represents number of points to make between T.min and T.max
        x_suave = np.linspace(T.min(), T.max(), 300)
        spl = make_interp_spline(T, y, k=3)  # BSpline object
        y_suave = spl(x_suave)
        return x_suave, y_suave
    else:
        return x, y


def ReadData():
    #print(paths.batch_data)
    batchs1 = sorted(os.listdir(paths.batch_data))
    #print(batchs1)
    print(os.path.join(paths.batch_data,batchs1[-1]))
    batchs2 = sorted(os.listdir(os.path.join(paths.batch_data,batchs1[-1])))
    #print(batchs2)
    epochs = sorted(os.listdir(paths.epoch_data))
    #print(epochs)
    lista_acc = []
    lista_loss = []
    for epoch in batchs2:
        file = open(os.path.join(paths.batch_data,batchs1[-1],epoch), "r")
        lista_batch_acc = []
        lista_batch_loss = []
        for line in file:
            line = line.rstrip('\n').split('\t')
            lista_batch_acc.append(float(line[0]))
            lista_batch_loss.append(float(line[1]))
        lista_acc.append(tuple(lista_batch_acc))
        lista_loss.append(tuple(lista_batch_loss))
    file = open(paths.epoch_data+"/"+epochs[-1], "r")
    lista_epoch_acc = []
    lista_epoch_loss = []
    lista_epoch_val_acc = []
    lista_epoch_val_loss = []
    for line in file:
        line = line.rstrip('\n').split('\t')
        print(line)
        lista_epoch_acc.append(float(line[0]))
        lista_epoch_val_acc.append(float(line[1]))
        lista_epoch_loss.append(float(line[2]))
        lista_epoch_val_loss.append(float(line[3]))
    return tuple(lista_acc), tuple(lista_loss), tuple(lista_epoch_acc), tuple(lista_epoch_val_acc), tuple(lista_epoch_loss), tuple(lista_epoch_val_loss)


#lista_acc, lista_loss, lista_epoch_acc, lista_epoch_val_acc, lista_epoch_loss, lista_epoch_val_loss = ReadData()


def Multiplot(l1, tittle):
    largo = len(l1)
    if 1 <= largo <= 3:
        f = 1
        c = largo
    elif 4 <= largo <= 6:
        f = 2
        if largo == 4:
            c = 2
        else:
            c = 3
    elif largo in [7, 9]:
        f = 3
        c = 3
    elif largo == 8:
        f = 2
        c = 4
    else:
        f = 3
        c = 4
    plt.figure()
    for idx, i in enumerate(l1):
        batchs = [j for j in range(len(i))]
        #batchs, i = suavizar(batchs, i)
        #print(f,c,idx +1)
        plt.subplot(f, c, idx+1)
        plt.plot(batchs, i, 'r')
        plt.title(f'{tittle} {idx}')


def Epoch_plot(acc, val_acc, loss, val_loss):
    epochs = range(len(acc))
#    for a,va,l,vl in zip(acc,val_acc,loss,val_loss):
#        print(a,va,l,vl,"----")
    plt.figure()
    #e, acc = suavizar(epochs, acc)
    e, acc = epochs, acc
    plt.plot(e, acc, 'r')
    #e, val_acc = suavizar(epochs, val_acc)
    e, val_acc = epochs, val_acc
    plt.plot(e, val_acc, 'g')
    plt.title('Training(red) and validation(green) accuracy')

    plt.figure()
    #e, loss = suavizar(epochs, loss)
    e, loss = epochs, loss
    plt.plot(e, loss, 'r')
    #e, val_loss = suavizar(epochs, val_loss)
    e, val_loss = epochs, val_loss
    plt.plot(e, val_loss, 'g')
    plt.title('Training(red) and validation(green) loss')


def plot_training(history):

    acc = history.history['accuracy']
    val_acc = history.history['val_accuracy']
    loss = history.history['loss']
    val_loss = history.history['val_loss']
#    for a,va,l,vl in zip(acc,val_acc,loss,val_loss):
#        print(a,va,l,vl,"----")
    epochs = range(len(acc))
    plt.figure()
    #e, acc = suavizar(epochs, acc)
    e, acc = epochs, acc
    plt.plot(e, acc, 'r')
    #e, val_acc = suavizar(epochs, val_acc)
    e, val_acc = epochs, val_acc
    plt.plot(e, val_acc, 'g')
    plt.title('Training(red) and validation(green) accuracy plot')

    plt.figure()
    #e, loss = suavizar(epochs, loss)
    e, loss = epochs, loss
    plt.plot(e, loss, 'r')
    #e, val_loss = suavizar(epochs, val_loss)
    e, val_loss = epochs, val_loss
    plt.plot(e, val_loss, 'g')
    plt.title('Training(red) and validation(green) plot loss')


def main():
    lista_acc, lista_loss, lista_epoch_acc, lista_epoch_val_acc, lista_epoch_loss, lista_epoch_val_loss = ReadData()
    Multiplot(lista_acc, tittle="Accuracy Epoch")
    Multiplot(lista_loss, tittle="Loss Epoch")
    Epoch_plot(lista_epoch_acc, lista_epoch_val_acc,
               lista_epoch_loss, lista_epoch_val_loss)
    plt.show()


def Plot(history):
    lista_acc, lista_loss, lista_epoch_acc, lista_epoch_val_acc, lista_epoch_loss, lista_epoch_val_loss = ReadData()
    Multiplot(lista_acc, tittle="Accuracy Epoch")
    Multiplot(lista_loss, tittle="Loss Epoch")
    #plot_training(history)
    Epoch_plot(lista_epoch_acc, lista_epoch_val_acc,
               lista_epoch_loss, lista_epoch_val_loss)
    plt.show()


if __name__ == "__main__":
    main()

"""     T = np.array([6, 7, 8, 9, 10, 11, 12])
    power = np.array([1.53E+03, 5.92E+02, 2.04E+02,
                      7.24E+01, 2.72E+01, 1.10E+01, 4.70E+00])
    plt.plot(T, power)
    plt.figure()

    # 300 represents number of points to make between T.min and T.max
    xnew = np.linspace(T.min(), T.max(), 300)
    spl = make_interp_spline(T, power, k=3)  # BSpline object
    power_smooth = spl(xnew)
    plt.plot(xnew, power_smooth)
    plt.show() """
