package com.g.e.photogallery;

import com.google.gson.Gson;

import org.json.JSONObject;

public class JsonPhotoItem {
    private String id;
    private String title;
    private String url;

    public JsonPhotoItem() {
    }

    public GalleryItem toGalleryItem() {
        GalleryItem galleryItem = new GalleryItem();
        galleryItem.setId(id);
        galleryItem.setCaption(title);
        galleryItem.setUrl(url);

        return galleryItem;
    }

    public static JsonPhotoItem createFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JsonPhotoItem.class);
    }

    public static JsonPhotoItem createFromJsonObject(JSONObject jsonObject) {
        return createFromJson(jsonObject.toString());
    }
}
