package co.calendar.calendartrack;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class CalendarActivity extends Activity implements OnClickListener {
    //text for selected date and month
	private TextView selectedMonth;

    //buttons for choosing months
	private ImageView goBack;
	private ImageView goForward;

    //calendar
	private GridView calendarGrid;
	private GridCellAdapter adapter;

    //calendar class
	private Calendar cal;
    DateFormat monthYear = new SimpleDateFormat("MMMM yyyy", Locale.US);

    DatabaseHandler dbHandler;

    int nowDay, nowMonth, nowYear;

    private final String TAG = "note of worthiness";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

        dbHandler = new DatabaseHandler(getApplicationContext());

        //get current day
		cal = Calendar.getInstance(Locale.getDefault());
        nowDay = cal.get(Calendar.DATE);
        nowMonth = cal.get(Calendar.MONTH);
        nowYear = cal.get(Calendar.YEAR);

        //selected month
        selectedMonth = (TextView) findViewById(R.id.currentMonth);
        selectedMonth.setText(monthYear.format(cal.getTime()));

        //buttons to go to prev/next month
		goBack = (ImageView) findViewById(R.id.prevMonth);
		goBack.setOnClickListener(this);
		goForward = (ImageView) this.findViewById(R.id.nextMonth);
		goForward.setOnClickListener(this);

        //calendar grid
		calendarGrid = (GridView) this.findViewById(R.id.calendar);

	}

    @Override
    public void onResume() {
        super.onResume();
        createCalendar();
    }

    //for prev/next month buttons
    @Override
    public void onClick(View v) {
        if (v == goBack) {
            cal.add(Calendar.MONTH, -1);
        }
        if (v == goForward) {
            cal.add(Calendar.MONTH, +1);
        }
        //change text
        selectedMonth.setText(monthYear.format(cal.getTime()));
        //create new calendar
        createCalendar();
    }

    //creates calendar view according to month
	private void createCalendar() {
        //add new days
        adapter = new GridCellAdapter(getApplicationContext());
		adapter.notifyDataSetChanged();
		calendarGrid.setAdapter(adapter);
	}

	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
        private Context c;
        private List<Day> daysList = new ArrayList<>();
        private Button gridcell;
        DateFormat month = new SimpleDateFormat("MMMM", Locale.US);
        DateFormat year = new SimpleDateFormat("yyyy", Locale.US);

        // Days in Current Month
        public GridCellAdapter(Context context) {
            super();
            c = context;

            //creates days of month on grid
            //number of days in selected month
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            //current month/year
            String currentMonth = month.format(cal.getTime());
            String currentYear = year.format(cal.getTime());

            //previous month/year/# of days
            cal.add(Calendar.MONTH, -1);
            String prevMonth = month.format(cal.getTime());
            String prevYear = year.format(cal.getTime());
            int daysInPrevMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            //next month/year
            cal.add(Calendar.MONTH, +2);
            String nextMonth = month.format(cal.getTime());
            String nextYear = year.format(cal.getTime());

            //reset to current
            cal.add(Calendar.MONTH, -1);

            // Compute how much to leave before before the first day of the month.
            cal.set(Calendar.DATE,1);
            int trailingSpaces = cal.get(Calendar.DAY_OF_WEEK)-1;
            cal.set(Calendar.DATE, nowDay);

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                daysList.add(new Day(String.valueOf((daysInPrevMonth - trailingSpaces) + i + 1), prevMonth, prevYear, "none", "", "PN", ""));
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                if ((i == nowDay) && (cal.get(Calendar.MONTH) == nowMonth) && (cal.get(Calendar.YEAR) == nowYear)) {
                    daysList.add(new Day(String.valueOf(i), currentMonth, currentYear, "none", "", "DAY", ""));
                } else {
                    daysList.add(new Day(String.valueOf(i), currentMonth, currentYear, "none", "", "CUR", ""));
                }
            }

            // Leading Month days
            for (int i = 0; i < daysList.size() % 7; i++) {
                daysList.add(new Day(String.valueOf(i+1), nextMonth, nextYear, "none", "", "PN", ""));
            }

            //get info from sqlite database and update days accordingly
            List<Day> changedDays = dbHandler.getAllDays(month.format(cal.getTime()), year.format(cal.getTime()));

            for (Day d : changedDays) {
                for (Day dd : daysList) {
                    if (d.same(dd)) {
                        dd.setColor(d.getColor());
                        dd.setList(d.getList());
                        dd.setCalNum(d.getCalNum());
                    }
                }
            }
        }

        //create graphics for day
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.single_day_layout, parent, false);
            }

            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            gridcell.setOnClickListener(this);

            Day day = daysList.get(position);

            gridcell.setText(day.getDay());
            gridcell.setTag(day);

            switch (day.getColor()) {
                case "good": gridcell.setTextColor(getResources().getColor(R.color.green)); break;
                case "ok": gridcell.setTextColor(getResources().getColor(R.color.orange)); break;
                case "eh": gridcell.setTextColor(getResources().getColor(R.color.yellow)); break;
                case "bad": gridcell.setTextColor(getResources().getColor(R.color.red)); break;
                default: break;
            }

            switch (day.getType()) {
                case "PN": gridcell.setBackgroundResource(R.drawable.button_not_curr); break;
                case "CUR": gridcell.setBackgroundResource(R.drawable.button_current); break;
                case "DAY": gridcell.setBackgroundResource(R.drawable.button_curr_day); break;
                default: break;
            }

            return row;
        }

        //when day is clicked, go to edit screen
        @Override
        public void onClick(View view) {
            Day day = (Day) view.getTag();

            Intent i = new Intent(getApplicationContext(), EditActivity.class);
            i.putExtra("day", day);
            startActivity(i);

        }

        @Override
        public int getCount() { return daysList.size(); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public Day getItem(int position) { return daysList.get(position); }
    }
}
