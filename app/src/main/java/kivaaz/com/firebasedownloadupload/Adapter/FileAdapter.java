package kivaaz.com.firebasedownloadupload.Adapter;

/**
 * Created by Muguntan on 11/26/2017.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kivaaz.com.firebasedownloadupload.DB.DatabaseHandler;
import kivaaz.com.firebasedownloadupload.ImageUpload;
import kivaaz.com.firebasedownloadupload.R;

/**
 * Created by Muguntan on 11/17/2017.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.myViewHolder>{
    private LayoutInflater inflater;
    List<ImageUpload> data = new ArrayList<>();
    Context context;
    String userEmail;
    StorageReference riversRef;
    DatabaseHandler db;
    private OnItemClick mCallback;
    private DeleteOnItemClick dCallback;
    private FirebaseAuth mAuth;

    public FileAdapter(List<ImageUpload> data, Context context,String userEmail, OnItemClick listener, DeleteOnItemClick dCallback) {
        this.data = data;
        this.context = context;
        this.mCallback = listener;
        this.dCallback = dCallback;
        this.userEmail = userEmail;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public FileAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.file_adapter,null);
        myViewHolder holder = new myViewHolder(view);
        db = new DatabaseHandler(context);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FileAdapter.myViewHolder holder, int position) {
        final ImageUpload img = data.get(position);
        Files file = GetFile(img.getName());
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if(file != null ){
            holder.ImgName.setText(file.getName() + "." + file.getType());
            Glide.with(context).load(file.getLocal_url()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.Thumb);
        }
        else {
            holder.ImgName.setText(img.getName() + "." + img.getType());
            try {
                riversRef = mStorageRef.child(img.getUrl());
                final File localFile = File.createTempFile("file", img.getType());
                riversRef.getFile(localFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    }
                }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Glide.with(context).load(localFile.getAbsolutePath()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                               holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(holder.Thumb);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView Thumb;
        TextView ImgName;
        FrameLayout fileFrame;
        LinearLayout progressBar;
        ImageView deletebtn;
        AVLoadingIndicatorView progress;

        public myViewHolder(View itemView) {
            super(itemView);
            ImgName = itemView.findViewById(R.id.imgName);
            Thumb = itemView.findViewById(R.id.ad_thumb);
            fileFrame = itemView.findViewById(R.id.file_frame);
            progressBar = itemView.findViewById(R.id.progress);
            progress = itemView.findViewById(R.id.progressBar);
            deletebtn = itemView.findViewById(R.id.delBtn);
            progressBar.setVisibility(View.VISIBLE);

            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dCallback.onClick(data.get(getLayoutPosition()));
                }
            });

            fileFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    String videoName = data.get(position).getName();
                    Files file = GetFile(videoName);

                    mCallback.onClick(file.getLocal_url(),file.getType());

                }
            });
        }
    }

    public Boolean CheckDB(String filename){
        db = new DatabaseHandler(context);
        if (db.getFilesCount() > 0 && db.isExists(filename)){ // Check if DB is Empty & if file exists
            return true;
        }
        else {
            return false;
        }
    }

    public Files GetFile(String Filename){
        db = new DatabaseHandler(context);
        if(CheckDB(Filename)){
            Files file = db.getFiles(Filename);
            return file;
        }else{
            return null;
        }
    }

    public interface OnItemClick {// Callback Interface
        void onClick(String value, String type);
    }
    public interface DeleteOnItemClick {// Callback Interface
        void onClick(ImageUpload img);
    }

}
