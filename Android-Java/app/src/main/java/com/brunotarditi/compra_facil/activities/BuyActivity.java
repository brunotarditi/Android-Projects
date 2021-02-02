package com.brunotarditi.compra_facil.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.brunotarditi.compra_facil.R;
import com.brunotarditi.compra_facil.adapter.BuyAdapter;
import com.brunotarditi.compra_facil.models.Buy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class BuyActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Buy>>, AdapterView.OnItemClickListener {

    private FloatingActionButton fab;
    private Realm realm;
    private ListView listView;
    private BuyAdapter adapter;
    private RealmResults<Buy> compras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo);

        //DB Realm
        realm = Realm.getDefaultInstance();
        compras = realm.where(Buy.class).findAll();
        compras.addChangeListener(this);

        adapter = new BuyAdapter(this, compras, R.layout.list_view_buy_item);
        fab = findViewById(R.id.fabAddBuy);
        listView = findViewById(R.id.listViewBuy);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingBoard("Añadir nueva lista de compra", "Escribe el nombre para la lista");
            }
        });
        registerForContextMenu(listView);
    }

    //CRUD Actions
    private void createNewBoard(String boardName) {
        realm.beginTransaction();
        Buy buy = new Buy(boardName);
        realm.copyToRealm(buy);
        realm.commitTransaction();
    }

    private void deleteBoard(Buy buy){
        realm.beginTransaction();
        buy.deleteFromRealm();
        realm.commitTransaction();
    }

    private void editBoard(String newName, Buy buy) {
        realm.beginTransaction();
        buy.setTitle(newName);
        realm.copyToRealmOrUpdate(buy);
        realm.commitTransaction();
    }

    private void deleteAll(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    //Dialogs
    private void showAlertForCreatingBoard(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_buy, null);
        builder.setView(viewInflated);

        final EditText input = viewInflated.findViewById(R.id.editTextNewBuy);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if (boardName.length() > 0) createNewBoard(boardName);
                else
                    Toast.makeText(getApplicationContext(), "El nombre es requerido", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Dialogs
    private void showAlertForEditingBoard(String title, String message, final Buy buy) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_buy, null);
        builder.setView(viewInflated);

        final EditText input = viewInflated.findViewById(R.id.editTextNewBuy);
        input.setText(buy.getTitle());
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if (boardName.length() == 0)
                    Toast.makeText(getApplicationContext(), "El nombre es requerido para editar la nueva lista de compra", Toast.LENGTH_LONG).show();
                else if (boardName.equals(buy.getTitle()))
                    Toast.makeText(getApplicationContext(), "El nombre es igual al anterior", Toast.LENGTH_SHORT).show();
                else
                    editBoard(boardName, buy);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //Event
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buy_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAll:
                deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(compras.get(info.position).getTitle());
        getMenuInflater().inflate(R.menu.context_menu_buy_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_buy:
                deleteBoard(compras.get(info.position));
                return true;
            case R.id.edit_buy:
                showAlertForEditingBoard("Editar lista", "El nombre de la lista fue modificado", compras.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onChange(RealmResults<Buy> compras) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(BuyActivity.this, ProductActivity.class);
        intent.putExtra("id", compras.get(position).getId());
        startActivity(intent);
    }
}