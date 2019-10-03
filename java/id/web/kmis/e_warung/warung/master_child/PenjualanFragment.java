package id.web.kmis.e_warung.warung.master_child;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by neokree on 20/01/15.
 */
public class PenjualanFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.masterchild_master2,container,false);

        return view;
    }

    @Override
    public void onClick(View v) {
        ((MaterialNavigationDrawer)this.getActivity()).setFragmentChild(new ChildFragment(),"Penjualan");
    }
}
