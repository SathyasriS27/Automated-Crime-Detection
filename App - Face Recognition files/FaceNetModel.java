package com.openlab.humanpokedex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.os.Environment;
import android.text.format.Time;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.util.ArrayList;

public class FaceNetModel {

    private Interpreter interpreter;
    private int imgSize = 300, embeddingDim = 128;

    private ImageProcessor imageTensorProcessor = new ImageProcessor.Builder().add(new ResizeOp(imgSize, imgSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(new NormalizeOp(127.5f, 127.5f)).build();

    public FaceNetModel(Context context) {
        Interpreter.Options interpreterOptions = new Interpreter.Options().setNumThreads(4);
        interpreter = new Interpreter(FileUtil.loadMappedFile(context, ""), interpreterOptions);
    }

    private ArrayList<Float> getFaceEmbedding(Bitmap image , Rect crop, Boolean preRotate, Boolean isRearCameraOn) {
        return runFaceNet(convertBitmapToBuffer(cropRectFromBitmap(image, crop, preRotate, isRearCameraOn)));
    }

    private ArrayList<Float> runFaceNet(ByteBuffer buffer) {
        long t1 = System.currentTimeMillis();
        ArrayList<Float> output = new ArrayList<>();
        interpreter.run(buffer, output);
        return output;
    }

    private ByteBuffer convertBitmapToBuffer(Bitmap image) {
        TensorImage imageTensor = imageTensorProcessor.process(TensorImage.fromBitmap(image));
        return imageTensor.getBuffer();
    }

    private Bitmap cropRectFromBitmap(Bitmap source, Rect rect, Boolean preRotate, Boolean isRearCameraOn) {
        Bitmap croppedBitmap;
        int width = rect.width();
        int height = rect.height();

        if ((rect.left + width) > source.getWidth()){
            width = (source.getWidth() - rect.left);
        }

        if ((rect.top + height) > source.getHeight()){
            height = (source.getHeight() - rect.top);
        }

        if (preRotate) {
            croppedBitmap = Bitmap.createBitmap(rotateBitmap(source, -90f));
        } else {
            croppedBitmap = Bitmap.createBitmap(source, rect.left, rect.top, width, height);
        }

        // Add a 180 degrees rotation if the rear camera is on.
        if (isRearCameraOn) {
            croppedBitmap = rotateBitmap(croppedBitmap, 180f);
        }

        // Uncomment the below line if you want to save the input image.
        // Make sure the app has the `WRITE_EXTERNAL_STORAGE` permission.
        //saveBitmap( croppedBitmap , "image")

        return croppedBitmap;
    }

    private void saveBitmap(Bitmap image, String name) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + name + ".jpg"));
        image.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
    }

    private Bitmap rotateBitmap(Bitmap source, Float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
    }
}
