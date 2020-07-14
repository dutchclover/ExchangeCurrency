package com.nostra13.universalimageloader.core;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;



public class MemKeyGenerator {

    public static String getkey(String uri, ImageLoaderConfiguration imageLoaderConfig){
        ImageSize imageSize = imageLoaderConfig.getMaxImageSize();
        return MemoryCacheUtils.generateKey(uri, imageSize);
    }
}
