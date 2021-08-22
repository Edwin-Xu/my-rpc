package cn.edw.seri.core;

import cn.edw.seri.exception.TypeNotFoundException;
import cn.edw.seri.io.SeriByteArrayInputStream;

import java.util.List;

/**
 * 反序列化
 *
 * @author taoxu.xu
 * @date 8/11/2021 3:54 PM
 */
public class Deseri implements Deseriable {

    private SeriByteArrayInputStream bin;

    public Deseri(byte[] bytes) {
        this.bin = new SeriByteArrayInputStream(bytes);
    }

    @Override
    public int readInt()  {
        return bin.readInt();
    }


    @Override
    public byte readByte()  {
        return bin.readByte();
    }

    @Override
    public short readShort() {
        return bin.readShort();
    }

    @Override
    public long readLong()  {
        return bin.readLong();
    }

    @Override
    public float readFloat() {
        return 0;
    }

    @Override
    public double readDouble() {
        return 0;
    }

    @Override
    public boolean readBoolean() {
        return false;
    }

    @Override
    public char readChar() {
        return 0;
    }

    @Override
    public String readString() throws TypeNotFoundException {
        return null;
    }



    @Override
    public Object readObject() throws Exception {
        return bin.readObject();
    }

    @Override
    public List<?> readList() {
        return null;
    }
}
