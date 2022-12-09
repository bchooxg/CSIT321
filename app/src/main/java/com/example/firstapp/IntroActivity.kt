package com.example.firstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType

class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        isColorTransitionsEnabled = true
        setTransformer(
            AppIntroPageTransformerType.Parallax(
            titleParallaxFactor = 1.0,
            imageParallaxFactor = -1.0,
            descriptionParallaxFactor = 2.0
        ))

        addSlide(
            AppIntroFragment.createInstance(
                title = "Secure Folder Management",
                description = "Your one stop shop for all your file management needs",
                imageDrawable = R.drawable.security,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                titleTypefaceFontRes = R.font.open_sans,
                descriptionTypefaceFontRes = R.font.open_sans,
        ))
        addSlide(AppIntroFragment.createInstance(
            title = "Privacy",
            description = "Keep sensitive files hidden on your device ",
            imageDrawable = R.drawable.security,
            titleColorRes = R.color.black,
            descriptionColorRes = R.color.black,
            titleTypefaceFontRes = R.font.open_sans,
            descriptionTypefaceFontRes = R.font.open_sans,
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        finish()
    }

}