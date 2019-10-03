package id.web.kmis.e_warung.warung.master_child.penjualan;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import id.web.kmis.e_warung.R;

public class ListViewAdapter_N extends BaseAdapter {

    public ArrayList<Model_N> productList;
    Activity activity;

    public ListViewAdapter_N(Activity activity, ArrayList<Model_N> productList) {
        super();
        this.activity = activity;
        this.productList = productList;
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
        TextView mNbarang;
        TextView mHarga;
        TextView mJumlah;
        TextView mTotal;
        TextView mKode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_nota, null);
            holder = new ViewHolder();
            holder.mNbarang = (TextView) convertView.findViewById(R.id.namabarang);
            holder.mHarga = (TextView) convertView.findViewById(R.id.hargaitem);
            holder.mKode = (TextView) convertView.findViewById(R.id.kodebara);

            holder.mJumlah = (TextView) convertView
                    .findViewById(R.id.jumlah);
            holder.mTotal = (TextView) convertView.findViewById(R.id.subtotal);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model_N item = productList.get(position);
        holder.mKode.setText(item.getsKbarang());
        holder.mNbarang.setText(item.getsNbarang());
        holder.mHarga.setText(String.valueOf(item.getsHarga()));
        holder.mJumlah.setText(String.valueOf(item.getsJumlah()));
        holder.mTotal.setText(String.valueOf(item.getsHarga() * item.getsJumlah()));

        return convertView;
    }
}