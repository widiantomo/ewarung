package id.web.kmis.e_warung.warung.master_child.penjualan;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.sensor.SDImageLoader;

public class ListViewAdapter_J extends BaseAdapter {
    private Context context;
    public ArrayList<Model_J> productList;
    //Activity context;

    public ListViewAdapter_J(Activity context, ArrayList<Model_J> productList) {
        super();
        //this.activity = activity;
        this.productList = productList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView mGambar;
        TextView mKode;
        TextView mNama;
        TextView mHjual;
        TextView mQty;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mobile, null);
            holder = new ViewHolder();

            holder.mGambar = (ImageView) convertView.findViewById(R.id.barangitem);
            holder.mKode = (TextView) convertView.findViewById(R.id.kodbar);
            holder.mNama = (TextView) convertView
                    .findViewById(R.id.barg);
            holder.mHjual = (TextView) convertView.findViewById(R.id.hargbar);
            holder.mQty = (TextView) convertView.findViewById(R.id.qtybar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model_J item = productList.get(position);

        holder.mKode.setText(item.getjKode());
        holder.mNama.setText(item.getjNama());
        holder.mHjual.setText(item.getjHjual());
        holder.mQty.setText(item.getjQty());

        if (item.getjGambar() == "simpokok") {
            int id = context.getResources().getIdentifier("simpokok", "drawable", context.getPackageName());
            holder.mGambar.setImageResource(id);
        } else if (item.getjGambar() == "simwajib3") {
            int id = context.getResources().getIdentifier("simwajib3", "drawable", context.getPackageName());
            holder.mGambar.setImageResource(id);
        } else if (item.getjGambar() == "simsukarela10") {
            int id = context.getResources().getIdentifier("simsukarela10", "drawable", context.getPackageName());
            holder.mGambar.setImageResource(id);
        } else {
            SDImageLoader mImageLoader = new SDImageLoader();
            mImageLoader.load(item.getjGambar(), holder.mGambar);
        }

        return convertView;
    }
}