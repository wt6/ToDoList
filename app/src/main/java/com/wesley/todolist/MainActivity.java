package com.wesley.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    final List<String> list = new ArrayList<>();
    final int[] backgroundColors = {Color.LTGRAY, Color.WHITE, Color.YELLOW};
    final int highlightColor = 0x7d0000cc;
    //final int highlightColor = ContextCompat.getColor(getApplicationContext(),
    // R.color.highlightColor);
    final List<Integer> highlightedTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final TextAdapter adapter = new TextAdapter();

        readInfo();

        adapter.setData(list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                int highlightIndex = highlightedTasks.indexOf(position);
                if (highlightIndex != -1){
                    highlightedTasks.remove(highlightIndex);
                    adapter.setData(list);
                }
                else {
                    highlightedTasks.add(position);
                    view.findViewById(R.id.task).setBackgroundColor(highlightColor);
                }
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

        final Button deleteTasksButton = findViewById(R.id.deleteTasksButton);
        deleteTasksButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(highlightedTasks.size() > 0) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                            MainActivity.this);
                    dialogBuilder.setTitle("Confirm Delete");
                    dialogBuilder.setMessage(
                            "Are you sure you want to delete the selected task(s)?");
                    dialogBuilder.setPositiveButton("Delete Selected", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListIterator<String> iterator = list.listIterator();
                            int count = 0;
                            while (iterator.hasNext()) {
                                iterator.next();
                                if (highlightedTasks.contains(count)) {
                                    Log.d("Task Deletion",
                                            String.format("Index of task to be removed = %d",
                                                    count));
                                    iterator.remove();
                                }
                                count++;
                            }
                            adapter.setData(list);
                            saveInfo();
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Please select tasks to be deleted.", Toast.LENGTH_LONG).show();
                }
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
        int[] assignedColors;
        List<String> list = new ArrayList<>();

        TextAdapter(){
            super();
        }

        void setData(List<String> mList){
            list.clear();
            highlightedTasks.clear();
            list.addAll(mList);

            // Create array of set colour for each item in task list
            int colorsCount = backgroundColors.length;
            int listLen = list.size();
            assignedColors = new int[listLen];
            for(int i=0; i<listLen; i++) {
                assignedColors[i] = backgroundColors[i % colorsCount];
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
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                try{
                    convertView = inflater.inflate(R.layout.item, parent, false);
                }catch(InflateException ex){
                    Log.e("MainAct_TextInflation", ex.toString());
                }
            }

            final TextView textView = convertView.findViewById(R.id.task);

            textView.setText(list.get(position));
            textView.setTextColor(Color.BLACK);

            // Setting background colour
            if (highlightedTasks.contains(position)){
                textView.setBackgroundColor(highlightColor);
            }
            else {
                textView.setBackgroundColor(assignedColors[position]);
            }

            return convertView;
        }
    }
}
