package co.calendar.calendartrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class EditActivity extends Activity {

    String day, month, year, color, list, numColor, num;
    ArrayList<String> currentItems;
    ArrayList<String> currentNums;

    ArrayList<View> viewList = new ArrayList<>();

    ViewGroup inclusionViewGroup;

    int totalNum = 0;

    private final String TAG = "note of worthiness";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //get day info
        Intent in = getIntent();
        Day selectedDay = in.getParcelableExtra("day");
        day = selectedDay.getDay();
        month = selectedDay.getMonth();
        year = selectedDay.getYear();
        color = selectedDay.getColor();
        list = selectedDay.getList();
        numColor = selectedDay.getType();
        num = selectedDay.getCalNum();

        //get day list and calnums
        currentItems = new ArrayList<>(Arrays.asList(list.replace("[", "").replace("]", "").replace("\"","").split(", ")));
        currentNums = new ArrayList<>(Arrays.asList(num.replace("[", "").replace("]", "").replace("\"", "").split(", ")));

        //list view
        inclusionViewGroup = (ViewGroup)findViewById(R.id.things_list);

        //for all items in list
        for (String s : currentItems) {
            addItem(s, currentNums.get(currentItems.indexOf(s)));
        }

        //set date and calnum total
        TextView dateTitle = (TextView) findViewById(R.id.date_title);
        TextView totalTitle = (TextView) findViewById(R.id.calNum);
        dateTitle.setText(month + " " + day + ", " + year);
        totalTitle.setText(totalNum+"");

        //dropdown for choosing color
        final Spinner editColor = (Spinner)findViewById(R.id.editColor);
        String[] items = new String[]{"select one...", "good", "ok", "eh", "bad"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_item, items);
        editColor.setAdapter(adapter);

        //set color
        switch (color) {
            case "good": editColor.setSelection(1); break;
            case "ok": editColor.setSelection(2); break;
            case "eh": editColor.setSelection(3); break;
            case "bad": editColor.setSelection(4); break;
            default: break;
        }

        //add item
        final Button addItemBtn = (Button) findViewById(R.id.btnAdd);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem("","");
            }
        });

        //save and put info in database
        final Button saveBtn = (Button) findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newColor = editColor.getSelectedItem().toString();
                ArrayList<String> newItems = new ArrayList<>();
                ArrayList<String> newCals = new ArrayList<>();
                for (View v : viewList) {
                    EditText editList = (EditText) v.findViewById(R.id.editItem);
                    String item = editList.getText().toString();
                    if (item.equals("")) {
                        item = " ";
                    }
                    newItems.add(item);

                    EditText numList = (EditText) v.findViewById(R.id.editNum);
                    String num = numList.getText().toString();
                    if (num.equals("")) {
                        num = "0";
                    }
                    newCals.add(num);
                }
                Log.i(TAG, newItems.toString());
                Log.i(TAG, newCals.toString());
                DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());

                Day d = new Day(day, month, year, newColor, newItems.toString(), numColor, newCals.toString());
                if (dbHandler.nameExists(day, month, year)) {
                    dbHandler.updateDay(d);
                } else {
                    dbHandler.createDay(d);
                }

                finish();
            }
        });

        //delete day
        final Button deleteBtn = (Button) findViewById(R.id.btnErase);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());

                Day d = new Day(day, month, year, "", "", "", "");
                dbHandler.deleteDay(d);

                finish();
            }
        });
    }

    public void addItem(String s, String n) {
        //create edit item view and add it to list view
        final View child = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_item, inclusionViewGroup, false);
        inclusionViewGroup.addView(child);
        //add to list of edit items
        viewList.add(child);
        //delete item button
        final ImageView xBtn = (ImageView) child.findViewById(R.id.xBtn);
        xBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                child.setVisibility(View.GONE);
                viewList.remove(child);
            }
        });

        if (s.equals(" ")) {
            s = "";
        }
        if (n.equals("0")) {
            n = "";
        }

        //set text
        EditText editList = (EditText) child.findViewById(R.id.editItem);
        editList.setText(s, TextView.BufferType.EDITABLE);

        //set calnum
        EditText editNum = (EditText) child.findViewById(R.id.editNum);
        editNum.setText(n, TextView.BufferType.EDITABLE);
        if (n.equals("")) {
            n = "0";
        }
        //add to total
        totalNum += Integer.parseInt(n);
    }
}
