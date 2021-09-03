package cn.edw.seri.core;

import cn.edw.seri.exception.TypeNotFoundException;
import cn.edw.seri.io.SeriByteArrayOutputStream;
import cn.edw.seri.protocol.*;
import cn.edw.seri.util.ClassUtil;
import cn.edw.seri.util.ObjectUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 发现Seri这个名字很好，采用之.
 * <p>
 * 序列化
 * <p>这里每个方法应该只被调用一次，</p>
 *
 * @author taoxu.xu
 * @date 8/10/2021 7:29 PM
 */
public class Seri implements Seriable {
    /**
     * 使用我自己的IO流
     */
    protected SeriByteArrayOutputStream bout = new SeriByteArrayOutputStream();

    /**
     * 对应基本类型包装类和对象，在类型后插入1字节表示是否为null。
     */
    private void writeNullFlag(boolean isNull) {
        bout.writeBool(isNull);
    }

    /**
     * 写入类型标志
     */
    private void writeTypeFlag(byte type) {
        bout.writeByte(type);
    }

    @Override
    public void writeInt(Integer val) {
        // 先插入一字节的类型标志位
        writeTypeFlag(TypeFlags.INT);
        // 判断是否为空，插入标志位
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        // 再写入数据
        if (!isNull) {
            bout.writeInt(val);
        }
    }

    @Override
    public void writeByte(Byte val) {
        writeTypeFlag(TypeFlags.BYTE);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            bout.writeByte(val);
        }
    }

    @Override
    public void writeShort(Short val) {
        writeTypeFlag(TypeFlags.SHORT);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            bout.writeShort(val);
        }
    }

    @Override
    public void writeLong(Long val) {
        writeTypeFlag(TypeFlags.LONG);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            bout.writeLong(val);
        }
    }

    @Override
    public void writeFloat(Float val) {
        writeTypeFlag(TypeFlags.FLOAT);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            bout.writeFloat(val);
        }
    }

    @Override
    public void writeDouble(Double val) {
        writeTypeFlag(TypeFlags.DOUBLE);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            bout.writeDouble(val);
        }
    }

    @Override
    public void writeBoolean(Boolean val) {
        writeTypeFlag(TypeFlags.BOOLEAN);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            bout.writeBool(val);
        }
    }

    @Override
    public void writeChar(Character val) {
        writeTypeFlag(TypeFlags.CHAR);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            bout.writeChar(val);
        }
    }

    @Override
    public int writeString(String val) {
        writeTypeFlag(TypeFlags.STRING);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            return bout.writeString(val);
        }
        return 0;
    }

    @Override
    public int writeObject(Object val) throws Exception {
        // 类型标志位
        writeTypeFlag(TypeFlags.REGULAR_OBJECT);

        // null标志位
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);

        // null，后面没有了
        if (isNull) {
            return 0;
        }
        // 对象不为null

        // 先跳过一个int位置，预留给后面填充长度
        final int indexForObjectSize = bout.skipInts(1);
        // 统计该对象的成员属性所占用的字节数
        int byteCount = 0;

        final Class<?> clazz = val.getClass();

        // 获取全限定名
        final byte[] fullClassNameBytes = clazz.getName()
                .getBytes(Constants.DEFAULT_CHARSET);

        // 写入全限定名长度
        final int fullClassNameLength = fullClassNameBytes.length;
        bout.writeInt(fullClassNameLength);
        byteCount += PrimitiveTypeByteLengths.INT_LENGTH;

        // 写入全限定名
        bout.write(fullClassNameBytes);
        byteCount += fullClassNameLength;

        // 遍历field
        final Field[] fields = clazz.getDeclaredFields();

        // 写入field个数
        final int fieldLength = fields.length;
        bout.writeInt(fieldLength);
        byteCount += fieldLength;

        for (Field field : fields) {
            // 获取类型名称
            final Type type = field.getType();
            final String typeName = type.getTypeName();

            // 设置可见性
            field.setAccessible(true);

            // 属性名长度、名称
            final byte[] fieldNameBytes = field.getName().getBytes(Constants.DEFAULT_CHARSET);
            final int fieldNameLength = fieldNameBytes.length;
            bout.writeInt(fieldNameLength);
            byteCount += fieldNameLength;
            bout.write(fieldNameBytes);

            // 属性值
            final Object fieldValue = field.get(val);
            // 所有类型都占2字节
            byteCount += Lengths.TYPE_NULL_FLAG_LENGTH;
            if (TypeNames.isByte(typeName)) {
                // Byte 类型
                final Byte value = (Byte) fieldValue;
                // 直接调用写入字节的函数，其中会写入type、null 两个flag，然后才是值
                writeByte(value);
                byteCount += PrimitiveTypeByteLengths.BYTE_LENGTH;
            } else if (TypeNames.isShort(typeName)) {
                // short类型
                final Short value = (Short) fieldValue;
                writeShort(value);
                byteCount += PrimitiveTypeByteLengths.SHORT_LENGTH;
            } else if (TypeNames.isInt(typeName)) {
                // int
                final Integer value = (Integer) fieldValue;
                writeInt(value);
                byteCount += PrimitiveTypeByteLengths.INT_LENGTH;
            } else if (TypeNames.isLong(typeName)) {
                final Long value = (Long) fieldValue;
                writeLong(value);
                byteCount += PrimitiveTypeByteLengths.LONG_LENGTH;
            } else if (TypeNames.isFloat(typeName)) {
                final Float value = (Float) fieldValue;
                writeFloat(value);
                byteCount += PrimitiveTypeByteLengths.FLOAT_LENGTH;
            } else if (TypeNames.isDouble(typeName)) {
                final Double value = (Double) fieldValue;
                writeDouble(value);
                byteCount += PrimitiveTypeByteLengths.DOUBLE_LENGTH;
            } else if (TypeNames.isBoolean(typeName)) {
                final Boolean value = (Boolean) fieldValue;
                writeBoolean(value);
                byteCount += PrimitiveTypeByteLengths.BOOLEAN_LENGTH;
            } else if (TypeNames.isChar(typeName)) {
                final Character value = (Character) fieldValue;
                writeChar(value);
                byteCount += PrimitiveTypeByteLengths.CHAR_LENGTH;
            } else if (TypeNames.isString(typeName)) {
                final String value = (String) fieldValue;
                byteCount += writeString(value);
            } else if (TypeNames.isList(typeName)) {
                // 是否是列表
                writeList((List<?>) fieldValue);
            } else if (TypeNames.isArrayByClassName(typeName)) {
                // 是否是 数组，返回值size太麻烦了, 不过长度byteCount似乎并不会影响到功能
                writeArray(fieldValue);
            } else if (TypeNames.isMap(typeName)) {
                // MAP
                writeMap((Map<?, ?>) fieldValue);
            }else if (TypeNames.isSet(typeName)) {
                // SET
                writeSet((Set<?>) fieldValue);
            } else {
                // 其他的视为 普通对象，需要递归处理
                final int size = writeObject(fieldValue);
                byteCount += size;
            }
        }

        // 把对象大小回填到预留的位置
        bout.writeInt(byteCount, indexForObjectSize);

        return byteCount;
    }


    /**
     * write数组
     */
    public void writeArray(Object val) throws Exception {
        // 类型标志位
        writeTypeFlag(TypeFlags.ARRAY);

        // null标志位
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);

        if (!isNull) {
            // 获取数组对象
            Class<?> clazz = val.getClass();
            final String className = clazz.getSimpleName();
            final int arrayDimension = TypeNames.getArrayDimension(className);

            // 写入维度数、
            bout.writeInt(arrayDimension);

            // 纬度值Map，维度从0开始
            final Map<Integer, Integer> dimensionMap = new HashMap<>();

            // 通过递归计算维度值
            getDimensions(val, 0, dimensionMap);

            // 写入每个维度的长度
            for (int i = 0; i < arrayDimension; i++) {
                final Integer dimensionVal = dimensionMap.get(i);
                if (dimensionVal == null) {
                    // 按道理不会为null的。
                    throw new RuntimeException("dimension err");
                }
                bout.writeInt(dimensionVal);
            }

            TypeWithDefaultEnum type = null;

            if (TypeNames.isPrimitiveArray(className)) {
                // 写入数组类型标识
                bout.writeByte(ArrayTypeFlag.PRIMITIVE);

                // Primitive 没法用。即使转化为包装类，序列化了，反序列化为包装类，也没法转换到primitive。
                // 那反序列化的结果就是包装类吧

                // 还需要写入类型 writeTypeFlag
                if (TypeNames.isPrimitiveByteArray(className)) {
                    type = TypeWithDefaultEnum.PRI_BYTE;
                    writeTypeFlag(TypeFlags.BYTE);
                } else if (TypeNames.isPrimitiveShortArray(className)) {
                    type = TypeWithDefaultEnum.PRI_SHORT;
                    writeTypeFlag(TypeFlags.SHORT);
                } else if (TypeNames.isPrimitiveIntArray(className)) {
                    type = TypeWithDefaultEnum.PRI_INT;
                    writeTypeFlag(TypeFlags.INT);
                } else if (TypeNames.isPrimitiveLongArray(className)) {
                    type = TypeWithDefaultEnum.PRI_LONG;
                    writeTypeFlag(TypeFlags.LONG);
                } else if (TypeNames.isPrimitiveCharArray(className)) {
                    type = TypeWithDefaultEnum.PRI_CHAR;
                    writeTypeFlag(TypeFlags.CHAR);
                } else if (TypeNames.isPrimitiveFloatArray(className)) {
                    type = TypeWithDefaultEnum.PRI_FLOAT;
                    writeTypeFlag(TypeFlags.FLOAT);
                } else if (TypeNames.isPrimitiveDoubleArray(className)) {
                    type = TypeWithDefaultEnum.PRI_DOUBLE;
                    writeTypeFlag(TypeFlags.DOUBLE);
                } else if (TypeNames.isPrimitiveBoolArray(className)) {
                    type = TypeWithDefaultEnum.PRI_BOOL;
                    writeTypeFlag(TypeFlags.BOOLEAN);
                }else{
                    throw new TypeNotFoundException();
                }
            } else if (TypeNames.isWrappedArray(className)) {
                bout.writeByte(ArrayTypeFlag.WRAPPED);

                if (TypeNames.isWrappedByteArray(className)) {
                    type = TypeWithDefaultEnum.WRA_BYTE;
                    writeTypeFlag(TypeFlags.BYTE);
                } else if (TypeNames.isWrappedShortArray(className)) {
                    type = TypeWithDefaultEnum.WRA_SHORT;
                    writeTypeFlag(TypeFlags.SHORT);
                } else if (TypeNames.isWrappedIntArray(className)) {
                    type = TypeWithDefaultEnum.WRA_INT;
                    writeTypeFlag(TypeFlags.INT);
                } else if (TypeNames.isWrappedLongArray(className)) {
                    type = TypeWithDefaultEnum.WRA_LONG;
                    writeTypeFlag(TypeFlags.LONG);
                } else if (TypeNames.isWrappedCharArray(className)) {
                    type = TypeWithDefaultEnum.WRA_CHAR;
                    writeTypeFlag(TypeFlags.CHAR);
                } else if (TypeNames.isWrappedFloatArray(className)) {
                    type = TypeWithDefaultEnum.WRA_FLOAT;
                    writeTypeFlag(TypeFlags.FLOAT);
                } else if (TypeNames.isWrappedDoubleArray(className)) {
                    type = TypeWithDefaultEnum.WRA_DOUBLE;
                    writeTypeFlag(TypeFlags.DOUBLE);
                } else if (TypeNames.isWrappedBoolArray(className)) {
                    type = TypeWithDefaultEnum.WRA_BOOL;
                    writeTypeFlag(TypeFlags.BOOLEAN);
                }else{
                    throw new TypeNotFoundException();
                }
            } else if (TypeNames.isStringArray(className)) {
                bout.writeByte(ArrayTypeFlag.STRING);
                // 标识冗余，解析时注意
                type = TypeWithDefaultEnum.STRING;
                writeTypeFlag(TypeFlags.STRING);
            } else {
                // 当作其他对象处理
                bout.writeByte(ArrayTypeFlag.REGULAR_OBJECT);
                // 这里本来不需要的
                writeTypeFlag(TypeFlags.REGULAR_OBJECT);
                type = TypeWithDefaultEnum.REGULAR_OBJECT;

                // 获取数组Class
                final String arrClassName = val.getClass().getName();

                final int LIndex = arrClassName.indexOf('L');
                if (LIndex < 0) {
                    throw new TypeNotFoundException();
                }
                // 去掉数组类名前面的[*[L 和 末尾的;
                final String fullPathClassName = arrClassName.substring(LIndex + 1, arrClassName.length() - 1);

                // 写入类全限定名
                bout.writeString(fullPathClassName);
            }

            // 获取对象数组的类型，比如Object[]的数组类型是 java.lang.Object
            String arrClassName = clazz.getName();
            arrClassName = arrClassName.substring(0, arrClassName.indexOf("["));

            // 写入数组数据
            writeArrayElements(val, dimensionMap, type, arrClassName);
        }
    }

    /**
     * 先递归一次，得到维度。
     * <p>主要是解决手动创建并赋值数组的情况，每一个维下的子数组长度不一样, 比如下面的例子，
     * 第二维长度不一，但是总的维度应该是2X6的，少于6的都应该填充默认值. 虽然填入默认值会引入脏数据，但是不统一长度的话反序列化是没法构造数组</p>
     * <blockquote>
     * <pre>
     *       final Byte [][] strings =
     *       new Byte[][]{
     *               {1,1,1},
     *               {2,2,2,2,2,2},
     *               {2},
     *               {}
     *       };
     *     </pre>
     * </blockquote>
     */
    private void getDimensions(Object arr, int dimensionNo, Map<Integer, Integer> dimensionMap) {
        if (arr == null) {
            return;
        }
        final Class<?> clazz = arr.getClass();
        if (clazz.isArray()) {
            final int length = Array.getLength(arr);

            // 取该维度下最长的长度
            final Integer dimensionVal = dimensionMap.get(dimensionNo);
            dimensionMap.put(dimensionNo, dimensionVal == null ? length : Math.max(dimensionVal, length));

            for (int i = 0; i < length; i++) {
                final Object ele = Array.get(arr, i);
                getDimensions(ele, dimensionNo + 1, dimensionMap);
            }
        }
    }


    private void writeArrayElements(Object arr, Map<Integer, Integer> dimensionMap, TypeWithDefaultEnum type, String arrClassName) throws Exception {
        dfsWrite(arr, 0, dimensionMap, type,arrClassName);
    }

    /**
     * DFS遍历 未知维度 数组
     *
     * @param type 默认值，对于没有值的部分进行填充
     */
    private void dfsWrite(Object arr, int dimensionNo, Map<Integer, Integer> dimensionMap, TypeWithDefaultEnum type, String arrClassName) throws Exception {
        // 1.值本身是null：包装类型
        // 2.数组是null的
        if (arr == type.getDefaultValue() || arr == null) {
            // 写入默认值
            writeArrayElement(arrClassName,type, null, true);
            return;
        }
        final Class<?> clazz = arr.getClass();
        // 是否是数组，是则进入递归
        if (clazz.isArray()) {
            final int length = Array.getLength(arr);

            for (int i = 0; i < length; i++) {
                final Object ele = Array.get(arr, i);
                dfsWrite(ele, dimensionNo + 1, dimensionMap, type, arrClassName);
            }
            // 对于长度小于 该维度最长的，需要使用默认值补齐
            final Integer dimNo = dimensionMap.get(dimensionNo);
            for (int i = length; i < (dimNo == null ? 0 : dimNo); i++) {
                dfsWrite(type.getDefaultValue(), dimensionNo + 1, dimensionMap, type,arrClassName);
            }
        } else {
            // 写入值
            writeArrayElement(arrClassName,type, arr, false);
        }
    }

    /**
     * 写入单个元素
     * <p>直接利用封装好的write函数，而不是使用bout的write。
     * 两个好处：1.复用，2.支持null。 一个坏处：数据有些冗余</p>
     */
    private void writeArrayElement(String arrClassName, TypeWithDefaultEnum type, Object value, boolean useDefault) throws Exception {
        final Object val = useDefault ? type.getDefaultValue() : value;
        // TODO 这里有非常大的问题，这个type是前面通过数组定义的类型判断的，而这里写值是应该使用具体的类型
        // TODO 比如数组定义时是Object， 但是实际写入的可能是String、Integer. 必须要根据值的类型来判断



        // 获取值Class
        final Class<?> clazz = val.getClass();
        final String eleClassName = clazz.getName();
        // 如果元素的类型和数组定义的类型相同
        if (eleClassName.equals(arrClassName)){
            // 使用原来的代码
            switch (type) {
                case PRI_BYTE:
                case WRA_BYTE:
                    // 直接强转有问题，Integer就不能转Byte
                    writeByte(val == null ? null : Byte.parseByte(String.valueOf(val)));
                    break;
                case PRI_SHORT:
                case WRA_SHORT:
                    writeShort(val == null ? null : Short.parseShort(String.valueOf(val)));
                    break;
                case PRI_INT:
                case WRA_INT:
                    writeInt(val == null ? null : Integer.parseInt(String.valueOf(val)));
                    break;
                case PRI_LONG:
                case WRA_LONG:
                    writeLong(val == null ? null : Long.parseLong(String.valueOf(val)));
                    break;
                case PRI_FLOAT:
                case WRA_FLOAT:
                    writeFloat(val == null ? null : Float.parseFloat(String.valueOf(val)));
                    break;
                case PRI_DOUBLE:
                case WRA_DOUBLE:
                    writeDouble(val == null ? null : Double.parseDouble(String.valueOf(val)));
                    break;
                case PRI_BOOL:
                case WRA_BOOL:
                    writeBoolean(val == null ? null : Boolean.parseBoolean(String.valueOf(val)));
                    break;
                case PRI_CHAR:
                case WRA_CHAR:
                    writeChar((Character) val);
                    break;
                case STRING:
                    writeString((String) val);
                    break;
                case REGULAR_OBJECT:
                    writeObject(val);
                    break;
                // TODO 数组中包含Map\Set比较难以实现，暂不支持！
                default:
                    throw new TypeNotFoundException();
            }
        }else{
            //否则还需要判断值的类型，即值是子类，需要知道子类的具体类型。
            writeAnyTypeObject(val);
        }
    }


    @Override
    public void writeList(List<?> list) throws Exception {
        writeTypeFlag(TypeFlags.LIST);

        boolean isNull = ObjectUtil.isNull(list);
        writeNullFlag(isNull);

        if (!isNull) {
            String className = list.getClass().getName();

            if(ClassUtil.isAnonymousInnerClass(className)){
                className = CollectionDefaultTypes.LIST_DEFAULT_TYPE;
            }

            // 写入List的全限定名
            bout.writeString(className);

            // 长度
            int size = list.size();
            bout.writeInt(size);
            // 如果长度大于0，则后面跟类型和数据。否则结束
            if (size > 0) {
                // 获取List的元素类型，只使用第一个元素类型代表全局类型是不对的，比如Object就可以存放任何类型，比如第一个是Double，后面的是Integer就会出问题
                // 所以每一个元素都应该带有类型属性.
                // 元素类型甚至可以是其他复杂类型，比如数组: ArrayList<byte[][]> bytes = new ArrayList<>();
                // 那么问题又来了，基本类型又不可用了
                for (Object ele : list) {
                    // 挨个写入对象
                    writeAnyTypeObject(ele);
                }
            }
        }
    }


    /**
     * ！写任何类型的对象，传入的对象可以是任何类型
     */
    private void writeAnyTypeObject(Object ele) throws Exception {
        // 需要做null判断
        if (ele == null) {
            // 如果是null，我也不知道它会是什么类型, 那就当作object写入，读取的时候需要判断，如果是null，写入null而不用类型转换
            writeObject(null);
        } else {
            final Class<?> eleClass = ele.getClass();
            final String eleClassName = eleClass.getName();
            if (TypeNames.isByte(eleClassName)) {
                writeByte((Byte) ele);
            } else if (TypeNames.isShort(eleClassName)) {
                writeShort((Short) ele);
            } else if (TypeNames.isInt(eleClassName)) {
                writeInt((Integer) ele);
            } else if (TypeNames.isLong(eleClassName)) {
                writeLong((Long) ele);
            } else if (TypeNames.isFloat(eleClassName)) {
                writeFloat((Float) ele);
            } else if (TypeNames.isDouble(eleClassName)) {
                writeDouble((Double) ele);
            } else if (TypeNames.isChar(eleClassName)) {
                writeChar((Character) ele);
            } else if (TypeNames.isString(eleClassName)) {
                writeString((String) ele);
            } else if (TypeNames.isBoolean(eleClassName)) {
                writeBoolean((Boolean) ele);
            } else if (TypeNames.isArrayByClassName(eleClassName)) {
                // 数组
                writeArray(ele);
            } else if (TypeNames.isList(eleClass)) {
                // List
                writeList((List<?>) ele);
            } else if (TypeNames.isMap(eleClass)) {
                // MAP
                writeMap((Map<?, ?>) ele);
            } else {
                // 否则当作Object处理
                writeObject(ele);
            }
        }
    }

    /**
     * map 和 list也是类似，对每一个key, value都需要做类型判断，以免反序列化错误。
     */
    @Override
    public void writeMap(Map<?, ?> map) throws Exception {
        writeTypeFlag(TypeFlags.MAP);

        boolean isNull = ObjectUtil.isNull(map);
        writeNullFlag(isNull);

        if (!isNull) {
            String className = map.getClass().getName();

            if(ClassUtil.isAnonymousInnerClass(className)){
                className = CollectionDefaultTypes.MAP_DEFAULT_TYPE;
            }

            // 写入map的全限定名
            bout.writeString(className);

            final Set<? extends Map.Entry<?, ?>> entries = map.entrySet();
            final int size = entries.size();

            // 写入长度
            bout.writeInt(size);

            // 遍历
            for (Map.Entry<?, ?> entry : entries) {
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                // 写入key 和 value
                writeAnyTypeObject(key);
                writeAnyTypeObject(value);
            }
        }
    }


    @Override
    public void writeSet(Set<?> set) throws Exception {
        writeTypeFlag(TypeFlags.SET);

        boolean isNull = ObjectUtil.isNull(set);
        writeNullFlag(isNull);

        if (!isNull) {
            String className = set.getClass().getName();

            // 处理匿名内部类
            if(ClassUtil.isAnonymousInnerClass(className)){
                className = CollectionDefaultTypes.SET_DEFAULT_TYPE;
            }

            // 写入set的全限定名
            bout.writeString(className);

            final int size = set.size();

            // 写入长度
            bout.writeInt(size);

            // 写入set值
            for (Object o : set) {
                writeAnyTypeObject(o);
            }
        }
    }

    /**
     * 重置。 TODO 要不要缩小数组？？？
     * */
    public void reset(){
        this.bout.reset();
    }

    /**
     * 根据传入的参数类型动态判断类型，调用不同的写入
     * <p>这个方法才是建议使用的方法，即通用方法</p>
     * */
    @Override
    public void write(Object obj) throws Exception {
        writeAnyTypeObject(obj);
    }

    /**
     * 获取序列化的二进制副本
     * */
    public byte[] getBytes() {
        return bout.toByteArray();
    }

    /**
     * 获取反序列化Deseri
     */
    public Deseri toDeseri() {
        return new Deseri(getBytes());
    }
}
