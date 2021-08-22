package cn.edw.seri.io;

import cn.edw.seri.protocol.Booleans;
import cn.edw.seri.protocol.Constants;
import cn.edw.seri.protocol.PrimitiveTypeByteLengths;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 自定义输出流。 可以考虑继承ByteArrayOutputStream，但是发现如果要添加自己的功能的化需要对很多private再次写一遍。
 * <p>索性模仿ByteArrayOutputStream，重写自己的字节输出流</p>
 * <p>主要提供一些基本类型的write</p>
 * <p>高位存储在低位，即大端</p>
 * @author taoxu.xu
 * @date 8/22/2021 2:32 PM
 */
public class SeriByteArrayOutputStream {
    /**
     * 默认数组初始化大小，设置大一些。
     * */
    private static final int DEFAULT_ARRAY_SIZE = 128;
    /**
     * 最大数组大小
     * */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * store data
     * */
    protected byte buf[];

    /**
     * valid data count
     * */
    protected int count;


    public SeriByteArrayOutputStream() {
        this(DEFAULT_ARRAY_SIZE);
    }

    public SeriByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        if (size > MAX_ARRAY_SIZE) {
            throw new IllegalArgumentException("Initial size can't be larger than " + MAX_ARRAY_SIZE);
        }
        buf = new byte[size];
    }

    /**
     * 确保满足容量需求
     * */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity < 0){
            throw new IllegalArgumentException("Negative minCapacity: " + minCapacity);
        }
        // overflow-conscious code
        // 需要的最小容量大于当前的数组长度,扩充，注意不是大于count，
        // 和当前的数据数量没关系，关注的是插入新数据时能否装得下
        if (minCapacity > buf.length ) {
            grow(minCapacity);
        }
    }
    /**
     * 扩容
     * */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        // 2倍扩容, 注意这里newCapacity可能是负数，因为乘以2后超出MAX_VALUE了
        int newCapacity = oldCapacity << 1;
        // 于是如果变负数后新容量满足最小需求就行
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        // 如果2倍扩容超出了MAX，但是还是在int的正数范围内，设置为int的最大值
        if (newCapacity > MAX_ARRAY_SIZE ) {
            newCapacity = Integer.MAX_VALUE;
        }
        buf = Arrays.copyOf(buf, newCapacity);
    }

    /**
     * 写bytes数组
     * */
    public synchronized void write(byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }
    public synchronized void write(byte b[]) {
        write(b, 0, b.length);
    }

        /**
         * 写byte
         * */
    public synchronized void writeByte(byte val) {
        int len = PrimitiveTypeByteLengths.BYTE_LENGTH;
        ensureCapacity(count + len);
        buf[count++] = val;
    }

    /**
     * 写short
     * */
    public synchronized void writeShort(short val) {
        int len = PrimitiveTypeByteLengths.SHORT_LENGTH;
        ensureCapacity(count + len);
        buf[count++] = (byte) ((val >> 8) & 0xFF);
        buf[count++] = (byte) (val  & 0xFF);
    }

    /**
     * 写int
     * */
    public synchronized void writeInt(int val) {
        int len = PrimitiveTypeByteLengths.INT_LENGTH;
        ensureCapacity(count + len);
        buf[count++] = (byte) ((val >> 24) & 0xFF);
        buf[count++] = (byte) ((val >> 16) & 0xFF);
        buf[count++] = (byte) ((val >> 8) & 0xFF);
        buf[count++] = (byte) (val  & 0xFF);
    }

    /**
     * 往指定下标处写入int值
     * @param index 指定下标处
     * @param val 写入的int值
     * */
    public synchronized void writeInt(int val, int index) {
        if (index <0 || index>count){
            throw new IllegalArgumentException("Insert position invalid:"+index);
        }
        // 包装index往后4字节可以装得下
        ensureCapacity (index + PrimitiveTypeByteLengths.INT_LENGTH );
        buf[index] = (byte) ((val >> 24) & 0xFF);
        buf[index+1] = (byte) ((val >> 16) & 0xFF);
        buf[index+2] = (byte) ((val >> 8) & 0xFF);
        buf[index+3] = (byte) (val  & 0xFF);
    }

        /**
         * 写 long
         * */
    public synchronized void writeLong(long val) {
        int len = PrimitiveTypeByteLengths.LONG_LENGTH;
        ensureCapacity(count + len);
        buf[count++] = (byte) ((val >> 56) & 0xFF);
        buf[count++] = (byte) ((val >> 48) & 0xFF);
        buf[count++] = (byte) ((val >> 40) & 0xFF);
        buf[count++] = (byte) ((val >> 32) & 0xFF);
        buf[count++] = (byte) ((val >> 24) & 0xFF);
        buf[count++] = (byte) ((val >> 16) & 0xFF);
        buf[count++] = (byte) ((val >> 8) & 0xFF);
        buf[count++] = (byte) (val  & 0xFF);
    }

    /**
     * write float
     * */
    public synchronized void writeFloat(float val) {
        int len = PrimitiveTypeByteLengths.FLOAT_LENGTH;
        ensureCapacity(count + len);
        // TODO 原理：将float转化为整数，存整数。取出时先得到整数，再转化为float
        final int floatToIntBits = Float.floatToIntBits(val);
        writeInt(floatToIntBits);
    }

    /**
     * write double
     * */
    public synchronized void writeDouble(double val) {
        int len = PrimitiveTypeByteLengths.DOUBLE_LENGTH;
        ensureCapacity(count + len);
        final long doubleToLongBits = Double.doubleToLongBits(val);
        writeLong(doubleToLongBits);
    }

    /**
     * write char
     * */
    public synchronized void writeChar(char val) {
        int len = PrimitiveTypeByteLengths.CHAR_LENGTH;
        ensureCapacity(count + len);
        buf[count++] = (byte) ((val >> 8) & 0xFF);
        buf[count++] = (byte) (val  & 0xFF);
    }

    /**
     * write boolean
     * */
    public synchronized void writeBool(boolean val) {
        int len = PrimitiveTypeByteLengths.BOOLEAN_LENGTH;
        ensureCapacity(count + len);
        buf[count++] = (byte) (val  ? Booleans.TRUE : Booleans.FALSE);
    }

    /**
     * write string
     * */
    public synchronized int writeString(String val){
        final byte[] bytes = val.getBytes(Constants.DEFAULT_CHARSET);
        final int len = bytes.length;
        ensureCapacity(count + len);

        // 写入字符串长度
        writeInt(len);

        for (int i = 0; i < len; i++) {
            buf[count++] = bytes[i];
        }
        return len;
    }


    /**
     * 跳过N字节
     * */
    public int skipBytes(int bytes){
        ensureCapacity(count + bytes);
        int res = count;
        count+=bytes;
        return res;
    }

    /**
     * 跳过N short
     * */
    public int skipShorts(int shorts){
        int bytes = shorts * PrimitiveTypeByteLengths.SHORT_LENGTH;
        ensureCapacity(count + bytes);
        int res = count;
        count+=bytes;
        return res;
    }
    /**
     * 跳过N int
     * */
    public int skipInts(int ints){
        int bytes = ints * PrimitiveTypeByteLengths.INT_LENGTH;
        ensureCapacity(count + bytes);
        int res = count;
        count+=bytes;
        return res;
    }

    /**
     * 跳过N long
     * */
    public int skipLongs(int longs){
        int bytes = longs * PrimitiveTypeByteLengths.INT_LENGTH;
        ensureCapacity(count + bytes);
        int res = count;
        count+=bytes;
        return res;
    }

    /**
     * 返回data
     * */
    public synchronized byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }
    /**
     * 重置、清楚所有数据
     * */
    public synchronized void reset() {
        count = 0;
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }

    /**
     * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     */
    public void close() throws IOException {
    }
}
