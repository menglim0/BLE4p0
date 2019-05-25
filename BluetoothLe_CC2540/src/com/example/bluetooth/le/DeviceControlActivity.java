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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.example.bluetooth.le.OsExcel.CAN_Receive_Var;
import com.example.bluetooth.le.OsExcel.CurCell;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
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
import android.os.Message;
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
    TextView[] editT_DisplaySignalName;
    TextView[] valuediaplay;
   // private editT_DisplaySignalName[]=new TextView[3];
    //private TextView editT_DisplaySignalName2;
    //private TextView editT_DisplaySignalName3;
    
    private CheckBox CheckBox1=null,ReceiveID_CheckBox1=null,ReceiveID_CheckBox2=null,ReceiveID_CheckBox3=null;
    
    private BluetoothLeService mBluetoothLeService;
  
    private boolean mConnected = false,checkbox1_checked=false,checkbox2_checked=false,ReceiveIDConfig1_checked = false;
    private boolean config_and_Check =false,config_and_Check_2 =false;
    private boolean Sending_Frame =false,Sending_CMD =false;
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
    int ReceiveID1;
    
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
   
   byte[] StartCmd = { 					   
					   (byte)0xE6,0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x22,0x20,0x00,0x00,0x00,0x00,0x00,0x00,0x06
					   };
   
   byte[] StartCmd_Backup = { (byte)0xE6,0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x10,0x03,0x00,0x00,0x00,0x00,0x00,0x01,
		   (byte)0xE6,0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x02,0x27,0x09,0x00,0x00,0x00,0x00,0x00,0x02,
		   (byte)0xE6,0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x30,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x03,
		   (byte)0xE6,0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x10,0x0E,0x27,0x0A,0x00,0x00,0x00,0x00,0x04,
		   (byte)0xE6,0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x21,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x05,					   
		   (byte)0xE6,0x14,(byte)0xDA,(byte)0xE1,(byte)0xF1,0x22,0x20,0x00,0x00,0x00,0x00,0x00,0x00,0x06
		   };
   
			   
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
   
   /* 处理Excel 相关变量*/
   int Column_Excel_Sheet0,Column_Excel_Sheet1,Column_Excel_Sheet2,Column_Excel_Sheet_Receive;
   int Row_Excel_Sheet0,Row_Excel_Sheet1,Row_Excel_Sheet_Receive;
   List<byte[]> mArrayList_CANData,mArrayList_Receive_Var,mArrayList_CANData_MultiFrame;
   int[] Receive_ID_Var; 
   List<String> mArrayList_Receive_SignalName;
   
   CAN_Receive_Var CAN_Receive_Unpack;
   private List<CAN_Receive_Var> Unpack_Array;
   
   
   ArrayList<String> signal_name_Array;
   
   List<String> mArrayList_Row;
   List<ArrayList<String>>mArrayList_Row_total;
   /*开线程*/
   private Handler Drawhandler;
   private Timer drawTimer = new Timer();
   private TimerTask drawTask;
   boolean FirewallsPassReq,FirewallsPassReq_init;
   boolean KeepServiceAlive;
   int FirewallIndex;
   int SendingCmd_Index,SendingCmd_Index_Max,KeepAlive_Index;
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
            	int display_index=0;
            	long convertData;
            	double phsValue_FromCAN;
            //	byte tempdata = (byte) 0x81;
            	
            	String DataHexStr = "0";
            	
            	DataHexStr=dataconvert.Data2Str(data_B);
            	
            	for(int index=0;index<3;index++)
            	{
            		if(Unpack_Array.get(index).ID ==firstByte)
            		{
            			display_index=index;
            			if(data_B[1]==(byte)0x81)
            			{
            			double value_CAN=dataconvert.Data2Unpack(   data_B,
									            					index,
									            					Unpack_Array.get(index).ID,
									            					Unpack_Array.get(index).StartBit,
									            					Unpack_Array.get(index).SigLength,
									            					Unpack_Array.get(index).Offset,
									            					Unpack_Array.get(index).reslouation);
            			
                        	DecimalFormat df=new DecimalFormat(".##");	            		
                    		//displayValueData1(df.format(phsValue_FromCAN));
                    		displayValueData(df.format(value_CAN),index,valuediaplay);
            			}
            		}            		
            	}
/*
            	if(Unpack_Array.get(0).ID ==firstByte  &&  data_B[1]==(byte)0x81)
            	{
	            	
	                	convertData = dataconvert.DataStramp(data_B,received_StartBit,  received_SigLength);
	                 	System.out.println("convertData----" + convertData);
	                 	
	            		data_CAN =(int) (convertData-received_Offset);
	            		
	            		phsValue_FromCAN=(double)data_CAN*received_reslouation;
	            		//DecimalFormat df=new DecimalFormat(".##");	            		
	            		//displayValueData1(df.format(phsValue_FromCAN));
	            		//displayValueData(df.format(phsValue_FromCAN),0,valuediaplay);
	        
            	}*/
            	//Data2Unpack(byte[] data_B,int index,int ID,byte Startbit,byte Length,byte Offset,double resoluation)
            	/*
            	double value_CAN=dataconvert.Data2Unpack(data_B,0,Unpack_Array.get(0).ID,Unpack_Array.get(0).StartBit,Unpack_Array.get(0).SigLength,Unpack_Array.get(0).Offset,Unpack_Array.get(0).reslouation);
            	DecimalFormat df=new DecimalFormat(".##");	            		
        		//displayValueData1(df.format(phsValue_FromCAN));
        		displayValueData(df.format(value_CAN),0,valuediaplay);
        		*/
        		/*
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
            	}*/
      
 
            }
        }
    };    
    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @SuppressWarnings("unchecked")
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
        valuediaplay1= (TextView) findViewById(R.id.Receive_Value_01);
        valuediaplay2= (TextView) findViewById(R.id.Receive_Value_02);
      //  editT_DisplaySignalValue = (TextView) findViewById(R.id.data_value);
        
        
        mLogInfor.setMovementMethod(ScrollingMovementMethod.getInstance());
        mscrollView = (ScrollView) findViewById(R.id.scrollView1);
        
        
        CheckBox1=(CheckBox)findViewById(R.id.checkBox1);
        
        mDataField =  (EditText) findViewById(R.id.data_value);
        
        editT_DisplaySignalName=new TextView[3];
        editT_DisplaySignalName[0] = (TextView) findViewById(R.id.SignalName_01);
        editT_DisplaySignalName[1] = (TextView) findViewById(R.id.SignalName_02);
        editT_DisplaySignalName[2] = (TextView) findViewById(R.id.SignalName_03); 
        
        valuediaplay =new TextView[3];
        
        valuediaplay[0]= (TextView) findViewById(R.id.Receive_Value_01);
        valuediaplay[1]= (TextView) findViewById(R.id.Receive_Value_02);
        valuediaplay[2]= (TextView) findViewById(R.id.Receive_Value_03);
        
        ReceiveID_CheckBox1=(CheckBox)findViewById(R.id.Receive_CheckBox01);
        ReceiveID_CheckBox2=(CheckBox)findViewById(R.id.Receive_CheckBox02);
        ReceiveID_CheckBox3=(CheckBox)findViewById(R.id.Receive_CheckBox03);
        /*保持屏幕常亮*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        /**/
      //这里的Handler实例将配合下面的Timer实例，完成定时更新图表的功能
        
        
        
        
        /*处理Excel数据*/
        
        String Excel_path="signal.xls";
        List<CurCell> mArrayList_Sheet1 = new ArrayList<CurCell>();
        List<CurCell> mArrayList_Sheet_Receive = new ArrayList<CurCell>();
        
        mArrayList_Sheet1=OsExcel.ReadExcel(Excel_path,1);
        Column_Excel_Sheet1=OsExcel.Getcolumns(); 
        Row_Excel_Sheet1 = OsExcel.Getrows();
        List<String> mArrayList_Column = new ArrayList<String>();
        mArrayList_Row = new ArrayList<String>();
        mArrayList_Row_total=new ArrayList<ArrayList<String>>(); 
        mArrayList_CANData = new ArrayList<byte[]>();
        mArrayList_CANData_MultiFrame = new ArrayList<byte[]>();
        //mArrayList_Column=OsExcel.Getcolumns_list(mArrayList_Sheet1,0,6,11);
        
        mArrayList_Receive_SignalName = new ArrayList<String>();
        for(int row_i=1;row_i<Row_Excel_Sheet1;row_i++)
        {
        	mArrayList_Row=OsExcel.Getrows_list(mArrayList_Sheet1,row_i,Row_Excel_Sheet1,11);
        	mArrayList_Row_total.add((ArrayList<String>) mArrayList_Row);
        	mArrayList_CANData.add(dataconvert.StrToChar_List(mArrayList_Row));
        	mArrayList_Receive_SignalName.add(mArrayList_Row.get(0));
        }
        
        /*处理接收数据*/
       
        mArrayList_Sheet_Receive=OsExcel.ReadExcel(Excel_path,2); //接收Excel内容
        Column_Excel_Sheet_Receive=OsExcel.Getcolumns();   //获取列数   
        List<String> mArrayList_Receive_Column = new ArrayList<String>();
        List<String> mArrayList_Receive_Row = new ArrayList<String>();
        
        mArrayList_Receive_Var=new ArrayList<byte[]>();
        
        
        Column_Excel_Sheet_Receive=OsExcel.Getcolumns();
        Row_Excel_Sheet_Receive = OsExcel.Getrows();
        int ID_Hex=0;
        Receive_ID_Var = new int[Row_Excel_Sheet_Receive];
        
        
        Unpack_Array=new ArrayList<CAN_Receive_Var>();
        for(int row_i=1;row_i<Row_Excel_Sheet_Receive;row_i++)
        {
        	CAN_Receive_Unpack = new CAN_Receive_Var();
        	mArrayList_Receive_Row=OsExcel.Getrows_list(mArrayList_Sheet1,row_i,2,6);
        	ID_Hex= dataconvert.DataStr2Integer(mArrayList_Receive_Row.get(1).substring(2));
        	Receive_ID_Var[row_i-1]=ID_Hex;
        	
        	CAN_Receive_Unpack.ID=dataconvert.DataStr2Integer(mArrayList_Receive_Row.get(1).substring(2));;
        	CAN_Receive_Unpack.StartBit= (byte) dataconvert.DataStr2Decmial(mArrayList_Receive_Row.get(2));
        	CAN_Receive_Unpack.SigLength= (byte) dataconvert.DataStr2Decmial(mArrayList_Receive_Row.get(3));
        	CAN_Receive_Unpack.reslouation= dataconvert.DataStr2Double(mArrayList_Receive_Row.get(4));// need floating type
        	CAN_Receive_Unpack.Offset= (byte) dataconvert.DataStr2Decmial(mArrayList_Receive_Row.get(5));
        	Unpack_Array.add(CAN_Receive_Unpack);
        	//String SignalName=mArrayList_Receive_Row.get(0);
        	editT_DisplaySignalName[row_i-1].setText(mArrayList_Receive_Row.get(0));
        	//mArrayList_Receive_Var.add(dataconvert.StrToChar_List(mArrayList_Row));        	
        }
        
        
        ReceiveID1=dataconvert.DataStr2Integer(mArrayList_Receive_Row.get(1).substring(2));
        
        received_ID= ReceiveID1; // convert the text to integer
        received_StartBit= dataconvert.DataStr2Decmial(mArrayList_Receive_Row.get(2));
        received_SigLength= dataconvert.DataStr2Decmial(mArrayList_Receive_Row.get(3));
        received_reslouation= dataconvert.DataStr2Double(mArrayList_Receive_Row.get(4));// need floating type
        received_Offset= dataconvert.DataStr2Decmial(mArrayList_Receive_Row.get(5));
      
        /*End of 处理Excel数据*/
        
        /* convert data*/
        //SendData_Display[0][0] = (String) SendData[0][0];
        
        /*******Add Spinner***/
        final Spinner mSpinner = (Spinner) findViewById(R.id.spinner1); 
    	// 建立数据源
		final String[] mItems = getResources().getStringArray(R.array.spinnername);
     // 建立Adapter并且绑定数据源
     	//final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		
		signal_name_Array = MyList.getSingle(mArrayList_Receive_SignalName);
		
		final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, signal_name_Array);
		
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
										if(!mArrayList_CANData_MultiFrame.isEmpty())
										{
										mArrayList_CANData_MultiFrame.clear();
										}
										SendingCmd_Index_Max=0;
										String inputString="Sending Command:";	
										//mArrayList_CANData_MultiFrame.add(mArrayList_CANData.get(arg2));
     									// TODO Auto-generated method stub
     								    // 将所选mySpinner 的值带入myTextView 中      
										for(int i=arg2;i<Row_Excel_Sheet1-1;i++)
										{	
										if(signal_name_Array.get(arg2)==mArrayList_Receive_SignalName.get(i))
										{
										inputString +="\n"+ MyList.getTxt(mArrayList_Row_total.get(i));
										mArrayList_CANData_MultiFrame.add(mArrayList_CANData.get(i));
										Sending_Frame =true;
										SendingCmd_Index_Max++;
										}
										}
										
												//mArrayList_Row.get(1)+mArrayList_Row.get(2)+mArrayList_Row.get(3)+mArrayList_Row.get(4);
     							edittext_input_value.setText(inputString);//文本说明
     							Spin_index= arg2;
     							}

     								@Override
     								public void onNothingSelected(AdapterView<?> arg0) {
     									// TODO Auto-generated method stub
     									
     								}


     								});

 	
 	
        
        button_send_value = (Button) findViewById(R.id.button_send_value);
        button_start = (Button) findViewById(R.id.Start_button);
   
        
        edittext_input_value = (EditText) findViewById(R.id.data_value);
      
        
        
        button_send_value.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final boolean action_state;
				
				Sending_CMD =true;
				
			}
		});
        
        button_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final boolean action_state;
				FirewallsPassReq=true;
				FirewallIndex=0;		
                
                edittext_input_value.setText("");
			}
		});
       
        

        
CheckBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
    @Override 
    public void onCheckedChanged(CompoundButton buttonView, 
            boolean isChecked) { 
						        // TODO Auto-generated method stub 
						        if(isChecked){ 
						           
						            characteristic.setValue(mArrayList_CANData.get(0));
						            mBluetoothLeService.writeCharacteristic(characteristic);
						            KeepServiceAlive=true;
						        	
						        }else{ 
						            //editText1.setText(buttonView.getText()+"取消选中"); 
						        	KeepServiceAlive=false;
						        	/*
						 byte[] temp = new byte[14];
						 byte[] temp2 = new byte[14];
						 temp=mArrayList_CANData.get(0);
						 for(int index_i=0;index_i<14;index_i++)
						 {
							 temp2[index_i]=temp[index_i];
						 }
						 temp2[1]=(byte) ((byte) temp2[1]|0x80);
						        	
						        	characteristic.setValue(temp2);
						            mBluetoothLeService.writeCharacteristic(characteristic);*/
						        } 
						    } 
						});  

ReceiveID_CheckBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
    @Override 
    public void onCheckedChanged(CompoundButton buttonView, 
            boolean isChecked) { 
						        // TODO Auto-generated method stub
    							ReceiveCAN_Config[5]=(byte)0x01;
						       	ReceiveCAN_Config[4]=(byte) (Receive_ID_Var[0]&0xFF);
						    	ReceiveCAN_Config[3]=(byte) ((Receive_ID_Var[0]>>8)&0xFF);
						    	ReceiveCAN_Config[2]=(byte) ((Receive_ID_Var[0]>>16)&0xFF);
						    	ReceiveCAN_Config[1]=(byte) ((Receive_ID_Var[0]>>24)&0xFF);
						        if(isChecked){ 
						           
						            characteristic.setValue(ReceiveCAN_Config);
						            mBluetoothLeService.writeCharacteristic(characteristic);
						        	
						        }else{ 
						    
						        	ReceiveCAN_Config[5]=(byte) ((byte) ReceiveCAN_Config[5]|0x80);
						        	characteristic.setValue(ReceiveCAN_Config);
						            mBluetoothLeService.writeCharacteristic(characteristic);
						        } 
						    } 
						});  
ReceiveID_CheckBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
    @Override 
    public void onCheckedChanged(CompoundButton buttonView, 
            boolean isChecked) { 
							    	// TODO Auto-generated method stub
    								ReceiveCAN_Config[5]=(byte)0x02;
							       	ReceiveCAN_Config[4]=(byte) (Receive_ID_Var[1]&0xFF);
							    	ReceiveCAN_Config[3]=(byte) ((Receive_ID_Var[1]>>8)&0xFF);
							    	ReceiveCAN_Config[2]=(byte) ((Receive_ID_Var[1]>>16)&0xFF);
							    	ReceiveCAN_Config[1]=(byte) ((Receive_ID_Var[1]>>24)&0xFF);
							        if(isChecked){ 
							           
							            characteristic.setValue(ReceiveCAN_Config);
							            mBluetoothLeService.writeCharacteristic(characteristic);
							        	
							        }else{ 
							    
							        	ReceiveCAN_Config[5]=(byte) ((byte) ReceiveCAN_Config[5]|0x80);
							        	characteristic.setValue(ReceiveCAN_Config);
							            mBluetoothLeService.writeCharacteristic(characteristic);
							        } 
						    } 
						});  
ReceiveID_CheckBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
    @Override 
    public void onCheckedChanged(CompoundButton buttonView, 
            boolean isChecked) { 
						    	// TODO Auto-generated method stub
    							ReceiveCAN_Config[5]=(byte)0x03;
						       	ReceiveCAN_Config[4]=(byte) (Receive_ID_Var[2]&0xFF);
						    	ReceiveCAN_Config[3]=(byte) ((Receive_ID_Var[2]>>8)&0xFF);
						    	ReceiveCAN_Config[2]=(byte) ((Receive_ID_Var[2]>>16)&0xFF);
						    	ReceiveCAN_Config[1]=(byte) ((Receive_ID_Var[2]>>24)&0xFF);
						        if(isChecked){ 
						           
						            characteristic.setValue(ReceiveCAN_Config);
						            mBluetoothLeService.writeCharacteristic(characteristic);
						        	
						        }else{ 
						    
						        	ReceiveCAN_Config[5]=(byte) ((byte) ReceiveCAN_Config[5]|0x80);
						        	characteristic.setValue(ReceiveCAN_Config);
						            mBluetoothLeService.writeCharacteristic(characteristic);
						        } 
						    } 
						});  
        
        
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //FirewallsPassReq_init=true;


		Drawhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) 
        {
         //刷新图表||FirewallsPassReq_init==true
        	if(FirewallsPassReq==true||FirewallsPassReq_init==true)
        	{
        		KeepAlive_Index=0;
        		//updateChart();
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
                           // characteristic.setValue(mArrayList_CANData.get(FirewallIndex+2));                      
                            mBluetoothLeService.writeCharacteristic(characteristic);                       
                            
                            Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
                 
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                    }
                    
                   // FirewallIndex++;
            		//if(FirewallIndex>=6)
            		//{
            			FirewallIndex=0;
            			FirewallsPassReq=false;
            			FirewallsPassReq_init=false;
            		//}
        		
        	}
        	if(Sending_CMD==true &&Sending_Frame==true)
        		
        	{	KeepAlive_Index=0;
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

                        //characteristic.setValue(StartCmd);
                        characteristic.setValue(mArrayList_CANData_MultiFrame.get(SendingCmd_Index));                      
                        mBluetoothLeService.writeCharacteristic(characteristic);                       
                        
                        Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
             
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                
                SendingCmd_Index++;
        		if(SendingCmd_Index>=SendingCmd_Index_Max)
        		{
        			SendingCmd_Index=0;
        			Sending_CMD=false;
        			Sending_Frame=false;
        		}
        		
        	}
        	if(KeepServiceAlive==true)
        	{
/*
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

                        //characteristic.setValue(StartCmd);
                       // characteristic.setValue(mArrayList_CANData.get(0));                      
                       // mBluetoothLeService.writeCharacteristic(characteristic);                       
                        
                        Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
             
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                */
                KeepAlive_Index++;
        		if(KeepAlive_Index>=40)
        		{
        			KeepAlive_Index=0;
        			characteristic.setValue(mArrayList_CANData.get(0));                      
                    mBluetoothLeService.writeCharacteristic(characteristic);  
        		}
        		
        	
        		
        	}
        	
         super.handleMessage(msg);
        }
        };
        
        drawTask = new TimerTask() {
        @Override
        public void run() {
        Message message = new Message();
            message.what = 1;
            Drawhandler.sendMessage(message);
        }
        };
        
        drawTimer.schedule(drawTask, 1000, 500);
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

		//FirewallsPassReq_init=true;
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
    	
    	valuediaplay[0].setText(" "+data);
    }
    


private void displayValueData1(String data) {
    	
	valuediaplay[0].setText(data);
    }

private void displayValueData2(int data) {
	
	valuediaplay2.setText(" "+data);
}

private void displayValueData2(String data) {
	
	valuediaplay2.setText(data);
}


private void displayValueData(int data,int index,TextView[] view) {
	
	view[index].setText(" "+data);
}

private void displayValueData(String data,int index,TextView[] view) {
	
	view[index].setText(" "+data);
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

