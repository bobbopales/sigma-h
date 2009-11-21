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

import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Date;

import org.activityinfo.server.domain.*;
import org.activityinfo.server.dao.AuthDAO;
import org.json.JSONException;

/**
 *
 *
 * @author Alex Bertram
 */
@Singleton
public class SyncServlet extends HttpServlet {

    private final Injector injector;

    @Inject
    public SyncServlet(Injector injector) {
        this.injector = injector;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String authToken = req.getParameter("authToken");
        AuthDAO authDAO = injector.getInstance(AuthDAO.class);
        Authentication auth = authDAO.getSession(authToken);
        if(auth==null) {
            resp.setStatus(500);
            return;
        }

        Date version = new Date(Long.parseLong(req.getParameter("lastVersion")));

        EntityManager em = injector.getInstance(EntityManager.class);

        try {
            String json = SynchBuilder.packageChanges(em, auth.getUser(), version.getTime());
            resp.setContentType("text/json");
            resp.getWriter().write(json);
        } catch (JSONException e) {
            resp.setStatus(500);
            resp.setContentType("text/plain");
            e.printStackTrace(resp.getWriter());
        }
    }

}