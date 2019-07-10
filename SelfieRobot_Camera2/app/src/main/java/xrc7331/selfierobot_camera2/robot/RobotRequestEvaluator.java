package xrc7331.selfierobot_camera2.robot;

/**
 * Created by XRC_7331 on 4/6/2016.
 */
public interface RobotRequestEvaluator {
    byte[] evaluate() throws RobotRequestCannotBeEvaluatedException;
}
