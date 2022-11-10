package com.example.vivd
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vivd.Adapter.UserAdapter
import com.example.vivd.ModelClass.modell
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_question.*
import java.lang.NullPointerException
class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database: FirebaseDatabase? = null
    var users: ArrayList<modell>? = null
    var userAdapter: UserAdapter? = null
    var dialog: ProgressDialog? = null
    private lateinit var ref: DatabaseReference
    private lateinit var refrance: DatabaseReference

    var user: modell? = null

    //     var context: Context?=null
    var recyclerview: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()
        users = arrayListOf()
        recyclerview = findViewById(R.id.mRes)
        recyclerview!!.layoutManager = LinearLayoutManager(this)
        recyclerview!!.setHasFixedSize(true)

        cardview.setBackgroundResource(R.drawable.cardviewdesign)
        dialog = ProgressDialog(this)
        dialog?.setCancelable(false)
        dialog?.setMessage("Uploading image...")
        database = FirebaseDatabase.getInstance()
        users = ArrayList<modell>()
        ref = FirebaseDatabase.getInstance().getReference("User")
        refrance = FirebaseDatabase.getInstance().getReference("Profile")
        getdata()
        getdp()
        hi.setOnClickListener {
            auth.signOut()

            startActivity(Intent(this,OtpRecieved::class.java))
            finish()
        }
    }

    private fun getdp() {
refrance.addValueEventListener(object :ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.exists()){
            for (snapshot1 in snapshot.children){
                val image=snapshot1.getValue(modell::class.java)
                users!!.add(image!!)
            }
            recyclerview!!.adapter = UserAdapter(users!!, this@HomeActivity)

        }
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }
})
    }


    private fun getdata() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot1 in snapshot.children) {
                        val use = snapshot1.getValue(modell::class.java)
                        users?.add(use!!)
                    }
                    recyclerview!!.adapter = UserAdapter(users!!, this@HomeActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onResume() {
        val msg = intent.getStringExtra("message_key")
        usernameshow.text = msg
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")

    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database?.reference?.child("presence")
            ?.child(currentId!!)?.setValue("Ofline")
    }

    override fun onStart() {
        super.onStart()
        val msg = intent.getStringExtra("message_key")
        usernameshow.text = msg
    }
}



