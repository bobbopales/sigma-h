package org.activityinfo.server.report.generator.map;

import org.activityinfo.server.domain.SiteData;
import org.activityinfo.server.report.generator.MapSymbol;
import org.activityinfo.shared.report.content.PieMapMarker;
import org.activityinfo.shared.report.content.Point;

import java.awt.*;
import java.util.List;


public class PointValue {

    public PointValue() {
    }

    public PointValue(SiteData site, MapSymbol symbol, double value, Point px) {
        this.site = site;
        this.symbol = symbol;
        this.value = value;
        this.px = px;
    }

    public PointValue(SiteData site, Point px, Rectangle iconRect) {
        this.site = site;
        this.px = px;
        this.iconRect = iconRect;
        this.value = 1;
    }

    public SiteData site;
    public MapSymbol symbol;
    public double value;
    public Point px;
    public Rectangle iconRect;
    public List<PieMapMarker.Slice> slices;

}