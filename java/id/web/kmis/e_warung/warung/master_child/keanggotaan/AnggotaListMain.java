package id.web.kmis.e_warung.warung.master_child.keanggotaan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.dbadapter.LoginDataBaseAdapter;
import id.web.kmis.e_warung.dbadapter.Mcrypt;
import id.web.kmis.e_warung.dbadapter.SetDatabaseAdapter;


/**
 * Created by neokree on 11/01/15.
 */
public class AnggotaListMain extends ListFragment {
    boolean mDualPane;
    int mCurCheckPosition = 0;
    int baru = 0;
    String idangx = "";
    private ProgressDialog pDialog;

    private JSONArray mComments = null;
    private ArrayList<HashMap<String, String>> mCommentList;

    private static final String TAG_SITEID = "site_id";
    private static final String TAG_LOCNAME = "LocationName";
    private static final String TAG_ADDRESS = "Address";

    private String latit[] = new String[500];
    private String longi[] = new String[500];
    private String locnm[] = new String[500];

    private float[] longif = new float[10];
    private float[] latitf = new float[10];

    private String post_username;
    EditText inputSearch;
    private ListAdapter adapter;
    Button post_comment, addanggota;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //   setListAdapter(new ArrayAdapter<String>(this.getActivity(),
        //          android.R.layout.simple_list_item_activated_1,
        //         Shakespeare.TITLES));

        View view = inflater.inflate(R.layout.read_comments, container, false);

        post_username = "";
        // receive bundle code warung
        // dikirm dari masterchildactivity
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        post_comment = (Button) view.findViewById(R.id.post_comment);
        addanggota = (Button) view.findViewById(R.id.addanggota);

        LoginDataBaseAdapter loginDataBaseAdapter = LoginDataBaseAdapter.getInstance(getActivity());
        loginDataBaseAdapter.open();
        mCommentList = new ArrayList<HashMap<String, String>>();
        //post_username nama warung
        Cursor c = loginDataBaseAdapter.insertRaw("select * from anggota");

        String idanggotax, kore;
        //load data anggota
        kore = "";
        Mcrypt mcrypt = new Mcrypt();
        int x = 0;
        if (!(c.moveToFirst()) || c.getCount() == 0) {
            c.close();
        } else {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                HashMap<String, String> mapx = new HashMap<String, String>();
                mapx.put(TAG_SITEID, c.getString(c.getColumnIndex("posisi"))); //site id
                mapx.put(TAG_LOCNAME, c.getString(c.getColumnIndex("nama"))); //TAG_LOCNAME
                idanggotax = c.getString(c.getColumnIndex("idanggota"));
                try {
                    kore = new String(mcrypt.decrypt(idanggotax), "UTF-8");
                    //res = URLDecoder(kore, "UTF-8");
                } catch (Exception exe) {
                    //Your error handling code
                }
                mapx.put(TAG_ADDRESS, kore); //address


                locnm[x] = kore;
                latit[x] = c.getString(c.getColumnIndex("filektp"));
                longi[x] = c.getString(c.getColumnIndex("filefoto"));
                mCommentList.add(mapx);

                x++;
            }
            c.close();
        }
        adapter = new SimpleAdapter(getActivity(), mCommentList,
                R.layout.single_post, new String[]{TAG_SITEID, TAG_LOCNAME,
                TAG_ADDRESS}, new int[]{R.id.title, R.id.message,
                R.id.username});

        setListAdapter(adapter);
        loginDataBaseAdapter.close();

        SetDatabaseAdapter lDa = SetDatabaseAdapter.getInstance(getActivity());
        lDa.open();
        if (!lDa.checkkeydevexist()) {
            addanggota.setVisibility(View.GONE);
        }
        lDa.close();
        post_comment.setVisibility(View.GONE);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                // ((SimpleAdapter) adapter).getFilter().filter(cs);
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                ((SimpleAdapter) adapter).getFilter().filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        addanggota.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final int x = mCurCheckPosition;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                alertDialogBuilder //kebalik cuy
                        .setTitle("Laman Persetujuan")
                        .setMessage(getResources().getString(R.string.pesansponsor))
                        .setCancelable(true)
                        .setNeutralButton("", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setPositiveButton("Setuju", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                baru = 1;
                                showDetails(0);
                            }
                        })
                        .setNegativeButton("Tidak Setuju", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setMaxLines(8);
                textView.setScroller(new Scroller(getActivity()));
                textView.setVerticalScrollBarEnabled(true);
                textView.setMovementMethod(new ScrollingMovementMethod());

            }
        });

        post_comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here


            }
        });

        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv = getListView();


        // receive bundle from masterchildactivity

        //setListAdapter(new ArrayAdapter<String>(getActivity(),
        //        android.R.layout.simple_list_item_activated_1,
        //        Shakespeare.TITLES));

        View detailsFrame = getActivity().findViewById(R.id.details);

        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("index", 0);
            idangx = savedInstanceState.getString("idang", "");
            baru = savedInstanceState.getInt("baru", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected
            // item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
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
        outState.putString("idang", idangx);
        //outState.putInt("idang", );

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    void showDetails(int index) {
        mCurCheckPosition = index;
        idangx = locnm[index];

        if (mDualPane) {
            getListView().setItemChecked(index, true);
            //i.putExtra("kodeanggota", locnm[x]);

            // if (getActivity().getSupportFragmentManager().findFragmentById(R.id.details) != null) {
            //      getActivity().getSupportFragmentManager().popBackStack();
            //  } //to do add fragment

            Fragment details = getActivity().getSupportFragmentManager().findFragmentById(R.id.details);
            AnggotaDetailsFragment detailx = AnggotaDetailsFragment.newInstance(mCurCheckPosition, idangx, baru);
            if (details != null && !(details.getClass().equals(detailx.getClass()))) {
                AnggotaDetailsFragment detailz = AnggotaDetailsFragment.newInstance(mCurCheckPosition, idangx, baru);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.details, detailz);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                baru = 0;
            } else {

                AnggotaDetailsFragment detailz = (AnggotaDetailsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.details);
                if (detailz == null || baru == 1 || (detailz.getShownIndex() != index)) {
                    detailz = AnggotaDetailsFragment.newInstance(mCurCheckPosition, idangx, baru);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.details, detailz);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                    baru = 0;
                }
            }
        } else {

            Intent intent = new Intent();
            intent.setClass(getActivity(), AnggotaDetailsActivity.class);
            intent.putExtra("idang", mCurCheckPosition);
            intent.putExtra("index", idangx);
            intent.putExtra("baru", baru);
            startActivity(intent);
            baru = 0;
        }

    }
}
