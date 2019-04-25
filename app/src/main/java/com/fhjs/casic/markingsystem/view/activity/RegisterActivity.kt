package com.fhjs.casic.markingsystem.view.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.fhjs.casic.markingsystem.R
import com.fhjs.casic.markingsystem.base.BaseActivity
import com.fhjs.casic.markingsystem.model.ScoringBean
import com.fhjs.casic.markingsystem.model.UserBean
import com.fhjs.casic.markingsystem.util.ActivityUtil
import com.fhjs.casic.markingsystem.util.BmobUtil
import com.fhjs.casic.markingsystem.util.ConstantUtil
import com.fhjs.casic.markingsystem.util.Utils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.pub_title.*
import org.w3c.dom.Text

class RegisterActivity : BaseActivity(), View.OnClickListener {

    private var departStr: Array<String>? = null
    private var postStr: Array<String>? = null
    private var groupStr: Array<String>? = null
    private val levelStr = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    override fun init() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initView()
        initData()

    }

    fun initView() {
        title_text.text = "注册账号"
        title_right.text = "注册"
    }

    private fun initData() {
        getSelectData()
        title_right.setOnClickListener(this)
        title_image.setOnClickListener(this)
        register_department_text.setOnClickListener(this)
        register_post_text.setOnClickListener(this)
        register_group_text.setOnClickListener(this)
        register_level_text.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.title_right -> registerUser()//注册
            R.id.title_image -> ActivityUtil.getInstance().finishActivity()//回退
            R.id.register_department_text -> selectText(v as TextView, departStr!!)
            R.id.register_post_text -> selectText(v as TextView, postStr!!)
            R.id.register_group_text -> selectText(v as TextView, groupStr!!)
            R.id.register_level_text -> selectText(v as TextView, levelStr)
        }
    }

    /**
     * 注册账户的相关逻辑
     */
    private fun registerUser() {
        val user = register_user_edit.text
        val name = register_name_edit.text
        val pwd = register_pwd_edit.text
        val againPwd = register_again_pwd_edit.text
        val post = register_post_text.text
        val department = register_department_text.text
        val group = register_group_text.text
        val level = register_level_text.text

        if (user.isNullOrBlank() || name.isNullOrBlank() || pwd.isNullOrBlank() || againPwd.isNullOrBlank()
                || post.isNullOrBlank() || department.isNullOrBlank() || group.isNullOrBlank() || level.isNullOrBlank()) {
            showToast("请输入完整信息")
            return
        }
        if (pwd!=againPwd) {
            showToast("两次输入的密码不一致")
            Log.e(TAG,"PWD"+pwd+"====>"+againPwd+"是否相等："+(pwd==againPwd));
            return
        }
        val userBean = UserBean()
        userBean.username = user.toString()
        userBean.admin = false
        userBean.setPassword(pwd.toString())
        userBean.userName_CHS = name.toString()
        userBean.department = Integer.parseInt(register_department_text.tag.toString())
        userBean.post = Integer.parseInt(register_post_text.tag.toString())
        userBean.group = Integer.parseInt(register_group_text.tag.toString())
        userBean.level = Integer.parseInt(register_level_text.tag.toString())
        userBean.signUp(object : SaveListener<UserBean>() {
            override fun done(userBean: UserBean, e: BmobException?) {
                if (e == null) {
                    register_user_edit.text = Editable.Factory.getInstance().newEditable("")
                    register_name_edit.text = Editable.Factory.getInstance().newEditable("")
                    register_pwd_edit.text = Editable.Factory.getInstance().newEditable("")
                    register_again_pwd_edit.text = Editable.Factory.getInstance().newEditable("")
                    register_post_text.text = Editable.Factory.getInstance().newEditable("")
                    register_department_text.text = Editable.Factory.getInstance().newEditable("")
                    register_group_text.text = Editable.Factory.getInstance().newEditable("")
                    register_level_text.text = Editable.Factory.getInstance().newEditable("")

                    showToast("添加成功")
                } else {
                    showToast("添加失败" + e.message)
                }
            }
        })
    }


    private fun selectText(view: TextView, data: Array<String>) {
        Utils.hideInput(this)
        XPopup.Builder(this).asBottomList("请选择一项", data
        ) { position, text ->
            view.tag = position
            view.text = text
        }.show()
    }


    //提前加载选择框的数据
    private fun getSelectData() {

        departStr = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_DEPART)
        postStr = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_POST)
        groupStr = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_GROUP)


    }

}
