package xrc7331.selfierobot_camera2.robot;

/**
 * Created by XRC_7331 on 4/6/2016.
 */
public class RobotTranslator {
    private static RobotTranslator ourInstance = new RobotTranslator();

    public static RobotTranslator getInstance() {
        return ourInstance;
    }


    private final byte RECOGNIZE_ME = 2;

    private RobotTranslator() {
    }

    public RobotRequestEvaluator getRequestEvaluator(byte requestCode) {
        RobotRequestEvaluator requestEvaluator = null;
        switch (requestCode) {
            case RECOGNIZE_ME:
                requestEvaluator = RobotLEDImageRecognizer.getInstance();
                break;
        }

        return requestEvaluator;
    }
}
