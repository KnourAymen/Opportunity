package com.example.opportunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView usersRecyclerView;
    List<Users> usersList;
    TraineeCustomAdapter traineeCustomAdapter;
    SearchView searchView;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        usersRecyclerView = findViewById(R.id.users_recycler_view);
        searchView = findViewById(R.id.search);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(AdminActivity.this, 1);
        usersRecyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        usersList = new ArrayList<>();
        traineeCustomAdapter = new TraineeCustomAdapter(AdminActivity.this, usersList);
        usersRecyclerView.setAdapter(traineeCustomAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Users users = itemSnapshot.getValue(Users.class);
                    users.setUserKey(itemSnapshot.getKey());
                    usersList.add(users);
                }
                traineeCustomAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    public void searchList(String text) {
        ArrayList<Users> searchUsersList = new ArrayList<>();
        for (Users users : usersList) {
            if (users.getUserFullName().toLowerCase().contains(text.toLowerCase()) || users.getUserPhoneNumber().contains(text) || users.getUserSpecialty().toLowerCase().contains(text.toLowerCase())) {
                searchUsersList.add(users);
            }
        }
        traineeCustomAdapter.searchDataList(searchUsersList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if ((time + 2000) > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Press again", Toast.LENGTH_LONG).show();
        }
        time = System.currentTimeMillis();
    }
}