package com.dawoo.lotterybox.view.fragment.ssc_b;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.patternlockview.utils.RandomUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dawoo.coretool.util.activity.DensityUtil;
import com.dawoo.lotterybox.ConstantValue;
import com.dawoo.lotterybox.R;
import com.dawoo.lotterybox.bean.playtype.PlayDetailBean;
import com.dawoo.lotterybox.bean.playtype.PlayTypeBean;
import com.dawoo.lotterybox.util.ThreadUtils;
import com.dawoo.lotterybox.view.activity.lottery.ssc.SSCBActivity;
import com.dawoo.lotterybox.view.fragment.BaseFragment;
import com.dawoo.lotterybox.view.view.SpaceItemDecoration;
import com.dawoo.lotterybox.view.view.LotteryBFragmentManager;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.dawoo.lotterybox.view.activity.lottery.BaseLotteryBActivity.changeType;

/**
 * Created by archar on 18-2-27.
 */

public class SSC_LH_Fragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.rv_szp_play_type)
    RecyclerView mRvSzpPlayType;
    private SZPAdapter mAdapter;

    private List<PlayTypeBean.PlayBean> mList = new ArrayList<>();
    private LinkedHashMap<String, List<PlayDetailBean>> mListLinkedHashMap = new LinkedHashMap<>();

    private List<PlayDetailBean> selectedBean = new ArrayList<>();

    public SSC_LH_Fragment() {
    }

    @Override
    public void onDestroyView() {
        RxBus.get().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected void loadData() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_just, container, false);
        unbinder = ButterKnife.bind(this, v);
        RxBus.get().register(this);
        initViews();
        initData();
        initListener();
        return v;
    }


    private void initViews() {
        mAdapter = new SZPAdapter(R.layout.layout_lottery_b_common_item);
        mRvSzpPlayType.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRvSzpPlayType.setAdapter(mAdapter);
    }


    private void initData() {
        selectedBean.clear();
        mList = SSCBDataUtil.initLHData();
        for (PlayTypeBean.PlayBean playBean : mList) {
            mListLinkedHashMap.put(playBean.getPlayTypeName(), SSCBDataUtil.init_LH_Data(playBean.getPlayTypeName()));
        }
        mAdapter.setNewData(mList);
    }

    private void initListener() {

    }


    @Subscribe(tags = {@Tag(LotteryBFragmentManager.TOP_FRGMENT)})
    public void clearSelectedData(String id) {
        if (!id.equals(this.getId()) && selectedBean.size() > 0) {
            ThreadUtils.newThread(new Runnable() {
                @Override
                public void run() {
                    for (PlayTypeBean.PlayBean playBean : mList) {
                        mListLinkedHashMap.put(playBean.getPlayTypeName(), SSCBDataUtil.clearSelected(mListLinkedHashMap.get(playBean.getPlayTypeName())));
                    }
                    selectedBean.clear();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                }
            });
        }
    }

    private class SZPAdapter extends BaseQuickAdapter {

        public SZPAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            PlayTypeBean.PlayBean playBean = (PlayTypeBean.PlayBean) item;
            helper.setText(R.id.tv_play_type_name, playBean.getPlayTypeName());
            helper.getView(R.id.tv_play_type_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SSCBActivity) getActivity()).showHowPlayDialog(playBean);
                }
            });

            RecyclerView recyclerView = helper.getView(R.id.rv_detail);
            SZPDetailAdapter szpDetailAdapter = new SZPDetailAdapter(R.layout.click_play_type_item_layout, helper.getAdapterPosition());
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            if (recyclerView.getItemDecorationCount() == 0) {
                recyclerView.addItemDecoration(new SpaceItemDecoration(DensityUtil.dp2px(mContext, (float) 0.20)));
            }
            recyclerView.setAdapter(szpDetailAdapter);
            szpDetailAdapter.setNewData(mListLinkedHashMap.get(playBean.getPlayTypeName()));
        }
    }


    private class SZPDetailAdapter extends BaseQuickAdapter {

        private int parentPostion;

        public SZPDetailAdapter(int layoutResId, int postion) {
            super(layoutResId);
            parentPostion = postion;
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            PlayDetailBean playDetailBean = (PlayDetailBean) item;
            helper.setText(R.id.tv_playType, playDetailBean.getNum());
            helper.setText(R.id.tv_odds, playDetailBean.getOdd());
            if (changeType == 1) {
                helper.setText(R.id.tv_lr, playDetailBean.getLr());
            } else if (changeType == 2) {
                helper.setText(R.id.tv_lr, playDetailBean.getYl());
            } else {
                helper.setText(R.id.tv_lr, "");
            }
            helper.itemView.setSelected(playDetailBean.isSelected());
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playDetailBean.isSelected()) {
                        playDetailBean.setSelected(false);
                        selectedBean.remove(playDetailBean);
                    } else {
                        playDetailBean.setSelected(true);
                        selectedBean.add(playDetailBean);
                    }

                    if (selectedBean.size() > 0) {
                        int bottomParent = mAdapter.getViewByPosition(mRvSzpPlayType, parentPostion, R.id.rl_root_common).getTop();
                        int bottomChild = helper.itemView.getBottom();
                        int bottomContentHeight = ((SSCBActivity) getActivity()).getBottomContentHeight();
                        int bottomTouzhu = DensityUtil.dp2px(mContext, 150);
                        if (bottomContentHeight - (bottomParent + bottomChild + DensityUtil.dp2px(mContext, 40)) < bottomTouzhu) {
                            int move = bottomTouzhu - (bottomContentHeight - (bottomParent + bottomChild + DensityUtil.dp2px(mContext, 40)));
                            mRvSzpPlayType.scrollBy(0, move);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    ((SSCBActivity) getActivity()).onTouZhuView(playDetailBean);
                }
            });
        }
    }

    @Subscribe(tags = {@Tag(ConstantValue.EVENT_CHANGE_LR)})
    public void changeLr(String id) {
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(tags = {@Tag(ConstantValue.EVENT_CHANGE_SELECTED)})
    public void randomSelected(String id) {
        if (!isHidden()) {
            ThreadUtils.newThread(new Runnable() {
                @Override
                public void run() {
                    if (selectedBean.size() > 0) {
                        for (PlayTypeBean.PlayBean playBean : mList) {
                            mListLinkedHashMap.put(playBean.getPlayTypeName(),
                                    SSCBDataUtil.clearSelected(mListLinkedHashMap.get(playBean.getPlayTypeName())));
                        }

                    }
                    int ranomItem = RandomUtils.randInt(mListLinkedHashMap.size() - 1);
                    int randomChild = RandomUtils.randInt(2);
                    mListLinkedHashMap.get(mList.get(ranomItem).getPlayTypeName()).get(randomChild).setSelected(true);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (selectedBean.size() > 0) {
                                selectedBean.clear();
                              //  ((SSCBActivity) getActivity()).offTouZhuView();
                            }
                            selectedBean.add(mListLinkedHashMap.get(mList.get(ranomItem).getPlayTypeName()).get(randomChild));
                            mAdapter.notifyDataSetChanged();
                            ((SSCBActivity) getActivity()).onTouZhuView(mListLinkedHashMap.get(mList.get(ranomItem).getPlayTypeName()).get(randomChild));
                        }
                    });
                }
            });
        }
    }
}
