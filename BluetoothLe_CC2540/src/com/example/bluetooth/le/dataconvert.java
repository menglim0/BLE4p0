package com.example.bluetooth.le;

import  java.lang.String;


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
 
    	double temp_Value3=0;

        if (data != null) {        	
        	
        	temp_Value3 = Double.parseDouble(data);        	
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



}
