package org.activityinfo.client.page.entry.editor;

import com.extjs.gxt.ui.client.widget.form.TextField;
import org.activityinfo.client.Application;
import org.activityinfo.shared.dto.ActivityModel;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class LocationFieldSet extends AdminFieldSet {


    public LocationFieldSet(ActivityModel activity) {
        super(activity);

        if (activity.getLocationType().getBoundAdminLevelId() == null) {

            TextField<String> nameField = new TextField<String>();
            nameField.setName("locationName");
            nameField.setFieldLabel(activity.getLocationType().getName());
            nameField.setAllowBlank(false);
            add(nameField);

            TextField<String> axeField = new TextField<String>();
            axeField.setName("locationAxe");
            axeField.setFieldLabel(Application.CONSTANTS.axe());
            add(axeField);
        }
    }

}
