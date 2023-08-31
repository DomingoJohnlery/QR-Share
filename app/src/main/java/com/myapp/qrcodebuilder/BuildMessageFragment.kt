package com.myapp.qrcodebuilder

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.qrcodebuilder.databinding.FragmentBuildMessageBinding

class BuildMessageFragment : Fragment() {

    private lateinit var binding: FragmentBuildMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBuildMessageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textMultiline = binding.textMultiLine
        textMultiline.filters = arrayOf(InputFilter.LengthFilter(2000))
    }


    fun shareData() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("content",binding.textMultiLine.text.toString()).apply()
    }

}