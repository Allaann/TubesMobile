package com.example.mytix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mytix.database.DataHelper;
import com.example.mytix.session.SessionManager;

import java.util.Calendar;
import java.util.HashMap;

public class Pesawat_page extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinMaskapai, spinDestinasi, spinDewasa;
    SessionManager session;
    String email;
    int id_book;
    public String sMaskapai , sDestinasi, sTanggal, sDewasa;
    int jmlDewasa;
    int hargaDewasa;
    int hargaTotalDewasa, hargaTotal;

    private DatePickerDialog dpTanggal;
    Calendar newCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesawat_page);

        dbHelper = new DataHelper(Pesawat_page.this);
        db = dbHelper.getReadableDatabase();

        final String[] maskapai = {"Avansa", "APV", "Hiace", "Xenia", "L300"};
        final String[] destinasi = {"Jakarta", "Bali", "Surabaya", "Sidoarjo", "Blitar"};
        final String[] dewasa = {"0", "1", "2", "3", "4", "5", "6", "7"};
        final String[] anak = {"0", "1", "2", "3", "4", "5", "6", "7"};

        spinMaskapai = findViewById(R.id.maskapai);
        spinDestinasi = findViewById(R.id.destinasi);
        spinDewasa = findViewById(R.id.dewasa);


        ArrayAdapter<CharSequence> adapterMaskapai = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, maskapai);
        adapterMaskapai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMaskapai.setAdapter(adapterMaskapai);

        ArrayAdapter<CharSequence> adapterDestinasi = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, destinasi);
        adapterDestinasi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDestinasi.setAdapter(adapterDestinasi);

        ArrayAdapter<CharSequence> adapterDewasa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, dewasa);
        adapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDewasa.setAdapter(adapterDewasa);



        spinMaskapai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sMaskapai = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinDestinasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sDestinasi = parent.getItemAtPosition(position).toString();
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
                if (sMaskapai != null && sDestinasi != null && sTanggal != null && sDewasa != null) {
                    AlertDialog dialog = new AlertDialog.Builder(Pesawat_page.this)
                            .setTitle("Ingin pesan sekarang?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        db.execSQL("INSERT INTO TB_BOOK (asal, tujuan, tanggal, dewasa) VALUES ('" +
                                                sMaskapai + "','" +
                                                sDestinasi + "','" +
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
                                        Toast.makeText(Pesawat_page.this, "Pesanan berhasil", Toast.LENGTH_LONG).show();
                                        finish();
                                    } catch (Exception e) {
                                        Toast.makeText(Pesawat_page.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .create();
                    dialog.show();
                } else {
                    Toast.makeText(Pesawat_page.this, "Mohon Isi Pemesanan", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbPswt);
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
        if (sMaskapai.equalsIgnoreCase("Avansa") && sDestinasi.equalsIgnoreCase("Jakarta")) {
            hargaDewasa = 700000;

        } else if (sMaskapai.equalsIgnoreCase("Avansa") && sDestinasi.equalsIgnoreCase("Bali")) {
            hargaDewasa = 800000;

        } else if (sMaskapai.equalsIgnoreCase("Avansa") && sDestinasi.equalsIgnoreCase("Surabaya")) {
            hargaDewasa = 650000;

        } else if (sMaskapai.equalsIgnoreCase("Avansa") && sDestinasi.equalsIgnoreCase("Sidoarjo")) {
            hargaDewasa = 1200000;

        } else if (sMaskapai.equalsIgnoreCase("Avansa") && sDestinasi.equalsIgnoreCase("Blitar")) {
            hargaDewasa = 1500000;

        } else if (sMaskapai.equalsIgnoreCase("APV") && sDestinasi.equalsIgnoreCase("Jakarta")) {
            hargaDewasa = 600000;

        } else if (sMaskapai.equalsIgnoreCase("APV") && sDestinasi.equalsIgnoreCase("Bali")) {
            hargaDewasa = 760000;

        } else if (sMaskapai.equalsIgnoreCase("APV") && sDestinasi.equalsIgnoreCase("Surabaya")) {
            hargaDewasa = 864000;

        } else if (sMaskapai.equalsIgnoreCase("APV") && sDestinasi.equalsIgnoreCase("Sidoarjo")) {
            hargaDewasa = 958000;

        } else if (sMaskapai.equalsIgnoreCase("APV") && sDestinasi.equalsIgnoreCase("Blitar")) {
            hargaDewasa = 1125000;

        } else if (sMaskapai.equalsIgnoreCase("Hiace") && sDestinasi.equalsIgnoreCase("Jakarta")) {
            hargaDewasa = 750000;

        } else if (sMaskapai.equalsIgnoreCase("Hiace") && sDestinasi.equalsIgnoreCase("Bali")) {
            hargaDewasa = 820000;

        } else if (sMaskapai.equalsIgnoreCase("Hiace") && sDestinasi.equalsIgnoreCase("Surabaya")) {
            hargaDewasa = 780000;

        } else if (sMaskapai.equalsIgnoreCase("Hiace") && sDestinasi.equalsIgnoreCase("Sidoarjo")) {
            hargaDewasa = 970000;

        } else if (sMaskapai.equalsIgnoreCase("Hiace") && sDestinasi.equalsIgnoreCase("Blitar")) {
            hargaDewasa = 1080000;

        } else if (sMaskapai.equalsIgnoreCase("Xenia") && sDestinasi.equalsIgnoreCase("Jakarta")) {
            hargaDewasa = 730000;

        } else if (sMaskapai.equalsIgnoreCase("Xenia") && sDestinasi.equalsIgnoreCase("Bali")) {
            hargaDewasa = 835000;

        } else if (sMaskapai.equalsIgnoreCase("Xenia") && sDestinasi.equalsIgnoreCase("Surabaya")) {
            hargaDewasa = 770000;

        } else if (sMaskapai.equalsIgnoreCase("Xenia") && sDestinasi.equalsIgnoreCase("Sidoarjo")) {
            hargaDewasa = 939000;

        } else if (sMaskapai.equalsIgnoreCase("Xenia") && sDestinasi.equalsIgnoreCase("Blitar")) {
            hargaDewasa = 1210000;

        } else if (sMaskapai.equalsIgnoreCase("L300") && sDestinasi.equalsIgnoreCase("Jakarta")) {
            hargaDewasa = 810000;

        } else if (sMaskapai.equalsIgnoreCase("L300") && sDestinasi.equalsIgnoreCase("Bali")) {
            hargaDewasa = 860000;

        } else if (sMaskapai.equalsIgnoreCase("L300") && sDestinasi.equalsIgnoreCase("Surabaya")) {
            hargaDewasa = 844000;

        } else if (sMaskapai.equalsIgnoreCase("L300") && sDestinasi.equalsIgnoreCase("Sidoarjo")) {
            hargaDewasa = 1360000;

        } else if (sMaskapai.equalsIgnoreCase("L300") && sDestinasi.equalsIgnoreCase("Blitar")) {
            hargaDewasa = 1420000;

        }

        jmlDewasa = Integer.parseInt(sDewasa);


        hargaTotalDewasa = jmlDewasa * hargaDewasa;

        hargaTotal = hargaTotalDewasa ;
    }


}
