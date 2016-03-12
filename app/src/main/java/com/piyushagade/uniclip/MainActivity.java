package com.piyushagade.uniclip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends Activity{
    private static final String PREF_FILE = "com.piyushagade.uniclip.preferences";
    Button b_start_stop, b_clear_history, b_view_access_pin, b_set_access_pin;
    CheckBox cb_autostart, cb_notification, cb_vibrate, cb_theme, cb_open_url;
    EditText input_access_pin;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private boolean sp_autostart, sp_notification, sp_vibrate, sp_theme, sp_first_run, sp_open_url, sp_are_creator;
    private String sp_user_email, sp_device_name;
    ImageView clip_icon, sync_anim, b_close, b_menu, b_back, b_info, b_user, b_history, b_help, b_help_shake;
    SeekBar sb_sensitivity, sb_numberShakes;
    TextView sensitivity_indicator, shakes_indicator, access_pin_desc, welcome_text;
    private int sensitivity, numberShakes;
    private int sp_sensitivity, sp_shakes;
    Animation fade_in, fade_out, rotate, blink, slide_in_top, slide_out_top, fade_in_rl_top, fade_out_rl_top, bob, fade_out_rl_settings;
    private RelativeLayout rl_settings, rl_running, rl_main, rl_top, rl_menu_on, rl_menu_content, rl_history, rl_info, rl_user, rl_help;
    private String user_email;
    private View.OnClickListener  mOnClickListener;
    private ClipboardManager myClipboard;
    private ArrayList<String> history_list_activity;
    private Handler handler_history, handler_devices;
    private TextView user_access_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        //UI Components
        b_start_stop = (Button) findViewById(R.id.b_start_stop);
        b_clear_history = (Button) findViewById(R.id.b_clear_history);
        b_view_access_pin = (Button) findViewById(R.id.b_view_access_pin);
        b_set_access_pin = (Button) findViewById(R.id.b_set_access_pin);
        cb_autostart = (CheckBox) findViewById(R.id.cb_autostart);
        cb_notification = (CheckBox) findViewById(R.id.cb_notification);
        cb_vibrate = (CheckBox) findViewById(R.id.cb_vibrate);
        cb_theme = (CheckBox) findViewById(R.id.cb_theme);
        cb_open_url = (CheckBox) findViewById(R.id.cb_open_url);
        clip_icon = (ImageView) findViewById(R.id.clip_icon);
        sync_anim = (ImageView) findViewById(R.id.sync_anim);
        b_close = (ImageView) findViewById(R.id.b_close);
        b_menu = (ImageView) findViewById(R.id.b_menu);
        b_back = (ImageView) findViewById(R.id.b_back);
        b_user = (ImageView) findViewById(R.id.b_user);
        b_history = (ImageView) findViewById(R.id.b_history);
        b_help = (ImageView) findViewById(R.id.b_help);
        b_info = (ImageView) findViewById(R.id.b_info);
        b_help_shake = (ImageView) findViewById(R.id.b_help_shake);
        sb_sensitivity = (SeekBar) findViewById(R.id.sb_sensitivity);
        sb_numberShakes = (SeekBar) findViewById(R.id.sb_shakes);
        sensitivity_indicator = (TextView) findViewById(R.id.sensitivity_indicator);
        shakes_indicator = (TextView) findViewById(R.id.shakes_indicator);
        welcome_text = (TextView) findViewById(R.id.welcome_text);
        access_pin_desc = (TextView) findViewById(R.id.access_pin_desc);
        user_access_pin = (TextView) findViewById(R.id.user_access_pin);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_in_rl_top = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fade_out_rl_top = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fade_out_rl_settings = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        bob = AnimationUtils.loadAnimation(this, R.anim.bob);
        blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        slide_in_top = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        slide_out_top = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
        rl_settings = (RelativeLayout) findViewById(R.id.rl_settings);
        rl_running = (RelativeLayout) findViewById(R.id.rl_running);
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        rl_top = (RelativeLayout) findViewById(R.id.rl_top);
        rl_menu_on = (RelativeLayout) findViewById(R.id.rl_menu_on);
        rl_history = (RelativeLayout) findViewById(R.id.rl_history);
        rl_user = (RelativeLayout) findViewById(R.id.rl_user);
        rl_info = (RelativeLayout) findViewById(R.id.rl_info);
        rl_help = (RelativeLayout) findViewById(R.id.rl_help);
        rl_menu_content = (RelativeLayout) findViewById(R.id.rl_menu_content);
        input_access_pin = (EditText) findViewById(R.id.input_access_pin);

        sp = getSharedPreferences(PREF_FILE, 0);
        ed = sp.edit();

        //Initialize App
        initialize();


        //Close Button listener
        b_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                if (isServiceRunning(UniClipService.class))
                    makeToast("Service will continue running in the background.");

            }

        });

        //Service start stop listener
        b_start_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isServiceRunning(UniClipService.class)) {
                    Intent intent = new Intent(MainActivity.this, UniClipService.class);
                    intent.putExtra("isAutorun", "false");
                    startService(intent);


                    //Reinitialize
                    reinitialize();

                    rl_menu_content.setVisibility(View.GONE);

                    b_start_stop.setText("Stop UniClip!");
                    vibrate(50);
                    rl_settings.startAnimation(fade_out_rl_settings);




                    fade_out_rl_settings.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rl_settings.setVisibility(View.GONE);
                            welcome_text.setText("You can close this app. The background service will make " +
                                    "sure clipboards on all your devices stay unified.");



                            rl_running.setVisibility(View.VISIBLE);
                            rl_running.startAnimation(fade_in);
                            b_set_access_pin.setEnabled(true);
                            b_set_access_pin.setText("Validate");

                            sync_anim.setVisibility(View.VISIBLE);
                            sync_anim.setAlpha(0.10f);


                            clip_icon.startAnimation(bob);

                        }
                    });

                } else {
                    stopService(new Intent(getBaseContext(), UniClipService.class));
                    ed.putBoolean("authenticated", false);

                    b_start_stop.setText("Start UniClip!");
                    rl_settings.startAnimation(fade_in);
                    rl_settings.setVisibility(View.VISIBLE);

                    rl_running.startAnimation(fade_out);
                    rl_running.setVisibility(View.GONE);


                    sync_anim.setAlpha(0.00f);
                    welcome_text.setText("UniClip is a multi-device clipboard synchronization " +
                            "application, which makes sharing texts, links, etc easy.");

                    rl_menu_content.setVisibility(View.GONE);

                }


            }

        });

        //Access Pin Button listener
        b_view_access_pin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int key = sp.getInt("access_pin", 0);
                if(key != 0){
                    b_view_access_pin.setText("Your Access PIN: " + String.valueOf(key));
                }
                else {
                    b_view_access_pin.setText("No PIN set yet");
                }

            }

        });




        //Menu button listener
        b_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(26);

                rl_user.setVisibility(View.INVISIBLE);
                rl_info.setVisibility(View.INVISIBLE);
                rl_history.setVisibility(View.INVISIBLE);
                rl_help.setVisibility(View.INVISIBLE);


                rl_top.startAnimation(fade_out_rl_top);
                rl_menu_on.setVisibility(View.VISIBLE);
                rl_menu_on.startAnimation(fade_in_rl_top);

                fade_in_rl_top.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rl_top.setVisibility(View.INVISIBLE);
                        rl_menu_content.setVisibility(View.VISIBLE);
                        rl_menu_content.startAnimation(slide_in_top);
                    }
                });

            }


        });

        //Back button listener
        b_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                rl_menu_on.startAnimation(fade_out_rl_top);
                rl_top.setVisibility(View.VISIBLE);
                rl_top.startAnimation(fade_in_rl_top);

                //Wait for rl_top to reappear
                fade_in_rl_top.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rl_menu_on.setVisibility(View.INVISIBLE);
                        rl_menu_content.startAnimation(slide_out_top);

                        //Wait for rl_menu_content to slide up
                        slide_out_top.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                rl_menu_content.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });

            }
        });

        //History button listener
        b_history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                rl_history.setVisibility(View.VISIBLE);
                rl_info.setVisibility(View.INVISIBLE);
                rl_user.setVisibility(View.INVISIBLE);
                rl_help.setVisibility(View.INVISIBLE);

                handler_history = new Handler();
                handler_history.postDelayed(refreshHistory, 0);
            }
        });

        //Help for shakes button listener
        b_help_shake.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                makeToast("Help pressed.");
            }
        });

        //Validate access pin button listener
        b_set_access_pin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                final String input_pin = input_access_pin.getText().toString();

                //Format email address (remove the .)
                String user_node = sp_user_email.replaceAll("\\.", "");

                //Firebase
                Firebase fb = new Firebase("https://uniclip.firebaseio.com/cloudboard/" + user_node);

                fb.child("key").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            //Correct Access Pin
                            if (input_pin.equals(snapshot.getValue().toString())) {
                                input_access_pin.setVisibility(View.GONE);
                                b_set_access_pin.setText("Verified");
                                b_set_access_pin.setEnabled(false);
                                user_access_pin.setText(snapshot.getValue().toString());

                                sync_anim.startAnimation(rotate);
                                ed.putBoolean("authenticated", true).commit();

                                //Restart Service
                                stopService(new Intent(getBaseContext(), UniClipService.class));
                                Intent intent = new Intent(MainActivity.this, UniClipService.class);
                                intent.putExtra("isAutorun", "false");
                                startService(intent);

                            }
                            else {
                                makeToast("Wrong Access Pin. Try Again");
                                ed.putBoolean("authenticated", false).commit();

                                //Stop Service
                                stopService(new Intent(getBaseContext(), UniClipService.class));
                            }

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                    }
                });

            }
        });

        //User button listener
        b_user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                rl_user.setVisibility(View.VISIBLE);
                rl_info.setVisibility(View.INVISIBLE);
                rl_history.setVisibility(View.INVISIBLE);
                rl_help.setVisibility(View.INVISIBLE);

                //Set data
                TextView tv_username  = (TextView) findViewById(R.id.user_username);
                TextView tv_device  = (TextView) findViewById(R.id.user_device);
                tv_username.setText(sp_user_email);
                tv_device.setText(sp_device_name);


                handler_history = new Handler();
                handler_history.postDelayed(refreshDevicesList, 0);
            }
        });

        //Info button listener
        b_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                rl_info.setVisibility(View.VISIBLE);
                rl_user.setVisibility(View.INVISIBLE);
                rl_history.setVisibility(View.INVISIBLE);
                rl_help.setVisibility(View.INVISIBLE);
            }
        });


        //Help button listener
        b_help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                rl_help.setVisibility(View.VISIBLE);
                rl_user.setVisibility(View.INVISIBLE);
                rl_history.setVisibility(View.INVISIBLE);
                rl_info.setVisibility(View.INVISIBLE);
            }
        });

        //Clear history listener
        b_clear_history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibrate(27);
                history_list_activity.clear();
                UniClipService.history_list_service.clear();
                setHistoryListItems();
            }
        });


        //Autostart checkbox listener
        cb_autostart.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener()

                 {
                     @Override
                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                         if (isChecked) {
                             ed.putBoolean("autostart", true);
                         } else {
                             ed.putBoolean("autostart", false);
                         }
                         ed.commit();

                     }
                 }

                );

        //Notiication checkbox listener
        cb_notification.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener()

                 {
                     @Override
                     public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){
                         if (isChecked) {
                             ed.putBoolean("notification", true);
                         } else {
                             ed.putBoolean("notification", false);
                         }
                         ed.commit();

                     }
                 }

                );

        //Open_URL checkbox listener
        cb_open_url.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener()

                 {
                     @Override
                     public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){
                         if (isChecked) {
                             ed.putBoolean("open_url", true);
                         } else {
                             ed.putBoolean("open_url", false);
                         }
                         ed.commit();

                     }
                 }

                );

        //Vibrate checkbox listener
        cb_vibrate.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener()

                 {
                     @Override
                     public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){
                         if (isChecked) {
                             ed.putBoolean("vibrate", true);
                         } else {
                             ed.putBoolean("vibrate", false);
                         }
                         ed.commit();

                     }
                 }

                );

        //Theme checkbox listener
        cb_theme.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener()

                 {
                     @Override
                     public void onCheckedChanged (CompoundButton buttonView,boolean isChecked){
                         if (isChecked) {
                             ed.putBoolean("theme", true);
                             rl_main.setBackgroundColor(Color.parseColor("#009688"));
                         } else {
                             ed.putBoolean("theme", false);
                             rl_main.setBackgroundColor(Color.parseColor("#DE111111"));
                         }
                         ed.commit();

                     }
                 }

                );

    }

    private void reinitialize() {

        //Get Values SP
        sp_are_creator = sp.getBoolean("creator", false);


        //Make snack if network unavailable
        if(!isNetworkAvailable())makeSnack("Network unavailable.");



        //Get user email
        if(hasPermission()) {
            AccountManager accountManager = AccountManager.get(MainActivity.this);
            Account account = getAccount(accountManager);

            if (account != null) {
                ed.putString("user_email", account.name);
                ed.commit();
            } else
                makeSnackForPermissions("Grant 'Contacts' permission to UniClip!");
        }


        //Set content if this device is/is not the creator


        final Handler creatorHandler = new Handler();
        creatorHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sp_are_creator = sp.getBoolean("creator", false);
                if(!sp_are_creator){
                    access_pin_desc.setText("In order to start listening to the cloudboard, you need to input the Access Pin. You can find Access Pin in the Menu of the device that created the cloudboard.");

                    b_view_access_pin.setVisibility(View.GONE);

                    sync_anim.clearAnimation();
                    b_set_access_pin.setVisibility(View.VISIBLE);
                    input_access_pin.setVisibility(View.VISIBLE);
                }
                else {
                    //Reset Access Pin Button text
                    b_view_access_pin.setText("View Access Pin");
                    b_view_access_pin.setVisibility(View.VISIBLE);

                    sync_anim.startAnimation(rotate);
                    b_set_access_pin.setVisibility(View.INVISIBLE); //Let this be invilsible
                    input_access_pin.setVisibility(View.GONE);
                }
            }
        }, 600);





        //Format email address (remove the .)
        String user_node = sp_user_email.replaceAll("\\.", "");

        //Firebase
        Firebase fb = new Firebase("https://uniclip.firebaseio.com/cloudboard/" + user_node);

        //Check if this device is creator
        fb.child("creator").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if(sp_device_name.equals(snapshot.getValue().toString())){
                        ed.putBoolean("creator", true).commit();
                    }
                    else ed.putBoolean("creator", false).commit();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

    }



    //History refresh with 4 sec delay
    private Runnable refreshHistory = new Runnable() {
        public void run() {
            setHistoryListItems();

            handler_history.postDelayed(this, 4000);
        }
    };

    //Set history feed
    private void setHistoryListItems() {
        syncHistoryLists();

        int i = 1;
        LinearLayout ll_history_feed = (LinearLayout) findViewById(R.id.ll_history_feed);
        ll_history_feed.removeAllViews();

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(history_list_activity != null)
            if(history_list_activity.size() != 0)
                for (final String listItem : history_list_activity) {
                    final TextView row1 = new TextView(getBaseContext());
                    row1.generateViewId();
                    row1.setPadding(20, 12, 30, 12);

                    row1.setText(i + ". " + listItem.toString());
                    i++;

                    if(isEven(i))row1.setTextColor(Color.parseColor("#CCFFFFFF"));
                    else row1.setTextColor(Color.parseColor("#88FFFFFF"));

                    row1.setTypeface(Typeface.MONOSPACE);
                    row1.setTextSize(16);

                    row1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            ClipData myClip = ClipData.newPlainText("text", String.valueOf(listItem));
                            makeToast("Text copied to clipboard.");
                            myClipboard.setPrimaryClip(myClip);
                        }

                    });

                    RelativeLayout row2 = new RelativeLayout(getBaseContext());
                    row2.setBackgroundColor(Color.parseColor("#AA009688"));
                    row2.setMinimumHeight(2);


                    RelativeLayout row3 = new RelativeLayout(getBaseContext());
                    row3.setBackgroundColor(Color.parseColor("#00009688"));
                    row3.setMinimumHeight(15);

                    RelativeLayout row4 = new RelativeLayout(getBaseContext());
                    row4.setBackgroundColor(Color.parseColor("#00009688"));
                    row4.setMinimumHeight(15);

                    ll_history_feed.addView(row1);
                    ll_history_feed.addView(row3);
                    ll_history_feed.addView(row2);
                    ll_history_feed.addView(row4);
                    ll_history_feed.setVisibility(View.VISIBLE);
                }
            else{
                final TextView row1 = new TextView(getBaseContext());
                row1.generateViewId();
                row1.setPadding(20, 12, 20, 12);
                row1.setText("Clipboard history is empty!");

                row1.setTextColor(Color.parseColor("#009688"));
                row1.setTypeface(Typeface.MONOSPACE);
                row1.setTextSize(16);

                ll_history_feed.addView(row1);
                ll_history_feed.setVisibility(View.VISIBLE);

            }
    }

    //Check if a number is even
    private boolean isEven(int i) {
        if(i % 2 == 0) return true;
        return false;
    }

    //Refresh device list runnable with 4 sec delay
    private Runnable refreshDevicesList = new Runnable() {
        public void run() {
            setOtherDevicesListItems();

            handler_history.postDelayed(this, 4000);
        }
    };

    //Set devices in feed
    private void setOtherDevicesListItems() {
        int i = 1;
        final LinearLayout ll_other_devices_feed = (LinearLayout) findViewById(R.id.ll_other_devices_feed);
        ll_other_devices_feed.removeAllViews();

        final Firebase fb_devices = new Firebase("https://uniclip.firebaseio.com/cloudboard/" +
                sp_user_email.replaceAll("\\.", "") + "/devices");

        fb_devices.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ll_other_devices_feed.removeAllViews();

                int i = 1;    //Serial number
                if (snapshot.getChildrenCount() != 0)
                    for (final DataSnapshot postSnapshot : snapshot.getChildren()) {

                        final TextView row1 = new TextView(getBaseContext());
                        row1.generateViewId();
                        row1.setPadding(20, 12, 20, 12);

                        row1.setText(i + ". " + postSnapshot.getKey().toString());
                        i++;

                        if(postSnapshot.getValue().toString().equals("1"))row1.setTextColor(Color.parseColor("#009688"));
                        else if(postSnapshot.getValue().toString().equals("0"))row1.setTextColor(Color.parseColor("#AC2358"));
                        row1.setTypeface(Typeface.MONOSPACE);
                        row1.setTextSize(16);

                        row1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if(postSnapshot.getValue().toString().equals("1"))makeToast(postSnapshot.getKey().toString() + " is listening.");
                                else if(postSnapshot.getValue().toString().equals("0"))makeToast(postSnapshot.getKey().toString() + " is inactive.");
                            }

                        });

                        ll_other_devices_feed.addView(row1);
                        ll_other_devices_feed.setVisibility(View.VISIBLE);
                    }
                else {
                    final TextView row1 = new TextView(getBaseContext());
                    row1.generateViewId();
                    row1.setPadding(20, 12, 20, 12);
                    row1.setText("No registered devices.");

                    row1.setTextColor(Color.parseColor("#AAFFFFFF"));
                    row1.setTypeface(Typeface.MONOSPACE);
                    row1.setTextSize(16);

                    ll_other_devices_feed.addView(row1);
                    ll_other_devices_feed.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    //Initialize application
    private void initialize() {
        //Showcase UI
        presentShowcaseSequence();

        //Set Authenticated as false
        ed.putBoolean("authenticated", false).commit();

        //Get Values SP
        sp_autostart = sp.getBoolean("autostart", true);
        sp_notification = sp.getBoolean("notification", true);
        sp_vibrate = sp.getBoolean("vibrate", true);
        sp_theme = sp.getBoolean("theme", true);
        sp_user_email = sp.getString("user_email", "unknown");
        sp_device_name = sp.getString("device_name", "unknown");
        sp_first_run = sp.getBoolean("first_run", true);
        sp_open_url = sp.getBoolean("open_url", true);
        sp_are_creator = sp.getBoolean("creator", false);

        //Intro Screen
        if(sp_first_run){
            startActivity(new Intent(MainActivity.this, MainIntroActivity.class));
            finish();
        }

        //Make snack if network unava
        if(!isNetworkAvailable())makeSnack("Network unavailable.");

        //Detect accelerometer
        PackageManager manager = getPackageManager();
        boolean hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);

        if(!hasAccelerometer){
            sb_sensitivity.setEnabled(false);
            sb_numberShakes.setEnabled(false);
            sb_sensitivity.setProgress(0);

        }

        //Set content if this device is not the creator
        if(!sp_are_creator){
            access_pin_desc.setText("In order to start listening to the cloudboard, you need to input the Access Pin. You can find Access Pin in the Menu of the device that created the cloudboard.");
            b_view_access_pin.setVisibility(View.GONE);

            sync_anim.clearAnimation();
            b_set_access_pin.setVisibility(View.VISIBLE);
            input_access_pin.setVisibility(View.VISIBLE);
        }

        //Detect Vibrator
        String vs = Context.VIBRATOR_SERVICE;
        Vibrator mVibrator = (Vibrator)getSystemService(vs);

        boolean hasVibrator = mVibrator.hasVibrator();

        if(!hasVibrator){
            cb_vibrate.setChecked(false);
            cb_vibrate.setEnabled(false);
            sp_vibrate = false;
            ed.putBoolean("vibrate", false).commit();
        }


        //Get user email
        if(hasPermission()) {
            AccountManager accountManager = AccountManager.get(MainActivity.this);
            Account account = getAccount(accountManager);

            if (account != null) {
                ed.putString("user_email", account.name);
                ed.commit();
            } else
                makeSnackForPermissions("Grant 'Contacts' permission to UniClip!");
        }

        else if(!hasPermission() && !sp_first_run){
            startActivity(new Intent(MainActivity.this, ActivityPermission.class));
            finish();
        }


        //Get device model
        ed.putString("device_name", getDeviceName());
        ed.commit();

        history_list_activity = new ArrayList<String>();
        syncHistoryLists();

        rl_menu_content.setVisibility(View.INVISIBLE);


        //Format email address (remove the .)
        String user_node = sp_user_email.replaceAll("\\.", "");

        //Firebase
        Firebase fb = new Firebase("https://uniclip.firebaseio.com/cloudboard/" + user_node);

        //Get user pin from firebase
        fb.child("key").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Set user access pin
                    if(sp_are_creator) user_access_pin.setText(snapshot.getValue().toString());

                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        //Check if this device is creator
        fb.child("creator").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if(sp_device_name.equals(snapshot.getValue().toString())){
                        ed.putBoolean("creator", true).commit();
                    }
                    else ed.putBoolean("creator", false).commit();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        //Set up content if service is running
        if (isServiceRunning(UniClipService.class)) {

            b_start_stop.setText("Stop UniClip!");

            rl_settings.setVisibility(View.GONE);
            rl_running.setVisibility(View.VISIBLE);
            if(sp_are_creator){

            }

            sync_anim.setAlpha(0.2f);

            welcome_text.setText("You can close this app. The background service will make " +
                    "sure clipboards on all your devices stay unified.");

        } else {
            b_start_stop.setText("Start UniClip!");
            rl_settings.setVisibility(View.VISIBLE);
            sync_anim.setAlpha(0.00f);

            welcome_text.setText("UniClip is a multi-device clipboard synchronization "+
                    "application, which makes sharing texts, links, etc easy.");
        }

        sync_anim.startAnimation(rotate);


        sp_sensitivity = sp.getInt("sensitivity", 2+1);
        sp_shakes = sp.getInt("shakes", 2);

        sb_numberShakes.setProgress(sp_shakes);
        sb_sensitivity.setProgress(sp_sensitivity);
        sensitivity_indicator.setText(String.valueOf(sp_sensitivity+1));
        shakes_indicator.setText(String.valueOf(sp_shakes));

        if(sp_autostart)cb_autostart.setChecked(true);
        else cb_autostart.setChecked(false);

        if(sp_notification)cb_notification.setChecked(true);
        else cb_notification.setChecked(false);

        if(sp_vibrate)cb_vibrate.setChecked(true);
        else cb_vibrate.setChecked(false);

        if(sp_open_url)cb_open_url.setChecked(true);
        else cb_open_url.setChecked(false);

        if(sp_theme){
            cb_theme.setChecked(true);
            rl_main.setBackgroundColor(Color.parseColor("#009688"));
        }
        else{
            cb_theme.setChecked(false);
            rl_main.setBackgroundColor(Color.parseColor("#DE111111"));
        }


        int value = sb_numberShakes.getProgress();
        shakes_indicator.setText(String.valueOf(value));


        sb_sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                sensitivity = progress;
                sensitivity_indicator.setText(String.valueOf(sensitivity + 1));
                ed.putInt("sensitivity", sensitivity);
                ed.commit();
            }
        });


        sb_numberShakes.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        numberShakes = progress;
                        shakes_indicator.setText(String.valueOf(numberShakes));
                        ed.putInt("shakes", numberShakes);
                        ed.commit();

                        if(progress == 0)sb_sensitivity.setEnabled(false);
                        else sb_sensitivity.setEnabled(true);

                    }
                }
        );

        if(sb_numberShakes.getProgress() == 0)sb_sensitivity.setEnabled(false);

        ed.putBoolean("first_run", false).commit();


    }

    private void syncHistoryLists() {
        if(isServiceRunning(UniClipService.class)){
            history_list_activity = UniClipService.history_list_service;
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void sendToast(Object charSequence){
        Toast.makeText(getBaseContext(), (String) charSequence, Toast.LENGTH_SHORT);

    }

    public void makeSnack(String t){
        View v = findViewById(R.id.rl_main);
        Snackbar.make(v, t, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void makeSnackForPermissions(String t){
        View v = findViewById(R.id.rl_main);
        Snackbar.make(v, t, Snackbar.LENGTH_LONG)
                .setAction("Let's Go", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void vibrate(int time){
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(time);
    }

    private void makeToast(Object data) {
        Toast.makeText(getApplicationContext(),String.valueOf(data),Toast.LENGTH_LONG).show();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "sequence example");

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
//                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(b_menu)
                        .setDismissText("Proceed >")
                        .setContentText("This is the menu button. It has user information, clipboard history, and much more.")
                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(b_start_stop)
                        .setDismissText("Proceed >")
                        .setContentText("This is 'Start UniClip' button. This turns on the awesome.")
                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(rl_settings)
                        .setDismissText("Got it >")
                        .setContentText("And these are your settings.")
                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(b_close)
                        .setDismissText("Awesome, let's begin. >")
                        .setContentText("This thing here, turns down the awesome.")
                        .withCircleShape()
                        .build()
        );

        sequence.start();

    }

    private boolean hasPermission()
    {

        String permission = "android.permission.GET_ACCOUNTS";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
