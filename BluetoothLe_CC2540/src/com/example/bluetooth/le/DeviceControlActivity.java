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
import java.text.DecimalFormat;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog; 
import android.app.Dialog;
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
    private TextView mLogInfor;    
    private TextView editT_DisplaySignalValue;
    private TextView valuediaplay1;  
    private TextView valuediaplay2;  
    private ScrollView mscrollView;
    private EditText mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    
    private byte SendComand;
    
    private Button button_send_value ; // 发送按钮
    private Button button_start ; // 开始按钮
    private Button button_CAN_config ; // CAN init button
    private EditText edittext_input_value ; // 数据在这里输入
    private EditText edittext_CAN_Config ; // 数据在这里输入
    
    private CheckBox CheckBox1=null;
    
    private BluetoothLeService mBluetoothLeService;
  
    private boolean mConnected = false,checkbox1_checked=false,checkbox2_checked=false,ReceiveIDConfig1_checked = false;
    private boolean config_and_Check =false,config_and_Check_2 =false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;


    //写数据
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattService mnotyGattService;;
    //读数据
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService readMnotyGattService;
    byte[] WriteBytes = new byte[20];
    
    int[] data_String2Int= new int[14];
    
    int receiveID_Int=0;               
    
    
    byte[] data_String2Byte = new byte[4];
    
    /*******Add Spinner***/
   private ArrayAdapter<String> adapter;//创建一个数组适配器
   byte[] Command_in ={0x30,0x31,0x33,0x34};
   byte[][] SendData = {{0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x3E,(byte)0x80,0x00,0x00,0x00,0x00,0x00,0x00,0x00},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x10,0x03,0x00,0x00,0x00,0x00,0x00,0x00,0x00},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x27,0x09,0x00,0x00,0x00,0x00,0x00,0x00,0x00},
					    {0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x10,0x0E,0x27,0x0A,0x20,0x20,0x20,0x20,0x00,0x00},
					    {0x10,(byte)0xDB,(byte)0xFE,(byte)0xF1,0x04,0x14,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x00,0x00,0x00,0x00,0x00}
					    };
   
   byte[] StartCmd = {(byte)0xFF,(byte)0x01,(byte)0xE1,(byte)0xF1,0x02,0x3E,(byte)0x80,(byte)0xFF,0x00,0x00,0x00,0x00,0x00,0x00};
   
   byte[] CAN_init_Cmd = {(byte)0xEA,(byte)0x01,(byte)0x00,(byte)0x00,0x00,0x00,(byte)0x00,(byte)0x00,0x00,0x00,0x00,0x00,0x00,0x00};
   
   byte[] SendCAN_Config = {(byte)0xEA,(byte)0x01,(byte)0x00,(byte)0x00,0x00,0x00,(byte)0x00,(byte)0x00,0x00,0x00,0x00,0x00,0x00,0x00};							   
							  
   
   byte[] ReceiveCAN_Config = {(byte)0xE3,(byte)0x01,(byte)0x00,(byte)0x00,0x00,0x00,(byte)0x00,(byte)0x00,0x00,0x00,0x00,0x00,0x00,0x00};		    
		   
   
   String[] SendData_Display=  {"0x14DAE1F1 0x02,0x3E,0x80,0x00,0x00,0x00,0x00,0x00",
		   						"0x14DAE1F1 0x02,0x10,0x03,0x00,0x00,0x00,0x00,0x00",
		   						"0x14DAE1F1 0x02,0x27,0x09,0x00,0x00,0x00,0x00,0x00",
		   						"0x14DAE1F1 0x10,0x0E,0x27,0x0A,0x20,0x20,0x20,0x20",
		   						"0x10DBFEF1 0x04,0x14,0xFF,0xFF,0xFF,0x00,0x00,0x00"};
   
   String[] SendData_CAN_Config=  {" CAN 33.3K",
									" CAN 500K",
									" CAN 500K",
									" CAN 500K",
									" Not Define"
									};
   
   String[] SendData_CANFD_Config=  {" /FD Dis",
										" /FD 2M",
										" /FD 5M"
										};
   
  
   String signal_ReceiveID;
   String signal_StartBit;
   String signal_SigLength;
   String signal_reslouation;
   String signal_Offset;
   String signal_Name;
   
   String signal_ReceiveID_2;
   String signal_StartBit_2;
   String signal_SigLength_2;
   String signal_reslouation_2;
   String signal_Offset_2;
   String signal_Name_2;
 
   
   //String[] loginfo;
   
   int Spin_index,Spin_CAN_index,Spin_CANFD_index;
   int received_ID;
   int received_StartBit;
   int received_SigLength;
   double received_reslouation;
   int received_Offset;
  
   int Spin_index_2,Spin_CAN_index_2;
   int received_ID_2;
   int received_StartBit_2;
   int received_SigLength_2;
   double received_reslouation_2;
   int received_Offset_2;
   boolean ID_Config_reminder;
    
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
            
            //final TextView editT_DisplaySignalValue = (TextView) findViewById(R.id.textView5);
            
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
                
                /* 设置可写 add 0321*/
               // read();
                mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                //读数据的服务和characteristic
                readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
            } 
            //显示数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	//将数据显示在mDataField上    
            	
            	
            	//String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	//String data="A211120065001200000000000000";
            	byte[] data_B = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	//ZASfor
            	int firstByte= 0, firstByte1= 0, firstByte2= 0, firstByte3= 0,firstByte4= 0;
            	firstByte1= ((int)data_B[2]&0xff)<<24;
            	firstByte2= ((int)data_B[3]&0xff)<<16;
            	firstByte3= ((int)data_B[4]&0xff)<<8;
            	firstByte4= ((int)data_B[5]&0xff);
            			//+((int)data_B[3]&0xff)<<16+((int)data_B[4]&0xff)<<8+(int)data_B[5]&0xff;
            	firstByte=firstByte1+firstByte2+firstByte3+firstByte4;
            	
            	
            	//data_CAN=data_B[7]&0xff,
            	int data_CAN=0;
            	long convertData;
            	double phsValue_FromCAN;
            //	byte tempdata = (byte) 0x81;
            	
            	String DataHexStr = "0";
            	
            	DataHexStr=dataconvert.Data2Str(data_B);    

            	if(received_ID ==firstByte  &&  data_B[1]==(byte)0x81)
            	{
	            	if(checkbox1_checked ==true && received_ID ==firstByte  &&  data_B[1]==(byte)0x81)
	            	{
	                	convertData = dataconvert.DataStramp(data_B,received_StartBit,  received_SigLength);
	                 	System.out.println("convertData----" + convertData);
	                 	
	            		data_CAN =(int) (convertData-received_Offset);
	            		
	            		phsValue_FromCAN=(double)data_CAN*received_reslouation;
	            		DecimalFormat df=new DecimalFormat(".##");
	            		//String st=df.format(phsValue_FromCAN);
	            		//displayValueData1(Double.toString(phsValue_FromCAN));
	            		displayValueData1(df.format(phsValue_FromCAN));
	            		//displayData("ID "+ Integer.toHexString(firstByte)+" received");
	            		displayData("ID_0x"+ Integer.toHexString(firstByte)+":"+DataHexStr+" received");
	            		displayData(signal_Name+ "="+ data_CAN);
	            		
	            		//received_Offset
	            	} 
	            	else if(checkbox1_checked == false)
	            	{    if(ID_Config_reminder==false)
			            	{
			            		displayData("Please set the receive 1 to Enable");
			            		ID_Config_reminder = true;
			            	}
	            		displayValueData1("invalid");
	            	}
            	}
            	//else
            	//{displayData("Data received but no display requirement");}
            	
            	if(received_ID_2 ==firstByte  &&  data_B[1]==(byte)0x82)
            	{
            		data_CAN =data_CAN-received_Offset;
            		displayValueData2(data_CAN);	
            		displayData("ID "+ Integer.toHexString(firstByte)+" received");
            		displayData(signal_Name+ "="+ data_CAN);
            		
            		//received_Offset
            	}            	
            	else if(data_B[1]==(byte)0x82)
            	{
            		displayData(signal_ReceiveID_2+" can't receive in list 2");
            		displayValueData2("invalid");
            	}
      
 
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
        mLogInfor = (TextView) findViewById(R.id.mLogInfor);
        valuediaplay1= (TextView) findViewById(R.id.CAN_value_display1);
        valuediaplay2= (TextView) findViewById(R.id.CAN_value_display2);
      //  editT_DisplaySignalValue = (TextView) findViewById(R.id.textView5);
        
        
        mLogInfor.setMovementMethod(ScrollingMovementMethod.getInstance());
        mscrollView = (ScrollView) findViewById(R.id.scrollView1);
        
        
        CheckBox1=(CheckBox)findViewById(R.id.CheckBox1);
        
        mDataField =  (EditText) findViewById(R.id.data_value);
        
       	//final EditText editT_DisplaySignalName = (EditText) findViewById(R.id.textView4);
        
        //private EditText editT_DisplaySignalName = (EditText) findViewById(R.id.textView4);
        
        
        
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

 	/*******Add CAN Spinner***/
    final Spinner mSpinner_CAN_Config = (Spinner) findViewById(R.id.spinner2); 
	// 建立数据源
	final String[] mItems_CAN_Config = getResources().getStringArray(R.array.spinnername_CAN);
 // 建立Adapter并且绑定数据源
 	final ArrayAdapter<String> adapter_CAN_Config=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_CAN_Config);
 	//绑定 Adapter到控件
 	mSpinner_CAN_Config.setAdapter(adapter_CAN_Config);
 	//4.为下拉列表设置各种点击事件，以响应菜单中的文本item被选中了，用setOnItemSelectedListener   
 	mSpinner_CAN_Config.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
														@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
															
							try {
								//以下三行代码是解决问题所在
								/*返回到包含spinner的activity中，再次点击相同的item无法实现跳转操作。研究了半天才发现原因，
								Android spinner本身记住了上一次选择的项，再次点击相同的项是不会触发onitemselected事件的。*/
								Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
								field.setAccessible(true);	//设置mOldSelectedPosition可访问
								field.setInt(mSpinner_CAN_Config, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
							} catch (Exception e) {
								e.printStackTrace();
							}

										
								// TODO Auto-generated method stub
							    // 将所选mySpinner 的值带入myTextView 中      						
							
							Spin_CAN_index= arg2;
							edittext_CAN_Config.setText(SendData_CAN_Config[Spin_CAN_index]+SendData_CANFD_Config[Spin_CANFD_index]);//文本说明
							SendCAN_Config[2]=(byte) ((SendCAN_Config[2]&0xF0)|(Spin_CAN_index&0x0F));
						}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub
								
							}


							});
 	
 	
 	/*******Add CAN FD Spinner***/
    final Spinner mSpinner_CANFD_Config = (Spinner) findViewById(R.id.CANFD_Config_Spinner); 
	// 建立数据源
	final String[] mItems_CANFD_Config = getResources().getStringArray(R.array.spinnername_CANFD);
 // 建立Adapter并且绑定数据源
 	final ArrayAdapter<String> adapter_CANFD_Config=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_CANFD_Config);
 	//绑定 Adapter到控件
 	mSpinner_CANFD_Config.setAdapter(adapter_CANFD_Config);
 	//4.为下拉列表设置各种点击事件，以响应菜单中的文本item被选中了，用setOnItemSelectedListener   
 	mSpinner_CANFD_Config.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
														@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
															
							try {
								//以下三行代码是解决问题所在
								/*返回到包含spinner的activity中，再次点击相同的item无法实现跳转操作。研究了半天才发现原因，
								Android spinner本身记住了上一次选择的项，再次点击相同的项是不会触发onitemselected事件的。*/
								Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
								field.setAccessible(true);	//设置mOldSelectedPosition可访问
								field.setInt(mSpinner_CAN_Config, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
							} catch (Exception e) {
								e.printStackTrace();
							}

										
								// TODO Auto-generated method stub
							    // 将所选mySpinner 的值带入myTextView 中      					
							
							Spin_CANFD_index= arg2;
							edittext_CAN_Config.setText(SendData_CAN_Config[Spin_CAN_index]+SendData_CANFD_Config[Spin_CANFD_index]);//文本说明
							SendCAN_Config[2]=(byte) ((SendCAN_Config[2]&0x0F)|((Spin_CANFD_index<<4)&0xF0));
						}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub
								
							}


							});
 	
        
        button_send_value = (Button) findViewById(R.id.button_send_value);
        button_start = (Button) findViewById(R.id.Start_button);
        button_CAN_config = (Button) findViewById(R.id.CAN_config);
        
        edittext_input_value = (EditText) findViewById(R.id.edittext_input_value);
        edittext_CAN_Config = (EditText) findViewById(R.id.editText1);
        
        
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
       
        
button_CAN_config.setOnClickListener(new View.OnClickListener() {
			
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

                        characteristic.setValue(SendCAN_Config);
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
        
CheckBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
    @Override 
    public void onCheckedChanged(CompoundButton buttonView, 
            boolean isChecked) { 
        // TODO Auto-generated method stub 
        if(isChecked){ 
           // editText1.setText(buttonView.getText()+"选中"); 
        	if(ReceiveIDConfig1_checked == true)
        	{
        		CheckBox1.setChecked(true);
        	checkbox1_checked = true;
        	config_and_Check=true;
        	ReceiveCAN_Config[1]=(byte) 0x01;
        	ReceiveCAN_Config[5]=(byte) (received_ID&0xFF);
        	ReceiveCAN_Config[4]=(byte) ((received_ID>>8)&0xFF);
        	ReceiveCAN_Config[3]=(byte) ((received_ID>>16)&0xFF);
        	ReceiveCAN_Config[2]=(byte) ((received_ID>>24)&0xFF);
            characteristic.setValue(ReceiveCAN_Config);
            mBluetoothLeService.writeCharacteristic(characteristic);
        	}
        	else
        	{
        		CheckBox1.setChecked(false);
        		Toast.makeText(getApplicationContext(), "请先配置接收ID！", Toast.LENGTH_SHORT).show();
        		checkbox1_checked = true;
        		config_and_Check=false;
        	}
        }else{ 
            //editText1.setText(buttonView.getText()+"取消选中"); 
        	checkbox1_checked = false;
        	config_and_Check=true;
        	ReceiveCAN_Config[1]=(byte) 0x01;
        	ReceiveCAN_Config[5]=(byte) 0x00;
        	ReceiveCAN_Config[4]=(byte) 0x00;
        	ReceiveCAN_Config[3]=(byte) 0x00;
        	ReceiveCAN_Config[2]=(byte) 0x00;
        	characteristic.setValue(ReceiveCAN_Config);
            mBluetoothLeService.writeCharacteristic(characteristic);
        } 
    } 
});       
        
        
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    
    /*diaglog start*/
    public void alert_edit(View view){
    	LayoutInflater factory = LayoutInflater.from(this);
    	
    	final TextView editT_DisplaySignalName = (TextView) findViewById(R.id.textView4);
    	//final TextView editT_DisplaySignaValue = (TextView) findViewById(R.id.textView5);
    	
        final View textEntryView = factory.inflate(R.layout.diag_layout, null); 
        
        final EditText editT_ReceiveID = (EditText) textEntryView.findViewById(R.id.editT_ReceiveID); 
        final EditText editT_StartBit = (EditText)textEntryView.findViewById(R.id.editT_StartBit); 
        final EditText editT_SigLength = (EditText) textEntryView.findViewById(R.id.editT_SigLength); 
        final EditText editT_reslouation = (EditText)textEntryView.findViewById(R.id.editT_reslouation);         
        final EditText editT_Offset = (EditText) textEntryView.findViewById(R.id.editT_Offset); 
        final EditText EditT_SignalName = (EditText)textEntryView.findViewById(R.id.EditT_SignalName);
        
        AlertDialog.Builder ad1 = new AlertDialog.Builder(this); 
        ad1.setTitle("信号定义:"); 
        ad1.setIcon(android.R.drawable.ic_dialog_info); 
        ad1.setView(textEntryView); 
        ad1.setPositiveButton("是", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int i) { 
                   
                Log.i("111111", editT_ReceiveID.getText().toString()); 
                
           /*receive the info from the alert diaglog*/
                signal_ReceiveID=editT_ReceiveID.getText().toString();
                signal_StartBit=editT_StartBit.getText().toString();
                signal_SigLength=editT_SigLength.getText().toString();
                signal_reslouation=editT_reslouation.getText().toString();                
                signal_Offset=editT_Offset.getText().toString();
                signal_Name = EditT_SignalName.getText().toString();
                
                
                received_ID= dataconvert.DataStr2Integer(signal_ReceiveID); // convert the text to integer
                 received_StartBit= dataconvert.DataStr2Decmial(signal_StartBit);
                 received_SigLength= dataconvert.DataStr2Decmial(signal_SigLength);
                 received_reslouation= dataconvert.DataStr2Double(signal_reslouation);// need floating type
                 received_Offset= dataconvert.DataStr2Decmial(signal_Offset);
               
                
				editT_DisplaySignalName.setText(signal_Name);
				ReceiveIDConfig1_checked = true;
   
            } 
        }); 
        ad1.setNegativeButton("否", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int i) { 
   
            } 
        }); 
        ad1.show();// 显示对话框

    }
    
    /*diaglog start*/
    public void alert_edit2(View view){
    	LayoutInflater factory = LayoutInflater.from(this);
    	
    	//final TextView editT_DisplaySignalName = (TextView) findViewById(R.id.textView4);
    	final TextView editT_DisplaySignalName2 = (TextView) findViewById(R.id.TextView05);
    	
        final View textEntryView = factory.inflate(R.layout.diag_layout2, null); 
        
        final EditText editT_ReceiveID = (EditText) textEntryView.findViewById(R.id.editT_ReceiveID); 
        final EditText editT_StartBit = (EditText)textEntryView.findViewById(R.id.editT_StartBit); 
        final EditText editT_SigLength = (EditText) textEntryView.findViewById(R.id.editT_SigLength); 
        final EditText editT_reslouation = (EditText)textEntryView.findViewById(R.id.editT_reslouation);         
        final EditText editT_Offset = (EditText) textEntryView.findViewById(R.id.editT_Offset); 
        final EditText EditT_SignalName = (EditText)textEntryView.findViewById(R.id.EditT_SignalName);
        
        AlertDialog.Builder ad1 = new AlertDialog.Builder(this); 
        ad1.setTitle("信号定义:"); 
        ad1.setIcon(android.R.drawable.ic_dialog_info); 
        ad1.setView(textEntryView); 
        ad1.setPositiveButton("是", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int i) { 
                   
                Log.i("111111", editT_ReceiveID.getText().toString()); 
                
           /*receive the info from the alert diaglog*/
                signal_ReceiveID_2=editT_ReceiveID.getText().toString();
                signal_StartBit_2=editT_StartBit.getText().toString();
                signal_SigLength_2=editT_SigLength.getText().toString();
                signal_reslouation_2=editT_reslouation.getText().toString();                
                signal_Offset_2=editT_Offset.getText().toString();
                signal_Name_2 = EditT_SignalName.getText().toString();
                
                
                received_ID_2= dataconvert.DataStr2Integer(signal_ReceiveID); // convert the text to integer
                 received_StartBit_2= dataconvert.DataStr2Decmial(signal_StartBit);
                 received_SigLength_2= dataconvert.DataStr2Decmial(signal_SigLength);
                 received_reslouation_2= dataconvert.DataStr2Double(signal_reslouation);// need floating type
                 received_Offset_2= dataconvert.DataStr2Decmial(signal_Offset);
               
                
				editT_DisplaySignalName2.setText(signal_Name);
                
   
            } 
        }); 
        ad1.setNegativeButton("否", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int i) { 
   
            } 
        }); 
        ad1.show();// 显示对话框

    }
    /*diaglog end*/
    
  /*  
    private int DataStr2Hex(String data) {
    	
    	int value=0,temp_Value2=0,temp_Value=0,char_length=0,loop_i=0;
    	char[] string2char={0,0,0,0,0,0,0,0};
    	byte[] signal_Char2byte={0,0,0,0,0,0,0,0};
        if (data != null) {
        	
        	 char[] signal_string2char = data.toCharArray();
             char_length = signal_string2char.length;
             
             for(loop_i=0;loop_i<char_length;loop_i++)
             {        
            	
	             signal_Char2byte[loop_i]=(byte) signal_string2char[loop_i];
	             if(signal_Char2byte[loop_i]>=48&&signal_Char2byte[loop_i]<=57)
	             {
	            	 temp_Value = signal_Char2byte[loop_i]-48;
	             } 
	             else if(signal_Char2byte[loop_i]>=97&&signal_Char2byte[loop_i]<=102)
	             {temp_Value = signal_Char2byte[loop_i]-97+10;}
	             else if(signal_Char2byte[loop_i]>=65&&signal_Char2byte[loop_i]<=70)
	             {temp_Value = signal_Char2byte[loop_i]-65+10;}
	             
	             temp_Value2=temp_Value<<(4*(char_length-loop_i-1));
	             
	             value = value+ temp_Value2;
	             
	             }
                            
             	
        	
             
            	}
		return value;
    }
*/
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
        	mLogInfor.append(data+"\n");
        	
			mscrollView.scrollBy(0, 30);
        	//mscrollView.scrollTo(0, mLogInfor.getBottom());
        	//scroll2Bottom(mscrollView,mLogInfor);
        	//mscrollView.scrollTo(0, 20); 
        	//Handler mHandler = new Handler();
        	//mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
        	/*
        	mHandler.post(new Runnable() {
            	public void run() {
            		mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
            		}
             }); 	*/
        	/*mscrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                    	mscrollView.post(new Runnable() {
                            public void run() {
                            	mscrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                    */
               
  
        	
        	

           // loginfo[0]=data;
           // loginfo[1]=data;
           // loginfo[2]=data;
            //mDataField.setText( loginfo[0]+loginfo[1]+loginfo[2]);
            //mLogInfor.setText("\n");
    }
    }
    
    public static void scroll2Bottom(final ScrollView scroll, final View inner) 
    { 
    	Handler handler = new Handler(); 
    	handler.post(new Runnable() { 

    	@Override 
    	public void run() { 
    	// TODO Auto-generated method stub 
    	if (scroll == null || inner == null) { 
    	return; 
    	} 
    	// 内层高度超过外层 
    	int offset = inner.getMeasuredHeight() 
    	- scroll.getMeasuredHeight(); 
    	if (offset < 0) { 
    	System.out.println("定位..."); 
    	offset = 0; 
    	} 
    	scroll.scrollTo(0, offset); 
    	} 
    	}); 
    	} 

    
    private void displayValueData1(int data) {
    	
    	valuediaplay1.setText(" "+data);
    }

private void displayValueData1(String data) {
    	
    	valuediaplay1.setText(data);
    }

private void displayValueData2(int data) {
	
	valuediaplay2.setText(" "+data);
}

private void displayValueData2(String data) {
	
	valuediaplay2.setText(data);
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
   // final ScrollView svResult = (ScrollView) findViewById(R.id.svResult);
  //给CheckBox设置事件监听 
    



    
}

