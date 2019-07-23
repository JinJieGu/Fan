package com.link.component_login.app.login

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.LogInListener
import com.alibaba.android.arouter.launcher.ARouter
import com.link.component_login.app.register.RegisterFragment
import com.link.component_login.data.LoginRepository
import com.link.librarycomponent.entity.user.UserEntity
import com.link.librarycomponent.router.RouterConstant
import com.link.librarymodule.base.mvvm.viewmodel.BaseViewModel
import com.link.librarymodule.bus.event.SingleLiveEvent
import com.link.librarymodule.utils.AppManager
import com.link.librarymodule.utils.ToastUtils

class LoginViewModel constructor(model: LoginRepository) : BaseViewModel<LoginRepository>(model) {

    //用户名的绑定
    val phone = MutableLiveData<String>()
    //密码的绑定
    val password = MutableLiveData<String>()

    //封装一个界面发生改变的观察者
    var uc = UIChangeObservable()

    inner class UIChangeObservable {
        //密码开关观察者
        var pSwitchEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    }

    init {
        phone.value=getModel().getUserInfo().mobilePhoneNumber
        password.value=getModel().getUserInfo().pwd
        uc.pSwitchEvent.value=false
    }

    //清除用户名的点击事件, 逻辑从View层转换到ViewModel层
    fun clearPhone(){
        phone.value=null
    }


    /**
     * 登录操作
     */
    fun login() {

        if (TextUtils.isEmpty(phone.value)) {
            ToastUtils.showShort("请输入手机号码！")
            return
        }
        if (TextUtils.isEmpty(password.value)) {
            ToastUtils.showShort("请输入密码！")
            return
        }

        val userEntity=UserEntity()

        BmobUser.loginByAccount(phone.value,password.value,object : LogInListener<UserEntity>(){

            override fun done(user: UserEntity?, e: BmobException?) {
                if (e!=null){
                    ToastUtils.showShort(e.toString())
                    Log.e("error", e.toString());
                }else{
                    ToastUtils.showLong("登录成功")
                    AppManager.instance.finishAllActivity()
                    ARouter.getInstance().build(RouterConstant.APP).navigation()
                }
            }

        })

    }


}