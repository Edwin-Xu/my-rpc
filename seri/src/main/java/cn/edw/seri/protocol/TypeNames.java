package cn.edw.seri.protocol;

import java.util.regex.Pattern;

/**
 * 类型
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
    private static final String MAP="java.util.Map";
    private static final String SET="java.util.Set";

    /**
     * 基本类型/包装类 的数组类型。 多少个[]就表示多少纬.  注意上面都是根据class.SimpleName实现的
     * */
    private static final String PRI_ARRAY_PATTERN = "(byte|short|int|long|float|double|char|bool)(\\[\\])+";
    private static final String WRA_ARRAY_PATTERN = "(Byte|Short|Integer|Long|Float|Double|Character|Boolean)(\\[\\])+";
    public static final Pattern PAT_PRI_ARRAY = Pattern.compile(PRI_ARRAY_PATTERN);
    public static final Pattern WRA_PRI_ARRAY = Pattern.compile(WRA_ARRAY_PATTERN);

    public static boolean isPrimitiveArray(String type){
        return PAT_PRI_ARRAY.matcher(type).matches();
    }
    public static boolean isWrappedArray(String type){
        return WRA_PRI_ARRAY.matcher(type).matches();
    }

    public static boolean isPrimitiveByteArray(String str){return isPrimitiveArray(str) && str.startsWith("by");}
    public static boolean isWrappedByteArray(String str){return isWrappedArray(str) && str.startsWith("By");}

    public static boolean isPrimitiveShortArray(String str){return isPrimitiveArray(str) && str.startsWith("s");}
    public static boolean isWrappedShortArray(String str){return isWrappedArray(str) && str.startsWith("S");}

    public static boolean isPrimitiveIntArray(String str){return isPrimitiveArray(str) && str.startsWith("i");}
    public static boolean isWrappedIntArray(String str){return isWrappedArray(str) && str.startsWith("I");}

    public static boolean isPrimitiveLongArray(String str){return isPrimitiveArray(str) && str.startsWith("l");}
    public static boolean isWrappedLongArray(String str){return isWrappedArray(str) && str.startsWith("L");}

    public static boolean isPrimitiveFloatArray(String str){return isPrimitiveArray(str) && str.startsWith("f");}
    public static boolean isWrappedFloatArray(String str){return isWrappedArray(str) && str.startsWith("F");}

    public static boolean isPrimitiveDoubleArray(String str){return isPrimitiveArray(str) && str.startsWith("d");}
    public static boolean isWrappedDoubleArray(String str){return isWrappedArray(str) && str.startsWith("D");}

    public static boolean isPrimitiveCharArray(String str){return isPrimitiveArray(str) && str.startsWith("c");}
    public static boolean isWrappedCharArray(String str){return isWrappedArray(str) && str.startsWith("C");}

    public static boolean isPrimitiveBoolArray(String str){return isPrimitiveArray(str) && str.startsWith("bo");}
    public static boolean isWrappedBoolArray(String str){return isWrappedArray(str) && str.startsWith("Bo");}

    private static final String STRING_ARRAY_PATTERN = "String(\\[\\])+";
    private static final Pattern PAT_PRI_STRING_ARRAY = Pattern.compile(STRING_ARRAY_PATTERN);
    public static boolean isStringArray(String str){return PAT_PRI_STRING_ARRAY.matcher(str).matches();}


    /**
     * 获取数组的维度
     * @param type 类型，比如 int[]
     * */
    public static int getArrayDimension(String type){
        final int index = type.indexOf('[');
        return (type.length() - index)/2;
    }


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

    /**
     * 通过类名简单判断是否是数组，这里的类名是class.getSimpleName;
     * TODO 这种判断是不严谨的
     * */
    public static boolean isArrayByClassName(String className){
        return className!=null
                && className.endsWith("[]");
    }
    public static boolean isList(Class< ? > listClassName){
        if (LIST.equals(listClassName.getName())){
            return true;
        }
        final Class<?>[] interfaces = listClassName.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            final String interfaceName = anInterface.getName();
            if (LIST.equals(interfaceName)){
                return true;
            }
        }
        return false;
    }
    public static boolean isList(String listClassName)  {
        final Class<?> clazz;
        try {
            clazz = Class.forName(listClassName);
            return isList(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isMap(Class< ? > className){
        if (MAP.equals(className.getName())){
            return true;
        }
        final Class<?>[] interfaces = className.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            final String interfaceName = anInterface.getName();
            if (MAP.equals(interfaceName)){
                return true;
            }
        }
        return false;
    }

    public static boolean isMap(String mapClassName)  {
        try {
            final Class<?> clazz = Class.forName(mapClassName);
            return isMap(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isSet(Class< ? > className){
        if (SET.equals(className.getName())){
            return true;
        }
        final Class<?>[] interfaces = className.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            final String interfaceName = anInterface.getName();
            if (SET.equals(interfaceName)){
                return true;
            }
        }
        return false;
    }
    public static boolean isSet(String className)  {
        try {
            final Class<?> clazz = Class.forName(className);
            return isSet(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}
