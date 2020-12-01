package com.example.android.photogallery.RecyclerviewAdapter;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
