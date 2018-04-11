package com.lqSPRimaging.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CameraUtil implements SurfaceHolder.Callback, PreviewCallback {

    public Handler handler = new Handler();
    private Camera myCamera;
    private SurfaceHolder mySurfaceHolder = null;
    private int[] pixels;
    private int width, height;
    private double[] result;
    private int mode = 0;
    public Runnable dataProcessing = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int[] data = getPixels();
            // System.out.println(data[5]);
            double[] resData = imageProcessing(mode, data);
            // System.out.println(Arrays.toString(result));
            setResult(resData);
        }
    };
    public Runnable task = new Runnable() {

        @Override
        public void run() {
            myCamera.setOneShotPreviewCallback(CameraUtil.this);

            handler.postDelayed(dataProcessing, 500);

            handler.postDelayed(task, 200);
        }
    };

    public CameraUtil(SurfaceView sv, Context ct) {
        initSurfaceView(sv, ct);

    }

    // Get the screen size
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);

    }

    private void openCamera(SurfaceHolder sfh) {
        myCamera = Camera.open();
        if (myCamera != null)
            try {

                myCamera.setPreviewDisplay(sfh);
                //myCamera.setDisplayOrientation(90);
                Parameters parm = myCamera.getParameters();

                parm.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                // —°‘Ò∫œ  µƒ‘§¿¿≥ﬂ¥Á
                List<Camera.Size> sizeList = parm.getSupportedPreviewSizes();

                if (sizeList.size() > 1) {
                    Iterator<Camera.Size> itor = sizeList.iterator();
                    while (itor.hasNext()) {
                        Camera.Size cur = itor.next();
                        Log.d("lqTest", "openCamera: width= " + cur.width + "----height= " + cur.height);

                        if (cur.width == 640) {
                            parm.setPreviewSize(640, cur.height);
                        }
                        /*if (cur.width == 1088) {
                            parm.setPreviewSize(1088, cur.height);
						}*/
                    }
                }

                myCamera.setParameters(parm);
                myCamera.startPreview();
                Size size = myCamera.getParameters().getPreviewSize();
                width = size.width;
                height = size.height;

                pixels = new int[width * height];
                // Initialize the pixcels array first
            } catch (IOException e) {
                // TODO Auto-generated catch block
                if (null != myCamera) {
                    myCamera.release();
                    myCamera = null;
                }
                e.printStackTrace();
            }
    }

    private void initSurfaceView(SurfaceView sv, Context ct) {
        System.out.println("initSurfaceView");
        mySurfaceHolder = sv.getHolder();
        mySurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mySurfaceHolder.addCallback(this);
        mySurfaceHolder.setKeepScreenOn(true);// The screen is always on

//Preview
        LayoutParams params = sv.getLayoutParams();
        Point p = getScreenMetrics(ct);
        params.width = p.x;
        params.height = p.x * 3 / 4;
        //params.height =params.width *height/width;
        //Log.d("lqTest", "initSurfaceView: params.width= "+params.width+"----params.height= "+params.height);

        sv.setLayoutParams(params);

    }

    public void lightOn() {
        Camera.Parameters myParam = myCamera.getParameters();
        myParam.setFlashMode(Parameters.FLASH_MODE_TORCH);
        myCamera.setParameters(myParam);
    }

    public void lightOff() {
        Camera.Parameters myParam = myCamera.getParameters();
        myParam.setFlashMode(Parameters.FLASH_MODE_OFF);
        myCamera.setParameters(myParam);
    }

    public void doTask(int mode) {
        this.mode = mode;

        handler.post(task);

        result = new double[mode];
        // System.out.println(pixels[5]);

    }

    public void cancelTask() {

        System.out.println("cancelTask()");
        handler.removeCallbacks(task);
    }

    public double[] imageProcessing(int mode, int[] data) {
        // TODO Auto-generated method stub
        switch (mode) {
            case 1:

                //return singleChannelIntensity(data);
                return greenChannelIntensity(data);
            case 2:

                return dualChannelIntensity(data);
            case 4:

                return fourChannelIntensity(data);

        }
        return null;

    }

    public double[] singleChannelIntensity(int[] data) {
        double singleRes[] = new double[1];
        singleRes[0] = getRectIntensity(data, 0, height, 0, width);
        return singleRes;
    }

    public double[] greenChannelIntensity(int[] data) {
        double singleRes[] = new double[1];
        singleRes[0] = getGreenChannelRectIntensity(data, 0, height, 0, width);
        return singleRes;
    }


    public double[] dualChannelIntensity(int data[]) {
        double doubleRes[];
		/*doubleRes[0] = getRectIntensity(data, height / 2, height, 0, width);
		doubleRes[1] = getRectIntensity(data, 0, height / 2, 0, width);*/

        doubleRes = getDualChannelIntensity(data, 0, height, 0, width);

        return doubleRes;
    }


    public double[] getDualChannelIntensity(int[] pixcelData, int m, int n, int p, int q) {

        int sumred = 0;
        int sumgreen = 0;

        double[] intensity = {0.0, 0.0};
        for (int i = m; i < n; i++) {
            for (int j = p; j < q; j++) {
                int grayscale = pixels[width * i + j];

                int red = ((grayscale & 0x00FF0000) >> 16);
                int green = ((grayscale & 0x0000FF00) >> 8);

                sumred = sumred + red;
                sumgreen = sumgreen + green;

                intensity[0] = (double) sumgreen / ((n - m) * (q - p));
                intensity[0] = Math.round(intensity[0] * 1000) / 1000.0;

                intensity[1] = (double) sumred / ((n - m) * (q - p));
                intensity[1] = Math.round(intensity[1] * 1000) / 1000.0;

            }
        }
        return intensity;
    }


/*	public double[] fourChannelIntensity(int[] data) {
		double fourRes[] = new double[4];
		fourRes[0] = getRectIntensity(data, height / 2, height, 0, width / 2);
		fourRes[1] = getRectIntensity(data, 0, height / 2, 0, width / 2);
		fourRes[2] = getRectIntensity(data, height / 2, height, width / 2,
				width);
		fourRes[3] = getRectIntensity(data, 0, height / 2, width / 2, width);
		return fourRes;
	}*/

    public double[] fourChannelIntensity(int[] data) {
        double fourRes[];
        //blue
		/*fourRes[0] = getRectIntensity(data, height / 2, height, 0, width / 2);

		//green
		fourRes[1] = getRectIntensity(data, 0, height / 2, 0, width / 2);

		//red
		fourRes[2] = getRectIntensity(data, height / 2, height, width / 2,
				width);

		//Intensity
		fourRes[3] = getRectIntensity(data, 0, height, 0, width);*/
        fourRes = getRGBIntensity(data, 0, height, 0, width);
        return fourRes;
    }

    // Light intensity calculation
    public double[] getRGBIntensity(int[] pixcelData, int m, int n, int p, int q) {
        int sum = 0;
        int sumred = 0;
        int sumgreen = 0;
        int sumblue = 0;

        double[] intensity = {0.0, 0.0, 0.0, 0.0};
        for (int i = m; i < n; i++) {
            for (int j = p; j < q; j++) {
                int grayscale = pixels[width * i + j];

                int red = ((grayscale & 0x00FF0000) >> 16);
                int green = ((grayscale & 0x0000FF00) >> 8);
                int blue = (grayscale & 0x000000FF);

                sumred = sumred + red;
                sumblue = sumblue + blue;
                sumgreen = sumgreen + green;

                grayscale = (red * 19595 + green * 38469 + blue * 7472) >> 16;
                sum = sum + grayscale;

                intensity[3] = (double) sum / ((n - m) * (q - p));
                intensity[3] = Math.round(intensity[3] * 1000) / 1000.0;

                intensity[0] = (double) sumblue / ((n - m) * (q - p));
                intensity[0] = Math.round(intensity[0] * 1000) / 1000.0;

                intensity[1] = (double) sumgreen / ((n - m) * (q - p));
                intensity[1] = Math.round(intensity[1] * 1000) / 1000.0;

                intensity[2] = (double) sumred / ((n - m) * (q - p));
                intensity[2] = Math.round(intensity[2] * 1000) / 1000.0;

            }
        }
        return intensity;
    }

    public double getRectIntensity(int[] pixcelData, int m, int n, int p, int q) {
        int sum = 0;
        double intensity = 0.0;
        for (int i = m; i < n; i++) {
            for (int j = p; j < q; j++) {
                int grayscale = pixels[width * i + j];

                int red = ((grayscale & 0x00FF0000) >> 16);
                int green = ((grayscale & 0x0000FF00) >> 8);
                int blue = (grayscale & 0x000000FF);

                grayscale = (red * 19595 + green * 38469 + blue * 7472) >> 16;
                sum = sum + grayscale;
                intensity = (double) sum / ((n - m) * (q - p));

                intensity = Math.round(intensity * 1000) / 1000.0;

            }
        }
        return intensity;
    }


    public double getGreenChannelRectIntensity(int[] pixcelData, int m, int n, int p, int q) {
        int sumG = 0;
        double intensity = 0.0;
        for (int i = m; i < n; i++) {
            for (int j = p; j < q; j++) {
                int grayscale = pixels[width * i + j];

                int green = ((grayscale & 0x0000FF00) >> 8);

                sumG = sumG + green;
                intensity = (double) sumG / ((n - m) * (q - p));

                intensity = Math.round(intensity * 1000) / 1000.0;

            }
        }
        return intensity;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        System.out.println("surfaceCreated");
        openCamera(mySurfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    public void releaseCamera() {
        if (mySurfaceHolder != null)
            CameraUtil.this.surfaceDestroyed(mySurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        System.out.println("surfaceDestroyed");
        if (null != myCamera) {
            System.out.println("surfaceDestroyed");
            // cancelTask();
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub

        YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height,
                null);
        if (image != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
            if (!image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
                return;
            }
            byte[] tmp = os.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

            int[] pixelsData = new int[width * height]; // Create a pixel array with the size of the bitmap

            bmp.getPixels(pixelsData, 0, width, 0, 0, width, height);
            // pixels = pixelsData;

            setPixels(pixelsData);
            System.out.println(pixels[5]);
        }
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public double[] getResult() {
        return result;
    }

    public void setResult(double[] result) {
        this.result = result;
    }

}
