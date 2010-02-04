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
 * Copyright 2010 Alex Bertram and contributors.
 */

package org.activityinfo.endtoend.fixture;

import org.activityinfo.endtoend.fixture.AbstractContainer;

/**
 * Models the GWT Development Mode container.
 */
public class DevModeContainer extends AbstractContainer {

    @Override
    public String getUrl(String page) {
        StringBuilder url = new StringBuilder();
        url.append("http://localhost:9090");
        url.append(page);
        url.append(page.contains("?") ? "&" : "?");
        url.append("gwt.codesvr=localhost:9997");
        return url.toString();
    }
}