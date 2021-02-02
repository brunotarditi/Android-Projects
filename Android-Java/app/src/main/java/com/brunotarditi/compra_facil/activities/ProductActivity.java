package com.brunotarditi.compra_facil.activities;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.brunotarditi.compra_facil.R;
import com.brunotarditi.compra_facil.adapter.ProductAdapter;
import com.brunotarditi.compra_facil.models.Buy;
import com.brunotarditi.compra_facil.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class ProductActivity extends AppCompatActivity implements RealmChangeListener<Buy> {

    private ListView listView;
    private FloatingActionButton fab;

    private ProductAdapter adapter;
    private RealmList<Product> products;
    private Realm realm;

    private int productId;
    private Buy buy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo);

        realm = Realm.getDefaultInstance();

        if (getIntent().getExtras() != null)
            productId = getIntent().getExtras().getInt("id");

        buy = realm.where(Buy.class).equalTo("id", productId).findFirst();
        buy.addChangeListener(this);
        products = buy.getProducts();

        this.setTitle(buy.getTitle());

        fab = (FloatingActionButton) findViewById(R.id.fabAddProduct);
        listView = (ListView) findViewById(R.id.listViewProduct);
        adapter = new ProductAdapter(this, products, R.layout.list_view_product_item);

        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForCreatingNote("Agregar nuevo producto", "Escribe un producto para " + buy.getTitle() + ".");
            }
        });


        registerForContextMenu(listView);
    }


    //** CRUD Actions **/

    private void createNewProduct(String productoName, int productQuantity, double productPrice) {
        realm.beginTransaction();
        Product _product = new Product(productoName, productQuantity, productPrice);
        realm.copyToRealm(_product);
        buy.getProducts().add(_product);
        realm.commitTransaction();
    }

    private void editNote(String newProductDescription, int newProductQuantity, double newProductPrice, Product product) {
        realm.beginTransaction();
        product.setName(newProductDescription);
        product.setQuantity(newProductQuantity);
        product.setPrice(newProductPrice);
        realm.copyToRealmOrUpdate(product);
        realm.commitTransaction();
    }

    private void deleteNote(Product product) {
        realm.beginTransaction();
        product.deleteFromRealm();
        realm.commitTransaction();
    }

    private void deleteAll() {
        realm.beginTransaction();
        buy.getProducts().deleteAllFromRealm();
        realm.commitTransaction();
    }


    //** Dialogs **/

    private void showAlertForCreatingNote(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_product, null);
        builder.setView(viewInflated);

        final EditText nameInput = (EditText) viewInflated.findViewById(R.id.editTextNewName);
        final EditText quantityInput = viewInflated.findViewById(R.id.editTextNewQuantity);
        final EditText priceInput = viewInflated.findViewById(R.id.editTextNewPrice);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = nameInput.getText().toString().trim();
                String quantity = quantityInput.getText().toString().trim();
                String price = priceInput.getText().toString().trim();
                if (name.length() > 0 && quantity.length() > 0 && price.length() > 0)
                    createNewProduct(name, Integer.parseInt(quantity), Double.parseDouble(price));
                else
                    Toast.makeText(getApplicationContext(), "El producto debe tener todos los campos", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForEditingNote(String title, String message, final Product product) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_product, null);
        builder.setView(viewInflated);

        EditText nameInput = (EditText) viewInflated.findViewById(R.id.editTextNewName);
        EditText quantityInput = viewInflated.findViewById(R.id.editTextNewQuantity);
        EditText priceInput = viewInflated.findViewById(R.id.editTextNewPrice);
        nameInput.setText(product.getName());
        quantityInput.setText(String.valueOf(product.getQuantity()));
        priceInput.setText(String.valueOf(product.getPrice()));


        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = nameInput.getText().toString().trim();
                String quantity = quantityInput.getText().toString().trim();
                String price = priceInput.getText().toString().trim();
                if (name.length() == 0 || quantity.length() == 0 || price.length() == 0)
                    Toast.makeText(getApplicationContext(), "Debe completar todos los campos", Toast.LENGTH_LONG).show();
                else
                    editNote(name, Integer.parseInt(quantity), Double.parseDouble(price), product);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /* Events*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.deleteAllNotes:
                deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_product_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.delete_product:
                deleteNote(products.get(info.position));
                return true;
            case R.id.edit_product:
                showAlertForEditingNote("Editar", "Modificar nota", products.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onChange(Buy element) {
        adapter.notifyDataSetChanged();
    }
}