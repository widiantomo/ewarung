package id.web.kmis.e_warung.warung.master_child.keanggotaan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.warung.master_child.ringkasan.Shakespeare;

/**
 * Created by js on 8/24/2016.
 */
public class AnggotaDisclaimerFragment extends Fragment {


    public static AnggotaDisclaimerFragment newInstance(int index, String idang) {
        AnggotaDisclaimerFragment f = new AnggotaDisclaimerFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("idang", idang);

        f.setArguments(args);

        return f;
    }


    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.disclaimer, container, false);

        Button buttonStart = (Button) view.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(startListener); // Register the onClick listener with the implementation above

        Button buttonStop = (Button) view.findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(stopListener); // Register the onClick listener with the implementation above

        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View v) {

            getActivity().setResult(Activity.RESULT_FIRST_USER);
            getActivity().finish();

        }
    };

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener stopListener = new View.OnClickListener() {
        public void onClick(View v) {


//            getActivity().setResult("0");
            getActivity().setResult(2);
            getActivity().finish();
        }
    };

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        Fragment fragment = (getFragmentManager()
                .findFragmentById(android.R.id.content));
        if (fragment.isResumed()) {
            getFragmentManager().beginTransaction().remove(fragment)
                    .commit();
        }
        super.onDestroyView();
    }


}
