package xrc7331.selfierobot_camera2.opencv;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XRC_7331 on 3/12/2016.
 */
public class ColorConverter {
    private static final ColorConverter INSTANCE = new ColorConverter();

    public static ColorConverter getInstance() {
        return INSTANCE;
    }

    private final int colorTypesCount = ExMat.ColorType.values().length;
    private final List<List<int[]>> CONVERSION_CODES = new ArrayList<>(colorTypesCount);

    private ColorConverter() {
        //source GRAY
        List<int[]> graySrcList = new ArrayList<>(colorTypesCount);
        graySrcList.add(ExMat.ColorType.GRAY.ordinal(), new int[]{0});
        graySrcList.add(ExMat.ColorType.BGR.ordinal(), new int[]{Imgproc.COLOR_GRAY2BGR});
        graySrcList.add(ExMat.ColorType.HSV.ordinal(), new int[]{Imgproc.COLOR_GRAY2BGR, Imgproc.COLOR_BGR2HSV});
        graySrcList.add(ExMat.ColorType.RGBA.ordinal(), new int[]{Imgproc.COLOR_GRAY2RGBA});

        CONVERSION_CODES.add(ExMat.ColorType.GRAY.ordinal(), graySrcList);

        //source BGR
        List<int[]> bgrSrcList = new ArrayList<>(colorTypesCount);
        bgrSrcList.add(ExMat.ColorType.GRAY.ordinal(), new int[]{Imgproc.COLOR_BGR2GRAY});
        bgrSrcList.add(ExMat.ColorType.BGR.ordinal(), new int[]{0});
        bgrSrcList.add(ExMat.ColorType.HSV.ordinal(), new int[]{Imgproc.COLOR_BGR2HSV});
        bgrSrcList.add(ExMat.ColorType.RGBA.ordinal(), new int[]{Imgproc.COLOR_BGR2RGBA});
        CONVERSION_CODES.add(ExMat.ColorType.BGR.ordinal(), bgrSrcList);

        //source HSV
        List<int[]> hsvSrcList = new ArrayList<>(colorTypesCount);
        hsvSrcList.add(ExMat.ColorType.GRAY.ordinal(), new int[]{Imgproc.COLOR_HSV2BGR, Imgproc.COLOR_BGR2GRAY});
        hsvSrcList.add(ExMat.ColorType.BGR.ordinal(), new int[]{Imgproc.COLOR_HSV2BGR});
        hsvSrcList.add(ExMat.ColorType.HSV.ordinal(), new int[]{0});
        hsvSrcList.add(ExMat.ColorType.RGBA.ordinal(), new int[]{Imgproc.COLOR_HSV2BGR, Imgproc.COLOR_BGR2RGBA});
        CONVERSION_CODES.add(ExMat.ColorType.HSV.ordinal(), hsvSrcList);

        //source RGBA
        List<int[]> rgbaSrcList = new ArrayList<>(colorTypesCount);
        rgbaSrcList.add(ExMat.ColorType.GRAY.ordinal(), new int[]{Imgproc.COLOR_RGBA2GRAY});
        rgbaSrcList.add(ExMat.ColorType.BGR.ordinal(), new int[]{Imgproc.COLOR_RGBA2BGR});
        rgbaSrcList.add(ExMat.ColorType.HSV.ordinal(), new int[]{Imgproc.COLOR_RGBA2BGR,Imgproc.COLOR_BGR2HSV});
        rgbaSrcList.add(ExMat.ColorType.RGBA.ordinal(), new int[]{0});
        CONVERSION_CODES.add(ExMat.ColorType.RGBA.ordinal(), rgbaSrcList);


    }

    public ExMat convert(ExMat sourceImage, ExMat.ColorType colorType) {
        ExMat resultImage = new ExMat(colorType);
        int[] conversionCodes = CONVERSION_CODES.get(sourceImage.getColorType().ordinal()).get(colorType.ordinal());
        for (int conversionCode : conversionCodes) {
            if (conversionCode == 0)
                break;

            Imgproc.cvtColor(sourceImage, resultImage, conversionCode);
        }

        return resultImage;
    }
}
