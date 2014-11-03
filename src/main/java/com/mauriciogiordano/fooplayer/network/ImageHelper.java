package com.mauriciogiordano.fooplayer.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by mauricio on 10/31/14.
 */
public class ImageHelper {

    private com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    private DisplayImageOptions defaultOptions;

    private static ImageHelper imageHelper = null;

    private ImageHelper(Context context)
    {
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .displayer(new FadeInBitmapDisplayer(400))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(120, 240)
                .defaultDisplayImageOptions(defaultOptions)
                .writeDebugLogs()
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
    }

    private static void load(Context context)
    {
        if(imageHelper == null)
            imageHelper = new ImageHelper(context);
    }

    public static void loadImage(String url, ImageView target, Context context)
    {
        load(context);

        imageHelper.imageLoader.displayImage
        (
            url,
            target,
            imageHelper.defaultOptions
        );
    }
}
