package jason.lubo.com.recorddingdemo;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Calendar;

import jason.lubo.com.recorddingdemo.utls.FileUtils;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
  private final   String TAG="BUG";

private SurfaceView SurfaceView;
    private SurfaceHolder surfaceholder;
    private Button videoviewbutton;
    private  Camera camera;
 private    Button startbutton;
 private    Button stopButton;

    private MediaRecorder mediaRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
     //   initapi();


    }

    private void initUI(){
        SurfaceView= (SurfaceView) this.findViewById(R.id.surfaceview);
        startbutton= (Button) this.findViewById(R.id.btstart);
        videoviewbutton= (Button) this.findViewById(R.id.videoviewbutton);
        videoviewbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,VideoActivity.class);
                startActivity(intent);
            }
        });
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // surfaceholder.setType();3.0以下SDK 需要设置

mediaRecorder.start();
            }
        });
        stopButton= (Button) this.findViewById(R.id.btstop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
mediaRecorder.stop();

              //  mediaRecorder.reset();
                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.stopPreview();
                camera.lock();
            }
        });

    }

    private void initapi(){
        mediaRecorder=new MediaRecorder();
        SurfaceHolder   holder= SurfaceView.getHolder();
        holder.addCallback(this);
         camera= Camera.open();
        camera.setDisplayOrientation(90);
        camera.unlock();
        mediaRecorder.setCamera(camera);
      //  mediaRecorder.setOrientationHint(90);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//声音来源麦克风
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//摄像头
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//录制输出格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//录制声音的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        // mediaRecorder.setVideoSize(480,320);//视频的分辨率
        mediaRecorder.setVideoFrameRate(20);//设置视频的捕获帧速率

        mediaRecorder.setOutputFile(FileUtils.getvideopath());//设置视频存储目录

mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {
        Log.d(TAG, "Error: " + i + ", " + i1);
    }
});

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                Log.d(TAG, "onInfo: " + i + ", " + i1);
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG,"surfaceCreated");
this.surfaceholder=surfaceHolder;
        mediaRecorder.setPreviewDisplay(surfaceholder.getSurface());//设置预览的Surface
        try {
            mediaRecorder.prepare();
            Log.d(TAG, " prepare");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, ""+e.getMessage());
        }
        Log.d(TAG, " prepare完成");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.surfaceholder=surfaceHolder;
        Log.d(TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        SurfaceView=null;
        this.surfaceholder=null;
        if (mediaRecorder!=null){
            mediaRecorder.release();
            mediaRecorder=null;
        }
        if(camera!=null){
            camera.release();
            camera=null;
        }
    }
}
