package com.burak.humanbenchmarks.Messages

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.burak.humanbenchmarks.R
import kotlin.collections.ArrayList


class ChatRecyclerAdapter(
    private val messageArray : ArrayList<String>,
    private val usernameArray : ArrayList<String>,
    private val uidArray : ArrayList<String>,
    private val fromOrTo : ArrayList<Boolean>,
    private val timeArray : ArrayList<String>
): RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder>() {

    private lateinit var context : Context

    class ChatHolder(view: View) : RecyclerView.ViewHolder(view){
        var greenMessage : TextView = view.findViewById(R.id.greenText)
        var whiteMessage : TextView = view.findViewById(R.id.whiteText)
        var usernameTv : TextView = view.findViewById(R.id.usernameWhiteTv)
        var whiteTimeTv : TextView = view.findViewById(R.id.whiteTimeTvChat)
        var greenTimeTv : TextView = view.findViewById(R.id.greenTimeTvChat)
        var greenMessageLayout : ConstraintLayout = view.findViewById(R.id.greenMessageLayout)
        var whiteMessageLayout : ConstraintLayout = view.findViewById(R.id.whiteMessageLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_row, parent, false)
        return ChatHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.whiteMessageLayout.visibility = View.GONE
        holder.greenMessageLayout.visibility = View.GONE
        val fromOrTo  = fromOrTo[position]
        if (fromOrTo){ // From
            holder.greenMessageLayout.visibility = View.VISIBLE
            holder.greenTimeTv.text = timeArray[position]
            holder.greenMessage.text = messageArray[position]
        }
        else if (!fromOrTo){ // To
            holder.whiteMessageLayout.visibility = View.VISIBLE
            holder.usernameTv.text = usernameArray[position]
            holder.whiteTimeTv.text = timeArray[position]
            holder.whiteMessage.text = messageArray[position]
        }
    }

    override fun getItemCount(): Int {
        return uidArray.size
    }
}