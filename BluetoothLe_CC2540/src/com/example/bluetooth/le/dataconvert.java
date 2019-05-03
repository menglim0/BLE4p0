package com.example.bluetooth.le;

import  java.lang.String;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class dataconvert {
	
	public void String2hex_ID(){
		
		
	}
	
	public void String2hex_Signal(){
		
		
	}
	
	public static int DataStr2Integer(String data) {
    	
    	int value=0,temp_Value2=0,temp_Value=0,char_length=0,loop_i=0;
    	char[] string2char={0,0,0,0,0,0,0,0};
    	byte[] signal_Char2byte={0,0,0,0,0,0,0,0};
        if (data != null) {
        	
        	string2char = data.toCharArray();
             char_length = string2char.length;
             
             for(loop_i=0;loop_i<char_length;loop_i++)
             {        
            	
	             signal_Char2byte[loop_i]=(byte) string2char[loop_i];
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
	
	public static int DataStr2Decmial(String data) {
    	
    	int value=0,temp_Value2=0,temp_Value=0,char_length=0,loop_i=0;
    	char[] string2char={0,0,0,0,0,0,0,0};
    	byte[] signal_Char2byte={0,0,0,0,0,0,0,0};
        if (data != null) {
        	
        	string2char = data.toCharArray();
             char_length = string2char.length;
             
             for(loop_i=0;loop_i<char_length;loop_i++)
             {        
            	
	             signal_Char2byte[loop_i]=(byte) string2char[loop_i];
	             if(signal_Char2byte[loop_i]>=48&&signal_Char2byte[loop_i]<=57)
	             {
	            	 temp_Value = signal_Char2byte[loop_i]-48;
	             } 
	             else if(signal_Char2byte[loop_i]>=97&&signal_Char2byte[loop_i]<=102)
	             {temp_Value = signal_Char2byte[loop_i]-97+10;}
	             else if(signal_Char2byte[loop_i]>=65&&signal_Char2byte[loop_i]<=70)
	             {temp_Value = signal_Char2byte[loop_i]-65+10;}
	             
	             temp_Value2=temp_Value;
	             
	             value = value*10+ temp_Value2;
	             
	             }
                            
             	
        	
             
            	}
		return value;
    }
	
public static double DataStr2Double(String data) {
 
    	double temp_Value3=1;

        if ((data != "0")&&(data != null)) {        	
        	
        	temp_Value3 = Double.parseDouble(data);        	
        }
        else
        {
        	String resoluation=null;
			Log.i(resoluation, "defalut resoluation =1");
        	//Log.d(TAG, "defalut");
        }
		return temp_Value3;
    }
/*
received_ID= dataconvert.DataStr2Integer(signal_ReceiveID); // convert the text to integer
received_StartBit= dataconvert.DataStr2Decmial(signal_StartBit);
received_SigLength= dataconvert.DataStr2Decmial(signal_SigLength);
received_reslouation= dataconvert.DataStr2Float(signal_reslouation);// need floating type
received_Offset= dataconvert.DataStr2Decmial(signal_Offset);
*/
public static int Data_ConvertFromCAN(int StartBit,int StartByte,int Length ,float reslouation,int Offset,byte[] data) {
	
	int length=0,loop_i=0,loop_i_p=0,zero_pos=0,bytePos,bitPos,bitLength;
	int value=0;
	
	//value=subBytes(data,StartBit,Length);
	
	return value;
}

public static int subBytes(String data,int point1,int point2) {
	int bs=0,bs2d=0;

	String temp1="0";
	char[] temp_char={0,0,0,0};
	
	String Str1 = new String("www.runoob.com");
    char[] Str2 = new char[6];

    try {
    	data.getChars(1, 2, Str2, 0);
        System.out.print("¿½±´µÄ×Ö·û´®Îª£º" );
        System.out.println(Str2 );
    } catch( Exception ex) {
        System.out.println("´¥·¢Òì³£...");
    }
	
		//temp=data.substring(point1,point2);
		//temp1=data.substring(1,3);
		data.getChars(1,3,temp_char,0);
		temp_char = data.toCharArray();
		System.out.println("temp_Str:"+temp1);
		System.out.println(data.substring(1,4));
		bs=Integer.valueOf(temp1);
		bs2d=0;
	
	
    return bs;
}

public static long DataStramp(byte[] data,int startbit, int length) {
	
	
	byte[] signal_Char2byte={0,0,0,0,0,0,0,0};
	int startbit_inLong,maxbyte,loop_i,startbit_Offset;
	long CANData=0,value,CANData_mask=1;
	int[] table ={56,57,58,59,60,61,62,63,
			48,49,50,51,52,53,54,55,40,41,
			42,43,44,45,46,47,32,33,34,35,
			36,37,38,39,24,25,26,27,28,29,
			30,31,16,17,18,19,20,21,22,23,
			8,9,10,11,12,13,14,15,
			0,1,2,3,4,5,6,7};
   
		startbit_inLong=table[startbit];
		
    	//string2char = data.toCharArray();
       //  char_length = string2char.length;
         
         for(loop_i=6;loop_i<14;loop_i++)
         {        
        	 CANData=CANData<<8;
        	 CANData =CANData+ (long) ((int)data[loop_i]&0xFF);       
        }
         //for(startbit_Offset=0;startbit_Offset<startbit_inLong;startbit_inLong++)
         //{
        	 CANData=CANData>>startbit_inLong;
        // }
        	 for(startbit_Offset=0;startbit_Offset<length-1;startbit_Offset++)
        	 {
        	 CANData_mask =CANData_mask<<1;
        	 CANData_mask =CANData_mask|0x01; 
        	 
        	 }
        	 value = CANData&CANData_mask;
         
         
         //value = CANData;
	return value;
}

public static String Data2Str(byte[] data) {
	
	int loop_i,DataHexStr_length_index,DataHexStr_length;
	long CANData=0;
	String value,DataHexStr;
	         
         for(loop_i=6;loop_i<14;loop_i++)
         {        
        	 CANData=CANData<<8;
        	 CANData =CANData+ (long) ((int)data[loop_i]&0xFF);
     
        }
         
     	DataHexStr=Long.toHexString(CANData);
    	DataHexStr_length = DataHexStr.length();
        	
    	for(DataHexStr_length_index=0;DataHexStr_length_index<16-DataHexStr_length;DataHexStr_length++)
    	{
    		DataHexStr="0"+DataHexStr;
    	}
         value = DataHexStr;
	return value;
}

public static byte[] StrToChar_List(List<String> list) { 
	List<byte[]> CAN_Date= new ArrayList<byte[]>();
	//List<byte[]> mArrayList_CANData = new ArrayList<byte[]>();
    byte[] b = new byte[13]; 
    String temp = null;
    byte[] d = new byte[30];
    byte temp_result_Byte,temp_result_Byte1,temp_result_Byte2;
    char[] tempchar= new char[8];
    int i,j,loop_i,char_length,Temp_int,Data_index=0;
    int value=0,temp_Value2=0,temp_Value=0;
    
	char[] string2char={0,0,0,0,0,0,0,0};
	byte[] signal_Char2byte={0,0,0,0,0,0,0,0};
    
    for(i=1;i<list.size();i++)
    {
    	if(i<list.size()-1)
    	{
    	temp=list.get(i).substring(2); 
    	}
    	else
    	{
    		temp=list.get(i);
    	}
    	System.out.println("output----" + temp);
    	tempchar=temp.toCharArray();
    	char_length=tempchar.length;
    	
        	for(loop_i=0;loop_i<char_length;loop_i++)
            {     d[loop_i]=0;
	             signal_Char2byte[loop_i]=(byte) tempchar[loop_i];
	             
	             
	             if(signal_Char2byte[loop_i]>=48&&signal_Char2byte[loop_i]<=57)
	             {
	            	 temp_Value = signal_Char2byte[loop_i]-48;
	             } 
	             else if(signal_Char2byte[loop_i]>=97&&signal_Char2byte[loop_i]<=102)
	             {temp_Value = signal_Char2byte[loop_i]-97+10;}
	             else if(signal_Char2byte[loop_i]>=65&&signal_Char2byte[loop_i]<=70)
	             {temp_Value = signal_Char2byte[loop_i]-65+10;}
	          
	             d[loop_i]=(byte)temp_Value;
	             System.out.println("loop_i----"+loop_i+"--" + d[loop_i]);
	             
	             //b[Data_index] = (byte) (temp_Value%256); 
	             //Data_index++;
	             
	             
	             /*
	             temp_Value2=temp_Value<<(4*(char_length-loop_i-1));	             
	             value = value+ temp_Value2;*/	             
	         }
        	
        	if(char_length%2==0)
        	{
	        	for(loop_i=0;loop_i<char_length;loop_i++)
	        	{
	        		
		        		temp_result_Byte1=(byte) (d[loop_i]<<4);
		        		temp_result_Byte2=d[loop_i+1];
		        		temp_result_Byte =(byte)( temp_result_Byte1+temp_result_Byte2);
	        		
	        		  //temp_result_Byte= (byte) ((byte) d[loop_i]<<4+(byte) d[loop_i+1]); 
	        		  b[Data_index]=temp_result_Byte;
	        		 System.out.println("output----" +d[loop_i]+"----"+ d[loop_i+1]);
	        		 System.out.println("output----" +Data_index+"----"+ b[Data_index]);
		             Data_index++;
		             loop_i++;
	        	}
        	}
        	else
        	{	
        		b[Data_index]=d[0];
        		Data_index++;
	        	for(loop_i=1;loop_i<char_length;loop_i++)
	        	{
	        		
		        		temp_result_Byte1=(byte) (d[loop_i]<<4);
		        		temp_result_Byte2=d[loop_i+1];
		        		temp_result_Byte =(byte)( temp_result_Byte1+temp_result_Byte2);
	        		
	        		  //temp_result_Byte= (byte) ((byte) d[loop_i]<<4+(byte) d[loop_i+1]); 
	        		  b[Data_index]=temp_result_Byte;
	        		 System.out.println("output----" +d[loop_i]+"----"+ d[loop_i+1]);
	        		 System.out.println("output----" +Data_index+"----"+ b[Data_index]);
		             Data_index++;
		             loop_i++;
	        	}
        	}
        	
    	//b[i] = (byte) (value%256);   
       	}
    
    //for(i=0;i<Data_index;i++)
    
    CAN_Date.add(b);

    return b; 
}

}
