package cn.hbu.hebeiuniversity.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.hbu.hebeiuniversity.Adapter.ImageListAdapter;
import cn.hbu.hebeiuniversity.R;

public class ImageFragment extends Fragment {

    private ImageListAdapter mAdapter;

    private int[] idRes = {R.drawable.photo1, R.drawable.photo2, R.drawable.photo3,
            R.drawable.photo4, R.drawable.photo5, R.drawable.photo6,
            R.drawable.photo7, R.drawable.photo8, R.drawable.photo9,
            R.drawable.photo10, R.drawable.photo11, R.drawable.photo12,
            R.drawable.photo13, R.drawable.photo14, R.drawable.photo15,
            R.drawable.photo16, R.drawable.photo17};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(
                R.id.fragment_list_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        if(recyclerView != null){
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);

            mAdapter = new ImageListAdapter(idRes);
            recyclerView.setAdapter(mAdapter);
        }


        return view;
    }
}
