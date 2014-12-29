Based on [UriUtils][1]

```java
    private static final int REQUEST_IMAGE_CAPTURE = 333;
    private static final int REQUEST_IMAGE_GET = 3331;
    private static final int REQUEST_IMAGE_CROP = 3332;

    //very important is to add extension to the saving file
    //this will prevent rotation bug on some samsung devices
    private static final String IMAGE_NAME = "capture.jpg";
    private static final String CROPPED_IMAGE_NAME = "cropped_capture.jpg";
	
	private Toast mToast;
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            File file = new File(getExternalCacheDir(), IMAGE_NAME);
            startCrop(Uri.fromFile(file));
        } else if (requestCode == REQUEST_IMAGE_GET) {
            Uri uri = data.getData();
            startCrop(Uri.fromFile(new File(UriUtils.getPathFromUri(getBaseContext(),uri))));
        } else if (requestCode == REQUEST_IMAGE_CROP) {
            File file = new File(getExternalCacheDir(), CROPPED_IMAGE_NAME);
			//ViewerActivity.start(this, Uri.fromFile(file));
        }
    }
	
	private void startGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        } else {
            showToast(R.string.Gallery_app_isnt_installed);
        }
    }

    private void startCamera() {
        File cacheDir = getExternalCacheDir();
        if (cacheDir != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.withAppendedPath(Uri.fromFile(cacheDir), IMAGE_NAME)
            );

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            } else {
                showToast(R.string.Camera_app_isnt_installed);
            }
        } else {
            showToast(R.string.SD_is_unmounted);
        }
    }

    private void startCrop(@NonNull Uri uri) {
        File cacheDir = getExternalCacheDir();
        if (cacheDir != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("noFaceDetectiona", true);
            intent.putExtra("return-data", false);
            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.withAppendedPath(Uri.fromFile(cacheDir), CROPPED_IMAGE_NAME)
            );
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CROP);
            } else {
                //ViewerActivity.start(this, uri);
            }
        } else {
            showToast(R.string.SD_is_unmounted);
        }
    }

    private void showToast(int textId) {
        if (mToast == null) {
            mToast = Toast.makeText(
                    getApplicationContext(),
                    textId,
                    Toast.LENGTH_LONG
            );
        }

        if (!mToast.getView().isShown()) {
            mToast.show();
        } else {
            mToast.setText(textId);
        }
    }
```

**strings.xml**
```xml
    <string name="SD_is_unmounted">SD is unmounted</string>
    <string name="Gallery_app_isnt_installed">Gallery app isn\'t installed</string>
    <string name="Camera_app_isnt_installed">Camera app isn\'t installed</string>
```
[1]: /gists/utils/uri.md
