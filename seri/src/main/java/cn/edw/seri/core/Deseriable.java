package cn.edw.seri.core;

import cn.edw.seri.exception.TypeNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 反序列化
 * @author taoxu.xu
 * @date 8/11/2021 3:54 PM
 */
public interface Deseriable {
    int readInt() throws TypeNotFoundException;
    byte readByte() throws TypeNotFoundException;
    short readShort() throws TypeNotFoundException;
    long readLong() throws TypeNotFoundException;
    float readFloat();
    double readDouble();
    boolean readBoolean();
    char readChar();
    String readString() throws TypeNotFoundException;
    Object readObject() throws Exception;
    List<?> readList();
}
