package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import org.apache.commons.io.FileUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> listStr;
    Button buttonAdd;
    EditText editItem;
    RecyclerView listItem;
    ItemAdapter itemAdapter;
    public final static String KEY_ITEM_TEXT = "item_text";
    public final static String KEY_ITEM_POSITION = "item_position";
    public final static int EDIT_TEXT_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = findViewById(R.id.buttonAdd);
        editItem = findViewById(R.id.editItem);
        listItem = findViewById(R.id.listItem);

        loadItems();

        ItemAdapter.OnLongClickListener onLongClickListener = new ItemAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
               listStr.remove(position);
               itemAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item removed!", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemAdapter.OnClickListener onClickListener = new ItemAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "A single click at position");
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, listStr.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        itemAdapter = new ItemAdapter(listStr, onLongClickListener, onClickListener);
        listItem.setAdapter(itemAdapter);
        //uses a vertical layout
        listItem.setLayoutManager(new LinearLayoutManager(this));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Box was clicked");
                String listItem = editItem.getText().toString();
                listStr.add(listItem);
                itemAdapter.notifyItemInserted(listStr.size()-1);
                editItem.setText("");
                Toast.makeText(getApplicationContext(), "Item added!", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    //handles saving and loading the files
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    private void loadItems() {
        try{
        listStr = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        }
        catch (IOException e){
            Log.e("MainActivity", "Error reading items!", e);
            listStr = new ArrayList<>();
        }
    }

    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), listStr);
        } catch (IOException e) {
            Log.e("MainActivity", "Error saving items!", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String itemText = data.getStringExtra(KEY_ITEM_TEXT);
        int position = data.getExtras().getInt(KEY_ITEM_POSITION);
        listStr.set(position, itemText);
        itemAdapter.notifyItemChanged(position);
        saveItems();
        Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();
    }
}