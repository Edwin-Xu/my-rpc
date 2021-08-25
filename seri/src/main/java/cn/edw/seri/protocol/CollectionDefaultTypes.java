package cn.edw.seri.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 集合的默认类型。对于遇到内部类、匿名类的请况，使用默认了类型
 * @author taoxu.xu
 * @date 8/24/2021 8:41 PM
 */
public class CollectionDefaultTypes {
    public static final String LIST_DEFAULT_TYPE = ArrayList.class.getName();
    public static final String SET_DEFAULT_TYPE = HashSet.class.getName();
    public static final String MAP_DEFAULT_TYPE = HashMap.class.getName();
}
