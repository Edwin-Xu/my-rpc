package cn.edw.seri.io;

import cn.edw.seri.exception.NoMoreDataException;
import cn.edw.seri.exception.TypeNotFoundException;
import cn.edw.seri.protocol.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 自定义输入流
 *
 * @author taoxu.xu
 * @date 8/22/2021 7:13 PM
 */
public class SeriByteArrayInputStream {

    /**
     * default capacity 128 BYTE
     */
    private static final int DEFAULT_CAPACITY = 1 << 8;

    /**
     * MAXIMUM CAPACITY 1G
     */
    private static final int MAXIMUM_CAPACITY = 1 << 20;

    /**
     * 数据
     */
    private byte[] bytes;

    /**
     * 实际使用的数组长度，即二进制数据的真实长度
     */
    private int length;

    /**
     * 容量，当前数组的长度
     */
    private int capacity;

    /**
     * 当前游标的位置，从这个位置开始往后处理
     */
    private int curIndex;

    /**
     * 这里采用copy的方式，还是采用引用的方式。如果采用引用的方式则是不安全的，
     * 外部可以修改数组内容，所以应该采用copy
     *
     * @param bytes 支持数组形式的参数传入
     */
    public SeriByteArrayInputStream(byte[]... bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("The initial data can not be null!");
        }

        int initLength = 0;
        for (byte[] byteArr : bytes) {
            initLength += byteArr.length;
        }

        // length为实际的数据长度
        this.length = initLength;

        // 初始化为 实际需要长度的 1.5倍
        int initCapacity = initLength + (initLength>>1);
        // 处理范围越界问题
        if (initCapacity < DEFAULT_CAPACITY) {
            initCapacity = DEFAULT_CAPACITY;
        } else if (initCapacity > MAXIMUM_CAPACITY) {
            throw new OutOfMemoryError("The memory that the byte array need is larger than 1G!");
        }

        this.capacity = initCapacity;
        this.bytes = new byte[capacity];

        int index = 0;
        for (byte[] byteArr : bytes) {
            final int len = byteArr.length;
            System.arraycopy(byteArr, 0, this.bytes, index, len);
            index += len;
        }

    }

    public synchronized void append(byte[] seriBytes) {
        if (seriBytes != null) {
            append(seriBytes, 0, seriBytes.length);
        }
    }

    /**
     * 支持动态添加序列化的二进制数据
     * @param seriBytes 添加的数据
     * @param start 开始index
     * @param len 取的长度
     */
    public synchronized void append(byte[] seriBytes, int start, int len) {
        if (seriBytes != null) {
            final int seriBytesLen = seriBytes.length;
            // 校验参数
            if (start < 0 || start >= seriBytesLen || len <= 0 || len > seriBytesLen) {
                throw new IllegalArgumentException();
            }
            // 新的长度
            int newCapacity = len + length;

            // 确保容量足够，及时扩容
            ensureCapacity(newCapacity);

            // 复制数据
            System.arraycopy(seriBytes, start, bytes, length, len);
            // 更新length
            length += len;
        }
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new IllegalArgumentException("Negative minCapacity: " + minCapacity);
        }
        if (minCapacity > MAXIMUM_CAPACITY) {
            throw new OutOfMemoryError("The memory of the byte array is larger than 1G!");
        }
        // 如果需要的长度 比 当前容量大， 扩容
        if (minCapacity > capacity) {
            grow(minCapacity);
        }
    }

    /**
     * 扩容
     *
     * @param minCapacity 最小的容量
     */
    private void grow(int minCapacity) {
        // 按最小需要的1.5倍扩容
        int newCapacity = minCapacity + (minCapacity >> 1);
        // 判断是否超出容量
        if (newCapacity > MAXIMUM_CAPACITY) {
            newCapacity = MAXIMUM_CAPACITY;
        }
        // 复制
        bytes = Arrays.copyOf(bytes, newCapacity);
        // 修改当前容量为新的容量
        capacity = newCapacity;
    }

    /**
     * 读取Null标志位并返回结果
     */
    private boolean isNull() {
        return readByteValue() == Booleans.TRUE;
    }

    /**
     * 读取一字节
     */
    private byte readByteValue() {
        return bytes[curIndex++];
    }

    /**
     * 确保还剩余足够的数据
     *
     * @param minLength 还应该具有的数据
     */
    private void ensureEnoughDataLeft(int minLength) {
        if (curIndex + minLength > length) {
            throw new NoMoreDataException();
        }
    }

    public synchronized Byte readByte() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.BYTE) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.BYTE_LENGTH);

        return readByteValue();
    }

    public synchronized Short readShort() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.SHORT) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.SHORT_LENGTH);

        return (short) ((0xff00 & (bytes[curIndex++] << 8))
                | (0xff & bytes[curIndex++]));
    }

    public synchronized Integer readInt() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        // 先检查类型是否匹配
        if (readByteValue() != TypeFlags.INT) {
            throw new TypeNotFoundException();
        }
        // 检查是否null，如果是则返回null
        if (isNull()) {
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.INT_LENGTH);

        return readIntValue();
    }

    /**
     * 读取int值
     */
    private int readIntValue() {
        return (0xff000000 & (bytes[curIndex++] << 24))
                | (0xff0000 & (bytes[curIndex++] << 16))
                | (0xff00 & (bytes[curIndex++] << 8))
                | (0xff & bytes[curIndex++]);
    }

    public synchronized Long readLong() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.LONG) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.LONG_LENGTH);

        return readLongValue();
    }

    private long readLongValue() {
        return (0xff00000000000000L & ((long) bytes[curIndex++] << 56))
                | (0xff000000000000L & ((long) bytes[curIndex++] << 48))
                | (0xff0000000000L & ((long) bytes[curIndex++] << 40))
                | (0xff00000000L & ((long) bytes[curIndex++] << 32))
                | (0xff000000L & ((long) bytes[curIndex++] << 24))
                | (0xff0000L & ((long) bytes[curIndex++] << 16))
                | (0xff00L & ((long) bytes[curIndex++] << 8))
                | (0xffL & ((long) bytes[curIndex++]));
    }

    public synchronized Float readFloat() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.FLOAT) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        final int intOfFloat = readIntValue();
        return Float.intBitsToFloat(intOfFloat);
    }

    public synchronized Double readDouble() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.DOUBLE) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        final long intOfDouble = readLongValue();
        return Double.longBitsToDouble(intOfDouble);
    }

    public synchronized Character readChar() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.CHAR) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.SHORT_LENGTH);

        return (char) ((0xff00 & (bytes[curIndex++] << 8))
                | (0xff & bytes[curIndex++]));
    }

    public synchronized Boolean readBoolean() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.BOOLEAN) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        ensureEnoughDataLeft(PrimitiveTypeByteLengths.BOOLEAN_LENGTH);

        return bytes[curIndex++] == Booleans.TRUE;
    }

    public synchronized String readString() {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.STRING) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        // 读取字符串长度
        final int length = readIntValue();
        ensureEnoughDataLeft(length);
        return readStringValue(length);
    }


    /**
     * 读取字符串
     */
    private String readStringValue(int len) {
        final String res = new String(bytes, curIndex, len, Constants.DEFAULT_CHARSET);
        curIndex += len;
        return res;
    }

    public synchronized Object readObject() throws Exception {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.REGULAR_OBJECT) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
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
                    // list
                    field.set(instance, readList());
                    break;
                case TypeFlags.ARRAY:
                    // Array
                    field.set(instance, readArray());
                    break;
                case TypeFlags.MAP:
                    //  map
                    field.set(instance, readMap());
                    break;
                case TypeFlags.SET:
                    //  set
                    field.set(instance, readSet());
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


    public synchronized Object readArray() throws Exception {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.ARRAY) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }

        // 读取纬度数
        final int dimensionNum = readIntValue();
        // 读取维度值
        int[] dimensionArr = new int[dimensionNum];
        for (int i = 0; i < dimensionNum; i++) {
            dimensionArr[i] = readIntValue();
        }

        // 读取数组类型标识：primitive、wrapped、string、object. 由于primitive没法处理了，所以没用
        byte arrayTypeFlag = readByteValue();

        return readArr(dimensionArr);
    }


    /**
     * 解析类型，读取数组. 这里主要是获取数组的定义类型，用来构造数组
     */
    private Object readArr(int[] dimensionArr) throws Exception {
        Class<?> componentType = null;

        // 读取类型标识符, 注意是数组的类型，而不是元素的类型
        final byte type = readByteValue();

        switch (type) {
            case TypeFlags.BYTE:
                componentType = Byte.class;
                break;
            case TypeFlags.SHORT:
                componentType = Short.class;
                break;
            case TypeFlags.INT:
                componentType = Integer.class;
                break;
            case TypeFlags.LONG:
                componentType = Long.class;
                break;
            case TypeFlags.FLOAT:
                componentType = Float.class;
                break;
            case TypeFlags.DOUBLE:
                componentType = Double.class;
                break;
            case TypeFlags.CHAR:
                componentType = Character.class;
                break;
            case TypeFlags.BOOLEAN:
                componentType = Boolean.class;
                break;
            case TypeFlags.STRING:
                componentType = String.class;
                break;
            case TypeFlags.MAP:
                // TODO
                componentType = Map.class;
                break;
            case TypeFlags.LIST:
                // TODO
                componentType = List.class;
                break;
            case TypeFlags.REGULAR_OBJECT:
                // 对于对象类型，需要先读取类名长度，然后读取类名，在通过反射获取对象
                final int classNameLen = readIntValue();
                final String className = readStringValue(classNameLen);
                try {
                    componentType = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new TypeNotFoundException();
                }
                break;
            default:
                throw new TypeNotFoundException();
        }

        return dfsRead(componentType, dimensionArr, 0);
    }

    /**
     * 递归读取数组
     *
     * @param componentType 数组类型
     * @param dimensionArr  维度数组，a X b X c...
     * @param index         当前处理的index
     */
    private Object dfsRead(Class<?> componentType, int[] dimensionArr, int index) throws Exception {
        final int dimLen = dimensionArr.length;
        // 创建一个子对象
        Object subArr = null;
        // 这是最后一维
        if (index == dimLen - 1) {
            // 读取值，初始化最后一维并返回
            int length = dimensionArr[index];
            subArr = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                // 读取填充
                Array.set(subArr, i, readByType());
            }
        } else {
            // 剩余的维度数
            int dimLeft = dimLen - index;
            // 剩余的维度
            int[] subDimensionArray = new int[dimLeft];
            System.arraycopy(dimensionArr, index, subDimensionArray, 0, dimLeft);
            // 将剩余的维度构造数组
            subArr = Array.newInstance(componentType, subDimensionArray);
            // 递归赋值
            for (int i = 0; i < dimensionArr[index]; i++) {
                final Object value = dfsRead(componentType, dimensionArr, index + 1);
                Array.set(subArr, i, value);
            }
        }
        return subArr;
    }

    /**
     * 通过类型判断读取什么。
     * <p>代码可以重构下，很多类似的Switch可以通过一些数据结构整合</p>
     */
    private Object readByType() throws Exception {
        // 真正读取的的时候在获取类型
        byte type = bytes[curIndex];
        switch (type) {
            case TypeFlags.BYTE:
                return readByte();
            case TypeFlags.SHORT:
                return readShort();
            case TypeFlags.INT:
                return readInt();
            case TypeFlags.LONG:
                return readLong();
            case TypeFlags.FLOAT:
                return readFloat();
            case TypeFlags.DOUBLE:
                return readDouble();
            case TypeFlags.CHAR:
                return readChar();
            case TypeFlags.BOOLEAN:
                return readBoolean();
            case TypeFlags.STRING:
                return readString();
            case TypeFlags.LIST:
                return readList();
            case TypeFlags.MAP:
                return readMap();
            case TypeFlags.SET:
                return readSet();
            case TypeFlags.REGULAR_OBJECT:
                return readObject();
            default:
                throw new TypeNotFoundException();
        }
    }

    /**
     * 读取list。
     * TODO 要不要考虑 是那种List？ 不考虑的会出问题，不同list的特性差异很大。所以需要考虑的
     */
    public synchronized List<?> readList() throws Exception {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.LIST) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        // 获取全限定名长度
        final int classNameLength = readIntValue();
        // 获取全限定名
        String className = readStringValue(classNameLength);

        // 反射获取Class
        final Class<?> listClass = Class.forName(className);
        // 注意，默认构造器不能是一个参数为 int 的构造器，LinkedList等就没有带int参构造器
        final Constructor<?> constructor = listClass.getConstructor();

        // 获取数组的大小
        int size = readIntValue();
        // 使用size作为初始尺寸构造List
        List<Object> list = (List<Object>) constructor.newInstance();

        for (int i = 0; i < size; i++) {
            // 元素类型， curIndex不往前移动
            byte eleType = bytes[curIndex];
            Object ele = readAnyTypeObject(eleType);
            list.add(ele);
        }

        return list;
    }

    /**
     * 读取未知类型，任何类型皆可。 抽离的公共方法
     */
    private Object readAnyTypeObject(byte type) throws Exception {
        Object ele = null;
        switch (type) {
            case TypeFlags.BYTE:
                ele = readByte();
                break;
            case TypeFlags.SHORT:
                ele = readShort();
                break;
            case TypeFlags.INT:
                ele = readInt();
                break;
            case TypeFlags.LONG:
                ele = readLong();
                break;
            case TypeFlags.FLOAT:
                ele = readFloat();
                break;
            case TypeFlags.DOUBLE:
                ele = readDouble();
                break;
            case TypeFlags.CHAR:
                ele = readChar();
                break;
            case TypeFlags.STRING:
                ele = readString();
                break;
            case TypeFlags.BOOLEAN:
                ele = readBoolean();
                break;
            case TypeFlags.ARRAY:
                ele = readArray();
                break;
            case TypeFlags.LIST:
                ele = readList();
                break;
            case TypeFlags.MAP:
                //TODO
                break;
            case TypeFlags.SET:
                //tODO
                break;
            case TypeFlags.REGULAR_OBJECT:
                ele = readObject();
                break;
            default:
                throw new TypeNotFoundException();
        }
        return ele;
    }

    /**
     * 读取map对象
     */
    public synchronized Map<?, ?> readMap() throws Exception {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.MAP) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        // 获取全限定名长度
        final int classNameLength = readIntValue();
        // 获取全限定名
        final String className = readStringValue(classNameLength);

        // 反射获取Class
        final Class<?> listClass = Class.forName(className);
        // 获取构造器
        final Constructor<?> constructor = listClass.getConstructor();

        // 获取map中元素数量
        int size = readIntValue();
        Map<Object, Object> map = (Map<Object, Object>) constructor.newInstance();

        for (int i = 0; i < size; i++) {
            byte type = bytes[curIndex];
            Object key = readAnyTypeObject(type);
            type = bytes[curIndex];
            Object value = readAnyTypeObject(type);
            map.put(key, value);
        }

        return map;
    }


    /**
     * 读取set对象
     */
    public synchronized Set<?> readSet() throws Exception {
        ensureEnoughDataLeft(Lengths.TYPE_NULL_FLAG_LENGTH);

        if (readByteValue() != TypeFlags.SET) {
            throw new TypeNotFoundException();
        }
        if (isNull()) {
            return null;
        }
        // 获取全限定名长度
        final int classNameLength = readIntValue();
        // 获取全限定名
        final String className = readStringValue(classNameLength);

        // 反射获取Class
        final Class<?> listClass = Class.forName(className);
        // 获取构造器
        final Constructor<?> constructor = listClass.getConstructor();

        // 获取set中元素数量
        int size = readIntValue();
        Set<Object> set = (Set<Object>) constructor.newInstance();

        // 读取set元素
        for (int i = 0; i < size; i++) {
            byte type = bytes[curIndex];
            Object ele = readAnyTypeObject(type);
            set.add(ele);
        }

        return set;
    }

    /**
     * 自动判断类型并读取。
     * <p>推荐使用</p>
     */
    public synchronized Object read() throws Exception {
        return readByType();
    }

    /**
     * 重置，使用默认容量
     * */
    public void reset(){
        bytes = new byte[DEFAULT_CAPACITY];
        capacity = DEFAULT_CAPACITY;
        length = 0;
        curIndex = 0;
    }
}
