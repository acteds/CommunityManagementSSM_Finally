package com.aotmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**工具类
 * @author aotmd
 */
public class Tools {
	/**
	 * 删除数组中的指定值  或者数组中的元素包含指定值
	 * @param s   数组
	 * @param dels    指定值
	 * @return 重构后的字符串数组
	 */
    public static String[] delString(String[] s, String dels){
        String[] res = null;
        if(s.length > 0) {
            List<String> tempList = Arrays.asList(s);
            //Arrays.asList(filters) 迭代器实现类 不支持remove() 删除，多一步转化
            List<String> arrList = new ArrayList<>(tempList);
            Iterator<String> it = arrList.iterator();
            while(it.hasNext()) {
                String x = it.next();
                if(x.indexOf(dels) != -1) {
                    it.remove();
                }
            }
            res = new String[arrList.size()];
            arrList.toArray(res);
        }
        return res;
    }
}
