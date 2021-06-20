package com.example.printers_3d

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_item_forum_list_threads_market.view.*
import java.util.ArrayList

class ForumThreadsMarketAdapter (mCtx: Context, val forum_lists : ArrayList<Market_List_Thread>, var clickListener: OnForumThreadsItemClickListener, var clickListener2: OnForumThreadsItemClickListener2)
: RecyclerView.Adapter<ForumThreadsMarketAdapter.ViewHolder>()
{
    val mCtx= mCtx
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val txtForumAuthor=itemView.txt_forum_list_article_author_thread_market
        val txtForumTitle=itemView.txt_forum_list_article_title_thread_market
        val txtForumDate=itemView.txt_forum_list_article_date_thread_market
        val txt_forum_list_article_dateforum_author_avatar=itemView.txt_forum_list_article_dateforum_author_avatar_thread_market
        val txt_forum_list_article_dateforum_description=itemView.txt_forum_list_article_description_thread_market
        val txt_forum_list_article_dateforum_author_thumbnail_thread=itemView.txt_forum_list_article_dateforum_author_thumbnail_thread_market
        val txt_forum_list_article_prize_thread=itemView.txt_forum_list_article_prize_thread_market
        val txt_forum_list_article_contact_thread=itemView.txt_forum_list_article_contact_thread_market
        fun initialize(forum_list: Market_List_Thread, action:OnForumThreadsItemClickListener)
        {
            txtForumAuthor.text=forum_list.author
            txtForumTitle.text= forum_list.title
            txtForumDate.text= forum_list.date
            txt_forum_list_article_dateforum_description.text=forum_list.description
            if(forum_list.prize=="")
            {
                txt_forum_list_article_prize_thread.text=""
            }
            if(forum_list.contact=="")
            {
                txt_forum_list_article_contact_thread.text=""
            }
            if(forum_list.prize!="")
            {
                txt_forum_list_article_prize_thread.text = itemView.getContext()
                    .getString(R.string.prize) + ": " + forum_list.prize + " PLN"
            }
            if(forum_list.contact!="")
            {
                txt_forum_list_article_contact_thread.text = itemView.getContext().getString(R.string.contact) + ": " + forum_list.contact
            }

            if(forum_list.avatar!=null)
            {
                Glide.with(txt_forum_list_article_dateforum_author_avatar)
                    .load(forum_list.avatar)
                    .into(txt_forum_list_article_dateforum_author_avatar)
            }
            if(forum_list.imageThumbnail!=null)
            {
                Glide.with(txt_forum_list_article_dateforum_author_thumbnail_thread)
                    .load(forum_list.imageThumbnail)
                    .into(txt_forum_list_article_dateforum_author_thumbnail_thread)
            }

            itemView.setOnClickListener{
                action.OnItemClick(forum_list,adapterPosition)
            }

        }
        fun clickThumbnail(forum_list: Market_List_Thread, action:OnForumThreadsItemClickListener2)
        {
            itemView.setOnClickListener{
                action.OnItemClick2(forum_list,adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumThreadsMarketAdapter.ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.layout_item_forum_list_threads_market,parent,false)
        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return forum_lists.size
    }

    override fun onBindViewHolder(holder: ForumThreadsMarketAdapter.ViewHolder, position: Int) {
        // val forum_list: Forum_List= forum_lists[position]
        //holder.txtForumAuthor.text=forum_list.author
        //holder.txtForumTitle.text= forum_list.title
        //holder.txtForumDate.text= forum_list.date
        //if(forum_list.image!=null)
        //{
        //   Glide.with(holder.txt_forum_list_article_dateforum_author_avatar)
        //      .load(forum_list.image)
        //      .into(holder.txt_forum_list_article_dateforum_author_avatar)
        //}
        holder.initialize(forum_lists.get(position),clickListener)
        holder.clickThumbnail(forum_lists.get(position),clickListener2)

    }
    interface OnForumThreadsItemClickListener
    {
        fun OnItemClick(item:Market_List_Thread,position: Int)

    }
    interface OnForumThreadsItemClickListener2
    {
        fun OnItemClick2(item:Market_List_Thread,position: Int)

    }
}