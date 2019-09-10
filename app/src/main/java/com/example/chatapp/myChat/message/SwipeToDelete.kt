package com.example.chatapp.myChat.message

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R


class SwipeToDelete(val adapter: MessageAdapter, val context: Context) : ItemTouchHelper.SimpleCallback(0,
    ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT) {
    private val deleteIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_32dp)!!
    private val backgroundColor: ColorDrawable = ColorDrawable(Color.RED)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
        dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 10
        val deleteIconMargin = (itemView.height - deleteIcon.intrinsicHeight)/2
        val deleteIconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight)
        val deleteIconBottom = deleteIconTop + deleteIcon.intrinsicHeight
        when {
            dX > 0 -> {
                val deleteIconLeft = itemView.left + deleteIconMargin + deleteIcon.intrinsicWidth
                val deleteIconRight = itemView.left + deleteIconMargin
                deleteIcon.bounds = Rect(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                backgroundColor.bounds =  Rect(itemView.left, itemView.top, itemView.left+ dX.toInt() +
                        backgroundCornerOffset, itemView.bottom)
            }
            dX < 0 -> {
                val deleteIconLeft = itemView.right - deleteIconMargin - deleteIcon.intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                deleteIcon.bounds = Rect(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                backgroundColor.bounds =  Rect(itemView.right+ dX.toInt() - backgroundCornerOffset, itemView.top,
                    itemView.right, itemView.bottom)

            }
            else -> backgroundColor.bounds = Rect(0,0,0,0)
        }
        backgroundColor.draw(c)
        deleteIcon.draw(c)
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.deleteItemFromRecyclerView(viewHolder.adapterPosition)
    }
}