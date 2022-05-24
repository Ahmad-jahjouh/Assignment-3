package com.example.ass3.ui

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import coil.clear
import com.example.ass3.*
import com.example.ass3.FireBase.NotificationData
import com.example.ass3.FireBase.PushNotification
import com.example.ass3.FireBase.RetrofitIInstance
import com.example.ass3.model.Book
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import kotlinx.android.synthetic.main.activity_edit_book.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditBook : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
  //  var db: FirebaseFirestore? = null
    lateinit var id:String
    var videoUrl: Uri? = null

    val TAG = "E_Book"
    lateinit var database: DatabaseReference
    lateinit var date: Date

    var imageUri: Uri? = null
    var storge: FirebaseStorage? = null
    var referance: StorageReference? = null
    lateinit var path:String
    lateinit var path2:String
    lateinit var name:String
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)
        progressDialog=ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Upload...")
        val data = intent.getSerializableExtra("data") as Book
        path=String()
        path2=String()
        id= data.id
        name=data.BookName
        storge= Firebase.storage
        referance=storge!!.reference
        database = Firebase.database.reference
        edit_name.append(data.BookName)
        edit_auther.append(data.BookAuthor)
        edit_year.append(data.year.toString())
        edit_price.append(data.price.toString())
        edit_img
        path= data.image.toString()
        path2 = data.video.toString()
        edit_ratingBar2.rating=data.rate
        edit_year.setOnClickListener {
            edit_year.text!!.clear()
            val datePickerDialog= DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
        edit_but.setOnClickListener {
            val name=edit_name.text.toString()
            val Author=edit_auther.text.toString()
            val year=edit_year.text.toString()
            val price =edit_price.text.toString()
            val rate=edit_ratingBar2.rating
            val Book= Book(id,name,Author,year,price.toInt(),rate,path!!,path2)
            UpdateBook(Book)
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        delete_but.setOnClickListener {
            DeleteBook()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
        edit_img.setOnClickListener {
            edit_img.clear()
            OptionsSheet().show(this) {
                title("select")
                with(

                    Option(R.drawable.ic_baseline_insert_photo_24, "gallery")
                )
                onPositive { index: Int, option: Option ->
                    if (index == 0) {
                        dispatchTakeGalleryIntent()
                    }

                }


            }
        }

    }

    private fun DeleteBook() {
        database.child("book").child(id).removeValue() //setValue(null)
            .addOnSuccessListener {
                PushNotification(
                    NotificationData("delete BOOK ${name}","The book has been successfully delete"),
                    AddBook.topic
                ).also {
                    sendNotification(it)
                }
            }
            .addOnFailureListener {}

    }
    val REQUEST_Video_CAPTURE = 1
    val REQUEST_Gallery_CAPTURE = 2
    private fun dispatchTakeGalleryIntent() {
        val takePictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(takePictureIntent, REQUEST_Gallery_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

//    private fun dispatchTakevideoIntent() {
//        val takePictureIntent =
//            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_Video_CAPTURE)
//        } catch (e: ActivityNotFoundException) {
//            // display error state to the user
//        }
//    }



    private fun UpdateBook(book: Book) {
        val values = hashMapOf(
       "bookName" to book.BookName,
         "bookAuthor" to book.BookAuthor,
        "year" to book.year,
            "price" to book.price,
        "rate" to book.rate,
        "image" to book.image

        ) as Map<String, Book>

        database.child("book").child(id).updateChildren(values)

        PushNotification(
            NotificationData("update BOOK ${name}","The book has been successfully update"),
            AddBook.topic
        ).also {
            sendNotification(it)
        }

    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val format = SimpleDateFormat("yyyy-MM-dd")
        date = format.parse("$year-"+"$month-"+"$day")
        edit_year.append("$date")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_Gallery_CAPTURE && resultCode == RESULT_OK) {
            edit_img
            imageUri = data!!.data
            uploadimage1(imageUri)

        }

    }

    fun uploadimage1(uri: Uri?) {
        progressDialog.show()
        if (videoUrl==uri){
            referance!!.child("book/"+ UUID.randomUUID().toString()).putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {uri ->
                    path2=uri.toString()
                    progressDialog.dismiss()
                }
            }.addOnFailureListener {exception ->

            }
        }else{
            referance!!.child("book/"+ UUID.randomUUID().toString()).putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {uri ->
                    path=uri.toString()
                    progressDialog.dismiss()
                }
            }.addOnFailureListener {exception ->

            }
        }

    }

    private  fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitIInstance.api.postNotifiacation(notification)
            if (response.isSuccessful){
             //  Toast.makeText(this@AddBook, "aaa", Toast.LENGTH_SHORT).show()
            }else{
                Log.e(TAG,response.errorBody().toString())
            }
        }catch (e: Exception){
            Log.e(TAG,e.toString())
        }
    }


}