package com.evercare.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.ConsultReviewInfo;
import com.evercare.app.model.ConsultReviewItemInfo;

import java.util.List;

/**
 * 作者：LXQ on 2016-10-27 14:37
 * 邮箱：842202389@qq.com
 * 咨询/回访记录
 */
public class ConsultRecordAdapter extends BaseRecyclerAdapter<ConsultReviewItemInfo> {

    private List<ConsultReviewItemInfo> datas;
    private Context context;

    public ConsultRecordAdapter(Context context, int itemLayoutId, List<ConsultReviewItemInfo> datas) {
        super(context, itemLayoutId, datas);
        this.datas = datas;
        this.context = context;
    }

    @Override
    public void convert(final BaseRecyclerHolder holder, ConsultReviewItemInfo item, int position) {
        ConsultReviewItemInfo consultReviewItemInfo = datas.get(position);

        holder.setText(R.id.txt_content, consultReviewItemInfo.getContent());
        holder.setText(R.id.txt_time, consultReviewItemInfo.getDay());

        final TextView txt_content = (TextView) holder.getView(R.id.txt_content);

        //boolean isOverFlowed = isOverFlowed(txt_content);
        int length = consultReviewItemInfo.getContent().length();
        if (consultReviewItemInfo.getContent().length() < 38) {
            holder.setVisible(R.id.expend_view, false);
        } else {
            holder.setVisible(R.id.expend_view, true);

            final ImageView expend_view = holder.getView(R.id.expend_view);
            expend_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_content.getLineCount() > 2) {
                        txt_content.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                        txt_content.setLines(2);
                        expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                    } else {
                        txt_content.setEllipsize(null); // 展开
                        txt_content.setSingleLine(false);
                        expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
                    }
                }
            });
        }

        switch (consultReviewItemInfo.getType()) {
            case "1":
                holder.setText(R.id.txt_type, "咨询");
                break;
            case "2":
                holder.setText(R.id.txt_type, "预约");
                break;
            case "3":
                holder.setText(R.id.txt_type, "任务");
                break;
        }
    }

}
