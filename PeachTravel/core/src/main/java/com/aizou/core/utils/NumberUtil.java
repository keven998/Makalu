package com.aizou.core.utils;

public class NumberUtil {
	
	/**
	 * 获取数组中的最大值
	 * @param values
	 * @return
	 */
	public static int getMax(int[] values)  
    {  
        int tmp= Integer.MIN_VALUE;
          
        if(null!=values)  
        {  
            tmp=values[0];  
            for(int i=0; i<values.length; i++)  
            {  
                if(tmp<values[i])  
                {  
                    tmp = values[i];  
                }  
            }  
        }  
          
        return tmp;       
    }  

}
