package com.demon.huitu.ui.task;
/*
 * 文件名: TaskListFragment
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demon.huitu.R;
import com.demon.huitu.injection.DaggerServiceComponent;
import com.demon.huitu.injection.ServiceModule;
import com.demon.huitu.net.NetConstant;
import com.demon.huitu.net.model.TaskModel;
import com.demon.huitu.net.service.AppService;
import com.demon.huitu.ui.basic.BasicPullToRefreshFragment;
import com.demon.huitu.ui.basic.pulltorefresh.PullToRefreshListView;
import com.demon.huitu.util.DialUtil;
import com.demon.huitu.util.imageloader.ImageLoader;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskListFragment extends BasicPullToRefreshFragment {
  public static final int TYPE_UNTREAT = 0;
  public static final int TYPE_TREATED = 1;
  public static final int TYPE_ALL = 2;
  public static final String KEY_BUNDLE_TYPE = "type";
  private static final String STATE_TREATING = "处理中";

  @Bind(R.id.titleBar)
  Toolbar titleBar;
  @Bind(R.id.list)
  PullToRefreshListView list;
  @Bind(R.id.empty_layout)
  FrameLayout emptyLayout;
  @Bind(R.id.progressBar)
  ProgressBar progressBar;

  private int mType;
  private TaskListAdapter mAdapter;
  @Inject
  AppService mAppService;
  private List<TaskModel> mModelList = new ArrayList<>();

  public static TaskListFragment newInstance(int type) {
    TaskListFragment fragment = new TaskListFragment();
    Bundle args = new Bundle();
    args.putInt(KEY_BUNDLE_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.common_pull_to_refresh_list, container, false);
    ButterKnife.bind(this, view);
    injectService();
    return view;
  }

  private void injectService() {
    DaggerServiceComponent.builder().serviceModule(new ServiceModule()).build().inject(this);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    mType = getArguments().getInt(KEY_BUNDLE_TYPE);
    super.onViewCreated(view, savedInstanceState);
    initViews();
  }

  private void initViews() {
    titleBar.setVisibility(View.GONE);
    mAdapter = new TaskListAdapter();
    list.getRefreshListView().setAdapter(mAdapter);
  }

  @Override
  protected PullToRefreshListView getListView() {
    return list;
  }

  @Override
  protected void loadData() {
//    manageRpcCall(mAppService.getTaskList(getComponent().loginSession().getUserId(),
//        getCurrentPage(), PAGE_COUNT, mType), new UiRpcSubscriber<TaskModel>() {
//
//
//      @Override
//      protected void onSuccess(TaskModel model) {
//        if (getCurrentPage() == START_PAGE) {
//          mModelList.clear();
//        }
//        mModelList.addAll(model.getLog());
//        mAdapter.notifyDataSetChanged();
//
//        onRefreshLoaded(model.getLog().size() >= PAGE_COUNT);
//      }
//
//      @Override
//      protected void onEnd() {
//        dismissInitProgress();
//        resetRefreshStatus();
//      }
//
//      @Override
//      public void onApiError(RpcApiError apiError) {
//        super.onApiError(apiError);
//      }
//
//      @Override
//      public void onHttpError(RpcHttpError httpError) {
//        super.onHttpError(httpError);
//      }
//    });
  }

  @Override
  protected View getInitProgress() {
    return progressBar;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  class TaskListAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return mModelList.size();
    }

    @Override
    public TaskModel getItem(int i) {
      return mModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      final ViewHolder holder;
      if (null == view) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.task_list_item, viewGroup,
            false);
        holder = new ViewHolder(view);
        view.setTag(holder);
      } else {
        holder = (ViewHolder) view.getTag();
      }
      final TaskModel model = getItem(i);
      holder.snapshot.setVisibility(!TextUtils.isEmpty(model.getUrl()) ? View.VISIBLE : View.GONE);
      if (TextUtils.isEmpty(model.getUrl())) {
        holder.snapshot.setVisibility(View.GONE);
      } else {
        holder.snapshot.setVisibility(View.VISIBLE);
        ImageLoader.loadOptimizedHttpImage(getActivity(), NetConstant.BASE_URL_LOCATION +
            model.getUrl()).into(holder.snapshot);
      }
      holder.time.setText(model.getInputdate());
      holder.description.setText(model.getQuestion());
      holder.state.setText(model.getProcstatus());

      holder.finish.setVisibility(TextUtils.equals(model.getProcstatus(), STATE_TREATING) ? View
          .VISIBLE :
          View.GONE);
      holder.finish.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//          startActivity(ReportCommitActivity.makeIntent(getActivity(), model.getId()));
        }
      });
      holder.dial.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          DialUtil.dial(getActivity(), "025-88888888");
        }
      });
      holder.location.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          boolean showBtn = TextUtils.equals(model.getProcstatus(), STATE_TREATING);
//          startActivity(ReportLocationMapActivity.makeIntent(getActivity(), model.getLat(), model
//              .getCon(), showBtn, model.getId()));
        }
      });
      return view;
    }

    class ViewHolder {
      @Bind(R.id.snapshot)
      ImageView snapshot;
      @Bind(R.id.time)
      TextView time;
      @Bind(R.id.state)
      TextView state;
      @Bind(R.id.description)
      TextView description;
      @Bind(R.id.dial)
      TextView dial;
      @Bind(R.id.location)
      TextView location;
      @Bind(R.id.finish)
      TextView finish;

      ViewHolder(View view) {
        ButterKnife.bind(this, view);
      }
    }
  }
}
