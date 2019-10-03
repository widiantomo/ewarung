package id.web.kmis.e_warung.materialnavigationdrawer.elements.listeners;

import id.web.kmis.e_warung.materialnavigationdrawer.elements.MaterialAccount;

/**
 * Created by neokree on 11/12/14.
 */
public interface MaterialAccountListener {

    public void onAccountOpening(MaterialAccount account);

    public void onChangeAccount(MaterialAccount newAccount);

}
