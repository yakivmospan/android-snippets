### Utils: Random

Utils can generate random number from closed [min, max] range, when min < max

```java
import junit.framework.Assert;
import java.util.Random;

/**
 * Created by Yakiv M. on 28.11.2014.
 */
public class RandomUtils extends Random {

    /**
     * @param min generated value. Can't be > then max
     * @param max generated value
     * @return values in closed range [min, max].
     */
    public int nextInt(int min, int max) {
        Assert.assertFalse("min can't be > then max; values:[" + min + ", " + max + "]", min > max);
        if (min == max) {
            return max;
        }

        return nextInt(max - min + 1) + min;
    }
}
```
