package com.myapp.qrcodebuilder

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.myapp.qrcodebuilder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var barLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private var onFragment = false
    private val messageFragment = MessageDisplay()
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isLoading.value }
        }
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        auth = Firebase.auth

        signIn()
        setupBarLauncher()
        setupButtons()
    }
    private fun setupButtons() {
        binding.iBtnBuild.setOnClickListener {
            startActivity(Intent(this,BuilderActivity::class.java))
        }
        binding.iBtnScan.setOnClickListener {
            scanCode()
        }
    }
    private fun setupBarLauncher() {
        barLauncher = registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null){
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(result.contents)
                if (fileExtension != "") {
                    downloadFile(result.contents)
                } else {
                    readMsg(result.contents)
                }
            }
        }
    }
    private fun scanCode() {
        val options = ScanOptions()
        options.setPrompt("Volume up to flash")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = CaptureAct::class.java
        barLauncher.launch(options)
    }
    private fun downloadFile(url: String) {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        val title = URLUtil.guessFileName(url,null,null)
        request.setTitle(title)
        request.setDescription("Downloading please wait.....")
        val cookie = CookieManager.getInstance().getCookie(url)
        request.addRequestHeader("cookie",cookie)
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
        request.setMimeType(mimeType)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(this,"Downloading Started", Toast.LENGTH_SHORT).show()
    }
    private fun readMsg(contents: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("msg",contents).apply()
        binding.iBtnBuild.visibility = View.INVISIBLE
        binding.iBtnScan.visibility = View.INVISIBLE
        binding.tvBuilder.visibility = View.INVISIBLE
        binding.tvScanner.visibility = View.INVISIBLE
        setCurrentFragment(messageFragment).addToBackStack(null)
        onFragment = true
    }
    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment,fragment)
            commit()
        }
    private fun removeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            remove(fragment)
            commit()
        }
    private fun signIn() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Authentication Success!",Toast.LENGTH_SHORT).show()
                    user = auth.currentUser!!.uid
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("user",user).apply()
                    Log.i("Firebase User",user)
                } else {
                    Toast.makeText(this,"Authentication Failed!",Toast.LENGTH_SHORT).show()
                }
            }
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (onFragment) {
                binding.iBtnBuild.visibility = View.VISIBLE
                binding.iBtnScan.visibility = View.VISIBLE
                binding.tvBuilder.visibility = View.VISIBLE
                binding.tvScanner.visibility = View.VISIBLE
                removeCurrentFragment(messageFragment)
                onFragment = false
            } else {
                finish()
            }
        }
    }
}