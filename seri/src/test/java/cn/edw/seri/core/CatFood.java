package cn.edw.seri.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author taoxu.xu
 * @date 8/22/2021 9:04 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatFood {
    private int foodId;
    private FoodType foodType;
}
