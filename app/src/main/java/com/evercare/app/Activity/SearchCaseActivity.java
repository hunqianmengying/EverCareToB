package com.evercare.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.R;
import com.evercare.app.adapter.SearchItemAdatpter;
import com.evercare.app.model.CustomerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：LXQ on 2016-9-26 11:46
 * 邮箱：842202389@qq.com
 * 搜索界面，搜索案例和活动
 */
public class SearchCaseActivity extends BaseActivity implements View.OnClickListener {

    private TextView txt_center;
    private TextView txt_left;
    private RecyclerView contentRecyclerView;
    private ImageView img_delete;
    private List<String> nameList;
    private SearchItemAdatpter adatpter;

    private EditText search_et_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchcase);
        initView();
    }

    private void initView() {
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_center.setText("搜索");
        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setOnClickListener(this);
        txt_left.setText("返回");

        search_et_input = (EditText) findViewById(R.id.search_et_input);

        img_delete = (ImageView) findViewById(R.id.img_delete);
        img_delete.setOnClickListener(this);

        contentRecyclerView = (RecyclerView) findViewById(R.id.contentRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchCaseActivity.this, LinearLayoutManager.VERTICAL, false);
        contentRecyclerView.setLayoutManager(layoutManager);
        nameList = new ArrayList<>();
        nameList.add("无痛脱毛");
        nameList.add("腋下脱毛");

        adatpter = new SearchItemAdatpter(this, nameList, new CustomerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                String str = nameList.get(postion);
                search_et_input.setText(str);
                Toast.makeText(SearchCaseActivity.this, search_et_input.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        contentRecyclerView.setAdapter(adatpter);

        watchSearch();
    }

    private void watchSearch() {
        search_et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) search_et_input.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchCaseActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    Toast.makeText(SearchCaseActivity.this, search_et_input.getText(), Toast.LENGTH_SHORT).show();
                    addToSearchedList(search_et_input.getText().toString());
                    return true;
                }

                return false;
            }
        });
    }

    private void addToSearchedList(String content) {
        nameList.add(0, content);
        if (nameList.size() > 3) {
            nameList.remove(nameList.size() - 1);
        }
        adatpter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_text:
                finish();
                break;
            case R.id.img_delete:
                nameList.clear();
                adatpter.notifyDataSetChanged();
                break;
        }
    }

}
