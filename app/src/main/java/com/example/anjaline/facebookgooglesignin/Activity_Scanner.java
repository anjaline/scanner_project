package com.example.anjaline.facebookgooglesignin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;

/**
 * Created by anjaline on 7/6/17.
 */

public class Activity_Scanner extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView scan, textdata;
    EditText typeinfo;
    Button btn_scan;
    ImageView image;
    String textQR;
    ListView listViewforScanning;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scan);
        scan = (TextView) findViewById(R.id.text);
        btn_scan = (Button) findViewById(R.id.btnscan);

        listViewforScanning=(ListView)findViewById(R.id.Scannerclasss_listing);
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_main_scan, R.id.text_list_view, values);
        listViewforScanning.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        TextView textView = (TextView) findViewById(R.id.profile_data);
        String profile = null;
        if (bundle != null) {
            profile = bundle.getString("intent_key");
        }
        textView.setText("Profile details: " + profile);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Activity_Scanner.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.btnLogoutFb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                finish();
            }
        });

        findViewById(R.id.button_scannr_google_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Toast.makeText(Activity_Scanner.this, "loggedout successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });


            }
        });
        findViewById(R.id.btn_scanner_linkedin_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
                LISession session = sessionManager.getSession();
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.cancelCalls(Activity_Scanner.this);
                Toast.makeText(Activity_Scanner.this, "loggedout successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object and put them in your textview

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the values you need from your textview into "outState"-object
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String scanContent = result.getContents();
            if (scanContent != null) {
                textdata.setText("ScanContent " + scanContent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
