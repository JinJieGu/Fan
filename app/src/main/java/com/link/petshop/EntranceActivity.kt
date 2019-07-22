package com.link.petshop

import android.os.Bundle
import androidx.navigation.findNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.link.librarycomponent.router.RouterConstant
import com.link.librarymodule.base.BaseActivity
import com.link.petshop.R

/**
 * @author WJ
 * @date 2019-07-21
 *
 * 描述：入口activity
 */
@Route(path = RouterConstant.APP)
class EntranceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_container)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.container).navigateUp()
    }


}