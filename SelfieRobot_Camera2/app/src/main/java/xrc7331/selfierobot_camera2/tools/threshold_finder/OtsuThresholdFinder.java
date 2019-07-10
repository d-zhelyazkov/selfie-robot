package xrc7331.selfierobot_camera2.tools.threshold_finder;

/**
 * Created by XRC_7331 on 2/23/2016.
 */
public class OtsuThresholdFinder implements ThresholdFinder {

    private static final OtsuThresholdFinder INSTANCE = new OtsuThresholdFinder();

    public static OtsuThresholdFinder getInstance() {
        return INSTANCE;
    }

    private OtsuThresholdFinder() {
    }

    @Override
    public int findThreshold(int[] histogram) {

        int area = 0;
        for (int elem : histogram)
            area += elem;

        int sum = 0;
        int product = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            product += histogram[i] * i;
        }

        float maxVariance = 0;
        int maxIndex = -1;
        int leftSum = 0;
        int leftProduct = 0;
        int rightSum = sum;
        int rightProduct = product;
        for (int i = 0; i < (histogram.length - 1); i++) {
            leftSum += histogram[i];
            leftProduct += histogram[i] * i;
            rightSum -= histogram[i];
            rightProduct -= histogram[i] * i;

            float leftWeight = ((float) leftSum) / area;
            float leftMean = (leftSum == 0) ? 0 : ((float) leftProduct) / leftSum;
            float rightWeight = ((float) rightSum) / area;
            float rightMean = (rightSum == 0) ? 0 : ((float) rightProduct) / rightSum;

            float variance = (float) (leftWeight * rightWeight * Math.pow((leftMean - rightMean), 2));
            if (variance > maxVariance) {
                maxVariance = variance;
                maxIndex = i;
            }
        }

        return maxIndex;
    }


}
