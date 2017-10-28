package edu.upc.eseiaat.pma.shoppinglist3;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import edu.upc.eseiaat.pma.shoppinglist3.R;
import edu.upc.eseiaat.pma.shoppinglist3.ShoppingListAdapter;

public class ShoppingListActivity extends AppCompatActivity {
    //Lo que necesita una lista
    private ArrayList<ShoppingItem> itemList;
    private ShoppingListAdapter adapter;

    private static final String FILENAME = "shopping_list.txt";
    //Tamaño de la tabla
    private static final int MAX_BYTES = 8000;


    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    private void writeItemList(){

        //Formato del fichero
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            //Tengo que pasar por todos los items
            for (int i=0; i<itemList.size();i++) {
                ShoppingItem it = itemList.get(i);
                //Línia: String;Boolean + salto de línia == %s;%b\n (Hay un item por linea)
                String line = String.format("%s;%b\n", it.getText(), it.isChecked());
                //Como guardar el fichero
                fos.write(line.getBytes());
            }
            //Cerramos el fichero
            fos.close();

            //Los catch me cogen los errores generados en el try
            //Con los toast informo al usuario de que algo ha ido mal
        } catch (FileNotFoundException e) {
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_LONG).show();
        }
    }

    private void readItemList (){
        itemList = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(FILENAME);
            //Crear la tabla para guardar los ficheros
            byte[] buffer = new byte[MAX_BYTES];
            int nread = fis.read(buffer);
            //Confirmar que el fichero no esta vacío para leerlo, sino se satura el programa
            if (nread > 0) {
                //Passar el buffer a un string
                String content = new String (buffer, 0, nread);
                //Extraer las lineas. Cortar el codigo en cada "\n"=espacio
                String[] lines = content.split("\n");
                //Divido cada linea en cada ; passa por cada linea
                for (String line : lines) {
                    String[] parts = line.split(";");
                    itemList.add(new ShoppingItem(parts[0], parts[1].equals("true")));
                }
            }
            fis.close();
        } catch (FileNotFoundException e) {
            //EXCEPCIÓN: cuando va buscar el fichero no existe (seguro que pasa cuando se enciende la app x primera vez)
        } catch (IOException e) {
            Toast.makeText(this, R.string.cannot_read, Toast.LENGTH_LONG).show();
        }

    }
//Cuando la app se para, podemos utilizar onStop para grabar lo que había dentro
    @Override
    protected void onStop() {
        super.onStop();
        writeItemList();
    }

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
      readItemList();

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

    //Método para crear el menú y sus opciones
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    //Gestionar los clicks del menú
    public boolean onOptionsItemSelected(MenuItem item) {
        //Gestionar la selección de un item
        switch (item.getItemId()) {
            case R.id.clear_checked:
                clearChecked();
                return true;
            //"En el caso que clicken clear all, llama al método clearAll"
            case R.id.clear_all:
                clearAll();
                //Cuando gestionas la opción de menú siempre tienes que devolver 'true'
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {
        //Confirmación de que quiere borrarlo all con un cuadro de diálago
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_clear_all);
        builder.setPositiveButton(R.string.clear_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    //CREAR METODO: borrar todos los items que están marcados
    private void clearChecked() {
        //Utilizamos un while en vez de un for, porque al ir eliminando items los indices 'i' van variando y no se acaban de 'mirar' todos los items
        //Sólo augmentamos el indice 'i' si no se ha eliminado el item anterior
        int i = 0;
        while (i < itemList.size()) {
            if (itemList.get(i).isChecked()) {
                itemList.remove(i);
            } else {
                i++;
            }
        }
        adapter.notifyDataSetChanged();
    }

}
