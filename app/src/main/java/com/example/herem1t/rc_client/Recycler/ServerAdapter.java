package com.example.herem1t.rc_client.Recycler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.herem1t.rc_client.Constants;
import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Database.Server;

import java.util.List;
import java.util.Random;

import static com.example.herem1t.rc_client.Constants.SERVER_NOT_RESPONDING;
import static com.example.herem1t.rc_client.Constants.SERVER_AVAILABLE;
import static com.example.herem1t.rc_client.Constants.SERVER_DOWN;

/**
 * Created by Herem1t on 16.04.2018.
 */

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {

    private List<Server> serverList;

    public class ServerViewHolder extends RecyclerView.ViewHolder {

        public TextView ext_ip, description;
        public ImageView status, logo, favourite;

        public ServerViewHolder(View itemView) {
            super(itemView);
            ext_ip = (TextView) itemView.findViewById(R.id.tv_ext_ip);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            status = (ImageView) itemView.findViewById(R.id.iv_status);
            logo = (ImageView) itemView.findViewById(R.id.iv_logo);
            favourite = (ImageView)  itemView.findViewById(R.id.iv_favourite);

        }

    }

    public ServerAdapter(List<Server> data) {
        this.serverList = data;
    }

    @Override
    public ServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_row, parent, false);
        return new ServerViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        Server server = serverList.get(position);
        holder.ext_ip.setText(server.getExternalIP());
        holder.description.setText(server.getDescription());
        Context context = holder.logo.getContext();
        //Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("ic_marker", "mipmap", context.getPackageName()));

//        String icon = Constants.OS[new Random().nextInt(Constants.OS.length-1)];
//        byte[] image = DrawableAction.getImageAsByte(context, icon, "mipmap");
//        Drawable drawable = DrawableAction.getDrawableFromByte(context, image);
        String icon = DBOperations.getServerLogo(context, server.getExternalIP());
        if (icon == null) icon = Constants.UNKNOWN_LINUX;
        byte[] icon_byte = DrawableAction.getImageAsByte(context, icon, "mipmap");
        Drawable drawable = DrawableAction.getDrawableFromByte(context, icon_byte);
        holder.logo.setImageDrawable(drawable);


        if (server.isFavourite()) {
            holder.favourite.setVisibility(View.VISIBLE);
        } else {
            holder.favourite.setVisibility(View.INVISIBLE);
        }


        GradientDrawable background = (GradientDrawable)holder.status.getBackground();
        switch (server.getStatus()) {
            case SERVER_AVAILABLE:
                background.setColor(context.getResources().getColor(R.color.green));
                break;
            case SERVER_DOWN:
                background.setColor(context.getResources().getColor(R.color.red));
                break;
            case SERVER_NOT_RESPONDING:
                background.setColor(context.getResources().getColor(R.color.yellow));
                break;
            default:
                background.setColor(context.getResources().getColor(R.color.red));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return serverList.size();
    }
}
