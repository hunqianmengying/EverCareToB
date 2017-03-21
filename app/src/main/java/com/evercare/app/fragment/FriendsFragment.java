package com.evercare.app.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.FriendAdapter;
import com.evercare.app.model.Friend;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.PinyinComparator;
import com.evercare.app.util.PinyinUtils;
import com.evercare.app.view.SideBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.rong.imkit.RongIM;
import io.rong.imkit.tools.CharacterParser;
import io.rong.imlib.model.UserInfo;


/**
 * 作者：xlren on 2016-11-29 17:56
 * 邮箱：renxianliang@126.com
 * 好友列表
 */
public class FriendsFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener,RongIM.UserInfoProvider {


    //不带字母的数据集合
    private List<Friend> mSourceDateList = new ArrayList<>();
    //带字母的集合
    private List<Friend> sourceDateList;

    public static FriendsFragment instance = null;

    public static FriendsFragment getInstance() {
        if (instance == null) {
            instance = new FriendsFragment();
        }
        return instance;
    }

    private View mView;

    /**
     * 好友列表的 ListView
     */
    private ListView mListView;
    /**
     * 好友列表的 adapter
     */
    private FriendAdapter adapter;
    /**
     * 右侧好友指示 Bar
     */
    private SideBar mSidBar;
    /**
     * 中部展示的字母提示
     */
    public TextView dialog;

    private TextView show_no_friends;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.rp_friend_fragment, null);
        getFriendsList( new JSONObject());
        initView();
        return mView;
    }
    private void initView() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();
        mListView = (ListView) mView.findViewById(R.id.friendlistview);
        mSidBar = (SideBar) mView.findViewById(R.id.sidrbar);
        dialog = (TextView) mView.findViewById(R.id.dialog);
        show_no_friends = (TextView) mView.findViewById(R.id.show_no_friends);
        mSidBar.setTextView(dialog);
        //设置右侧触摸监听
        mSidBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                try{
                    //该字母首次出现的位置
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position >= 0) {
                        mListView.setSelection(position);
                    }
                }catch (Exception e){

                }
            }
        });

    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<Friend> filledData(List<Friend> list) {
        List<Friend> mFriendList = new ArrayList<Friend>();

        for (int i = 0; i < list.size(); i++) {
            Friend friendModel = new Friend();
            String spelliing = PinyinUtils.getPingYin(list.get(i).getName());
            String firstletter = spelliing.substring(0, 1).toUpperCase();
            friendModel.setSpelling(spelliing);
            // 正则表达式，判断首字母是否是英文字母
            if (firstletter.matches("[A-Z]")) {
                friendModel.setFirstLetter(firstletter.toUpperCase());
            } else {
                friendModel.setFirstLetter("#");
            }
            friendModel.setName(list.get(i).getName());
            friendModel.setCustom_id(list.get(i).getCustom_id());
            friendModel.setRongyun_id(list.get(i).getRongyun_id());
            friendModel.setPortraits(list.get(i).getPortraits());
            mFriendList.add(friendModel);
        }
        return mFriendList;

    }

    public TextView getDialog() {
        return dialog;
    }

    public void setDialog(TextView dialog) {
        this.dialog = dialog;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent mIntent = new Intent(getActivity(), StartDiscussionActivity.class);
//        mIntent.putExtra("FRIENDDATA", (Serializable) SourceDateList);
//        getActivity().startActivity(mIntent);
        return true;
    }

    /**
     * 开启多人好友聊天
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(getActivity(), sourceDateList.get(position).getRongyun_id(),
                    sourceDateList.get(position).getName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    private void getFriendsList(JSONObject jsonObject) {
        HttpUtils.postWithJson(getActivity(), Constant.CHAT_FRIENDS, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<Friend>>() {
                        }.getType();
                        JsonListResult<Friend> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        mSourceDateList = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && mSourceDateList.size() > 0) {
                            sourceDateList = filledData(mSourceDateList); //过滤数据对象为友字母字段
                            if (sourceDateList.size() > 0) {
                                show_no_friends.setVisibility(View.GONE);
                            }
                            // 根据a-z进行排序源数据
                            Collections.sort(sourceDateList, pinyinComparator);
                            adapter = new FriendAdapter(getActivity(), sourceDateList);
                            mListView.setAdapter(adapter);
                            mListView.setOnItemLongClickListener(FriendsFragment.this);
                            mListView.setOnItemClickListener(FriendsFragment.this);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    String str = errorResponse.toString();
                    Toast.makeText(getActivity(), str + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public UserInfo getUserInfo(String s) {
        for (Friend i : sourceDateList) {
            if (i.getRongyun_id().equals(s)) {
                return new UserInfo(i.getRongyun_id(),i.getName(), Uri.parse(i.getPortraits()));
            }
        }
        return null;
    }

}
