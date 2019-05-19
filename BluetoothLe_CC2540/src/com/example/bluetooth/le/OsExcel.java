package com.example.bluetooth.le;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OsExcel{
    private static final CAN_DS CAN_DS = null;
	TextView tv1,tv2;
    Button bOpen;
    EditText et;
    static int row;
    static int columns;

 
    static String loadFromSDFile(String fname) {
        fname="/"+fname;
        String result=null;
        try {
        	String File_Path = Environment.getExternalStorageDirectory().getPath()+fname;
            File f=new File(File_Path);
            int length=(int)f.length();
            byte[] buff=new byte[length];
            FileInputStream fin=new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result=new String(buff,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
           // Toast.makeText(createExcel.this,"没有找到指定文件",Toast.LENGTH_SHORT).show();
        }
        return result;
    }
    
    static class CurCell{
        int row;
        int col;
        String content;
    }
    
    static class CAN_DS{
        String ID;
        byte[] Data_byte={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        byte multframe;
     
    }
    
    static class CAN_Receive_Var{
        int ID;
        byte StartBit;
        byte SigLength;
        double reslouation;
        byte Offset;
        String content;
    }
    
  
    private void UpdateExcel(String fname){
    	fname="/"+fname;
        try {
        	String File_Path = Environment.getExternalStorageDirectory().getPath()+fname;
            Workbook mWorkbook = Workbook.getWorkbook(new File(File_Path));
            WritableWorkbook mWritableWorkbook = Workbook.createWorkbook(new File(File_Path), mWorkbook);
            WritableSheet mWritableSheet = mWritableWorkbook.getSheet(0);
            WritableCell mWritableCell = mWritableSheet.getWritableCell(1, 0);
            Label mLabel = (Label)mWritableCell;
            mLabel.setString("modify");
            mWritableWorkbook.write();
            mWritableWorkbook.close();
            mWorkbook.close();
        } catch (BiffException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WriteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static List<CurCell> mArrayList = new ArrayList<CurCell>();
    
    static List ReadExcel(String fileName,int sheet){
    	
    	mArrayList.clear();
    	
    	fileName="/"+fileName;
        try {
        	int cnt=0;
        	String File_Path = Environment.getExternalStorageDirectory().getPath()+fileName;
            //InputStream mInputStream = getResources().getAssets().open(File_Path);
            //Workbook wb = Workbook.getWorkbook(mInputStream);
            Workbook wb = Workbook.getWorkbook(new File(File_Path));
            Sheet mSheet = wb.getSheet(sheet);
           row = mSheet.getRows();
           columns = mSheet.getColumns();
            Log.i("W","Total Row: " + row + ", Total Columns: " + columns);
            for(int i= 0 ; i < row ; i ++){
                int cols = mSheet.getRow(i).length;
                for(int j = 0 ; j < cols ; j ++){
                    Cell temp = mSheet.getCell(j, i);
                    String content = temp.getContents();
                    Log.i("W",j + " ," + i + " ," + content);
                    CurCell mCell = new CurCell();
                    List mCell_rtn;
                    mCell.row = i;
                    mCell.col = j;
                    mCell.content = content;
                    cnt++;
				mArrayList.add(mCell);
				
				//mCell_rtn=(List) mArrayList.get(cnt);
				//System.out.println(mArrayList.get(cnt));
				
                }
            }
            wb.close();
           // mInputStream.close();
        } catch (BiffException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return mArrayList;
    }
    
    static int Getcolumns(){
    return columns;
    }
    
    static int Getrows(){
    return row;
    }
    
    static List GetContents(){
    return mArrayList;
    }
    
    static List Getcolumns_list(List<CurCell> mArrayList,int colums,int Total_Row,int Total_Colum){
    	
    	List columns_list= new ArrayList();   	
    	
		String signal_name;
		
		CurCell temp_cell;
		
		int i;
		
		for(i=0;i<Total_Row;i++)
		{
			temp_cell = (CurCell) mArrayList.get(i*Total_Colum+colums);
			signal_name=temp_cell.content;
			columns_list.add(signal_name);
		}
		
		return columns_list;
    }
    
    static List Getrows_list(List list,int rows,int Total_Row,int Total_Colum){
       	List rows_list= new ArrayList();        	

		String signal_name;
		
		CurCell temp_cell;
		
		int i;
		
		for(i=0;i<Total_Colum;i++)
		{
			temp_cell = (CurCell) mArrayList.get(i+(Total_Colum*rows));
			signal_name=temp_cell.content;
			rows_list.add(signal_name);
		}
        return rows_list;
    }
    
    @SuppressWarnings("null")
	static CAN_DS GetCANData_list(List<CurCell> mArrayList,int rows,int Total_Row,int Total_Colum){
    	
    	List columns_list= new ArrayList();   	
    	CAN_DS CAN_Send_CMD = new CAN_DS();
    	
    	
		;		
		CurCell temp_cell;
		
		int i,j;
		 temp_cell= mArrayList.get(12);
		 
		 String signal_name = temp_cell.content;
		 CAN_Send_CMD.ID=signal_name;
		 
		/*
			for(j=0;j<Total_Colum-1;j++)
			{
			temp_cell = (CurCell) mArrayList.get(rows*Total_Colum+1+j);
			signal_name=temp_cell.content;
			columns_list.add(signal_name);
			}
		*/
		
		return CAN_Send_CMD;
    }
    
    static List GetReceive_var(List list,int rows,int Total_Row,int Total_Colum){
       	List rows_list= new ArrayList();        	

		String signal_name;
		
		CAN_Receive_Var temp_Var;
		
		int i;
		
		for(i=0;i<Total_Colum;i++)
		{
			//temp_Var.ID = (int) list.get(i);			
			//rows_list.add(temp_Var);
		}
        return rows_list;
    }
    
    
}



