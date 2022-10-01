package com.example.digi_dhobi;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digi_dhobi.adapter.WashViewAdapter;
import com.example.digi_dhobi.api.WashService;
import com.example.digi_dhobi.context.GlobalContext;
import com.example.digi_dhobi.model.Wash;
import com.example.digi_dhobi.utils.Constants;
import com.example.digi_dhobi.utils.HttpStatus;
import com.example.digi_dhobi.utils.MyRunnable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class AllWashesActivity extends AppCompatActivity {

    private WashService washService;
    private RecyclerView pastWashesRecyclerView;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washes);
        pastWashesRecyclerView = findViewById(R.id.recycler_pastwashes);

        pastWashesRecyclerView.setHasFixedSize(true);
        pastWashesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        WashViewAdapter pastWashViewAdapter = new WashViewAdapter(AllWashesActivity.this, new ArrayList<>());
        pastWashesRecyclerView.setAdapter(pastWashViewAdapter);

        washService = new WashService();
        loadingBar = new ProgressDialog(this);
        loadingBar.show();

        System.out.print("harshhh");
        washService.listWashesForUser(GlobalContext.currentOnlineUser.getId(),new PostLoadWashesRunnable());
    }

    class PostLoadWashesRunnable extends MyRunnable {

        @Override
        public void run() {

            if (HttpStatus.SC_OK == statusCode) {
                try {
                    GlobalContext.washes = Constants.OBJECT_MAPPER.readValue(jsonResponse, new TypeReference<List<Wash>>(){});
                    List<Wash> completedWashes = new ArrayList<>();
                    if(GlobalContext.washes.size()>0) {
                        if(GlobalContext.washes.get(0).getStatus().equals("IN_PROGRESS")) {
                            completedWashes = GlobalContext.washes.subList(1, GlobalContext.washes.size());
                        } else {
                            completedWashes = GlobalContext.washes;
                        }
                    }

                    //Paper.book().write(GlobalContext.userKey, user);
                    Toast.makeText(AllWashesActivity.this, "Washes output : " +
                            Constants.OBJECT_MAPPER.writeValueAsString(GlobalContext.washes), Toast.LENGTH_LONG).show();

                    WashViewAdapter pastWashViewAdapter = new WashViewAdapter(AllWashesActivity.this, completedWashes);
                    pastWashesRecyclerView.setAdapter(pastWashViewAdapter);

                } catch (JsonProcessingException e) {
                    System.out.print("harshhh");
                    e.printStackTrace();
                }

            } else {
                System.out.print("harshhh");
                Toast.makeText(AllWashesActivity.this, "Error Loading Details : " + jsonResponse, Toast.LENGTH_LONG).show();
            }
            loadingBar.dismiss();
        }
    }

}