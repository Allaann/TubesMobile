package com.example.mytix;


import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytix.database.DataHelper;
import com.example.mytix.session.SessionManager;


import java.util.HashMap;

public class Kereta_page extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinAsal, spinTujuan, spinDewasa;
    SessionManager session;
    String email;
    int id_book;
    public String sAsal, sTujuan, sTanggal, sDewasa;
    int jmlDewasa;
    int hargaDewasa;
    int hargaTotalDewasa, hargaTotal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kereta_page);

        dbHelper = new DataHelper(Kereta_page.this);
        db = dbHelper.getReadableDatabase();

        final String[] asal = {"Jakarta", "Bandung", "Purwokerto", "Yogyakarta", "Surabaya"};
        final String[] tujuan = {"Jakarta", "Bandung", "Purwokerto", "Yogyakarta", "Surabaya"};
        final String[] dewasa = {"0", "1", "2", "3", "4", "5", "6", "7"};


        spinAsal = findViewById(R.id.asal);
        spinTujuan = findViewById(R.id.tujuan);
        spinDewasa = findViewById(R.id.dewasa);


        ArrayAdapter<CharSequence> adapterAsal = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, asal);
        adapterAsal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAsal.setAdapter(adapterAsal);

        ArrayAdapter<CharSequence> adapterTujuan = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, tujuan);
        adapterTujuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTujuan.setAdapter(adapterTujuan);

        ArrayAdapter<CharSequence> adapterDewasa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, dewasa);
        adapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDewasa.setAdapter(adapterDewasa);



        spinAsal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sAsal = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinTujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sTujuan = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinDewasa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sDewasa = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Button btnBook = findViewById(R.id.book);


        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);


        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perhitunganHarga();
                if (sAsal != null && sTujuan != null && sTanggal != null && sDewasa != null) {
                    if ((sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("jakarta"))
                            || (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("bandung"))
                            || (sAsal.equalsIgnoreCase("purwokerto") && sTujuan.equalsIgnoreCase("purwokerto"))
                            || (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("yogyakarta"))
                            || (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("surabaya"))) {
                        Toast.makeText(Kereta_page.this, "Asal dan Tujuan tidak boleh sama !", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(Kereta_page.this)
                                .setTitle("Ingin melakukan pesanan sekarang?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            db.execSQL("INSERT INTO TB_BOOK (asal, tujuan, tanggal, dewasa) VALUES ('" +
                                                    sAsal + "','" +
                                                    sTujuan + "','" +
                                                    sTanggal + "','" +
                                                    sDewasa + "','" +
                                                     "');");
                                            cursor = db.rawQuery("SELECT id_book FROM TB_BOOK ORDER BY id_book DESC", null);
                                            cursor.moveToLast();
                                            if (cursor.getCount() > 0) {
                                                cursor.moveToPosition(0);
                                                id_book = cursor.getInt(0);
                                            }
                                            db.execSQL("INSERT INTO TB_HARGA (username, id_book, harga_dewasa, harga_total) VALUES ('" +
                                                    email + "','" +
                                                    id_book + "','" +
                                                    hargaTotalDewasa + "','" +

                                                    hargaTotal + "');");
                                            Toast.makeText(Kereta_page.this, "Pemesanan berhasil", Toast.LENGTH_LONG).show();
                                            finish();
                                        } catch (Exception e) {
                                            Toast.makeText(Kereta_page.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Tidak", null)
                                .create();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(Kereta_page.this, "Mohon lengkapi data pemesanan!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbKrl);
        toolbar.setTitle("Form Pemesanan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void perhitunganHarga() {
        if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaDewasa = 100000;

        } else if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("surabaya")) {
            hargaDewasa = 200000;

        } else if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("purwokerto")) {
            hargaDewasa = 150000;

        } else if (sAsal.equalsIgnoreCase("jakarta") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaDewasa = 180000;

        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaDewasa = 100000;

        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("surabaya")) {
            hargaDewasa = 120000;

        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("purwokerto")) {
            hargaDewasa = 120000;

        } else if (sAsal.equalsIgnoreCase("bandung") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaDewasa = 190000;

        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaDewasa = 200000;

        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaDewasa = 120000;

        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("purwokerto")) {
            hargaDewasa = 170000;

        } else if (sAsal.equalsIgnoreCase("surabaya") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaDewasa = 180000;

        } else if (sAsal.equalsIgnoreCase("purwokerto") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaDewasa = 150000;

        } else if (sAsal.equalsIgnoreCase("purwokerto") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaDewasa = 120000;

        } else if (sAsal.equalsIgnoreCase("purwokerto") && sTujuan.equalsIgnoreCase("yogyakarta")) {
            hargaDewasa = 80000;

        } else if (sAsal.equalsIgnoreCase("purwokerto") && sTujuan.equalsIgnoreCase("surabaya")) {
            hargaDewasa = 170000;

        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("jakarta")) {
            hargaDewasa = 180000;

        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("bandung")) {
            hargaDewasa = 190000;

        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("purwokerto")) {
            hargaDewasa = 80000;

        } else if (sAsal.equalsIgnoreCase("yogyakarta") && sTujuan.equalsIgnoreCase("surabaya")) {
            hargaDewasa = 180000;

        }

        jmlDewasa = Integer.parseInt(sDewasa);


        hargaTotalDewasa = jmlDewasa * hargaDewasa;

        hargaTotal = hargaTotalDewasa;
    }


}
