package id.web.kmis.e_warung.warung.master_child.stok;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import id.web.kmis.e_warung.R;

public class StokListMain extends ListFragment {
    boolean mDualPane;
    int mCurCheckPosition = 0;
    int baru = 0;
    // String idangx = "";
    private ProgressDialog pDialog;

    private String post_username;
    EditText inputSearch;
    private ListAdapter adapter;
    Button post_comment, addanggota;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.stokactivity, container, false);
        // //return view;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                R.layout.your,
                MenuStok.TITLES));

        //  setListAdapter(new ArrayAdapter<String>(getActivity(),
        //           R.layout.sample_row,  simple_list_item_activated_1,
        //           MenuStok.TITLES));
        //modeList.setAdapter(modeAdapter);

        //return view;
        return super.onCreateView(inflater, container, savedInstanceState);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                R.layout.your,
                MenuStok.TITLES));
/*
        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                MenuStok.TITLES));
*/
        // ListView lv = getListView();
        View detailsFrame = getActivity().findViewById(R.id.details);

        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("index", 0);
            //   idangx = savedInstanceState.getString("idang", "");
            // baru = savedInstanceState.getInt("baru", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected
            // item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            //  Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        } else {
            // We also highlight in uni-pane just for fun
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            getListView().setItemChecked(mCurCheckPosition, true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", mCurCheckPosition);
        //outState.putString("idang", idangx);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
        Log.d("pencet menu ke", String.valueOf(position));
    }

    void showDetails(int index) {
        mCurCheckPosition = index;
        //    idangx = locnm[index];

        if (mDualPane) {
            getListView().setItemChecked(index, true);
            //getListView().setItemChecked(index, true);
            //i.putExtra("kodeanggota", locnm[x]);
            Log.d("show details ke", String.valueOf(mCurCheckPosition));
            // if (getActivity().getSupportFragmentManager().findFragmentById(R.id.details) != null) {
            //      getActivity().getSupportFragmentManager().popBackStack();
            //  } //to do add fragment

            Fragment details = getActivity().getSupportFragmentManager().findFragmentById(R.id.details);
            StokDetailsFragment detailx = StokDetailsFragment.newInstance(mCurCheckPosition);

            if (details != null && !(details.getClass().equals(detailx.getClass()))) {
                //StokDetailsFragment
                //       detailx = StokDetailsFragment.newInstance(mCurCheckPosition);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.details, detailx);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                baru = 0;

            } else {
                StokDetailsFragment detailv = (StokDetailsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.details);
                if (details == null || (detailv.getShownIndex() != index)) {
                    detailv = StokDetailsFragment.newInstance(mCurCheckPosition);
                    FragmentTransaction ftx = getActivity().getSupportFragmentManager().beginTransaction();
                    ftx.replace(R.id.details, detailv);
                    ftx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ftx.commit();
                    baru = 0;
                }

            }

        } else {

            Intent intent = new Intent();
            intent.setClass(getActivity(), StokDetailsActivity.class);
            intent.putExtra("index", mCurCheckPosition);
            startActivity(intent);
            baru = 0;
        }


    }
}
