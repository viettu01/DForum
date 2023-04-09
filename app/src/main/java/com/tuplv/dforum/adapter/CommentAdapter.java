package com.tuplv.dforum.adapter;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final Context context;
    private final int layout;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private final List<Comment> comments;

    public CommentAdapter(Context context, int layout, List<Comment> comments) {
        this.context = context;
        this.layout = layout;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.tvContentComment.setText(comment.getContent());

        reference.child(OBJ_ACCOUNT).child(comment.getAccountId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Account account = snapshot.getValue(Account.class);
                            if (Objects.requireNonNull(account).getAvatarUri().equals("null"))
                                holder.imvAvatar.setImageResource(R.drawable.no_avatar);
                            else
                                Picasso.get().load(account.getAvatarUri()).into(holder.imvAvatar);
                            holder.tvNameCommentator.setText(account.getNickName());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");
//        holder.tvApprovalDate.setText(dateFormat.format(post.getApprovalDate()));
    }

    @Override
    public int getItemCount() {
        if (comments != null) {
            return comments.size();
        }

        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameCommentator, tvTimeComment, tvContentComment;
        ImageView imvAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameCommentator = itemView.findViewById(R.id.tvNameCommentator);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
            tvContentComment = itemView.findViewById(R.id.tvContentComment);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
        }
    }
}
