# Security


[You can find source code here.](/gists/utils/security/Security.java)
 
One util class to manage key generation, key storing and encryption on different APIs of Android.

As you may know android provided API to use `keystore` that is stored in system only from API 18. They introduced [AndroidKeyStore](http://developer.android.com/training/articles/keystore.html) provider that is responsible to manage this.

But as always there are underwater stones. Up to API 23 you are only able to create asymmetric keys using  `AndroidKeyStore` provider. Also [algorithms](http://developer.android.com/training/articles/keystore.html#SupportedAlgorithms) that you can use are limited.

I've create API that wraps default [JCA](http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html) api and `AndroidKeyStore` API and makes it easy to create, manage and use your keys.

## Sample

```java
// Create and save key
Store store = new Store(getApplicationContext());
if (!store.hasKey("test")) {
   SecretKey key = store.generateSymmetricKey("test", null);
} 
...

// Get key
SecretKey key = store.getSymmetricKey("test", null);

// Encrypt/Dencrypt data
Security.Crypto crypto = new Security.Crypto(Security.TRANSFORMATION_SYMMETRIC);
String text = "Sample text";

String encryptedData = crypto.encrypt(text, key);
Log.i("Security", "Encrypted data: " + encryptedData);

String decryptedData = crypto.decrypt(encryptedData, key);
Log.i("Security", "Decrypted data: " + decryptedData);
```

## How it works?

Depending on what key you need and what Android can give to us, API will create `keystore` file in application inner cache or will use `AndroidKeyStore` to hold keys. Key generation will be also made with different API. The tables below shows what will be used in different cases.

In case you want to generate and save `KeyPair`

| API   | Application Keystore | AndroidKeyStore |
|:-----:|:--------------------:|:---------------:|
|`< 18` |  `+`                 |                 |
|`>= 18`|                      |        `+`      |


In case you want to generate and save `SecretKey`

| API   | Application Keystore | AndroidKeyStore |
|:-----:|:--------------------:|:---------------:|
|`< 18` |  `+`                 |                 |
|`>= 23`|                      |        `+`      |

After calling one of `generateKey` methods, key will be automatically stored in `keystore`.

To store asymmetric `PrivateKey` we need to provide `X509Certificate`. And of course there is no default API to do that.

On `18+` devices its pretty easy, google did it for us.

For  `pre 18`  there is one 3d party library that can create self signed `X509Certificate`. It is called [Bouncy Castle](http://www.bouncycastle.org/) and is available on maven as well. But after some research I found that [Google did copied this library](https://goo.gl/Zcaqpj) to their API but made it private. Why ? Don't ask me..

So I decided to make it like this :

- API will try to get  Google Bouncy Castle using reflection (I've checked it on few APIs and it seems to work well)
- If Google version is missing, API will try to get 3d party Bouncy Castle library.  It will use reflection as well. This gives two advantages:
 - You can add this API for 18+ devices with out any additional libraries
 - You can run this API on pre 18 devices with out any additional libraries as well. And in case if some device will miss google hidden API you will receive an error and then include  Bouncy Castle to project. This is pretty cool if you are getting error on 15 API but your min project API is 16, and there is no errors on it.

In general it creates simple interface to work with `Keystore` using API provided by Java and different versions of Android. 

## Extended Usage

Instead of using `generateAsymmetricKey(@NonNull String alias, char[] password)` method you can use ` generateAsymmetricKey(@NonNull KeyProps keyProps)` one, and  define key with specific options.
 
```java
// Create store with specific name and password
Store store = new Store(context, STORE_NAME, STORE_PASSWORD);

final Calendar start = Calendar.getInstance();
final Calendar end = Calendar.getInstance();
end.add(Calendar.YEAR, 1);

// Create a key store params, some of them are specific per platform
// Check KeyProps doc for more info
KeyProps keyProps = new KeyProps.Builder()
   .setAlias(alias)
   .setPassword(password)
   .setKeySize(512)
   .setKeyType("RSA")
   .setSerialNumber(BigInteger.ONE)
   .setSubject(new X500Principal("CN=" + alias + " CA Certificate"))
   .setStartDate(start.getTime())
   .setEndDate(start.getTime())
   .setBlockModes("ECB")
   .setEncryptionPaddings("PKCS1Padding")
   .setSignatureAlgorithm("SHA256WithRSAEncryption")
   .build();

// Generate KeyPair depending on KeyProps 
KeyPair keyPair = store.generateAsymmetricKey(keyProps);

// Encrypt/Dencrypt data with or with out Initialisation Vectors
// This additional level of safety is required on 23 API level for
// some algorithms 
Security.Crypto crypto = new Security.Crypto(Security.TRANSFORMATION_SYMMETRIC);

String text = "Sample text";
String encryptedData = crypto.encrypt(text, key, false);
String decryptedData = crypto.decrypt(encryptedData, key, false);
```
