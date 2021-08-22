package cn.edw.seri.core;

import cn.edw.seri.io.SeriByteArrayOutputStream;
import cn.edw.seri.protocol.*;
import cn.edw.seri.util.ObjectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
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
    public int writeString(String val)  {
        writeTypeFlag(TypeFlags.STRING);
        boolean isNull = ObjectUtil.isNull(val);
        writeNullFlag(isNull);
        if (!isNull) {
            return bout.writeString(val);
        }
        return 0;
    }

    @Override
    public int writeObject(Object val) throws IllegalAccessException {
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
            final Type type = field.getGenericType();
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
                // TODO
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

    @Override
    public void writeList(List<?> list) {

    }


    public byte[] getBytes(){
        return bout.toByteArray();
    }

}
