package com.bygorillas.andruino;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectBTSocketThread extends Thread {
	
    private BluetoothSocket	btSocket;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;
    private SocketManager btSocketManager;
 
    public ConnectBTSocketThread(BluetoothDevice device, SocketManager socketManager) {
    	
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevice = device;
        
        this.btSocketManager = socketManager;
        
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            Method m = btDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            btSocket = (BluetoothSocket) m.invoke(btDevice, 1);
        } catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    }
 
    public void run() {
        // Cancel discovery because it will slow down the connection
        btAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            btSocket.connect();
            
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
        	connectException.printStackTrace();
        	
            try {
                btSocket.close();
            } catch (IOException closeException) {
            	closeException.printStackTrace();
            }
            return;
        }

        this.btSocketManager.manage(btSocket);
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            btSocket.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}