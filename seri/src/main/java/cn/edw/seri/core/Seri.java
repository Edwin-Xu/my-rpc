package cn.edw.seri.core;

import cn.edw.seri.protocol.Constants;
import cn.edw.seri.protocol.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 发现Seri这个名字很好，采用之.
 * <p>
 * 序列化
 * <p>这里每个方法应该只被调用一次，</p>
 *
 * @author taoxu.xu
 * @date 8/10/2021 7:29 PM
 */
public class Seri extends AbstractSeri implements Serializable {
    // TODO 协议应该变更一下，可以重复写入
    protected ByteArrayOutputStream bout = new ByteArrayOutputStream();

    @Override
    public void writeInt(int val) {
        // 先插入一字节的类型标志位
        writeByteVal(Type.INT, bout);
        // 再写入数据
        writeIntVal(val, bout);
    }

    @Override
    public void writeByte(byte val) {
        writeByteVal(Type.BYTE, bout);
        writeByteVal(val, bout);
    }

    @Override
    public void writeShort(short val) {
        writeByteVal(Type.SHORT, bout);
        writeShortVal(val, bout);
    }

    @Override
    public void writeLong(long val) {
        writeByteVal(Type.LONG, bout);
        writeLongVal(val, bout);
    }

    @Override
    public void writeFloat(float val) {

    }

    @Override
    public void writeDouble(double val) {

    }

    @Override
    public void writeBoolean(boolean bool) {
        writeByteVal(Type.BOOLEAN, bout);
        writeBooleanVal(bool, bout);
    }

    @Override
    public void writeChar(char ch) throws IOException {
        writeByteVal(Type.CHAR, bout);
        writeCharVal(ch, bout);
    }

    @Override
    public void writeString(String str) throws IOException {
        writeByteVal(Type.STRING, bout);
        writeStringVal(str, bout);
    }

    @Override
    public void writeObject(Object obj) throws IOException, IllegalAccessException {
        writeByteVal(Type.REGULAR_OBJECT, bout);

        final ByteArrayOutputStream byteArrayOutputStream = writeObjectVal(obj);
        final int size = byteArrayOutputStream.size();
        if (size > 0){
            final byte[] fullClassNameBytes = obj.getClass().getName().getBytes(Constants.DEFAULT_CHARSET);
            // 写入全限定名
            writeIntVal(fullClassNameBytes.length, bout);
            bout.write(fullClassNameBytes);

            // 写入真实数据
            writeIntVal(size, bout);
            bout.write(byteArrayOutputStream.toByteArray());
        }else{
            // 是空的话，把长度设置为0
            writeIntVal(0, bout);
        }
    }

    @Override
    public void writeList(List<?> list) {

    }

    @Override
    public void writeNull(Class<?> clazz) {

    }

    public ByteArrayOutputStream getOutputStream() {
        return bout;
    }
    public byte[] getBytes(){
        return bout.toByteArray();
    }

    public static void main(String[] args) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bout.write(128);

        final byte[] bytes = bout.toByteArray();
        System.out.println(bytes.length);
        System.out.println(bytes[0]);
    }
}
