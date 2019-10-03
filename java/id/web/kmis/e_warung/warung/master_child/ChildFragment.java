package id.web.kmis.e_warung.warung.master_child;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.web.kmis.e_warung.R;

//import it.neokree.example.R;

/**
 * Created by neokree on 20/01/15.
 */
public class ChildFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MaterialNav","Child created");

        return inflater.inflate(R.layout.masterchild_child,container,false);
    }

}
