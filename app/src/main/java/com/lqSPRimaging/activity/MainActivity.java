package com.lqSPRimaging.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.lqSPRimaging.R;

//¶¨ÒåMainActivity¼Ì³Ð×ÔActivity
public class MainActivity extends Activity {

    private ImageView welcomeImg = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeImg = (ImageView) findViewById(R.id.ImageView1);
        AlphaAnimation anima = new AlphaAnimation(0.3f, 1.0f);
        anima.setDuration(500);// Animation display time
        welcomeImg.setAnimation(anima);
        anima.setAnimationListener(new animationImpl());
    }

    private void skip() {
        // TODO Auto-generated method stub
        startActivity(new Intent(this, ImageSPR.class));
        finish();
    }

    public class animationImpl implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
            welcomeImg.setBackgroundResource(R.drawable.welcome1);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // TODO Auto-generated method stub
            skip();

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

        }

    }
}