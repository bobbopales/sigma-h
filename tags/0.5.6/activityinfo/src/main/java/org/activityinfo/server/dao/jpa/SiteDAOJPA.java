package org.activityinfo.server.dao.jpa;

import com.google.inject.Inject;
import org.activityinfo.server.dao.SiteDAO;
import org.activityinfo.server.domain.Site;

import javax.persistence.EntityManager;/*
 * @author Alex Bertram
 */

public class SiteDAOJPA implements SiteDAO {

    private EntityManager em;

    @Inject
    public SiteDAOJPA(EntityManager em) {
        this.em = em;
    }

    public Site findSiteById(int id) {
        return em.getReference(Site.class, id);
    }
}
