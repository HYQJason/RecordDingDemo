package jason.lubo.com.recorddingdemo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    SurfaceView surfaceView;
    Button switchbutton;
    Camera camera;
    SurfaceHolder msurfaceHolder;

    private static String TAG="BUG";

private int cameraId=0;
   private int width=480;
    private   int height=800;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }
    private void initui(){
        surfaceView= (SurfaceView) this.findViewById(R.id.surfaceview);
        switchbutton= (Button) this.findViewById(R.id.switchbutton);
        SurfaceHolder surfaceHolder= surfaceView.getHolder();
       surfaceHolder.addCallback(this);
    }
private void initapi(){
    camera=Camera.open(cameraId);
    try {
        camera.setPreviewDisplay(msurfaceHolder);
        Camera.Parameters parameters= camera.getParameters();//获得相机属性设置
        parameters.setPreviewSize(width,height);//预览的宽高
        parameters.setPreviewFormat(ImageFormat.NV21);//预览的格式
        parameters.setPictureSize(width,height);
        parameters.setRotation(90);
        camera.setDisplayOrientation(90);
        List<Camera.Size>  sizeList=  parameters.getSupportedPictureSizes();
        if(sizeList.size()>1){
//摄像头支持的预览尺寸
            Iterator<Camera.Size> sizeIterator= sizeList.iterator();
            while (sizeIterator.hasNext()){
                Camera.Size csz=    sizeIterator.next();
                Log.d(TAG,"宽========"+csz.height);
                Log.d(TAG,"宽========"+csz.width);

            }
        }

//获得摄像头支持的数据格式
        List<Integer> formatslist= parameters.getSupportedPreviewFormats();

        for (Integer integer: formatslist){
            Log.d(TAG,String.valueOf(integer));

        }
        camera.setParameters(parameters);
        camera.setPreviewCallback(cb);
        camera.startPreview();
    } catch (IOException e) {
        e.printStackTrace();
        Log.d(TAG,"IOException==="+e.getMessage());
    }




}
  /*  Android中的摄像头Camera提供了两个方式回调接口来获取每一帧数据：
    第一种方式：setPreviewCallback方法，设置回调接口：PreviewCallback
    在回调方法：onPreviewFrame(byte[] data, Camera camera) 中处理每一帧数据
    第二种方式：setPreviewCallbackWithBuffer方法，同样设置回调接口：
    PreviewCallback，不过还需要一个方法配合使用：addCallbackBuffer，这个方法接受一个byte数组。
    第二种方式和第一种方式唯一的区别：
    第一种方式是onPreviewFrame回调方法会在每一帧数据准备好了就调用，但是第二种方式是在需要在前一帧的onPreviewFrame方法中调用addCallbackBuffer方法，下一帧的onPreviewFrame才会调用，同时addCallbackBuffer方法的参数的byte数据就是每一帧的原数据。所以这么一看就好理解了，就是第一种方法的onPreviewFrame调用是不可控制的，就是每一帧数据准备好了就回调，
    但是第二种方法是可控的，我们通过addCallbackBuffer的调用来控制onPreviewFrame的回调机制。*/

    /*注意：
    因为第二种方式在调用的时候有点注意的地方：
            1》在调用Camera.startPreview()接口前，我们需要setPreviewCallbackWithBuffer，
    而setPreviewCallbackWithBuffer之前我们需要重新addCallbackBuffer，因为setPreviewCallbackWithBuffer
    使用时需要指定一个字节数组作为缓冲区，用于预览图像数据 即addCallbackBuffer，然后你在onPerviewFrame中的data才会有值。
            2》从上面看来，我们设置addCallbackBuffer的地方有两个，一个是在startPreview之前，
    一个是在onPreviewFrame中，这两个都需要调用，如果在onPreviewFrame中不调用，那么，就无法继续回调到onPreviewFrame中了。*/

    Camera.PreviewCallback cb =new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
   Camera.Size size= camera.getParameters().getPreviewSize();
            Log.d(TAG,"每一帧的大小，宽"+size.width+"高"+size.height);
            YuvImage yuvImage=new YuvImage(bytes,ImageFormat.NV16,size.width,size.height,null);
            Bitmap bitmap=null;
            if(yuvImage!=null){

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
//压缩图片
                yuvImage.compressToJpeg(new Rect(0,0,size.width,size.height),80,stream);

              bitmap=  BitmapFactory.decodeByteArray(stream.toByteArray(),0,stream.size());

                //这里可以处理bitmap，加水印等
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
 Canvas canvas= msurfaceHolder.lockCanvas();

                canvas.drawBitmap(bitmap,0,0,new Paint());

                msurfaceHolder.unlockCanvasAndPost(canvas);
            }


        }
    };

private void camerainfo(){
    Camera.CameraInfo info=new Camera.CameraInfo();

  int  cameranumber=  Camera.getNumberOfCameras();
    Log.d(TAG,"摄像头的数量=========="+cameranumber);
    for(int i=0;i<cameranumber;i++){
Camera.getCameraInfo(i,info);
Log.d(TAG,"orientation======="+info.orientation+"//facing==========="+info.facing);
        if(info.facing== Camera.CameraInfo.CAMERA_FACING_FRONT){

        }else if(info.facing== Camera.CameraInfo.CAMERA_FACING_BACK){

        }



    }


}
    private void destroyCamera(){
if(camera!=null){
    camera.setPreviewCallback(null);//清空接口回调
    camera.stopPreview();//停止预览
    camera.release();//释放
    camera=null;


}

    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        msurfaceHolder=surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
