package com.example.rohitshukla.apps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LocationListener {
    CheckBox checkBox, checkBox1;
    Spinner linkSpinner, activitySpinner;
    TextView mtextView;
    Button takephto, submit, takevideo,mgetLocationBtn;
    Bitmap bitmap;
    LocationManager locationManager;
    String latitude, longitude, ad;
    int n1, n2;
    Bundle extras;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String URL = "http://52.172.141.55/divya/project1/imageapi.php";
    Map<String, String> params;
    private String selectedPath;
    static final int REQUEST_VIDEO_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mgetLocationBtn=findViewById(R.id.getLocationBtn);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        mgetLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        try{
            getLocation();
        }catch (Exception e){
            getLocation();
        }
        checkBox = findViewById(R.id.photocheck);
        checkBox1 = findViewById(R.id.videocheck);
        linkSpinner = (Spinner) findViewById(R.id.linkspinner);
        activitySpinner = (Spinner) findViewById(R.id.activityspinner);
        takephto = findViewById(R.id.photo);
        takevideo = findViewById(R.id.video);
        mtextView = findViewById(R.id.locationText);
        submit = findViewById(R.id.submit);



        Random random = new Random();

        n1 = random.nextInt(10000) + 100;


        takevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();

                Random random = new Random();

                n2 = random.nextInt(10000) + 100;
            }
        });

        takephto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

                Random random = new Random();

                n2 = random.nextInt(10000) + 100;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (n1 != n2) {
                    Log.d("kkk","  helloo    "+latitude+"      "+longitude);
                    UploadImage();
                    uploadVideo();
                    Log.d("logten", "I am here");
                    n1 = n2;
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter next Record", Toast.LENGTH_SHORT).show();
                    checkBox1.setChecked(false);
                    checkBox.setChecked(false);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            show();
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            selectedPath = getPath(videoUri);
            show();
        }
    }

    public String getStringImage(Bitmap bm) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    private void UploadImage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s = response.trim();
                Toast.makeText(MainActivity.this, "Data Uploadeded !!", Toast.LENGTH_SHORT).show();
                checkBox.setChecked(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                params = new HashMap<String, String>();

                params.put("imagename","abhishek");
                params.put("image",image);
                params.put("longitude",longitude);
                params.put("lati",latitude);
                Log.d("MyParam:", "" + params);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mtextView.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
//        this.location = location;



  //      Log.d("MyLoc", "MyLoca"+location.getLatitude()+" "+location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
           mtextView.setText(mtextView.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                   addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));

            Log.d("loggergetLatitude:",""+location.getLatitude());
            Log.d("loggergetLongitude:",""+location.getLongitude());

            Double longi = location.getLongitude();
            Double lati = location.getLatitude();

            latitude = lati.toString();
            longitude = longi.toString();

        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    private void chooseVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();
        return path;
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(MainActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);
                uploading.dismiss();
                checkBox1.setChecked(true);
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.upLoad2Server(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    public void show() {
        if (extras != null && selectedPath != null)
            submit.setVisibility(View.VISIBLE);
    }
}


