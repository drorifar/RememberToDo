package il.ac.shenkar.remember_to_do;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class TaskListBaseAdapter extends BaseAdapter {

    private Context context;
    private TaskDAO dao;
    private LayoutInflater l_Inflater;


    public TaskListBaseAdapter(android.content.Context context) {
        this.context = context;
        this.dao = TaskDAO.getInstance(context);
        this.l_Inflater = LayoutInflater.from(context);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.task_details_view, null);
            holder = new ViewHolder();
            holder.txt_itemName = (TextView) convertView.findViewById(R.id.title);
            holder.txt_time = (TextView) convertView.findViewById(R.id.time);
            holder.bt_done = (ImageView) convertView.findViewById(R.id.done_button);
//          holder.txt_itemDescription = (TextView) convertView.findViewById(R.id.description);

            holder.bt_done.setOnClickListener(doneButtonOnClickListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_itemName.setText(getItem(position).getTitle());

        String date = getItem(position).getDate();

        holder.txt_time.setText(date);
        holder.bt_done.setTag(position);

        return convertView;
    }

    static class ViewHolder {
        TextView txt_itemName;
        TextView txt_itemDescription;
        TextView txt_time;
        ImageView bt_done;
    }


}
