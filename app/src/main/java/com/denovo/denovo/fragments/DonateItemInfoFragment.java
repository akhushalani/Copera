package com.denovo.denovo.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denovo.denovo.R;
import com.denovo.denovo.activities.DonateActivity;
import com.denovo.denovo.activities.SearchableActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class DonateItemInfoFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;

    private View mImageSelector;
    private ImageView mImageThumbnail;
    private ImageView mImageThumbnailFrame;
    private EditText itemNameEditText;
    private TextView itemChapterTextView;
    private EditText itemDescriptionEditText;
    private String mCurrentPhotoPath;
    private String mItemName;
    private String mItemChapter;
    private String mItemDescription;
    private boolean mHasImage;
    private boolean mHasName;
    private boolean mHasDescription;
    private Uri photoUri;
    private DonateActivity mActivity;

    boolean mFieldsFilled = false;

    OnInfoSubmittedListener mCallback;

    public DonateItemInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donate_item_info, container,
                false);

        mActivity = (DonateActivity) getActivity();

        mImageSelector = rootView.findViewById(R.id.item_thumbnail);
        mImageThumbnail = (ImageView) rootView.findViewById(R.id.thumbnail);
        mImageThumbnailFrame = (ImageView) rootView.findViewById(R.id.thumbnail_frame);
        mImageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //use standard intent to capture an image
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //we will handle the returned data in onActivityResult
                    startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException anfe)  {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        itemNameEditText = (EditText) rootView.findViewById(R.id.item_name_edit_text);
        itemNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHasName = s.toString().trim().length() != 0;
                if (mHasImage && mHasName && mHasDescription) {
                    onFieldsFilled();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mItemName = s.toString();
            }
        });

        itemChapterTextView = (TextView) rootView.findViewById(R.id.item_yard_sale_edit_text);
        mItemChapter = itemChapterTextView.getText().toString();

        itemChapterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchableActivity.class);
                startActivity(intent);
            }
        });


        itemDescriptionEditText = (EditText) rootView.findViewById(R.id.item_description_edit_text);
        itemDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHasDescription = s.toString().trim().length() != 0;
                if (mHasImage && mHasName && mHasDescription) {
                    onFieldsFilled();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mItemDescription = s.toString();
            }
        });

        Button confirmInfoButton = (Button) rootView.findViewById(R.id.btn_confirm_info);
        confirmInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onInfoSubmitted(mCurrentPhotoPath, mItemName, mItemChapter, mItemDescription);
            }
        });

        return rootView;
    }

    public void onFieldsFilled() {
        mFieldsFilled = true;
        Log.v("DonateItemInfoFragment", mItemName);
        DonateActivity activity = (DonateActivity) getActivity();
        activity.enableButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                photoUri = data.getData();
                performCrop();
            } else if(requestCode == REQUEST_IMAGE_CROP) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImageThumbnailFrame.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.selected_image_thumbnail_frame));
                mImageThumbnail.setImageBitmap(imageBitmap);
                float sizeInDp = 7.0f;
                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (sizeInDp * scale + 0.5f);
                mImageThumbnail.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                mHasImage = true;
                if (mHasImage && mHasName && mHasDescription) {
                    onFieldsFilled();
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnInfoSubmittedListener) context;
            Log.v("DonateItemInfoFragment", "Callback initialized");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnInfoSubmittedListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Your callback initialization here
        try {
            mCallback = (OnInfoSubmittedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnInfoSubmittedListener");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void performCrop(){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(photoUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            File f = null;
            // Here I initialize empty file
            try{
                // This returns the file created
                f = createImageFile();
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                cropIntent.putExtra("output", Uri.fromFile(f));
            }
            catch(IOException e){
                e.printStackTrace();
            }
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void getItemInfo() {
        mActivity.mItemPhotoPath = mCurrentPhotoPath;
        mActivity.mItemName = mItemName;
        mActivity.mItemYardSale = mItemChapter;
        mActivity.mItemDescription = mItemDescription;
    }



    public interface OnInfoSubmittedListener {
        void onInfoSubmitted(String photoPath, String name, String yardSale, String
                description);
    }
}
