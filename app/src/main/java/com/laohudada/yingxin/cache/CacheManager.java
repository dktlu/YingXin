package com.laohudada.yingxin.cache;

import android.content.Context;


import com.laohudada.yingxin.utils.NetUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by tao on 2016/1/28.
 */
public class CacheManager {
    //wifi缓存时间为5分钟
    private static long wifi_cache_time = 5 * 60 * 1000;
    //其它网络缓存时间为1小时
    private static long other_cache_time = 60 * 60 * 1000;

    /**
     * 保存对象
     * @param context
     * @param ser
     * @param file
     * @return
     */
    public static boolean saveObject(Context context, String ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     * @param context
     * @param file
     * @return
     */
    public static String readObject(Context context, String file) {
        if (!isExistDataCache(context, file)) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (String) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 判断缓存是否存在
     * @param context
     * @param cacheFile
     * @return
     */
    public static boolean isExistDataCache(Context context, String cacheFile) {
        if (context == null) {
            return false;
        }
        boolean exist = false;
        File data = context.getFileStreamPath(cacheFile);
        if (data.exists()) {
            exist = true;
        }
        return exist;
    }

    /**
     * 判断缓存是否已经失效
     * @param context
     * @param cacheFile
     * @return
     */
    public static boolean isCacheDataFailure(Context context, String cacheFile) {
        File data = context.getFileStreamPath(cacheFile);
        if (!data.exists()) {
            return false;
        }
        long existTime = System.currentTimeMillis() - data.lastModified();
        boolean failure = false;
        if (NetUtils.isWifi(context)) {
            failure = existTime > wifi_cache_time ? true : false;
        } else {
            failure = existTime >other_cache_time ? true : false;
        }
        return failure;
    }
}
