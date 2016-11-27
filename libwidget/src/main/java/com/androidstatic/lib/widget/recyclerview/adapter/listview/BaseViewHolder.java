/**
 * Copyright 2013 Joan Zapata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.androidstatic.lib.widget.recyclerview.adapter.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Allows an abstraction of the ViewHolder pattern.<br>
 * <br>
 * <p/>
 * <b>Usage</b>
 * <p/>
 * 
 * <pre>
 * return BaseViewHolder.get(context, convertView, parent, R.layout.item)
 *         .setText(R.id.tvName, contact.getName())
 *         .setText(R.id.tvEmails, contact.getEmails().toString())
 *         .setText(R.id.tvNumbers, contact.getNumbers().toString()).getView();
 * </pre>
 */
public class BaseViewHolder {

    /** Views indexed with their IDs */
    private final SparseArray<View> views;

    private final Context context;

    private int position;

    private View convertView;

    public int layoutId;

    /**
     * Package private field to retain the associated user object and detect a change
     */
    Object associatedObject;

    protected BaseViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.context = context;
        this.position = position;
        this.layoutId = layoutId;
        this.views = new SparseArray<View>();
        convertView = LayoutInflater.from(context) //
                .inflate(layoutId, parent, false);
        convertView.setTag(this);
    }

    /**
     * This method is the only entry point to get a BaseViewHolder.
     * 
     * @param context The current context.
     * @param convertView The convertView arg passed to the getView() method.
     * @param parent The parent arg passed to the getView() method.
     * @return A BaseViewHolder instance.
     */
    public static BaseViewHolder get(Context context, View convertView, ViewGroup parent,
            int layoutId) {
        return get(context, convertView, parent, layoutId, -1);
    }

    /** This method is package private and should only be used by QuickAdapter. */
    static BaseViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId,
            int position) {
        if (convertView == null) {
            return new BaseViewHolder(context, parent, layoutId, position);
        }

        // Retrieve the existing helper and update its position
        BaseViewHolder existingHelper = (BaseViewHolder) convertView.getTag();

        if (existingHelper.layoutId != layoutId) {
            return new BaseViewHolder(context, parent, layoutId, position);
        }

        existingHelper.position = position;
        return existingHelper;
    }

    /**
     * This method allows you to retrieve a view and perform custom operations on it, not covered by the BaseViewHolder.<br/>
     * If you think it's a common use case, please consider creating a new issue at
     * https://github.com/JoanZapata/base-adapter-helper/issues.
     * 
     * @param viewId The id of the view you want to retrieve.
     */
    public <T extends View> T getView(int viewId) {
        return findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * Will set the text of a TextView.
     * 
     * @param viewId The view id.
     * @param value The text to put in the text view.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setText(int viewId, String value) {
        TextView view = findViewById(viewId);
        if (TextUtils.isEmpty(value)) {
            view.setText("");
        }else{
            view.setText(value);
        }
        return this;
    }

    /**
     * Will set the image of an ImageView from a resource id.
     * 
     * @param viewId The view id.
     * @param imageResId The image resource id.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setImageResource(int viewId, int imageResId) {
        ImageView view = findViewById(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    /**
     * Will set background color of a view.
     * 
     * @param viewId The view id.
     * @param color A color, not a resource id.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = findViewById(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    /**
     * Will set background of a view.
     * 
     * @param viewId The view id.
     * @param backgroundRes A resource to use as a background.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = findViewById(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    /**
     * Will set text color of a TextView.
     * 
     * @param viewId The view id.
     * @param textColor The text color (not a resource id).
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = findViewById(viewId);
        view.setTextColor(textColor);
        return this;
    }

    /**
     * Will set text color of a TextView.
     * 
     * @param viewId The view id.
     * @param textColorRes The text color resource id.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setTextColorRes(int viewId, int textColorRes) {
        TextView view = findViewById(viewId);
        view.setTextColor(context.getResources().getColor(textColorRes));
        return this;
    }

    /**
     * Will set the image of an ImageView from a drawable.
     * 
     * @param viewId The view id.
     * @param drawable The image drawable.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = findViewById(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    /**
     * Will download an image from a URL and put it in an ImageView.<br/>
     * It uses Square's Picasso library to download the image asynchronously and put the result into the ImageView.<br/>
     * Picasso manages recycling of views in a ListView.<br/>
     * If you need more control over the Picasso settings, use {BaseViewHolder#setImageBuilder}.
     * 
     * @param viewId The view id.
     * @param imageUrl The image URL.
     * @return The BaseViewHolder for chaining.
     */
//    public BaseViewHolder setImageUrl(int viewId, String imageUrl) {
//        ImageView view = findViewById(viewId);
//        ImageLoader.getInstance().displayImage(imageUrl, view,
//                UniversalImageLoaderUtil.getInstance());
//
//        return this;
//    }

    /**
     * Will download an image from a URL and put it in an ImageView.<br/>
     * 
     * @param viewId The view id.
     * @param requestBuilder The Picasso request builder. (e.g. Picasso.with(context).load(imageUrl))
     * @return The BaseViewHolder for chaining.
     */
    // public BaseViewHolder setImageBuilder(int viewId, RequestCreator requestBuilder) {
    // ImageView view = findViewById(viewId);
    // requestBuilder.into(view);
    // return this;
    // }

    /**
     * Add an action to set the image of an image view. Can be called multiple times.
     */
    public BaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = findViewById(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    /**
     * Add an action to set the alpha of a view. Can be called multiple times. Alpha between 0-1.
     */
    @SuppressLint("NewApi")
    public BaseViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            findViewById(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            findViewById(viewId).startAnimation(alpha);
        }
        return this;
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     * 
     * @param viewId The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setVisible(int viewId, boolean visible) {
        View view = findViewById(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, int visibility) {
        View view = findViewById(viewId);
        view.setVisibility(visibility);
        return this;
    }

    /**
     * Add links into a TextView.
     * 
     * @param viewId The id of the TextView to linkify.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder linkify(int viewId) {
        TextView view = findViewById(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    /** Apply the typeface to the given viewId, and enable subpixel rendering. */
    public BaseViewHolder setTypeface(int viewId, Typeface typeface) {
        TextView view = findViewById(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    /**
     * Apply the typeface to all the given viewIds, and enable subpixel rendering.
     */
    public BaseViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = findViewById(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    /**
     * Sets the progress of a ProgressBar.
     * 
     * @param viewId The view id.
     * @param progress The progress.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = findViewById(viewId);
        view.setProgress(progress);
        return this;
    }

    /**
     * Sets the progress and max of a ProgressBar.
     * 
     * @param viewId The view id.
     * @param progress The progress.
     * @param max The max value of a ProgressBar.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = findViewById(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    /**
     * Sets the range of a ProgressBar to 0...max.
     * 
     * @param viewId The view id.
     * @param max The max value of a ProgressBar.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setMax(int viewId, int max) {
        ProgressBar view = findViewById(viewId);
        view.setMax(max);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar.
     * 
     * @param viewId The view id.
     * @param rating The rating.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setRating(int viewId, float rating) {
        RatingBar view = findViewById(viewId);
        view.setRating(rating);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) and max of a RatingBar.
     * 
     * @param viewId The view id.
     * @param rating The rating.
     * @param max The range of the RatingBar to 0...max.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setRating(int viewId, float rating, int max) {
        RatingBar view = findViewById(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    /**
     * Sets the tag of the view.
     * 
     * @param viewId The view id.
     * @param tag The tag;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setTag(int viewId, Object tag) {
        View view = findViewById(viewId);
        view.setTag(tag);
        return this;
    }

    /**
     * Sets the tag of the view.
     * 
     * @param viewId The view id.
     * @param key The key of tag;
     * @param tag The tag;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setTag(int viewId, int key, Object tag) {
        View view = findViewById(viewId);
        view.setTag(key, tag);
        return this;
    }

    /**
     * Sets the checked status of a checkable.
     * 
     * @param viewId The view id.
     * @param checked The checked status;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = (Checkable) findViewById(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * Sets the adapter of a adapter view.
     * 
     * @param viewId The view id.
     * @param adapter The adapter;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setAdapter(int viewId, Adapter adapter) {
        AdapterView view = findViewById(viewId);
        view.setAdapter(adapter);
        return this;
    }

    /**
     * Sets the on click listener of the view.
     * 
     * @param viewId The view id.
     * @param listener The on click listener;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = findViewById(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnCheckedChangeListener(int viewId,
            CompoundButton.OnCheckedChangeListener listener) {
        CompoundButton view = findViewById(viewId);
        view.setOnCheckedChangeListener(listener);
        return this;

    }

    /**
     * Sets the on touch listener of the view.
     * 
     * @param viewId The view id.
     * @param listener The on touch listener;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = findViewById(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    /**
     * Sets the on long click listener of the view.
     * 
     * @param viewId The view id.
     * @param listener The on long click listener;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = findViewById(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    /** Retrieve the convertView */
    public View getView() {
        return convertView;
    }

    /**
     * Retrieve the overall position of the data in the list.
     * 
     * @throws IllegalArgumentException If the position hasn't been set at the construction of the this helper.
     */
    public int getPosition() {
        if (position == -1)
            throw new IllegalStateException("Use BaseViewHolder constructor "
                    + "with position if you need to retrieve the position.");
        return position;
    }

    /** Retrieves the last converted object on this view. */
    public Object getAssociatedObject() {
        return associatedObject;
    }

    /** Should be called during convert */
    public void setAssociatedObject(Object associatedObject) {
        this.associatedObject = associatedObject;
    }
}
