package xrc7331.selfierobot_camera2.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import org.opencv.core.Point;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XRC_7331 on 4/6/2016.
 */
public class DrawBoard extends View {
    private static final int CROSS_OFFSET = 10;

    private Paint greenColor;
    private List<Point> crosses = new LinkedList<>();

    public DrawBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        greenColor = new Paint();
        greenColor.setColor(0xff00ff00);
        greenColor.setStyle(Paint.Style.FILL);
    }

    public void clear() {
        crosses.clear();

        super.invalidate();
    }

    public List<Point> getCrosses() {
        return crosses;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Point point : crosses) {
            canvas.drawLine(((float) point.x - CROSS_OFFSET), ((float) point.y - CROSS_OFFSET), ((float) point.x + CROSS_OFFSET), ((float) point.y + CROSS_OFFSET), greenColor);
            canvas.drawLine(((float) point.x - CROSS_OFFSET), ((float) point.y + CROSS_OFFSET), ((float) point.x + CROSS_OFFSET), ((float) point.y - CROSS_OFFSET), greenColor);
        }
    }

}
