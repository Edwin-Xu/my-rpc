package cn.edw.seri.core;

import java.io.IOException;
import java.util.List;

/**
 * @author taoxu.xu
 * @date 8/10/2021 8:45 PM
 */
public interface Serializable {
    void writeInt(int val) throws IOException;
    void writeByte(byte val);
    void writeShort(short val);
    void writeLong(long val);
    void writeFloat(float val);
    void writeDouble(double val);
    void writeBoolean(boolean bool);
    void writeChar(char ch) throws IOException;
    void writeString(String str) throws IOException;
    void writeObject(Object obj) throws IOException, IllegalAccessException;
    void writeNull(Class< ? >clazz);
    void writeList(List<?> list);
}
