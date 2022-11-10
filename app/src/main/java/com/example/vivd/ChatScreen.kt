package com.example.vivd

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vivd.Adapter.MessageAdapter
import com.example.vivd.ModelClass.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat_screen.*
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatScreen : AppCompatActivity() {
    var adapter: MessageAdapter?=null
    var messages:ArrayList<Message>?=null
    var senderRoom:String?=null
    val recyclerVieww:RecyclerView?=null

    var recieverRoom:String?=null
    var database:FirebaseDatabase?=null
    var storage:FirebaseStorage?=null
    var dialog:ProgressDialog?=null
    var senderUid:String?=null
    var recieverUid:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        setSupportActionBar(toolbar)
        database= FirebaseDatabase.getInstance()
        storage= FirebaseStorage.getInstance()
        dialog=ProgressDialog(this)
        dialog?.setMessage("Uploading Image... ")
        dialog?.setCancelable(false)
        messages= ArrayList()

        val name=intent.getStringExtra("name")
        val profile=intent.getStringExtra("image")
        prifilenme.text=name
        Glide.with(this).load(profile)
            .placeholder(R.drawable.king)
            .into(userDP)
        backbtn.setOnClickListener {
      finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        recieverUid=intent.getStringExtra("uid")
        senderUid=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence").child(recieverUid!!)

            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
if (snapshot.exists()){
    val status=snapshot.getValue(String::class.java)
    if (status=="offline"){
        statusss.visibility=View.GONE

    }else{
        statusss.setText(status)
        statusss.visibility=View.GONE

    }
}
}

                override fun onCancelled(error: DatabaseError) {
                }

            })
        senderRoom=senderUid+recieverUid
        recieverRoom=recieverRoom+senderUid
       adapter =MessageAdapter(this@ChatScreen,messages,senderRoom!!,recieverRoom!!)
        recyclerview.layoutManager=LinearLayoutManager(this@ChatScreen)
        recyclerview.adapter=adapter
        database!!.reference.child("chats")
            .child(senderRoom!!)
            .child("message")
            .addValueEventListener(object :ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages!!.clear()
                    for (snapshot1 in snapshot.children){
                        val message: Message?= snapshot1.getValue(Message::class.java)
                        message!!.messageId=snapshot1.key
                       messages!!.add(message)
                    }
                    adapter!!.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        send.setOnClickListener {
            val messageTxt:String=messageBox.text.toString()
            val date=Date()
            val message=Message(messageTxt,senderUid,date.time)
            messageBox.setText("")


            val ramdomkey=database!!.reference.push().key
            val lastMsgObj=HashMap<String,Any>()
            lastMsgObj["lastMsg"]=message.message!!
            lastMsgObj["lastMsgTime"]=date.time
            database!!.reference.child("chats").child(senderRoom!!)
                .updateChildren(lastMsgObj)

            database!!.reference.child("chats").child(recieverRoom!!)
                .updateChildren(lastMsgObj)

            database!!.reference.child("chats").child(senderRoom!!)
                .child("messages")
                .child(ramdomkey!!)
                .setValue(message).addOnSuccessListener {
                    database!!.reference.child("chats").child(recieverRoom!!)
                        .child("message")
                        .child(ramdomkey)
                        .setValue(message).addOnSuccessListener {

                        }
                }


        }
        attechment.setOnClickListener {
            val intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(intent,25 )


        }
        val handler=Handler()
        messageBox.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                database!!.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStopedTyping,1000)


            }
            val userStopedTyping= Runnable {
                database!!.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("Online")
            }

        })

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode==25){
                if (data!=null){
                    if (data.data!=null){

                        val selectedImage=data.data
                        val calender=Calendar.getInstance()
                        val refrance=storage!!.reference.child("chats")
                            .child(calender.timeInMillis.toString()+"")
                        dialog!!.show()
                        refrance.putFile(selectedImage!!).addOnCompleteListener{task->
                            if (task.isSuccessful){
                                refrance.downloadUrl.addOnSuccessListener {uri->

                                    val filepath=uri.toString()
                                    val messageTxt:String=messageBox.text.toString()
                                    val date=Date()
                                    val message=Message(messageTxt,senderUid,date.time)
                                    message.message="photo"
                                    message.imageUrl=filepath
                                    messageBox.setText("")
                                    val randomkey=database!!.reference.push().key
                                    val lastMsgObj=HashMap<String,Any>()
                                    lastMsgObj["lastMsg"]=message.message!!
                                    lastMsgObj["lastMsgTime"]=date.time
                                    database!!.reference.child("chats")
                                        .updateChildren(lastMsgObj)
                                    database!!.reference.child("chats")
                                        .child(recieverRoom!!)
                                        .updateChildren(lastMsgObj)

                                    database!!.reference.child("chats")
                                        .child(senderRoom!!)
                                        .child("messages")
                                        .child(randomkey!!)
                                        .setValue(message).addOnSuccessListener {
                                            database!!.reference.child("chats")
                                                .child(recieverRoom!!)
                                                .child("messages")
                                                .child(randomkey)
                                                .setValue(message)
                                                .addOnSuccessListener {


                                                }



                                        }

                                }
                            }










                        }
                    }

                }
            }

    }
    override fun onResume() {
        super.onResume()
        val currentId=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }
    override fun onPause() {
        super.onPause()
    val currentId=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")


    }
}