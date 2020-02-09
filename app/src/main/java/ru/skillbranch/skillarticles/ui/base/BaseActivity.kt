package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify

abstract class BaseActivity<T : BaseViewModel<out IViewModelState>> : AppCompatActivity() {
    protected abstract val binding: Binding
    protected abstract var viewModel: T
    protected abstract val layout: Int

    // set listeners, tuning views
    abstract fun setupViews()

    abstract fun renderNotification(notify: Notify)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setupViews()
        binding.onFinishInfale()
        viewModel.observeState(this) { binding.bind(it) }
        viewModel.observeNotifications(this) { renderNotification(it) }
    }
}