package me.wcy.music.account.login

import android.view.View
import top.wangchenyan.common.ext.viewBindings
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentLoginRouteBinding
import me.wcy.router.CRouter
import me.wcy.router.RouteResultListener
import me.wcy.router.annotation.Route

/**
 * Created by wangchenyan.top on 2024/1/3.
 */
@Route(RoutePath.LOGIN)
class LoginRouteFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentLoginRouteBinding>()

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun isLazy(): Boolean {
        return false
    }

    private val routeResultListener: RouteResultListener = {
        if (it.isSuccess()) {
            setResultAndFinish()
        } else if (it.resultCode == RESULT_SWITCH_QRCODE) {
            startQrCode()
        } else if (it.resultCode == RESULT_SWITCH_PHONE) {
            startPhone()
        } else if (it.resultCode == RESULT_SWITCH_PASSWORD) {
            startPassword()
        } else {
            finish()
        }
    }

    override fun onLazyCreate() {
        super.onLazyCreate()
        startPassword()
    }

    private fun startPhone() {
        CRouter.with(requireActivity())
            .url(RoutePath.PHONE_LOGIN)
            .startForResult(routeResultListener)
    }

    private fun startQrCode() {
        CRouter.with(requireActivity())
            .url(RoutePath.QRCODE_LOGIN)
            .startForResult(routeResultListener)
    }

    private fun startPassword() {
        CRouter.with(requireActivity())
            .url(RoutePath.PASSWORD_LOGIN)
            .startForResult(routeResultListener)
    }

    companion object {
        const val RESULT_SWITCH_QRCODE = 100
        const val RESULT_SWITCH_PHONE = 200
        const val RESULT_SWITCH_PASSWORD = 300
    }
}