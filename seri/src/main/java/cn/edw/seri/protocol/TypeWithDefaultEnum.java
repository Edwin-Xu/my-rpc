package cn.edw.seri.protocol;

/** 类型及默认值
 * TODO 是否要区分包装类，包装类默认值为null，而primitive默认值是0等。
 * TODO 引入包装类null导致协议更加复杂了，先不使用
 * @author taoxu.xu
 * @date 8/23/2021 2:09 PM
 */
public enum TypeWithDefaultEnum {
    /**
     * primitive types
     * */
    PRI_BYTE("byte",0),
    WRA_BYTE("byte",null),
    PRI_SHORT("short",0),
    WRA_SHORT("short",null),
    PRI_INT("int",0),
    WRA_INT("int",null),
    PRI_LONG("long",0L),
    WRA_LONG("long",null),
    PRI_CHAR("char",0),
    WRA_CHAR("char",null),
    PRI_BOOL("bool",false),
    WRA_BOOL("bool",null),
    PRI_FLOAT("float",0f),
    WRA_FLOAT("float",null),
    PRI_DOUBLE("double",0),
    WRA_DOUBLE("double",null),

    STRING("String",null),
    REGULAR_OBJECT("object",null),
    ;

    private String name;
    private Object defaultValue;

    TypeWithDefaultEnum(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
