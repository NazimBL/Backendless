package com.example.dell.listviewapp;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.BaseKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.async.callback.UploadCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.files.FileInfo;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoCategory;
import com.backendless.geo.GeoPoint;
import com.backendless.messaging.DeliveryOptions;

import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushBroadcastMask;
import com.backendless.property.UserProperty;
import com.backendless.services.messaging.MessageStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final static String APP_VERSION="v1";
    final static String APP_ID="C78D1DDB-2076-EDD6-FF5A-ED7241CD8D00";
    final static String APP_KEY="AC3435E6-E4F1-EE56-FF8F-80B10D894300";
    final static String SERVER_ID="AIzaSyAH5yDvjBB1XbdnxkY0cmvSFjfOHt67XCA";
    final static String SENDER_ID="150169581759";

    EditText email,pass;
    Button login,register,facebook;
    VideoView video;
    boolean videoTag=false;
    int position=0;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        Backendless.initApp(this, APP_ID, APP_KEY, APP_VERSION);
        final BackendlessUser user = new BackendlessUser();


        //get device registration
//        DeviceRegistration registration=Backendless.Messaging.getDeviceRegistration();

      searchForGeoCategorie();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(user);

                //  video.start();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.setEmail(email.getText().toString());
                user.setPassword(pass.getText().toString());
                registerUser(user);

            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoTag = !videoTag;
                if (videoTag) video.start();
                else video.pause();
            }
        });


        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messaginRegisterSender();

                sigleEmail("yanis.yefsah20@gmail.com");

            }
        });
    }

    void searchForGeoCategorie(){

        final BackendlessGeoQuery geoQuery=new BackendlessGeoQuery();
        geoQuery.addCategory("Restaurants");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("Baratie", "East Blue");
        geoQuery.setMetadata(map);

        new Thread(new Runnable() {
            @Override
            public void run() {

                BackendlessCollection<GeoPoint> geoPoint=Backendless.Geo.getPoints(geoQuery);
                Log.d("Nazim",""+geoPoint.getTableName());
                Log.d("Nazim",""+geoPoint.getType());
                int i=0;
                while (i<geoPoint.getCurrentPage().size()){
                    Log.d("Nazim",""+geoPoint.getCurrentPage().get(i));
                    i++;
                }
            }
        }).start();


    }

    void saveMultipleCategoriesWithTheirData(){

        List<String> categories=new ArrayList<String>();
        categories.add("Restaurants");
        categories.add("StripClubs");

        Map<String,Object> meta=new HashMap<String,Object>();
        meta.put("Baratie","East Blue");
        meta.put("Chahma","Bouira");

        Backendless.Geo.savePoint(32.67, 3.9, categories, meta, new BackendlessCallback<GeoPoint>() {
            @Override
            public void handleResponse(GeoPoint response) {
                Log.d("Nazim","List of category created");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("Nazim",fault.toString());
            }
        });
    }

    void simplePushNotif(int numberOfReceivers){


        DeliveryOptions deliveryOptions=new DeliveryOptions();
        if(numberOfReceivers>1) deliveryOptions.setPushBroadcast(PushBroadcastMask.ANDROID);
        PublishOptions publishOptions=new PublishOptions();
        publishOptions.putHeader("android-ticker-text", "You just got a push notification!");
        publishOptions.putHeader("android-content-title", "This is a notification title");
        publishOptions.putHeader("android-content-text", "Push Notifications are cool");

        Backendless.Messaging.publish((Object) "Hi Dude", publishOptions, new BackendlessCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                Log.d("Nazim", "Yo");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("Nazim", fault.toString());
            }
        });

    }

    void saveGeoPoint(double latitude,double longitude){

        GeoPoint geoPoint=new GeoPoint(latitude,longitude);
        Backendless.Geo.savePoint(geoPoint, new BackendlessCallback<GeoPoint>() {
            @Override
            public void handleResponse(GeoPoint response) {

                Log.d("Nazim","geopoint saved");
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            Log.d("Nazim",fault.toString());
            }
        });

    }
    void sigleEmail(String email){

        Backendless.Messaging.sendTextEmail("WARNING", "Mr Yanis Yefsah , being recently caught viewing inapropriate content on the Darknet,you are subject of an investigation,US Federal agency will soon be in touch with you.You can email back on this adress if you feel being wrongly accused.", email,
                new BackendlessCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        Log.d("Nazim", "SUCESS");
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.d("Nazim", fault.toString());
                    }
                });

    }

    void messaginRegisterSender(){
        Backendless.Messaging.registerDevice(SENDER_ID, new BackendlessCallback<Void>() {
            @Override
            public void handleResponse(Void response) {

                Log.d("Nazim", "MESSAGE CLEAR");

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("Nazim", "FAULT : " + fault.toString());
            }
        });


    }

    void videoMaker(){
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("My first Video");
        progressDialog.setMessage("...Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try{
            MediaController mc=new MediaController(MainActivity.this);
            Uri uri=Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video);
            video.setMediaController(mc);
            video.setClickable(true);
            video.canPause();
            video.canSeekBackward();
            video.canSeekForward();


            video.setVideoURI(uri);

        }catch (Exception e) {
            Log.d("Nazim", e.toString());
        }
        video.requestFocus();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                progressDialog.dismiss();
                video.seekTo(position);
                if (position == 0) video.start();
                else video.pause();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt("Position", video.getCurrentPosition());
        video.pause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        position=savedInstanceState.getInt("Position");
        video.seekTo(position);
    }

    public void ls(){

        Backendless.Files.listing("/web", new BackendlessCallback<BackendlessCollection<FileInfo>>() {
            @Override
            public void handleResponse(BackendlessCollection<FileInfo> response) {

                Iterator<FileInfo> filesIterator = response.getCurrentPage().iterator();
                while (filesIterator.hasNext()) {
                    FileInfo file = filesIterator.next();
                    String URL = file.getURL();
                    String publicURL = file.getPublicUrl();
                    Date createdOn = new Date(file.getCreatedOn());
                    String name = file.getName();
                    Log.d("Nazim", "Url : " + URL);
                    Log.d("Nazim", "public Url : " + publicURL);
                    Log.d("Nazim", "Date :" + createdOn);
                    Log.d("Nazim", "name " + name);

                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("Nazim", "FAULT");
            }
        });


    }

    public static void saveFile(){

new Thread(new Runnable() {
    @Override
    public void run() {
        byte[] bytes = "Never give up".getBytes();
        String savedFileURL = Backendless.Files.saveFile("tempfolder", "niggaa.txt", bytes, true );
        Log.d( "Nazim","File saved. File URL - " + savedFileURL );
    }
}).start();

    }


    void uploadFile(int create_open){


        File file=new File(getApplicationContext().getFilesDir(),"niggaa.txt");
        file.setReadable(true);
if(create_open==1) {
        try{
            FileOutputStream o=new FileOutputStream(file);
            o.write("fuck".getBytes());
            o.close();

        }catch( Exception e){
            Log.d("Nazim",e.toString());
        }
}

        Backendless.Files.upload(file, "/folder", new BackendlessCallback<BackendlessFile>() {
            @Override
            public void handleResponse(BackendlessFile response) {

                Log.d("Nazim", "Upload");

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("Nazim", "" + fault.toString());
            }
        })  ;
    }
    void uploadBitmap(){
        Backendless.Files.Android.upload(BitmapFactory.decodeResource(getResources(), R.drawable.yep),
                Bitmap.CompressFormat.PNG,
                100,
                "myphoto.png",
                "mypics",
                new BackendlessCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(BackendlessFile response) {

                        Log.d("Nazim","Bitmap");


                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.d("Nazim","FAULT");
                    }
                });
    }
    void loginUser(BackendlessUser user){
        user.setEmail(email.getText().toString());
        user.setPassword(pass.getText().toString());
        try {
            Backendless.UserService.login(user.getEmail(), user.getPassword(), new BackendlessCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser response) {

                    BackendlessUser u = Backendless.UserService.CurrentUser();
                    String phone = "0796770812";
                    u.setProperty("phone", phone);
                    updateUser(u);
                    Log.d("Nazim", "phone : " + u.getProperty("phone"));
                }
            });
        } catch (BackendlessException e) {
            Log.d("Nazim", " code : " + e.getCode() + "\n" + "detail : " + e.getDetail());
        }
    }

    void initialize(){

        email=(EditText)findViewById(R.id.edit1);
        pass=(EditText)findViewById(R.id.edit2);
        login=(Button)findViewById(R.id.b);
        register=(Button)findViewById(R.id.b2);
        facebook=(Button)findViewById(R.id.b3);
        video=(VideoView)findViewById(R.id.videoView);
    }
    void registerUser(final BackendlessUser us){


                Backendless.UserService.register(us, new BackendlessCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {

                        Log.d("Nazim","registred");
                        final Bitmap b= BitmapFactory.decodeResource(getResources(),R.drawable.yep);


                        try{

                            new Thread(new Runnable() {
                                public void run() {
                                    // synchronous backendless API call here:
                                    Person person=new Person();
                                    person.name="Nazim";
                                    person.phone=45;
                                    person.bitmap=b;
                                    Backendless.Data.of( Person.class ).save(person);
                                }
                            }).start();

                        }catch(Exception e){Log.d("Nazim",e.toString());}

                    }
                });

    }

    void fbLog(){

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Backendless.UserService.loginWithFacebook(MainActivity.this, new BackendlessCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {

                        Log.d("Nazim", "Loged with fb");
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Log.d("Nazim", "EROOR");
                    }
                });
            }
        });

    }
    void saveComment(){

        Backendless.Persistence.save(new Comment(), new BackendlessCallback<Comment>() {
            @Override
            public void handleResponse(Comment response) {
                Log.d("Nazim", "saved");
            }
        });
    }
    void savePerson(){

       new Thread(new Runnable() {
           @Override
           public void run() {

               Person person=new Person();
               person.name="Eden";
               person.phone=20;
               person.bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.yep);

               Backendless.Persistence.of(Person.class).save(person);
           }
       }).start();
    }
    void updateUser(BackendlessUser u){

        Backendless.UserService.update(u, new BackendlessCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

                Log.d("Nazim", "Updated ");
            }
        });
    }
    void describePersistence(){


        Backendless.UserService.describeUserClass(new BackendlessCallback<List<UserProperty>>() {
            @Override
            public void handleResponse(List<UserProperty> response) {

                for (UserProperty property : response) {


                    Log.d("Nazim", "Name : " + property.getName());
                    Log.d("Nazim", "is identity ? : " + property.isIdentity());
                    Log.d("Nazim", "Type : " + property.getType());

                }
                // synchronous backendless API call here:

            }
        });
    }

    void saveGeoLocCategorie(String categorieName){

        Backendless.Geo.addCategory(categorieName, new BackendlessCallback<GeoCategory>() {
            @Override
            public void handleResponse(GeoCategory response) {
                Log.d("Nazim", "GeoCategorie Created");

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("Nazim", fault.toString());
            }
        });

    }

    void lsGeoCategorie(){
        Backendless.Geo.getCategories(new BackendlessCallback<List<GeoCategory>>() {
            @Override
            public void handleResponse(List<GeoCategory> response) {

                Iterator<GeoCategory> iterator=response.iterator();
                while(iterator.hasNext()){
                    Log.d("Nazim",iterator.next().toString());
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {


            }
        });
    }

}
