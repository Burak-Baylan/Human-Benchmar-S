package com.burak.humanbenchmarks.Messages

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.burak.humanbenchmarks.R
import com.burak.humanbenchmarks.UserStatus
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.messages_recylcler_row.view.*

class MessagesRecyclerAdapter (
    private val usernameArray : ArrayList<String>,
    private val userStatusArray : ArrayList<String>,
    private val uidArray : ArrayList<String>,
    private val ppUrlArray : ArrayList<String?>,
    private val emailArray : ArrayList<String>) : RecyclerView.Adapter<MessagesRecyclerAdapter.MessageHolder>() {

    private val userStatus = UserStatus()

    private lateinit var context : Context

    class MessageHolder (view : View) : RecyclerView.ViewHolder(view){
        var ppImage : ImageView = view.findViewById(R.id.currentUserPpInMessage)
        var username : TextView = view.findViewById(R.id.messagesUsernameTextView)
        var emailTXT : TextView = view.findViewById(R.id.emailTextViewInMessageRow)
        var userStatusButton : Button = view.findViewById(R.id.userStatusInMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.messages_recylcler_row, parent, false)
        view.mLinearLayoutMessageRow.animation = AnimationUtils.loadAnimation(context, R.anim.anim_for_message_row)
        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {

        holder.username.text = usernameArray[position]
        userStatus.addOnlineOrOfflineChangeListener(uidArray[position], null, holder.userStatusButton)
        holder.emailTXT.text = emailArray[position]
        val ppUrl = ppUrlArray[position]
        if(ppUrl != "nulla") {
            Picasso.get().load(ppUrl).into(holder.ppImage)
        }
        else{
            holder.ppImage.setImageResource(R.drawable.questionmark)
        }



        holder.itemView.setOnClickListener {
            println("tıklandı: ${usernameArray[position]}")

            val intent = Intent(context, Chat::class.java)
            intent.putExtra("username", usernameArray[position])
            intent.putExtra("ppurl", ppUrlArray[position])
            intent.putExtra("status", userStatusArray[position])
            intent.putExtra("toUid", uidArray[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return uidArray.size
    }
}