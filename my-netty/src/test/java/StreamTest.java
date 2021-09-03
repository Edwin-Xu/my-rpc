import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * @author taoxu.xu
 * @date 8/30/2021 5:36 PM
 */
public class StreamTest {
    public static void main(String[] args) {
        int size = 1000;
        final Random random = new Random();
        final List<Integer> nums = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            nums.add(random.nextInt());
        }

        long start = System.currentTimeMillis();
        System.out.println(findMax(nums));
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();;
        System.out.println(findMaxByStream(nums));
        System.out.println(System.currentTimeMillis() - start);
    }

    public static int findMax(List<Integer> nums){
        int max = Integer.MIN_VALUE;
        for (Integer num : nums) {
            max = Math.max(max, num);
        }
        return max;
    }

    public static int findMaxByStream(List<Integer> nums){
        return nums.stream().max(Comparator.comparingInt(o -> o)).get();
    }
}
