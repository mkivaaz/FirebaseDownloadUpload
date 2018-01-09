package kivaaz.com.firebasedownloadupload.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upload_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String userEmail = "/" + currentUser.getEmail().replace(".","");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseR = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH + userEmail);
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
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Uploading File...");
                progressDialog.show();

                if (ImgURI != null){
                    StorageReference ref = mStorageRef.child(FB_STORAGE_PATH +  System.currentTimeMillis() + "." + getImageExt(ImgURI));
                    ref.putFile(ImgURI).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageUpload imgUpload = new ImageUpload(String.valueOf(System.currentTimeMillis()),taskSnapshot.getStorage().getPath(),getImageExt(ImgURI));
                            String uploadId = mDatabaseR.push().getKey();
                            mDatabaseR.child(uploadId).setValue(imgUpload);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload Complete", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
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
            try{
            imgName.setText(ImgURI.getLastPathSegment().substring(ImgURI.getLastPathSegment().lastIndexOf("/")+1, ImgURI.getLastPathSegment().lastIndexOf(".")).replace("-",""));
            }catch (StringIndexOutOfBoundsException e){
                Log.d("Exception", e.getMessage());
            }
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
