package com.amicly.photogallery;

/**
 * Created by darrankelinske on 11/2/17.
 */

public class GalleryItem {
    private String caption;
    private String id;
    private String url;

    public String getCaption() {
        return caption;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return caption;
    }
}
