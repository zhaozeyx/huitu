/*
 * 文件名: ImageDisplayView
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2016/10/31
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.ui.imagepick;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.demon.huitu.R;
import com.demon.huitu.log.Logger;
import com.demon.huitu.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 2016/10/31]
 */
public class ImageDisplayView extends FrameLayout implements IImageDisplayView {
  private static final int MAX_ITEM_COUNT = 9;
  private static final String PLACE_HOLDER_IMG_STR = "DEFAULT";
  @Bind(R.id.count)
  TextView count;
  private int mMaxItemCount = MAX_ITEM_COUNT;
  @Bind(R.id.img_list)
  RecyclerView imgList;

  private ItemAdapter mAdapter;
  private OnItemDeleteClickListener onItemDeleteClickListener;
  private OnAddImgClickListener onAddImgClickListener;

  public ImageDisplayView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  private void initView() {
    View childView = LayoutInflater.from(getContext()).inflate(R.layout.img_display_view, this,
        false);
    ButterKnife.bind(this, childView);
    addView(childView);
    imgList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager
        .HORIZONTAL, false));

    mAdapter = new ItemAdapter();
    imgList.setAdapter(mAdapter);
  }

  @Override
  public void show() {
    setVisibility(VISIBLE);
  }

  @Override
  public void hide() {
    setVisibility(GONE);
  }

  @Override
  public void delete(int index) {
    mAdapter.delete(index);
  }

  @Override
  public void add(List<String> imgPaths) {
    mAdapter.addAll(imgPaths);
  }

  @Override
  public void onImgClear() {
    hide();
  }

  @Override
  public void setMaxCount(int count) {
    mMaxItemCount = count;
  }

  @Override
  public void onExceedMaxCount() {

  }

  @Override
  public void clear() {
    mAdapter.clear();
  }

  @Override
  public List<String> getSelectedImgPaths() {
    return mAdapter.getImgPaths();
  }

  @Override
  public void setOnItemDeleteClickListener(OnItemDeleteClickListener listener) {
    onItemDeleteClickListener = listener;
  }

  @Override
  public void setOnAddImgClickListener(OnAddImgClickListener listener) {
    onAddImgClickListener = listener;
  }

  class ItemHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.src)
    ImageView src;
    @Bind(R.id.btn_delete)
    ImageView btnDelete;

    public ItemHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

  }

  class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    private List<String> mImgPaths = new ArrayList<>();

    public ItemAdapter() {
      mImgPaths.add(PLACE_HOLDER_IMG_STR);
    }

    public void addAll(List<String> paths) {
      if (null == paths) {
        return;
      }
      // 因为最后一张是默认图片，所以添加数据要在倒数第一个位置添加
      mImgPaths.addAll(mImgPaths.size() - 1, paths);
      notifyDataSetChanged();
      updateCountView();
    }

    public void add(String path) {
      // 因为最后一张是默认图片，所以添加数据要在倒数第一个位置添加
      mImgPaths.add(mImgPaths.size() - 1, path);
      notifyDataSetChanged();
      updateCountView();
    }

    public void delete(int index) {
      if (index == mImgPaths.size() - 1) {
        return;
      }
      mImgPaths.remove(index);
      notifyDataSetChanged();
      updateCountView();
    }

    public void clear() {
      mImgPaths.clear();
      mImgPaths.add(PLACE_HOLDER_IMG_STR);
      notifyDataSetChanged();
    }

    public List<String> getImgPaths() {
      return mImgPaths.subList(0, mImgPaths.size() - 1);
    }

    public void setMaxItemCount(int maxItemCount) {
      mMaxItemCount = maxItemCount;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(getContext()).inflate(R.layout.img_display_view_item,
          parent, false);
      return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {
      if (position == mImgPaths.size() - 1) {
        Logger.d("YZZ", "set default img");
        holder.src.setImageResource(R.mipmap.add_post_photo);
        holder.btnDelete.setVisibility(View.GONE);
        holder.src.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mImgPaths.size() - 1 >= mMaxItemCount) {
              Toast.makeText(getContext(), "不能添加更多图片", Toast.LENGTH_SHORT).show();
              return;
            }
            performAddClicked();
          }
        });
        return;
      }
      holder.src.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          // DO NOTHING
        }
      });

      holder.btnDelete.setVisibility(View.VISIBLE);
      holder.btnDelete.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          performDeleteClicked(position);
        }
      });
      String path = mImgPaths.get(position);
      ImageLoader.loadOptimizedHttpImage(getContext(), path).dontAnimate().into(holder.src);
    }

    @Override
    public int getItemCount() {
      return mImgPaths.size() >= MAX_ITEM_COUNT ? mMaxItemCount : mImgPaths.size();
    }

  }

  private void updateCountView() {
    int selectedCount = mAdapter.getItemCount() - 1;
    count.setText(getResources().getString(R.string.image_display_view_count, selectedCount,
        mMaxItemCount));
  }

  private void performDeleteClicked(int index) {
    if (null != onItemDeleteClickListener) {
      onItemDeleteClickListener.onItemDeleteClicked(index, mAdapter.mImgPaths.get(index));
    }
    mAdapter.delete(index);
  }

  private void performAddClicked() {
    if (null != onAddImgClickListener) {
      onAddImgClickListener.onAddClicked();
    }
  }

}
