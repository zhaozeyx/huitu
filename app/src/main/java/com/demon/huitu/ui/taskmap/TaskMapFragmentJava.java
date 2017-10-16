package com.demon.huitu.ui.taskmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.SupportMapFragment;
import com.demon.huitu.R;

/*
 * 文件名: TaskMapFragmentJava
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/6/19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

public class TaskMapFragmentJava extends Fragment {
    private static TaskMapFragmentJava fragment;
    private AMap mMap;

    public static Fragment newIntance() {
        if (null == fragment) {
            fragment = new TaskMapFragmentJava();
            fragment.setUpMapIfNeeded(null);
        }

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setUpMapIfNeeded(View view) {
        if (mMap == null) {
//            mMap = ((SupportMapFragment)view.findViewById(R.id.map_fragment)).
        }
    }
}
