package com.myapp.qrcodebuilder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.myapp.qrcodebuilder.databinding.ActivityBuilderBinding

class BuilderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuilderBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var fileRef: StorageReference
    private lateinit var downloadURL: String
    private val storageRef = Firebase.storage.reference
    private val messageFragment = BuildMessageFragment()
    private val qrFragment = QrFragment()
    private var selectedFile: Uri? = null
    private var filename: String? = null
    private var fileMime: String? = null
    private var onFragment = false
    private var onFile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuilderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        initializeLauncher()
        setupButtons()
    }
    private fun initializeLauncher() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    val documentFile = DocumentFile.fromSingleUri(this,it)
                    filename = documentFile?.name
                    val fileExtension = documentFile?.name?.substringAfterLast('.',"")
                    fileMime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
                        ?.substringBeforeLast('/')
                    selectedFile = it
                }
                onFragment = true
                fileReferenceInit()
                uploadFile()
            }
        }
    }
    private fun setupButtons() {
        binding.btnBuildMessage.setOnClickListener {
            onFragment = true
            binding.btnBuildMessage.visibility = View.INVISIBLE
            binding.btnOpenFile.visibility = View.INVISIBLE
            binding.tvMessage.visibility = View.INVISIBLE
            binding.tvFile.visibility = View.INVISIBLE
            binding.btnBuild.visibility = View.VISIBLE
            setCurrentFragment(messageFragment).addToBackStack(null)
        }
        binding.btnOpenFile.setOnClickListener {
            openFile()
        }
        binding.btnBuild.setOnClickListener {
            messageFragment.shareData()
            binding.btnBuild.visibility = View.INVISIBLE
            setCurrentFragment(qrFragment).addToBackStack(null)
        }
    }
    private fun fileReferenceInit() {
        val sharedPreferences = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("user","")
        when (fileMime) {
            "image" -> fileRef = storageRef.child("$user").child("images/$filename")
            "application" -> fileRef = storageRef.child("$user").child("files/$filename")
            "video" -> fileRef = storageRef.child("$user").child("videos/$filename")
            "audio" -> fileRef = storageRef.child("$user").child("audios/$filename")
        }
    }
    private fun uploadFile() {
        try {
            selectedFile?.let { fileRef.putFile(it) }

            fileRef.downloadUrl.addOnSuccessListener { uri ->
                downloadURL = uri.toString()

                urlToQR()

                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                onFragment = false
                Log.e("Download URL", "Failed to retrieve download URL: ${exception.message}")
            }
        } catch (e: Exception) {
            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun openFile() {
        val pickerInitialUri: Uri? = null
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        launcher.launch(intent)
    }
    private fun urlToQR() {
        onFile = true
        val sharedPreferences = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("content",downloadURL).apply()
        binding.btnBuildMessage.visibility = View.INVISIBLE
        binding.btnOpenFile.visibility = View.INVISIBLE
        binding.tvMessage.visibility = View.INVISIBLE
        binding.tvFile.visibility = View.INVISIBLE
        setCurrentFragment(qrFragment).addToBackStack(null)
    }
    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    private fun removeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            remove(fragment)
            commit()
        }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (onFragment) {
                if (messageFragment.isVisible) {
                    binding.btnBuildMessage.visibility = View.VISIBLE
                    binding.btnOpenFile.visibility = View.VISIBLE
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.tvFile.visibility = View.VISIBLE
                    binding.btnBuild.visibility = View.INVISIBLE
                    removeCurrentFragment(messageFragment)
                    onFragment = false
                }
                if (qrFragment.isVisible) {
                    removeCurrentFragment(qrFragment)
                    if (onFile) {
                        binding.btnBuildMessage.visibility = View.VISIBLE
                        binding.btnOpenFile.visibility = View.VISIBLE
                        binding.tvMessage.visibility = View.VISIBLE
                        binding.tvFile.visibility = View.VISIBLE
                        onFile = false
                        onFragment = false
                    } else {
                        binding.btnBuild.visibility = View.VISIBLE
                        setCurrentFragment(messageFragment).addToBackStack(null)
                    }
                }
            } else {
                finish()
            }
        }
    }
}