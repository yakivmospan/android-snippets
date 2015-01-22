```java
import java.util.Comparator;

public class StringComparator implements Comparator<String>{

    @Override
    public int compare(String lhs, String rhs) {
        if (lhs == null && rhs == null) {
            return 0;
        }else if (lhs == null) {
            return rhs.compareTo(lhs);
        } else {
            return lhs.compareTo(rhs);
        }
    }
}
```
