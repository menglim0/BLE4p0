/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import java.lang.reflect.Field;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/*******Add Spinner***/
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
/***********end of Add Spinner*******************/


/**
 * 对于一个BLE设备，该activity向用户提供设备连接，显示数据，显示GATT服务和设备的字符串支持等界面，
 * 另外这个activity还与BluetoothLeService通讯，反过来与Bluetooth LE API进行通讯
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    //连接状态
    private TextView mConnectionState;
    private EditText mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    
    private byte SendComand;
    
    private Button button_send_value ; // 发送按钮
    private Button button_start ; // 开始按钮
    private EditText edittext_input_value ; // 数据在这里输入
    
    private BluetoothLeService mBluetoothLeService;
  
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;


    //写数据
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattService mnotyGattService;;
    //读数据
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService readMnotyGattService;
    byte[] WriteBytes = new byte[20];
    
    /*******Add Spinner***/
   private ArrayAdapter<String> adapter;//创建一个数组适配器
   byte[] Command_in ={0x30,0x31,0x33,0x34};
   byte[][] SendData = {{0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x3E,(byte)0x80,0x00,0x00,0x00,0x00,0x00},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x10,0x03,0x00,0x00,0x00,0x00,0x00},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x27,0x09,0x00,0x00,0x00,0x00,0x00},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x10,0x0E,0x27,0x0A,0x20,0x20,0x20,0x20},
					    {0x10,(byte)0xDB,(byte)0xFE,(byte)0xF1,0x04,0x14,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x00,0x00,0x00}
					    };
   
   byte[] StartCmd = {(byte)0xFF,(byte)0x01,(byte)0xE1,(byte)0xF1,0x02,0x3E,(byte)0x80,(byte)0xFF,0x00,0x00,0x00,0x00};
   
   
   String[] SendData_Display=  {"0x14DAE1F1 0x02,0x3E,0x80,0x00,0x00,0x00,0x00,0x00",
		   						"0x14DAE1F1 0x02,0x10,0x03,0x00,0x00,0x00,0x00,0x00",
		   						"0x14DAE1F1 0x02,0x27,0x09,0x00,0x00,0x00,0x00,0x00",
		   						"0x14DAE1F1 0x10,0x0E,0x27,0x0A,0x20,0x20,0x20,0x20",
		   						"0x10DBFEF1 0x04,0x14,0xFF,0xFF,0xFF,0x00,0x00,0x00"};
   int Spin_index;
   int received_data;

    
    /***********end of Add Spinner*******************/

    // 管理服务的生命周期
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.处理服务所激发的各种事件
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接一个GATT服务
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.从GATT服务中断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.查找GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.从服务中接受数据
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } 
            //发现有可支持的服务
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	//写数据的服务和characteristic
            	mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb"));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb"));
                //读数据的服务和characteristic
                readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
            } 
            //显示数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	//将数据显示在mDataField上            	
            	String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	System.out.println("data----" + data);
                displayData(data);
 
            }
        }
    };    
    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField =  (EditText) findViewById(R.id.data_value);
        
        /*保持屏幕常亮*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        /* convert data*/
        //SendData_Display[0][0] = (String) SendData[0][0];
        
        /*******Add Spinner***/
        final Spinner mSpinner = (Spinner) findViewById(R.id.spinner1); 
    	// 建立数据源
		final String[] mItems = getResources().getStringArray(R.array.spinnername);
     // 建立Adapter并且绑定数据源
     	final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
     	//绑定 Adapter到控件
     	mSpinner.setAdapter(adapter);
     	//4.为下拉列表设置各种点击事件，以响应菜单中的文本item被选中了，用setOnItemSelectedListener   
     			mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
     															@Override
     								public void onItemSelected(AdapterView<?> arg0,
     										View arg1, int arg2, long arg3) {
     																
										try {
											//以下三行代码是解决问题所在
											/*返回到包含spinner的activity中，再次点击相同的item无法实现跳转操作。研究了半天才发现原因，
											Android spinner本身记住了上一次选择的项，再次点击相同的项是不会触发onitemselected事件的。*/
											Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
											field.setAccessible(true);	//设置mOldSelectedPosition可访问
											field.setInt(mSpinner, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
										} catch (Exception e) {
											e.printStackTrace();
										}

     											
     									// TODO Auto-generated method stub
     								    // 将所选mySpinner 的值带入myTextView 中      						
     							edittext_input_value.setText(SendData_Display[arg2]);//文本说明
     							Spin_index= arg2;
     							}

     								@Override
     								public void onNothingSelected(AdapterView<?> arg0) {
     									// TODO Auto-generated method stub
     									
     								}


     								});
        
        button_send_value = (Button) findViewById(R.id.button_send_value);
        button_start = (Button) findViewById(R.id.Start);
        
        edittext_input_value = (EditText) findViewById(R.id.edittext_input_value);
        
        button_send_value.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final boolean action_state;
				read();
				
                final int charaProp = characteristic.getProperties();
                
                //如果该char可写
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    //读取数据，数据将在回调函数中
                    //mBluetoothLeService.readCharacteristic(characteristic);
                    byte[] value = new byte[20];
                    value[0] = (byte) 0x00;
                    if(edittext_input_value.getText().toString().equals("")){
                    	Toast.makeText(getApplicationContext(), "请输入！", Toast.LENGTH_SHORT).show();
                    	return;
                    }else{
                    	//WriteBytes[0]=SendComand;Spin_index
                    	
                    	//SendData[0]=SendData[Spin_index];
                    	WriteBytes = edittext_input_value.getText().toString().getBytes();
                        characteristic.setValue(SendData[Spin_index]);
                        mBluetoothLeService.writeCharacteristic(characteristic);
                        
                        Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
                    }
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                edittext_input_value.setText("");
			}
		});
        
        button_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final boolean action_state;
				read();
				
                final int charaProp = characteristic.getProperties();
                
                //如果该char可写
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }

                        characteristic.setValue(StartCmd);
                        mBluetoothLeService.writeCharacteristic(characteristic);
                        Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
             
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                edittext_input_value.setText("");
			}
		});
       
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /*
	 * **************************************************************
	 * *****************************读函数*****************************
	 */
    private void read() {
    	//mBluetoothLeService.readCharacteristic(readCharacteristic);
    	//readCharacteristic的数据发生变化，发出通知
    	mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);
    	//Toast.makeText(this, "读成功", Toast.LENGTH_SHORT).show();
	}
    
    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    /*
    private void delay(int ms){
		try {
            Thread.currentThread();
			Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
	 }
*/
}

