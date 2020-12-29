package com.burak.humanbenchmarks.Messages

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.burak.humanbenchmarks.R
import com.burak.humanbenchmarks.UserStatus
import com.burak.humanbenchmarks.animationControl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_messages.*

class Messages : AppCompatActivity() {

    private val userStatusUpdater = UserStatus()
    private val animationControl = animationControl(this)
    private val firebase = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val usernameArray : ArrayList<String> = ArrayList()
    private val userStatusArray : ArrayList<String> = ArrayList()
    private val uidArray : ArrayList<String> = ArrayList()
    private val ppUrlArray : ArrayList<String?> = ArrayList()

    var adapter : MessagesRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        supportActionBar?.title = "All Users"
        animationControl.forOnCreate(savedInstanceState)
        messagesRowAnimControl = true

        getMessages()

        adapter = MessagesRecyclerAdapter(usernameArray, userStatusArray, uidArray, ppUrlArray)
        messagesRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        animationControl.forOnStart()
    }

    override fun onPause() {
        super.onPause()
        userStatusUpdater.statusUpdater("OFFLINE")
    }

    override fun onResume() {
        super.onResume()
        userStatusUpdater.statusUpdater("ONLINE")
    }

    private fun getMessages(){
        if (currentUser != null){
            val currentUid = currentUser.uid
            firebase.collection("Users").addSnapshotListener { snapshot, error ->
                if (error != null){
                    /** HATA **/
                }
                else{
                    if (snapshot != null){

                        val docs = snapshot.documents
                        usernameArray.clear()
                        userStatusArray.clear()
                        uidArray.clear()
                        ppUrlArray.clear()
                        for(documents in docs){
                            val username : String = documents.get("UserName") as String
                            val status : String = documents.get("userStatus") as String
                            val email : String = documents.get("Email") as String
                            val uid : String = documents.get("uid") as String
                            val ppUrl : String = documents.get("ppurl") as String
                            println(
                                "username:$username\n" +
                                        "status:$status\n" +
                                        "email:$email\n" +
                                        "uid:$uid"
                            )
                            if (uid != currentUid) {
                                usernameArray.add(username)
                                userStatusArray.add(status)
                                uidArray.add(uid)
                                ppUrlArray.add(ppUrl)
                            }
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
    companion object{
        var messagesRowAnimControl = true
    }
}