package com.example.powerpuffgirls;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.*;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutYouActivity extends AppCompatActivity {

    private String TAG = "AboutYouActivity";
    private static final int RESULT_LOAD_IMAGE = 1;
    //private CheckBox sCaucasian, sAsian, sHispanic, sBlack, sMiddleEastern;
    FirebaseFirestore fstore;
    private FirebaseAuth fAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_you);
        fstore=FirebaseFirestore.getInstance();

        Button uploadImage = findViewById(R.id.uploadImageButton);
        uploadImage.setOnClickListener(UploadImageView -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });
        FloatingActionButton toEducationInfo= (FloatingActionButton) findViewById(R.id.buttonToEducationInfo);
        toEducationInfo.setOnClickListener(EducationInfoView-> openEducationInfoActivity());


    }

    public void openEducationInfoActivity(){
        CheckBox caucasian= findViewById(R.id.caucasian);
        CheckBox asian=  findViewById(R.id.asian);
        CheckBox hispanic=  findViewById(R.id.hispanic);
        CheckBox black=  findViewById(R.id.black);
        CheckBox middleEastern=  findViewById(R.id.middleeastern);
        CheckBox otherEthnicity= findViewById(R.id.otherEthnicity);
        List ethnicities=new ArrayList<>();
        RadioGroup sGenderOptions = (RadioGroup) findViewById(R.id.genderOptions);
        RadioGroup sEthnicityOptions=(RadioGroup) findViewById(R.id.ethnicityOptions);
        int selectedGender = sGenderOptions.getCheckedRadioButtonId();
        if (caucasian.isChecked()){
            String ethnicity=caucasian.getText().toString().trim();
            ethnicities.add(ethnicity);
        } if (asian.isChecked()){
            String ethnicity=asian.getText().toString().trim();
            ethnicities.add(ethnicity);
        } if (hispanic.isChecked()){
            String ethnicity=hispanic.getText().toString().trim();
            ethnicities.add(ethnicity);
        } if (black.isChecked()){
            String ethnicity=black.getText().toString().trim();
            ethnicities.add(ethnicity);
        } if (middleEastern.isChecked()){
            String ethnicity=middleEastern.getText().toString().trim();
            ethnicities.add(ethnicity);
        } if (otherEthnicity.isChecked()){
            String ethnicity=otherEthnicity.getText().toString().trim();
            ethnicities.add(ethnicity);
        }



        // find the radiobutton by returned id
        RadioButton genderButton = findViewById(selectedGender);
        try{
            String gender= genderButton.getText().toString().trim();

            SignUpActivity a= new SignUpActivity();
            HashMap<String, String> map = a.getHashmap();
            map.put("gender",gender);
            map.put("ethnicity",ethnicities.toString());
            //CheckBox ethnicityCheckbox=(CheckBox)findViewById(selectedEthnicity);
            //String ethnicity =ethnicityCheckbox.getText().toString().trim();
            //map.put("ethnicity",ethnicity);

            Intent educationInfoIntent = new Intent(this, EducationInfoActivity.class);
            startActivity(educationInfoIntent);
        }catch(Exception e) {
            Log.d(TAG, String.valueOf(e));
            Toast.makeText(getApplicationContext(), "Please ensure all fields are filled out", Toast.LENGTH_LONG).show();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ImageView imgView = findViewById(R.id.profilePicture);

                Picasso.get().load(selectedImage).into(imgView);
                ProfileImage pi = new ProfileImage();
                pi.setImgUri(selectedImage);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // String picturePath contains the path of selected Image
            ImageView imageView = (ImageView) findViewById(R.id.profilePicture);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            ProfileImage pi = new ProfileImage();
            pi.setImgbytes(getByteArray(imageView));
        }
    }

    // https://stackoverflow.com/questions/48110832/android-send-imageview-image-to-firebase-with-authentication
    public byte[] getByteArray(ImageView imageView){
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }
}