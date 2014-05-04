package com.bygorillas.andruino;

import java.util.Observable;

import android.bluetooth.BluetoothDevice;

public class DeviceSelected extends Observable {

	private BTSocketConnection btSocketConnection = new BTSocketConnection();
	
	private BluetoothDevice device;
	
	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {

		this.device = device;
		
		if(device != null) {
			connect();
		} else {
			disconnect();
		}
		
		setChanged();
		notifyObservers();
	}

	private void connect() {
		
		if(this.device != null) {
			ConnectBTSocketThread connectBTSocket = new ConnectBTSocketThread(device, btSocketConnection);	
			connectBTSocket.start();
		}
		
	}

	private void disconnect() {
		
		if(this.btSocketConnection != null) {
			this.btSocketConnection.close();
		}
		
	}
	
	public BTSocketConnection getConnection() {
		return btSocketConnection;
	}
	
}
