package kivaaz.com.firebasedownloadupload.Fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kivaaz.com.firebasedownloadupload.Adapter.FileAdapter;
import kivaaz.com.firebasedownloadupload.Adapter.Files;
import kivaaz.com.firebasedownloadupload.DB.DatabaseHandler;
import kivaaz.com.firebasedownloadupload.ImageUpload;
import kivaaz.com.firebasedownloadupload.R;

public class GalleryFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<ImageUpload> imgList;

    RecyclerView FileRecycle;
    FileAdapter adapter;

    VideoView video;
    ImageView img;

    TextView nofiles;

    DatabaseHandler db;
    public static final String FB_DATABASE_PATH = "images";
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private StorageReference mStorageRef;
    StorageReference riversRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fileviewer_fragment, container, false);
        db = new DatabaseHandler(getContext());
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        final String userEmail ="/" + currentUser.getEmail().replace(".","");

        FileRecycle = view.findViewById(R.id.file_recycle);
        video = view.findViewById(R.id.video_load);
        img = view.findViewById(R.id.img_load);
        nofiles = view.findViewById(R.id.noFiles);

        final DatabaseHandler db = new DatabaseHandler(getContext());
        myRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH + userEmail);

        myRef.keepSynced(true);
        imgList = new ArrayList<>();
        video.setVisibility(View.GONE);
        img.setVisibility(View.GONE);
        final List<Files> filesList = db.getAllFiles();

        if(filesList.size() == 0 || db.getCurrentFilesCount(currentUser.getEmail()) == 0){
            nofiles.setVisibility(View.VISIBLE);
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {



                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageUpload img = snapshot.getValue(ImageUpload.class);

                    imgList.add(img);

                }
                int fileNum = 0;
                for (ImageUpload img : imgList) {
                    Files file = GetFile(img.getName());

                    if(file != null){// if file exists load the local uri from DB
                        if(file.getUserEmail().equals(userEmail)){

                            fileNum = fileNum + 1;
                            if(file.getUrl().equalsIgnoreCase(img.getUrl())){// if firebase url & DB url matches
                                final File localFile = new File(file.getLocal_url());
                                if(localFile.exists()){
                                }else{
                                    UpdateFiles(img);
                                }
                            }else{// if firebase url & DB url doesn't match
                                UpdateFiles(img);
                            }
                        }
                    }else{// if file Doesn't exists download the file from uri, stores locally and stores the local uri in DB
                        fileNum = fileNum + 1;
                        AddFiles(img);
                    }
                }

                    // setting callback for Recyclerview
                    if(getContext() != null){
                        adapter = new FileAdapter(imgList, getContext(), currentUser.getEmail(),new FileAdapter.OnItemClick() {
                            @Override
                            public void onClick(String path, String type) {
                                playMedia(path, type, video, img);
                            }
                        });
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
                        FileRecycle.setAnimation(null);
                        FileRecycle.setAdapter(adapter);
                        FileRecycle.setLayoutManager(layoutManager);

                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void playMedia(String path, String type, VideoView video, ImageView img) {
        if(type.equals("jpg") || type.equals("png")){
            Glide.with(getContext()).load(path).into(img);
            img.setVisibility(View.VISIBLE);
            video.setVisibility(View.GONE);
        }else if(type.equals("3gp") || type.equals("mp4") || type.equals("webm") || type.equals("mkv")) {
            MediaController mediaController = new MediaController(video.getContext(),true);
            mediaController.setAnchorView(video);

            video.setVideoURI(Uri.parse(path));
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            video.start();
            video.setMediaController(mediaController);
            img.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
        }
    }

    public Boolean CheckDB(String filename){
        if (db.getFilesCount() > 0 && db.isExists(filename)){ // Check if DB is Empty & if file exists
            return true;
        }
        else {
            return false;
        }
    }

    private void AddFiles(final ImageUpload img) {
        try {
            riversRef = mStorageRef.child(img.getUrl());
            final File localFile = File.createTempFile("file", img.getType());
            riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    db.addFilesIf(new Files(img.getName(),img.getUrl(),localFile.getAbsolutePath(),img.getType(),"True",currentUser.getEmail()));
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateFiles(final ImageUpload img){
        riversRef = mStorageRef.child(img.getUrl());
        try {
            final File localFile = File.createTempFile("file", img.getType());
            riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    db.updateFiles(new Files(img.getName(),img.getUrl(),localFile.getAbsolutePath(),img.getType(),"True", currentUser.getEmail()));
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Files GetFile(String Filename){
        if(CheckDB(Filename) == true){
            Files file = db.getFiles(Filename);
            return file;
        }else{
            return null;
        }
    }


}
