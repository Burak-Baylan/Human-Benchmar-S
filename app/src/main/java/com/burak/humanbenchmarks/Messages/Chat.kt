package com.burak.humanbenchmarks.Messages

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
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
    private val currentUserId = currentUser!!.uid

    private lateinit var username : String
    private lateinit var ppurl : String
    private lateinit var status : String
    private lateinit var toUid : String

    var adapter : ChatRecyclerAdapter? = null

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

        if (ppurl != "nulla") {
            Picasso.get().load(ppurl).into(ppImageInChat)
        }

        userStatus.addOnlineOrOfflineChangeListener(toUid, null, userStatusInChat)


        goBackButtonInChat.setOnClickListener {
            finish()
        }
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            messageEditText.text.clear()
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

                        val ring: MediaPlayer = MediaPlayer.create(this, R.raw.message_success)
                        ring.start()
                        messageEditText.text.clear()
                        chatRecyclerView.scrollToPosition(adapter!!.itemCount - 1)
                        println("mesaj: gitti")

                    }.addOnFailureListener {
                        firebase.collection("Messages").document(currentUserId).collection(toUid).document(uuid.toString()).delete().addOnCompleteListener {
                            println("mesaj: mesaj gidemedi ve eskisi silindi")
                            messageEditText.setText(message)
                        }
                    }
                }.addOnFailureListener{
                    println("mesaj: hata \n${it.localizedMessage}")
                    messageEditText.setText(message)
                }
            }
        }
        listener(currentUserId, toUid)

        adapter = ChatRecyclerAdapter(messageArray, usernameArray, uidArray, fromOrTo, timeArray)
        chatRecyclerView.adapter = adapter
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

                        if (fromId == currentUserId){// From me
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

    override fun onResume() {
        userStatus.statusUpdater("ONLINE")
        super.onResume()
    }

    override fun onPause() {
        userStatus.statusUpdater("OFFLINE")
        super.onPause()
    }
}