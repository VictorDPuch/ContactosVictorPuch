package com.example.vctorpuch.contactos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Contacto> contactList;
    private String number;

    public ContactListAdapter(Context context, int layout, ArrayList<Contacto> contactosList) {
        this.context = context;
        this.layout = layout;
        this.contactList = contactosList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtNumber;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtNumber = (TextView) row.findViewById(R.id.txtNumber);
            holder.imageView = (ImageView) row.findViewById(R.id.imgContact);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Contacto contact = contactList.get(position);

        holder.txtName.setText(contact.getName());
        holder.txtNumber.setText(contact.getNumber());

        byte[] foodImage = contact.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
