package com.arif.remote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.util.UUID;

public class SocketHelper extends Thread {
    private BluetoothDevice mDevice;
    private BluetoothSocket superSocket;
    private UUID mDeviceUUID;
    private static final String TAG = "BlueTest5-Controlling-Looooooooooooooooooooooooooooooooooog";

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
        Log.d(TAG, String.valueOf(this.mDevice));
    }

    public BluetoothSocket getSuperSocket() {
        return superSocket;
    }

    public void setSuperSocket(BluetoothSocket superSocket) {
        this.superSocket = superSocket;
    }

    public UUID getmDeviceUUID() {
        return mDeviceUUID;
    }

    public void setmDeviceUUID(UUID mDeviceUUID) {
        this.mDeviceUUID = mDeviceUUID;
        Log.d(TAG, String.valueOf(this.mDeviceUUID));
    }
}
