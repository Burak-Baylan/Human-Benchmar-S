package com.burak.humanbenchmarks.Messages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.burak.humanbenchmarks.PopupMessageCreator
import com.burak.humanbenchmarks.R
import com.burak.humanbenchmarks.UserStatus
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList

class Chat : AppCompatActivity() {

    private val userStatus = UserStatus()

    private val firebase = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val popupToast = PopupMessageCreator()
    private val currentUserId = currentUser!!.uid

    private lateinit var username : String
    private lateinit var ppurl : String
    private lateinit var status : String
    private lateinit var toUid : String

    private var photoSelector = PhotoSelector(this)

    var adapter : ChatRecyclerAdapter? = null

    var hebele = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#2E3239")
        username = intent.getStringExtra("username")
        ppurl = intent.getStringExtra("ppurl")
        status = intent.getStringExtra("status")
        toUid = intent.getStringExtra("toUid")
        usernameTextInChat.text = username

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        if (ppurl != "nulla") {
            Picasso.get().load(ppurl).into(ppImageInChat)
        }

        userStatus.addOnlineOrOfflineChangeListener(toUid, null, userStatusInChat)

        profilePhotosUrlGetter()

        goBackButtonInChat.setOnClickListener {
            finish()
        }
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {

                val uuid = UUID.randomUUID()
                val messageHasMap = hashMapOf(
                    "fromId" to currentUserId,
                    "toId" to toUid,
                    "message" to message,
                    "time" to Timestamp.now(),
                    "documentId" to uuid.toString()
                )


                firebase.collection("Messages").document(currentUserId).collection(toUid).document(uuid.toString()).set(messageHasMap).addOnSuccessListener {
                    firebase.collection("Messages").document(toUid).collection(currentUserId).document(uuid.toString()).set(messageHasMap).addOnSuccessListener {

                        val ring: MediaPlayer = MediaPlayer.create(this, R.raw.correct)
                        ring.start()
                        messageEditText.text.clear()
                        chatRecyclerView.scrollToPosition(adapter!!.itemCount - 1)

                        println("mesaj: gitti")

                    }.addOnFailureListener {
                        firebase.collection("Messages").document(currentUserId).collection(toUid).document(uuid.toString()).delete().addOnCompleteListener {
                            println("mesaj: mesaj gidemedi ve eskisi silindi")
                        }
                    }
                }.addOnFailureListener{
                    println("mesaj: hata \n${it.localizedMessage}")
                }
                /*popupToast.customToast(
                    this, this, null, Toast.LENGTH_SHORT, message,
                    R.drawable.custom_toast_info, R.drawable.ic_info_image
                )*/
            }
        }
        listener(currentUserId, toUid)

        adapter = ChatRecyclerAdapter(messageArray, usernameArray, documentIdArray, uidArray, fromOrTo, fromIdArray, toIdArray, timeArray)
        chatRecyclerView.adapter = adapter

        /*scrollDownFab.visibility = View.INVISIBLE
        var oldState = 0
        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (oldState < newState){
                    println("kaydırdı: (newS: $newState, oldS: $oldState)")
                    scrollDownFab.visibility = View.VISIBLE
                    scrollDownFab.animate().translationX(+resources.getDimension(R.dimen.standard__155))
                }
                oldState = newState
            }
        })*/

        var zort = false
        showPhotosFab.setOnClickListener {

            zort = if (!zort) {
                showPhotosFab.animate().translationX(+(screenWidth - 10f))
                true
            } else{
                showPhotosFab.animate().translationX(0f)
                false
            }
            //chatRecyclerView.scrollToPosition(adapter!!.itemCount - 1)
            println("scrollDownFab Clicked")
            photoSelector.getPhotoWithScrollView(bitmapArray, photoCounter, scrollViewForPhotos)
        }

        ppImageInChat.setOnClickListener{
            changeImage()
        }

    }

    private val messageArray : ArrayList<String> = ArrayList()
    private val usernameArray : ArrayList<String> = ArrayList()
    private val documentIdArray : ArrayList<String> = ArrayList()
    private val uidArray : ArrayList<String> = ArrayList()
    private val fromOrTo : ArrayList<Boolean> = ArrayList()
    private val fromIdArray : ArrayList<String> = ArrayList()
    private val toIdArray : ArrayList<String> = ArrayList()
    private val timeArray : ArrayList<String> = ArrayList()

    private fun listener(fromIdForArray : String, toIdForArray : String){
        firebase.collection("Messages").document(toUid).collection(currentUserId).orderBy("time", Query.Direction.ASCENDING).addSnapshotListener{ snapshot, error ->
            if (error != null){
                /** HATA **/
            }
            else{
                if (snapshot != null){
                    val docs = snapshot.documents
                    messageArray.clear()
                    fromOrTo.clear()
                    usernameArray.clear()
                    documentIdArray.clear()
                    uidArray.clear()
                    for (documents in docs){
                        val message : String = documents.get("message") as String
                        val fromId : String = documents.get("fromId") as String
                        val documentId : String = documents.get("documentId") as String
                        val time : Timestamp = documents.get("time") as Timestamp

                        val date = time.toDate()
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        var hour = (calendar.get(Calendar.HOUR_OF_DAY)).toString()
                        var minute = (calendar.get(Calendar.MINUTE)).toString()
                        if (hour.length == 1){
                            hour = "0$hour"
                        }
                        if (minute.length == 1){
                            minute = "0$minute"
                        }
                        val lastTime = "$hour:$minute"

                        if (fromId == currentUserId){
                            fromOrTo.add(true)
                        }
                        else{
                            fromOrTo.add(false)
                        }
                        messageArray.add(message)
                        usernameArray.add(username)
                        documentIdArray.add(documentId)
                        fromIdArray.add(fromIdForArray)
                        timeArray.add(lastTime)
                        toIdArray.add(toIdForArray)
                        uidArray.add("")
                    }
                    adapter!!.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(adapter!!.itemCount - 1)
                }
                else{
                    println("chat: snapshot null")
                }
            }
        }
    }

    private fun profilePhotosUrlGetter(){
        //firebase.collection()
    }

    override fun onResume() {
        userStatus.statusUpdater("ONLINE")
        super.onResume()
    }

    override fun onPause() {
        userStatus.statusUpdater("OFFLINE")
        super.onPause()
    }

    fun customToastCreatorForAdapter(message : String, background : Int, image: Int){
        popupToast.customToast(this, this, null, Toast.LENGTH_SHORT, message, background, image)
    }

    private fun changeImage(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
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

    private var selectedPicture : Uri? = null
    private var photoCounter = 0

    private var bitmapArray : ArrayList<Bitmap> = ArrayList()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectedPicture = data.data
            try {
                if (selectedPicture != null) {
                    val bitmap : Bitmap
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(contentResolver, selectedPicture!!)
                        bitmap = ImageDecoder.decodeBitmap(source)
                        ppImageInChat.setImageBitmap(bitmap)
                    } else {
                        bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        ppImageInChat.setImageBitmap(bitmap)
                    }
                    ppImageInChat.borderWidth = 3
                    photoCounter++
                    bitmapArray.add(bitmap)

                    //usernameTextInChat.text = "$photoCounter"

                }
            }
            catch (e : Exception){e.printStackTrace()}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}