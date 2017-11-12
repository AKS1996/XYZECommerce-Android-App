package com.xyzecommerce;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class generic_download extends AppCompatActivity {

    final IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    private long downloadReference;
    final String app_name = "XYZ ECommerce App";
    DownloadManager manager;
    String SELECTED_FILE = "";

    private final List<String> CompleteFileNames = new ArrayList<>();
    private final List<String> URLs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_download);
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CompleteFileNames);
        ListView listView = (ListView) findViewById(R.id.listViewGenDownload);
        listView.setAdapter(listAdapter);

        // TODO Add on the callers side MSG, TABLE NAME
        // table must consist of LIST OF CompleteFileNames, LIST OF URL

        // Find whats to be displayed
        // Using intents
        String message, table_name;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                message = null;
                table_name = null;
            } else {
                message = extras.getString("message");
                table_name = extras.getString("table_name");
            }
        } else {
            message = (String) savedInstanceState.getSerializable("message");
            table_name = (String) savedInstanceState.getSerializable("table_name");
        }

        ((TextView) findViewById(R.id.MsgGenDownload)).setText((message + ". Long Press To Download"));
        getURLFromDB(table_name);
        listAdapter.notifyDataSetChanged();


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isOnline())
                    if (isExternalStorageWritable()) {
                        Toast.makeText(generic_download.this, "Download Started", Toast.LENGTH_SHORT).show();
                        SELECTED_FILE = CompleteFileNames.get(i);

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URLs.get(i))); // its the http://
                        request.setDescription(app_name);
                        request.setTitle(CompleteFileNames.get(i));

                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, CompleteFileNames.get(i));

                        // get download service and enqueue file
                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        downloadReference = manager.enqueue(request);
                    } else
                        Toast.makeText(generic_download.this, "Cannot write to external storage.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(generic_download.this, "Please switch on INTERNET.", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void getURLFromDB(String table_name) {

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //set filter to only when download is complete and register broadcast receiver
        registerReceiver(downloadReceiver, filter);
    }

    private void openFile() {
        File url = new File(Environment.DIRECTORY_DOWNLOADS + "/" + SELECTED_FILE);
        try {

            Uri uri = Uri.fromFile(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(generic_download.this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId)
                openFile();
        }
    };
}
