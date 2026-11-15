package com.example.galaxystudy;

import android.app.*;
import android.content.*;
import android.content.pm.ServiceInfo;
import android.graphics.*;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.nl.translate.*;
import java.nio.ByteBuffer;
import java.util.regex.*;

public class CaptureService extends Service {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private MediaProjection projection;
 private ImageReader reader;
 private android.hardware.display.VirtualDisplay display;
 private WindowManager windowManager;
 private TextView overlay;
 private android.view.View overlayRoot;
 private Translator translator;
 private com.google.mlkit.vision.text.TextRecognizer recognizer;
 private boolean busy=false, ready=false;
 private String last="";
 private int width,height,density;
 private final Runnable scan=new Runnable(){@Override public void run(){capture();handler.postDelayed(this,1700);}};
 @Override public void onCreate(){super.onCreate();
  NotificationChannel channel=new NotificationChannel("study","화면 번역",NotificationManager.IMPORTANCE_LOW);
  ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
  recognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
  translator=Translation.getClient(new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.KOREAN).build());
  translator.downloadModelIfNeeded().addOnSuccessListener(x->{ready=true;show("학습 단어를 인식 중입니다");}).addOnFailureListener(e->show("번역 모델 다운로드 실패: 인터넷 연결을 확인하세요"));
 }
 @Override public int onStartCommand(Intent intent,int flags,int startId){
  Notification notification=new Notification.Builder(this,"study").setContentTitle("학습 번역 실행 중").setContentText("화면 공유 중 · 알림에서 앱을 열어 중지하세요").setSmallIcon(android.R.drawable.ic_menu_search).setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE)).build();
  if(Build.VERSION.SDK_INT>=29) startForeground(1,notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION); else startForeground(1,notification);
  if(intent==null || !intent.hasExtra("data")){stopSelf();return START_NOT_STICKY;}
  windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);
  DisplayMetricsCompat size=new DisplayMetricsCompat(windowManager);width=size.w;height=size.h;density=size.d;
  setupOverlay();
  MediaProjectionManager mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
  Intent captureData=Build.VERSION.SDK_INT>=33?intent.getParcelableExtra("data",Intent.class):intent.getParcelableExtra("data");
  projection=mgr.getMediaProjection(intent.getIntExtra("result",-1),captureData);
  if(projection==null){stopSelf();return START_NOT_STICKY;}
  projection.registerCallback(new MediaProjection.Callback(){@Override public void onStop(){stopSelf();}},handler);
  reader=ImageReader.newInstance(width,height,android.graphics.PixelFormat.RGBA_8888,2);
  display=projection.createVirtualDisplay("study-screen",width,height,density,DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,reader.getSurface(),null,handler);
  handler.postDelayed(scan,1500);
  return START_NOT_STICKY;
 }
 private void setupOverlay(){
  LinearLayout panel=new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(20,12,20,12);panel.setBackgroundColor(0xEDE8F3FF);
  overlay=new TextView(this);overlay.setTextColor(0xFF10223B);overlay.setTextSize(17);overlay.setText("번역 준비 중...");panel.addView(overlay);
  Button close=new Button(this);close.setText("번역 종료");panel.addView(close);close.setOnClickListener(v->stopSelf());
  WindowManager.LayoutParams lp=new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,android.graphics.PixelFormat.TRANSLUCENT);
  lp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;lp.y=80;
  windowManager.addView(panel,lp);overlayRoot=panel;
 }
 private void show(String message){handler.post(()->{if(overlay!=null)overlay.setText(message);});}
 private void capture(){if(busy||!ready||reader==null)return;Image image=reader.acquireLatestImage();if(image==null)return;busy=true;
  try{Image.Plane plane=image.getPlanes()[0];ByteBuffer buf=plane.getBuffer();int pixelStride=plane.getPixelStride(),rowStride=plane.getRowStride();int extra=(rowStride-pixelStride*width)/pixelStride;
   Bitmap full=Bitmap.createBitmap(width+extra,height,Bitmap.Config.ARGB_8888);full.copyPixelsFromBuffer(buf);image.close();
   Bitmap cropped=Bitmap.createBitmap(full,0,0,width,height);full.recycle();
   recognizer.process(InputImage.fromBitmap(cropped,0)).addOnSuccessListener(result->{
    // Keep only a prominent single English word; never read or select answer choices.
    String word="";
    for(com.google.mlkit.vision.text.Text.TextBlock block:result.getTextBlocks()){
     for(com.google.mlkit.vision.text.Text.Line line:block.getLines()){
      String s=line.getText().trim();
      if(s.matches("[A-Za-z][A-Za-z'-]{2,24}")){word=s.toLowerCase(java.util.Locale.ROOT);break;}
     }if(!word.isEmpty())break;
    }
    if(word.isEmpty()){busy=false;return;}
    if(word.equals(last)){busy=false;return;}last=word;
    final String chosen=word;
    translator.translate(chosen).addOnSuccessListener(korean->show(chosen+"  →  "+korean)).addOnFailureListener(e->show("번역 오류: "+chosen)).addOnCompleteListener(t->busy=false);
   }).addOnFailureListener(e->busy=false);
  }catch(Exception e){image.close();busy=false;}
 }
 @Override public void onDestroy(){handler.removeCallbacks(scan);if(display!=null)display.release();if(reader!=null)reader.close();if(projection!=null)projection.stop();if(overlayRoot!=null&&windowManager!=null)windowManager.removeView(overlayRoot);if(translator!=null)translator.close();if(recognizer!=null)recognizer.close();super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
 private static class DisplayMetricsCompat {int w,h,d;DisplayMetricsCompat(WindowManager wm){android.util.DisplayMetrics m=new android.util.DisplayMetrics();wm.getDefaultDisplay().getRealMetrics(m);w=m.widthPixels;h=m.heightPixels;d=m.densityDpi;}}
}
