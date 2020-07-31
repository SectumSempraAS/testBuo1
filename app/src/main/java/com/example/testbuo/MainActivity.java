package com.example.testbuo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private BranchUniversalObject buo;
    private EditText name;
    private EditText link;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);

        link = findViewById(R.id.generatedLink);
        name = findViewById(R.id.username);
        String uName = name.getText().toString();
        buo = new BranchUniversalObject().setCanonicalIdentifier("item/1");
        buo.setTitle("Referral Testing Application");
        buo.setContentDescription("It opens a message with referrer's name");
        buo.setContentMetadata(new ContentMetadata()
                .addCustomMetadata("property1", name.getText().toString()));
        buo.setContentImageUrl("https://www.google.com/imgres?imgurl=https%3A%2F%2Fbranch.io%2Fimg%2Flogo-dark.svg&imgrefurl=https%3A%2F%2Fbranch.io%2F&tbnid=s9g51h8ewI5UnM&vet=12ahUKEwiyvMar1fLqAhWCkUsFHX5vCm4QMygAegUIARCjAQ..i&docid=WmC0K_XgcZBrOM&w=800&h=287&q=branch%20.io%20images&safe=strict&ved=2ahUKEwiyvMar1fLqAhWCkUsFHX5vCm4QMygAegUIARCjAQ");


        //setting link properties
        findViewById(R.id.generateReferralLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                LinkProperties linkProperties = new LinkProperties()
                        .addTag("testingReferral")
                        .setChannel("browser")
                        .setAlias(name.getText().toString())
                        .addControlParameter("$android_deeplink_path", "custom/path/abcd")
                        .addControlParameter("$android_url", "https://branch.io/")
                        .setDuration(100)
                        .addControlParameter("name for message", name.getText().toString());

                //.setAlias("myContentName") // in case you need to white label your link

                // Sync link create example.  This makes a network call on the UI thread
                // txtShortUrl.setText(branchUniversalObject.getShortUrl(MainActivity.this, linkProperties));

                // Async Link creation example
                buo.generateShortUrl(MainActivity.this, linkProperties, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        if (error != null) {
                            link.setText(error.getMessage());
                        } else {
                            link.setText(url);
                        }
                    }
                });

                ShareSheetStyle ss = new ShareSheetStyle(MainActivity.this, "Check this out!", "A message from me:")
                        .setCopyUrlStyle(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                        .setMoreOptionStyle(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_menu_search), "Show more")
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                        .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
                        .setAsFullWidthStyle(true)
                        .setSharingTitle("Share With");

                buo.showShareSheet(MainActivity.this, linkProperties,  ss,  new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {
                    }
                    @Override
                    public void onShareLinkDialogDismissed() {
                    }
                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                    }
                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                });
            }
        });





    }
    @Override public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }
    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)

//            if( error == null ){
//                String rName = null;
//                try {
//                    rName = linkProperties.getString("name");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    rName = "error";
//                }
//                name.setText(rName);
//            }
            if (error != null) {
                Log.i("testBuo Error", error.getMessage());
            } else if (linkProperties != null) {
                Log.i("testBuo noError", linkProperties.toString());
                try {
//                    if( (boolean) linkProperties.get("+clicked_branch_link")){
//                        Log.i("branch link works", "name of referrer");
//                        Log.i("link properties", linkProperties.toString());
//                    }
                    if ((boolean) linkProperties.get("+clicked_branch_link")
                            || (boolean) linkProperties.get("+is_first_session")){
                        Log.i("first click on link", "false");
                        Log.i("test", (String) linkProperties.get("name for message"));


                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("From your friend") .setTitle("Message");
                        builder.setMessage("From your friend")
                                .setCancelable(true);
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        finish();
//                                        Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        //  Action for 'NO' Button
//                                        dialog.cancel();
//                                        Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("Message");
                        alert.show();
                        Toast.makeText(MainActivity.this, "This a message from your friend", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
