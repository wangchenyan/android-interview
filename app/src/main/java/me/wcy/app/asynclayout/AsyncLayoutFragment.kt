package me.wcy.app.asynclayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.wcy.app.R
import me.wcy.app.databinding.FragmentComplexBinding

/**
 * Created by wangchenyan.top on 2021/10/28.
 */
class AsyncLayoutFragment : Fragment() {
    private lateinit var viewBinding: FragmentComplexBinding
    private val asyncLayoutHelper by lazy { AsyncLayoutHelper() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return asyncLayoutHelper.onCreateView(
            inflater,
            container,
            R.layout.fragment_complex,
            R.layout.fragment_complex_placeholder
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        asyncLayoutHelper.waitViewCreated {
            viewBinding = FragmentComplexBinding.bind(it)
            viewBinding.text.text = "onViewCreated"
        }
    }
}