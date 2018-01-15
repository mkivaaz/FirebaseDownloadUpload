package kivaaz.com.firebasedownloadupload.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import kivaaz.com.firebasedownloadupload.R;

/**
 * Created by Muguntan on 1/15/2018.
 */

public class QrGeneratorFragment extends Fragment {
    ImageView qrImg;
    EditText qrDataET;
    Button generateBtn,storeBtn;
    Bitmap bitmap;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.qr_generator_fragment,null);

        qrImg=view.findViewById(R.id.qrImg);
        qrDataET=view.findViewById(R.id.qrDataET);
        generateBtn=view.findViewById(R.id.generateBtn);
        storeBtn=view.findViewById(R.id.storeBtn);

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qrData = qrDataET.getText().toString().trim();

                if(!qrData.isEmpty()){
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        BitMatrix matrix = writer.encode(qrData, BarcodeFormat.QR_CODE,1000,1000);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        bitmap = encoder.createBitmap(matrix);
//                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//                        BarcodeDetector detector =new BarcodeDetector.Builder(getContext())
//                                .setBarcodeFormats(Barcode.ALL_FORMATS)
//                                .build();
//                        detector.detect(frame);
//                        detector.setProcessor(new Detector.Processor<Barcode>() {
//                            @Override
//                            public void release() {
//
//                            }
//
//                            @Override
//                            public void receiveDetections(Detector.Detections<Barcode> detections) {
//
//                                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
//                                Log.d("DETECTIONS: ",qrCodes.valueAt(0).displayValue);
//                            }
//                        });

                        qrImg.setImageBitmap(bitmap);
                        qrImg.setVisibility(View.VISIBLE);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(intent,"Choose Directory"),9999);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 9999:
                String path = "/sdcard/";
                String core = data.getData().getLastPathSegment();
                Log.i("Test", "Result URI " + path+ core.substring(core.lastIndexOf(":") + 1)  + "/" );
                storeImg(bitmap, path + core.substring(core .lastIndexOf(":") + 1)  + "/" );
                break;
        }
    }

    public void storeImg(Bitmap img , String path){
        FileOutputStream out = null;
        String filename = "QRCode-" + System.currentTimeMillis();
        File file = new File(path + filename + ".png" +
                "");

        try {
            out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.PNG,100,out);
            Toast.makeText(getContext(),"File Downloaded",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("ERROR: ", e.getMessage());
        }finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
