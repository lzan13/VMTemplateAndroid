package com.vmloft.develop.app.template.ui.post

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivityPostCreateBinding
import com.vmloft.develop.app.template.request.bean.Attachment
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.app.template.request.bean.Category
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.image.IMGChoose
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.app.template.report.ReportConstants
import com.vmloft.develop.library.common.report.ReportManager
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem

import kotlinx.android.synthetic.main.activity_post_create.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/8/07 07:10
 * 描述：发布界面
 */
@Route(path = AppRouter.appPublish)
class PostCreateActivity : BVMActivity<PostViewModel>() {

    private lateinit var mContent: String
    private lateinit var mCategory: Category

    private var picture: Any? = null

    override fun initVM(): PostViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_post_create

    override fun initUI() {
        super.initUI()
        (mBinding as ActivityPostCreateBinding).viewModel = mViewModel

        setTopTitle(R.string.publish_title)
        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_publish)) { planSubmit() }

        publishPictureBtn.setOnClickListener {
            IMGChoose.singlePicture(this) {
                picture = it
                publishPictureBtn.visibility = View.GONE
                IMGLoader.loadCover(publishPictureIV, picture, true, 8)
            }
        }
        publishPictureIV.setOnClickListener {
            CRouter.goDisplaySingle(picture.toString())
        }
        publishPictureCloseIV.setOnClickListener {
            picture = null
            publishPictureBtn.visibility = View.VISIBLE
        }
        // 监听输入框变化
        publishContentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mContent = s.toString().trim()
                verifyInputBox()
            }
        })
    }


    override fun initData() {
        mViewModel.getCategoryList()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "categoryList") {
            if (model.data is RPaging<*>) {
                bindCategoryData(model.data as RPaging<Category>)
            }
        } else if (model.type == "uploadPicture") {
            submit(model.data as Attachment)
        } else if (model.type == "createPost") {
            showBar(R.string.publish_success_hint)
            LDEventBus.post(Constants.createPostEvent, 0)
            VMSystem.runInUIThread({ finish() }, CConstants.timeSecond)
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        // 检查输入框是否为空
        setTopEndBtnEnable(!mContent.isNullOrEmpty())
    }

    /**
     * 准备
     */
    private fun planSubmit() {
        if (picture == null) {
            submit(null)
        } else {
            mViewModel.uploadPicture(picture!!)
        }
    }

    /**
     * 创建
     */
    private fun submit(attachment: Attachment?) {
        if (!VMReg.isCommonReg(mContent, "^[\\s\\S]{1,800}\$")) {
            return errorBar(R.string.publish_content_hint)
        }
//        val title = if (mContent.length > 16) mContent.substring(0, 16) else mContent

        val list = mutableListOf<String>()
        attachment?.let {
            list.add(it.id)
        }
        // 上报内容发布
        ReportManager.reportEvent(ReportConstants.eventPostCreate)

        mViewModel.createPost(mContent, mCategory.id, list)
    }

    /**
     * -----------------------------------------------------------------------------
     */
    /**
     * 绑定分类数据
     */
    private fun bindCategoryData(page: RPaging<Category>) {
        val list = page.data
        // 默认选择第一个
        mCategory = list[0]

        val adapter = SpinnerAdapter(this, list)

        publishCategorySpinner.adapter = adapter
        publishCategorySpinner.setPopupBackgroundResource(R.drawable.shape_card_common_bg)
//        publishCategorySpinner.dropDownVerticalOffset = VMDimen.dp2px(36)
        publishCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mCategory = list[position]
                adapter.setCurrPosition(position)
            }
        }
    }

    /**
     * 自定义 Spinner 适配器
     */
    class SpinnerAdapter(val context: Context, val list: List<Category>) : BaseAdapter() {

        private var currPosition = 0

        fun setCurrPosition(position: Int) {
            currPosition = position
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_publish_category_spinner, parent, false)

//            val itemIconIV = itemView.findViewById<ImageView>(R.id.categoryItemIconIV)
            val itemTV = itemView.findViewById<TextView>(R.id.categoryItemTV)

            itemTV.text = list[position].title
            return itemView
        }
    }


}
