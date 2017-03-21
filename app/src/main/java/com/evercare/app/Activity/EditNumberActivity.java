package com.evercare.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.R;
import com.evercare.app.util.CommonUtil;

/**
 * 作者：LXQ on 2016-12-7 13:27
 * 邮箱：842202389@qq.com
 * 修改订单项目个数
 */
public class EditNumberActivity extends Activity {
    private Button btn_confirm;
    private Button btn_cancel;
    private EditText edt_project_number;
    private EditText edt_project_price;
    private EditText edt_project_remark;
    private String number;
    private String remark;
    private String strPrice;
    private double oldPrice;
    private TextView txt_warning;

    private Toast mToast;

    private ImageView ic_delete_number;
    private ImageView ic_add_number;

    private TextView txt_current_words;

    private boolean isPackage = true;//是否是套餐

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_number);
        Intent intent = getIntent();

        index = intent.getIntExtra("index", 0);
        remark = intent.getStringExtra("remark");
        number = intent.getStringExtra("number");
        strPrice = intent.getStringExtra("price");
        oldPrice = Double.valueOf(intent.getStringExtra("oldprice"));
        isPackage = intent.getBooleanExtra("isPackage", false);
        initView();
    }

    private void initView() {

        txt_warning = (TextView) findViewById(R.id.txt_warning);
        if (isPackage) {
            txt_warning.setVisibility(View.VISIBLE);
        } else {
            txt_warning.setVisibility(View.GONE);
        }

        txt_current_words = (TextView) findViewById(R.id.txt_current_words);
        ic_add_number = (ImageView) findViewById(R.id.ic_add_number);
        ic_delete_number = (ImageView) findViewById(R.id.ic_delete_number);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);

        edt_project_number = (EditText) findViewById(R.id.edt_project_number);
        edt_project_price = (EditText) findViewById(R.id.edt_project_price);
        edt_project_remark = (EditText) findViewById(R.id.edt_project_remark);

        edt_project_number.setText(number);
        if (isPackage) {
            ic_add_number.setEnabled(false);
            ic_delete_number.setEnabled(false);
            edt_project_number.setTextColor(ContextCompat.getColor(EditNumberActivity.this, R.color.gray_7));
            edt_project_number.setEnabled(false);
        } else {
            edt_project_number.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String tem = s.toString();
                    if (!TextUtils.isEmpty(tem)) {
                        double num = Double.valueOf(tem);
                        if (num > 9999) {
                            s.delete(s.length() - 1, s.length());
                        }
                    }
                }
            });

            ic_add_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(edt_project_number.getText().toString())) {
                        int newNumber = Integer.valueOf(edt_project_number.getText().toString());
                        if (newNumber >= 0 && newNumber < 9999) {
                            edt_project_number.setText(String.valueOf(newNumber + 1));
                        }
                    } else {
                        edt_project_number.setText("1");
                    }
                }
            });

            ic_delete_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(edt_project_number.getText().toString())) {
                        int newNumber = Integer.valueOf(edt_project_number.getText().toString());
                        if (newNumber > 1) {
                            edt_project_number.setText(String.valueOf(newNumber - 1));
                        }
                    } else {
                        edt_project_number.setText("0");
                    }
                }
            });
        }

        edt_project_remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 50) {
                    s.delete(s.length() - 1, s.length());
                }

                txt_current_words.setText(String.valueOf(s.length()));
            }
        });
        edt_project_price.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                if (!TextUtils.isEmpty(edt.toString())) {
                    String temp = edt.toString();

                    double value = Double.valueOf(temp);
                    if (value > 9999999.99) {
                        edt.delete(edt.length() - 1, edt.length());
                    } else {
                        int posDot = temp.indexOf(".");
                        if (posDot <= 0) return;
                        if (temp.length() - posDot - 1 > 2) {
                            edt.delete(posDot + 3, posDot + 4);
                        }
                    }
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
        edt_project_price.setText(strPrice);


        edt_project_remark.setText(remark);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0, new Intent());
                finish();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {

                                               if (!TextUtils.isEmpty(edt_project_price.getText().toString())) {
                                                   double newPrice = Double.valueOf(edt_project_price.getText().toString());
                                                   if (oldPrice > newPrice) {
                                                       showToast(EditNumberActivity.this, "新单价不能低于老单价！");
                                                   } else {
                                                       if (!TextUtils.isEmpty(edt_project_number.getText().toString())) {
                                                           double newNumber = Double.valueOf(edt_project_number.getText().toString());
                                                           if (newNumber <= 0) {
                                                               showToast(EditNumberActivity.this, "数量必须大于0！");
                                                           } else {
                                                               Intent intent = new Intent();
                                                               intent.putExtra("price", String.valueOf(Double.valueOf(edt_project_price.getText().toString())));
                                                               intent.putExtra("number", String.valueOf(Integer.valueOf(edt_project_number.getText().toString())));
                                                               intent.putExtra("remark", edt_project_remark.getText().toString());
                                                               intent.putExtra("index", index);
                                                               setResult(1, intent);
                                                               finish();
                                                           }
                                                       } else {
                                                           showToast(EditNumberActivity.this, "数量必须大于0！");
                                                       }
                                                   }
                                               } else {
                                                   showToast(EditNumberActivity.this, "数量必须大于0！");
                                               }
                                           }
                                       }
        );
    }


    private void showToast(Context context, String str) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(0, new Intent());
            finish();
        }
        return false;
    }
}
