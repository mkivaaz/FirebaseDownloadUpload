package kivaaz.com.firebasedownloadupload.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kivaaz.com.firebasedownloadupload.Adapter.FileAdapter;
import kivaaz.com.firebasedownloadupload.Adapter.Files;
import kivaaz.com.firebasedownloadupload.DB.DatabaseHandler;
import kivaaz.com.firebasedownloadupload.ImageUpload;
import kivaaz.com.firebasedownloadupload.R;

public class FileviewerFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<ImageUpload> imgList;

    RecyclerView FileRecycle;
    FileAdapter adapter;

    VideoView video;

    public static final String FB_DATABASE_PATH = "images";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fileviewer_fragment, container, false);

        FileRecycle = view.findViewById(R.id.file_recycle);
        video = view.findViewById(R.id.video_load);
        final DatabaseHandler db = new DatabaseHandler(getContext());
        myRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        myRef.keepSynced(true);
        imgList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageUpload img = snapshot.getValue(ImageUpload.class);
                    Files file = null;

                    imgList.add(img);
                }

                // setting callback for Recyclerview
                adapter = new FileAdapter(imgList, getContext(), new FileAdapter.OnItemClick() {
                    @Override
                    public void onClick(String value) {

                        video.setVideoURI(Uri.parse(value));
                        video.start();
                    }
                });
                FileRecycle.setAdapter(adapter);
                FileRecycle.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }





}
