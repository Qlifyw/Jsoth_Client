package com.example.herem1t.rc_client.Recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.herem1t.rc_client.Constants;

import java.io.ByteArrayOutputStream;

/**
 * Created by Herem1t on 23.04.2018.
 */

public class DrawableAction {

    // TODO: use it in getting screenshot function
    public static byte[] getImageAsByte(Context context, String filename, String resPath) {
        Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier(filename, resPath, context.getPackageName()));
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // TODO use it in Adapter
    public static Drawable getDrawableFromPath(Context context, String filename, String path ) {
        return context.getResources().getDrawable(context.getResources().getIdentifier(filename, path, context.getPackageName()));
    }

    public static Drawable getDrawableFromByte(Context context, byte[] imageBytes) {
        return  new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
    }

    // TODO put  result in db
    public static String getOSIconName(String os) {
        String osname = os.toLowerCase();
        if (osname.contains("win")) {
            return Constants.WINDOWS;
        } else if (osname.contains("nix") || osname.contains("nix") || osname.contains("aix")) {
            if (osname.contains("ubuntu")) {
                return  Constants.UBUNTU;
            } else if (osname.contains("mint")) {
                return  Constants.MINT;
            } else if (osname.contains("debian")) {
                return  Constants.DEBIAN;
            } else if (osname.contains("fedora")) {
                return  Constants.FEDORA;
            } else if (osname.contains("gentoo")) {
                return  Constants.GENTOO;
            } else if (osname.contains("arch")) {
                return  Constants.ARCH_LINUX;
            } else if (osname.contains("kali")) {
                return  Constants.KALI_LINUX;
            } else if (osname.contains("centos")) {
                return  Constants.CENT_OS;
            } else if (osname.contains("red hat") || (osname.contains("redhat"))) {
                return  Constants.RED_HAT;
            } else if(osname.contains("suse")) {
                return  Constants.SUSE;
            }
        } else if (osname.contains("mac") || osname.contains("darwin")) {
            return Constants.MACOS;
        }
        // TODO add default linux icon
        return "-";
    }
}
