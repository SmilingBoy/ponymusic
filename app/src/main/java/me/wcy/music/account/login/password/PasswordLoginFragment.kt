package me.wcy.music.account.login.password

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.account.login.LoginRouteFragment
import me.wcy.music.account.service.UserService
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentPasswordLoginBinding
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.utils.ToastUtils
import javax.inject.Inject

/**
 * 账号密码登录页面
 */
@Route(RoutePath.PASSWORD_LOGIN)
@AndroidEntryPoint
class PasswordLoginFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentPasswordLoginBinding>()
    private val viewModel by viewModels<PasswordLoginViewModel>()

    @Inject
    lateinit var userService: UserService

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initView()
    }

    private fun initView() {
        // 更新登录按钮状态的逻辑
        val updateLoginBtnState = {
            viewBinding.btnLogin.isEnabled = viewBinding.etAccount.length() > 0 && viewBinding.etPassword.length() > 0
        }

        // 账号输入框文本变化监听
        viewBinding.etAccount.doAfterTextChanged {
            updateLoginBtnState()
        }

        // 密码输入框文本变化监听
        viewBinding.etPassword.doAfterTextChanged {
            updateLoginBtnState()
        }

        // 登录按钮点击事件
        viewBinding.btnLogin.setOnClickListener {
            val account = viewBinding.etAccount.text?.toString()
            if (account.isNullOrEmpty()) {
                ToastUtils.show("请输入账号")
                return@setOnClickListener
            }

            val password = viewBinding.etPassword.text?.toString()
            if (password.isNullOrEmpty()) {
                ToastUtils.show("请输入密码")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                showLoading(false)
                val res = viewModel.passwordLogin(account, password)
                dismissLoading()
                if (res.isSuccess()) {
//                    com.blankj.utilcode.util.ToastUtils.showShort("登录成功")
//                    ToastUtils.show("登录成功")
                    setResultAndFinish()
                } else {
                    ToastUtils.show(res.msg.orEmpty().ifEmpty {
                        "登录失败，请更新服务端版本或稍后重试"
                    })
                }
            }
        }

        // 切换到手机号登录
        viewBinding.tvPhoneLogin.setOnClickListener {
            activity?.apply {
                setResult(LoginRouteFragment.RESULT_SWITCH_PHONE)
                finish()
            }
        }
    }
}