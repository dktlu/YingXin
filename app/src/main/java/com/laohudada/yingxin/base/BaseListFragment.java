package com.laohudada.yingxin.base;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laohudada.yingxin.AppContext;
import com.laohudada.yingxin.R;
import com.laohudada.yingxin.cache.CacheManager;
import com.laohudada.yingxin.http.mdel.BaseApiResponse;
import com.laohudada.yingxin.recyclerview.EndlessRecyclerOnScrollListener;
import com.laohudada.yingxin.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.laohudada.yingxin.recyclerview.RecyclerViewStateUtils;
import com.laohudada.yingxin.utils.NetUtils;
import com.laohudada.yingxin.utils.StringUtils;
import com.laohudada.yingxin.widget.EmptyLayout;
import com.laohudada.yingxin.widget.LoadingFooter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by tao on 2016/1/22.
 */
public abstract class BaseListFragment<T extends BaseApiResponse> extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwiperefreshlayout;
    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    protected int mStoreEmptyState = -1;
    protected int mCurrentPage = 0;
    protected HeaderAndFooterRecyclerViewAdapter footerRecyclerViewAdapter;
    protected BaseRecyclerAdapter mAdapter;
    private int mCurrentCounter = 0;
    /**服务器端一共多少条数据*/
    private int totalCounter = 0;

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;
    private AsyncTask<String, Void, BaseApiResponse<List<T>>> mCacheTask;
    private ParserTask mParserTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        init(savedInstanceState);
        return view;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mSwiperefreshlayout.setOnRefreshListener(this);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage = 0;
                mState = STATE_REFRESH;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });

        if (mAdapter != null) {
            setAdapter();
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mAdapter = getBaseAdapter();
            setAdapter();
            if (requestDataIfViewCreated()) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                mState = STATE_NONE;
                requestData(false);
            }
        }
        if (mStoreEmptyState != -1) {
            mErrorLayout.setErrorType(mStoreEmptyState);
        }
    }

    private void setAdapter() {
        footerRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        mRecyclerview.setAdapter(footerRecyclerViewAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.addOnScrollListener(mOnScrollListener);
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

            LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerview);
            if (state == LoadingFooter.State.Loading) {
                return;
            }

            if (mCurrentCounter < totalCounter) {
                // loading more
                RecyclerViewStateUtils.setFooterViewState(getActivity(), mRecyclerview, REQUEST_COUNT, LoadingFooter.State.Loading, null);
            } else {
                //the end
                RecyclerViewStateUtils.setFooterViewState(getActivity(), mRecyclerview, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
            }
        }
    };

    protected String getCacheKeyPrefix() {
        return null;
    }

    protected BaseApiResponse<List<T>> readList(String seri) {
        return null;
    }

    protected BaseApiResponse<List<T>> parseList(InputStream is) throws Exception{
        return null;
    }

    protected void sendRequestData() {

    }

    private String getCacheKey() {
        return new StringBuilder(getCacheKeyPrefix()).append("_")
                .append(mCurrentPage).toString();
    }

    protected boolean requestDataIfViewCreated() {
        return true;
    }

    /**
     * 获取列表数据
     * @param refresh
     */
    protected void requestData(boolean refresh) {
        String key = getCacheKey();
        if (isReadCacheData(refresh)) {
            readCacheData(key);
        } else {
            //获取新数据
            sendRequestData();
        }
    }

    /**
     * 是否到时间刷新
     * @return
     */
    private boolean onTimeRefresh() {
        String lastRefreshTime = AppContext.getLaseRefreshTime(getCacheKey());
        String currTime = StringUtils.getCurTimeStr();
        long diff = StringUtils.calDateDifferent(lastRefreshTime, currTime);
        return needAutoRefresh() && diff > getAutoRefreshTime();
    }

    /**
     * 是否需要自动刷新
     * @return
     */
    protected boolean needAutoRefresh() {
        return true;
    }

    /**
     * 自动刷新时间
     * 默认：半天时间
     * @return
     */
    protected long getAutoRefreshTime() {
        return 12 * 60 * 60;
    }

    private void readCacheData(String cacheKey) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    private class CacheTask extends AsyncTask<String, Void, BaseApiResponse<List<T>>> {

        private final WeakReference<Context> mContext;

        public CacheTask(Context context) {
            this.mContext = new WeakReference<Context>(context);
        }

        @Override
        protected BaseApiResponse<List<T>> doInBackground(String... params) {
            String strData = CacheManager.readObject(mContext.get(), params[0]);
            if (strData == null) {
                return null;
            } else {
                return readList(strData);
            }
        }

        @Override
        protected void onPostExecute(BaseApiResponse<List<T>> tBaseApiResponse) {
            super.onPostExecute(tBaseApiResponse);
            if (tBaseApiResponse != null) {
                executeOnLoadDataSuccess(tBaseApiResponse.getRetData());
            } else {
                executeOnLoadDataError(null);
            }
            executeOnLoadFinish();
        }
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> mContext;
        private final String strData;
        private final String key;

        public SaveCacheTask(Context context, String strData, String key) {
            this.mContext = new WeakReference<Context>(context);
            this.strData = strData;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), strData, key);
            return null;
        }
    }

    protected void executeOnLoadDataSuccess(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (mCurrentPage == 0) {
            mAdapter.clear();
        }
        totalCounter = mAdapter.getItemCount() + data.size();
        if ((mAdapter.getItemCount() + data.size()) == 0) {
            RecyclerViewStateUtils.setFooterViewState(getActivity(),
                    mRecyclerview, REQUEST_COUNT, LoadingFooter.State.Normal, null);
        } else if (data.size() == 0
                || (data.size() < REQUEST_COUNT && mCurrentPage == 0)) {
            RecyclerViewStateUtils.setFooterViewState(getActivity(),
                    mRecyclerview, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
        } else {
            RecyclerViewStateUtils.setFooterViewState(getActivity(),
                    mRecyclerview, REQUEST_COUNT, LoadingFooter.State.Loading, null);
        }
        footerRecyclerViewAdapter.notifyDataSetChanged();
        mAdapter.addItem(data);

        //判断等于是因为最后有一项是listview的状态
        if (footerRecyclerViewAdapter.getItemCount() == 1) {
            if (needShowEmptyNoData()) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA);
            } else {
                RecyclerViewStateUtils.setFooterViewState(getActivity(),
                        mRecyclerview, REQUEST_COUNT, LoadingFooter.State.Normal, null);
                footerRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void executeOnLoadDataError(String error) {
        if (mCurrentPage == 0
                && !CacheManager.isExistDataCache(getActivity(), getCacheKey())) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            mCurrentPage--;
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            RecyclerViewStateUtils.setFooterViewState(getActivity(),
                    mRecyclerview, REQUEST_COUNT, LoadingFooter.State.NetWorkError, null);
            footerRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 完成刷新
     */
    protected void executeOnLoadFinish() {
        setSwiperefreshLoadedState();
        mState = STATE_NONE;
    }

    /**
     * 是否需要隐藏recyclerview，显示无数据状态
     * @return
     */
    protected boolean needShowEmptyNoData() {
        return true;
    }

    private void executeParserTask(String data) {
        cancelParserTask();
        mParserTask = new ParserTask(data);
        mParserTask.execute();
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    private class ParserTask extends AsyncTask<Void, Void, String> {

        private final String reponseData;
        private boolean parserError;
        private List<T> list;

        public ParserTask(String reponseData) {
            this.reponseData = reponseData;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                BaseApiResponse<List<T>> data = parseList(new ByteArrayInputStream(
                        reponseData.getBytes()));
                new SaveCacheTask(getActivity(), reponseData, getCacheKey()).execute();
                list = data.getRetData();
            } catch (Exception e) {
                e.printStackTrace();
                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (parserError) {
                readCacheData(getCacheKey());
            } else {
                executeOnLoadDataSuccess(list);
                executeOnLoadFinish();
            }
        }
    }

    /**
     * 判断是否需要读取缓存的数据
     * @param refresh
     * @return
     */
    protected boolean isReadCacheData(boolean refresh) {
        String key = getCacheKey();
        if (!NetUtils.isConnected(getActivity())) {
            return true;
        }
        //第一页若不是主动刷新，缓存存在，优先取缓存的
        if (CacheManager.isExistDataCache(getActivity(), key) && !refresh
                && mCurrentPage == 0) {
            return true;
        }
        //其它页数的，缓存存在以及还没失效，优先取缓存的
        if (CacheManager.isExistDataCache(getActivity(), key)
                && !CacheManager.isCacheDataFailure(getActivity(), key)
                && mCurrentPage != 0) {
            return true;
        }
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_recyclerview;
    }

    @Override
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }
        //设置顶部正在刷新
        setSwipeRefreshLoadingState();
        mCurrentPage = 0;
        mState = STATE_REFRESH;
        requestData(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onTimeRefresh()) {
            onRefresh();
        }
    }

    /**
     * 设置顶部正在加载的状态
     */
    protected void setSwipeRefreshLoadingState() {
        if (mSwiperefreshlayout != null) {
            mSwiperefreshlayout.setRefreshing(true);
            //防止多次重复刷新
            mSwiperefreshlayout.setEnabled(false);
        }
    }

    /**
     *
     * 设置顶部加载完毕的状态
     */
    protected void setSwiperefreshLoadedState() {
        if (mSwiperefreshlayout != null) {
            mSwiperefreshlayout.setRefreshing(false);
            mSwiperefreshlayout.setEnabled(true);
        }
    }

    protected abstract BaseRecyclerAdapter getBaseAdapter();

    @Override
    public void onDestroyView() {
        mStoreEmptyState = mErrorLayout.getErrorState();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        cancelReadCacheTask();
        cancelParserTask();
        super.onDestroy();
    }
}
