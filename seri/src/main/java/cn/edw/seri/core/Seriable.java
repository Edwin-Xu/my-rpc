package cn.edw.seri.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 序列化接口.
 * <p>使用包装类</p>
 * @author taoxu.xu
 * @date 8/10/2021 8:45 PM
 */
public interface Seriable {
    void writeInt(Integer val) throws IOException;
    void writeByte(Byte val);
    void writeShort(Short val);
    void writeLong(Long val);
    void writeFloat(Float val);
    void writeDouble(Double val);
    void writeBoolean(Boolean bool);
    void writeChar(Character ch) throws IOException;
    int writeString(String str) throws IOException;
    /**
     * @return 返回一个对象的二进制表示中，除了类型标志位和null标志位的剩余长度，比如null就是0
     * */
    int writeObject(Object obj) throws Exception;
    void writeList(List<?> list) throws Exception;

    void writeMap(Map<?,?> map) throws Exception;
    void writeSet(Set<?> set) throws Exception;
    void write(Object obj) throws Exception;
}
