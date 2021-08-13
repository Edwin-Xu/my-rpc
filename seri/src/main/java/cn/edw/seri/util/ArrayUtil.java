package cn.edw.seri.util;

/**
 * @author taoxu.xu
 * @date 8/11/2021 8:42 PM
 */
public class ArrayUtil {
    public static Object[] subArray(Object[] array, int startIndex, int length){
        Object[] dest = new Object[length];
        System.arraycopy(array, startIndex, dest, 0, length);
        return dest;
    }
    public static byte[] subByteArray(byte[] array, int startIndex, int length){
        byte[] dest = new byte[length];
        System.arraycopy(array, startIndex, dest, 0, length);
        return dest;
    }
}
