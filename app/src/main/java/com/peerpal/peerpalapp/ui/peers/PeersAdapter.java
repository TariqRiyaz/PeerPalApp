package com.peerpal.peerpalapp.ui.peers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.peerpal.peerpalapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PeersAdapter extends RecyclerView.Adapter<PeersViewHolder> {
    private Context context;
    private ArrayList<PeersClass> peersList;
    public void setList(ArrayList<PeersClass> peersList){
        this.peersList = peersList;
    }
    public PeersAdapter(Context context, ArrayList<PeersClass> peersList){
        this.context = context;
        this.peersList = peersList;
    }
    @NonNull
    @Override
    public PeersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peers_item, parent, false);
        return new PeersViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PeersViewHolder holder, int position) {
        String hobbyString = peersList.get(position).getPeersHobbies()[0];

        for (int i = 1; i < peersList.get(position).getPeersHobbies().length; i++) {
            hobbyString += (", " + peersList.get(position).getPeersHobbies()[i]);
        }

        Picasso.get().load(peersList.get(position).getPeersImage()).into(holder.peersImage);
        holder.peersName.setText(peersList.get(position).getPeersName());
        holder.peersDegree.setText("Degree: " + peersList.get(position).getPeersDegree());
        holder.peersHobbies.setText("Hobbies: " + hobbyString);
    }
    @Override
    public int getItemCount() {
        return peersList.size();
    }
}

class PeersViewHolder extends RecyclerView.ViewHolder {
    ImageView peersImage;
    TextView peersName, peersDegree, peersHobbies;
    CardView peersCard;

    public PeersViewHolder(@NonNull View itemView) {
        super(itemView);

        peersImage = itemView.findViewById(R.id.peersImage);
        peersName = itemView.findViewById(R.id.peersName);
        peersDegree = itemView.findViewById(R.id.peersDegree);
        peersHobbies = itemView.findViewById(R.id.peersHobbies);
        peersCard = itemView.findViewById(R.id.peersCard);
    }
}