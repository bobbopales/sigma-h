package org.activityinfo.client.offline.ui;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.inject.Singleton;
import org.activityinfo.client.offline.dao.AuthDAO;
import org.activityinfo.client.offline.Installer;

import java.util.Date;

@Singleton
public class OfflineMenuDisabled extends OfflineMenu {
    
    public OfflineMenuDisabled() {
    }

    @Override
    protected void init() {
        this.setText("Activer Mode Hors Connection");
        this.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

                // check to see if Gears is installed and accessible.

                if(Factory.getInstance() != null) {
                    Installer installer = new Installer();
                    installer.install(auth);
                } else {
                      // TODO: i18n
                      MessageBox.alert("Mode Hors Connexion", "Le mode hors connexion exige l'installation de" +
                        " <b>Google Gears</b>. Cliquer <a href='http://tools.google.com/gears/' target='_blank'>ici</a>" +
                        " à l'installer. ", null);
                }
            }
        });
    }
}
