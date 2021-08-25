package cn.edw.seri.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author taoxu.xu
 * @date 8/22/2021 9:01 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cat{
    private int id;
    private String name ;
    char ch;
    private byte byt;
    private short sho;
    private Long lon;
    private Boolean isMan;
    private float flo;
    private Double dou;

    private CatFood catFood;
    /**
     * 若是基本类型byte则不会生效
     * */
    private Byte [] bytes;

    private Short[] shorts;

    private Integer[] ints;

    private Long[] longs;

    private Character[] characters;

    private Float[] floats;

    private Double[] doubles;

    private String[] strings;

    private Friend[] friends;

    private MyInterface myInterface;

    private List<Friend> friendList;

    private Set<Integer> set;

    Map<Integer, Integer> map ;
}
