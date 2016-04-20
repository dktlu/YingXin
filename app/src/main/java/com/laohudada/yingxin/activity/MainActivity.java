package com.laohudada.yingxin.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.laohudada.yingxin.R;
import com.laohudada.yingxin.adapter.DataAdapter;
import com.laohudada.yingxin.base.BaseActivity;
import com.laohudada.yingxin.http.mdel.ItemModel;
import com.laohudada.yingxin.recyclerview.EndlessRecyclerOnScrollListener;
import com.laohudada.yingxin.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.laohudada.yingxin.recyclerview.RecyclerViewStateUtils;
import com.laohudada.yingxin.recyclerview.RecyclerViewUtils;
import com.laohudada.yingxin.utils.NetUtils;
import com.laohudada.yingxin.utils.T;
import com.laohudada.yingxin.widget.EmptyLayout;
import com.laohudada.yingxin.widget.HeaderLayout;
import com.laohudada.yingxin.widget.LoadingFooter;
import com.laohudada.yingxin.widget.dialog.ConfirmDialogFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogListener {

    @Bind(R.id.layout_error)
    EmptyLayout layoutError;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;

    private ArrayList<ItemModel> dataList;

    /**
     * 服务器端一共多少条数据
     */
    private static final int TOTAL_COUNTER = 64;

    /**
     * 每一页展示多少条数据
     */
    private static final int REQUEST_COUNT = 10;

    /**
     * 已经获取到多少条数据了
     */
    private int mCurrentCounter = 0;

    private HeaderAndFooterRecyclerViewAdapter adapter = null;
    private DataAdapter mDataAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ItemModel item = new ItemModel();
            item.id = i;
            item.title = "item " + i;
            dataList.add(item);
        }

        mCurrentCounter = dataList.size();
        mDataAdapter = new DataAdapter(dataList);
//        mDataAdapter.addItems(dataList);
        adapter = new HeaderAndFooterRecyclerViewAdapter(mDataAdapter);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewUtils.setHeaderView(mRecyclerview, new HeaderLayout(this));
        mRecyclerview.addOnScrollListener(mOnScrollListener);

        adapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                showWaitDialog("加载资源中。。。", true);
            }
        });

        adapter.setOnItemLongClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position, long id) {
                mDialogFactory.showConfirmDialog("activity调起确认框", "我是Activity中的确认框", true, true, MainActivity.this);
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

            LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerview);
            if (state == LoadingFooter.State.Loading) {
                return;
            }

            if (mCurrentCounter < TOTAL_COUNTER) {
                // loading more
                RecyclerViewStateUtils.setFooterViewState(MainActivity.this, mRecyclerview, REQUEST_COUNT, LoadingFooter.State.Loading, null);
                requestData();
            } else {
                //the end
                RecyclerViewStateUtils.setFooterViewState(MainActivity.this, mRecyclerview, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
            }
        }
    };

    /**
     * 模拟请求网络
     */
    private void requestData() {

        new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //模拟一下网络请求失败的情况 测试
                if (NetUtils.isConnected(getApplicationContext())) {
                    mHandler.sendEmptyMessage(-1);
                } else {
                    mHandler.sendEmptyMessage(-3);
                }
            }
        }.start();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        T.showShort(getApplicationContext(), "点击了Activity调起的确认对话框which" + which);
    }

    private class PreviewHandler extends Handler {

        private WeakReference<MainActivity> ref;

        PreviewHandler(MainActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            switch (msg.what) {
                case -1:
                    int currentSize = activity.mDataAdapter.getItemCount();

                    //模拟组装10个数据
                    ArrayList<ItemModel> newList = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }

                        ItemModel item = new ItemModel();
                        item.id = currentSize + i;
                        item.title = "item" + (item.id);

                        newList.add(item);
                    }

//                    mDataAdapter.addItems(newList);
                    dataList.addAll(newList);
                    adapter.notifyDataSetChanged();
                    mCurrentCounter += newList.size();
                    RecyclerViewStateUtils.setFooterViewState(activity.mRecyclerview, LoadingFooter.State.Normal);
                    break;
                case -2:
                    adapter.notifyDataSetChanged();
                    break;
                case -3:
                    RecyclerViewStateUtils.setFooterViewState(activity, activity.mRecyclerview, REQUEST_COUNT, LoadingFooter.State.NetWorkError, activity.mFooterClick);
                    break;
            }
        }
    }

    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewStateUtils.setFooterViewState(MainActivity.this, mRecyclerview, REQUEST_COUNT, LoadingFooter.State.Loading, null);
            requestData();
        }
    };

//    @OnClick(R.id.layout_error)
//    public void ssh() {
//        RequestParams params = new RequestParams(this);
//        params.put("id", "1361653183");
//        HttpRequest.get(Api.BASE_API_URL, params, new MyBaseHttpRequestCallback<TicketDetailResponse>() {
//            @Override
//            public void onLoginSuccess(TicketDetailResponse ticketDetailResponse) {
//                showWaitDialog();
//            }
//
//            @Override
//            public void onLoginFailure(TicketDetailResponse ticketDetailResponse) {
//                Log.e("this", "errMsg = " + ticketDetailResponse.getErrMsg());
//            }
//        });
//        Toast.makeText(this, TDevice.getNetworkType() + "", Toast.LENGTH_SHORT).show();
//    }
}
