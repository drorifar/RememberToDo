package il.ac.shenkar.remember_to_do;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TaskListBaseAdapter extends BaseAdapter {

    private Context context;
    private TaskDAO dao;
    private LayoutInflater l_Inflater;


    public TaskListBaseAdapter(android.content.Context context) {
        this.context = context;
        this.dao = TaskDAO.getInstance(context, false);
        this.l_Inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.task_details_view, null);
            holder = new ViewHolder();
            holder.txt_itemName = (TextView) convertView.findViewById(R.id.title);
            holder.txt_time = (TextView) convertView.findViewById(R.id.time);
            holder.txt_location = (TextView) convertView.findViewById(R.id.location);
            holder.img_priority = (ImageView) convertView.findViewById(R.id.exclamation_mark);
            holder.img_notes = (ImageView) convertView.findViewById(R.id.notes);
            holder.img_clock = (ImageView) convertView.findViewById(R.id.alarm_clock);
            holder.img_map = (ImageView) convertView.findViewById(R.id.map);
            holder.bt_done = (ImageView) convertView.findViewById(R.id.done_button);

           // holder.bt_done.setOnClickListener(doneButtonOnClickListener);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setAttachments(holder,position);

        return convertView;
    }

    private void setAttachments(ViewHolder holder, int position)
    {

        holder.txt_itemName.setText(getItem(position).getTitle());

        String date = getItem(position).getDate();
        String location = getItem(position).getLocation();
        String notes = getItem(position).getNotes();

        if (notes == null || notes.isEmpty()) {
            holder.img_notes.setVisibility(View.INVISIBLE);
        }
        else  {
            holder.img_notes.setVisibility(View.VISIBLE);
        }

        if (date == null || date.isEmpty())  {
            holder.txt_time.setVisibility(View.INVISIBLE);
            holder.img_clock.setVisibility(View.INVISIBLE);
        }
        else {
            holder.txt_time.setVisibility(View.VISIBLE);
            holder.img_clock.setVisibility(View.VISIBLE);
            holder.txt_time.setText(date);
        }

        if (location == null || location.isEmpty()) {
            holder.txt_location.setVisibility(View.INVISIBLE);
            holder.img_map.setVisibility(View.INVISIBLE);
        }
        else {
            holder.txt_location.setVisibility(View.VISIBLE);
            holder.img_map.setVisibility(View.VISIBLE);
            holder.txt_location.setText(location);
        }
        if (!getItem(position).isPriority()) {
            holder.img_priority.setVisibility(View.INVISIBLE);
        }
        else holder.img_priority.setVisibility(View.VISIBLE);

        holder.bt_done.setTag(position);
    }

    @Override
    public int getCount() {
        return dao.getCount();
    }

    @Override
    public boolean isEmpty(){
        return dao.isEmpty();
    }

    @Override
    public Task getItem(int position) {
        return dao.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    private final View.OnClickListener doneButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            dao.deleteTask(getItem(position));
            notifyDataSetChanged();
        }
    };

    static class ViewHolder {
        TextView txt_itemName;
        TextView txt_location;
        TextView txt_time;
        ImageView img_clock;
        ImageView img_priority;
        ImageView img_map;
        ImageView img_notes;
        ImageView bt_done;
    }
}
