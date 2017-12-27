package com.example.vctorpuch.contactos;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Contacto> list;
    ContactListAdapter adapter = null;
    FloatingActionButton floating, call;
    EditText busqueda;
    public static SQLiteHelper sqLiteHelper;

    public static ContactListAdapter contactlist;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        sqLiteHelper = new SQLiteHelper(this, "Contacts.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CONTACTS(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, number VARCHAR, image BLOB)");

        floating = (FloatingActionButton) findViewById(R.id.faButton);
        call= (FloatingActionButton) findViewById(R.id.btncall);
        listView = (ListView) findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new ContactListAdapter(this, R.layout.contacts_items, list);
        listView.setAdapter(adapter);

        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContact.class);
                startActivity(intent);
            }

        });

        // get all data from sqlite
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM CONTACTS");
        list.clear();
        if (cursor.getCount()==0){
            Toast.makeText(getApplicationContext(), "No hay contactos almacenados",Toast.LENGTH_SHORT).show();
        }
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String number = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Contacto(name, number, image, id));
        }
        adapter.notifyDataSetChanged();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"));
                startActivity(i);
            }

        });

        final ListView list = (ListView) findViewById(R.id.listView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = list.getItemAtPosition(position);
                String number;
                Intent i = new Intent(Intent.ACTION_DIAL);
                Cursor cursor = sqLiteHelper.getData("SELECT * FROM CONTACTS");
                cursor.moveToPosition(position);
                number = cursor.getString(2);
                Toast.makeText(getApplicationContext(), " Llamando a: "+number,Toast.LENGTH_SHORT).show();
                i.setData(Uri.parse("tel:"+number));
                startActivity(i);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Editar", "Eliminar"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("Que accion se realizara");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = sqLiteHelper.getData("SELECT id FROM CONTACTS");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));

                            }
                            // show dialog update at here
                            showDialogUpdate(MainActivity.this, arrID.get(position));



                        } else {
                            // delete
                            Cursor c = sqLiteHelper.getData("SELECT id FROM CONTACTS");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    ImageView imageViewFood;
    private void showDialogUpdate(Activity activity, final int position){

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_contact_activity);
        dialog.setTitle("Editar");
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM CONTACTS");

        imageViewFood = (ImageView) dialog.findViewById(R.id.imageViewcontactupdate);
        final EditText edtName = (EditText) dialog.findViewById(R.id.edtName);
        final EditText edtNumber = (EditText) dialog.findViewById(R.id.edtNumber);
        byte [] IMAGE;
        cursor.moveToPosition(position-1);
        edtName.setText(cursor.getString(1));
        edtNumber.setText(cursor.getString(2));
        IMAGE=(cursor.getBlob(3));
        try{
        Bitmap bmp=BitmapFactory.decodeByteArray(IMAGE,0,IMAGE.length);
        if (bmp!=null)
        {
           imageViewFood.setImageBitmap(bmp);
        }
        }
        catch (Exception e)
        {
        Log.e("error ",e.getMessage());
        }
        FloatingActionButton btnUpdate = (FloatingActionButton) dialog.findViewById(R.id.btnUpdate);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    sqLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            edtNumber.getText().toString().trim(),
                            AddContact.imageViewToByte(imageViewFood),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Actualizado con exito!",Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Log.e("Error", error.getMessage());
                }
                updateContactList();
            }
        });
    }

    private void showDialogDelete(final int idFood){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);
        dialogDelete.setIcon(R.mipmap.alert);
        dialogDelete.setTitle("Advertencia!!");
        dialogDelete.setMessage("Seguro que desea eliminar el contacto?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sqLiteHelper.deleteData(idFood);
                    Toast.makeText(getApplicationContext(), "Eliminado Correctamente!",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("Error", e.getMessage());
                }

                updateContactList();
            }
        });

        dialogDelete.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateContactList(){
        // get all data from sqlite
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM CONTACTS");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String number = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            list.add(new Contacto(name, number, image, id));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else {
                Toast.makeText(getApplicationContext(), "No tienes permisos suficientes!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewFood.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}