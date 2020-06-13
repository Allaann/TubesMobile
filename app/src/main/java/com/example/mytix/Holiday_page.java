package com.example.mytix;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytix.database.DataHelper;
import com.example.mytix.session.SessionManager;

import java.util.Calendar;
import java.util.HashMap;

public class Holiday_page extends AppCompatActivity {
    protected Cursor cursor;
    DataHelper dbHelper;
    SessionManager session;

    SQLiteDatabase db;
    Spinner spinTempat, spinDewasa;

    String email;
    int id_book;
    public String sTempat, sTanggal, sDewasa, asal="liburan";
    int jmlDewasa;
    int hargaDewasa;
    int hargaTotalDewasa, hargaTotal;

    private DatePickerDialog dpTanggal;
    Calendar newCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_page);

        dbHelper = new DataHelper(Holiday_page.this);
        db = dbHelper.getReadableDatabase();

        final String[] tempat = {"Baju", "Celana", "Aksesoris", "Topi", "Makanan", "Minuman"};
        final String[] dewasa = {"0", "1", "2", "3", "4", "5", "6", "7"};


        spinTempat = findViewById(R.id.tmptlibur);
        spinDewasa = findViewById(R.id.dewasa);


        ArrayAdapter<CharSequence> adapterTempat = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, tempat);
        adapterTempat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTempat.setAdapter(adapterTempat);

        ArrayAdapter<CharSequence> adapterDewasa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, dewasa);
        adapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDewasa.setAdapter(adapterDewasa);




        spinTempat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sTempat = parent.getItemAtPosition(position).toString();
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
                if (sTempat != null && sTanggal != null && sDewasa != null) {
                    AlertDialog dialog = new AlertDialog.Builder(Holiday_page.this)
                            .setTitle("Ingin beli sekarang?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        db.execSQL("INSERT INTO TB_BOOK (asal, tujuan, tanggal, dewasa) VALUES ('" +
                                                asal+"','"+
                                                sTempat + "','" +
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
                                        Toast.makeText(Holiday_page.this, "Pemesanan berhasil", Toast.LENGTH_LONG).show();
                                        finish();
                                    } catch (Exception e) {
                                        Toast.makeText(Holiday_page.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("Tidak", null)
                            .create();
                    dialog.show();
                }
                else {
                    Toast.makeText(Holiday_page.this, "Mohon lengkapi data pemesanan barang anda!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.liburan);
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
        if (sTempat.equalsIgnoreCase("Baju")) {
            hargaDewasa = 455000;

        } else if (sTempat.equalsIgnoreCase("Celana")) {
            hargaDewasa = 85000;

        } else if (sTempat.equalsIgnoreCase("Aksesoris")) {
            hargaDewasa = 150000;

        } else if (sTempat.equalsIgnoreCase("Topi")) {
            hargaDewasa = 180000;

        } else if (sTempat.equalsIgnoreCase("Makanan")) {
            hargaDewasa = 100000;

        } else if (sTempat.equalsIgnoreCase("Minuman")) {
            hargaDewasa = 120000;

        }

        jmlDewasa = Integer.parseInt(sDewasa);


        hargaTotalDewasa = jmlDewasa * hargaDewasa;

        hargaTotal = hargaTotalDewasa;
    }

}