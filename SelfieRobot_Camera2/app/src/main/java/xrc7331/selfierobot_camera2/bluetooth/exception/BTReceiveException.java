package xrc7331.selfierobot_camera2.bluetooth.exception;

import xrc7331.selfierobot_camera2.bluetooth.IEvent;

import java.io.IOException;

/**
 * Created by XRC_7331 on 11/29/2015.
 */
public class BTReceiveException extends Exception implements IEvent {
    public BTReceiveException(IOException e) {
        super(e);
    }
}