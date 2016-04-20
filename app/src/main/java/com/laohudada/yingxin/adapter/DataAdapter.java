package com.laohudada.yingxin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laohudada.yingxin.R;
import com.laohudada.yingxin.base.BaseRecyclerAdapter;
import com.laohudada.yingxin.http.mdel.ItemModel;

import java.util.ArrayList;

/**
 * Created by tao on 2016/1/27.
 */
public class DataAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder, ItemModel> {

    public DataAdapter(ArrayList<ItemModel> mDataList) {
        super(mDataList);
    }

    @Override
    public BaseRecyclerViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder holder;
        View view = inflater.inflate(R.layout.item_text, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, ItemModel data) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvInfo.setText(data.title);
    }

    public class ViewHolder extends BaseRecyclerViewHolder {

        private TextView tvInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }
}
