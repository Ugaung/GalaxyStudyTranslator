package com.example.galaxystudy;

import android.app.*;
import android.content.*;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
 private static final int CAPTURE = 101;
 private MediaProjectionManager manager;
 @Override public void onCreate(Bundle b) { super.onCreate(b);
  manager=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
  LinearLayout root=new LinearLayout(this); root.setOrientation(1); root.setPadding(32,45,32,24);
  TextView title=new TextView(this); title.setText("학습 화면 번역 도우미"); title.setTextSize(24); root.addView(title);
  TextView description=new TextView(this); description.setText("일반 학습용 도구입니다. 화면을 공유하면 영어 단어를 인식해 한국어 뜻을 표시합니다. 첫 실행 시 번역 모델 다운로드가 필요합니다. 시험·평가 중에는 사용하지 마세요.\n\n화면의 영어 문구가 여러 개일 경우 첫 번째 단어를 번역합니다."); description.setTextSize(16); root.addView(description);
  Button start=new Button(this); start.setText("화면 인식 시작"); root.addView(start);
  Button stop=new Button(this); stop.setText("중지"); root.addView(stop);
  start.setOnClickListener(v->{if(!Settings.canDrawOverlays(this)){ Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())); startActivity(i); Toast.makeText(this,"다른 앱 위에 표시 권한을 허용한 후 다시 시작하세요",Toast.LENGTH_LONG).show(); return; }
   startActivityForResult(manager.createScreenCaptureIntent(),CAPTURE);
  });
  stop.setOnClickListener(v->stopService(new Intent(this,CaptureService.class)));
  setContentView(root);
 }
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==CAPTURE&&result==RESULT_OK&&data!=null){Intent i=new Intent(this,CaptureService.class);i.putExtra("result",result);i.putExtra("data",data);startForegroundService(i);Toast.makeText(this,"학습 화면 번역을 시작합니다",Toast.LENGTH_SHORT).show();}}
}
