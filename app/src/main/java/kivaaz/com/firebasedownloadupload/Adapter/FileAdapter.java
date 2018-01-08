package kivaaz.com.firebasedownloadupload.Adapter;

/**
 * Created by Muguntan on 11/26/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    DatabaseHandler db;
    StorageReference riversRef;
    private OnItemClick mCallback;
    private StorageReference mStorageRef;

    public FileAdapter(List<ImageUpload> data, Context context, OnItemClick listener) {
        this.data = data;
        this.context = context;
        this.mCallback = listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public FileAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.file_adapter,null);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FileAdapter.myViewHolder holder, int position) {
        final ImageUpload img = data.get(position);
        Files file = null;
        db = new DatabaseHandler(context);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        riversRef = mStorageRef.child(img.getUrl());

        holder.ImgName.setText(img.getName() + "." + img.getType());

        file = GetFile(img.getName());

        if(file != null){// if file exists load the local uri from DB
            if(file.getUrl().equalsIgnoreCase(img.getUrl())){// if firebase url & DB url matches
                final File localFile = new File(file.getLocal_url());
                if(localFile.exists()){
                    Glide.with(context).load(file.getLocal_url()).into(holder.Thumb);
                }else{
                    UpdateFiles(img,context,holder.Thumb);
                }
            }else{// if firebase url & DB url doesn't match
                UpdateFiles(img, context, holder.Thumb);
            }
        }else{// if file Doesn't exists download the file from uri, stores locally and stores the local uri in DB
            AddFiles(img, context, holder.Thumb);
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

        public myViewHolder(View itemView) {
            super(itemView);
            ImgName = itemView.findViewById(R.id.imgName);
            Thumb = itemView.findViewById(R.id.ad_thumb);
            fileFrame = itemView.findViewById(R.id.file_frame);
            fileFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    String videoName = data.get(position).getName();
                    Files file = GetFile(videoName);

                    mCallback.onClick(file.getLocal_url());

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

    private void AddFiles(final ImageUpload img, final Context context, final ImageView thumb) {
        try {
            final File localFile = File.createTempFile("file", img.getType());
            riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    db.addFilesIf(new Files(img.getName(),img.getUrl(),localFile.getAbsolutePath(),img.getType(),"True"));
                    Glide.with(context).load(localFile.getAbsolutePath()).into(thumb);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateFiles(final ImageUpload img, final Context context, final ImageView thumb){
        try {
            final File localFile = File.createTempFile("file", img.getType());
            riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    db.updateFiles(new Files(img.getName(),img.getUrl(),localFile.getAbsolutePath(),img.getType(),"True"));
                    Glide.with(context).load(localFile.getAbsolutePath()).into(thumb);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
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
        void onClick (String value);
    }

}
