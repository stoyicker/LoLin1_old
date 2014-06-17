package org.jorge.lolin1.io.local;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import org.jorge.lolin1.io.net.HTTPServices;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.jorge.lolin1.utils.LoLin1DebugUtils.logString;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by Jorge Antonio Diaz-Benito Soriano on 03/05/2014.
 */
public final class ProfileCacheableBitmapLoader {

    private static final int DEFAULT_HARD_CACHE_CAPACITY = 10;
    private static final int DEFAULT_DELAY_BEFORE_PURGE_MILLIS = 10000;
    private final int HARD_CACHE_CAPACITY;
    private final int DELAY_BEFORE_PURGE_MILLIS;
    private final String VERSION_PLACEHOLDER = "VERSION_PLACEHOLDER", ID_PLACEHOLDER =
            "ID_PLACEHOLDER", BASE_URL =
            "http://ddragon.leagueoflegends.com/cdn/" + VERSION_PLACEHOLDER + "/img/profileicon/" +
                    ID_PLACEHOLDER + ".png";

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<Integer, Bitmap> sHardBitmapCache;

    // Soft cache for bitmaps kicked out of hard cache
    private final ConcurrentHashMap<Integer, SoftReference<Bitmap>> sSoftBitmapCache;

    private final Handler purgeHandler;

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    public ProfileCacheableBitmapLoader() {
        this(-1, -1);
    }

    public ProfileCacheableBitmapLoader(int hardCacheCapacity,
                                        int delayBeforePurgeMillis) {
        try {
            Looper.prepare();
        } catch (RuntimeException ex) {
            //Already prepared, do nothing
        }
        purgeHandler = new Handler();
        HARD_CACHE_CAPACITY =
                hardCacheCapacity > 0 ? hardCacheCapacity : DEFAULT_HARD_CACHE_CAPACITY;
        DELAY_BEFORE_PURGE_MILLIS = delayBeforePurgeMillis > 0 ? delayBeforePurgeMillis :
                DEFAULT_DELAY_BEFORE_PURGE_MILLIS;
        sSoftBitmapCache = new ConcurrentHashMap<>(HARD_CACHE_CAPACITY / 2);
        sHardBitmapCache =
                new LinkedHashMap<Integer, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            LinkedHashMap.Entry<Integer, Bitmap> eldest) {
                        if (size() > HARD_CACHE_CAPACITY) {
                            // Entries push-out of hard reference cache are transferred to soft reference cache
                            sSoftBitmapCache
                                    .put(eldest.getKey(), new SoftReference<>(eldest.getValue()));
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
    }

    private void addBitmapToCache(int id, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(id, bitmap);
            }
        }
    }

    public static File getPathByID(Context context, int id) {
        return new File(
                context.getExternalFilesDir(
                        LoLin1Utils.getString(context, "profile_icons_folder_name", null)).getAbsolutePath() +
                        LoLin1Utils.getString(context, "symbol_path_separator",
                                null) + id + ".png"
        );
    }

    public void assignImageToProfileView(Activity activity, int id, final ImageView imageView) {
        resetPurgeTimer();
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(id);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(id);
                sHardBitmapCache.put(id, bitmap);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
                return;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(id);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
                return;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(id);
            }
        }

        File path = ProfileCacheableBitmapLoader.getPathByID(activity, id);

        logString("debug", "Path for image with id " + id + ": " + path);

        if (path.exists()) {
            final Bitmap bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(path.getAbsolutePath(), options);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });
            addBitmapToCache(id, bitmap);
            logString("debug", "Exists");
            return;
        }
        logString("debug", "Doesn't exist");

        logString("debug", "Reached last cache level, downloading image with id " + id);

        Bitmap bitmap = loadBitmapFromNetwork(activity, id, imageView);
        addBitmapToCache(id, bitmap);
    }

    private Bitmap loadBitmapFromNetwork(Activity activity, int id, final ImageView imageView) {
        Bitmap bitmap = null;
        if (!LoLin1Utils.isInternetReachable(activity)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        } else {
            File root = activity.getExternalFilesDir(
                    LoLin1Utils.getString(activity, "profile_icons_folder_name", null));
            assert root != null;
            if (!root.exists()) {
                if (!root.mkdirs())
                    throw new RuntimeException("Key mkdirs failed");
            }
            File path;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                HTTPServices.downloadFile(
                        BASE_URL.replace(ID_PLACEHOLDER, id + "").replace(VERSION_PLACEHOLDER,
                                PreferenceManager.getDefaultSharedPreferences(activity)
                                        .getString("pref_version_na", "0")
                        ), path = new File(
                                root.getAbsolutePath() +
                                        LoLin1Utils.getString(activity, "symbol_path_separator",
                                                null) + id + ".png"
                        )
                );
                bitmap = BitmapFactory.decodeFile(path.getAbsolutePath(), options);
            } catch (IOException e) {
                bitmap = null;
            }
        }
        final Bitmap finalBitmap = bitmap;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(finalBitmap);
            }
        });
        return bitmap;
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
