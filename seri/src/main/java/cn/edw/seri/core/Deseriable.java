package cn.edw.seri.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 反序列化
 * @author taoxu.xu
 * @date 8/11/2021 3:54 PM
 */
public interface Deseriable {
    Integer readInt() ;
    Byte readByte() ;
    Short readShort() ;
    Long readLong() ;
    Float readFloat();
    Double readDouble();
    Boolean readBoolean();
    Character readChar();
    String readString() ;
    Object readObject() throws Exception;
    /**
     * 读取数组
     * */
    Object readArray() throws Exception;
    List<?> readList() throws Exception;

    Map<?,?> readMap() throws Exception;
    Set<?> readSet() throws Exception;

    /**
     * 通用方法，根据类型自动判断类型
     * @return 匹配到的类型
     * */
    Object read() throws Exception;

}
