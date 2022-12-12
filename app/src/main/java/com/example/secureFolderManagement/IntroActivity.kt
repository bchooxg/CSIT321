package com.example.secureFolderManagement

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.appintro.*

class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        isWizardMode = true
//        setTransformer(
//            AppIntroPageTransformerType.Parallax(
//            titleParallaxFactor = 1.0,
//            imageParallaxFactor = -1.0,
//            descriptionParallaxFactor = 2.0
//        ))
        setIndicatorColor(
            selectedIndicatorColor = getColor(R.color.proj_select),
            unselectedIndicatorColor = getColor(R.color.prog_unselect)
        )


        // Slide 1
        addSlide(
            AppIntroFragment.createInstance(
                title = "Secure Folder Management",
                description = "Your one stop shop for all your file management needs",
                imageDrawable = R.drawable.guy_and_files,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                titleTypefaceFontRes = R.font.open_sans,
                descriptionTypefaceFontRes = R.font.open_sans,
        ))
        // Slide 2
        addSlide(AppIntroFragment.createInstance(
            title = "Privacy",
            description = "Keep sensitive files hidden on your device ",
            imageDrawable = R.drawable.privacy,
            titleColorRes = R.color.black,
            descriptionColorRes = R.color.black,
            titleTypefaceFontRes = R.font.open_sans,
            descriptionTypefaceFontRes = R.font.open_sans,
        ))
        // Slide 3
        addSlide(AppIntroFragment.createInstance(
            title = "Security",
            description =  "Pin protect your files for ease of mind",
            imageDrawable = R.drawable.security,
            titleColorRes = R.color.black,
            descriptionColorRes = R.color.black,
            titleTypefaceFontRes = R.font.open_sans,
            descriptionTypefaceFontRes = R.font.open_sans,
        ))
        // Slide 4
        addSlide(AppIntroFragment.createInstance(
            title = "But First...",
            description =  "Our app needs some permissions to work properly",
            imageDrawable = R.drawable.finger,
            titleColorRes = R.color.black,
            descriptionColorRes = R.color.black,
            titleTypefaceFontRes = R.font.open_sans,
            descriptionTypefaceFontRes = R.font.open_sans,
        ))
        // Slide 5
        addSlide(CustomSlidePolicyFragment.newInstance())
        // Slide 6
        addSlide(AppIntroFragment.createInstance(
            title = "Lets get started",
            description =  "You are all ready to use the app",
            imageDrawable = R.drawable.man_with_box,
            titleColorRes = R.color.black,
            descriptionColorRes = R.color.black,
            titleTypefaceFontRes = R.font.open_sans,
            descriptionTypefaceFontRes = R.font.open_sans,
        ))
        // Passed all slides moving to file list


    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        val intent = Intent(this, fileList::class.java)
        var path = Environment.getExternalStorageDirectory().path
        path += "/" + resources.getString(R.string.folderName)
        updateOnboardingFlag()
        intent.putExtra("path", path)
        startActivity(intent)
        finish()
    }
    override fun onUserDeniedPermission(permissionName: String) {
        // User pressed "Deny" on the permission dialog
        Toast.makeText(this, "Deny Clicked", Toast.LENGTH_SHORT).show()
    }
    override fun onUserDisabledPermission(permissionName: String) {
        // User pressed "Deny" + "Don't ask again" on the permission dialog
        Toast.makeText(this, "Disabled clicked", Toast.LENGTH_SHORT).show()

    }

    private fun updateOnboardingFlag() {
        val sharedPref = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("completedOnboarding", true)
            apply()
        }
    }

}