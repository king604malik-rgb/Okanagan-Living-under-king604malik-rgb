package com.okanaganliving;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
public class MainActivity extends Activity {
 @Override public void onCreate(Bundle b){super.onCreate(b);
  LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setGravity(Gravity.CENTER);l.setPadding(42,42,42,42);l.setBackgroundColor(0xff09111d);
  TextView title=new TextView(this);title.setText("OKANAGAN\nLIVING");title.setTextColor(Color.WHITE);title.setTextSize(35);title.setGravity(Gravity.CENTER);title.setTypeface(Typeface.create("sans-serif-light",0));l.addView(title);
  TextView info=new TextView(this);info.setText("\nA living lake, made for your lock screen.\n\nThe scene follows the time of day. Water and reflections move gently while the screen is awake.\n\nSamsung controls the actual Always On Display: this app cannot replace its AOD with an animated screen.\n");info.setTextColor(0xffb7c6d8);info.setTextSize(15);info.setGravity(Gravity.CENTER);l.addView(info);
  Button b1=new Button(this);b1.setText("PREVIEW & SET LIVE WALLPAPER");l.addView(b1);b1.setOnClickListener(v->{Intent i=new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,new android.content.ComponentName(this,LakeWallpaper.class));startActivity(i);});
  TextView hint=new TextView(this);hint.setText("\nIn the wallpaper preview, choose Lock screen if your Samsung offers it. Otherwise use Both and adjust your home screen wallpaper afterward.");hint.setTextColor(0xff8da0b7);hint.setGravity(Gravity.CENTER);l.addView(hint);setContentView(l);
 }
}