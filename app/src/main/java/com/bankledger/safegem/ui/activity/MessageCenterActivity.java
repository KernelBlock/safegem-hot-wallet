package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.greendaodb.MessageUtils;
import com.bankledger.safegem.greendaodb.entity.MessageTb;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.StringUtil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageCenterActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<MessageTb> list;
    private BaseRecyclerAdapter<MessageTb> mAdapter;
    private MessageUtils messageUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        setTitle(getString(R.string.mesage_str));
        list = new ArrayList();
        messageUtils = new MessageUtils(this);
        list.addAll(messageUtils.queryAllMessage());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<MessageTb>(this, list, R.layout.message_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, MessageTb item, int position, boolean isScrolling) {
                holder.setText(R.id.time, DateTimeUtil.formatDate(item.date));
                if (item.msgType == Constants.RECEIVE_MSG_TYPE) {
                    holder.setImageResource(R.id.img, R.drawable.receieve_money_icon).setText(R.id.name_tv, getString(R.string.receive_broad)).setText(R.id.content, item.content + "  " + getString(R.string.receive_success));
                } else if (item.msgType == Constants.SEND_MSG_TYPE) {
                    holder.setImageResource(R.id.img, R.drawable.send_icon).setText(R.id.name_tv, getString(R.string.send_broad)).setText(R.id.content, item.content + "  " + getString(R.string.send_success));
                } else {
                    holder.setImageUrl(R.id.img, item.icon, R.mipmap.app_launcher).setText(R.id.name_tv, item.getTitle()).setText(R.id.content, item.getContent());
                }
            }
        };
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                MessageTb messageTb = list.get(position);
                if (messageTb.msgType == Constants.RECEIVE_MSG_TYPE || messageTb.msgType == Constants.SEND_MSG_TYPE) {
                    HashMap<String, Object> args = new HashMap<>(1);
                    args.put(Constants.INTENT_KEY1, messageTb.txHash);
                    args.put(Constants.INTENT_KEY2, messageTb.getColdUniqueId());
                    args.put(Constants.INTENT_KEY3, messageTb.getCoinType());
                    ActivitySkipUtil.skipAnotherActivity(MessageCenterActivity.this, TradeDetailActivity.class, args);
                } else {
                    if (!TextUtils.isEmpty(messageTb.msgUrl) && messageTb.msgUrl.toLowerCase().startsWith("http")) {
                        HashMap<String, String> helep_map = new HashMap<>();
                        helep_map.put(Constants.INTENT_DATA, messageTb.title);
                        helep_map.put(Constants.URL, messageTb.msgUrl);
                        ActivitySkipUtil.skipAnotherActivity(MessageCenterActivity.this, WebviewActivity.class, helep_map);
                    }
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

}
