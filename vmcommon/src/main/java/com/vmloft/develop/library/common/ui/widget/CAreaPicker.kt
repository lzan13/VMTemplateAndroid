package com.vmloft.develop.library.common.ui.widget

import android.content.Context
import android.content.res.AssetManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

import com.aigestudio.wheelpicker.model.City
import com.aigestudio.wheelpicker.model.Province

import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.common.databinding.WidgetCommonPickerAreaBinding

import java.io.ObjectInputStream

/**
 * Create by lzan13 2020/12/12
 * 地区选择器
 */
class CAreaPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    val mBinding = WidgetCommonPickerAreaBinding.inflate(LayoutInflater.from(context), this, true)

    private var mProvinceList: MutableList<Province> = mutableListOf()
    private var mCityList: MutableList<City> = mutableListOf()

    init {
        loadDataFromAssets(context.assets)
        initPicker()
    }

    fun getProvince(): String {
        return mProvinceList[mBinding.commonPickerProvince.currentItemPosition].getName()
    }

    fun getCity(): String {
        return mCityList[mBinding.commonPickerCity.currentItemPosition].getName()
    }

    fun getArea(): String {
        return mCityList[mBinding.commonPickerCity.currentItemPosition].getArea()[mBinding.commonPickerArea.currentItemPosition]
    }

    /**
     * 从 Assets 加载数据
     */
    private fun loadDataFromAssets(assetManager: AssetManager) {
        try {
            val inputStream = assetManager.open("RegionJsonData.dat")
            val objectInputStream = ObjectInputStream(inputStream)
            val list: List<Province> = objectInputStream.readObject() as List<Province>
            mProvinceList.addAll(list)
            objectInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPicker() {
        mBinding.commonPickerProvince.setOnItemSelectedListener { _, _, position ->
            fillCityAndArea(position)
        }
        mBinding.commonPickerCity.setOnItemSelectedListener { _, _, position ->
            mBinding.commonPickerArea.data = mCityList[position].area
        }

        val provinceList = mProvinceList.map { it.name }
        mBinding.commonPickerProvince.data = provinceList

        fillCityAndArea(0)
    }

    /**
     * 填充市区数据
     */
    private fun fillCityAndArea(position: Int) {
        mCityList = mProvinceList[position].city

        val cityList = mCityList.map { it.name }
        mBinding.commonPickerCity.data = cityList
        mBinding.commonPickerCity.selectedItemPosition = 0

        mBinding.commonPickerArea.data = mCityList[0].area
        mBinding.commonPickerArea.selectedItemPosition = 0
    }

}