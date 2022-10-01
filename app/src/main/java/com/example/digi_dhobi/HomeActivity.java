package com.example.digi_dhobi;

import android.app.ProgressDialog;
import android.app.appsearch.GlobalSearchSession;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.digi_dhobi.api.WashService;
import com.example.digi_dhobi.context.GlobalContext;
import com.example.digi_dhobi.model.Wash;
import com.example.digi_dhobi.utils.Constants;
import com.example.digi_dhobi.utils.HttpStatus;
import com.example.digi_dhobi.utils.MyRunnable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private WashService washService;
    private ProgressDialog loadingBar;
    private TextView name, mobile, regno, block, room, dropdate, units, token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name = findViewById(R.id.nameField);
        mobile = findViewById(R.id.phoneField);
        regno = findViewById(R.id.regnoField);
        block = findViewById(R.id.blockField);
        room = findViewById(R.id.roomField);
        dropdate = findViewById(R.id.wash_drop_date);
        units = findViewById(R.id.wash_quantity);
        token = findViewById(R.id.wash_token);

        name.setText(GlobalContext.currentOnlineUser.getName());
        mobile.setText(GlobalContext.currentOnlineUser.getMobile());
        regno.setText("Reg No : " + GlobalContext.currentOnlineUser.getRoll());
        block.setText("Block: " + GlobalContext.currentOnlineUser.getBlock());
        room.setText("Room : " + GlobalContext.currentOnlineUser.getRoom());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);

        washService = new WashService();
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Loading Details");
        loadingBar.setCancelable(false);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        washService.listWashesForUser(GlobalContext.currentOnlineUser.getId(), new MyRunnable() {
            @Override
            public void run() {
                if (HttpStatus.SC_OK == statusCode) {
                    try {
                        GlobalContext.washes = Constants.OBJECT_MAPPER.readValue(jsonResponse, new TypeReference<List<Wash>>(){});
//                    Paper.book().write(GlobalContext.userKey, user);
                        int f1=0;
                        if(GlobalContext.washes.size()>0) {
                            if(GlobalContext.washes.get(0).getStatus().equals("IN_PROGRESS")) {
                                f1=1;
                                Date d = new Date(GlobalContext.washes.get(0).getSubmitTime() * 1000);
                                DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                dropdate.setText("Drop Off Date : " + f.format(d));
                                units.setText(GlobalContext.washes.get(0).getUnits());
                                token.setText(GlobalContext.washes.get(0).getToken());
                            }
                        }
                        if(f1==0){
                            dropdate.setVisibility(View.INVISIBLE);
                            units.setVisibility(View.INVISIBLE);
                            token.setVisibility(View.INVISIBLE);
                        }

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Error Loading Details : " + jsonResponse, Toast.LENGTH_LONG).show();
                }
                loadingBar.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void resetActionBar() {
//        if (actionBarCartTextView == null) {
//            return;
//        }
//        if (GlobalContext.cart == null || GlobalContext.cart.getItems() == null || GlobalContext.cart.getItems().isEmpty()) {
//            actionBarCartTextView.setVisibility(View.GONE);
//        } else {
//            actionBarCartTextView.setVisibility(View.VISIBLE);
//            actionBarCartTextView.setText(String.format("%d", GlobalContext.cart.getItems().size()));
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.badge);
        View actionView = menuItem.getActionView();
        resetActionBar();

        ImageView cartImageView = actionView.findViewById(R.id.actionbar_cart_imageview);

        cartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked");
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.badge) {
            System.out.println("Badge clicked");
            Intent intent=new Intent(HomeActivity.this, QrCodeActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_washes)
        {
            startActivity(new Intent(HomeActivity.this, AllWashesActivity.class));
        }

        else if (id == R.id.nav_logout)
        {
//            Paper.book().destroy();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
