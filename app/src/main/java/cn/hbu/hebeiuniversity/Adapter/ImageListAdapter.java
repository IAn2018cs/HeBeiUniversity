package cn.hbu.hebeiuniversity.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.hbu.hebeiuniversity.R;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {

    private int[] idRes;

    public ImageListAdapter(int[] idRes) {
        this.idRes = idRes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_image_item,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setImageResource(idRes[i]);
    }

    @Override
    public int getItemCount() {
        return idRes == null ? 0 : idRes.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView title;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (ImageView) itemView.findViewById(R.id.list_iv);
        }
    }

}

