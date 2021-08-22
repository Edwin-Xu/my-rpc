package cn.edw.seri.io;

import cn.edw.seri.exception.NoMoreDataException;
import cn.edw.seri.exception.TypeNotFoundException;
import cn.edw.seri.protocol.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 自定义输入流
 * @author taoxu.xu
 * @date 8/22/2021 7:13 PM
 */
public class SeriByteArrayInputStream {
    
    /**
     * 数据
     * */
    private byte[] bytes;
    
    private int length;
    
    /**
     * 当前游标的位置，从这个位置开始往后处理
     * */
    private int curIndex;

    public SeriByteArrayInputStream(byte[] bytes) {
        if (bytes == null){
            throw new IllegalArgumentException("The initial data can not be null!");
        }
        this.bytes = bytes;
        length = bytes.length;
    }
    
    /**
     * 读取Null标志位并返回结果
     * */
    private boolean isNull(){
        return readByteValue() == Booleans.TRUE;
    }
    /**
     * 读取一字节
     * */
    private byte readByteValue(){
        return bytes[curIndex++];
    }
    
    /**
     * 确保还剩余足够的数据
     * @param minLength 还应该具有的数据
     * */
    private void ensureEnoughDataLeft(int minLength){
        if (curIndex + minLength > length ){
            throw new NoMoreDataException();
        }
    }

    public Byte readByte()  {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.BYTE) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.BYTE_LENGTH);

        return readByteValue();
    }

    public Short readShort()  {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.SHORT) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.SHORT_LENGTH);

        return  (short)((0xff00 & (bytes[curIndex++] << 8))
                | (0xff & bytes[curIndex++]));
    }
    
    public Integer readInt()  {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        // 先检查类型是否匹配
        if (readByteValue() != TypeFlags.INT) {
            throw new TypeNotFoundException();
        }
        // 检查是否null，如果是则返回null
        if (isNull()){
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.INT_LENGTH);
        
        return readIntValue();
    }

    /**
     * 读取int值
     * */
    private int readIntValue(){
        return (0xff000000 & (bytes[curIndex++] << 24))
                | (0xff0000 & (bytes[curIndex++] << 16))
                | (0xff00 & (bytes[curIndex++] << 8))
                | (0xff & bytes[curIndex++]);
    }

    public Long readLong()  {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.LONG) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.LONG_LENGTH);

        return readLongValue();
    }

    private long readLongValue(){
        return    (0xff00000000000000L & ((long) bytes[curIndex++] << 56))
                | (0xff000000000000L & ((long) bytes[curIndex++] << 48))
                | (0xff0000000000L & ((long) bytes[curIndex++] << 40))
                | (0xff00000000L & ((long) bytes[curIndex++] << 32))
                | (0xff000000L & ((long) bytes[curIndex++] << 24))
                | (0xff0000L   & ((long) bytes[curIndex++] << 16))
                | (0xff00L     & ((long) bytes[curIndex++] << 8))
                | (0xffL       & ((long) bytes[curIndex++]));
    }

    public Float readFloat(){
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.FLOAT) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        final int intOfFloat = readIntValue();
        return Float.intBitsToFloat(intOfFloat);
    }

    public Double readDouble(){
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.DOUBLE) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        final long intOfDouble = readLongValue();
        return Double.longBitsToDouble(intOfDouble);
    }

    public Character readChar() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.CHAR) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.SHORT_LENGTH);

        return  (char)((0xff00 & (bytes[curIndex++] << 8))
                | (0xff & bytes[curIndex++]));
    }

    public Boolean readBoolean() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.BOOLEAN) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.BOOLEAN_LENGTH);

        return  bytes[curIndex++] == Booleans.TRUE;
    }

    public String readString(){
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.STRING) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }
        // 读取字符串长度
        final int length = readIntValue();
        ensureEnoughDataLeft(length);
        return readStringValue(length);
    }


    /**
     * 读取字符串
     * */
    private String readStringValue(int len){
        final String res = new String(bytes, curIndex, len, Constants.DEFAULT_CHARSET);
        curIndex+=len;
        return res;
    }

    public Object readObject() throws Exception{
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.REGULAR_OBJECT) {
            throw new TypeNotFoundException();
        }
        if (isNull()){
            return null;
        }

        // 读取对象长度(除开类型、null两个flag)
        final int objectSize = readIntValue();
        ensureEnoughDataLeft(objectSize);

        // 读取全限定名长度
        int fullClassNameLength = readIntValue();

        // 读取全限定名
        final String fullClassName = readStringValue(fullClassNameLength);

        // 通过反射构造一个对象
        final Class<?> clazz = Class.forName(fullClassName);
        final Constructor<?> constructor = clazz.getConstructor();
        final Object instance = constructor.newInstance();

        // 读取field数量
        final int fieldNum = readIntValue();

        // 给实例一个个填充field
        for (int i = 0; i < fieldNum; i++) {
            // field名长度
            final int fieldNameLen = readIntValue();
            // field名
            final String fieldName = readStringValue(fieldNameLen);

            // 反射获取Field
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            // 类型， curIndex不往前移动
            byte fieldType = bytes[curIndex];

            switch (fieldType) {
                case TypeFlags.BYTE:
                    field.set(instance, readByte());
                    break;
                case TypeFlags.SHORT:
                    field.set(instance, readShort());
                    break;
                case TypeFlags.INT:
                    field.set(instance, readInt());
                    break;
                case TypeFlags.LONG:
                    field.set(instance, readLong());
                    break;
                case TypeFlags.FLOAT:
                    field.set(instance, readFloat());
                    break;
                case TypeFlags.DOUBLE:
                    field.set(instance, readDouble());
                    break;
                case TypeFlags.BOOLEAN:
                    field.set(instance, readBoolean());
                    break;
                case TypeFlags.STRING:
                    field.set(instance, readString());
                    break;
                case TypeFlags.CHAR:
                    field.set(instance, readChar());
                    break;
                case TypeFlags.LIST:
                    // TODO
                    break;
                case TypeFlags.REGULAR_OBJECT:
                    final Object object = readObject();
                    field.set(instance, object);
                    break;
                default:
                    throw new TypeNotFoundException();
            }
        }
        return instance;
    }
}
