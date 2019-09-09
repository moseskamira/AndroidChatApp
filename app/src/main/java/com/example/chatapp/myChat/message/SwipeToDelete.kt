package com.example.chatapp.myChat.message

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class SwipeToDelete(val adapter: MessageAdapter, val context: Context) : ItemTouchHelper.SimpleCallback(0,
    ItemTouchHelper
    .LEFT or  ItemTouchHelper.RIGHT) {



    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.layoutPosition
        adapter.deleteItem(position)
    }
}