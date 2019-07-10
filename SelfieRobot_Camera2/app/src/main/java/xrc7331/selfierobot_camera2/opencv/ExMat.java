package xrc7331.selfierobot_camera2.opencv;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 * Created by XRC_7331 on 3/9/2016.
 */
public class ExMat extends Mat {
    private ColorType colorType;

    public ExMat(ColorType colorType) {
        super();
        this.colorType = colorType;
    }

    public ExMat(ExMat mat) {
        this(mat.getColorType(), mat);
    }

    public ExMat(ColorType colorType, Mat img) {
        super(img, new Rect(0, 0, img.width(), img.height()));
        //super();
        //img.copyTo(this);

        this.colorType = colorType;
    }

    public ColorType getColorType() {
        return colorType;
    }

    public byte[] getData() {
        byte[] data = new byte[super.channels() * super.width() * super.height()];
        super.get(0, 0, data);

        return data;
    }

    public byte getByte(Point point, int channel) {
        byte[] pixel = new byte[super.channels()];
        super.get((int) point.y, (int) point.x, pixel);
        return pixel[channel];
    }

    public byte getByte(Point point) {
        return getByte(point, 0);
    }

    public enum ColorType {
        GRAY(1),
        BGR(3),
        HSV(3),
        RGBA(4);

        private int channels;

        ColorType(int channels) {
            this.channels = channels;
        }

        public int getChannels() {
            return channels;
        }
    }
}
