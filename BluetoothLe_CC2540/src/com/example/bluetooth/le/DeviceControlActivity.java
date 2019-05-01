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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalActivity;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.bluetooth.le.LineGraphicView;
import com.example.bluetooth.le.R;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    
    ArrayList<Double> yList;
	LineGraphicView tu;
    //连接状态
    private TextView mConnectionState;
    private EditText mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    
    private byte SendComand;
    
    private Button button_send_value ; // 发送按钮
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
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x27,0x09,0x00,0x00,0x00,0x00,0x00},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x10},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x10},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x0a}
					    };
   String[] SendData_Display={"0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x10",
		   						"0x38,0x39,0x40,0x34,0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x10",
		   						"0x41,0x42,0x33,0x34,0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x10",
		   						"0x51,0x52,0x33,0x34,0x30,0x31,0x33,0x34,0x30,0x31,0x33,0x10"};
   int Spin_index,DateReceiveIndex,Receive_Datalength,total_cnt;
   
   public List<double[]> x = new ArrayList<double[]>(); 
   public List<double[]> y = new ArrayList<double[]>();

   private Timer drawTimer = new Timer();
   private TimerTask drawTask;
   private Handler Drawhandler;
   
   public LinearLayout layout;
   public GraphicalView chart;
   public XYMultipleSeriesDataset dataset;
   public String[] titles;
   public XYMultipleSeriesRenderer renderer;
   public Context context1;
   public XYSeries series;
   public XYSeries series2;
   
	int data_press[]=new int[800];
	int data_Pos[]=new int[800];
   
   boolean StartRecord,DataTransfer_Complete;
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
            	//String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	byte[] data_B = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	
            	Receive_Datalength=data_B.length;
            	

            	byte data_total[]=new byte[3200];
            	
            	if(Receive_Datalength%4==0)
            	{
            		for(int groupCnt=0;groupCnt<(Receive_Datalength/4);groupCnt++)
            		{
            			DateReceiveIndex=dataconvert.Byte2Integer(data_B,4*groupCnt,2);
            			if(DateReceiveIndex<800)
                		{
            			data_press[DateReceiveIndex]=dataconvert.Byte2Integer(data_B,4*groupCnt,2);
            			data_Pos[DateReceiveIndex]=dataconvert.Byte2Integer(data_B,4*groupCnt+2,2); 
            			System.out.println("DateReceiveIndex----" +DateReceiveIndex+"--"+ data_press[DateReceiveIndex]+"--"+data_Pos[DateReceiveIndex]);
	            			if(DateReceiveIndex>=798)
	            			{
	            				DataTransfer_Complete=true;
	            			}
                		}
            			else
            			{
            				DataTransfer_Complete=true;
            			}
            		}
            		
            	}
            	
            	System.out.println("Receive_Datalength----" +Receive_Datalength);
            	

            	
            	for(int datacnt=0;datacnt<Receive_Datalength;datacnt++)
            	{
            		//data_total[total_cnt]=data_B[datacnt];
            		total_cnt++;
            		
            	}
            	/*
            	if(dataconvert.Byte2Integer(data_B,0,2)==1)
            	{
            	
            		StartRecord=true;
            	}
            	*/
            	/*
            	StartRecord=true;
            	if(DateReceiveIndex>=799)
            	{
            		DateReceiveIndex=0;
            		DataTransfer_Complete=true;
            	}
            	else
            	{	
            		DateReceiveIndex++;
            	}*/
            /*	
            	DateReceiveIndex=dataconvert.Byte2Integer(data_B,0,2);
            	if(DateReceiveIndex>799)
            	{
            		DateReceiveIndex=799;
            	}
            	
            	if(StartRecord==true){
            		if(DateReceiveIndex<800)
            		{
            			
            			if(DateReceiveIndex<800)
            			{
            			//data_press[DateReceiveIndex]=dataconvert.Byte2Integer(data_B,0,2);
            			try{
            				data_press[DateReceiveIndex]=dataconvert.Byte2Integer(data_B,0,2);
            			data_Pos[DateReceiveIndex]=dataconvert.Byte2Integer(data_B,2,2);  
            			}
            			catch(ArrayIndexOutOfBoundsException  e){
            				System.out.println(e);
            				//System.out.println("数组越界异常:"+DateReceiveIndex);
            				}
            			}
            		}
            	}*/
            	//System.out.println("DateReceiveIndex----" +DateReceiveIndex+"--"+ data_press[DateReceiveIndex]+"--"+data_Pos[DateReceiveIndex]);
            	//System.out.println("DateReceiveIndex----" +data_Pos+"--"+ data_Pos[DateReceiveIndex]);
            	//int receivedata = dataconvert.DataStr2Integer(data);
                //displayData(data);
            	double x_list[]=new double[800];
                double y_list[]=new double[800];
                if(DataTransfer_Complete==true)
                {
                for(int i=0;i<800;i++)
                {
               	 
               	 x_list[i]=(double)data_press[i]/8;
               	 y_list[i]=(double)data_Pos[i]/100;
                   	//x_list[i]=(double)i/8;
                   	//y_list[i]=(double)i/100;
                }
                updateLineChart(x_list,y_list);
                DataTransfer_Complete=false;
                }
            }
        }
    };    
    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //mDataField.setText(R.string.no_data);
    }
   

	@SuppressWarnings("null")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        //Context context1 = getApplicationContext();
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
       
        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        //mDataField =  (EditText) findViewById(R.id.data_value);
        
        //add line chart
       titles = new String[] { "First", "Second"};
       // String[] titles = new String[] { "First"};


        x.add(new double[] { 0} );         
        y.add(new double[] { 0});
        
        x.add(new double[] { 0, 2, 4, 6, 8} );
        y.add(new double[] { 0,0,0,0,0}); 
     

        dataset = LineGraphicView.buildDataset(titles, x, y);

        int[] colors = new int[] { Color.BLUE, Color.GREEN}; 
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND}; 
        renderer = LineGraphicView.buildRenderer(colors, styles, true);

        LineGraphicView.setChartSettings(renderer, "Line Chart Demo", "X", "Y", 0, 100, 0, 80 , Color.WHITE, Color.WHITE);
        layout = (LinearLayout)findViewById(R.id.chart_show); 
        chart = ChartFactory.getLineChartView(this, dataset, renderer);
        layout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
         
       
        
        button_send_value = (Button) findViewById(R.id.button_send_value);
        //edittext_input_value = (EditText) findViewById(R.id.edittext_input_value);
        
        button_send_value.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
                    boolean flag=false;
                    value[0] = (byte) 0x00;
                    if(flag){
                    	Toast.makeText(getApplicationContext(), "请输入！", Toast.LENGTH_SHORT).show();
                    	return;
                    }else{
                        characteristic.setValue(SendData[Spin_index]);                    	
                        mBluetoothLeService.writeCharacteristic(characteristic);
                        Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
                    }
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                
           
           
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
            //mDataField.setText(data);
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
    
       private void updateLineChart(double x_list[],double y_list[])
       {

		layout.removeView(chart);//这里需要注意，很多人只做是没有用这个方法，导致图不能重新绘制，
        
        for (int i = 0; i < 2; i++) 
		{ 						
  		  XYSeries series325= dataset.getSeries()[1-i];
  		  dataset.removeSeries(series325);
  		  series325.clear();						
		}
     
        x.set(1,x_list );         
        y.set(1,y_list);
      
        dataset = LineGraphicView.buildDataset(titles, x, y);
        chart = ChartFactory.getLineChartView(DeviceControlActivity.this, dataset, renderer);   
        layout.addView(chart);	
       }
    
}

