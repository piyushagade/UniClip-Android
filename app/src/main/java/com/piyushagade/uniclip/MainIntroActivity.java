package com.piyushagade.uniclip;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;

public class MainIntroActivity extends IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setFinishOnTouchOutside(false);

        setFinishEnabled(true);

        setSkipEnabled(false);


        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro1_title)
                .description(R.string.intro1_desc)
                .image(R.drawable.welcome_intro)
                .background(R.color.intro1)
                .backgroundDark(R.color.introDark1)
                .scrollable(true)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro2_title)
                .description(R.string.intro2_desc)
                .image(R.drawable.shake_intro)
                .background(R.color.intro2)
                .backgroundDark(R.color.introDark2)
                .scrollable(true)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro2_title)
                .description(R.string.intro2_desc)
                .image(R.drawable.shake_intro)
                .background(R.color.intro2)
                .backgroundDark(R.color.introDark2)
                .scrollable(true)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro3_title)
                .description(R.string.intro3_desc)
                .image(R.drawable.settings_intro)
                .background(R.color.intro3)
                .backgroundDark(R.color.introDark3)
                .scrollable(true)
                .build());

        if (!hasPermission() && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            addSlide(new FragmentSlide.Builder()
                    .background(R.color.intro4)
                    .backgroundDark(R.color.introDark4)
                    .fragment(FragmentPermission.newInstance())
                    .build());


            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro5_title)
                    .description(R.string.intro5_desc)
                    .image(R.drawable.wait_intro)
                    .background(R.color.intro4)
                    .backgroundDark(R.color.introDark4)
                    .scrollable(true)
                    .build());
        }
        else {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro5_title)
                    .description(R.string.intro5_desc)
                    .image(R.drawable.wait_intro)
                    .background(R.color.intro4)
                    .backgroundDark(R.color.introDark4)
                    .scrollable(true)
                    .build());
        }

        //Page Slide Listeners
        addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(!hasPermission())
                    if(position == 5){
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Wait
                            }
                        }, 2800);


                            startActivity(new Intent(MainIntroActivity.this, MainActivity.class));
                            finish();
                    }
                if(hasPermission())
                    if(position == 4){
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Wait
                            }
                        }, 2800);

                        startActivity(new Intent(MainIntroActivity.this, MainActivity.class));
                        finish();
                    }
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void makeToast(Object data) {
        Toast.makeText(getApplicationContext(), String.valueOf(data), Toast.LENGTH_LONG).show();
    }

    private boolean hasPermission()
    {
        String permission = "android.permission.GET_ACCOUNTS";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}