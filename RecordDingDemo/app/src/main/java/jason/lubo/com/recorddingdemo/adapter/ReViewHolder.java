package jason.lubo.com.recorddingdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import jason.lubo.com.recorddingdemo.Ipms.ReItemClickListener;
import jason.lubo.com.recorddingdemo.Ipms.ReItemLongClickListener;
import jason.lubo.com.recorddingdemo.R;

/**
 * Created by WY on 2017/6/28.
 */

public class ReViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnLongClickListener {
    public VideoView videoView;
    public TextView nametext;
    public ImageView videoimage;
    private ReItemClickListener itemClickListener;
    private ReItemLongClickListener itemLongClickListener;
    public ReViewHolder(View itemView) {
        super(itemView);
        nametext=itemView.findViewById(R.id.natextview);
        videoView=itemView.findViewById(R.id.videoview);
        videoimage=itemView.findViewById(R.id.videoimage);
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void setOnClickListener(ReItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    public void setOnLongClickListener(ReItemLongClickListener itemLongClickListener){
        this.itemLongClickListener=itemLongClickListener;
    }
    @Override
    public void onClick(View view) {
    if (itemClickListener!=null){
        itemClickListener.OnReItemClickListener(view,getLayoutPosition());
    }

    }

    @Override
    public boolean onLongClick(View view) {
        if(itemLongClickListener!=null){
            itemLongClickListener.OnReItemLongClickListener(view,getLayoutPosition());
        }
        return false;
    }
}

