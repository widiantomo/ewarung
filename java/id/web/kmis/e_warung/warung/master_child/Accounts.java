package id.web.kmis.e_warung.warung.master_child;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.warung.master_child.mockedActivity.Settings;
import id.web.kmis.e_warung.warung.master_child.mockedFragments.FragmentButton;
import id.web.kmis.e_warung.warung.master_child.mockedFragments.FragmentIndex;
import id.web.kmis.e_warung.materialnavigationdrawer.MaterialNavigationDrawer;
import id.web.kmis.e_warung.materialnavigationdrawer.elements.MaterialAccount;
import id.web.kmis.e_warung.materialnavigationdrawer.elements.listeners.MaterialAccountListener;

/**
 * Created by neokree on 18/01/15.
 */
public class Accounts extends MaterialNavigationDrawer implements MaterialAccountListener {

    @Override
    public void init(Bundle savedInstanceState) {

        // add accounts
        MaterialAccount account = new MaterialAccount(this.getResources(),"NeoKree","neokree@gmail.com",R.drawable.photo, R.drawable.bamboo);
        this.addAccount(account);

        MaterialAccount account2 = new MaterialAccount(this.getResources(),"Hatsune Miky","hatsune.miku@example.com",R.drawable.photo2,R.drawable.mat2);
        this.addAccount(account2);

        MaterialAccount account3 = new MaterialAccount(this.getResources(),"Example","example@example.com",R.drawable.photo,R.drawable.mat3);
        this.addAccount(account3);

        // set listener
        this.setAccountListener(this);

        // create sections
        this.addSection(newSection("Section 1", new FragmentIndex()));
        this.addSection(newSection("Section 2",new FragmentIndex()));
        this.addSection(newSection("Section 3",R.drawable.ic_mic_white_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("Section",R.drawable.ic_hotel_grey600_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#03a9f4")));

        // create bottom section
        this.addBottomSection(newSection("Bottom Section",R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));

    }

    @Override
    public void onAccountOpening(MaterialAccount account) {

    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {

    }
}
