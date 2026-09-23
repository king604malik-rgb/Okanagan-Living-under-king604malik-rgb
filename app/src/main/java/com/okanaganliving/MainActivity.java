package com.okanaganliving;
import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
public class MainActivity extends Activity {
 TextView status;
 @Override public void onCreate(Bundle b){super.onCreate(b);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(48,75,48,40);root.setBackgroundColor(0xff09121e);
  TextView title=new TextView(this);title.setText("OKANAGAN\nLIVING");title.setTextSize(40);title.setTextColor(Color.WHITE);title.setTypeface(Typeface.create("sans-serif-light",0));root.addView(title);
  TextView desc=new TextView(this);desc.setText("\nSIGNATURE DASHBOARD\n\nA refined, resizable home-screen widget inspired by the Okanagan. Kelowna weather, monthly calendar, local time, battery, storage and your next calendar event.\n\nYour existing wallpaper and Transparent Dialer stay untouched.\n");desc.setTextSize(16);desc.setTextColor(0xffb9c8d8);root.addView(desc);
  Button permission=new Button(this);permission.setText("ALLOW CALENDAR EVENTS");root.addView(permission);permission.setOnClickListener(v->{if(checkSelfPermission(Manifest.permission.READ_CALENDAR)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.READ_CALENDAR},5);else Toast.makeText(this,"Calendar permission already allowed",Toast.LENGTH_SHORT).show();});
  Button refresh=new Button(this);refresh.setText("REFRESH DASHBOARD & WEATHER");root.addView(refresh);refresh.setOnClickListener(v->{DashboardWidget.refresh(this,true);Toast.makeText(this,"Refreshing widgets",Toast.LENGTH_SHORT).show();});
  Button widgets=new Button(this);widgets.setText("ADD HOME SCREEN WIDGET");root.addView(widgets);widgets.setOnClickListener(v->{AppWidgetManager m=AppWidgetManager.getInstance(this);android.content.ComponentName provider=new android.content.ComponentName(this,DashboardWidget.class);if(m.isRequestPinAppWidgetSupported())m.requestPinAppWidget(provider,null,null);else Toast.makeText(this,"Long-press home screen → Widgets → Okanagan Living",Toast.LENGTH_LONG).show();});
  TextView help=new TextView(this);help.setText("\nTIP  ·  Resize the widget to approximately 4 × 5 or larger for crisp details. Tap the widget to return here. Weather requires internet; calendar events require permission.\n\nThe earlier lake live wallpaper remains available separately if you want it.");help.setTextSize(14);help.setTextColor(0xff91a5b9);root.addView(help);
  ScrollView sc=new ScrollView(this);sc.addView(root);setContentView(sc);
 }
 @Override public void onRequestPermissionsResult(int request,String[] p,int[] results){super.onRequestPermissionsResult(request,p,results);if(request==5)DashboardWidget.refresh(this,false);}
}