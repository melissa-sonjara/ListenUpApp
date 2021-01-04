package com.sonjara.listenup.database;

import android.content.Context;
import android.widget.ImageView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.io.File;

public class ImageCache
{
    private Context m_context;
    private GlideToVectorYou m_glide;

    public Context getContext()
    {
        return m_context;
    }

    private File                cacheDir;
    private File                cacheDirEvent;
    private File                cacheDirPromotion;

    public ImageCache(final Context context)
    {
        m_context = context;
        m_glide = GlideToVectorYou.init().with(m_context);
    }


    private String getImageUrl(int image_id)
    {
        return "https://listenup.sonjara.com/action/image/show?image_id=" + image_id;
    }

    public void cacheImage(int image_id)
    {
        String url = getImageUrl(image_id);
        m_glide.getRequestBuilder().load(url).preload();
    }

    public void showImage(int image_id, ImageView view)
    {
        m_glide.getRequestBuilder()
                .load(getImageUrl(image_id))
                .onlyRetrieveFromCache(true)
                .into(view);
    }
}
