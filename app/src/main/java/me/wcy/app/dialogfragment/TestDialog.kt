package me.wcy.app.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import me.wcy.app.databinding.FragmentDialogBinding
import me.wcy.app.viewBindings

/**
 * Created by wangchenyan.top on 2021/12/23.
 */
class TestDialog : DialogFragment() {
    private val viewBinding by viewBindings<FragmentDialogBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnClose.setOnClickListener {
            dismiss()
        }
    }
}