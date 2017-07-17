package jason.lubo.com.recorddingdemo;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


import jason.lubo.com.recorddingdemo.adapter.ReAdapter;
import jason.lubo.com.recorddingdemo.model.Videomodel;
import jason.lubo.com.recorddingdemo.utls.FileUtils;

public class VideoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ReAdapter reAdapter;
    private List<Videomodel> videolist=new ArrayList<Videomodel>();

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
       reAdapter.setData(videolist);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initui();
        initdata();

    }
    public  void getVideoFile(final List<Videomodel> list, File file) {// 获得视频文件

        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8")
                            || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v")
                            || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")) {
                        Videomodel vi = new Videomodel();
                        vi.setVideoname(file.getName());
                        vi.setVideopath(file.getAbsolutePath());
                        list.add(vi);
                        handler.sendEmptyMessage(1);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });

    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            getVideoFile(videolist, Environment.getExternalStorageDirectory());
        }
    };
    private void initdata(){
   new Thread(runnable).start();



    }
  private void   initui(){
      recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
      LinearLayoutManager layoutManager=new LinearLayoutManager(this);
      layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
      recyclerView.setLayoutManager(layoutManager);
     reAdapter=new ReAdapter(videolist,VideoActivity.this);
      recyclerView.setAdapter(reAdapter);


  }
}
