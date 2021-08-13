package cn.edw.seri.core;

import cn.edw.seri.exception.TypeNotFoundException;
import cn.edw.seri.protocol.Constants;
import cn.edw.seri.protocol.Type;
import cn.edw.seri.util.ArrayUtil;
import cn.edw.seri.util.BitConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 反序列化
 *
 * @author taoxu.xu
 * @date 8/11/2021 3:54 PM
 */
public class Deseri implements Deserializable {

    private final byte[] bytes;

    private int curPos = 0;

    public Deseri(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public int readInt() throws TypeNotFoundException {
        // 先检查类型是否匹配
        if (bytes[curPos++] != Type.INT) {
            throw new TypeNotFoundException();
        }
        /*
        * return ((bytes[curPos++] & 0xFF) << 24)
                | ((bytes[curPos++] & 0xFF) << 16)
                | ((bytes[curPos++] & 0xFF) << 8)
                | (bytes[curPos++] & 0xFF);
        * */
        final int res = BitConverter.toInt(bytes, curPos);
        curPos += 4;
        return res;
    }


    @Override
    public byte readByte() throws TypeNotFoundException {
        if (bytes[curPos++] != Type.BYTE) {
            throw new TypeNotFoundException();
        }
        return bytes[curPos++];
    }

    @Override
    public short readShort() throws TypeNotFoundException {
        if (bytes[curPos++] != Type.SHORT) {
            throw new TypeNotFoundException();
        }
        return (short) (((bytes[curPos++] & 0xFF) << 8)
                | (bytes[curPos++] & 0xFF));
    }

    @Override
    public long readLong() throws TypeNotFoundException {
        if (bytes[curPos++] != Type.LONG) {
            throw new TypeNotFoundException();
        }
        final long res = BitConverter.toLong(bytes, curPos);
        curPos += 8;
        return res;
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
        if (bytes[curPos++] != Type.STRING) {
            throw new TypeNotFoundException();
        }
        return readStringVal();
    }

    /**
     * 单纯读取字符串值
     */
    private String readStringVal() {
        final int len = BitConverter.toInt(bytes, curPos);
        curPos += 4;
        final byte[] dest = ArrayUtil.subByteArray(bytes, curPos, len);
        curPos += len;
        return new String(dest, Constants.DEFAULT_CHARSET);
    }

    @Override
    public Object readObject() throws TypeNotFoundException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if (bytes[curPos++] != Type.REGULAR_OBJECT) {
            throw new TypeNotFoundException();
        }
        final int fullClassNameLen = BitConverter.toInt(bytes, curPos);
        curPos += 4;

        System.out.println("fullClassNameLen:" + fullClassNameLen);

        final byte[] fullClassNameByteArr = ArrayUtil.subByteArray(this.bytes, curPos, fullClassNameLen);
        final String fullClassName = BitConverter.toString(fullClassNameByteArr, Constants.DEFAULT_CHARSET);
        System.out.println("fullClassName:" + fullClassName);
        final Class<?> clazz = Class.forName(fullClassName);
        final Constructor<?> constructor = clazz.getConstructor();
        final Object instance = constructor.newInstance();

        curPos += fullClassNameLen;

        final int objLen = BitConverter.toInt(bytes, curPos);
        curPos += 4;
        System.out.println("objLen:" + objLen);

        int end = curPos + objLen;

        while (curPos < end) {
            final int fieldNameLen = BitConverter.toInt(bytes, curPos);
            System.out.println("fieldNameLen:" + fieldNameLen);
            curPos += 4;
            final String fieldName = BitConverter.toString(ArrayUtil.subByteArray(bytes, curPos, fieldNameLen));
            System.out.println("fieldName:" + fieldName);
            curPos += fieldNameLen;

            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            final byte fieldType = bytes[curPos++];

            switch (fieldType) {
                case Type.BYTE:
                    field.set(instance, bytes[curPos++]);
                    break;
                case Type.SHORT:
                    final short fieldShortVal = BitConverter.toShort(bytes, curPos);
                    curPos += 2;
                    field.set(instance, fieldShortVal);
                    break;
                case Type.INT:
                    final int fieldVal = BitConverter.toInt(bytes, curPos);
                    curPos += 4;
                    field.set(instance, fieldVal);
                    break;
                case Type.LONG:
                    final long fieldLongVal = BitConverter.toLong(bytes, curPos);
                    curPos += 8;
                    field.set(instance, fieldLongVal);
                    break;
                case Type.FLOAT:
                    final float fieldFloatVal = BitConverter.toFloat(bytes, curPos);
                    curPos += 4;
                    field.set(instance, fieldFloatVal);
                    break;
                case Type.DOUBLE:
                    final double fieldDoubleVal = BitConverter.toDouble(bytes, curPos);
                    curPos += 8;
                    field.set(instance, fieldDoubleVal);
                    break;
                case Type.BOOLEAN:
                    field.set(instance, bytes[curPos++] == 1);
                    break;
                case Type.STRING:
                    int length = BitConverter.toInt(bytes, curPos);
                    curPos += 4;
                    final String fieldStrVal = BitConverter.toString(ArrayUtil.subByteArray(bytes, curPos, length));
                    curPos += length;
                    field.set(instance, fieldStrVal);
                    break;
                case Type.CHAR:
                    final char chs = BitConverter.toChar(bytes, curPos);
                    curPos += 2;
                    field.set(instance, chs);
                    break;
                case Type.LIST:
                    break;
                case Type.REGULAR_OBJECT:
                    final Object object = readObject();
                    field.set(instance, object);
                    break;
                default:

                    throw new TypeNotFoundException();
            }

        }

        return instance;
    }

    @Override
    public List<?> readList() {
        return null;
    }
}
