package com.bygorillas.andruino;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SerialMessageActivity extends Activity implements Observer {

	private final int REQUEST_ENABLE_BT = 1;

	private List<String> commands = new ArrayList<String>();

	private ArrayAdapter<String> commandsAdapter;

	private DeviceSelected deviceSelected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.send_serial_messages_activity);

		ListView commandsListView = (ListView) findViewById(R.id.listView1);

		commandsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, commands);

		commandsListView.setAdapter(commandsAdapter);
		
		//TODO CONTINUA AQUI
		//commandsListView.setOnItemClickListener();

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		deviceSelected = new DeviceSelected();
		deviceSelected.addObserver(this);
		
		//Nenhum dispositivo selecionado
		deviceSelected.setDevice(null);
	}

	public void scanBT(View view) {

		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		final BTDevicesAdapter btDevicesAdapter = new BTDevicesAdapter(this);

		if (btAdapter.getBondedDevices() != null
				&& !btAdapter.getBondedDevices().isEmpty()) {
			btDevicesAdapter.addAll(btAdapter.getBondedDevices());
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_bt);
		builder.setSingleChoiceItems(btDevicesAdapter, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {

						dialog.dismiss();

						if (btAdapter.isDiscovering())
							btAdapter.cancelDiscovery();

						BluetoothDevice device = btDevicesAdapter.getItem(item);

						selectDevice(device);
					}
				});
		builder.setView(getLayoutInflater().inflate(R.layout.choose_bt_loading, null));
		builder.show();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					btDevicesAdapter.add(device);
				}
			}
		}, filter);

		btAdapter.startDiscovery();
	}

	private void selectDevice(BluetoothDevice device) {

		this.deviceSelected.setDevice(device);
	}

	private final void disconnectDevice() {
		disconnectDevice(this.deviceSelected.getDevice());
	}
	
	private void disconnectDevice(BluetoothDevice device) {

		this.deviceSelected.setDevice(null);
	}

	public void sendSerialMessage(View view) {

		EditText serialMessageEditText = (EditText) findViewById(R.id.message);

		if (serialMessageEditText.length() > 0) {
			
			String message = serialMessageEditText.getText().toString();
			
			commands.add(0, serialMessageEditText.getText().toString());
			commandsAdapter.notifyDataSetChanged();

			deviceSelected.getConnection().write(message);
			
			serialMessageEditText.getText().clear();
		}
	}
	
	@Override
	public void update(Observable observable, Object data) {
		
		DeviceSelected deviceSelected = (DeviceSelected) observable;
		
		BluetoothDevice device = deviceSelected.getDevice();
		
		if(device == null) {
			
			TextView deviceNameTextView = (TextView) findViewById(R.id.deviceNameTextView);
			
			deviceNameTextView.setText(R.string.no_conn);
			
			TextView macTextView = (TextView) findViewById(R.id.macTextView);
			
			macTextView.setText(R.string.choose_bt);

			Button connectDisconnectButton = (Button) findViewById(R.id.connectDisconnectButton);
			
			connectDisconnectButton.setText(R.string.connect_button);
			connectDisconnectButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					scanBT(v);
				}
			});

			EditText editText = (EditText) findViewById(R.id.message);
			
			editText.setEnabled(false);
			
			Button sendButton = (Button) findViewById(R.id.sendButton);
			
			sendButton.setEnabled(false);
		} else {
			
			TextView deviceNameTextView = (TextView) findViewById(R.id.deviceNameTextView);
			
			deviceNameTextView.setText(device.getName());
			
			TextView macTextView = (TextView) findViewById(R.id.macTextView);
			
			macTextView.setText(device.getAddress());

			Button connectDisconnectButton = (Button) findViewById(R.id.connectDisconnectButton);
			
			connectDisconnectButton.setText(R.string.disconnect_button);
			connectDisconnectButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					disconnectDevice();
				}
			});
			
			EditText editText = (EditText) findViewById(R.id.message);
			
			editText.setEnabled(true);
			
			Button sendButton = (Button) findViewById(R.id.sendButton);
			
			sendButton.setEnabled(true);
		}
		
		
	}

	
}
