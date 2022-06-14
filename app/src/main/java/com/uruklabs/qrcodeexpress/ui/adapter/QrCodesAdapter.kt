package com.uruklabs.qrcodeexpress.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.uruklabs.qrcodeexpress.R
import com.uruklabs.qrcodeexpress.database.model.QrCode

class QrCodesAdapter (private val onItemClick : (QrCode) -> Unit) : RecyclerView.Adapter<QrCodesAdapter.ViewHolder>() {
     var itemList : List<QrCode> = listOf()


    fun setDataSet( item: List<QrCode>){ //Alimenta a RecyclerView
        this.itemList = item
    }

    fun filterList(qrSearch: MutableList<QrCode>) {
        this.itemList = qrSearch
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun linkItem( onItemClick: (QrCode) -> Unit, item: QrCode) {

            itemView.setOnClickListener { //Captura os eventos de click
                onItemClick(item)
            }

            val txtTitleQrCode : TextView = itemView.findViewById(R.id.txtTitleQrCode)
            txtTitleQrCode.text = item.title

            val txtConteudoQrCode : TextView = itemView.findViewById(R.id.txtConteudoQrCode)
            txtConteudoQrCode.text = item.codeText

            val origin_indicator : ImageView = itemView.findViewById(R.id.origin_indicator)
            val origin_indicator2 : ImageView = itemView.findViewById(R.id.origin_indicato2)
            if (item.origin == 1){
                origin_indicator2.isVisible = true
            } else {
                origin_indicator.isVisible = true
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_qr_list , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item =  itemList[position]
        holder.linkItem(onItemClick, item)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}