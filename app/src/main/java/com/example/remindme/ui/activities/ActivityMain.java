package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.ui.main.AdapterSectionsPager;
import com.google.android.material.tabs.TabLayout;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AdapterSectionsPager adapterSectionsPager = new AdapterSectionsPager(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapterSectionsPager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActivityHelper.setTitle(this, "");

        final Button btnNewReminder = findViewById(R.id.btn_main_new_reminder);
        btnNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewReminderActivity = new Intent(ActivityMain.this, ActivityReminderInput.class);
                startActivity(addNewReminderActivity);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.context_menu, menu);
//
////        final MenuItem menuItem = menu.findItem(R.id.action_search);
////        final SearchView sv = (SearchView) menuItem.getActionView();
////        sv.setQueryHint("Enter name to find");
////
////        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
////
////                if(selectedTabFrag != null && selectedTabFrag.getClass() == FragmentActiveReminder.class){
////                    ((ISearchable)selectedTabFrag).search(query);
////                }
////
////                return true;
////            }
////
////            @Override
////            public boolean onQueryTextChange(String newText) {
////                //Toast.makeText(MainActivity.this, newText, Toast.LENGTH_SHORT).show();
////                return false;
////            }
////        });
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
            case R.id.action_settings:
                Intent settings_intent = new Intent(this, ActivitySettings.class);
                startActivity(settings_intent);
                //Toast.makeText(ActivityMain.this, "Halua", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}