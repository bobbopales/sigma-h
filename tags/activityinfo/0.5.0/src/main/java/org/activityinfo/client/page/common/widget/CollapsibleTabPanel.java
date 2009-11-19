package org.activityinfo.client.page.common.widget;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 * A tab panel that can be collapsed by double-clicking the tab.
 *
 * TODO: move the logic from {@link org.activityinfo.client.page.entry.SiteGridPage} to this class
 *
 * @author Alex Bertram
 */
public class CollapsibleTabPanel extends TabPanel {

    public El getBody() {
        if (getTabPosition() == TabPosition.TOP) {
            return el().getChild(1);
        } else {
            return el().getChild(0);
        }
    }

    public El getBar() {
       if (getTabPosition() == TabPosition.TOP) {
            return el().getChild(0);
        } else {
            return el().getChild(1);
        }
    }

}
