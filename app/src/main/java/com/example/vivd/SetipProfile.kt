package com.example.vivd

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.comix.rounded.RoundedCornerImageView
import com.example.vivd.ModelClass.modell
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_setip_profile.*
import java.util.*
import kotlin.collections.HashMap

class SetipProfile : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    lateinit var uri: Uri
    var dialog: ProgressDialog? = null
 var image:RoundedCornerImageView?=null
    private var Storageref=Firebase.storage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setip_profile)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
Storageref=FirebaseStorage.getInstance()
        image=findViewById(R.id.imageViewuser)


        val galleryimage=registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {

                image!!.setImageURI(it)
                  uri= it!!
            })
        userimage.setOnClickListener{
//    galleryimage.launch("image/*")
            selectimage()


        }




        continuebtn.setOnClickListener {
            dialog?.show()
            dialog = ProgressDialog(this)
            dialog!!.setMessage("Updating Profile...")
            dialog!!.setCancelable(false)
            val name: String = username.text.toString()
            if (name.isEmpty()) {
                dialog!!.dismiss()
                Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show()
            } else {
                dialog!!.show()
                if (userimage != null) {
                   Storageref.getReference("Profile").child(System.currentTimeMillis().toString())
                       .putFile(uri)


                       .addOnSuccessListener { task ->
                                  task.metadata!!.reference!!.downloadUrl
                                      .addOnSuccessListener {
                                          val userID = FirebaseAuth.getInstance().currentUser!!.uid
                                           val mapImage= mapOf("url" to it.toString())


                                          val imageUrl = uri.toString()

                                          val name: String = username.text.toString()
                                          val phone = auth!!.currentUser!!.phoneNumber
                                          val user = modell(userID, name, phone, imageUrl)
                                          database!!.reference
                                              .child("User")
                                              .child(userID)
                                              .setValue(user)
                                              .addOnCompleteListener {
                                                  dialog!!.dismiss()
                                                  val intent =
                                                      Intent(this, HomeActivity::class.java)
                                                  val message = username.text.toString()
                                                  intent.putExtra("message_key", message)
                                                  startActivity(intent)
                                              }
                                      }
                            }
                        }
                        else{
                            val uid = auth!!.uid
                            val name: String = username.text.toString()
                            val phone = auth!!.currentUser!!.phoneNumber
                            val user=modell(uid,name,phone,"No image")

                            database!!.reference
                                .child("User")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCompleteListener {
                                    dialog!!.dismiss()

                                    val intent=Intent(this,HomeActivity::class.java)
                                    val message = username.text.toString()
                                    intent.putExtra("message_key", message)
                                    startActivity(intent)
                                }
                        }
                    }

                }
            }

    private fun selectimage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(
            intent, 12

        )    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==12 && resultCode == Activity.RESULT_OK && data != null && data.data!=null){
             uri= data.data!!
            userimage.setImageURI(uri)

        }
    }
}


//
//    private fun imagePicker() {
//        val choice = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
//        var myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
//        myAlertDialog.setTitle("Select Image")
//        myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
//
//            when {
//                choice[item] == "Choose from Gallery" -> {
////                    val intent = Intent()
////                    intent.action = Intent.ACTION_GET_CONTENT
////                    intent.type = "image/*"
////                    startActivityForResult(
////                        intent, 45
//
////                    )
//
//
//
//                }
//                choice[item] == "Take Photo" -> {
//                    val cameraPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(cameraPicture, 0)
//                }
//                // Select "Cancel" to cancel the task
//                choice[item] == "Cancel" -> {
//
//
//                }
//
//
//            }
//        })
//        myAlertDialog.show()
//
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(data !=null){
//            if (data.data !=null){
//                val uri=data.data
//                val storage=FirebaseStorage.getInstance()
//                val time=Date().time
//                val reference=storage.reference
//                    .child("Profile")
//                    .child(time.toString() + "")
//                    reference.putFile(uri!!).addOnCompleteListener {task->
//
//                        if (task.isSuccessful){
//                        reference.downloadUrl.addOnCompleteListener { uri->
//                            val filepath=uri.toString()
//                            val obj=HashMap<String,Any>()
//                            obj["image"]=filepath
//                            database!!.reference
//                                .child("User")
//                                .child(FirebaseAuth.getInstance().uid!!)
//                                .updateChildren(obj).addOnCompleteListener {
//
//                                }
//
//                        }
//                        }
//
//                    }
//                userimage.setImageURI(data.data)
//                selectedimg=data.data
//            }
//        }
//    }
//}