package me.wcy.app.dialogfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.wcy.app.databinding.ActivityDialogFragmentBinding
import me.wcy.app.viewBindings
import me.wcy.app.viewlifecycle.ViewLifecycleActivity

/**
 * Created by wangchenyan.top on 2021/12/23.
 */
class TestFragment : Fragment() {
    private val viewBinding by viewBindings<ActivityDialogFragmentBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnShow.setOnClickListener {
            val dialog = TestDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager.beginTransaction(), "dialog")
        }
        viewBinding.btnJump.setOnClickListener {
            startActivity(Intent(context, ViewLifecycleActivity::class.java))
        }
    }
}