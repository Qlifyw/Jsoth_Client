package com.example.herem1t.rc_client.ui.screens.mainlist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.herem1t.rc_client.Constants;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.utils.DrawableUtils;

import java.util.List;

import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_AVAILABLE;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_DOWN;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_NOT_RESPONDING;

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
            ext_ip = itemView.findViewById(R.id.tv_ext_ip);
            description = itemView.findViewById(R.id.tv_description);
            status = itemView.findViewById(R.id.iv_status);
            logo = itemView.findViewById(R.id.iv_logo);
            favourite =  itemView.findViewById(R.id.iv_favourite);

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
        String description = server.getDescription().length() > 25 ?
                server.getDescription().substring(0, 25) + "..." : server.getDescription();
        holder.description.setText(description);
        Context context = holder.logo.getContext();

        byte[] iconByte = DrawableUtils.getImageAsByte(context, server.getLogoPath(), "mipmap");
        Drawable drawable = DrawableUtils.getDrawableFromByte(context, iconByte);
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
