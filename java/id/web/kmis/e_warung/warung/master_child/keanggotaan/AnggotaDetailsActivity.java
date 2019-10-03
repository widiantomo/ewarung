package id.web.kmis.e_warung.warung.master_child.keanggotaan;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


/**
 * Created by js on 8/21/2016.
 */
public class AnggotaDetailsActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_fragment_layout);
        // Toast.makeText(this, "DetailsActivity", Toast.LENGTH_SHORT).show();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.

            // create fragment
            AnggotaDetailsFragment details = new AnggotaDetailsFragment();

            // get and set the position input by user (i.e., "index")
            // which is the construction arguments for this fragment
            details.setArguments(getIntent().getExtras());

            //  //   final FragmentManager fragmentManager = getApplicationContext().getFragmentManager();
            //FragmentManager fragmentManager = .getSupportFragmentManager();
            // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //    fragmentTransaction.add(R.id.details, details);
            //   fragmentTransaction.commit();

            //
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, details).commit();
            //   Display the fragment as the main content.

            //   details.getSupportFragmentManager().beginTransaction()
            //         .replace(R.id.details, details)
            //        .commit();

        }
    }
}
