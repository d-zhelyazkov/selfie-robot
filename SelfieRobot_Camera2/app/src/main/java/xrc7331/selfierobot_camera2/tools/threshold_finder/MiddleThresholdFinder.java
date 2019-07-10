package xrc7331.selfierobot_camera2.tools.threshold_finder;

/**
 * Created by XRC_7331 on 3/14/2016.
 */
public class MiddleThresholdFinder implements ThresholdFinder {
    private static MiddleThresholdFinder ourInstance = new MiddleThresholdFinder();

    public static MiddleThresholdFinder getInstance() {
        return ourInstance;
    }

    private final OtsuThresholdFinder OTSU_THRESHOLD_FINDER = OtsuThresholdFinder.getInstance();

    private MiddleThresholdFinder() {
    }

    @Override
    public int findThreshold(int[] histogram) {
        int halfSize = (histogram.length >> 1);
        int quarterSize = halfSize >> 1;
        int[] middleHistogram = new int[halfSize];
        System.arraycopy(histogram, quarterSize, middleHistogram, 0, halfSize);
        return OTSU_THRESHOLD_FINDER.findThreshold(middleHistogram) + quarterSize;

    }


//    @Override
//    public int findThreshold(int[] histogram) {
//        int halfSize = histogram.length >> 1;
//        int quarterSize = halfSize >> 1;
//        int threeQuarters = halfSize + quarterSize;
//
//        int maxL = halfSize;
//        for (int i = maxL; i < threeQuarters; i++) {
//            if (histogram[maxL] < histogram[i])
//                maxL = i;
//        }
//        int maxR = threeQuarters;
//        for (int i = maxR; i < histogram.length; i++) {
//            if (histogram[maxR] < histogram[i])
//                maxR = i;
//        }
////        int min = maxL;
////        for (int i = min; i < maxR; i++) {
////            if (histogram[min] > histogram[i])
////                min = i;
////        }
//        int min = (maxR + maxL) >> 1;
//
//        return min;
//    }

}
