package com.bygorillas.andruino;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BTDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

	private static int layout = android.R.layout.simple_list_item_2;

	public BTDevicesAdapter(Context context) {

		super(context, layout);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(convertView == null)
			convertView = inflater.inflate(layout, null);

		TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
		TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);

		BluetoothDevice device = getItem(position);

		text1.setText(device.getName());
		text2.setText(device.getAddress());

		return convertView;
	}
}