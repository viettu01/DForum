package com.tuplv.dforum.adapter;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
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

        final boolean[] isShowMore = {false};
        holder.tvContentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowMore[0]){
                    holder.tvContentComment.setMaxLines(3);
                }
                else {
                    holder.tvContentComment.setMaxLines(Integer.MAX_VALUE);
                }
                isShowMore[0] = !isShowMore[0];
            }
        });
        Date commentDate = new Date(comment.getCommentId());
        long diffInMillis = new Date().getTime() - commentDate.getTime();
        long secondsAgo = TimeUnit.SECONDS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (secondsAgo < 60) {
            holder.tvTimeComment.setText("Vừa xong");
        } else if (secondsAgo < 3600) {
            long minutesAgo = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);
            holder.tvTimeComment.setText(minutesAgo + " phút");
        } else if (secondsAgo < 86400) {
            long hoursAgo = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            holder.tvTimeComment.setText(hoursAgo + " giờ");
        } else if (secondsAgo < 604800) {
            long daysAgo = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            holder.tvTimeComment.setText(daysAgo + " ngày");
        } else if (secondsAgo < 31536000) {
            long diffInWeeks = diffInMillis / (7 * 24 * 60 * 60 * 1000);
            holder.tvTimeComment.setText(diffInWeeks + " tuần");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(commentDate);

            int commentMonth = calendar.get(Calendar.MONTH);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

            int commentDate2 = calendar.get(Calendar.DATE);
            int currentDate = Calendar.getInstance().get(Calendar.DATE);

            int diffInYears = Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            if (currentMonth < commentMonth || (currentMonth == commentMonth && currentDate < commentDate2)) {
                diffInYears--;
            }
            holder.tvTimeComment.setText(diffInYears + " năm");
        }
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
        Button btnReadMore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameCommentator = itemView.findViewById(R.id.tvNameCommentator);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
            tvContentComment = itemView.findViewById(R.id.tvContentComment);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);

            btnReadMore = itemView.findViewById(R.id.btn_read_more);
        }
    }
}
