package co.calendar.calendartrack;

import android.os.Parcel;
import android.os.Parcelable;

public class Day implements Parcelable {

    private String day, month, year, color, list, type, calNum;

    public Day(String d, String m, String y, String c, String l, String n, String nu) {
        day = d;
        month = m;
        year = y;
        color = c;
        list = l;
        type = n; //DAY = today's date, CUR = current month, PN = surrounding months
        calNum = nu;
    }

    public boolean same(Day d) {
        return (getDay().equals(d.getDay())) && (getMonth().equals(d.getMonth())) && (getYear().equals(d.getYear()));
    }

    public String getDay() { return day; }
    public String getMonth() { return month; }
    public String getYear() { return year; }
    public String getColor() { return color; }
    public String getList() { return list; }
    public String getType() { return type; }
    public String getCalNum() { return calNum; }

    public void setColor(String c) {
        color = c;
    }

    public void setList(String l) {
        list = l;
    }

    public void setCalNum(String n) {
        calNum = n;
    }

    //parcel part
    public Day(Parcel in){
        String[] data= new String[7];

        in.readStringArray(data);
        this.day = data[0];
        this.month = data[1];
        this.year = data[2];
        this.color = data[3];
        this.list = data[4];
        this.type = data[5];
        this.calNum = data[6];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.day,
                this.month,
                this.year,
                this.color,
                this.list,
                this.type,
                this.calNum});
    }

    public static final Parcelable.Creator<Day> CREATOR= new Parcelable.Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }
        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

}
