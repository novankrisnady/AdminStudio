package com.novankisnady.staffstudioband;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.novankisnady.staffstudioband.Adapter.MyStudioAdapter;
import com.novankisnady.staffstudioband.Common.Common;
import com.novankisnady.staffstudioband.Common.SpacesItemDecoration;
import com.novankisnady.staffstudioband.Interface.IGetRoomListener;
import com.novankisnady.staffstudioband.Interface.IOnLoadCountStudio;
import com.novankisnady.staffstudioband.Interface.IStudioBandLoadListener;
import com.novankisnady.staffstudioband.Interface.IUserLoginRememberListener;
import com.novankisnady.staffstudioband.Model.Room;
import com.novankisnady.staffstudioband.Model.Studio;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class StudioListActivity extends AppCompatActivity implements IOnLoadCountStudio, IStudioBandLoadListener, IGetRoomListener, IUserLoginRememberListener {

    @BindView(R.id.txt_studio_count)
    TextView txt_studio_count;

    @BindView(R.id.recycler_studio)
    RecyclerView recycler_studio;

    IOnLoadCountStudio iOnLoadCountStudio;
    IStudioBandLoadListener iStudioBandLoadListener;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_list);

        ButterKnife.bind(this);

        initView();

        init();

        loadStudioBaseOnCity(Common.state_name);
    }

    private void loadStudioBaseOnCity(String name) {
        dialog.show();

        FirebaseFirestore.getInstance().collection("AllRental")
                .document(name)
                .collection("StudioBand")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<Studio> studios = new ArrayList<>();
                            iOnLoadCountStudio.onLoadCountStudioSuccess(task.getResult().size());
                            for (DocumentSnapshot studioSnapShot:task.getResult())
                            {
                                Studio studio = studioSnapShot.toObject(Studio.class);
                                studio.setStudioId(studioSnapShot.getId());
                                studios.add(studio);

                            }
                            iStudioBandLoadListener.onStudioBandLoadSuccess(studios);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iStudioBandLoadListener.onStudioBandLoadFailed(e.getMessage());
            }
        });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();
        iOnLoadCountStudio=this;
        iStudioBandLoadListener = this;
    }

    private void initView() {
        recycler_studio.setHasFixedSize(true);
        recycler_studio.setLayoutManager(new GridLayoutManager(this,2));
        recycler_studio.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onLoadCountStudioSuccess(int count) {
        txt_studio_count.setText(new StringBuilder("All Studio (")
        .append(count)
        .append(")"));
    }

    @Override
    public void onStudioBandLoadSuccess(List<Studio> studioBandList) {
        MyStudioAdapter studioAdapter = new MyStudioAdapter(this,studioBandList,this,this);
        recycler_studio.setAdapter(studioAdapter);

        dialog.dismiss();
    }

    @Override
    public void onStudioBandLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onGetRoomSuccess(Room room) {
        Common.currentRoom = room;
        Paper.book().write(Common.ROOM_KEY,new Gson().toJson(room));

    }

    @Override
    public void onUserLoginSuccess(String user) {
        //Save user
        Paper.init(this);
        Paper.book().write(Common.LOGGED_KEY,user);
        Paper.book().write(Common.STATE_KEY,Common.state_name);
        Paper.book().write(Common.STUDIO_KEY,new Gson().toJson(Common.selected_studio));

    }
}