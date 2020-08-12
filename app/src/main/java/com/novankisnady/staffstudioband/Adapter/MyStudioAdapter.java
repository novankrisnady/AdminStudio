package com.novankisnady.staffstudioband.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.novankisnady.staffstudioband.Common.Common;
import com.novankisnady.staffstudioband.Common.CustomLoginDialog;
import com.novankisnady.staffstudioband.Interface.IDialogClickListener;
import com.novankisnady.staffstudioband.Interface.IGetRoomListener;
import com.novankisnady.staffstudioband.Interface.IRecyclerItemSelectedListener;
import com.novankisnady.staffstudioband.Interface.IUserLoginRememberListener;
import com.novankisnady.staffstudioband.Model.Room;
import com.novankisnady.staffstudioband.Model.Studio;
import com.novankisnady.staffstudioband.R;
import com.novankisnady.staffstudioband.StaffHomeActivity;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MyStudioAdapter extends RecyclerView.Adapter<MyStudioAdapter.MyViewHolder> implements IDialogClickListener {

    Context context;
    List<Studio> studioList;
    List<CardView> itemViewList;

    IUserLoginRememberListener iUserLoginRememberListener;
    IGetRoomListener iGetRoomListener;

    public MyStudioAdapter(Context context, List<Studio> studioList,IUserLoginRememberListener iUserLoginRememberListener, IGetRoomListener iGetRoomListener) {
        this.context = context;
        this.studioList = studioList;
        itemViewList = new ArrayList<>();
        this.iGetRoomListener = iGetRoomListener;
        this.iUserLoginRememberListener = iUserLoginRememberListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_studio,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_studio_name.setText(studioList.get(i).getName());
        myViewHolder.txt_studio_address.setText(studioList.get(i).getAddress());
        if (!itemViewList.contains(myViewHolder.card_studio))
            itemViewList.add(myViewHolder.card_studio);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {

                Common.selected_studio = studioList.get(position);
                showLoginDialog();

            }
        });
    }

    private void showLoginDialog() {
        CustomLoginDialog.getInstance()
                .showLoginDialog("STAFF LOGIN",
                        "LOGIN",
                        "CANCEL",
                        context,
                        this);
    }

    @Override
    public int getItemCount() {
        return studioList.size();
    }

    @Override
    public void onClickPositiveButton(DialogInterface dialogInterface, String userName, String password) {
        //Show loading dialog
        AlertDialog loading = new SpotsDialog.Builder().setCancelable(false)
                .setContext(context).build();

        loading.show();

        // /AllRental/Cikupa/StudioBand/9qREOUipdtsPtGIRHu6k/Room/KbjYvXFqEXNeUXLzRglw
        FirebaseFirestore.getInstance()
                .collection("AllRental")
                .document(Common.state_name)
                .collection("StudioBand")
                .document(Common.selected_studio.getStudioId())
                .collection("Room")
                .whereEqualTo("username",userName)
                .whereEqualTo("password",password)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().size() > 0)
                            {
                                dialogInterface.dismiss();

                                loading.dismiss();

                                iUserLoginRememberListener.onUserLoginSuccess(userName);

                                //Create Room
                                Room room = new Room();
                                for (DocumentSnapshot roomSnapShot:task.getResult())
                                {
                                    room = roomSnapShot.toObject(Room.class);
                                    room.setRoomId(roomSnapShot.getId());
                                }
                                iGetRoomListener.onGetRoomSuccess(room);

                                //Navigate staff home and clear all previous activity
                                Intent staffHome = new Intent(context, StaffHomeActivity.class);
                                staffHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                staffHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(staffHome);
                            }
                            else 
                            {
                                loading.dismiss();
                                Toast.makeText(context, "Wrong username/password or wong Studio", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_studio_name,txt_studio_address;
        CardView card_studio;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_studio = (CardView)itemView.findViewById(R.id.card_studio);
            txt_studio_address = (TextView)itemView.findViewById(R.id.txt_studio_address);
            txt_studio_name = (TextView)itemView.findViewById(R.id.txt_studio_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelected(view, getAdapterPosition());
        }
    }
}
