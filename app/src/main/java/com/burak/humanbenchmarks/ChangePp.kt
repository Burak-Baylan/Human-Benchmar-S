package com.burak.humanbenchmarks

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.burak.humanbenchmarks.Messages.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_change_pp.*


class ChangePp : AppCompatActivity() {

    private var selectedPicture : Uri? = null
    private lateinit var viewReal : View
    private lateinit var firebaseManage: FirebaseManage
    private var animationControl : animationControl = animationControl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pp)
        animationControl.forOnCreate(savedInstanceState)
        supportActionBar?.title = "Change Profile Photo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0C4531")))
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#0C4531")

        viewReal = window.decorView.rootView
        ppChangeImage.borderWidth = 2
        val getProfilePhoto = GetProfilePhoto(this,this,viewReal)
        getProfilePhoto.getProfilePhoto(ppChangeImage)
        chooseImageButton.setOnClickListener {
            changeImage(this, this, ppChangeImage)
        }

        ppChangeImage.setOnClickListener{
            changeImage(this, this, ppChangeImage)
        }

        saveProfilePhotoButton.setOnClickListener {
            saveProfilePhotoFun()
        }
    }

    private fun saveProfilePhotoFun(){

        val storage = FirebaseStorage.getInstance()
        val reference = storage.reference
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val saveId= currentUser?.uid
        val imagesReference = reference.child("images").child("$saveId.jpg")
        val firestore = FirebaseFirestore.getInstance()
        val snackCreator = PopupMessageCreator()
        val firebaseManage = FirebaseManage(this,viewReal,this)
        firebaseManage.loadingScreenStarter(false)
        val netControl = firebaseManage.internetControl(this)


        if (netControl) {
            if (selectedPicture != null) {
                imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                    val uploadedPictureReference =
                        FirebaseStorage.getInstance().reference.child("images").child("$saveId.jpg")
                    uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        firestore.collection("Users").document(saveId!!).update("ppurl", downloadUrl)
                            .addOnCompleteListener { task ->
                                if (task.isComplete && task.isSuccessful) {

                                    val userNameGetFromAuth = currentUser.displayName
                                    val profileUpdate = userProfileChangeRequest {
                                        displayName = userNameGetFromAuth
                                        photoUri = Uri.parse(downloadUrl)
                                    }

                                    currentUser.updateProfile(profileUpdate).addOnSuccessListener {

                                        snackCreator.customToast(
                                            this, this, null, Toast.LENGTH_SHORT,
                                            "Profile Photo Changed.",
                                            R.drawable.custom_toast_success, R.drawable.ic_success_image
                                        )
                                        firebaseManage.loadingScreenDestroyer(false)

                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                                    }.addOnFailureListener {
                                        imagesReference.delete().addOnCompleteListener {
                                            firestore.collection("ProfilePhotos").document(saveId)
                                                .delete().addOnCompleteListener {

                                                    snackCreator.customToast(
                                                        this, this, null, Toast.LENGTH_SHORT,
                                                        "Profile Photo Changed Failed!",
                                                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                                                    )
                                                    firebaseManage.loadingScreenDestroyer(false)
                                            }
                                        }
                                    }
                                }
                            }.addOnFailureListener {
                            imagesReference.delete().addOnCompleteListener {
                                snackCreator.customToast(
                                    this, this, null, Toast.LENGTH_SHORT,
                                    "Profile Photo Changed Failed!",
                                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                                )
                                firebaseManage.loadingScreenDestroyer(false)
                            }
                        }
                    }
                }.addOnFailureListener {
                    firebaseManage.loadingScreenDestroyer(false)
                    snackCreator.customToast(
                        this, this, null, Toast.LENGTH_SHORT,
                        "Profile Photo Changed Failed!",
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                }
            }
        }
        else if (!netControl){
            firebaseManage.loadingScreenDestroyer(false)
            snackCreator.customToast(
                this, this, null, Toast.LENGTH_SHORT,
                "You must be connected to the Internet.",
                R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
        }
    }

    fun changeImage(activity : Activity, context : Context, imageView : CircleImageView){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(intent,2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private lateinit var imageViewForSelectedPicture : CircleImageView
    private var isComingFromChat = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectedPicture = data.data
            try {
                if (selectedPicture != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(contentResolver, selectedPicture!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        ppChangeImage.setImageBitmap(bitmap)
                    } else {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        ppChangeImage.setImageBitmap(bitmap)
                    }
                    saveProfilePhotoButton.visibility = View.VISIBLE
                    ppChangeImage.borderWidth = 3
                }
            }
            catch (e : Exception){e.printStackTrace()}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val userStatusUpdater = UserStatus()
    override fun onPause() {
        super.onPause()
        userStatusUpdater.statusUpdater("OFFLINE")
    }

    override fun onResume() {
        super.onResume()
        userStatusUpdater.statusUpdater("ONLINE")
    }

}