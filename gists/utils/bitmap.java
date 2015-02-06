import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yakiv M. on 22.10.2014.
 */
public class BitmapUtils extends BitmapFactory {

    public static final String TAG = BitmapUtils.class.getSimpleName();

    @Nullable
    public static Bitmap decodeUri(@Nullable Context context, @Nullable Uri fileUri) {
        if (context == null || fileUri == null) {
            return null;
        }


        String path = UriUtils.getPathFromUri(context, fileUri);
        return decodeFile(path);
    }

    @Nullable
    public static Bitmap decodeUri(@Nullable Context context, @Nullable Uri fileUri, int minWidth,
            int minHeight) {
        if (context == null || fileUri == null) {
            return null;
        }

        String path = UriUtils.getPathFromUri(context, fileUri);

        Options options = new Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        if (minWidth > 0 || minHeight > 0) {
            // First decode with inJustDecodeBounds=true to check dimensions

            options.inJustDecodeBounds = true;
            decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize2(options, minWidth, minHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
        }

        return decodeFile(path, options);

    }

    @NonNull
    public static Options decodeBounds(@Nullable Context context, @Nullable Uri fileUri) {
        if (context == null || fileUri == null) {
            return null;
        }

        String path = UriUtils.getPathFromUri(context, fileUri);

        Options options = new Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        options.inJustDecodeBounds = true;
        decodeFile(path);

        return options;
    }

    @Nullable
    public static Bitmap decodeFile(@NonNull String path, int minWidth, int minHeight) {
        Options options = null;

        if (minWidth > 0 || minHeight > 0) {
            // First decode with inJustDecodeBounds=true to check dimensions
            options = new Options();
            options.inJustDecodeBounds = true;
            decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, minWidth, minHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
        }

        return decodeFile(path, options);
    }

    @Nullable
    public static Bitmap decodeResources(@NonNull Resources res, int resId, int minWidth,
            int minHeight) {
        BitmapFactory.Options options = null;

        if (minWidth > 0 || minHeight > 0) {
            // First decode with inJustDecodeBounds=true to check dimensions
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, minWidth, minHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
        }

        return decodeResource(res, resId, options);
    }


    public static void save(@Nullable Bitmap bitmap, @Nullable Uri fileUri) {
        if (fileUri == null || bitmap == null) {
            return;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileUri.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            closeStream(out);
        }
    }

    @NonNull
    public static Bitmap rotateBitmap(@NonNull Context context, @NonNull Uri fileUri,
            @NonNull Bitmap bitmap) {
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(UriUtils.getPathFromUri(context, fileUri));
            int rotation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            if (rotation != 0f) {
                matrix.preRotate(rotationInDegrees);
            }

        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true
        );
    }

    public static int exifToDegrees(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
        }
        return 0;
    }

    private static void closeStream(@Nullable Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options o, int minWidth, int minHeight) {
        // Raw height and width of image
        final int height = o.outHeight;
        final int width = o.outWidth;
        int inSampleSize = 1;

        if (height > minHeight || width > minWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > minHeight
                    && (halfWidth / inSampleSize) > minWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static int calculateInSampleSize2(BitmapFactory.Options o, int minWidth,
            int minHeight) {
        final int height = o.outHeight;
        final int width = o.outWidth;

        int inSampleSize = 1;

        if (height > minHeight || width > minWidth) {
            int minLargerSide = Math.max(minWidth, minHeight);
            int largerSide = Math.max(height, width);

            final int halfLargerSide = largerSide / 2;

            while ((halfLargerSide / inSampleSize) > minLargerSide) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
