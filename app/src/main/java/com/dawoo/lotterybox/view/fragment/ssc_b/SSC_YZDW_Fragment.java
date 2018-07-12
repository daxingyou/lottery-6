package com.dawoo.lotterybox.view.fragment.ssc_b;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.patternlockview.utils.RandomUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dawoo.coretool.util.activity.DensityUtil;
import com.dawoo.lotterybox.BoxApplication;
import com.dawoo.lotterybox.ConstantValue;
import com.dawoo.lotterybox.R;
import com.dawoo.lotterybox.bean.playtype.PlayDetailBean;
import com.dawoo.lotterybox.bean.playtype.PlayTypeBean;
import com.dawoo.lotterybox.util.SingleToast;
import com.dawoo.lotterybox.util.ThreadUtils;
import com.dawoo.lotterybox.view.activity.lottery.ssc.SSCBActivity;
import com.dawoo.lotterybox.view.fragment.BaseFragment;
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

public class SSC_YZDW_Fragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.rv_szp_play_type)
    RecyclerView mRvSzpPlayType;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    private DetailAdapter mDetailAdapter;

    private List<PlayTypeBean.PlayBean> mList = new ArrayList<>();
    private LinkedHashMap<String, List<PlayDetailBean>> mListLinkedHashMap = new LinkedHashMap<>();
    private List<PlayDetailBean> extraData = new ArrayList<>();//万位 额外的总和数据

    private List<PlayDetailBean> selectedBean = new ArrayList<>();
    String playName = BoxApplication.getContext().getString(R.string.ww);

    public SSC_YZDW_Fragment() {
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
        View v = inflater.inflate(R.layout.fragment_ssc_b_yzdw, container, false);
        unbinder = ButterKnife.bind(this, v);
        RxBus.get().register(this);
        initViews();
        initData();
        initListener();
        return v;
    }


    private void initViews() {
        mList = SSCBDataUtil.initYZDWData();
        initTab();
        mDetailAdapter = new DetailAdapter(R.layout.click_play_type_item_layout);
        mRvSzpPlayType.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRvSzpPlayType.setAdapter(mDetailAdapter);
    }


    private void initData() {
        selectedBean.clear();
        ((SSCBActivity) getActivity()).offTouZhuView();
        for (PlayTypeBean.PlayBean playBean : mList) {
            mListLinkedHashMap.put(playBean.getPlayTypeName(), SSCBDataUtil.init_YZDW_0_9_DXDSZH_Data(false
                    , playBean.getPlayTypeName(), ""));
        }
        extraData.clear();
        extraData.addAll(SSCBDataUtil.init_YZDW_0_9_DXDSZH_Data(true, getString(R.string.ww), getString(R.string.zh)));
        mListLinkedHashMap.get(getString(R.string.ww)).addAll(extraData);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDetailAdapter.setNewData(mListLinkedHashMap.get(playName));
            }
        });
    }


    /**
     * 初始化 tab ui
     */
    private void initTab() {
        for (int i = 0; i < mList.size(); i++) {
            if (i == 0) {
                mTabLayout.addTab(mTabLayout.newTab().setText(mList.get(i).getPlayTypeName()), true);
            } else {
                mTabLayout.addTab(mTabLayout.newTab().setText(mList.get(i).getPlayTypeName()), false);
            }
        }
    }


    private void initListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((SSCBActivity) getActivity()).offTouZhuView();
                if (playName.equalsIgnoreCase(mList.get(tab.getPosition()).getPlayTypeName())) {
                    return;
                }
                playName = mList.get(tab.getPosition()).getPlayTypeName();
                chooseType();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void chooseType() {
        SingleToast.showMsg(playName);
        if (selectedBean.size() > 0) {
            selectedBean.clear();
            for (PlayTypeBean.PlayBean playBean : mList) {
                mListLinkedHashMap.put(playBean.getPlayTypeName(),
                        SSCBDataUtil.clearSelected(mListLinkedHashMap.get(playBean.getPlayTypeName())));
            }
        }
        mDetailAdapter.setNewData(mListLinkedHashMap.get(playName));
    }


    @Subscribe(tags = {@Tag(LotteryBFragmentManager.TOP_FRGMENT)})
    public void clearSelectedData(String id) {
        if (!id.equals(this.getId())&& selectedBean.size() > 0) {
            ThreadUtils.newThread(new Runnable() {
                @Override
                public void run() {
                    if (selectedBean.size() > 0) {
                        selectedBean.clear();
                        for (PlayTypeBean.PlayBean playBean : mList) {
                            mListLinkedHashMap.put(playBean.getPlayTypeName(),
                                    SSCBDataUtil.clearSelected(mListLinkedHashMap.get(playBean.getPlayTypeName())));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDetailAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        }
    }


    private class DetailAdapter extends BaseQuickAdapter {

        public DetailAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            PlayDetailBean playDetailBean = (PlayDetailBean) item;
            helper.setText(R.id.tv_playType, playDetailBean.getNum());
            helper.itemView.setSelected(playDetailBean.isSelected());
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
                        int bottomParent = mRvSzpPlayType.getTop();
                        int bottomChild = helper.itemView.getBottom();
                        int bottomContentHeight = ((SSCBActivity) getActivity()).getBottomContentHeight();
                        int bottomTouzhu = DensityUtil.dp2px(mContext, 150);

                        if (bottomContentHeight - (bottomParent + bottomChild) < bottomTouzhu) {
                            int move = bottomTouzhu - (bottomContentHeight - (bottomParent + bottomChild));
                            mRvSzpPlayType.scrollBy(0, move);
                        }
                    }
                    ((SSCBActivity) getActivity()).onTouZhuView(playDetailBean);
                    mDetailAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Subscribe(tags = {@Tag(ConstantValue.EVENT_CHANGE_LR)})
    public void changeLr(String id) {
        mDetailAdapter.notifyDataSetChanged();
    }

    @Subscribe(tags = {@Tag(ConstantValue.EVENT_CHANGE_SELECTED)})
    public void randomSelected(String id) {
        if (!isHidden()) {
            ThreadUtils.newThread(new Runnable() {
                @Override
                public void run() {
                    if (selectedBean.size() > 0) {
                        selectedBean.clear();
                        for (PlayTypeBean.PlayBean playBean : mList) {
                            mListLinkedHashMap.put(playBean.getPlayTypeName(),
                                    SSCBDataUtil.clearSelected(mListLinkedHashMap.get(playBean.getPlayTypeName())));
                        }
                        selectedBean.clear();
                    }
                    int ranomItem = RandomUtils.randInt(mListLinkedHashMap.get(playName).size() - 1);
                    mListLinkedHashMap.get(playName).get(ranomItem).setSelected(true);
                    selectedBean.add(mListLinkedHashMap.get(playName).get(ranomItem));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                      //      ((SSCBActivity) getActivity()).offTouZhuView();
                            mDetailAdapter.notifyDataSetChanged();
                            ((SSCBActivity) getActivity()).onTouZhuView(mListLinkedHashMap.get(playName).get(ranomItem));
                        }
                    });
                }
            });
        }
    }
}
