import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.x500.X500Principal;

import static java.security.KeyStore.PasswordProtection;
import static java.security.KeyStore.PrivateKeyEntry;
import static java.security.KeyStore.SecretKeyEntry;
import static java.security.KeyStore.getDefaultType;
import static java.security.KeyStore.getInstance;

public final class Security {

    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_SHA256_WITH_RSA_ENCRYPTION = "SHA256WithRSAEncryption";
    public static final String BLOCK_MODE_ECB = "ECB";
    public static final String BLOCK_MODE_CBC = "CBC";
    public static final String PADDING_PKCS_1 = "PKCS1Padding";
    public static final String PADDING_PKCS_7 = "PKCS7Padding";

    public static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";
    public static final String AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding";

    public static final int RSA_ECB_PKCS1PADDING_1024_ENCRYPTION_BLOCK_SIZE = 117;
    public static final int RSA_ECB_PKCS1PADDING_1024_DECRYPTION_BLOCK_SIZE = 128;

    /**
     * For default created asymmetric keys
     */
    public static String TRANSFORMATION_ASYMMETRIC = RSA_ECB_PKCS1PADDING;

    /**
     * For default created symmetric keys
     */
    public static String TRANSFORMATION_SYMMETRIC = AES_CBC_PKCS7PADDING;

    /**
     * For default created asymmetric keys
     */
    public static int ENCRYPTION_BOLOCK_SIZE = RSA_ECB_PKCS1PADDING_1024_ENCRYPTION_BLOCK_SIZE;

    /**
     * For default created asymmetric keys
     */
    public static int DECRYPTION_BOLOCK_SIZE = RSA_ECB_PKCS1PADDING_1024_DECRYPTION_BLOCK_SIZE;

    private static String TAG = Security.class.getName();
    private static final int VERSION = Build.VERSION.SDK_INT;

    /**
     * API to create, save and get keys
     */
    public static class Store extends ErrorHandler {

        private static final String PROVIDER_BC = "BC";
        private static final String PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore";
        private static final String DEFAULT_KEYSTORE_NAME = "keystore";
        private static final char[] DEFAULT_KEYSTORE_PASSWORD = BuildConfig.APPLICATION_ID.toCharArray();

        private String mKeystoreName = DEFAULT_KEYSTORE_NAME;
        private char[] mKeystorePassword = DEFAULT_KEYSTORE_PASSWORD;
        private final File mKeystoreFile;

        private final Context mContext;

        /**
         * Creates a store with default name and password. Name is "keystore" and password is application id
         *
         * @param context used to get cache dir of application
         */
        public Store(@NonNull Context context) {
            mContext = context;
            mKeystoreFile = new File(mContext.getCacheDir(), mKeystoreName);
        }

        /**
         * Creates a store with provided name and password.
         *
         * @param context used to get cache dir of application
         */
        public Store(@NonNull Context context, @NonNull String name, char[] password) {
            mContext = context;
            mKeystoreName = name;
            mKeystorePassword = password;
            mKeystoreFile = new File(mContext.getCacheDir(), mKeystoreName);
        }

        /**
         * Create and saves RSA 1024 Private key with given alias and password. Use generateAsymmetricKey(@NonNull
         * KeyProps keyProps) to customize key properties
         * <p/>
         * Saves key to KeyStore. Uses keystore with default type located in application cache on device
         * if API < 18. Uses AndroidKeyStore if API is >= 18.
         *
         * @return KeyPair or null if any error occurs
         */
        public KeyPair generateAsymmetricKey(@NonNull String alias, char[] password) {
            final Calendar start = Calendar.getInstance();
            final Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 20);

            KeyProps keyProps = new KeyProps.Builder()
                    .setAlias(alias)
                    .setPassword(password)
                    .setKeySize(1024)
                    .setKeyType(ALGORITHM_RSA)
                    .setSerialNumber(BigInteger.ONE)
                    .setSubject(new X500Principal("CN=" + alias + " CA Certificate"))
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .setBlockModes(BLOCK_MODE_ECB)
                    .setEncryptionPaddings(PADDING_PKCS_1)
                    .setSignatureAlgorithm(ALGORITHM_SHA256_WITH_RSA_ENCRYPTION)
                    .build();

            return generateAsymmetricKey(keyProps);
        }

        /**
         * Create and saves Private key specified in KeyProps with self signed x509 Certificate.
         * <p/>
         * Saves key to KeyStore. Uses keystore with default type located in application cache on device
         * if API < 18. Uses AndroidKeyStore if API is >= 18.
         *
         * @return KeyPair or null if any error occurs
         */
        public KeyPair generateAsymmetricKey(@NonNull KeyProps keyProps) {
            KeyPair result = null;
            if (lowerThenJellyBean()) {
                result = generateDefaultAsymmetricKey(keyProps);
            } else if (lowerThenMarshmallow()) {
                result = generateAndroidJellyAsymmetricKey(keyProps);
            } else {
                result = generateAndroidMAsymmetricKey(keyProps);
            }
            return result;
        }

        /**
         * Create and saves 256 AES SecretKey key using provided alias and password.
         * <p/>
         * Saves key to KeyStore. Uses keystore with default type located in application cache on device
         * if API < 23. Uses AndroidKeyStore if API is >= 23.
         *
         * @return KeyPair or null if any error occurs
         */
        public SecretKey generateSymmetricKey(@NonNull String alias, char[] password) {
            KeyProps keyProps = new KeyProps.Builder()
                    .setAlias(alias)
                    .setPassword(password)
                    .setKeySize(256)
                    .setKeyType(ALGORITHM_AES)
                    .setBlockModes(BLOCK_MODE_CBC)
                    .setEncryptionPaddings(PADDING_PKCS_7)
                    .build();
            return generateSymmetricKey(keyProps);
        }

        /**
         * Create and saves SecretKey key specified in KeyProps.
         * <p/>
         * Saves key to KeyStore. Uses keystore with default type located in application cache on device
         * if API < 23. Uses AndroidKeyStore if API is >= 23.
         *
         * @return KeyPair or null if any error occurs
         */
        public SecretKey generateSymmetricKey(@NonNull KeyProps keyProps) {
            SecretKey result = null;
            if (lowerThenMarshmallow()) {
                result = generateDefaultSymmetricKey(keyProps);
            } else {
                result = generateAndroidSymmetricKey(keyProps);
            }
            return result;
        }

        /**
         * @return KeyPair or null if any error occurs
         */
        public KeyPair getAsymmetricKey(@NonNull String alias, char[] password) {
            KeyPair result = null;
            if (lowerThenJellyBean()) {
                result = getAsymmetricKeyFromDefaultKeyStore(alias, password);
            } else {
                result = getAsymmetricKeyFromAndroidKeyStore(alias);
            }
            return result;
        }

        /**
         * @return SecretKey or null if any error occurs
         */
        public SecretKey getSymmetricKey(@NonNull String alias, char[] password) {
            SecretKey result = null;
            if (lowerThenMarshmallow()) {
                result = getSymmetricKeyFromDefaultKeyStore(alias, password);
            } else {
                result = getSymmetricKeyFromAndroidtKeyStore(alias);
            }
            return result;
        }

        /**
         * @return true if key with given alias is in keystore
         */
        public boolean hasKey(@NonNull String alias) {
            boolean result = false;
            try {
                KeyStore keyStore;
                if (lowerThenJellyBean()) {
                    keyStore = createDefaultKeyStore();
                    result = isKeyEntry(alias, keyStore);
                } else if (lowerThenMarshmallow()) {
                    keyStore = createAndroidKeystore();
                    result = isKeyEntry(alias, keyStore);
                    if (!result) {
                        // SecretKey's are stored in default keystore up to 23 API
                        keyStore = createDefaultKeyStore();
                        result = isKeyEntry(alias, keyStore);
                    }
                } else {
                    keyStore = createAndroidKeystore();
                    result = isKeyEntry(alias, keyStore);
                }

            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
                onException(e);
            }

            return result;
        }

        /**
         * Deletes key with given alias
         */
        public void deleteKey(@NonNull String alias) {
            try {
                KeyStore keyStore;
                if (lowerThenJellyBean()) {
                    keyStore = createDefaultKeyStore();
                    deleteEntry(alias, keyStore);
                } else if (lowerThenMarshmallow()) {
                    keyStore = createAndroidKeystore();
                    if (isKeyEntry(alias, keyStore)) {
                        keyStore.deleteEntry(alias);
                    } else {
                        keyStore = createDefaultKeyStore();
                        if (isKeyEntry(alias, keyStore)) {
                            keyStore.deleteEntry(alias);
                        }
                    }
                } else {
                    keyStore = createAndroidKeystore();
                    deleteEntry(alias, keyStore);
                }

            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
                onException(e);
            }
        }

        private boolean isKeyEntry(@NonNull String alias, KeyStore keyStore) throws KeyStoreException {
            return keyStore != null && keyStore.isKeyEntry(alias);
        }

        private void deleteEntry(@NonNull String alias, KeyStore keyStore) throws KeyStoreException {
            if (keyStore != null) {
                keyStore.deleteEntry(alias);
            }
        }

        private KeyPair generateDefaultAsymmetricKey(KeyProps keyProps) {
            try {
                KeyPair keyPair = createAsymmetricKey(keyProps);
                PrivateKey key = keyPair.getPrivate();
                X509Certificate certificate = keyToCertificateReflection(keyPair, keyProps);
                KeyStore keyStore = createDefaultKeyStore();

                keyStore.setKeyEntry(keyProps.mAlias, key, keyProps.mPassword, new Certificate[]{certificate});
                keyStore.store(new FileOutputStream(mKeystoreFile), mKeystorePassword);
                return keyPair;
            } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | UnsupportedOperationException e) {
                onException(e);
            } catch (NoSuchMethodException e) {
                onException(e);
            } catch (InvocationTargetException e) {
                onException(e);
            } catch (InstantiationException e) {
                onException(e);
            } catch (IllegalAccessException e) {
                onException(e);
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        private KeyPair generateAndroidJellyAsymmetricKey(KeyProps keyProps) {
            try {
                KeyPairGeneratorSpec keySpec = keyPropsToKeyPairGeneratorSpec(keyProps);
                return generateAndroidAsymmetricKey(keyProps, keySpec);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
                onException(e);
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.M)
        private KeyPair generateAndroidMAsymmetricKey(KeyProps keyProps) {
            try {
                KeyGenParameterSpec keySpec = keyPropsToKeyGenParameterASpec(keyProps);
                return generateAndroidAsymmetricKey(keyProps, keySpec);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
                onException(e);
            }
            return null;
        }

        private KeyPair generateAndroidAsymmetricKey(KeyProps keyProps, AlgorithmParameterSpec keySpec)
                throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
            String provider = PROVIDER_ANDROID_KEY_STORE;
            KeyPairGenerator generator = KeyPairGenerator.getInstance(keyProps.mKeyType, provider);
            generator.initialize(keySpec);
            return generator.generateKeyPair();
        }

        private KeyPair createAsymmetricKey(KeyProps keyProps) throws NoSuchAlgorithmException {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(keyProps.mKeyType);
            generator.initialize(keyProps.mKeySize);
            return generator.generateKeyPair();
        }

        private SecretKey generateDefaultSymmetricKey(KeyProps keyProps) {
            try {
                SecretKey key = createSymmetricKey(keyProps);
                SecretKeyEntry keyEntry = new SecretKeyEntry(key);
                KeyStore keyStore = createDefaultKeyStore();

                keyStore.setEntry(keyProps.mAlias, keyEntry, new PasswordProtection(keyProps.mPassword));
                keyStore.store(new FileOutputStream(mKeystoreFile), mKeystorePassword);
                return key;
            } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException e) {
                onException(e);
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.M)
        private SecretKey generateAndroidSymmetricKey(KeyProps keyProps) {
            try {
                String provider = PROVIDER_ANDROID_KEY_STORE;
                KeyGenerator keyGenerator = KeyGenerator.getInstance(keyProps.mKeyType, provider);
                KeyGenParameterSpec keySpec = keyPropsToKeyGenParameterSSpec(keyProps);
                keyGenerator.init(keySpec);
                return keyGenerator.generateKey();
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
                onException(e);
            }
            return null;
        }

        /**
         * Generating X509Certificate using private com.android.org.bouncycastle.x509.X509V3CertificateGenerator class.
         * If it is not found, tries to use
         * Google did copied http://www.bouncycastle.org/ but made it private. To not include additional library Im
         * using reflection here. Tested on API level 16, 17
         */
        private X509Certificate keyToCertificateReflection(KeyPair keyPair, KeyProps keyProps)
                throws UnsupportedOperationException, IllegalAccessException, InstantiationException,
                NoSuchMethodException,
                InvocationTargetException {

            Class generatorClass = null;
            try {
                generatorClass = Class.forName("com.android.org.bouncycastle.x509.X509V3CertificateGenerator");
            } catch (ClassNotFoundException e) {
                // if there is no android default implementation of X509V3CertificateGenerator try to find it from library
                try {
                    generatorClass = Class.forName("org.bouncycastle.x509.X509V3CertificateGenerator");
                } catch (ClassNotFoundException e1) {
                    throw new UnsupportedOperationException(
                            "You need to include  http://www.bouncycastle.org/ library to generate KeyPair on "
                                    + VERSION
                                    + " API version. You can do this via gradle using command 'compile 'org.bouncycastle:bcprov-jdk15on:1.54'");
                }
            }
            return keyToCertificateReflection(generatorClass, keyPair, keyProps);
        }

        /**
         * Generating X509Certificate using private com.android.org.bouncycastle.x509.X509V3CertificateGenerator class.
         * Google did copied http://www.bouncycastle.org/ but made it private. To not include additional library Im
         * using reflection here. Tested on API level 16, 17
         */
        private X509Certificate keyToCertificateReflection(Class generatorClass, KeyPair keyPair, KeyProps keyProps)
                throws IllegalAccessException, InstantiationException, NoSuchMethodException,
                InvocationTargetException {
            Object generator = generatorClass.newInstance();

            Method method = generator.getClass().getMethod("setPublicKey", PublicKey.class);
            method.invoke(generator, keyPair.getPublic());

            method = generator.getClass().getMethod("setSerialNumber", BigInteger.class);
            method.invoke(generator, keyProps.mSerialNumber);

            method = generator.getClass().getMethod("setSubjectDN", X500Principal.class);
            method.invoke(generator, keyProps.mSubject);

            method = generator.getClass().getMethod("setIssuerDN", X500Principal.class);
            method.invoke(generator, keyProps.mSubject);

            method = generator.getClass().getMethod("setNotBefore", Date.class);
            method.invoke(generator, keyProps.mStartDate);

            method = generator.getClass().getMethod("setNotAfter", Date.class);
            method.invoke(generator, keyProps.mEndDate);

            method = generator.getClass().getMethod("setSignatureAlgorithm", String.class);
            method.invoke(generator, keyProps.mSignatureAlgorithm);

            method = generator.getClass().getMethod("generate", PrivateKey.class, String.class);
            return (X509Certificate) method.invoke(generator, keyPair.getPrivate(), PROVIDER_BC);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        private KeyPairGeneratorSpec keyPropsToKeyPairGeneratorSpec(KeyProps keyProps) throws NoSuchAlgorithmException {
            return new KeyPairGeneratorSpec.Builder(mContext)
                    .setAlias(keyProps.mAlias)
                    .setSerialNumber(keyProps.mSerialNumber)
                    .setSubject(keyProps.mSubject)
                    .setStartDate(keyProps.mStartDate)
                    .setEndDate(keyProps.mEndDate)
                    .build();
        }

        @TargetApi(Build.VERSION_CODES.M)
        private KeyGenParameterSpec keyPropsToKeyGenParameterASpec(KeyProps keyProps) throws NoSuchAlgorithmException {
            int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            return new KeyGenParameterSpec.Builder(keyProps.mAlias, purposes)
                    .setKeySize(keyProps.mKeySize)
                    .setCertificateSerialNumber(keyProps.mSerialNumber)
                    .setCertificateSubject(keyProps.mSubject)
                    .setCertificateNotBefore(keyProps.mStartDate)
                    .setCertificateNotAfter(keyProps.mEndDate)
                    .setBlockModes(keyProps.mBlockModes)
                    .setEncryptionPaddings(keyProps.mEncryptionPaddings)
                    .build();
        }

        @TargetApi(Build.VERSION_CODES.M)
        private KeyGenParameterSpec keyPropsToKeyGenParameterSSpec(KeyProps keyProps) throws NoSuchAlgorithmException {
            int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            return new KeyGenParameterSpec.Builder(keyProps.mAlias, purposes)
                    .setKeySize(keyProps.mKeySize)
                    .setBlockModes(keyProps.mBlockModes)
                    .setEncryptionPaddings(keyProps.mEncryptionPaddings)
                    .build();
        }

        private SecretKey createSymmetricKey(KeyProps keyProps) throws NoSuchAlgorithmException {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(keyProps.mKeyType);
            keyGenerator.init(keyProps.mKeySize);
            SecretKey key = keyGenerator.generateKey();
            return key;
        }

        private KeyPair getAsymmetricKeyFromDefaultKeyStore(@NonNull String alias, char[] password) {
            KeyPair result = null;
            try {
                // get asymmetric key
                KeyStore keyStore = createDefaultKeyStore();
                PasswordProtection protection = new PasswordProtection(password);
                PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(alias, protection);
                if(entry != null) {
                    result = new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());
                }
            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
                onException(e);
            }
            return result;
        }

        private KeyPair getAsymmetricKeyFromAndroidKeyStore(@NonNull String alias) {
            KeyPair result = null;
            try {
                KeyStore keyStore = createAndroidKeystore();
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
                if(privateKey != null) {
                    PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
                    result = new KeyPair(publicKey, privateKey);
                }
            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
                onException(e);
            }
            return result;
        }

        private SecretKey getSymmetricKeyFromDefaultKeyStore(@NonNull String alias, char[] password) {
            SecretKey result = null;
            try {
                KeyStore keyStore = createDefaultKeyStore();
                result = (SecretKey) keyStore.getKey(alias, password);
            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
                onException(e);
            }
            return result;
        }

        private SecretKey getSymmetricKeyFromAndroidtKeyStore(@NonNull String alias) {
            SecretKey result = null;
            try {
                KeyStore keyStore = createAndroidKeystore();
                result = (SecretKey) keyStore.getKey(alias, null);
            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
                onException(e);
            }
            return result;
        }

        /**
         * Cache for default keystore
         */
        private KeyStore mDefaultKeyStore;
        private KeyStore createDefaultKeyStore()
                throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
            if (mDefaultKeyStore == null) {
                String defaultType = getDefaultType();
                mDefaultKeyStore = getInstance(defaultType);
                if (!mKeystoreFile.exists()) {
                    mDefaultKeyStore.load(null);
                } else {
                    mDefaultKeyStore.load(new FileInputStream(mKeystoreFile), mKeystorePassword);
                }
            }
            return mDefaultKeyStore;
        }

        /**
         * Cache for android keystore
         */
        private KeyStore mAndroidKeyStore;
        private KeyStore createAndroidKeystore()
                throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
            if (mAndroidKeyStore == null) {
                mAndroidKeyStore = KeyStore.getInstance(PROVIDER_ANDROID_KEY_STORE);
            }
            mAndroidKeyStore.load(null);
            return mAndroidKeyStore;
        }
    }

    /**
     * API to encrypt/decrypt data
     */
    public static class Crypto extends ErrorHandler {

        private static final String UTF_8 = "UTF-8";
        private static final String IV_SEPARATOR = "]";
        private String mTransformation;
        private int mEncryptionBlockSize;
        private int mDecryptionBlockSize;

        /**
         * Initializes Crypto to encrypt/decrypt data with given transformation.
         *
         * @param transformation is used to encrypt/decrypt data. See {@link Cipher} for more info.
         */
        public Crypto(@NonNull String transformation) {
            mTransformation = transformation;
        }

        /**
         * Initializes Crypto to encrypt/decrypt data using buffer with provided lengths. This might be useful if you
         * want to encrypt/decrypt big amount of data using Block Based Algorithms (such as RSA). By default
         * they can proceed only one block of data, not bigger then a size of a key that was used for
         * encryption/decryption.
         *
         * @param transformation is used to encrypt/decrypt data. See {@link Cipher} for more info.
         * @param encryptionBlockSize block size for keys used with this Crypto for encryption. For 1024 size
         * RSA/ECB/PKCS1Padding key will equal to (keySize / 8) - 11 == (1024 / 8) - 11 == 117
         * @param decryptionBlockSize block size for keys used with this Crypto for decryption. For 1024 size
         * RSA/ECB/PKCS1Padding key will equal to (keySize / 8) == (1024 / 8) == 128
         */
        public Crypto(@NonNull String transformation, int encryptionBlockSize, int decryptionBlockSize) {
            mTransformation = transformation;
            mEncryptionBlockSize = encryptionBlockSize;
            mDecryptionBlockSize = decryptionBlockSize;
        }

        /**
         * The same as encrypt(data, key.getPublic(), false);
         *
         * @return encrypted data in Base64 String or null if any error occur. Doesn't use Initialisation Vectors
         */
        public String encrypt(@NonNull String data, @NonNull KeyPair key) {
            return encrypt(data, key.getPublic(), false);
        }

        /**
         * The same as encrypt(data, key, true)
         *
         * @return encrypted data in Base64 String or null if any error occur. Does use Initialisation Vectors
         */
        public String encrypt(@NonNull String data, @NonNull SecretKey key) {
            return encrypt(data, key, true);
        }

        /**
         * @param useInitialisationVectors specifies when ever IvParameterSpec should be used in encryption
         *
         * @return encrypted data in Base64 String or null if any error occur. if useInitialisationVectors is true, data
         * also contains iv key inside. In this case data will be returned in this format <iv key>]<encrypted data>
         */
        public String encrypt(@NonNull String data, @NonNull Key key, boolean useInitialisationVectors) {
            String result = "";
            try {
                Cipher cipher = Cipher.getInstance(mTransformation == null ? key.getAlgorithm() : mTransformation);
                cipher.init(Cipher.ENCRYPT_MODE, key);

                if (useInitialisationVectors) {
                    byte[] iv = cipher.getIV();
                    String ivString = Base64.encodeToString(iv, Base64.DEFAULT);
                    result = ivString + IV_SEPARATOR;
                }

                byte[] plainData = data.getBytes(UTF_8);
                byte[] decodedData;
                if (mEncryptionBlockSize == 0 && mDecryptionBlockSize == 0) {
                    decodedData = decode(cipher, plainData);
                } else {
                    decodedData = decodeWithBuffer(cipher, plainData, mEncryptionBlockSize);
                }

                String encodedString = Base64.encodeToString(decodedData, Base64.DEFAULT);
                result += encodedString;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException |
                    IllegalBlockSizeException | IOException e) {
                onException(e);
            }
            return result;
        }

        /**
         * The same as decrypt(data, key.getPrivate(), false)
         *
         * @param data Base64 encrypted data. Doesn't use Initialisation Vectors
         *
         * @return decrypted data or null if any error occur
         */
        public String decrypt(@NonNull String data, @NonNull KeyPair key) {
            return decrypt(data, key.getPrivate(), false);
        }


        /**
         * The same as decrypt(data, key, true)
         *
         * @param data Base64 encrypted data with iv key. Does use Initialisation Vectors
         *
         * @return decrypted data or null if any error occur
         */
        public String decrypt(@NonNull String data, @NonNull SecretKey key) {
            return decrypt(data, key, true);
        }


        /**
         * @param data Base64 encrypted data. If useInitialisationVectors is enabled, data should contain iv key
         * inside. In this case data should be in this format <iv key>]<encrypted data>
         * @param useInitialisationVectors specifies when ever IvParameterSpec should be used in encryption
         *
         * @return decrypted data or null if any error occur
         */
        public String decrypt(@NonNull String data, @NonNull Key key, boolean useInitialisationVectors) {
            String result = null;
            try {
                String transformation = mTransformation == null ? key.getAlgorithm() : mTransformation;
                Cipher cipher = Cipher.getInstance(transformation);

                String encodedString;

                if (useInitialisationVectors) {
                    String[] split = data.split(IV_SEPARATOR);
                    String ivString = split[0];
                    encodedString = split[1];
                    IvParameterSpec ivSpec = new IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT));
                    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
                } else {
                    encodedString = data;
                    cipher.init(Cipher.DECRYPT_MODE, key);
                }

                byte[] decodedData;
                byte[] encryptedData = Base64.decode(encodedString, Base64.DEFAULT);
                if (mEncryptionBlockSize == 0 && mDecryptionBlockSize == 0) {
                    decodedData = decode(cipher, encryptedData);
                } else {
                    decodedData = decodeWithBuffer(cipher, encryptedData, mDecryptionBlockSize);
                }
                result = new String(decodedData, UTF_8);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException | InvalidAlgorithmParameterException e) {
                onException(e);
            }
            return result;
        }

        private byte[] decode(@NonNull Cipher cipher, @NonNull byte[] plainData)
                throws IOException, IllegalBlockSizeException, BadPaddingException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(baos, cipher);
            cipherOutputStream.write(plainData);
            cipherOutputStream.close();
            return baos.toByteArray();
        }

        private byte[] decodeWithBuffer(@NonNull Cipher cipher, @NonNull byte[] plainData, int bufferLength)
                throws IllegalBlockSizeException, BadPaddingException {
            // string initialize 2 buffers.
            // scrambled will hold intermediate results
            byte[] scrambled;

            // toReturn will hold the total result
            byte[] toReturn = new byte[0];

            // holds the bytes that have to be modified in one step
            byte[] buffer = new byte[(plainData.length > bufferLength ? bufferLength : plainData.length)];

            for (int i = 0; i < plainData.length; i++) {
                if ((i > 0) && (i % bufferLength == 0)) {
                    //execute the operation
                    scrambled = cipher.doFinal(buffer);
                    // add the result to our total result.
                    toReturn = append(toReturn, scrambled);
                    // here we calculate the bufferLength of the next buffer required
                    int newLength = bufferLength;

                    // if newLength would be longer than remaining bytes in the bytes array we shorten it.
                    if (i + bufferLength > plainData.length) {
                        newLength = plainData.length - i;
                    }
                    // clean the buffer array
                    buffer = new byte[newLength];
                }
                // copy byte into our buffer.
                buffer[i % bufferLength] = plainData[i];
            }

            // this step is needed if we had a trailing buffer. should only happen when encrypting.
            // example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
            scrambled = cipher.doFinal(buffer);

            // final step before we can return the modified data.
            toReturn = append(toReturn, scrambled);
            return toReturn;
        }

        private byte[] append(byte[] prefix, byte[] suffix) {
            byte[] toReturn = new byte[prefix.length + suffix.length];
            for (int i = 0; i < prefix.length; i++) {
                toReturn[i] = prefix[i];
            }
            for (int i = 0; i < suffix.length; i++) {
                toReturn[i + prefix.length] = suffix[i];
            }
            return toReturn;
        }
    }

    public static final class KeyProps {
        private String mAlias;
        private char[] mPassword;
        private String mKeyType;
        private int mKeySize;

        private String mBlockModes;
        private String mEncryptionPaddings;

        private String mSignatureAlgorithm;
        private BigInteger mSerialNumber;
        private X500Principal mSubject;
        private Date mStartDate;
        private Date mEndDate;

        public static final class Builder {
            private KeyProps mProps = new KeyProps();

            /**
             * Required for Symmetric and Asymmetric key
             */
            public Builder setAlias(String alias) {
                mProps.mAlias = alias;
                return this;
            }

            /**
             * Required for Symmetric and Asymmetric key
             */
            public Builder setKeyType(String keyType) {
                mProps.mKeyType = keyType;
                return this;
            }

            /**
             * Required for Symmetric using API < 23 and Asymmetric key using API < 18.
             *
             * @param password used for additional key secure in Default KeyStore.
             */
            public Builder setPassword(char[] password) {
                mProps.mPassword = password;
                return this;
            }

            /**
             * Required for Symmetric using API < 23 and Asymmetric key using API < 18.
             */
            public Builder setKeySize(int keySize) {
                mProps.mKeySize = keySize;
                return this;
            }

            /**
             * Required for Asymmetric key.
             */
            public Builder setSerialNumber(BigInteger serialNumber) {
                mProps.mSerialNumber = serialNumber;
                return this;
            }

            /**
             * Required for Asymmetric key.
             * <p/>
             * Example: final X500Principal subject = new X500Principal("CN=" + alias + " CA Certificate");
             */
            public Builder setSubject(X500Principal subject) {
                mProps.mSubject = subject;
                return this;
            }

            /**
             * Required for Asymmetric key.
             */
            public Builder setStartDate(Date startDate) {
                mProps.mStartDate = startDate;
                return this;
            }

            /**
             * Required for Asymmetric key.
             */
            public Builder setEndDate(Date endDate) {
                mProps.mEndDate = endDate;
                return this;
            }

            /**
             * Required for Symmetric and Asymmetric keys using API >= 23.
             */
            public Builder setBlockModes(String blockModes) {
                mProps.mBlockModes = blockModes;
                return this;
            }

            /**
             * Required for Symmetric and Asymmetric keys using API >= 23.
             */
            public Builder setEncryptionPaddings(String encryptionPaddings) {
                mProps.mEncryptionPaddings = encryptionPaddings;
                return this;
            }

            /**
             * Required for Asymmetric key using API < 18.
             */
            public Builder setSignatureAlgorithm(String signatureAlgorithm) {
                mProps.mSignatureAlgorithm = signatureAlgorithm;
                return this;
            }

            public KeyProps build() {
                return mProps;
            }
        }
    }

    private static class ErrorHandler {
        private ErrorListener mErrorListener;

        public void setErrorListener(ErrorListener errorListener) {
            mErrorListener = errorListener;
        }

        /**
         * Prints exception in logs and triggers listener if it is not null
         */
        protected void onException(Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            } else {
                Log.e(TAG, e.getMessage());
                Log.e(TAG, e.toString());
            }
            if (mErrorListener != null) {
                mErrorListener.onError(e);
            }
        }
    }

    public interface ErrorListener {
        void onError(Exception e);
    }

    /**
     * @return true it current api version is lower then 18
     */
    private static boolean lowerThenJellyBean() {
        return VERSION < Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * @return true it current api version is lower then 23
     */
    private static boolean lowerThenMarshmallow() {
        return VERSION < Build.VERSION_CODES.M;
    }
}
