package org.jorge.lolin1.io.local;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The code shown below has been extracted and adapted from
 * https://code.google.com/p/android-imagedownloader/source/browse/trunk/src/com/example/android/imagedownloader/ImageDownloader.java
 * and is therefore protected by its own license.
 */
public final class CacheableBitmapLoader {

    private static final int DEFAULT_HARD_CACHE_CAPACITY = 10;
    private static final int DEFAULT_DELAY_BEFORE_PURGE_MILLIS = 10000;
    private final int HARD_CACHE_CAPACITY;
    private final int DELAY_BEFORE_PURGE_MILLIS;

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache;

    // Soft cache for bitmaps kicked out of hard cache
    private final ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache;

    private final Handler purgeHandler;

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    public CacheableBitmapLoader() {
        this(-1, -1);
    }

    public CacheableBitmapLoader(int hardCacheCapacity,
                                 int delayBeforePurgeMillis) {
        try {
            Looper.prepare();
        }
        catch (RuntimeException ex) {
            //Already prepared, do nothing
        }
        purgeHandler = new Handler();
        HARD_CACHE_CAPACITY =
                hardCacheCapacity > 0 ? hardCacheCapacity : DEFAULT_HARD_CACHE_CAPACITY;
        DELAY_BEFORE_PURGE_MILLIS = delayBeforePurgeMillis > 0 ? delayBeforePurgeMillis :
                DEFAULT_DELAY_BEFORE_PURGE_MILLIS;
        sSoftBitmapCache = new ConcurrentHashMap<>(HARD_CACHE_CAPACITY / 2);
        sHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(
                    LinkedHashMap.Entry<String, Bitmap> eldest) {
                if (size() > HARD_CACHE_CAPACITY) {
                    // Entries push-out of hard reference cache are transferred to soft reference cache
                    sSoftBitmapCache
                            .put(eldest.getKey(), new SoftReference<>(eldest.getValue()));
                    return true;
                }
                else {
                    return false;
                }
            }
        };
    }

    private void addBitmapToCache(String absolutePath, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(absolutePath, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromCache(String absolutePath, int width, int height) {
        resetPurgeTimer();
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(absolutePath + width + height);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(absolutePath + width + height);
                sHardBitmapCache.put(absolutePath + width + height, bitmap);
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(absolutePath + width + height);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                return bitmap;
            }
            else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(absolutePath + width + height);
            }
        }

        Bitmap bitmap = loadBitmapFromDisk(absolutePath, width, height);
        addBitmapToCache(absolutePath, bitmap);
        return bitmap;

    }

    protected Bitmap loadBitmapFromDisk(String absolutePath, int width, int height) {
        Bitmap retAux;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        retAux = BitmapFactory.decodeFile(absolutePath, options);
        if (width > 0 && height > 0) {
            return Bitmap.createScaledBitmap(retAux, width, height, Boolean.TRUE);
        }
        else {
            return retAux;
        }
    }

    private void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    protected void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE_MILLIS);
    }
}
