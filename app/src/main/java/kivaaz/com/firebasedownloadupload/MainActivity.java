package kivaaz.com.firebasedownloadupload;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseR;

    private ImageView img;
    private Button Upload, File_Chooser, Download;
    private TextView imgName;

    private Uri ImgURI;

    public static final String FB_STORAGE_PATH = "images/";
    public static final String FB_DATABASE_PATH = "images";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseR = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseR.keepSynced(true);
        img = (ImageView) findViewById(R.id.iw);
        imgName = (TextView) findViewById(R.id.imageName);
        File_Chooser = (Button) findViewById(R.id.btn_File);
        Upload = (Button) findViewById(R.id.btn_Upload);
        Download = (Button) findViewById(R.id.btn_Download);


        File_Chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent,"Select Image"),REQUEST_CODE);
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImgURI != null){
                    StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(ImgURI));
                    ref.putFile(ImgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getApplicationContext(),"Image Uploaded", Toast.LENGTH_SHORT).show();
                            
                            ImageUpload imgUpload = new ImageUpload(imgName.getText().toString(),taskSnapshot.getStorage().getPath(),getImageExt(ImgURI));
                            String uploadId = mDatabaseR.push().getKey();
                            mDatabaseR.child(uploadId).setValue(imgUpload);


                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();

                }
            }
        });

        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Fileviewer.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null ){
            ImgURI = data.getData();

            File file = new File(ImgURI.toString());
            imgName.setText(String.valueOf(System.currentTimeMillis()));

            String mimeType =getImageExt(ImgURI);

            Toast.makeText(getApplicationContext(),mimeType, Toast.LENGTH_SHORT).show();
            Glide.with(getBaseContext()).load(ImgURI).into(img);
        }
    }

    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



}
