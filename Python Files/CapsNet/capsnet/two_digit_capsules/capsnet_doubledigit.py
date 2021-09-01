"""
Keras implementation of CapsNet in Hinton's paper Dynamic Routing Between Capsules.
The current version maybe only works for TensorFlow backend. Actually it will be straightforward to re-write to TF code.
Adopting to other backends should be easy, but I have not tested this. 
Usage:
       python capsnet/twodigitcapsules/cifar_capsulenet.py
       python capsnet/twodigitcapsules/cifar_capsulenet.py --epochs 50
       python capsnet/twodigitcapsules/cifar_capsulenet.py --epochs 50 --routings 3
       python capsnet/twodigitcapsules/cifar_capsulenet.py --primary_capsules=32 --number_of_primary_channels=16 --digit_capsules=16
       ... ...
       
Result:
    Validation accuracy > 99.5% after 20 epochs. Converge to 99.66% after 50 epochs.
    About 110 seconds per epoch on a single GTX1070 GPU card
    
Author: Xifeng Guo, E-mail: `guoxifeng1990@163.com`, Github: `https://github.com/XifengGuo/CapsNet-Keras`
"""

import os
import argparse

import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
from tensorflow import keras
from PIL import Image

from dataset import cifar10
from capsnet.twodigitcapsules.capsulelayers import CapsuleLayer, PrimaryCap, Length, Mask
from capsnet.twodigitcapsules.utils import combine_images, plot_log

keras.backend.set_image_data_format('channels_last')


def CapsNet(input_shape, n_class, routings, primary_capsules=16, number_of_primary_channels=32, digit_capsules=16):
    """
    A Capsule Network on CIFAR.
    :param input_shape: data shape, 3d, [width, height, channels]
    :param n_class: number of classes
    :param routings: number of routing iterations
    :return: Two Keras Models, the first one used for training, and the second one for evaluation.
            `eval_model` can also be used for training.
    """
    x = keras.layers.Input(shape=input_shape)

    # Layer 1: Just a conventional Conv2D layer
    conv1 = keras.layers.Conv2D(filters=256, kernel_size=5, strides=2, padding='valid', name='conv1', kernel_regularizer=keras.regularizers.l2(1.e-4))(x)
    norm = keras.layers.BatchNormalization(axis=3)(conv1)
    conv1 = keras.layers.Activation('relu')(norm)

    # Layer 2: Conv2D layer with `squash` activation, then reshape to [None, num_capsule, dim_capsule]
    primarycaps = PrimaryCap(conv1, dim_capsule=32, n_channels=16, kernel_size=5, strides=2, padding='valid')

    # Layer 2: Conv2D layer with `squash` activation, then reshape to [None, num_capsule, dim_capsule]
    primarycaps = PrimaryCap(primarycaps, dim_capsule=32, n_channels=16, kernel_size=5, strides=2, padding='valid', do_reshape=True)

    # Layer 3: Capsule layer. Routing algorithm works here.
    digitcaps = CapsuleLayer(num_capsule=n_class, dim_capsule=digit_capsules, routings=routings, name='digitcaps')(primarycaps)

    # Layer 4: This is an auxiliary layer to replace each capsule with its length. Just to match the true label's shape.
    # If using tensorflow, this will not be necessary. :)
    out_caps = Length(name='capsnet')(digitcaps)

    # Decoder network.
    y = keras.layers.Input(shape=(n_class,))
    masked_by_y = Mask()([digitcaps, y])  # The true label is used to mask the output of capsule layer. For training
    masked = Mask()(digitcaps)  # Mask using the capsule with maximal length. For prediction

    # Shared Decoder model in training and prediction
    decoder = keras.models.Sequential(name='decoder')
    decoder.add(keras.layers.Dense(1024, activation='relu', input_dim=digit_capsules*n_class))
    decoder.add(keras.layers.Dense(1024, activation='relu'))
    decoder.add(keras.layers.Dense(np.prod(input_shape), activation='sigmoid'))
    decoder.add(keras.layers.Reshape(target_shape=input_shape, name='out_recon'))

    # Models for training and evaluation (prediction)
    train_model = keras.models.Model([x, y], [out_caps, decoder(masked_by_y)])
    eval_model = keras.models.Model(x, [out_caps, decoder(masked)])

    # manipulate model
    noise = keras.layers.Input(shape=(n_class, digit_capsules))
    noised_digitcaps = keras.layers.Add()([digitcaps, noise])
    masked_noised_y = Mask()([noised_digitcaps, y])
    manipulate_model = keras.models.Model([x, y, noise], decoder(masked_noised_y))
    return train_model, eval_model, manipulate_model


def margin_loss(y_true, y_pred):
    """
    Margin loss for Eq.(4). When y_true[i, :] contains not just one `1`, this loss should work too. Not test it.
    :param y_true: [None, n_classes]
    :param y_pred: [None, num_capsule]
    :return: a scalar loss value.
    """
    L = y_true * tf.square(tf.maximum(0., 0.9 - y_pred)) + \
        0.5 * (1 - y_true) * tf.square(tf.maximum(0., y_pred - 0.1))

    return tf.reduce_mean(tf.reduce_sum(L, 1))


def train(model, args):
    """Training a CapsuleNet.
    :param model: the CapsuleNet model
    :param args: arguments
    :return: The trained model
    """
    # Setup callbacks
    log = keras.callbacks.CSVLogger(args.save_dir + '/log.csv')
    tb = keras.callbacks.TensorBoard(log_dir=args.save_dir + '/tensorboard-logs',
                                     batch_size=args.batch_size, histogram_freq=int(args.debug))
    checkpoint = keras.callbacks.ModelCheckpoint(args.save_dir + '/weights-{epoch:02d}.h5', monitor='val_capsnet_acc',
                                                 save_best_only=True, save_weights_only=True, verbose=1)
    lr_decay = keras.callbacks.LearningRateScheduler(schedule=lambda epoch: args.lr * (args.lr_decay ** epoch))

    # Compile the model
    model.compile(optimizer=keras.optimizers.Adam(lr=args.lr),
                  loss=[margin_loss, 'mse'],
                  loss_weights=[1., args.lam_recon],
                  metrics={'capsnet': 'accuracy'})

    # Training
    model.fit_generator(generator=cifar10.get_train_generator_for_capsnet(args.batch_size),
                        steps_per_epoch=int(cifar10.TRAIN_SIZE / args.batch_size),
                        epochs=args.epochs,
                        validation_data=cifar10.get_validation_data_for_capsnet(),
                        callbacks=[log, tb, checkpoint, lr_decay])

    model.save_weights(args.save_dir + '/trained_model.h5')
    print('Trained model saved to \'%s/trained_model.h5\'' % args.save_dir)

    plot_log(args.save_dir + '/log.csv', show=True)

    return model


def test(model, args):
    x_test, y_test = cifar10.get_test_data()
    y_pred, x_recon = model.predict(x_test, batch_size=100)
    print('-'*30 + 'Begin: test' + '-'*30)
    print('Test acc:', np.sum(np.argmax(y_pred, 1) == np.argmax(y_test, 1))/y_test.shape[0])

    img = combine_images(np.concatenate([x_test[:50],x_recon[:50]]))
    image = img * 255
    Image.fromarray(image.astype(np.uint8)).save(args.save_dir + "/real_and_recon.png")
    print()
    print('Reconstructed images are saved to %s/real_and_recon.png' % args.save_dir)
    print('-' * 30 + 'End: test' + '-' * 30)
    plt.imshow(plt.imread(args.save_dir + "/real_and_recon.png"))
    plt.show()


def manipulate_latent(model, args):
    print('-'*30 + 'Begin: manipulate' + '-'*30)
    x_test, y_test = cifar10.get_test_data()
    index = np.argmax(y_test, 1) == args.digit
    number = np.random.randint(low=0, high=sum(index) - 1)
    x, y = x_test[index][number], y_test[index][number]
    x, y = np.expand_dims(x, 0), np.expand_dims(y, 0)
    noise = np.zeros([1, 10, 16])
    x_recons = []
    for dim in range(16):
        for r in [-0.25, -0.2, -0.15, -0.1, -0.05, 0, 0.05, 0.1, 0.15, 0.2, 0.25]:
            tmp = np.copy(noise)
            tmp[:,:,dim] = r
            x_recon = model.predict([x, y, tmp])
            x_recons.append(x_recon)

    x_recons = np.concatenate(x_recons)

    img = combine_images(x_recons, height=16)
    image = img*255
    Image.fromarray(image.astype(np.uint8)).save(args.save_dir + '/manipulate-%d.png' % args.digit)
    print('manipulated result saved to %s/manipulate-%d.png' % (args.save_dir, args.digit))
    print('-' * 30 + 'End: manipulate' + '-' * 30)


if __name__ == "__main__":
    # setting the hyper parameters
    parser = argparse.ArgumentParser(description="Capsule Network on MNIST.")
    parser.add_argument('--epochs', default=50, type=int)
    parser.add_argument('--batch_size', default=100, type=int)
    parser.add_argument('--primary_capsules', default=16, type=int)
    parser.add_argument('--number_of_primary_channels', default=32, type=int)
    parser.add_argument('--digit_capsules', default=16, type=int)
    parser.add_argument('--lr', default=0.01, type=float,
                        help="Initial learning rate")
    parser.add_argument('--lr_decay', default=0.99, type=float,
                        help="The value multiplied by lr at each epoch. Set a larger value for larger epochs")
    parser.add_argument('--lam_recon', default=0.512, type=float,
                        help="The coefficient for the loss of decoder")
    parser.add_argument('-r', '--routings', default=9, type=int,
                        help="Number of iterations used in routing algorithm. should > 0")
    parser.add_argument('--debug', action='store_true',
                        help="Save weights by TensorBoard")
    parser.add_argument('--save_dir', default='./result')
    parser.add_argument('-t', '--testing', action='store_true',
                        help="Test the trained model on testing dataset")
    parser.add_argument('--digit', default=5, type=int,
                        help="Digit to manipulate")
    parser.add_argument('-w', '--weights', default=None,
                        help="The path of the saved weights. Should be specified when testing")
    args = parser.parse_args()
    print(args)

    if not os.path.exists(args.save_dir):
        os.makedirs(args.save_dir)

    # define model
    model, eval_model, manipulate_model = CapsNet(input_shape=cifar10.IMAGE_SHAPE,
                                                  n_class=cifar10.CLASSES,
                                                  routings=args.routings,
                                                  primary_capsules=args.primary_capsules,
                                                  number_of_primary_channels=args.number_of_primary_channels,
                                                  digit_capsules=args.digit_capsules)
    model.summary()

    # train or test
    if args.weights is not None:  # init the model weights with provided one
        model.load_weights(args.weights)
    if not args.testing:
        train(model=model, args=args)
    else:  # as long as weights are given, will run testing
        if args.weights is None:
            print('No weights are provided. Will test using random initialized weights.')
        manipulate_latent(manipulate_model, args)
        test(model=eval_model, args=args)