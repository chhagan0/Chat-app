package com.example.vivd.Adapter

import android.app.AlertDialog
import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vivd.ModelClass.Message
import com.example.vivd.R
import com.example.vivd.databinding.DeleteLayoutBinding
import com.example.vivd.databinding.SendMsgBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(
    var context: Context,
    messages: ArrayList<Message>?,
    senderRoom: String,
    recieverRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

 private lateinit var  messages:ArrayList<Message>
    val ITEM_SEND=1
    val ITEM_REVIEVE=2
 val senderRoom:String
 val recieveRoom:String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
return if (viewType==ITEM_SEND){
    val view=LayoutInflater.from(context).inflate(R.layout.send_msg,parent,false)
    SentMessageHolder(view)
}else{
val view=LayoutInflater.from(context).inflate(R.layout.recieve_msg,parent,false)
    RecieveMessageHolder(view)
}
    }

    override fun getItemViewType(position: Int): Int {
         val message=messages[position]
        return if (FirebaseAuth.getInstance().uid==message.senderId){
            ITEM_SEND

        }else{
            ITEM_REVIEVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
val message=messages[position]
    if (holder.javaClass==SentMessageHolder::class.java){
        val viewholder=holder as SentMessageHolder
        if (message.message.equals("photo")){
            viewholder.bindind.image.visibility=View.VISIBLE
            viewholder.bindind.message.visibility=View.GONE
            viewholder.bindind.sLinear.visibility=View.GONE
            Glide.with(context)
               .load(message.imageUrl)
                .placeholder(R.drawable.image)
                .into(viewholder.bindind.image)
        }
        viewholder.bindind.message.text=message.message
        viewholder.itemView.setOnLongClickListener{
            val view=LayoutInflater.from(context).inflate(R.layout.delete_layout,null)
            val binding:DeleteLayoutBinding= DeleteLayoutBinding.bind(view)
            val dialog=AlertDialog.Builder(context)
                .setTitle("Delete Message")
                .setView(binding.root)
                .create()
            binding.everyone.setOnClickListener {
                message.message="You Deleted this message"
                message.messageId?.let { it1->
                    FirebaseDatabase.getInstance().reference.child("chats")
                        .child(senderRoom)
                        .child("message")
                        .child(it1).setValue(message)


                }
                message.messageId?.let { it1->
                    FirebaseDatabase.getInstance().reference.child("chats")
                        .child(recieveRoom)
                        .child("message")
                        .child(it1).setValue(message)
                }
                dialog.dismiss()
            }
            binding.delete.setOnClickListener {
                message.messageId?.let { it1->
                    FirebaseDatabase.getInstance().reference.child("chats")
                        .child(senderRoom)
                        .child("message")
                        .child(it1).setValue(null)


                }
                dialog.dismiss()
            }
            binding.cancel.setOnClickListener { dialog.dismiss() }
            dialog.show()

            false
        }
    }
        else {
        val viewholder = holder as RecieveMessageHolder
        if (message.message.equals("photo")) {
            viewholder.bindind.image.visibility = View.VISIBLE
            viewholder.bindind.message.visibility = View.GONE
            viewholder.bindind.sLinear.visibility = View.GONE
            Glide.with(context)
                .load(message.imageUrl)
                .placeholder(R.drawable.image)
                .into(viewholder.bindind.image)
         }
        viewholder.bindind.message.text = message.message
        viewholder.itemView.setOnLongClickListener {
            val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
            val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete Message")
                .setView(binding.root)
                .create()
            binding.everyone.setOnClickListener {
                message.message = "You Deleted this message"
                message.messageId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("chats")
                        .child(senderRoom)
                        .child("message")
                        .child(it1).setValue(message)


                }
                message.messageId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("chats")
                        .child(recieveRoom)
                        .child("message")
                        .child(it1).setValue(message)
                }
                dialog.dismiss()
            }
            binding.delete.setOnClickListener {
                message.messageId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("chats")
                        .child(senderRoom)
                        .child("message")
                        .child(it1).setValue(null)


                }
                dialog.dismiss()
            }
            binding.cancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
            false
        }
    }

    }

    override fun getItemCount(): Int=messages.size

    inner class SentMessageHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        var bindind:SendMsgBinding= SendMsgBinding.bind(itemview)


    }
    inner class RecieveMessageHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        var bindind:SendMsgBinding= SendMsgBinding.bind(itemview)
    }
    init {
        if (messages!=null){
            this.messages=messages
        }
        this.senderRoom=senderRoom
        this.recieveRoom=recieverRoom
    }



}

