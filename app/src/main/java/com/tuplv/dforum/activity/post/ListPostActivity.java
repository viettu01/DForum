package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.SORT_DECREASE_VIEWS;
import static com.tuplv.dforum.until.Constant.SORT_EARLIEST;
import static com.tuplv.dforum.until.Constant.SORT_INCREASE_VIEWS;
import static com.tuplv.dforum.until.Constant.SORT_OLDEST;
import static com.tuplv.dforum.until.Constant.SORT_TITLE_AZ;
import static com.tuplv.dforum.until.Constant.SORT_TITLE_ZA;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.TYPE_END_DATE;
import static com.tuplv.dforum.until.Constant.TYPE_START_DATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.PostAdapter;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ListPostActivity extends AppCompatActivity implements View.OnClickListener, OnPostClickListener {

    Toolbar tbListPost;
    TextView tvNameForum, tvDesForum, tvNoPost, tvFilterPost, tvSortPost, tvStartDate, tvEndDate;
    ImageView imvFilterPost, imvSortPost;
    RecyclerView rvListPost;
    FloatingActionButton fabAddPost;
    PostAdapter postAdapter;
    List<Post> posts;
    Forum forum;
    Button btnFilterDate;
    int typeDatePicker;

    private final Calendar startDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();

    String filter, sort;
    Collator collator = Collator.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_post);
        init();
        tbListPost.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // hiện dialog để chọn ngày
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (typeDatePicker == TYPE_START_DATE) {
                    startDate.set(year, month, dayOfMonth, 0, 0, 0);
                    tvStartDate.setText(formatDate(startDate.getTime()));
                } else if (typeDatePicker == TYPE_END_DATE) {
                    endDate.set(year, month, dayOfMonth, 23, 59, 59);
                    tvEndDate.setText(formatDate(endDate.getTime()));
                }
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, dateSetListener,
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private boolean checkDate(long startDate, long centerDate, long endDate) {
//        Toast.makeText(this, "start: " + startDate + "\n center: " + centerDate + "\n end: " + endDate, Toast.LENGTH_SHORT).show();
        if (!tvStartDate.getText().toString().equals("Ngày bắt đầu") && tvEndDate.getText().toString().equals("Ngày kết thúc"))
            return centerDate >= startDate;

        if (tvStartDate.getText().toString().equals("Ngày bắt đầu") && !tvEndDate.getText().toString().equals("Ngày kết thúc"))
            return centerDate <= endDate;

        return startDate <= centerDate && endDate >= centerDate;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (forum != null && forum.getForumId() != 0)
            getPostByForumId(null, null);
        else if (forum != null && forum.getForumId() == 0)
            getAllPost(filter, sort);
        else
            getAllPost(filter, sort);
    }

    private void init() {
        tbListPost = findViewById(R.id.tbListPost);
        setSupportActionBar(tbListPost);
        tvNameForum = findViewById(R.id.tvNameForum);
        tvDesForum = findViewById(R.id.tvDesForum);
        tvNoPost = findViewById(R.id.tvNoPost);
        tvFilterPost = findViewById(R.id.tvFilterPost);
        tvSortPost = findViewById(R.id.tvSortPost);
        imvFilterPost = findViewById(R.id.imvFilterPost);
        rvListPost = findViewById(R.id.rvListPost);
        fabAddPost = findViewById(R.id.fabAddPost);
        imvSortPost = findViewById(R.id.imvSortPost);

        btnFilterDate = findViewById(R.id.btnFilterDate);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(this, R.layout.item_post, posts, this);
        rvListPost.setAdapter(postAdapter);
        rvListPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        forum = (Forum) getIntent().getSerializableExtra("forum");
        tvDesForum.setText(forum.getDescription());
        if (forum.getDescription().equals(""))
            tvDesForum.setVisibility(View.GONE);

        imvFilterPost.setOnClickListener(this);
        imvSortPost.setOnClickListener(this);
        fabAddPost.setOnClickListener(this);

        btnFilterDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
    }

    private void getAllPost(String filter, String sort) {
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (filter == null || Objects.requireNonNull(post).getCategoryName().equals(filter))
                                posts.add(post);
                            else if (filter.equals("filterDate") && checkDate(startDate.getTimeInMillis(), post.getApproveDate(), endDate.getTimeInMillis()))
                                posts.add(post);
                        }
                        if (posts.size() > 0) {
                            tvNoPost.setVisibility(View.GONE);
                            rvListPost.setVisibility(View.VISIBLE);
                        } else {
                            tvNoPost.setVisibility(View.VISIBLE);
                            rvListPost.setVisibility(View.GONE);
                        }
                        tvNameForum.setText(forum.getName() + " (" + posts.size() + ")");

                        Collections.reverse(posts);

                        if (sort != null) {
                            if (sort.equals(SORT_INCREASE_VIEWS)) // Sắp xếp bài viết có lượt xem tăng dần
                                posts.sort((p1, p2) -> Math.toIntExact(p1.getView() - p2.getView()));

                            if (sort.equals(SORT_DECREASE_VIEWS)) // Sắp xếp bài viết có lượt xem giảm dần
                                posts.sort((p1, p2) -> Math.toIntExact(p2.getView() - p1.getView()));

                            if (sort.equals(SORT_OLDEST)) // Bài viết cũ nhất đầu tiên
                                posts.sort((p1, p2) -> Math.toIntExact(p1.getPostId() - p2.getPostId()));

                            if (sort.equals(SORT_EARLIEST)) // Bài viết mới nhất đầu tiên
                                posts.sort((p1, p2) -> Math.toIntExact(p2.getPostId() - p1.getPostId()));

                            if (sort.equals(SORT_TITLE_AZ))
                                posts.sort((p1, p2) -> collator.compare(p1.getTitle(), p2.getTitle()));
//                                posts.sort((p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()));

                            if (sort.equals(SORT_TITLE_ZA))
                                posts.sort((p1, p2) -> collator.compare(p2.getTitle(), p1.getTitle()));
//                                posts.sort((p1, p2) -> p2.getTitle().compareToIgnoreCase(p1.getTitle()));
                        }

                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getPostByForumId(String filter, String sort) {
        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .orderByChild("forumId").equalTo(Objects.requireNonNull(forum).getForumId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsPost) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : dsPost.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (Objects.requireNonNull(post).getStatus().equals(STATUS_ENABLE))
                                if (filter == null || post.getCategoryName().equals(filter))
                                    posts.add(post);
                                else if (filter.equals("filterDate") && checkDate(startDate.getTimeInMillis(), post.getCreatedDate(), endDate.getTimeInMillis()))
                                    posts.add(post);
                        }
                        if (posts.size() > 0) {
                            tvNoPost.setVisibility(View.GONE);
                            rvListPost.setVisibility(View.VISIBLE);
                        } else {
                            tvNoPost.setVisibility(View.VISIBLE);
                            rvListPost.setVisibility(View.GONE);
                        }
                        tvNameForum.setText(forum.getName() + " (" + posts.size() + ")");
                        Collections.reverse(posts);

                        if (sort != null) {
                            if (sort.equals(SORT_INCREASE_VIEWS)) // Sắp xếp bài viết có lượt xem tăng dần
                                posts.sort((p1, p2) -> Math.toIntExact(p1.getView() - p2.getView()));

                            if (sort.equals(SORT_DECREASE_VIEWS)) // Sắp xếp bài viết có lượt xem giảm dần
                                posts.sort((p1, p2) -> Math.toIntExact(p2.getView() - p1.getView()));

                            if (sort.equals(SORT_OLDEST)) // Bài viết cũ nhất đầu tiên
                                posts.sort((p1, p2) -> Math.toIntExact(p1.getPostId() - p2.getPostId()));

                            if (sort.equals(SORT_EARLIEST)) // Bài viết mới nhất đầu tiên
                                posts.sort((p1, p2) -> Math.toIntExact(p2.getPostId() - p1.getPostId()));

                            if (sort.equals(SORT_TITLE_AZ))
                                posts.sort((p1, p2) -> collator.compare(p1.getTitle(), p2.getTitle()));
//                                posts.sort((p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()));

                            if (sort.equals(SORT_TITLE_ZA))
                                posts.sort((p1, p2) -> collator.compare(p2.getTitle(), p1.getTitle()));
//                                posts.sort((p1, p2) -> p2.getTitle().compareToIgnoreCase(p1.getTitle()));
                        }

                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvFilterPost:
                showPopupMenu();
                break;
            case R.id.imvSortPost:
                showPopupMenuSort();
                break;
            case R.id.tvStartDate:
                typeDatePicker = TYPE_START_DATE;
                showDatePickerDialog();
                break;
            case R.id.tvEndDate:
                typeDatePicker = TYPE_END_DATE;
                showDatePickerDialog();
                break;
            case R.id.btnFilterDate:
                filter = "filterDate";
                if (forum.getForumId() == 0)
                    getAllPost(filter, sort);
                else
                    getPostByForumId(filter, sort);
                break;
            case R.id.fabAddPost:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intent = new Intent(this, AddPostActivity.class);
                    intent.putExtra("forum", forum);
                    startActivity(intent);
                } else
                    Toast.makeText(this, "Bạn cần đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(this, DetailPostActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi, NonConstantResourceId, SetTextI18n")
    private void showPopupMenu() {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_popup_list_post, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, menuBuilder, imvFilterPost);
        menuPopupHelper.setForceShowIcon(true);

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuDeleteFilter:
                        filter = null;
                        tvStartDate.setText("Ngày bắt đầu");
                        tvEndDate.setText("Ngày kết thúc");
                        break;
                    case R.id.mnuFilterQA:
                        filter = HOI_DAP;
                        break;
                    case R.id.mnuFilterShareKnowledge:
                        filter = CHIA_SE_KIEN_THUC;
                        break;
                }
                tvFilterPost.setText(filter);
                if (forum.getForumId() == 0)
                    getAllPost(filter, sort);
                else
                    getPostByForumId(filter, sort);

                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });

        menuPopupHelper.show();
    }

    @SuppressLint("RestrictedApi, NonConstantResourceId")
    private void showPopupMenuSort() {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_popup_sort_post, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, menuBuilder, imvSortPost);
        menuPopupHelper.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuDeleteSort:
                        sort = null;
                        break;
                    case R.id.mnuSortEarliest:
                        sort = SORT_EARLIEST;
                        break;
                    case R.id.mnuSortOldest:
                        sort = SORT_OLDEST;
                        break;
                    case R.id.mnuSortIncreaseViews:
                        sort = SORT_INCREASE_VIEWS;
                        break;
                    case R.id.mnuSortDecreaseViews:
                        sort = SORT_DECREASE_VIEWS;
                        break;
                    case R.id.mnuSortTitleAZ:
                        sort = SORT_TITLE_AZ;
                        break;
                    case R.id.mnuSortTitleZA:
                        sort = SORT_TITLE_ZA;
                        break;
                }
                tvSortPost.setText(sort);
                if (forum.getForumId() == 0)
                    getAllPost(filter, sort);
                else
                    getPostByForumId(filter, sort);

                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });
        menuPopupHelper.show();
    }
}

//    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//try {
//        Date d1 = sdf.parse(date1);
//        Date d2 = sdf.parse(date2);
//        if (d1.compareTo(d2) < 0) {
//        // date1 < date2
//        } else if (d1.compareTo(d2) > 0) {
//        // date1 > date2
//        } else {
//        // date1 == date2
//        }
//        } catch (ParseException e) {
//        e.printStackTrace();
//        }