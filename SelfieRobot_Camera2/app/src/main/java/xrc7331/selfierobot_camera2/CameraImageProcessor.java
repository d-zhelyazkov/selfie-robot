package xrc7331.selfierobot_camera2;

import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import xrc7331.selfierobot_camera2.opencv.ColorConverter;
import xrc7331.selfierobot_camera2.opencv.ExMat;
import xrc7331.selfierobot_camera2.opencv.ImageProcessor;
import xrc7331.selfierobot_camera2.opencv.Thresholder;
import xrc7331.selfierobot_camera2.tools.observer.Observable;
import xrc7331.selfierobot_camera2.tools.observer.Observer;
import xrc7331.selfierobot_camera2.tools.threshold_finder.LightThresholdFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by XRC_7331 on 3/31/2016.
 */
public class CameraImageProcessor implements Observable<CameraImageProcessor> {
    private static final int INPUT_PERMITS = 1;
    private static final Size BLUR_KERNEL = new Size(21, 21);

    private static CameraImageProcessor INSTANCE = new CameraImageProcessor();

    public static CameraImageProcessor getInstance() {
        return INSTANCE;
    }

    private final Size MORPHOLOGY_KERNEL_SIZE = new Size(15, 15);
    private final ColorConverter COLOR_CONVERTER = ColorConverter.getInstance();
    private final ImageProcessor IMAGE_PROCESSOR = ImageProcessor.getInstance();
    private final Thresholder THRESHOLDER = Thresholder.getInstance();

    private Mat morphologyKernel;

    private LockedMat[] imageBuffer;
    private List<List<Point>> extractedContourCentroids;
    //    private ReentrantLock newInputAvailable = new ReentrantLock();
    private Semaphore newInputAvailable = new Semaphore(0);
    private List<Observer<CameraImageProcessor>> listeners = new LinkedList<>();

    private Semaphore blueImageAvailable = new Semaphore(0);
    private Semaphore satImageAvailable = new Semaphore(0);
    private Semaphore threadWorkDone = new Semaphore(0);
    private Runnable thread1 = new Runnable() {
        @Override
        public void run() {
            try {
                LockedMat startImage = imageBuffer[ImageType.INPUT_COPY.ordinal()];
                ExMat bgrImage = COLOR_CONVERTER.convert(startImage.mat, ExMat.ColorType.BGR);

                List<ExMat> bgrChannels = IMAGE_PROCESSOR.splitChannels(bgrImage);
                ExMat blueImage = bgrChannels.get(0);
                imageBuffer[ImageType.BLUE.ordinal()].setMat(blueImage);
                blueImageAvailable.release();

                ExMat redImage = bgrChannels.get(2);
                imageBuffer[ImageType.RED.ordinal()].setMat(redImage);
                bgrChannels.get(1).release();

                satImageAvailable.acquire();
                ExMat satImage = imageBuffer[ImageType.SAT.ordinal()].mat;
                ExMat redSatImage = IMAGE_PROCESSOR.and(redImage, satImage);
                imageBuffer[ImageType.RED_SAT.ordinal()].setMat(redSatImage);
                ExMat redSatBinImage = convertToBinary(redSatImage);
                imageBuffer[ImageType.RED_SAT_BIN.ordinal()].setMat(redSatBinImage);

                List<Point> contourCentroids = IMAGE_PROCESSOR.extractContourCentroids(redSatBinImage);
                extractedContourCentroids.set(ContourType.RED.ordinal(), contourCentroids);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (CvException e) {
                e.printStackTrace();
            }

            threadWorkDone.release();
        }
    };
    private Runnable thread2 = new Runnable() {
        @Override
        public void run() {
            try {
                LockedMat startImage = imageBuffer[ImageType.INPUT_COPY.ordinal()];
                ExMat hsvImage = COLOR_CONVERTER.convert(startImage.mat, ExMat.ColorType.HSV);

                List<ExMat> hsvChannels = IMAGE_PROCESSOR.splitChannels(hsvImage);
                ExMat satImage = hsvChannels.get(1);
                imageBuffer[ImageType.SAT.ordinal()].setMat(satImage);
                satImageAvailable.release();
                ExMat valImage = hsvChannels.get(2);
                imageBuffer[ImageType.VALUE.ordinal()].setMat(valImage);
                hsvChannels.get(0).release();

                blueImageAvailable.acquire();
                ExMat blueImage = imageBuffer[ImageType.BLUE.ordinal()].mat;
                ExMat blueSatImage = IMAGE_PROCESSOR.and(blueImage, satImage);
                imageBuffer[ImageType.BLUE_SAT.ordinal()].setMat(blueSatImage);
//                ExMat satValueImage = IMAGE_PROCESSOR.and(satImage, valImage);
//                imageBuffer[ImageType.SAT_VALUE.ordinal()].setMat(satValueImage);
                ExMat blueSatBinImage = convertToBinary(blueSatImage);
                imageBuffer[ImageType.BLUE_SAT_BIN.ordinal()].setMat(blueSatBinImage);

                List<Point> contourCentroids = IMAGE_PROCESSOR.extractContourCentroids(blueSatBinImage);
                extractedContourCentroids.set(ContourType.BLUE.ordinal(), contourCentroids);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (CvException e) {
                e.printStackTrace();
            }

            threadWorkDone.release();
        }
    };

    private CameraImageProcessor() {
        ImageType[] imageTypes = ImageType.values();
        imageBuffer = new LockedMat[imageTypes.length];
        for (int i = 0; i < imageTypes.length; i++)
            imageBuffer[i] = new LockedMat();

        imageBuffer[ImageType.INPUT.ordinal()].lock.release();
        int contourTypeCount = ContourType.values().length;
        List<List<Point>> emptyList = new ArrayList(contourTypeCount);
        for (int i = 0; i < contourTypeCount; i++)
            emptyList.add(i, Collections.<Point>emptyList());
        extractedContourCentroids = Collections.synchronizedList(emptyList);

    }

    public void setInputImage(ExMat mat) throws InterruptedException {
//        LockedMat inputImage = imageBuffer[ImageType.INPUT.ordinal()];
//        inputImage.lock.acquire(INPUT_PERMITS);
//
//        if (inputImage.mat != null)
//            inputImage.mat.release();
//
//        inputImage.mat = new ExMat(mat);
//        inputImage.lock.release(INPUT_PERMITS);
        imageBuffer[ImageType.INPUT.ordinal()].setMat(new ExMat(mat));
//        newInputAvailable.unlock();
        if (newInputAvailable.availablePermits() == 0)
            newInputAvailable.release();
    }

    public ExMat getImage(ImageType imageType) throws InterruptedException {
        LockedMat lockedMat = imageBuffer[imageType.ordinal()];
        lockedMat.lock.acquire();

        ExMat result = null;
        try {
            if (lockedMat.mat != null)
                result = new ExMat(lockedMat.mat);
        } catch (CvException e) {
            e.printStackTrace();
        }

        lockedMat.lock.release();
        return result;
    }

    public List<Point> getExtractedContourCentroids(ContourType contourType) {
        return extractedContourCentroids.get(contourType.ordinal());
    }

    public boolean hasUnprocessedImage() {
        return (newInputAvailable.availablePermits() > 0);
//        return !newInputAvailable.isLocked();
    }

    public synchronized void process() throws InterruptedException {
//        newInputAvailable.lock();
        newInputAvailable.acquire();
        ExMat inputImage = null;
        while (inputImage == null) {
            //imageBuffer[ImageType.INPUT.ordinal()].lock.acquire(INPUT_PERMITS);
            inputImage = getImage(ImageType.INPUT);
        }
        ExMat blurredImage = new ExMat(inputImage.getColorType());
        Imgproc.blur(inputImage, blurredImage, BLUR_KERNEL);
        //inputImage.release();
        imageBuffer[ImageType.BLURRED.ordinal()].setMat(blurredImage);
        imageBuffer[ImageType.INPUT_COPY.ordinal()].setMat(inputImage);

        new Thread(thread1, "BGR_RED_Thread").start();
        new Thread(thread2, "HSV_BLUE_Thread").start();
        threadWorkDone.acquire(2);

        this.notifyObservers();
//        ExMat blueSatBinImage = imageBuffer[ImageType.BLUE_SAT_BIN.ordinal()].mat;
//        ExMat redSatBinImage = imageBuffer[ImageType.RED_SAT_BIN.ordinal()].mat;
//        ExMat resultImage = IMAGE_PROCESSOR.or(blueSatBinImage, redSatBinImage);
//        imageBuffer[ImageType.RESULT.ordinal()].setMat(resultImage);

        //return resultImage.clone();
    }

    private ExMat convertToBinary(ExMat image) {
        ExMat binaryImage = THRESHOLDER.autoThreshold(image, LightThresholdFinder.getInstance());

        if (morphologyKernel == null)
            morphologyKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, MORPHOLOGY_KERNEL_SIZE);

//        Imgproc.morphologyEx(binaryImage, binaryImage, Imgproc.MORPH_OPEN, morphologyKernel);
//        Imgproc.morphologyEx(binaryImage, binaryImage, Imgproc.MORPH_CLOSE, morphologyKernel);
        Imgproc.dilate(binaryImage, binaryImage, morphologyKernel);

        return binaryImage;
        //return image;
    }

    @Override
    public void addObserver(Observer<CameraImageProcessor> observer) {
        listeners.add(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer<CameraImageProcessor> listener : listeners)
            listener.updated(this);
    }


    public enum ImageType {
        INPUT("input"),
        INPUT_COPY("input"),
        BLUE("blue"),
        RED("red"),
        SAT("sat"),
        VALUE("value"),
        BLUE_SAT("blue_sat"),
        BLUE_SAT_BIN("blue_sat_bin"),
        RED_SAT("red_sat"),
        RED_SAT_BIN("red_sat_bin"),
        RESULT("result"),
        BLURRED("blurred");

        String name;

        ImageType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ContourType {
        RED,
        BLUE
    }

    class LockedMat {
        Semaphore lock = new Semaphore(1);
        ExMat mat;

        void setMat(ExMat mat) throws InterruptedException {
            if (mat == null)
                return;
            
            lock.acquire();
            if (this.mat != null)
                this.mat.release();

            this.mat = mat;
            lock.release();
        }
    }
}


