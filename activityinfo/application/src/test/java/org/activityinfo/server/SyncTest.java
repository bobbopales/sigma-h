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

package org.activityinfo.server;

import org.junit.Test;
import org.activityinfo.server.servlet.SyncServlet;
import org.activityinfo.server.servlet.SynchBuilder;
import org.activityinfo.server.domain.User;
import org.activityinfo.client.offline.ClientPersistenceUnit;
import org.json.JSONException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;

import com.google.gwt.gears.persistence.mock.MockConnectionProvider;
import com.google.gwt.gears.persistence.mock.MockPersistenceUnitFactory;
import com.google.gwt.gears.bulk.mock.MockBulkOperation;

import java.sql.SQLException;

/**
 * @author Alex Bertram
 */
public class SyncTest extends DbUnitTestCase {

    /**
     * Verifies that a client side database is correctly populated
     * upon the first call to the synchronizer service.
     *
     */
    @Test
    public void testInit() throws JSONException, SQLException {

        // Populate our server-side database
        populate("sites-simple1");


        // Make our call to the server
        EntityManager serverEntityManager = emf.createEntityManager();
        User user = serverEntityManager.find(User.class, 1);
        String updatesJson = SynchBuilder.packageChanges(serverEntityManager, user, 0);
        serverEntityManager.close();

        // Construct our client-side entity manager
        MockPersistenceUnitFactory unitFactory = new MockPersistenceUnitFactory();
        MockConnectionProvider connProvider = new MockConnectionProvider("testInit");
        ClientPersistenceUnit pu = unitFactory.create(ClientPersistenceUnit.class);
        EntityManagerFactory clientEmf = pu.createEntityManagerFactory(connProvider);
        EntityManager clientEm = clientEmf.createEntityManager();

        MockBulkOperation op = new MockBulkOperation(updatesJson);
        op.execute(connProvider.getConnection());

    }


}
