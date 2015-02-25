import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Yakiv M. on 12.02.2015.
 */
public class Tag {

    private String[] mValues;

    public Tag(@NonNull String ...values) {
        mValues = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tag tag = (Tag) o;

        List<String> values = Arrays.asList(mValues);
        for (int i = 0; i < tag.mValues.length; i++) {
            if( values.contains(tag.mValues[i])){
                return true;
            }
        }
        return false;
    }
}
