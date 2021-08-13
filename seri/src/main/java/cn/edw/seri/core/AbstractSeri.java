package cn.edw.seri.core;

import cn.edw.seri.protocol.Constants;
import cn.edw.seri.protocol.TypeName;
import cn.edw.seri.util.BitConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author taoxu.xu
 * @date 8/10/2021 9:14 PM
 */
public abstract class AbstractSeri {

    public ByteArrayOutputStream writeIntVal(int val) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeIntVal(val, bout);
    }

    protected ByteArrayOutputStream writeIntVal(int val, ByteArrayOutputStream bout) {
        bout.write((val >> 24) & 0xFF);
        bout.write((val >> 16) & 0xFF);
        bout.write((val >> 8) & 0xFF);
        bout.write(val & 0xFF);
        return bout;
    }


    protected ByteArrayOutputStream writeByteVal(byte val) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeByteVal(val, bout);
    }

    public ByteArrayOutputStream writeByteVal(byte val, ByteArrayOutputStream bout) {
        bout.write(val);
        return bout;
    }


    public ByteArrayOutputStream writeShortVal(short val) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeShortVal(val, bout);
    }

    public ByteArrayOutputStream writeShortVal(short val, ByteArrayOutputStream bout) {
        bout.write((val >> 8) & 0xFF);
        bout.write((val) & 0xFF);
        return bout;
    }

    public ByteArrayOutputStream writeLongVal(long val) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeLongVal(val, bout);
    }

    public ByteArrayOutputStream writeLongVal(long val, ByteArrayOutputStream bout) {
        bout.write((int) ((val >> 56) & 0xFF));
        bout.write((int) ((val >> 48) & 0xFF));
        bout.write((int) ((val >> 40) & 0xFF));
        bout.write((int) ((val >> 32) & 0xFF));
        bout.write((int) ((val >> 24) & 0xFF));
        bout.write((int) ((val >> 16) & 0xFF));
        bout.write((int) ((val >> 8) & 0xFF));
        bout.write((int) ((val) & 0xFF));
        return bout;
    }


    public ByteArrayOutputStream writeFloatVal(float val) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return bout;
    }

    public ByteArrayOutputStream writeFloatVal(float val, ByteArrayOutputStream bout) {
        final byte[] bytes = BitConverter.getBytes(val);
        bout.write(bytes, 0, bytes.length);
        return bout;
    }

    public ByteArrayOutputStream writeDoubleVal(double val) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeDoubleVal(val, bout);
    }

    public ByteArrayOutputStream writeDoubleVal(double val, ByteArrayOutputStream bout) {
        final byte[] bytes = BitConverter.getBytes(val);
        bout.write(bytes, 0, bytes.length);
        return bout;
    }

    public ByteArrayOutputStream writeBooleanVal(boolean bool) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeBooleanVal(bool, bout);
    }

    public ByteArrayOutputStream writeBooleanVal(boolean bool, ByteArrayOutputStream bout) {
        bout.write(bool ? 1 : 0);
        return bout;
    }

    public ByteArrayOutputStream writeCharVal(char ch) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeCharVal(ch, bout);
    }

    public ByteArrayOutputStream writeCharVal(char ch, ByteArrayOutputStream bout) throws IOException {
        bout.write(BitConverter.getBytes(ch));
        return bout;
    }

    public ByteArrayOutputStream writeStringVal(String str) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        return writeStringVal(str, bout);
    }


    public ByteArrayOutputStream writeStringVal(String str, ByteArrayOutputStream bout) throws IOException {
        final byte[] bytes = str.getBytes(Constants.DEFAULT_CHARSET);
        // 插入长度
        writeIntVal(bytes.length, bout);
        // 插入数据
        bout.write(bytes);
        return bout;
    }

    public void writeListVal(List<?> list) {

    }

    public ByteArrayOutputStream writeObjectVal(Object obj) throws IllegalAccessException, IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        if (obj != null) {

            // 下面遍历Field
            final Class<?> clazz = obj.getClass();
            final Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                final Type type = field.getGenericType();
                final String typeName = type.getTypeName();

                // 设置可见性
                field.setAccessible(true);

                final byte[] fieldNameBytes = field.getName().getBytes(Constants.DEFAULT_CHARSET);
                // 写入属性名长度、名称
                writeIntVal(fieldNameBytes.length, bout);
                System.out.println("fieldNameBytes.length:"+fieldNameBytes.length);
                bout.write(fieldNameBytes);

                final Object fieldValue = field.get(obj);
                if (TypeName.isByte(typeName)) {
                    final byte val = (byte) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.BYTE, bout);
                    writeByteVal(val, bout);
                } else if (TypeName.isShort(typeName)) {
                    final short val = (short) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.SHORT, bout);
                    writeShortVal(val, bout);
                } else if (TypeName.isInt(typeName)) {
                    final int val = (int) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.INT, bout);
                    writeIntVal(val, bout);
                } else if (TypeName.isLong(typeName)) {
                    final long val = (long) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.LONG, bout);
                    writeLongVal(val, bout);
                } else if (TypeName.isFloat(typeName)) {
                    final float val = (float) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.FLOAT, bout);
                    writeFloatVal(val, bout);
                } else if (TypeName.isDouble(typeName)) {
                    final double val = (double) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.DOUBLE, bout);
                    writeDoubleVal(val, bout);
                } else if (TypeName.isBoolean(typeName)) {
                    final boolean val = (boolean) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.BOOLEAN, bout);
                    writeBooleanVal(val, bout);
                } else if (TypeName.isChar(typeName)) {
                    final char val = (char) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.CHAR, bout);
                    writeCharVal(val, bout);
                } else if (TypeName.isString(typeName)) {
                    final String val = (String) fieldValue;
                    writeByteVal(cn.edw.seri.protocol.Type.STRING, bout);
                    writeStringVal(val, bout);
                } else if (TypeName.isList(typeName)) {

                } else {
                    // 其他的视为 普通对象，需要递归处理
                    final ByteArrayOutputStream byteArrayOutputStream = writeObjectVal(fieldValue);
                    final int size = byteArrayOutputStream.size();
                    if (size > 0) {
                        writeByteVal(cn.edw.seri.protocol.Type.REGULAR_OBJECT, bout);

                        // 写入大小
                        writeIntVal(size, bout);
                        // 写入真正的数据
                        bout.write(byteArrayOutputStream.toByteArray());
                    }

                }

            }
        }
        return bout;
    }
}
