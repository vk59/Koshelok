package com.tinkoffsirius.koshelok.ui.transactioncategory.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tinkoffsirius.koshelok.R
import com.tinkoffsirius.koshelok.entities.Category

class TransactionCategoryAdapter(private val setSelected: (category: Category) -> Unit) :
    RecyclerView.Adapter<TransactionCategoryViewHolder>() {

    private var list: List<Category> = listOf()

    private var mPosition: Int = -1

    fun setData(data: List<Category>) {
        list = data
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionCategoryViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return TransactionCategoryViewHolder(
            inflater.inflate(
                R.layout.item_transaction_category,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionCategoryViewHolder, position: Int) {
        val category: Category = list[position]

        holder.mTransactionImage?.isVisible = mPosition == position

        holder.itemView.setOnClickListener {
            mPosition = holder.absoluteAdapterPosition
            setSelected(category)
            notifyDataSetChanged()
        }

        holder.bind(category)
    }

    override fun getItemCount(): Int = list.size
}
