package com.example.anjaline.facebookgooglesignin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anjaline.facebookgooglesignin.AdapterClasses.CustomUserAdapter;
import com.example.anjaline.facebookgooglesignin.AdapterClasses.NewCustomAdapterClass;
import com.example.anjaline.facebookgooglesignin.PojoClasses.UserData;
import com.example.anjaline.facebookgooglesignin.PojoClasses.UserScannedData;
import com.facebook.AccessToken;
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

import java.util.ArrayList;

/**
 * Created by anjaline on 7/6/17.
 */

public class Activity_Scanner extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    TextView scan, textdata;
    EditText typeinfo;
    Button btn_scan, btn_test;
    ImageView image;
    String textQR;
    String data;
    ListView listViewforScanning,scannList;
    String profile1 = null;
    String profile2 = null;
    String profile3 = null;
    String add;

    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scan);

        scan = (TextView) findViewById(R.id.text);
        btn_scan = (Button) findViewById(R.id.btnscan);
        //btn_test = (Button) findViewById(R.id.btn_test);
        listViewforScanning = (ListView) findViewById(R.id.listView_one);
       scannList=(ListView)findViewById(R.id.scan_item);
        insertUserDataIntoDB();
        HandleDatabase handleDatabase = new HandleDatabase(Activity_Scanner.this);
        ArrayList<UserData> arrayList = handleDatabase.getAllUserData();
        setUserDataInList(arrayList);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        TextView textView = (TextView) findViewById(R.id.profile_data);
        if (bundle != null) {
            profile1 = bundle.getString("user_id");
            profile2 = bundle.getString("user_name");
            profile3 = bundle.getString("user_email");
        }
        textView.setText("Profile details: " + profile1 + "\n" + profile2 + "\n" + profile3);
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
        ArrayList<UserScannedData> userScannedDataArrayList = handleDatabase.getScannedData();
        setScannedDataInList(userScannedDataArrayList);
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
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(accessToken != null){
                    LoginManager.getInstance().logOut();
                }
                Intent intent = new Intent(Activity_Scanner.this, MainActivitySignInAll.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Toast.makeText(Activity_Scanner.this, "logout successfully", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(intent);
            }
        });

        findViewById(R.id.button_scannr_google_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (mGoogleApiClient.isConnected())
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                Toast.makeText(Activity_Scanner.this, "logout successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Activity_Scanner.this, MainActivitySignInAll.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                            }
                        });
            }
        }
        );
        findViewById(R.id.btn_scanner_linkedin_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
                //LISession session = sessionManager.getSession();
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.cancelCalls(Activity_Scanner.this);
                Toast.makeText(Activity_Scanner.this, "logout successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Activity_Scanner.this, MainActivitySignInAll.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Or Not");
        builder.setMessage("Do you want to exit? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               Activity_Scanner.super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
               //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
             //Activity_Scanner.super.onBackPressed();
               // finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            String scanContent = result.getContents();
            if (scanContent != null) {
                Toast.makeText(Activity_Scanner.this,scanContent, Toast.LENGTH_SHORT).show();
                insertScannedDataIntoDB(scanContent);
                HandleDatabase handleDatabase = new HandleDatabase(Activity_Scanner.this);

                ArrayList<UserScannedData> userScannedDataArrayList = handleDatabase.getScannedData();
                setScannedDataInList(userScannedDataArrayList);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * This function will be called to save the user values into db when the user logs in
     *
     */

    public void insertUserDataIntoDB(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String pref_id = preferences.getString("id", null);
        final String _name = preferences.getString("Name", null);
        final String _email = preferences.getString("Email", null);
        HandleDatabase handleDatabase = new HandleDatabase(Activity_Scanner.this);
        handleDatabase.addTable(pref_id, _name, _email);
    }

    /**
     * This function will be called to show the user details in listview (on click of test btn)
     * to show the user details in list view first fetch the details from db and then pass them to adapter and then use
     * the adapter to show data in list
     */
    public void setUserDataInList(final ArrayList<UserData> userDataArrayList){

        CustomUserAdapter customUserAdapter = new CustomUserAdapter(Activity_Scanner.this,userDataArrayList);
        listViewforScanning.setAdapter(customUserAdapter);
        listViewforScanning.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HandleDatabase handleDatabase = new HandleDatabase(Activity_Scanner.this);
                ArrayList<UserData> arrayList = handleDatabase.getAllUserData();
                StringBuilder sb = new StringBuilder();
                sb.append("Id : " + arrayList.get(0).getUser_id());
                sb.append("\n");
                sb.append("Name : " + arrayList.get(0).getUser_name());
                sb.append("\n");
                sb.append("Email : " + arrayList.get(0).getUser_email());
                Toast.makeText(Activity_Scanner.this, sb.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    public void insertScannedDataIntoDB(String s_can) {
        HandleDatabase handleDatabase = new HandleDatabase(Activity_Scanner.this);
        handleDatabase.addTableTwo(s_can);
    }
    public void setScannedDataInList(final ArrayList<UserScannedData> userScannedDataArrayList){
        NewCustomAdapterClass new_customUserAdapter = new NewCustomAdapterClass(Activity_Scanner.this,userScannedDataArrayList);
        scannList.setAdapter(new_customUserAdapter);
        scannList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HandleDatabase handleDatabase = new HandleDatabase(Activity_Scanner.this);
                ArrayList<UserScannedData> scanList = handleDatabase.getScannedData();
                StringBuilder sb1 = new StringBuilder();
                sb1.append("data: " + scanList.get(1).getScan_data());
                Toast.makeText(Activity_Scanner.this, sb1.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
