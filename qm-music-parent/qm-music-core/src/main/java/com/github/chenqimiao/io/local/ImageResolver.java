package com.github.chenqimiao.io.local;

import com.github.chenqimiao.util.ImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.tag.images.Artwork;


/**
 * @author Qimiao Chen
 * @since 2025/4/1 21:38
 **/
public abstract class ImageResolver {

    public static String resolveArtwork(Artwork artwork) {
        if (artwork == null) {
            return null;
        }
        String mimeType = artwork.getMimeType();

        if (StringUtils.isNotBlank(mimeType)
                && mimeType.startsWith("image/")) {
            return mimeType.replace("image/", "");
        }

        return ImageUtils.resolveType(artwork.getBinaryData());
    }


}
