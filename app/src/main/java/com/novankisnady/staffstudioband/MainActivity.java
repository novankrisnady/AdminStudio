package com.novankisnady.staffstudioband;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novankisnady.staffstudioband.Adapter.MyStateAdapter;
import com.novankisnady.staffstudioband.Common.Common;
import com.novankisnady.staffstudioband.Common.SpacesItemDecoration;
import com.novankisnady.staffstudioband.Interface.IOnAllStateLoadListener;
import com.novankisnady.staffstudioband.Model.City;
import com.novankisnady.staffstudioband.Model.Room;
import com.novankisnady.staffstudioband.Model.Studio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements IOnAllStateLoadListener {

    @BindView(R.id.recycler_state)
    RecyclerView recycler_state;

    CollectionReference allRentalCollection;

    IOnAllStateLoadListener iOnAllStateLoadListener;

    MyStateAdapter adapter;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);
        String user = Paper.book().read(Common.LOGGED_KEY);
        if (TextUtils.isEmpty(user))

        {
            setContentView(R.layout.activity_main);

            ButterKnife.bind(this);

            initView();

            init();

            loadAllStateFromFireStore();
        }
        else
        {
            Gson gson = new Gson();
            Common.state_name = Paper.book().read(Common.STATE_KEY);
            Common.selected_studio = gson.fromJson(Paper.book().read(Common.STUDIO_KEY,""),
                    new TypeToken<Studio>(){}.getType());
            Common.currentRoom = gson.fromJson(Paper.book().read(Common.ROOM_KEY,""),
                    new TypeToken<Room>(){}.getType());

            Intent intent = new Intent(this,StaffHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

    private void loadAllStateFromFireStore() {
        dialog.show();

        allRentalCollection
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iOnAllStateLoadListener.onAllStateLoadFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    List<City> cities = new ArrayList<>();
                    for (DocumentSnapshot citySnapShot:task.getResult())
                    {
                        City city = citySnapShot.toObject(City.class);
                        cities.add(city);
                    }
                    iOnAllStateLoadListener.onAllStateLoadSuccess(cities);
                }
            }
        });
    }

    private void init() {
        allRentalCollection = FirebaseFirestore.getInstance().collection("AllRental");
        iOnAllStateLoadListener = this;
        dialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();

    }

    private void initView() {
        recycler_state.setHasFixedSize(true);
        recycler_state.setLayoutManager(new GridLayoutManager(this,2));
        recycler_state.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onAllStateLoadSuccess(List<City> cityList) {
        adapter = new MyStateAdapter(this,cityList);
        recycler_state.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onAllStateLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        dialog.dismiss();
    }
}