/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009 Alex Bertram and contributors.
 */

package org.activityinfo.server.servlet;

import org.activityinfo.server.domain.User;
import org.activityinfo.server.domain.DomainFilters;
import org.activityinfo.server.domain.UserDatabase;
import org.activityinfo.server.domain.Activity;
import org.json.JSONException;

import javax.persistence.EntityManager;

import com.google.gwt.gears.persistence.server.BulkOperationBuilder;


/**
 * @author Alex Bertram
 */
public class SynchBuilder {
    /**
     * Publically visible for testing.
     *
     * @param user
     * @param updateSequence
     * @return
     */
    public static String packageChanges(EntityManager em, User user, long updateSequence) throws JSONException {

        // Filter out entities which are not visible to the user due to
        // permissions
        DomainFilters.applyVisibleFilter(user, em);

        // Make a list of all of the sites that have been created or changed or deleted since
        // the user received their last update, order by date of change

        BulkOperationBuilder builder = new BulkOperationBuilder();
        builder.insert(UserDatabase.class, em.createQuery("select db from UserDatabase db").getResultList());
        builder.insert(Activity.class, em.createQuery("select a from Activity a").getResultList());

        return builder.asJson();
    }
}
