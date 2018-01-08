package kivaaz.com.firebasedownloadupload.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import kivaaz.com.firebasedownloadupload.ImageUpload;
import kivaaz.com.firebasedownloadupload.R;

import static android.app.Activity.RESULT_OK;

public class UploadFragment extends Fragment {

    private static final int REQUEST_CODE = 1234;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseR;

    private ImageView img;
    private Button Upload, File_Chooser, Download;
    private TextView imgName;

    private Uri ImgURI;

    public static final String FB_STORAGE_PATH = "images/";
    public static final String FB_DATABASE_PATH = "images";
    String mimeType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upload_fragment, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseR = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseR.keepSynced(true);
        img = (ImageView) view.findViewById(R.id.iw);
        imgName = (TextView) view.findViewById(R.id.imageName);
        File_Chooser = (Button) view.findViewById(R.id.btn_File);
        Upload = (Button) view.findViewById(R.id.btn_Upload);


        File_Chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent,"Select Image"),REQUEST_CODE);
                Toast.makeText(getContext(),mimeType, Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(getContext(),"Image Uploaded", Toast.LENGTH_SHORT).show();

                            ImageUpload imgUpload = new ImageUpload(imgName.getText().toString(),taskSnapshot.getStorage().getPath(),getImageExt(ImgURI));
                            String uploadId = mDatabaseR.push().getKey();
                            mDatabaseR.child(uploadId).setValue(imgUpload);


                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    Toast.makeText(getContext(),"Please Select Image", Toast.LENGTH_SHORT).show();

                }
            }
        });


        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null ){
            ImgURI = data.getData();
            imgName.setText(ImgURI.getLastPathSegment().substring(ImgURI.getLastPathSegment().lastIndexOf("/")+1, ImgURI.getLastPathSegment().lastIndexOf(".")));

            mimeType = getImageExt(ImgURI);


            Glide.with(getContext()).load(ImgURI).into(img);
        }
    }

    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



}
