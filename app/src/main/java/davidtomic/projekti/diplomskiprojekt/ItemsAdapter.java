package davidtomic.projekti.diplomskiprojekt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemsAdapter extends BaseAdapter {
    private final ArrayList mData;

    public ItemsAdapter(HashMap<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public HashMap.Entry<String, String> getItem(int position) {
        return (HashMap.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_adapter_item, parent, false);
        } else {
            result = convertView;
        }
        HashMap.Entry<String, String> item = getItem(position);

        ((TextView) result.findViewById(R.id.item_name)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.item_cost)).setText(item.getValue() + " KM");

        return result;
    }
}