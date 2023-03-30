package com.example.cmput301w23t31project;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


/**
 * A class that represents a count leaderboard user object
 */
public class LeaderboardArrayAdapter extends ArrayAdapter<Player> {
    private Context context;
    private String currentUser;
    private String state;
    public LeaderboardArrayAdapter(Context context, ArrayList<Player> players, String currentUser, String state){
        super(context,0,players);
        this.context = context;
        this.currentUser = currentUser;
        this.state = state;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_leaderboard_count_list, parent, false);
        } else {
            view = convertView;
        }

        Player player = getItem(position);

        TextView playerName = view.findViewById(R.id.leaderboard_content_player_name_text);
        TextView score = view.findViewById(R.id.leaderboard_content_count);
        TextView usernameText = view.findViewById(R.id.leaderboard_content_user_name);
        ImageView profileBtn = view.findViewById(R.id.leaderboard_content_profile_button);
        TextView rank = view.findViewById(R.id.leaderboard_content_rank);
        rank.setText(String.valueOf(player.getRank()));
        //playerName.setText(player.getPlayerName());
        if (state.equals("COUNT")) {
            score.setText(String.valueOf(player.getCount()));
        } else if (state.equals("HIGHSCORE")) {
            score.setText(String.valueOf(player.getHighestScoringQR()));
        } else if (state.equals("TOTALSCORE")) {
            score.setText(String.valueOf(player.getTotalScore()));
        }
        playerName.setText(player.getUsername());

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerProfileActivity.class);
                intent.putExtra("crnt_username", currentUser);
                intent.putExtra("username",player.getUsername());

                context.startActivity(intent);
            }
        });



        return view;

    }
    public String getUsername() {
        return currentUser;
    }
}