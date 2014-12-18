### Utils: ResourceFetcher

Some times we need find resource ID by it String name. This class will help to do this

**Usage**

```java
ResourceFetcher fetcher = new ResourceFetcher(context);

ImageView view = ...
view.setImageResource(fetcher.getDrawableId("name"));
```

**Sources**

```java
public class ResourceFetcher {

    private Context mContext;

    public ResourceFetcher(Context context) {
        mContext = context.getApplicationContext();
    }

    public int getDrawableId(String id) {
        return mContext.getResources().getIdentifier(id, "drawable", mContext.getPackageName());
    }
}
```
