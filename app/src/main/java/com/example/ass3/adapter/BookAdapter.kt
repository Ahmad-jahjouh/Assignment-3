package com.example.ass3.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ass3.R
import com.example.ass3.model.Book
import com.example.ass3.ui.EditBook
import kotlinx.android.synthetic.main.book_item.view.*

class BookAdapter(var activity: Activity, var data: ArrayList<Book>) :
    RecyclerView.Adapter<BookAdapter.viewHolder>() {
    class viewHolder( item: View): RecyclerView.ViewHolder(item){
        var name =item.BookName
        var author=item.BookAuthor
        var year=item.year
        var rate=item.MyRating
        var price=item.price
        var butEdit=item.button
        var image=item.image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val root = LayoutInflater.from(activity).inflate(R.layout.book_item, null)
        return viewHolder(root)
    }


    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.name.text=data[position].BookName
        holder.author.text=data[position].BookAuthor

        holder.year.text=data[position].year.toString().subSequence(0,10)
        holder.price.text="$${data[position].price}"
        holder.rate.rating=data[position].rate
        Glide.with(activity).load(data[position].image).into(holder.image)
        holder.butEdit.setOnClickListener {
            val i = Intent(activity, EditBook::class.java)
            i.putExtra("data", data[position])
            activity.startActivity(i)

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}