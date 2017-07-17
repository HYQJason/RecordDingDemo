package jason.lubo.com.recorddingdemo.adapter;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import jason.lubo.com.recorddingdemo.Ipms.ReItemClickListener;
import jason.lubo.com.recorddingdemo.Ipms.ReItemLongClickListener;
import jason.lubo.com.recorddingdemo.R;
import jason.lubo.com.recorddingdemo.VideoActivity;
import jason.lubo.com.recorddingdemo.model.Videomodel;

/**
 * Created by WY on 2017/6/28.
 */

public class ReAdapter extends RecyclerView.Adapter <ReViewHolder>{
    private ReItemClickListener itemClickListener;
    private ReItemLongClickListener itemLongClickListener;
    List<Videomodel> mData;
private Activity act;
   public ReAdapter(List<Videomodel> list, Activity activity){
        this.mData=list;
this.act=activity;

    }

    public void setData(List<Videomodel>  vlist){
        mData=vlist;
        notifyDataSetChanged();


    }
    @Override
    public ReViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=  LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item,parent,false);
        ReViewHolder viewHolder=new ReViewHolder(view);
        viewHolder.videoView.setMediaController(new MediaController(parent.getContext()));
        //播放完成回调
        viewHolder. videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("BUG","播放完成");
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ReViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ViewGroup.LayoutParams layoutParams= holder.itemView.getLayoutParams();
        layoutParams.height= 300;
        layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
     //   holder.itemView.setLayoutParams(layoutParams);

holder.itemView.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View view) {
        holder.videoimage.setVisibility(View.GONE);
        holder.videoView.start();
    }
});

        holder.videoView.setVideoPath(mData.get(position).getVideopath());
        Glide.with( act )
                .load( Uri.fromFile( new File( mData.get(position).getVideopath() ) ) )
                .into( holder.videoimage );
        holder.nametext.setText(mData.get(position).getVideoname());

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }



    public void setOnClickListener(ReItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    public void setOnLongClickListener(ReItemLongClickListener itemLongClickListener){
        this.itemLongClickListener=itemLongClickListener;
    }

}
