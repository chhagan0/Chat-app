package com.example.vivd.Adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.comix.rounded.RoundedCornerImageView
import com.example.vivd.ChatScreen
import com.example.vivd.ModelClass.modell
import com.example.vivd.R
import com.google.firebase.database.core.Context
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.chatdesign.view.*

class UserAdapter( var userList: ArrayList<modell>,private val context:android.content.Context) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chatdesign, parent, false)
        return UserViewHolder(v)

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userNama.text = user.name

Glide.with(context).load(userList[position].profileImage).into(holder.image)
        holder.itemView.setOnClickListener {
            val intent=Intent(context,ChatScreen::class.java)
            intent.putExtra("name",user.name)
            intent.putExtra("image",user.profileImage)
            intent.putExtra("uid",user.uid)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = userList.size


  class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNama: TextView =itemView.findViewById(R.id.usernameee)
      var image:RoundedCornerImageView=itemView.findViewById(R.id.imageViewuser)
    }

}