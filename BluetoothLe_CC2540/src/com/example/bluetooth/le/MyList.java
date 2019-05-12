package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyList {
	
	public static <E> ArrayList getSingle(List<String> list) {
        ArrayList tempList = new ArrayList<E>();                    //1,创建新集合
        Iterator it = list.iterator();                            //2,根据传入的集合(老集合)获取迭代器
        
        while(it.hasNext()) {                                    //3,遍历老集合
            Object obj = it.next();                                //记录住每一个元素
            if(!tempList.contains(obj)) {                        //如果新集合中不包含老集合中的元素
                tempList.add(obj);                                //将该元素添加
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
