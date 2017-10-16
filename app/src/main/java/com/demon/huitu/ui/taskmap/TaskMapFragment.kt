package com.demon.huitu.ui.taskmap

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.LocationSource
import com.amap.api.maps.SupportMapFragment
import com.amap.api.maps.model.*
import com.demon.huitu.R
import com.demon.huitu.net.model.TaskModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_task_map.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/*
 * 文件名: TaskMapFragment
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/6/12
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
class TaskMapFragment : LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, Fragment() {


    private var mMap: AMap? = null
    private var mListener: LocationSource.OnLocationChangedListener? = null
    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mMyLocation: AMapLocation? = null
    private var mLastMarker: Marker? = null
    private val mMarkerTaskCache: HashMap<MarkerOptions, TaskModel> = HashMap()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater?.inflate(R.layout.fragment_task_map, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMapIfNeeded()
        initMap()
        location()
        initInfoContainer()
    }

    private fun initInfoContainer() {
        task_info_container.setOnClickListener { startActivity(Intent(activity, ReportCommitActivity::class.java)) }
    }

    private fun setUpMapIfNeeded() {
        if (mMap == null) {
            val fragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mMap = fragment.map
        }
    }

    private fun initMap() {
        mMap?.setLocationSource(this)
        mMap?.isMyLocationEnabled = true
        mMap?.uiSettings?.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        mMap?.setOnMarkerClickListener(AMap.OnMarkerClickListener {
            //            it.showInfoWindow()
            task_info_container.visibility = View.VISIBLE
            task_info_name.text = mMarkerTaskCache.getValue(it.options).name
            price.text = "${mMarkerTaskCache.getValue(it.options).price}元"
            task_info_location.text = mMarkerTaskCache.getValue(it.options).locationStr
            distance.text = "${mMarkerTaskCache.getValue(it.options).distance}米"
            true
        })
        mMap?.setOnMapClickListener(AMap.OnMapClickListener {
            task_info_container.visibility = View.GONE
        })
    }

    private fun location() {
        var locationStyle = MyLocationStyle()
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        mMap?.myLocationStyle = locationStyle
    }

    private fun updateTask(markers: ArrayList<MarkerOptions>) {
        mMap?.clear()
        mMap?.addMarkers(markers, true)
    }

    operator fun Double.minus(b: Double?): Double {
        if (null == b) {
            Double.NaN
        }
        return this - b
    }

    operator fun Double.plus(b: Double?): Double {
        if (null == b) {
            Double.NaN
        }
        return this + b
    }


    private fun initOrUpdateTask() {
        val tasks: ArrayList<TaskModel> = ArrayList()
        val random: Random = Random()
        for (i in 0..10) {
            var taskModel: TaskModel = TaskModel()
            val myLat: Double = if (null == mMyLocation) Double.NaN else mMyLocation!!.latitude
            val myLng = if (null == mMyLocation) Double.NaN else mMyLocation!!.longitude
            val lat = if (i % 2 == 0) myLat.minus(random.nextDouble()) else myLat.plus(random.nextDouble())
            val lng = if (i % 2 == 0) myLng.minus(random.nextDouble()) else myLng.plus(random.nextDouble())
            taskModel.lat = lat
            taskModel.con = lng
            taskModel.name = "随机的名字$i"
            taskModel.price = i + random.nextInt()
            taskModel.distance = random.nextFloat()
            taskModel.locationStr = "随机的地点$i"
            tasks.add(taskModel)
        }

        var disposable: Disposable = Flowable.fromIterable(tasks).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .map { t: TaskModel ->
                    var options: MarkerOptions = MarkerOptions().icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .position(LatLng(t.lat, t.con))
                            .title("${t.name}")
                            .snippet("${t.locationStr}")
                            .draggable(true)
                    mMarkerTaskCache.put(options, t)
                    options
                }.toList().observeOn(AndroidSchedulers.mainThread()).subscribe { data -> updateTask(data as ArrayList<MarkerOptions>) }

        mCompositeDisposable.add(disposable)
    }

    override fun onLocationChanged(p0: AMapLocation?) {
        if (mListener == null) {
            return
        }
        if (p0?.errorCode == 0) {
            mMyLocation = p0
            initOrUpdateTask()
            mListener?.onLocationChanged(p0)
        } else {
//            Toast.makeText(activity, "定位失败", Toast.LENGTH_SHORT).show()
        }
    }


    override fun deactivate() {
        mListener = null
        mLocationClient?.stopLocation()
        mLocationClient?.onDestroy()
        mLocationClient = null
    }

    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        mListener = p0
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = AMapLocationClient(activity)
            //初始化定位参数
            mLocationOption = AMapLocationClientOption()
            //设置定位回调监听
            mLocationClient?.setLocationListener(this)
            //设置为高精度定位模式
            mLocationOption?.locationMode = AMapLocationMode.Hight_Accuracy
            //设置定位参数
            mLocationClient?.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient?.startLocation()//启动定位
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        mLastMarker?.hideInfoWindow()
        p0?.showInfoWindow()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient?.onDestroy()
        mCompositeDisposable.dispose()
    }

    companion object {
        private var fragment: TaskMapFragment? = null

        fun newInstance(): Fragment {
            fragment = TaskMapFragment()
            return fragment as Fragment
        }
    }

}