package davidtomic.projekti.diplomskiprojekt;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    File photoFile = null;

    ImageButton cameraBtn;
    TextView totalTextView;
    RecyclerView recyclerView;
    private ReceiptViewModel mReceiptViewModel;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    ReceiptAdapter adapter;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalTextView = findViewById(R.id.total_text_view);
        cameraBtn = findViewById(R.id.cameraBtn);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

         adapter = new ReceiptAdapter();

         adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
             @Override
             public void onChanged() {
                 float total = 0;
                 for(int i=0;i<adapter.getItemCount();i++){
                     total = total + adapter.getReceiptAt(i).getTotal();
                 }
                 String totalFormated = String.format("%.02f", total);
                 totalTextView.setText("Ukupno: " + totalFormated + " KM");
                 super.onChanged();
             }
         });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        mReceiptViewModel = ViewModelProviders.of(this).get(ReceiptViewModel.class);
        mReceiptViewModel.getAllReceipts().observe(this, new Observer<List<Receipt>>() {
            @Override
            public void onChanged(@Nullable List<Receipt> receipts) {
                adapter.setReceipts(receipts);

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Delete Receipt")
                        .setMessage("Are you sure you want to delete this receipt?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(adapter.getReceiptAt(viewHolder.getAdapterPosition()).getImagePath());
                                boolean isSuccess = file.delete();
                                mReceiptViewModel.delete(adapter.getReceiptAt(viewHolder.getAdapterPosition()));
                                mReceiptViewModel.getAllReceipts().getValue().remove(viewHolder.getAdapterPosition());
                                adapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new ReceiptAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Receipt receipt) {
                Intent intent = new Intent(MainActivity.this, ReceiptDetail.class);
                intent.putExtra("id", receipt.getId());
                intent.putExtra("category", receipt.getCategory());
                intent.putExtra("date", receipt.getDate());
                intent.putExtra("total", receipt.getTotal());
                intent.putExtra("path", receipt.getImagePath());
                intent.putExtra("icon", receipt.getIcon());
                intent.putExtra("items", receipt.getItems());

                startActivity(intent);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    captureImage();
            }
        });
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    photoFile = createImageFile();

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "davidtomic.projekti.diplomskiprojekt.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    displayMessage(getBaseContext(), ex.getMessage());
                    ex.printStackTrace();
                }

            } else {
                displayMessage(getBaseContext(), "Error occured, couldn't find file");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                adapter.getFilter().filter(text);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {

            Intent i = new Intent(MainActivity.this, ProcessActivity.class);
            i.putExtra("path", photoFile.getAbsolutePath());
            startActivity(i);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                displayMessage(getBaseContext(), "Unable to create directory.");
                return null;
            }
        }
        File image = new File(storageDir, timeStamp + ".jpg");

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
    }
}
