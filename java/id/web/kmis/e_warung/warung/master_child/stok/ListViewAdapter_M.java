package id.web.kmis.e_warung.warung.master_child.stok;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import id.web.kmis.e_warung.R;

public class ListViewAdapter_M extends BaseAdapter {

    public ArrayList<Model2> productList;
    Activity activity;
    private EditPlayerAdapterCallback callback;

    public ListViewAdapter_M(Activity activity, ArrayList<Model2> productList) {
        super();
        this.activity = activity;
        this.productList = productList;
    }


    public void setCallback(EditPlayerAdapterCallback callback) {
        this.callback = callback;
    }


    public interface EditPlayerAdapterCallback {
        public void deletePressed(int position);

        public void tolakpressed(int position);
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
        TextView msNo;
        TextView msTglp;
        TextView msSupp;
        TextView msNopo;
        TextView msKode;
        TextView msNama;
        TextView msJumlah;
        TextView msSatuan;
        TextView msHjual;

        ImageButton mTambah;
        ImageButton mTolak;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row_m, null);
            holder = new ViewHolder();
            holder.msNo = (TextView) convertView.findViewById(R.id.sNo);
            holder.msTglp = (TextView) convertView.findViewById(R.id.sTglp);
            holder.msSupp = (TextView) convertView.findViewById(R.id.sSupp);
            holder.msNopo = (TextView) convertView.findViewById(R.id.snopo);
            holder.msKode = (TextView) convertView.findViewById(R.id.sKode);
            holder.msNama = (TextView) convertView.findViewById(R.id.snama);
            holder.msJumlah = (TextView) convertView.findViewById(R.id.sjumlah);
            holder.msSatuan = (TextView) convertView.findViewById(R.id.ssatuan);
            holder.msHjual = (TextView) convertView.findViewById(R.id.shjual);

            holder.mTambah = (ImageButton) convertView.findViewById(R.id.terima);
            holder.mTambah.setTag(position);


            holder.mTolak = (ImageButton) convertView.findViewById(R.id.tolak);
            holder.mTolak.setTag(position);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        Model2 item = productList.get(position);
        holder.msNo.setText(item.getmsNo());
        holder.msTglp.setText(item.getmsTglp());
        holder.msSupp.setText(item.getmsSupp());
        holder.msNopo.setText(item.getmsNopo());
        holder.msKode.setText(item.getmsKode());
        holder.msNama.setText(item.getmsNama());
        holder.msJumlah.setText(item.getmsJumlah());
        holder.msSatuan.setText(item.getmsSatuan());
        holder.msHjual.setText(item.getmsHjual());

        if (item != null) {
            holder.mTambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cc = Integer.parseInt(String.valueOf(v.getTag()));
                    // int aa = Integer.parseInt(productList.get(cc).getmsNo());

                    if (callback != null) {
                        callback.deletePressed(cc); // apakah ditambahkan ke stok
                    }

                }
            });
        }
        holder.mTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cc = Integer.parseInt(String.valueOf(v.getTag()));
                // int aa = Integer.parseInt(productList.get(cc).getmsNo());

                if (callback != null) {
                    callback.tolakpressed(cc); // apakah ditambahkan ke stok
                }
            }
        });

        return convertView;
    }

}

