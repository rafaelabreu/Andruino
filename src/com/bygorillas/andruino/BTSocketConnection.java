package com.bygorillas.andruino;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class BTSocketConnection implements SocketManager, Closeable {

	private BluetoothSocket socket = null;
	private InputStream mmInStream = null;
	private OutputStream mmOutStream = null;

	public void manage(BluetoothSocket socket) {

		close();
		
		this.socket = socket;
		
		setupStreams();
		startRead();
	}

	private void setupStreams() {

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	private void startRead() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (socket != null || socket.isConnected()) {

					byte[] buffer = new byte[1024]; // buffer store for the
													// stream
					int bytes; // bytes returned from read()

					try {
						// Read from the InputStream
						bytes = mmInStream.read(buffer);

						// Send the obtained bytes to the UI activity
						// TODO mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
						// buffer)
						// .sendToTarget();
					} catch (IOException e) {
						break;
					}

				}
			}
		});

		thread.start();
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			if (this.socket != null || this.socket.isConnected()) {
				mmOutStream.write(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String message) {
		write(message.getBytes());
	}

	@Override
	public void close() {
		try {
			if(this.socket != null && this.socket.isConnected()) {
				this.socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}