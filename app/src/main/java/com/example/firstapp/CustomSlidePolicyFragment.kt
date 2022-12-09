package com.example.firstapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy

class CustomSlidePolicyFragment : Fragment(), SlidePolicy {

    private lateinit var checkBox: CheckBox
    private lateinit var imageView: ImageView
    private lateinit var button : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.intro_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.iv_intro_fragment)
        button = view.findViewById(R.id.btn_intro_fragment)
        button.setOnClickListener {
            Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
            // Todo : add permission logic here
            imageView.setImageResource(R.drawable.ic_baseline_check_24_green)
            imageView.tag = R.drawable.ic_baseline_check_24_green
        }
    }

    override val isPolicyRespected: Boolean
        get() =  imageView.tag == R.drawable.ic_baseline_check_24_green

    override fun onUserIllegallyRequestedNextPage() {
        Toast.makeText(
            requireContext(),
            "Please check the checkbox to continue",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance() : CustomSlidePolicyFragment {
            return CustomSlidePolicyFragment()
        }
    }
}