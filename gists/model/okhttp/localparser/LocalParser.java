import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakiv M. on 10.02.2015.
 */
public class LocalParser {

    private Context mContext;

    public LocalParser(Context context) {
        mContext = context;
    }

    public
    @Nullable
    People parsePeople() {
        JsonParser<People> parser = new JsonParser<>(new PeopleJHandler());
        return parser.parse(Utils.openStream(mContext, "people"));
    }
}