### Spinner: Hint

There is no default API to set hint on Spinner. To add it we will need a small workaround.

```java
List<Object> objects = new ArrayList<Object>();
objects.add(firstItem);
objects.add(secondItem);
// add hint as last item
objects.add(hint);

HintAdapter adapter = new HintAdapter(
                            context,
                            objects,
                            android.R.layout.simple_spinner_item);

adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

Spinner spinnerFilmType = (Spinner) findViewById(R.id.spinner);
spinner.setAdapter(adapter);

// show hint
spinner.setSelection(adapter.getCount());
```

Adapter source: 

```java
public class HintAdapter
        extends ArrayAdapter<Objects> {

    public HintAdapter(Context theContext, List<Object> objects) {
        super(theContext, R.id.text1, R.id.text1, objects);
    }

    public HintAdapter(Context theContext, List<Object> objects, int theLayoutResId) {
        super(theContext, theLayoutResId, R.id.text1, objects);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
```
