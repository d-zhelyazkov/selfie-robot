package xrc7331.selfierobot_camera2.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTCannotConnectToADeviceException;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTReceiveException;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTTransitionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by XRC_7331 on 11/29/2015.
 */
public class BluetoothConnection {

    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;

    public BluetoothConnection(BluetoothDevice device, UUID uuid) throws BTCannotConnectToADeviceException {
        try {
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BTCannotConnectToADeviceException(e);
        }
    }

    public void close() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] data ) throws BTTransitionException {
        try {
            mmOutStream.write(data);
        } catch (IOException e) {
            throw new BTTransitionException(e);
        }
    }

    public boolean isDataAvailable() throws BTReceiveException {
        try {
            return (mmInStream.available() != 0);
        } catch (IOException e) {
            throw new BTReceiveException(e);
        }
    }

    public byte[] readData() throws BTReceiveException {
        try {
            byte[] result = new byte[mmInStream.available()];
            mmInStream.read(result);
            return result;
        } catch (IOException e) {
            throw new BTReceiveException(e);
        }
    }

    public boolean isConnected()
    {
        return mmSocket.isConnected();
    }
}
