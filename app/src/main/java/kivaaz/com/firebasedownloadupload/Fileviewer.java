package kivaaz.com.firebasedownloadupload;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fileviewer extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<ImageUpload> imgList;

    RecyclerView FileRecycle;
    FileAdapter adapter;

    VideoView video;

    public static final String FB_STORAGE_PATH = "images/";
    public static final String FB_DATABASE_PATH = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileviewer);

        FileRecycle = findViewById(R.id.file_recycle);
        video = findViewById(R.id.video_load);
        final DatabaseHandler db = new DatabaseHandler(this);
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
                adapter = new FileAdapter(imgList, getBaseContext(), new FileAdapter.OnItemClick() {
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



    }


}
