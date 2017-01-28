package com.denovo.denovo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;



public class DonateSummaryFragment extends Fragment {

    public static final String TAG = "DonateSummaryFragment";

    private DonateActivity mActivity;
    private DatabaseReference mDatabase;
    private ImageView summaryItemPhoto;
    private TextView summaryItemName;
    private TextView summaryItemYardSale;
    private TextView summaryItemDescription;
    private RatingBar summaryRatingBar;
    private TextView summaryItemPrice;
    private String mItemPhotoPath;
    private String mItemName;
    private String mItemYardSale;
    private String mItemDescription;
    private int mItemRating;
    private double mItemPrice;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public DonateSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_donate_summary, container, false);
        mActivity = (DonateActivity) getActivity();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://denovo-4024e.appspot.com");

        summaryItemPhoto = (ImageView) rootView.findViewById(R.id.summary_item_photo);
        summaryItemName = (TextView) rootView.findViewById(R.id.summary_item_name);
        summaryItemYardSale = (TextView) rootView.findViewById(R.id.summary_item_yard_sale);
        summaryItemDescription = (TextView) rootView.findViewById(R.id.summary_description);
        summaryRatingBar = (RatingBar) rootView.findViewById(R.id.summary_item_rating);
        summaryItemPrice = (TextView) rootView.findViewById(R.id.summary_item_price);

        final Button submitButton = (Button) rootView.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButton.setEnabled(false);
                DatabaseReference childRef = mDatabase.child("items").push();
                String fileName = childRef.getKey() + ".jpg";
                Item item = new Item(mItemName, fileName, mItemYardSale, "Abhinav Khushalani",
                        mItemPrice, mItemRating, mItemDescription, new ArrayList<ItemComment>(), new
                        ArrayList<String>());
                childRef.setValue(item);
                final File file = new File(mItemPhotoPath);
                Uri fileUri = Uri.fromFile(file);
                StorageReference imageRef = storageRef.child("images/" + fileName);
                UploadTask uploadTask = imageRef.putFile(fileUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        file.delete();
                        mActivity.finish();
                    }
                });
            }
        });

        return rootView;
    }

    public String formatPrice(double price) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(price);
    }

    private void setPic(int targetW, String photoPath) {
        int targetH = targetW;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        summaryItemPhoto.setImageBitmap(bitmap);
    }

    public void populateView(int targetW, String itemPhotoPath, String itemName, String
            itemYardSale, String itemDescription, int itemRating, double itemPrice) {
        mItemPhotoPath = itemPhotoPath;
        mItemName = itemName;
        mItemYardSale = itemYardSale;
        mItemDescription = itemDescription;
        mItemRating = itemRating;
        mItemPrice = itemPrice;

        setPic(targetW, itemPhotoPath);
        summaryItemName.setText(itemName);
        summaryItemYardSale.setText(itemYardSale);
        summaryItemDescription.setText(itemDescription);
        summaryRatingBar.setRating(itemRating);
        summaryItemPrice.setText(formatPrice(itemPrice));
    }
}
