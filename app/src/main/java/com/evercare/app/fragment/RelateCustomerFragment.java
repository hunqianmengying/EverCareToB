package com.evercare.app.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Activity.BaseFragment;
import com.evercare.app.Activity.CustomerInfoViewPagerActivity;
import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.AddressListAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.PersonInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.PinyinUtils;
import com.evercare.app.util.SpellingComparator;
import com.evercare.app.view.OceanActionDialog;
import com.evercare.app.view.SideBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-10-26 15:39
 * 邮箱：842202389@qq.com
 * 关联客户
 */
public class RelateCustomerFragment extends BaseFragment {
    private AddressListAdapter adapter;
    private RecyclerView customerRecyclerView;
    private List<PersonInfo> personList;
    private TextView txt_selectedCode;
    private SideBar sidebar_Letters;
    private Context context;

    private SwipeRefreshLayout refreshLayout;
    private View no_value_view;
    private TextView txt_title_info;
    private View mProgressView;

    protected void initView(View view) {
        context = getContext();

        //设置Item增加、移除动画
        customerRecyclerView = (RecyclerView) view.findViewById(R.id.customerRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        customerRecyclerView.setLayoutManager(layoutManager);
        customerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mProgressView = view.findViewById(R.id.login_progress);
        txt_selectedCode = (TextView) view.findViewById(R.id.txt_selectedCode);
        no_value_view = view.findViewById(R.id.no_value_view);
        no_value_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setRefreshing(true);
                initData();
            }
        });
        txt_title_info = (TextView) view.findViewById(R.id.txt_title_info);
        txt_title_info.setText("关联客户");
        sidebar_Letters = (SideBar) view.findViewById(R.id.sidebar_Letters);
        sidebar_Letters.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (adapter != null) {
                    int position = adapter.getPositionForSelection(s.charAt(0));
                    if (position >= 0) {
                        customerRecyclerView.scrollToPosition(position);
                    }
                }
            }
        });
        sidebar_Letters.setTextView(txt_selectedCode);


        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressViewOffset(true, 80, 320);
        refreshLayout.setProgressViewEndTarget(true, 130);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, Constant.ON_REFRESH);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tempbase;
    }

    private void getLetters(List<PersonInfo> personList) {
        HashSet<String> letters = new HashSet<>();
        for (int i = 0; i < personList.size(); i++) {
            letters.add(personList.get(i).getFirstLetter());
        }
        sidebar_Letters.initLetter(letters);
    }

    protected void initData() {

        final JSONObject jsonObject = new JSONObject();

        HttpUtils.postWithJson(context, Constant.CUSTOM_RELATION, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<PersonInfo>>() {
                        }.getType();
                        JsonListResult<PersonInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        personList = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && personList != null && personList.size() > 0) {
                            personList = getPersonList(personList);
                            Collections.sort(personList, new SpellingComparator());

                            getLetters(personList);

                            adapter = new AddressListAdapter(getContext(), "TempBaseFragment", personList, new CustomerItemClickListener() {
                                @Override
                                public void onItemClick(View view, int postion) {

                                    Intent intent = new Intent(getContext(), CustomerInfoViewPagerActivity.class);
                                    intent.putExtra("custom_id", personList.get(postion).getCustom_id());
                                    intent.putExtra("isRelateCustomer", true);
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongClick(View view, final int position) {

                                    /*OceanActionDialog dialog = new OceanActionDialog(getContext(), personList.get(position).getName(), "临时库", new OceanActionDialog.ClickListenerInterface() {
                                        @Override
                                        public void doCancel() {
                                        }

                                        @Override
                                        public void doBackToPublic() {
                                            seleaseCustomer(personList.get(position), Constant.PUBLIC_SEA, position);
                                            showProgress(true);
                                        }

                                        @Override
                                        public void doTurnToPrivate() {
                                            seleaseCustomer(personList.get(position), Constant.PRIVATE_SEA, position);
                                            showProgress(true);
                                        }
                                    });
                                    dialog.show();*/
                                }
                            });
                            customerRecyclerView.setAdapter(adapter);
                            no_value_view.setVisibility(View.GONE);
                            customerRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            no_value_view.setVisibility(View.VISIBLE);
                            customerRecyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        no_value_view.setVisibility(View.VISIBLE);
                        customerRecyclerView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                no_value_view.setVisibility(View.VISIBLE);
                customerRecyclerView.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                no_value_view.setVisibility(View.VISIBLE);
                customerRecyclerView.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }
        });
    }


    private void seleaseCustomer(PersonInfo item, final String type, final int position) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", item.getBusiness_id());
            jsonObject.put("ocean", item.getOcean());//1临时 2私有 3公海
            jsonObject.put("targetocean", type);//1临时 2私有 3公海，将要去的
            jsonObject.put("name", item.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.postWithJson(context, Constant.CUSTOM_RELEASE, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                            switch (type) {
                                case Constant.PUBLIC_SEA:
                                    Toast.makeText(getContext(), "客户已经退回公海！", Toast.LENGTH_SHORT).show();
                                    break;
                                case Constant.PRIVATE_SEA:
                                    Toast.makeText(getContext(), "客户已经转到私海！", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            personList.remove(position);
                            adapter.notifyDataSetChanged();
                            if (personList.size() <= 0) {
                                no_value_view.setVisibility(View.VISIBLE);
                                customerRecyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getContext(), "操作失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                super.onFailure(statusCode, headers, throwable, response);
                Toast.makeText(getContext(), getResources().getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getContext(), getResources().getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });
    }


    /**
     * 获取所有用户的信息
     *
     * @param personList
     * @return
     */
    private List<PersonInfo> getPersonList(List<PersonInfo> personList) {
        List<PersonInfo> personInfos = new ArrayList<PersonInfo>();
        for (int i = 0; i < personList.size(); i++) {

            String spelliing = PinyinUtils.getPingYin(personList.get(i).getName());
            String firstletter = spelliing.substring(0, 1).toUpperCase();

            PersonInfo personInfo = new PersonInfo();
            personInfo.setName(personList.get(i).getName());
            personInfo.setSpelling(spelliing);
            personInfo.setMobile(personList.get(i).getMobile());
            personInfo.setCustom_id(personList.get(i).getCustom_id());
            personInfo.setAge(personList.get(i).getAge());
            personInfo.setBirthday(personList.get(i).getBirthday());
            personInfo.setCustom_card(personList.get(i).getCustom_card());
            personInfo.setSex(personList.get(i).getSex());
            personInfo.setOcean(personList.get(i).getOcean());
            personInfo.setOverlap(personList.get(i).getOverlap());
            personInfo.setBusiness_id(personList.get(i).getBusiness_id());
            if (firstletter.matches("[A-Z]")) {
                personInfo.setFirstLetter(firstletter);
            } else {
                personInfo.setFirstLetter("#");
            }
            personInfos.add(personInfo);
        }
        return personInfos;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customerRecyclerView.setLayoutManager(new LinearLayoutManager(customerRecyclerView.getContext()));

    }

    @Override
    public void fetchData() {

    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
