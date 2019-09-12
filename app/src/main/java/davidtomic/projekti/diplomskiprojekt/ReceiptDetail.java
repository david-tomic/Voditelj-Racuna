package davidtomic.projekti.diplomskiprojekt;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReceiptDetail extends AppCompatActivity {

    TextView mCategoryTextView;
    TextView mTotalTextView;
    TextView mDateTextView;
    ListView mListView;
    ImageView mThumbnailImageView;
    ImageView mIconImageView;
    String path;
    Bitmap bmp;
    ReceiptViewModel mReceiptViewModel;
    int icon;
    float total;
    public int screenWidth;
    public int screenHeight;
    Receipt receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);

        mCategoryTextView = findViewById(R.id.category_tv);
        mTotalTextView = findViewById(R.id.total_tv);
        mDateTextView = findViewById(R.id.date_tv);
        mListView = findViewById(R.id.list_view);
        mThumbnailImageView = findViewById(R.id.image_view_thumbnail);
        mIconImageView = findViewById(R.id.image_view_icon);
        Intent intent = getIntent();

        mReceiptViewModel = ViewModelProviders.of(this).get(ReceiptViewModel.class);

        mCategoryTextView.setText(intent.getStringExtra("category"));
        mDateTextView.setText(intent.getStringExtra("date"));

        total = intent.getFloatExtra("total", 0);
        String formatiranTotal = String.format("%.02f", total);
        mTotalTextView.setText("Ukupno: " +formatiranTotal + " KM");

        path = intent.getStringExtra("path");
//
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        mThumbnailImageView.setImageBitmap(decodeSampledBitmapFromResource(path, mThumbnailImageView.getLayoutParams().width, mThumbnailImageView.getLayoutParams().height));
//
        icon = intent.getIntExtra("icon", 0);
        Glide.with(this).load(icon).placeholder(R.drawable.ic_launcher_background).into(mIconImageView);

        HashMap<String, String> items = (HashMap<String, String>) intent.getSerializableExtra("items");

        ItemsAdapter adapter = new ItemsAdapter(items);
        mListView.setAdapter(adapter);
        mThumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });

        DisplayMetrics displaymetrics;
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
         screenWidth = displaymetrics.widthPixels;
         screenHeight = displaymetrics.heightPixels;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public void showImage() {
        final Dialog builder = new Dialog(this, R.style.Theme_Design_NoActionBar);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.BLACK));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                builder.dismiss();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                builder.cancel();
                builder.dismiss();
            }
        });
        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        boolean flag = true;
        if(flag) {
            bmp = decodeSampledBitmapFromResource(path, screenWidth, screenHeight);
            flag = false;
        }
        imageView.setRotation(90);

        Glide.with(this).load(bmp).into(imageView);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                bmp.recycle();
            }
        });

        builder.show();
    }
}
