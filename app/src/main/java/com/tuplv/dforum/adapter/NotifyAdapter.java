package com.tuplv.dforum.adapter;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.interf.OnNotifyClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Notify;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {

    Context context;
    int layout;
    List<Notify> notifies;
    OnNotifyClickListener listener;

    public NotifyAdapter(Context context, int layout, List<Notify> notifies, OnNotifyClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.notifies = notifies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);

        return new NotifyAdapter.ViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notify notify = notifies.get(position);
        if (notify.getStatus().equals(STATUS_ENABLE)) {
            holder.imvNotify.setVisibility(View.GONE);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(notify.getAccountId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Account account = snapshot.getValue(Account.class);
                            if (Objects.requireNonNull(account).getAvatarUri().equals("null"))
                                holder.imvAvatar.setImageResource(R.drawable.no_avatar);
                            else
                                Picasso.get().load(account.getAvatarUri()).into(holder.imvAvatar);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        holder.tvContentNotify.setText(notify.getNotifyContent());
        holder.tvDateNotify.setText(dateFormat.format(notify.getNotifyId()));

        holder.llItemListNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.goToViewPostActivity(notify);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (notifies != null)
            return notifies.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llItemListNotify;
        ImageView imvAvatar, imvNotify;
        TextView tvContentNotify, tvDateNotify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llItemListNotify = itemView.findViewById(R.id.llItemListNotify);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            imvNotify = itemView.findViewById(R.id.imvNotify);
            tvContentNotify = itemView.findViewById(R.id.tvContentNotify);
            tvDateNotify = itemView.findViewById(R.id.tvDateNotify);
        }
    }
}
