package davidtomic.projekti.diplomskiprojekt;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessActivity extends AppCompatActivity {

    String category = "";
    String date = "";
    String path = "";
    int icon = 0;
    String putanja;
    float total = 0;
    List<String> detectedItems = new ArrayList<>();
    List<String> detectedCosts = new ArrayList<>();
    Bitmap bmp;
    ReceiptViewModel mReceiptViewModel;
    Map<String, String> items = new HashMap<>();
    ExifInterface exifInterface;
    List<FirebaseVisionText.Line> lines;
    CropImageView mCropImage;
    boolean flag = true;
    private Integer mImageMaxWidth;
    private Integer mImageMaxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        mReceiptViewModel = ViewModelProviders.of(this).get(ReceiptViewModel.class);
        mCropImage = findViewById(R.id.cropImageView);

        Intent intent = getIntent();
        putanja = intent.getStringExtra("path");
        bmp = BitmapFactory.decodeFile(putanja);
        try {
            exifInterface = new ExifInterface(putanja);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(orientation);
            Matrix matrix = new Matrix();
            if (orientation != 0) {
                matrix.postRotate(rotationInDegrees);
            }
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            mCropImage.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCropImage.post(new Runnable() {
            @Override
            public void run() {
                mImageMaxWidth = mCropImage.getMeasuredWidth();
                mImageMaxHeight = mCropImage.getMeasuredHeight();

                float scaleFactor =
                        Math.max(
                                (float) bmp.getWidth() / (float) mImageMaxWidth,
                                (float) bmp.getHeight() / (float) mImageMaxHeight);

                Bitmap resizedBitmap =
                        Bitmap.createScaledBitmap(
                                bmp,
                                (int) (bmp.getWidth() / scaleFactor),
                                (int) (bmp.getHeight() / scaleFactor),
                                true);

                bmp = resizedBitmap;
                mCropImage.setImageBitmap(bmp);
                runTextRecognition(bmp);
            }
        });
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private void runTextRecognition(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                    processEverything(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ProcessActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void runTextorganizator(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processItems(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                e.printStackTrace();
                                Toast.makeText(ProcessActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void processEverything(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "Error occured, no text detected", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < blocks.size(); i++) {
            lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();

                if (lines.get(j).getText().matches("[0-9]*[,][0-9]+ KM$|[***] [0-9]*[,][0-9][0-9] KM$|[***][0-9]*[,][0-9][0-9] KM$|.+KM$|.+ KM$")) {
                    String pomoc = elements.get(j).getText().replaceAll("[^,0-9]", "");
                    String trosak = pomoc.replace(",", ".");

                    try {
                        total = Float.parseFloat(trosak);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Did not detect any numbers", Toast.LENGTH_SHORT).show();
                    }
                }

                for (int k = 0; k < elements.size(); k++) {

                    if (elements.get(k).getText().matches("^[0-9][0-9][.][0-9][0-9][.][0-9][0-9][0-9][0-9][.]$")) {
                        date = elements.get(k).getText();

                    } else if (elements.get(k).getText().matches("^[0-9][0-9][.][0-9][0-9][.][0-9][0-9][.]$")) {
                        date = elements.get(k).getText();

                    } else if (elements.get(k).getText().matches("^[0-9][0-9][.][0-9][0-9][.][0-9][0-9]$")) {
                        date = elements.get(k).getText();

                    } else {
                    }

                    if (elements.get(k).getText().matches("KONZUM|KONZUM\"|\"KONZUM|\"KONZUM\"")) {
                        category = "Konzum";
                        icon = R.drawable.konzum;
                    } else if (elements.get(k).getText().matches("ELEKTROPRIVREDA|JP ELEKTROPRIVREDA | elektroprivreda")) {
                        category = "Struja";
                        icon = R.drawable.elektro;

                    } else if (elements.get(k).getText().matches("CISTOCA|cistoca|ČISTOĆA|JKP ČISTOĆA")) {
                        category = "Smece";
                        icon = R.drawable.smece;

                    } else if (elements.get(k).getText().matches("HT| HT dd. Mostar| ")) {
                        category = "Eronet";
                        icon = R.drawable.eronet;

                    } else {
                    }
                }
            }
        }
    }

    private void processItems(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "Error occured, no text detected", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < blocks.size(); i++) {
            lines = blocks.get(i).getLines();

            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                if (lines.get(j).getText().length() >= 9) {
                    detectedItems.add(lines.get(j).getText());
                }
                for (int k = 0; k < elements.size(); k++) {
                    if (elements.get(k).getText().matches(".*[,][0-9][0-9][E]$")) {
                        String pom1 = elements.get(k).getText().replace("E", "");
                        String trosak = pom1.replace(",", ".");
                        detectedCosts.add(trosak.trim());
                    }
                }
            }
        }

        for (String trosak : detectedCosts) {
            total += Float.parseFloat(trosak.toString());
        }

        for (int i = 0; i < detectedCosts.size(); i++) {
            items.put(detectedItems.get(i), detectedCosts.get(i));
        }

        path = putanja;
        if(category.equals("")) {
            Toast.makeText(this, "Could not detect Category, try again.", Toast.LENGTH_SHORT).show();

        }else if(date.equals("")){
            Toast.makeText(this, "Could not detect Date, try again.", Toast.LENGTH_SHORT).show();
        }
        else if(total==0){
            Toast.makeText(this, "Could not detect Total, try again.", Toast.LENGTH_SHORT).show();
        }
        else{
            Receipt receipt = new Receipt(category, date, icon, total, path, (HashMap<String, String>) items);
            mReceiptViewModel.insert(receipt);
        }
        Intent i = new Intent(ProcessActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.saveReceipt){
            flag = false;
            bmp = mCropImage.getCroppedImage();
            runTextorganizator(bmp);
        }
        return super.onOptionsItemSelected(item);
    }
}





