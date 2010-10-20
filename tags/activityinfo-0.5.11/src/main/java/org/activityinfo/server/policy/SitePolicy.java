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

package org.activityinfo.server.policy;

import com.google.inject.Inject;
import org.activityinfo.server.dao.*;
import org.activityinfo.server.domain.*;
import org.activityinfo.shared.dto.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SitePolicy implements EntityPolicy<Site> {
    private final ActivityDAO activityDAO;
    private final AdminDAO adminDAO;
    private final LocationDAO locationDAO;
    private final PartnerDAO partnerDAO;
    private final ReportingPeriodDAO reportingPeriodDAO;
    private final SiteDAO siteDAO;


    @Inject
    public SitePolicy(ActivityDAO activityDAO, AdminDAO adminDAO, LocationDAO locationDAO,
                      PartnerDAO partnerDAO, SiteDAO siteDAO, ReportingPeriodDAO reportingPeriodDAO) {
        this.locationDAO = locationDAO;
        this.siteDAO = siteDAO;
        this.reportingPeriodDAO = reportingPeriodDAO;
        this.partnerDAO = partnerDAO;
        this.activityDAO = activityDAO;
        this.adminDAO = adminDAO;
    }

    @Override
    public Integer create(User user, PropertyMap properties) {

        Activity activity = activityDAO.findById((Integer) properties.get("activityId"));
        Partner partner = partnerDAO.findById(((PartnerModel) properties.get("partner")).getId());

        assertSiteEditPrivileges(user, activity, partner);


        /*
           * Create and save a new Location object in the database
           */

        Location location = new Location();
        location.setLocationType(activity.getLocationType());
        updateLocationProperties(location, properties);

        locationDAO.persist(location);

        updateAdminProperties(location, properties, true);

        /*
           * Create and persist the Site object
           */

        Site site = new Site();
        site.setLocation(location);
        site.setActivity(activity);
        site.setPartner(partner);
        site.setDateCreated(new Date());

        updateSiteProperties(site, properties, true);

        siteDAO.persist(site);

        updateAttributeValueProperties(site, properties, true);

        /*
           * Create the reporting period object
           * IF this is a report-once activity (punctual)
           *
           * otherwise ReportingPeriods are modeled separately on the client.
           */

        if (activity.getReportingFrequency() == ActivityModel.REPORT_ONCE) {

            ReportingPeriod period = new ReportingPeriod();
            period.setSite(site);
            period.setMonitoring(false);

            updatePeriodProperties(period, properties, true);

            reportingPeriodDAO.persist(period);

            updateIndicatorValueProperties(period, properties, true);

        }

        return site.getId();

    }


    public void update(User user, Object id, PropertyMap changes)  {

        Site site = siteDAO.findById((Integer)id);

        assertSiteEditPrivileges(user, site.getActivity(), site.getPartner());

        site.setDateEdited(new Date());

        updateSiteProperties(site, changes, false);
        updateAttributeValueProperties(site, changes, false);
        updateLocationProperties(site.getLocation(), changes);
        updateAdminProperties(site.getLocation(), changes, false);

        ReportingPeriod period = site.primaryReportingPeriod();
        if (period != null) {
            updatePeriodProperties(period, changes, false);
            updateIndicatorValueProperties(period, changes, false);
        }
    }

    /**
     * Asserts that the user has permission to edit a site in a given database
     * belonging to a given partner
     */
    protected void assertSiteEditPrivileges(User user, Activity activity, Partner partner)  {
        UserDatabase db = activity.getDatabase();

        if (db.getOwner().getId() == user.getId())
            return;

        UserPermission perm = db.getPermissionByUser(user);
        if (perm.isAllowEditAll())
            return;
        if (!perm.isAllowEdit())
            throw new IllegalAccessError();
        if (perm.getPartner().getId() != partner.getId())
            throw new IllegalAccessError();
    }

    protected void updateAdminProperties(Location location, PropertyMap changes, boolean creating) {

        for (Map.Entry<String, Object> change : changes.entrySet()) {
            String property = change.getKey();
            Object value = change.getValue();

            if (property.startsWith(AdminLevelModel.PROPERTY_PREFIX)) {

                int levelId = AdminLevelModel.levelIdForProperty(property);
                AdminEntityModel entity = (AdminEntityModel) value;

                if (creating) {
                    if(entity != null) {
                        locationDAO.addAdminMembership(location.getId(), entity.getId());
                    }
                } else {
                    if(entity != null) {
                        locationDAO.updateAdminMembership(location.getId(), levelId, entity.getId());
                    } else {
                        locationDAO.removeMembership(location.getId(), levelId);
                    }
                }
            }
        }
    }

    protected void updateAttributeValueProperties(Site site, PropertyMap changes, boolean creating) {

        Map<Integer, Boolean> attributeValues = new HashMap<Integer, Boolean>();

        for (Map.Entry<String, Object> change : changes.entrySet()) {
            if (change.getKey().startsWith(AttributeModel.PROPERTY_PREFIX)) {
                attributeValues.put(AttributeModel.idForPropertyName(change.getKey()), (Boolean)change.getValue());
            }
        }
        if(!attributeValues.isEmpty())
            siteDAO.updateAttributeValues(site.getId(), attributeValues);
   }

    protected void updateIndicatorValueProperties(ReportingPeriod period, PropertyMap changes, boolean creating) {

        for (Map.Entry<String, Object> change : changes.entrySet()) {

            String property = change.getKey();
            Object value = change.getValue();

            if (property.startsWith(IndicatorModel.PROPERTY_PREFIX)) {

                int indicatorId = IndicatorModel.indicatorIdForPropertyName(property);

                if(creating) {
                    if(value != null) {
                        reportingPeriodDAO.addIndicatorValue(period.getId(), indicatorId, (Double)value);
                    }
                } else {
                    reportingPeriodDAO.updateIndicatorValue(period.getId(), indicatorId, (Double) value);
                }
            }
        }
    }

    protected void updateSiteProperties(Site site, PropertyMap changes, boolean creating) {

        for (Map.Entry<String, Object> change : changes.entrySet()) {

            String property = change.getKey();
            Object value = change.getValue();

            if ("date1".equals(property)) {
                site.setDate1((Date) value);

            } else if ("date2".equals(property)) {
                site.setDate2((Date) value);

            } else if ("assessmentId".equals(property)) {
                site.setAssessment(siteDAO.findById((Integer) value));

            } else if ("comments".equals(property)) {
                site.setComments((String) value);

            } else if ("status".equals(property)) {
                site.setStatus((Integer) value);
            }
        }
    }


    protected void updateLocationProperties(Location location, PropertyMap changes) {

        boolean isAdminBound = location.getLocationType().getBoundAdminLevel() != null;

        for (Map.Entry<String, Object> change : changes.entrySet()) {

            String property = change.getKey();
            Object value = change.getValue();

            if (!isAdminBound && "locationName".equals(property)) {
                location.setName((String) value);

            } else if ("locationAxe".equals(property)) {
                location.setAxe((String) value);

            } else if ("x".equals(property)) {
                location.setX((Double) value);

            } else if ("y".equals(property)) {
                location.setY((Double) changes.get("y"));

            } else if (isAdminBound &&
                    AdminLevelModel.getPropertyName(location.getLocationType().getBoundAdminLevel().getId()).equals(property)) {

                location.setName(adminDAO.findById(((AdminEntityModel) value).getId()).getName());
            }
        }
    }

    protected void updatePeriodProperties(ReportingPeriod period, PropertyMap changes, boolean creating) {
        for (Map.Entry<String, Object> change : changes.entrySet()) {

            String property = change.getKey();
            Object value = change.getValue();

            if ("date1".equals(property)) {
                period.setDate1((Date) value);

            } else if ("date2".equals(property)) {
                period.setDate2((Date) value);
            }
        }
    }
}