package com.sonjara.listenup.database;

import android.content.Context;

import java.io.File;

public class ImageCache
{
    private File                cacheDir;
    private File                cacheDirEvent;
    private File                cacheDirPromotion;

    public ImageCache(final Context context)
    {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            this.cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "ListenUp/res/images");
        }
        else
        {
            this.cacheDir = context.getCacheDir();
        }
        if (!this.cacheDir.exists() && !this.cacheDirEvent.exists() && !this.cacheDirPromotion.exists())
        {
            this.cacheDir.mkdirs();
        }
    }

    public File getImageFile(int image_id)
    {

    }

}
