package com.link.librarymodule.base.mvvm.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.link.librarymodule.base.BaseFragment
import com.link.librarymodule.base.mvvm.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * Created by goldze on 2017/6/15.
 */
abstract class BaseMvvmFragment<VM : BaseViewModel<*>> : BaseFragment(), IBaseView {
    protected lateinit var mViewModel: VM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initParam()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //解除Messenger注册
//        Messenger.getDefault().unregister(mViewModel)
        //解除ViewModel生命周期感应
        lifecycle.removeObserver(mViewModel)
        mViewModel.removeRxBus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //私有的初始化Databinding和ViewModel方法
        initView()
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack()
        //页面数据初始化方法
        getData()
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()
        //注册RxBus
        mViewModel.registerRxBus()
    }

    fun initViewModel() {
        mViewModel = getViewModel()
        if (mViewModel == null) {
            val modelClass: Class<*>
            val type = javaClass.genericSuperclass
            if (type is ParameterizedType) {
                modelClass = type.actualTypeArguments[1] as Class<VM>
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel::class.java as Class<VM>
            }
            mViewModel = createViewModel(this, modelClass)
        }
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(mViewModel!!)
        //注入RxLifecycle生命周期
        //        mViewModel.injectLifecycleProvider(this);
    }

    /**
     * 初始化UI
     */
    open fun initView() {

    }

    /**
     * =====================================================================
     */
    //注册ViewModel与View的契约UI回调事件
    protected fun registorUIChangeLiveDataCallBack() {
        //跳入新页面
        mViewModel.uc.startActivityEvent.observe(this, Observer { params ->
            val clazz = params!![BaseViewModel.ParameterField.CLASS] as Class<*>
            val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle
            startActivity(clazz, bundle)
        })
        //跳入ContainerActivity
        mViewModel.uc.startContainerActivityEvent.observe(this, Observer { params ->
            val canonicalName = params!![BaseViewModel.ParameterField.CANONICAL_NAME] as String
            val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle?
            startContainerActivity(canonicalName, bundle)
        })
        //关闭界面
        mViewModel.uc.finishEvent.observe(this, Observer { activity!!.finish() })
        //关闭上一层
        mViewModel.uc.onBackPressedEvent.observe(this, Observer { activity!!.onBackPressed() })
    }


    /**
     * =====================================================================
     */

    override fun initParam() {

    }

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    abstract fun getViewModel(): VM


    override fun getData() {

    }

    override fun initViewObservable() {

    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
    </T> */
    fun <T : ViewModel> createViewModel(fragment: Fragment, cls: Class<T>): T {
        return ViewModelProviders.of(fragment).get(cls)
    }

    fun finish() {
        activity!!.finish()
    }
}
