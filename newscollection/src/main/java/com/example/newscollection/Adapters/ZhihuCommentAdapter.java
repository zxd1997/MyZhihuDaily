package com.example.newscollection.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newscollection.Beans.ZhihuComments;
import com.example.newscollection.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zxd1997 on 2018/03/15.
 */

public class ZhihuCommentAdapter extends RecyclerView.Adapter<ZhihuCommentAdapter.ViewHolder> {
    private List<ZhihuComments.Comment> comments;

    public ZhihuCommentAdapter(List<ZhihuComments.Comment> comments) {
        Log.d("con", "CommentAdapter: constature" + comments.size());
        this.comments = comments;
        String tmp = "";
        for (int i = comments.size() - 1; i >= 0; i--) {
            tmp += comments.get(i).getContent() + comments.get(i).isReply() + "\n";
        }
        Log.d("comments", "CommentAdapter: " + tmp);
    }

    @Override
    public ZhihuCommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zhihu_comment_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ZhihuComments.Comment comment = comments.get(position);
        long t = comment.getTime();
        Log.d("time", "onBindViewHolder: " + t);
        Date date = new Date(t * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.head.setImageURI(comment.getAvatar());
        holder.author.setText(comment.getAuthor());
        holder.content.setText(comment.getContent());
        holder.like.setText(comment.getLikes() + "");
        holder.time.setText(format.format(date));
        Log.d("date", "onBindViewHolder: " + format.format(date));
        if (comment.isReply()) {
            holder.cardView.setVisibility(View.VISIBLE);
            ZhihuComments.Comment.Reply reply = comment.getReply_to();
            if (reply.getStatus() == 0) {
                holder.replyContent.setText(reply.getAuthor() + ":" + reply.getContent());
            } else {
                holder.replyContent.setText("Sorry the original Reply has been deleted.");
            }
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView head;
        TextView author;
        TextView content;
        CardView cardView;
        TextView replyContent;
        TextView like;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            head = (SimpleDraweeView) itemView.findViewById(R.id.head);
            author = (TextView) itemView.findViewById(R.id.author);
            content = (TextView) itemView.findViewById(R.id.content);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
            replyContent = (TextView) itemView.findViewById(R.id.reply_content);
            time = (TextView) itemView.findViewById(R.id.time);
            like = (TextView) itemView.findViewById(R.id.like);
        }
    }
}
