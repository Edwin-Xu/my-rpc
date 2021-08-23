package cn.edw.seri.core;

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
    public Integer readInt()  {
        return bin.readInt();
    }


    @Override
    public Byte readByte()  {
        return bin.readByte();
    }

    @Override
    public Short readShort() {
        return bin.readShort();
    }

    @Override
    public Long readLong()  {
        return bin.readLong();
    }

    @Override
    public Float readFloat() {
        return bin.readFloat();
    }

    @Override
    public Double readDouble() {
        return bin.readDouble();
    }

    @Override
    public Boolean readBoolean() {
        return bin.readBoolean();
    }

    @Override
    public Character readChar() {
        return bin.readChar();
    }

    @Override
    public String readString()  {
        return bin.readString();
    }

    @Override
    public Object readObject() throws Exception {
        return bin.readObject();
    }

    @Override
    public Object readArray() throws Exception {
        return bin.readArray();
    }

    @Override
    public List<?> readList() {
        return null;
    }
}
