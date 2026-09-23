package com.okanaganliving;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.graphics.*;
import android.os.Handler;
import java.util.Calendar;
import java.util.Random;
public class LakeWallpaper extends WallpaperService {
 @Override public Engine onCreateEngine(){return new LakeEngine();}
 class LakeEngine extends Engine {
  final Handler handler=new Handler(); final Paint p=new Paint(3); boolean visible=false; int w=1080,h=2400;long tick=0;
  final Runnable frame=new Runnable(){@Override public void run(){draw();if(visible)handler.postDelayed(this,80);}};
  @Override public void onVisibilityChanged(boolean v){visible=v;handler.removeCallbacks(frame);if(v)frame.run();}
  @Override public void onSurfaceChanged(SurfaceHolder holder,int format,int width,int height){super.onSurfaceChanged(holder,format,width,height);w=width;h=height;draw();}
  @Override public void onSurfaceDestroyed(SurfaceHolder holder){visible=false;handler.removeCallbacks(frame);super.onSurfaceDestroyed(holder);}
  @Override public void onDestroy(){visible=false;handler.removeCallbacks(frame);super.onDestroy();}
  void color(int c){p.setShader(null);p.setColor(c);p.setStyle(Paint.Style.FILL);p.setStrokeWidth(1);}
  int rgb(int r,int g,int b){return Color.rgb(r,g,b);}
  void gradient(Canvas c,float top,float bottom,int a,int b){p.setShader(new LinearGradient(0,top,0,bottom,a,b,Shader.TileMode.CLAMP));c.drawRect(0,top,w,bottom,p);p.setShader(null);}
  void mountain(Canvas c,float y,int shade,float[] pts){color(shade);Path path=new Path();path.moveTo(0,h);path.lineTo(0,y+pts[0]*h);for(int i=1;i<pts.length;i++)path.lineTo((float)i/(pts.length-1)*w,y+pts[i]*h);path.lineTo(w,h);path.close();c.drawPath(path,p);}
  void draw(){SurfaceHolder holder=getSurfaceHolder();Canvas c=null;try{c=holder.lockCanvas();if(c==null)return;w=c.getWidth();h=c.getHeight();Calendar now=Calendar.getInstance();int hour=now.get(Calendar.HOUR_OF_DAY);float y=h*.56f;boolean night=hour<6||hour>=20;boolean dusk=(hour>=17&&hour<20)||(hour>=6&&hour<8);
   int skyTop=night?rgb(3,8,21):dusk?rgb(27,27,51):rgb(36,66,100);int skyBottom=night?rgb(40,53,73):dusk?rgb(178,112,105):rgb(153,185,196);
   gradient(c,0,y,skyTop,skyBottom);
   if(night){Random stars=new Random(817);for(int i=0;i<72;i++){float sx=stars.nextFloat()*w,sy=stars.nextFloat()*y*.83f; color(Color.argb(80+stars.nextInt(110),232,239,255));c.drawCircle(sx,sy,Math.max(0.6f,w*.001f)*(.4f+stars.nextFloat()),p);}}
   float sunX=w*.76f,sunY=y*.27f;float rad=w*(night?.043f:.085f);color(night?rgb(227,237,241):rgb(255,222,173));p.setShadowLayer(rad*.8f,0,0,night?0x99b7cbe9:0x99ffd9a0);c.drawCircle(sunX,sunY,rad,p);p.clearShadowLayer();
   mountain(c,y-h*.22f,night?rgb(19,28,43):rgb(69,85,99),new float[]{.09f,.04f,.11f,-.03f,.06f,-.07f,.01f,-.02f,.08f,.02f});
   mountain(c,y-h*.09f,night?rgb(9,19,33):rgb(35,60,76),new float[]{.05f,.01f,.04f,-.02f,.06f,-.04f,.02f,.07f,-.01f,.04f});
   gradient(c,y,h,night?rgb(21,39,57):dusk?rgb(65,74,93):rgb(58,103,128),night?rgb(3,13,25):rgb(12,44,66));
   tick++;float t=tick*.095f;Random rand=new Random(47);for(int i=0;i<105;i++){float fy=y+(h-y)*rand.nextFloat();float depth=(fy-y)/(h-y);float cx=rand.nextFloat()*w;float len=w*(.008f+.075f*depth);float shimmer=(float)Math.sin(t+i*1.7f)*w*.012f*depth;int alpha=(int)(12+38*(1-depth)+22*Math.max(0,Math.sin(t*.5+i)));color(Color.argb(Math.min(90,alpha),night?176:223,night?198:218,night?213:203));p.setStrokeWidth(Math.max(1,depth*2.5f));c.drawLine(cx+shimmer,fy,cx+len+shimmer,fy,p);}
   for(int i=0;i<30;i++){float depth=(i+1)/31f;float yy=y+depth*(h-y)*.67f;float half=w*.18f*depth;float wobble=(float)Math.sin(t+i*.8f)*w*.015f; color(Color.argb((int)(40*(1-depth)),night?211:255,night?223:207,night?222:150));p.setStrokeWidth(Math.max(1,depth*4));c.drawLine(sunX-half+wobble,yy,sunX+half+wobble,yy,p);}
   color(0x99050d19);c.drawRect(0,h*.91f,w,h,p);
  }catch(Exception ignored){}finally{if(c!=null)try{holder.unlockCanvasAndPost(c);}catch(Exception ignored){}}}
 }
}