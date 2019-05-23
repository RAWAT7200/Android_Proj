package com.paril.mlaclientapp.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

/**
 * Created by paril on 10-Nov-17.
 */

public class ContactChip implements ChipInterface{
    private String id,email,name,type;

    public ContactChip(String id, String email, String name,String type) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type=type;
    }

    public String getEmail(){
        return  email;
    }


    @Override
    public Object getId() {
        return id;
    }

    @Override
    public String getInfo() {
        return type;
    }

    @Override
    public String getLabel() {
        return name+"("+email+")";
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

}
