package com.example.unipin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;

public class Share {
    Context context;
    String photoUrl;
    private Bitmap bitmapp;
    LoadingView loadingView;
    int loadingLayout,loadingDrawable,loadingImageView;
    //Bitmap bitmapp = null;

    public Share(Activity activity, Context context, String photoUrl, int loadingLayout, int loadingImageView, int loadingDrawable) {
        this.context = context;
        this.photoUrl = photoUrl;
        this.loadingDrawable = loadingDrawable;
        this.loadingLayout = loadingLayout;
        this.loadingImageView = loadingImageView;

        loadingView = new LoadingView(activity,loadingLayout,loadingImageView,loadingDrawable);

        Glide.with(context)
                .asBitmap()
                .load(photoUrl)
                .override(Target.SIZE_ORIGINAL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        //you can use loaded bitmap here
                        bitmapp = bitmap;
                        new TestAsync(loadingView).execute();
                    }
                });

    }




    class TestAsync extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        LoadingView loadingView;

        public TestAsync(LoadingView loadingView) {
            this.loadingView = loadingView;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            loadingView.showDialog();
        }

        protected String doInBackground(Void...arg0) {

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("image/*");

            // save the image and get it's uri
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapp.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmapp, "Title", null);
            Uri uri = Uri.parse(path);
            i.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(i, "Share Image"));
            loadingView.hideDialog();

            return "done";
        }

        protected void onProgressUpdate(Integer...a) {
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    class LoadingView   {
        Activity activity;
        Dialog dialog;
        int loadingLayout, loadingImageView, loadingDrawable;

        public LoadingView(Activity activity, int loadingLayout, int loadingImageView, int loadingDrawable) {
            this.activity = activity;
            this.loadingDrawable = loadingDrawable;
            this.loadingImageView = loadingImageView;
            this.loadingLayout = loadingLayout;
        }

        public void showDialog() {

            dialog  = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //...set cancelable false so that it's never get hidden
            dialog.setCancelable(false);
            //...that's the layout i told you will inflate later
            dialog.setContentView(loadingLayout);

            //...initialize the imageView form infalted layout
            ImageView gifImageView = dialog.findViewById(loadingImageView);



            Glide.with(activity)
                    .asGif()
                    .load(loadingDrawable)
                    .into(gifImageView);

            dialog.show();
        }

        //..also create a method which will hide the dialog when some work is done
        public void hideDialog(){
            dialog.dismiss();
        }

    }


}
