package com.okanaganliving;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.*;
import android.os.BatteryManager;
import android.provider.CalendarContract;
import android.widget.RemoteViews;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.json.JSONObject;

public class DashboardWidget extends AppWidgetProvider {
 static final String REFRESH="com.okanaganliving.REFRESH";
 static final int W=900,H=1100;static final int WHITE=0xfff4f7ff,MUTED=0xffa2b0c5,GOLD=0xffd7c19c,BLUE=0xff8daed0;
 static final SimpleDateFormat TIME=new SimpleDateFormat("h:mm",Locale.CANADA);
 static final SimpleDateFormat DATE=new SimpleDateFormat("EEEE, MMMM d",Locale.CANADA);
 static final SimpleDateFormat MONTH=new SimpleDateFormat("MMMM yyyy",Locale.CANADA);
 static class Weather {String label="Weather unavailable";int temp=0;boolean ready=false;String stamp="";}
 static Weather weather(Context c,boolean force){
  android.content.SharedPreferences prefs=c.getSharedPreferences("weather",0);Weather out=new Weather();
  long age=System.currentTimeMillis()-prefs.getLong("updated",0);
  if(force||age>30*60*1000L){
   HttpURLConnection conn=null;
   try{
    URL url=new URL("https://api.open-meteo.com/v1/forecast?latitude=49.8880&longitude=-119.4960&current=temperature_2m,weather_code&timezone=America%2FVancouver");
    conn=(HttpURLConnection)url.openConnection();conn.setConnectTimeout(6500);conn.setReadTimeout(6500);
    ByteArrayOutputStream b=new ByteArrayOutputStream();try(InputStream in=conn.getInputStream()){byte[] buf=new byte[2048];int n;while((n=in.read(buf))!=-1)b.write(buf,0,n);}
    JSONObject current=new JSONObject(b.toString("UTF-8")).getJSONObject("current");
    int t=(int)Math.round(current.getDouble("temperature_2m"));int code=current.getInt("weather_code");
    String label=code==0?"Clear skies":code<=3?"Partly cloudy":code<=48?"Misty":code<=67?"Rain":code<=77?"Snow":code<=82?"Showers":code<=86?"Snow showers":"Storm";
    prefs.edit().putInt("temp",t).putString("label",label).putLong("updated",System.currentTimeMillis()).apply();
   }catch(Exception ignored){}finally{if(conn!=null)conn.disconnect();}
  }
  out.ready=prefs.contains("updated");out.temp=prefs.getInt("temp",0);out.label=prefs.getString("label","Weather unavailable");
  return out;
 }
 static String nextEvent(Context c){
  if(c.checkSelfPermission(Manifest.permission.READ_CALENDAR)!=PackageManager.PERMISSION_GRANTED)return "Allow calendar in app";
  long now=System.currentTimeMillis();String[] columns={CalendarContract.Instances.TITLE,CalendarContract.Instances.BEGIN};
  android.net.Uri.Builder builder=CalendarContract.Instances.CONTENT_URI.buildUpon();
  ContentUris.appendId(builder,now);ContentUris.appendId(builder,now+7L*86400000L);
  try(Cursor cur=c.getContentResolver().query(builder.build(),columns,null,null,CalendarContract.Instances.BEGIN+" ASC")){
   if(cur!=null&&cur.moveToFirst()){String title=cur.getString(0);long when=cur.getLong(1);return (title==null?"Event":title)+"  ·  "+new SimpleDateFormat("EEE h:mm a",Locale.CANADA).format(new Date(when));}
  }catch(Exception ignored){}return "Nothing upcoming";
 }
 static int battery(Context c){Intent i=c.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(i==null)return 0;int level=i.getIntExtra(BatteryManager.EXTRA_LEVEL,0),scale=i.getIntExtra(BatteryManager.EXTRA_SCALE,100);return scale==0?0:level*100/scale;}
 static String storage(){android.os.StatFs s=new android.os.StatFs(android.os.Environment.getDataDirectory().getPath());long total=s.getTotalBytes(),free=s.getAvailableBytes();return Math.round((total-free)/1073741824.0)+" / "+Math.round(total/1073741824.0)+" GB";}
 static class Art {
  Canvas c;Paint p=new Paint(3);Art(Canvas canvas){c=canvas;}
  void rect(float x,float y,float w,float h,int color,float r){p.setColor(color);p.setStyle(Paint.Style.FILL);p.setShader(null);c.drawRoundRect(x,y,x+w,y+h,r,r,p);}
  void text(String s,float x,float y,int size,int color,boolean bold){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create(bold?"sans-serif-medium":"sans-serif-light",Typeface.NORMAL));c.drawText(s,x,y,p);}
  void line(float x,float y,float xx,float yy,int color,int width){p.setShader(null);p.setColor(color);p.setStrokeWidth(width);c.drawLine(x,y,xx,yy,p);}
  void ring(float x,float y,float r,int pct,int color){p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(12);p.setStrokeCap(Paint.Cap.ROUND);p.setColor(0xff354353);c.drawArc(x-r,y-r,x+r,y+r,-90,360,false,p);p.setColor(color);c.drawArc(x-r,y-r,x+r,y+r,-90,Math.max(0,Math.min(100,pct))*3.6f,false,p);p.setStyle(Paint.Style.FILL);}
 }
 static Bitmap paint(Context ctx,Weather weather){
  Bitmap b=Bitmap.createBitmap(W,H,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);Art a=new Art(c);
  a.rect(0,0,W,H,0xff09121e,52);
  a.rect(22,22,856,215,0xff172335,40);
  a.text("O K A N A G A N   L I V I N G",55,75,21,GOLD,true);
  a.text("KELOWNA  /  BRITISH COLUMBIA",55,112,18,MUTED,false);
  a.text(weather.ready?weather.temp+"°":"—",52,201,90,WHITE,false);
  a.text(weather.label,255,177,29,WHITE,false);
  a.text("LOCAL WEATHER",255,207,18,MUTED,false);
  Calendar cal=Calendar.getInstance();Date date=cal.getTime();
  a.rect(22,254,856,345,0xff141f2e,40);
  a.text(MONTH.format(date).toUpperCase(Locale.CANADA),55,306,26,GOLD,true);
  String[] weekdays={"S","M","T","W","T","F","S"};
  for(int j=0;j<7;j++)a.text(weekdays[j],67+j*115,351,21,MUTED,true);
  Calendar first=(Calendar)cal.clone();first.set(Calendar.DAY_OF_MONTH,1);int start=first.get(Calendar.DAY_OF_WEEK)-1;int count=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
  for(int d=1;d<=count;d++){int idx=start+d-1;int row=idx/7,col=idx%7;int x=67+col*115,y=405+row*36;
   if(d==cal.get(Calendar.DAY_OF_MONTH))a.rect(x-14,y-26,54,34,0xffb7cbe0,15);
   a.text(""+d,x,y,22,d==cal.get(Calendar.DAY_OF_MONTH)?0xff0b1421:WHITE,d==cal.get(Calendar.DAY_OF_MONTH));}
  a.rect(22,615,410,235,0xff172335,40);a.rect(449,615,429,235,0xff172335,40);
  a.text("LOCAL TIME",53,668,20,MUTED,true);a.text(TIME.format(date),53,743,68,WHITE,false);a.text(DATE.format(date),53,792,23,GOLD,false);
  int charge=battery(ctx);a.text("DEVICE",477,668,20,MUTED,true);a.ring(553,754,48,charge,0xff92d6ba);a.text(charge+"%",510,766,31,WHITE,true);
  a.text("STORAGE",650,728,19,MUTED,true);a.text(storage(),650,766,22,WHITE,false);
  a.rect(22,868,856,207,0xff172335,40);a.text("NEXT ON YOUR CALENDAR",55,925,21,GOLD,true);
  String event=nextEvent(ctx);if(event.length()>39)event=event.substring(0,38)+"…";a.text(event,55,980,28,WHITE,false);
  a.line(55,1008,845,1008,0xff354457,2);a.text("TAP TO OPEN  ·  REFRESH WEATHER IN APP",55,1044,17,MUTED,false);
  return b;
 }
 static void update(Context c,AppWidgetManager mgr,int id,Weather w){
  RemoteViews views=new RemoteViews(c.getPackageName(),R.layout.dashboard_widget);
  views.setImageViewBitmap(R.id.dashboard_image,paint(c,w));
  Intent open=new Intent(c,MainActivity.class);
  PendingIntent pi=PendingIntent.getActivity(c,id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  views.setOnClickPendingIntent(R.id.open_dashboard,pi);mgr.updateAppWidget(id,views);
 }
 static void refresh(Context c,boolean force){
  new Thread(()->{Weather w=weather(c,force);AppWidgetManager mgr=AppWidgetManager.getInstance(c);
   int[] ids=mgr.getAppWidgetIds(new ComponentName(c,DashboardWidget.class));for(int id:ids)try{update(c,mgr,id,w);}catch(Exception ignored){}
  }).start();
 }
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){refresh(c,false);}
 @Override public void onReceive(Context c,Intent i){super.onReceive(c,i);if(REFRESH.equals(i.getAction())||Intent.ACTION_BATTERY_CHANGED.equals(i.getAction()))refresh(c,true);}
}