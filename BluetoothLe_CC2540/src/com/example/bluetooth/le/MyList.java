package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyList {
	
	public static <E> ArrayList getSingle(List<String> list) {
        ArrayList tempList = new ArrayList<E>();                    //1,�����¼���
        Iterator it = list.iterator();                            //2,���ݴ���ļ���(�ϼ���)��ȡ������
        
        while(it.hasNext()) {                                    //3,�����ϼ���
            Object obj = it.next();                                //��¼סÿһ��Ԫ��
            if(!tempList.contains(obj)) {                        //����¼����в������ϼ����е�Ԫ��
                tempList.add(obj);                                //����Ԫ�����
            }
        }
        return tempList;
    }
	
	public static  String getTxt(List<String> list) {
        //ArrayList tempList = new ArrayList<String>();
        String TempString = " " ;
   
        
        for(int i=1;i<list.size()-1;i++){
        	
        	
        	TempString = TempString+list.get(i).substring(2)+" ";
        	
        }
        return TempString;
    }

}
