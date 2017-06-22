package com.example.anjaline.facebookgooglesignin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.TokenData;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static android.R.attr.borderlessButtonStyle;
import static android.R.attr.key;

public class MainActivitySignInAll extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    ListView listView_Scanning;
    SharedPreferences sharedpreferences;
    private LoginButton facebookloginbutton;
    private CallbackManager callbackManager;
    private static final String TAG = MainActivitySignInAll.class.getSimpleName();
    private static final int RC_SIGN_IN_GOOGLE = 007;
    private static final int RC_SIGN_IN_Linkedin = 006;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnGoogleSignIn;
    private Button linkedIn_Loginbutton;
    private String linkedIn_Hashkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activitysigninall);

        callbackManager = CallbackManager.Factory.create();
        facebookloginbutton = (LoginButton) findViewById(R.id.facebook_login_button);
        linkedIn_Loginbutton = (Button) findViewById(R.id.login_li_button);
        btnGoogleSignIn = (SignInButton) findViewById(R.id.googlebtn_sign_in);
        facebookloginbutton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        facebookloginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLogin();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = v.getId();

                switch (id) {
                    case R.id.googlebtn_sign_in:
                        signIn();
                        break;
                }
            }
        });

        linkedIn_Loginbutton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (v.getId() == R.id.login_li_button) {
                                                            inLinkedinlogin();
                                                        } else {
                                                            onLinkedinLogout();
                                                        }
                                                    }
                                                }
        );

        linkedIn_Hashkey = generate_Hashkey();
        Log.d("hashKey", linkedIn_Hashkey);
        if (isFacebookLoggedIn()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String id = preferences.getString("id", null);
            final String username = preferences.getString("Name", null);
            final String email = preferences.getString("Email", null);
            startNewActivity("\nId:"+id,"\nName:"+username,"\nEmail:"+email);
        }

        if (isLogin_LinkdIn()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String id = preferences.getString("id", null);
            final String username = preferences.getString("Name", null);
            final String email = preferences.getString("Email", null);

            startNewActivity("\nId:"+id,"\nName:"+username,"\nEmail:"+email);
        }
        if (isSignIn_Google()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String id = preferences.getString("id", null);
            final String username = preferences.getString("Name", null);
            final String email = preferences.getString("Email", null);
            startNewActivity("\nId:"+id,"\nName:"+username,"\nEmail:"+email);
        }
    }

    public boolean isFacebookLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public boolean isSignIn_Google() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d("TAG", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
            return true;
        }
        return false;
    }

    private boolean isLogin_LinkdIn() {
        LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
        LISession session = sessionManager.getSession();
        boolean accessTokenValid = session.isValid();
        return accessTokenValid;
    }

    private void fbLogin() {
        Log.v("LoginActivity", "FB login called");
        facebookloginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("LoginActivity", loginResult.getAccessToken().getUserId());
                Profile profile = Profile.getCurrentProfile();
                String f_name = null;
                String facebook_id = null;
                String m_name = null;
                if (profile != null) {
                    facebook_id = profile.getId();
                    f_name = profile.getFirstName();
                    m_name = profile.getMiddleName();
                }
                save(facebook_id, f_name, m_name);

                startNewActivity(facebook_id, f_name, m_name);
                updateUI(true);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    public void inLinkedinlogin() {
        LISessionManager.getInstance(MainActivitySignInAll.this).init(MainActivitySignInAll.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                viewProfile();
                // startNewActivity(LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);

    }

    private Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    protected void viewProfile() {
        String host = "api.linkedin.com";
        String Url = "https://" + host + "/v1/people/~:(first-name,last-name,public-profile-url)";
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(MainActivitySignInAll.this, Url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiresponse) {
                try {
                    JSONObject jasonobject = apiresponse.getResponseDataAsJson();
                    String id = jasonobject.getString("id");
                    String firstname = jasonobject.getString("firstname");
                    String email = jasonobject.getString("email");
                    StringBuilder sb = new StringBuilder();
                    sb.append("Firstname:" + id);
                    sb.append("/n/n");
                    sb.append("Lastname:" + firstname);
                    sb.append("/n/n");
                    sb.append("Email:" + email);
                    save(id, firstname, email);

                    startNewActivity(id, firstname, email);
                    save(id, firstname, email);
                    updateUI(true);

                } catch (JSONException e) {

                    e.printStackTrace();
                    updateUI(true);
                }
            }

            @Override
            public void onApiError(LIApiError error) {
            }
        });
    }

    public String generate_Hashkey() {
        String hashKey = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.anjaline.facebookgooglesignin", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.d("hashKey", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Name not found", e.getMessage(), e);

        } catch (NoSuchAlgorithmException e) {
            Log.d("Error", e.getMessage(), e);
        }
        return hashKey;
    }


    public void onLinkedinLogout() {
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
        LISession session = sessionManager.getSession();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());
            String id = acct.getId();
            String personName = acct.getDisplayName();
            String personPhotoUrl = "";
            String email = acct.getEmail();
            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);
            save(id, personName, email);
            startNewActivity(id, personName, email);

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnGoogleSignIn.setVisibility(View.VISIBLE);
            facebookloginbutton.setVisibility(View.VISIBLE);
            linkedIn_Loginbutton.setVisibility(View.VISIBLE);
        } else {
            btnGoogleSignIn.setVisibility(View.VISIBLE);
            facebookloginbutton.setVisibility(View.VISIBLE);
            linkedIn_Loginbutton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if (requestCode == RC_SIGN_IN_Linkedin) {

            LISessionManager.getInstance(getApplicationContext())
                    .onActivityResult(this, requestCode, resultCode, data);
        }
    }

    public void startNewActivity(String data, String name, String email) {
        Intent intent = new Intent(MainActivitySignInAll.this, Activity_Scanner.class);
        intent.putExtra("user_id", data);
        intent.putExtra("user_name", name);
        intent.putExtra("user_email", email);
        startActivity(intent);
    }

    public void save(String id_data, String name, String email) {
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("id", id_data);
        editor.putString("Name", name);
        editor.putString("Email", email);
        editor.commit();

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
