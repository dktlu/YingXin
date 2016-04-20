package com.laohudada.yingxin.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SD卡相关辅助类
 * Created by Administrator on 2016/4/7.
 */
public class SDCardUtils {

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SDCard路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取SDCard的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardFreeSize() {
        if (isSDCardEnable()) {
            StatFs statFs = new StatFs(getSDCardPath());
            long blockSize;
            long availableBlocks;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.getBlockSizeLong();
                availableBlocks = statFs.getAvailableBlocksLong();
            } else {
                //获取空闲的数据块的数量
                availableBlocks = statFs.getAvailableBlocks();
                //获取单个数据块的大小（byte）
                blockSize = statFs.getBlockSize();
            }
            return availableBlocks * blockSize;
        }
        return 0;
    }

    /**
     * 获取SDCard的容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs statFs = new StatFs(getSDCardPath());
            long blockSize;
            long allBlocks;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.getBlockSizeLong();
                allBlocks = statFs.getBlockCountLong();
            } else {
                allBlocks = statFs.getBlockCount();
                blockSize = statFs.getBlockSize();
            }
            return allBlocks * blockSize;
        }
        return 0;
    }

    /**
     * 获取系统路径下剩余的容量 单位byte
     *
     * @return
     */
    public static long getRootFreeSize() {
        StatFs statFs = new StatFs(getRootDirectoryPath());
        long blockSize;
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            availableBlocks = statFs.getAvailableBlocksLong();
        } else {
            //获取空闲的数据块的数量
            availableBlocks = statFs.getAvailableBlocks();
            //获取单个数据块的大小（byte）
            blockSize = statFs.getBlockSize();
        }
        return availableBlocks * blockSize;
    }

    /**
     * 获取系统路径的容量 单位byte
     *
     * @return
     */
    public static long getRootAllSize() {
        StatFs statFs = new StatFs(getRootDirectoryPath());
        long blockSize;
        long allBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            allBlocks = statFs.getBlockCountLong();
        } else {
            allBlocks = statFs.getBlockCount();
            blockSize = statFs.getBlockSize();
        }
        return allBlocks * blockSize;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数， 单位byte
     *
     * @param filePath
     * @return
     */
    public static long getFreeBytes(String filePath) {
        //如果是SDCard下的路径，则获取SDCard可用容量
        if (filePath.startsWith(getSDCardPath())) {
            return getSDCardFreeSize();
        } else {    //如果是内部存储的路径，则获取内存存储的可用容量
            return getRootFreeSize();
        }
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }
}
