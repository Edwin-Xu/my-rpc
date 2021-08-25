package cn.edw.seri.util;

/**
 * @author taoxu.xu
 * @date 8/24/2021 8:26 PM
 */
public class ClassUtil {
    /**
     * 是否是匿名内部类
     * */
    public static boolean isAnonymousInnerClass(String className){
        return className != null && className.contains("$");
    }

}
