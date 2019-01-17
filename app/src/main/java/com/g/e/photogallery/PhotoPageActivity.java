package com.g.e.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {
    Fragment mFragment;

    public static Intent createIntent (Context context, Uri photoPageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = PhotoPageFragment.createInstance(getIntent().getData());
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        PhotoPageFragment photoPageFragment = (PhotoPageFragment) mFragment;

        if (photoPageFragment.goBack()) {
        } else super.onBackPressed();
    }
}
