package com.fhjs.casic.markingsystem.view.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import cn.bmob.v3.BmobBatch
import cn.bmob.v3.BmobObject
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BatchResult
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListListener
import com.fhjs.casic.markingsystem.R
import com.fhjs.casic.markingsystem.base.BaseActivity
import com.fhjs.casic.markingsystem.model.AssessmentBean
import com.fhjs.casic.markingsystem.model.ObjectBean
import com.fhjs.casic.markingsystem.model.ScoringBean
import com.fhjs.casic.markingsystem.util.*
import kotlinx.android.synthetic.main.activity_pa_marking.*
import kotlinx.android.synthetic.main.pub_title.*
import kotlinx.android.synthetic.main.include_cardview_info.*
import java.util.ArrayList
import java.nio.file.Files.size
import com.lxj.xpopup.XPopup


class PAMarkingActivity : BaseActivity(), View.OnClickListener, RatingBar.OnRatingBarChangeListener {
    val top = 1
    val center = 2
    val bottom = 3
    private val initSize = 5//打分内容默认个数
    private var typeArray: Array<String>? = null
    private var assessArray: Array<String>? = null//当前内容的数组
    private var contentArray1: List<AssessmentBean>? = null
    private var contentArray2: List<AssessmentBean>? = null
    private var contentArray3: List<AssessmentBean>? = null
    private var titleStr1: String? = null
    private var titleStr2: String? = null
    private var titleStr3: String? = null
    private lateinit var objectBeans: List<ObjectBean>
    private lateinit var ob: ObjectBean
    private var alreadyMarkMap = hashMapOf<String, String>()//key:evaluationID value: objectId
    private var topRBArray = arrayOfNulls<Float>(initSize)
    private var centerRBArray = arrayOfNulls<Float>(initSize)
    private var bottomRBArray = arrayOfNulls<Float>(initSize)
    private var isMultiple = false

    override fun init() {
        var dateType = intent.getIntExtra("dataType", ConstantUtil.INTENT_SINGLE_DATA)
        isMultiple = (dateType == ConstantUtil.INTENT_MULTI_DATA)
        if (isMultiple) {
            objectBeans = intent.getSerializableExtra("sameTypeData") as List<ObjectBean>
            ob = objectBeans[0]
        } else {
            ob = intent.getSerializableExtra("bean") as ObjectBean
        }
        Log.e(TAG, "$isMultiple        ")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pa_marking)
        initView()
        setListener()
        setTextContent()
        initAlreadyMark()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.title_right -> markingSubmit()//提交打分
            R.id.title_image -> ActivityUtil.getInstance().finishActivity()//回退
        }
    }

    override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
        when (ratingBar.id) {
            R.id.include_marking_ratingBar_1 -> setScoreText(top, 0, rating)
            R.id.include_marking_ratingBar_2 -> setScoreText(top, 1, rating)
            R.id.include_marking_ratingBar_3 -> setScoreText(top, 2, rating)
            R.id.include_marking_ratingBar_4 -> setScoreText(top, 3, rating)
            R.id.include_marking_ratingBar_5 -> setScoreText(top, 4, rating)

            R.id.include_marking_ratingBar_1_center -> setScoreText(center, 0, rating)
            R.id.include_marking_ratingBar_2_center -> setScoreText(center, 1, rating)
            R.id.include_marking_ratingBar_3_center -> setScoreText(center, 2, rating)
            R.id.include_marking_ratingBar_4_center -> setScoreText(center, 3, rating)
            R.id.include_marking_ratingBar_5_center -> setScoreText(center, 4, rating)

            R.id.include_marking_ratingBar_1_bottom -> setScoreText(bottom, 0, rating)
            R.id.include_marking_ratingBar_2_bottom -> setScoreText(bottom, 1, rating)
            R.id.include_marking_ratingBar_3_bottom -> setScoreText(bottom, 2, rating)
            R.id.include_marking_ratingBar_4_bottom -> setScoreText(bottom, 3, rating)
            R.id.include_marking_ratingBar_5_bottom -> setScoreText(bottom, 4, rating)
        }

    }

    //设置已打分的文本内容
    private fun setScoreText(type: Int, index: Int, score: Float) {
        when (type) {
            (top) -> {
                topRBArray[index] = score
                var score1 = 0f
                for (item in topRBArray) {
                    score1 += item ?: 0f
                }
                include_marking_pa_score_top.text = "${score1}"
            }
            (center) -> {
                centerRBArray[index] = score
                var score2 = 0f
                for (item in centerRBArray) {
                    score2 += item ?: 0f
                }
                include_marking_pa_score_center.text = "${score2}"
            }
            (bottom) -> {
                bottomRBArray[index] = score
                var score3 = 0f
                for (item in bottomRBArray) {
                    score3 += item ?: 0f
                }
                include_marking_pa_score_bottom.text = "${score3}"
            }
        }
    }

    /**
     * 设置基本数据
     */
    private fun setTextContent() {
        if (isMultiple) {
            include_marking_pa_single_layout.visibility = View.GONE
            include_marking_pa_multiple_layout.visibility = View.VISIBLE
            var objectBuff = StringBuffer()
            for (i in 0 until objectBeans.size) {
                objectBuff.append(objectBeans[i].name)
                if (i != objectBeans.size - 1) {
                    objectBuff.append("、")
                }
            }

            var typeStr = "考核类型：${Utils.getIntToNumber(ob.pa_type)}类 (${titleStr1}、${titleStr2}、${titleStr3})"
            include_marking_pa_object_content_text.text = objectBuff.toString()
            include_marking_pa_object_count_text.text = "共${objectBeans.size}人"
            include_marking_pa_type_text.text = typeStr
            include_marking_pa_date_text.text = "操作日期：" + DateTimeUtil.gainCurrentDateString()
            include_marking_pa_user_text.text = "操作人员：" + getUser().userName_CHS

        } else {
            include_marking_pa_single_layout.visibility = View.VISIBLE
            include_marking_pa_multiple_layout.visibility = View.GONE
            cardview_name.text = ob.name
            cardview_sex.text = ob.sex
            cardview_age.text = ob.age.toString()
            cardview_phone.text = ob.phone
            cardview_post.text = "岗位："+BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_POST,ob.post)
            cardview_department.text = "部门："+BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_DEPART,ob.department)
            cardview_date.text = ob.date
        }

    }

    private fun initView() {
        var type = BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_ASSESS_TYPE, ob.pa_type)

        typeArray = splitString(type, ",")//1,2,3===>int[]{1,2,3}
        assessArray = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_ASSESS)
        contentArray1 = BmobUtil.getInstance().getAssessContent(typeArray!![0].toInt())
        contentArray2 = BmobUtil.getInstance().getAssessContent(typeArray!![1].toInt())
        contentArray3 = BmobUtil.getInstance().getAssessContent(typeArray!![2].toInt())
        titleStr1 = assessArray?.get(typeArray!![0].toInt())
        titleStr2 = assessArray?.get(typeArray!![1].toInt())
        titleStr3 = assessArray?.get(typeArray!![2].toInt())

        title_text.text = "批量考核"
        include_marking_pa_title.text = titleStr1
        include_marking_pa_title_center.text = titleStr2
        include_marking_pa_title_bottom.text = titleStr3

        //设置内容值 top
        include_marking_pa_text1.text = contentArray1?.get(0)?.value
        include_marking_pa_text2.text = contentArray1?.get(1)?.value
        include_marking_pa_text3.text = contentArray1?.get(2)?.value
        include_marking_pa_text4.text = contentArray1?.get(3)?.value
        include_marking_pa_text5.text = contentArray1?.get(4)?.value
        //设置内容值 center
        include_marking_pa_text1_center.text = contentArray2?.get(0)?.value
        include_marking_pa_text2_center.text = contentArray2?.get(1)?.value
        include_marking_pa_text3_center.text = contentArray2?.get(2)?.value
        include_marking_pa_text4_center.text = contentArray2?.get(3)?.value
        include_marking_pa_text5_center.text = contentArray2?.get(4)?.value
        //设置内容值 bottom
        include_marking_pa_text1_bottom.text = contentArray3?.get(0)?.value
        include_marking_pa_text2_bottom.text = contentArray3?.get(1)?.value
        include_marking_pa_text3_bottom.text = contentArray3?.get(2)?.value
        include_marking_pa_text4_bottom.text = contentArray3?.get(3)?.value
        include_marking_pa_text5_bottom.text = contentArray3?.get(4)?.value

        endValueNull()

    }

    //若数组最后一项为空 则隐藏布局
    private fun endValueNull() {

        if (contentArray1?.get(4)?.value.isNullOrEmpty()) {
            include_marking_pa_end_layout_top.visibility = View.GONE
        } else {
            include_marking_pa_end_layout_top.visibility = View.VISIBLE
        }
        if (contentArray2?.get(4)?.value.isNullOrEmpty()) {
            include_marking_pa_end_layout_center.visibility = View.GONE
        } else {
            include_marking_pa_end_layout_center.visibility = View.VISIBLE
        }

        if (contentArray3?.get(4)?.value.isNullOrEmpty()) {
            include_marking_pa_end_layout_bottom.visibility = View.GONE
        } else {
            include_marking_pa_end_layout_bottom.visibility = View.VISIBLE
        }


    }

    private fun setListener() {
        title_right.setOnClickListener(this)
        title_image.setOnClickListener(this)
        include_marking_ratingBar_1.onRatingBarChangeListener = this
        include_marking_ratingBar_2.onRatingBarChangeListener = this
        include_marking_ratingBar_3.onRatingBarChangeListener = this
        include_marking_ratingBar_4.onRatingBarChangeListener = this
        include_marking_ratingBar_5.onRatingBarChangeListener = this

        include_marking_ratingBar_1_center.onRatingBarChangeListener = this
        include_marking_ratingBar_2_center.onRatingBarChangeListener = this
        include_marking_ratingBar_3_center.onRatingBarChangeListener = this
        include_marking_ratingBar_4_center.onRatingBarChangeListener = this
        include_marking_ratingBar_5_center.onRatingBarChangeListener = this

        include_marking_ratingBar_1_bottom.onRatingBarChangeListener = this
        include_marking_ratingBar_2_bottom.onRatingBarChangeListener = this
        include_marking_ratingBar_3_bottom.onRatingBarChangeListener = this
        include_marking_ratingBar_4_bottom.onRatingBarChangeListener = this
        include_marking_ratingBar_5_bottom.onRatingBarChangeListener = this
    }


    private fun splitString(s: String, regex: String): Array<String> {
        return s.split(regex.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * 获取当前用户当日打分的人员的集合
     * 根据打分人和时间查询 查询结果装入  alreadyMark 集合中
     */
    private fun initAlreadyMark() {
        val query = BmobQuery<ScoringBean>()
        query.addWhereEqualTo("userID", BaseActivity.getUser().objectId).addWhereEqualTo("date", ob.date).findObjects(
                object : FindListener<ScoringBean>() {
                    override fun done(list: List<ScoringBean>, e: BmobException?) {
                        if (e == null) {
                            if (list != null) {
                                for (item in list) {

                                    alreadyMarkMap[item.evaluationID] = item.objectId
                                }
                                Log.e(TAG, "已打分${alreadyMarkMap.size}人 详细: ${alreadyMarkMap.values}")
                            }
                        }
                    }
                })
    }

    /**
     * 提交考核成绩的相关逻辑
     */
    private fun markingSubmit() {

        val batch = BmobBatch()
        val addBeans = ArrayList<BmobObject>()
        val updateBeans = ArrayList<BmobObject>()

        if (isMultiple) {//多个考核对象
            for (objects in objectBeans) {//2  xietong huahua
                if (alreadyMarkMap.isEmpty()) {
                    addBeans.add(addMark(objects))
                    Log.e(TAG, "集合为空时新建  addBeans.size：${addBeans.size} ")
                } else {
                    if (alreadyMarkMap.containsKey(objects.objectId)) {//如果已经提交过 则为修改(这里map的key就是objectBean的objectId)
                        Log.e(TAG, " 集合中包含次用户 ===》修改${objects.name}")
                        updateBeans.add(updateMark(objects))
                    } else {
                        addBeans.add(addMark(objects))
                        Log.e(TAG, "集合不为空 但没有此人 ===》添加")
                    }
                }
            }
        } else {
            if (alreadyMarkMap.containsKey(ob.objectId)) {
                updateBeans.add(updateMark(ob))
            } else {
                addBeans.add(addMark(ob))
            }
        }
        batch.insertBatch(addBeans)//批量添加
        batch.updateBatch(updateBeans)//批量更新
        Log.e(TAG, "修改的个数: ${updateBeans.size} 添加的个数:${addBeans.size}")
        batch.doBatch(object : QueryListListener<BatchResult>() {

            override fun done(results: List<BatchResult>, e: BmobException?) {
                if (e == null) {
                    var successCount = 0
                    for (i in 0 until results.size) {
                        val result = results[i]
                        if (result.isSuccess) {//只有批量添加才返回objectId
                            successCount++
                        } else {
                            val error = result.error
                            Log.e(TAG, "失败原因:${error}")
                        }
                        if (isMultiple) {
                            toast(text = "已完成：成功:${successCount}个，失败:${results.size - successCount}个")
                        } else {
                            toast(text = "已完成")
                        }
                        Log.e(TAG, "已完成：成功:${successCount}个，失败:${results.size - successCount}个")
                    }

                    ActivityUtil.getInstance().finishActivity()//回退

                } else {
                    Log.i("bmob", "失败：" + e.message + "," + e.errorCode)
                }
            }
        })
    }

    /**
     *  添加到新建集合的方法
     */
    private fun addMark(item: ObjectBean): ScoringBean {
        val bean = ScoringBean()
        bean.userID = getUser().objectId
        bean.userName = getUser().userName_CHS
        bean.evaluationID = item.objectId
        bean.evaluationName = item.name
        bean.markType = ConstantUtil.ROLE_PAX
        bean.conclusion = null
        bean.date = item.date
        bean.score_A = include_marking_pa_score_top.text.toString()?.toFloat()
        bean.score_B = include_marking_pa_score_center.text.toString()?.toFloat()
        bean.score_C = include_marking_pa_score_bottom.text.toString()?.toFloat()
        return bean
    }

    /**
     *  添加到更新集合的方法
     */
    private fun updateMark(item: ObjectBean): ScoringBean {
        val bean = ScoringBean()
        bean.objectId = alreadyMarkMap[item.objectId]
        bean.userID = getUser().objectId
        bean.userName = getUser().userName_CHS
        bean.evaluationID = item.objectId
        bean.evaluationName = item.name
        bean.date = item.date
        bean.markType = ConstantUtil.ROLE_PAX
        bean.conclusion = null
        bean.score_A = include_marking_pa_score_top.text.toString()?.toFloat()
        bean.score_B = include_marking_pa_score_center.text.toString()?.toFloat()
        bean.score_C = include_marking_pa_score_bottom.text.toString()?.toFloat()
        return bean
    }

    private fun toast(context: Context = this, text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, "已完成", duration).show()
    }
}
