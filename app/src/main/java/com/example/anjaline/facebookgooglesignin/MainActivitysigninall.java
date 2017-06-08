package com.example.anjaline.facebookgooglesignin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class MainActivitysigninall extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    //facebook
    private LoginButton facebooklogin;
    private CallbackManager callbackManager;
    //google
    private static final String TAG = MainActivitysigninall.class.getSimpleName();
    private static final int RC_SIGN_IN_GOOGLE = 007;
    private static final int RC_SIGN_IN_Linkedin = 006;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnGoogleSignIn;

    //linkedin
    private Button linkedinLogin;
    private String linkdinHashKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activitysigninall);
        //facebook
        callbackManager = CallbackManager.Factory.create();
        //  AppEventsLogger.activateApp(this);
        facebooklogin = (LoginButton) findViewById(R.id.facebook_login_button);
        //linkedin
        linkedinLogin = (Button) findViewById(R.id.login_li_button);
        //GOOGLE//

        btnGoogleSignIn = (SignInButton) findViewById(R.id.googlebtn_sign_in);

        //facebook
        //LoginManager.getInstance().logOut();
        facebooklogin.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        facebooklogin.setOnClickListener(new View.OnClickListener() {
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

        // linkedinLogout.setVisibility(View.GONE);
        linkedinLogin.setOnClickListener(new View.OnClickListener() {
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

        linkdinHashKey = generateHashkey();
        Log.d("hashKey", linkdinHashKey);
        //LISessionManager.getInstance(getApplicationContext()).clearSession();
        if (isLoggedIn()) {
            startNewActivity("User is logged in with fb.");
        }




    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void fbLogin() {
        Log.v("LoginActivity", "FB login called");
        facebooklogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("LoginActivity", loginResult.getAccessToken().getUserId());
                startNewActivity(loginResult.getAccessToken().getUserId());
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

    //All methods//
    //linkedin//

    public void inLinkedinlogin() {

        try {
            LISessionManager.getInstance(MainActivitysigninall.this).init(MainActivitysigninall.this, buildScope(), new AuthListener() {
                @Override
                public void onAuthSuccess() {
                    LISessionManager sessionManager = LISessionManager.getInstance(MainActivitysigninall.this);
                    LISession session = sessionManager.getSession();
                    Toast.makeText(getApplicationContext(), "success" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString(), Toast.LENGTH_LONG).show();
                    viewProfile();
                }

                @Override
                public void onAuthError(LIAuthError error) {
                    Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
                }
            }, true);
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    private Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }


    protected void viewProfile() {

        String host = "api.linkedin.com";
        String Url = "https://" + host + "/v1/people/~:(first-name,last-name,public-profile-url)";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(MainActivitysigninall.this, Url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiresponse) {
                //Print the response s you will get profile parameters.
                try {
                    JSONObject jasonobject = apiresponse.getResponseDataAsJson();
                    // String id = jasonobject.getString("id");
                    String firstname = jasonobject.getString("firstname");
                    String lastname = jasonobject.getString("lastname");
                    String email = jasonobject.getString("email");
                    // Picasso.with(getApplicationContext()).load(pictureurl)
                    StringBuilder sb = new StringBuilder();
                    sb.append("Firstname:" + firstname);
                    sb.append("/n/n");
                    sb.append("Lastname:" + lastname);
                    sb.append("/n/n");
                    sb.append("Email:" + email);
                    updateUI(true);

                } catch (JSONException e) {

                    e.printStackTrace();
                    updateUI(true);
                }
            }

            @Override
            public void onApiError(LIApiError error) {
                //error message
            }
        });
    }


    public String generateHashkey() {
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

    //google//
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }


                });
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

            String personName = acct.getDisplayName();
            String personPhotoUrl = "";
            String email = acct.getEmail();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);
            startNewActivity("Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnGoogleSignIn.setVisibility(View.GONE);
            facebooklogin.setVisibility(View.GONE);
            linkedinLogin.setVisibility(View.GONE);
        } else {
            btnGoogleSignIn.setVisibility(View.GONE);
            facebooklogin.setVisibility(View.VISIBLE);
            linkedinLogin.setVisibility(View.GONE);
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
            startNewActivity(data.getData().toString());

            // Intent intent = new Intent(MainActivityli.this,home .class);
            // startActivity(intent);
        }

        // @Override
        //public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // }

        //@Override
        //public void onClick(View v) {

        //}
    }

    public void startNewActivity(String data) {
        Intent intent = new Intent(MainActivitysigninall.this, Activity_Scanner.class);
        intent.putExtra("intent_key", data);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
