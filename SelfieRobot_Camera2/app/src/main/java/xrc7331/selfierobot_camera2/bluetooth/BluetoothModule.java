package xrc7331.selfierobot_camera2.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTAdapterNotAvailableException;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTCannotConnectToADeviceException;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTModuleNotInitialized;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTNotEnabledException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by XRC_7331 on 11/29/2015.
 */
public class BluetoothModule {

    private static final String SERIAL_UUID = "00001101-0000-1000-8000-00805f9b34fb"; //Standard SerialPortService ID
    private BluetoothAdapter mBluetoothAdapter;
    private Collection<BluetoothConnection> connections = new LinkedList();

    public BluetoothModule() {
    }

    public void init() throws BTAdapterNotAvailableException, BTNotEnabledException {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new BTAdapterNotAvailableException();
        }

        if (!mBluetoothAdapter.isEnabled())
            throw new BTNotEnabledException();

    }

    public Collection<BluetoothDevice> getPairedDevices() throws BTModuleNotInitialized {
        if(mBluetoothAdapter == null)
            throw new BTModuleNotInitialized();

        return mBluetoothAdapter.getBondedDevices();
    }


    public BluetoothConnection connectToADevice(BluetoothDevice device) throws BTCannotConnectToADeviceException {
        UUID uuid = UUID.fromString(SERIAL_UUID);
        BluetoothConnection connection = new BluetoothConnection(device, uuid);
        connections.add(connection);
        return connection;
    }

    public void finishWork() {
        for (BluetoothConnection connection : connections) {
            connection.close();
        }
    }


}
