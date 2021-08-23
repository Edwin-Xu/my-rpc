package cn.edw.seri.core;

import java.util.List;

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
    List<?> readList();
}
