package xrc7331.selfierobot_camera2.robot;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import xrc7331.selfierobot_camera2.bluetooth.BluetoothConnection;
import xrc7331.selfierobot_camera2.bluetooth.BluetoothModule;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTCannotConnectToADeviceException;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTModuleNotInitialized;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTReceiveException;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTTransitionException;
import xrc7331.selfierobot_camera2.tools.observer.Observable;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by XRC_7331 on 4/5/2016.
 */
public class RobotCommunicator extends AsyncTask<Object, Object, Object> implements Observable<RobotCommunicator> {
    private static final byte DATA_PROCESSED = 0;
    private static final byte DATA_RECEIVE_PROBLEM = 1;
    private static final String ROBOT_BT_NAME = "HC-05";
    private static final long REFRESH_INTERVAL = 100;
    private static final RobotTranslator ROBOT_TRANSLATOR = RobotTranslator.getInstance();

    private List<Observer<RobotCommunicator>> observers = new LinkedList<>();

    private BluetoothModule btModule;
    private byte lastRequestCode;
    private boolean awaitingConfirmation = false;
    private RobotRequestEvaluator lastRequestEvaluator;
    private Progress lastProgress;

    public RobotCommunicator(BluetoothModule btModule) {
        this.btModule = btModule;
    }

    @Override
    protected Object doInBackground(Object... params) {

        BluetoothConnection robotConnection = null;
        try {
            while (!super.isCancelled()) {
                try {
                    if (robotConnection == null || !robotConnection.isConnected()) {
                        setProgress(Progress.CONNECTION_ERROR);
                        robotConnection = connectToTheRobot();
                    }

                    if (robotConnection != null && robotConnection.isConnected()) {
                        if (robotConnection.isDataAvailable()) {
                            byte[] data = robotConnection.readData();
                            if (awaitingConfirmation) {
                                boolean elementMatched = false;
                                for (byte elem : data) {
                                    elementMatched = true;
                                    switch (elem) {
                                        case DATA_PROCESSED:
                                            awaitingConfirmation = false;
                                            break;
                                        case DATA_RECEIVE_PROBLEM:
                                            robotConnection.sendData(lastRequestEvaluator.evaluate());
                                            break;
                                        default:
                                            elementMatched = false;
                                    }
                                    if (elementMatched)
                                        break;
                                }
                            } else {
                                lastRequestCode = data[data.length - 1];
                                setProgress(Progress.REQUEST_RECEIVED);
                                lastRequestEvaluator = ROBOT_TRANSLATOR.getRequestEvaluator(lastRequestCode);
                                if (lastRequestEvaluator == null) {
                                    setProgress(Progress.ROBOT_REQUEST_NOT_EVALUATED);
                                } else {
                                    byte[] response = lastRequestEvaluator.evaluate();
                                    robotConnection.sendData(response);
                                    setProgress(Progress.ROBOT_REQUEST_EVALUATED);
                                    awaitingConfirmation = true;
//                                    //flush other requests
//                                    if (robotConnection.isDataAvailable())
//                                        robotConnection.readData();
                                }
                            }
                        }
                    } else {
                        setProgress(Progress.CONNECTION_ERROR);
                    }
                } catch (BTReceiveException | BTTransitionException e) {
                    e.printStackTrace();
                    setProgress(Progress.COMMUNICATION_ERROR);
                } catch (RobotRequestCannotBeEvaluatedException e) {
                    setProgress(Progress.ROBOT_REQUEST_NOT_EVALUATED);
                    e.printStackTrace();
                } catch (BTModuleNotInitialized | BTCannotConnectToADeviceException e) {
                    e.printStackTrace();
                    setProgress(Progress.CONNECTION_ERROR);
                } catch (RobotBTNotPairedException e) {
                    setProgress(Progress.ROBOT_NOT_PAIRED);
                }

                Thread.sleep(REFRESH_INTERVAL);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BluetoothConnection connectToTheRobot() throws BTCannotConnectToADeviceException, BTModuleNotInitialized, RobotBTNotPairedException {
        BluetoothConnection robotConnection;
        Collection<BluetoothDevice> pairedDevices = btModule.getPairedDevices();
        BluetoothDevice robotBTDevice = null;
        for (BluetoothDevice btDevice : pairedDevices) {
            if (btDevice.getName().equals(ROBOT_BT_NAME)) {
                robotBTDevice = btDevice;
                break;
            }
        }
        if (robotBTDevice == null) {
            throw new RobotBTNotPairedException();
        } else {
            robotConnection = btModule.connectToADevice(robotBTDevice);

        }

        setProgress(Progress.ROBOT_CONNECTED);
        return robotConnection;
    }

    public byte getLastRequestCode() {
        return lastRequestCode;
    }

    private void setProgress(Progress progress) {
        this.lastProgress = progress;
        notifyObservers();
    }

    public Progress getLastProgress() {
        return lastProgress;
    }

    @Override
    public void addObserver(Observer<RobotCommunicator> observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers)
            observer.updated(this);
    }

    public enum Progress {

        CONNECTION_ERROR,
        COMMUNICATION_ERROR,
        ROBOT_REQUEST_EVALUATED,
        ROBOT_NOT_PAIRED,
        REQUEST_RECEIVED,
        ROBOT_CONNECTED,
        ROBOT_REQUEST_NOT_EVALUATED
    }
}
