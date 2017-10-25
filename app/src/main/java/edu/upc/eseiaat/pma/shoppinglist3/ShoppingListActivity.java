package edu.upc.eseiaat.pma.shoppinglist3;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.upc.eseiaat.pma.shoppinglist3.R;
import edu.upc.eseiaat.pma.shoppinglist3.ShoppingListAdapter;

public class ShoppingListActivity extends AppCompatActivity {
    //Lo que necesita una lista
    private ArrayList<ShoppingItem> itemList;
    private ShoppingListAdapter adapter;


    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        //CREAR CAMPOS: no las quiero como variables locales
        //ANTES: ListView list = (ListView) findViewById(R.id.list)
        //DESPUÉS: list = (ListView) findViewById(R.id.list);

        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        edit_item = (EditText) findViewById(R.id.edit_item);

        //CONSTRUIR OBJETO
        itemList = new ArrayList<>();
        itemList.add(new ShoppingItem("Patatas"));
        itemList.add(new ShoppingItem ("Zanahorias"));
        itemList.add(new ShoppingItem ("Papel WC"));
        itemList.add(new ShoppingItem ("Copas Danone"));

        //CREAR ADAPTADOR
        //this = puntero a la actividad actual, hace referencia a ella
        //adapter = crea los views y adapta el texto segun los datos de la lista
        adapter = new ShoppingListAdapter(
                this,
                R.layout.shopping_item,
                itemList);

        //LISTENER DE CUANDO CLICAN EL BOTÓN
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        edit_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                addItem();
                return true;
            }
        });

        //Se necesita ponerlo siempre en una lista, lo añadimos al inicio del proyecto
        list.setAdapter(adapter);

        //ENTERARNOS CUANDO CLICAN UN ELEMENTO
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //Cambiamos los datos
                itemList.get(pos).toggleChecked();
                //Notificamos al adaptador que hemos cambiado los datos
                adapter.notifyDataSetChanged();



            }
        });

        //ELIMINAR ITEMS
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> list, View item, int pos, long id) {
                maybeRemoveItem(pos);
                return true;
            }
        });
    }

    private void maybeRemoveItem(final int pos) {
        //Cuadro de dialogo para confirmar que se quiere eliminar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String fmt = getResources().getString(R.string.confirm_message);
        builder.setMessage(String.format(fmt, itemList.get(pos).getText()));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemList.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();


    }

    private void addItem() {
        String item_text= edit_item.getText().toString();
        //Evitar cuadros en blanco. Añadir solo si hay algo escrito.
        if (!item_text.isEmpty()){
            itemList.add(new ShoppingItem(item_text));
            adapter.notifyDataSetChanged();
            //Borrar la caja de texto cuando ya he añadido el item
            edit_item.setText("");
        }
        //Cuando añado un item, que la lista se mueva sola y baje esta la nueva posición para ver como lo añado
        //itemList.size()- 1= posición última de la lista
        //Le estoy diciendo a la lista que se mueva hasta ahí (última posición)
        list.smoothScrollToPosition(itemList.size()-1);
    }
}
