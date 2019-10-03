package id.web.kmis.e_warung.warung.master_child.keanggotaan;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import id.web.kmis.e_warung.warung.master_child.ringkasan.DetailsFragment;


/**
 * Created by js on 8/21/2016.
 */
public class AnggotaDisclaimerActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //     finish();
        //     return;
        //  }

        if (savedInstanceState == null) {
            AnggotaDisclaimerFragment details = new AnggotaDisclaimerFragment();

            details.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, details).commit();


        }
    }
}