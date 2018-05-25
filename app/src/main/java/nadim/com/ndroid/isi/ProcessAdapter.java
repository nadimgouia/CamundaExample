package nadim.com.ndroid.isi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Nadim on 04/05/2018.
 */

public class ProcessAdapter extends ArrayAdapter<ProcessModel> {


    Context context;

    public ProcessAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProcessModel> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ProcessModel processModel = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item_process, parent, false);

        TextView textProcess = convertView.findViewById(R.id.textProcess);
        textProcess.setText(processModel.getName());

        return convertView;
    }
}
