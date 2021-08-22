package cn.edw.seri.protocol;

/**
 * 基本类型名词，用于反射获取Field时判断类型
 * - PRI： primitive基本类型
 * - WRAPPER：Wrap，包装类型
 *
 * @author taoxu.xu
 * @date 8/11/2021 12:02 PM
 */
public class TypeNames {
    private static final String INT_PRI = "int";
    private static final String INT_WRAPPER = "java.lang.Integer";
    private static final String BYTE_PRI = "byte";
    private static final String BYTE_WRAPPER = "java.lang.Byte";
    private static final String SHORT_PRI = "short";
    private static final String SHORT_WRAPPER = "java.lang.Short";
    private static final String LONG_PRI = "long";
    private static final String LONG_WRAPPER = "java.lang.Long";
    private static final String FLOAT_PRI = "float";
    private static final String FLOAT_WRAPPER = "java.lang.Float";
    private static final String DOUBLE_PRI = "double";
    private static final String DOUBLE_WRAPPER = "java.lang.Double";
    private static final String CHAR_PRI = "char";
    private static final String CHAR_WRAPPER = "java.lang.Character";
    private static final String BOOLEAN_PRI="boolean";
    private static final String BOOLEAN_WRAPPER = "java.lang.Boolean";
    private static final String STRING="java.lang.String";
    private static final String LIST="java.util.List";
    private static final String ARRAY_LIST="java.util.ArrayList";
    private static final String LINKED_LIST="java.util.LinkedList";


    public static boolean isInt(String type){
        return INT_PRI.equals(type) || INT_WRAPPER.equals(type);
    }
    public static boolean isByte(String type){
        return BYTE_PRI.equals(type) || BYTE_WRAPPER.equals(type);
    }
    public static boolean isShort(String type){
        return SHORT_PRI.equals(type) || SHORT_WRAPPER.equals(type);
    }
    public static boolean isLong(String type){
        return LONG_PRI.equals(type) || LONG_WRAPPER.equals(type);
    }
    public static boolean isFloat(String type){
        return FLOAT_PRI.equals(type) || FLOAT_WRAPPER.equals(type);
    }
    public static boolean isDouble(String type){
        return DOUBLE_PRI.equals(type) || DOUBLE_WRAPPER.equals(type);
    }
    public static boolean isBoolean(String type){
        return BOOLEAN_PRI.equals(type) || BOOLEAN_WRAPPER.equals(type);
    }
    public static boolean isChar(String type){
        return CHAR_PRI.equals(type) || CHAR_WRAPPER.equals(type);
    }

    public static boolean isString(String type) {
        return STRING.equals(type);
    }
    public static boolean isList(String type){
/*     TODO 如何扩展判断？
        try {
            final Class<?> clazz  = Class.forName(type);
            return clazz.
        }catch (ClassNotFoundException e) {
            return false;
        }*/

        return LIST.equals(type)
                || LINKED_LIST.equals(type)
                || ARRAY_LIST.equals(type);
    }
}
