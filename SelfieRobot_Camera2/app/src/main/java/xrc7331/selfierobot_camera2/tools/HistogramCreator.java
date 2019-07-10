package xrc7331.selfierobot_camera2.tools;

/**
 * Created by XRC_7331 on 2/22/2016.
 */
public class HistogramCreator {

    private static final HistogramCreator INSTANCE = new HistogramCreator();

    public static HistogramCreator getInstance() {
        return INSTANCE;
    }

    private int[] lastHistogram;

    private HistogramCreator() {
    }

    public int[] createHistogram(byte[] data) {
        lastHistogram = new int[UnsignedByte.MAX_INT + 1];

        for (byte dataByte : data) {
            lastHistogram[UnsignedByte.toInt(dataByte)]++;
        }
        return lastHistogram;
    }

    public int[] getLastHistogram() {
        return lastHistogram;
    }
}
