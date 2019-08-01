package com.vmloft.develop.library.im.emotion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.adapter.VMHolder;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Create by lzan13 on 2019/5/29 14:16
 *
 * 表情适配器
 */
public class IMEmotionRecyclerAdapter extends VMAdapter<IMEmotionItem, IMEmotionRecyclerAdapter.EmotionHolder> {

    private IMEmotionGroup mEmotionGroup;

    public IMEmotionRecyclerAdapter(Context context, IMEmotionGroup group) {
        super(context);
        mEmotionGroup = group;
        mDataList = mEmotionGroup.mEmotionItemList;
        VMLog.d("初始化表情适配器 表情数: %d", getItemCount());
    }

    @Override
    public EmotionHolder createHolder(@NonNull ViewGroup root, int viewType) {
        View view = mInflater.inflate(R.layout.im_emotion_recycler_view_item, root, false);
        return new EmotionHolder(view);
    }

    @Override
    public void bindHolder(@NonNull EmotionHolder holder, int position) {
        IMEmotionItem item = getItemData(position);
        if (mEmotionGroup.isInnerEmotion) {
            holder.mNameView.setVisibility(View.GONE);
            holder.mIconView.setImageResource(item.mResId);
            if (mEmotionGroup.isBigEmotion) {
                holder.mNameView.setVisibility(View.VISIBLE);
                holder.mNameView.setText(item.mDesc.replaceAll("[\\[\\]]", ""));
            }
        } else {
            holder.mNameView.setVisibility(View.VISIBLE);
            holder.mNameView.setText(item.mDesc.replaceAll("[\\[\\]]", ""));
            // TODO 从服务器下载的表情加载
        }
    }

    class EmotionHolder extends VMHolder {
        public ImageView mIconView;
        public TextView mNameView;

        public EmotionHolder(View itemView) {
            super(itemView);
            mIconView = itemView.findViewById(R.id.im_emotion_item_icon_iv);
            mNameView = itemView.findViewById(R.id.im_emotion_item_name_tv);
        }
    }
}
