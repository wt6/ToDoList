package com.wesley.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final List<String> list = new ArrayList<>();
    int[] backgroundColors = {Color.LTGRAY, Color.WHITE, Color.YELLOW};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final TextAdapter adapter = new TextAdapter(backgroundColors);

        readInfo();

        adapter.setData(list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete task?")
                        .setMessage("Delete task: " + list.get(position))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
            }
        });

        final Button newTaskButton = findViewById(R.id.newTaskButton);
        newTaskButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final EditText taskInput = new EditText(MainActivity.this);
                taskInput.setSingleLine();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add a new task")
                        .setMessage("Enter task name")
                        .setView(taskInput)
                        .setPositiveButton("Add task", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.add(taskInput.getText().toString());
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        final Button deleteAllTasksButton = findViewById(R.id.deleteAllTasksButton);
        deleteAllTasksButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setTitle("Confirm Delete");
                dialogBuilder.setMessage("Are you sure you want to delete all tasks?");
                dialogBuilder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.clear();
                        adapter.setData(list);
                        saveInfo();
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", null);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
            });

    }

    private void saveInfo(){
        try{
            File file = new File(this.getFilesDir(), "saved");

            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            for(int i = 0; i<list.size(); i++) {
                bw.write(list.get(i));
                bw.newLine();
            }

            bw.close();
            fOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readInfo(){
        File file = new File(this.getFilesDir(), "saved");
        if(!file.exists()){
            return;
        }

        try{
            FileInputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            while(line != null) {
                list.add(line);
                line = br.readLine();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    class TextAdapter extends BaseAdapter{
        int[] colorsList;
        List<String> list = new ArrayList();
        int[] backgroundColors;

        TextAdapter(int[] mBackgroundColors){
            super();
            this.colorsList = mBackgroundColors;
        }

        void setData(List<String> mList){
            list.clear();
            list.addAll(mList);

            // Create array of set colour for each item in task list
            int colorsLen = this.colorsList.length;
            int listLen = list.size();
            backgroundColors = new int[listLen];
            for(int i=0; i<listLen; i++) {
                backgroundColors[i] = this.colorsList[i % colorsLen];
            }

            notifyDataSetChanged();
        }

        @Override
        public int getCount(){
            return list.size();
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)
                        MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item, parent, false);
            }

            final TextView textView = convertView.findViewById(R.id.task);

            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(backgroundColors[position]);
            textView.setText(list.get(position));
            return convertView;
        }
    }
}
