package cn.edw.seri.protocol;

/**
 * 基本类型和String类型 对应的标志
 * @author taoxu.xu
 * @date 8/10/2021 7:54 PM
 */
public class TypeFlags {
    public static final byte BYTE = 0;
    public static final byte SHORT = 1;
    public static final byte INT = 2;
    public static final byte LONG = 3;
    public static final byte FLOAT = 4;
    public static final byte DOUBLE = 5;
    public static final byte BOOLEAN = 6;
    public static final byte CHAR = 7;
    public static final byte STRING = 8;
    public static final byte REGULAR_OBJECT = 9;
    public static final byte LIST = 10;

    public static final byte NULL = 11;

}
