package xrc7331.selfierobot_camera2.gui;

import org.opencv.core.Point;
import xrc7331.selfierobot_camera2.camera.CameraImageProcessor;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

import java.util.List;

/**
 * Created by XRC_7331 on 4/12/2016.
 */
public class ProcessedImageListener extends MainThreadExecutor implements Observer<CameraImageProcessor> {
    private final DrawBoard drawBoard;
    private CameraImageProcessor imageProcessor;
    private Runnable executionBody = new Runnable() {
        @Override
        public void run() {
            drawBoard.clear();
            List<Point> crosses = drawBoard.getCrosses();
            crosses.addAll(imageProcessor.getExtractedContourCentroids(CameraImageProcessor.ContourType.BLUE));
            crosses.addAll(imageProcessor.getExtractedContourCentroids(CameraImageProcessor.ContourType.RED));
            drawBoard.invalidate();
        }
    };

    public ProcessedImageListener(DrawBoard drawBoard) {
        this.drawBoard = drawBoard;
    }

    @Override
    public void updated(CameraImageProcessor caller) {
        this.imageProcessor = caller;
        super.executeInMainThread();
    }

    @Override
    protected Runnable getExecutionBody() {
        return executionBody;
    }

}
