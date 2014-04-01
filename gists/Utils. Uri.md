### Utils: Uri

While working with file system we often need to get path from [Uri][1] object. 

As in Android 4.4 KitKat was added new Document picker we need to use 19 or later SDK version.

In your manifest :

```xml
<uses-sdk
    android:targetSdkVersion="19" />
```

And here is `UriUtils` class :

```java
public class UriUtils {

    public static String getPathFromUri(Context context, Uri uri) {
        String result = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                DocumentsContract.isDocumentUri(context, uri)) {
            result = getPathFromDocumentURI(context, uri);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            result = getPathFromContentTypeUri(context, uri);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            result = uri.getPath();
        }

        return result;
    }

    private static String getPathFromContentTypeUri(Context context, Uri uri) {
        String result = null;
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            if (cursor.moveToFirst()) {
                result = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            // Eat it
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    private static String getPathFromDocumentURI(Context context, Uri uri) {
        String result = null;
        //Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column,
                    sel,
                    new String[]{id},
                    null);

            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                result = cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
}

```


  [1]: http://developer.android.com/reference/android/net/Uri.html
