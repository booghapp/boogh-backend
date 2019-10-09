package org.boogh.web.rest.util;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class GeoUtils {

    private static final int DEFAULT_SRID =  4326;
    private static final GeometryFactory defaultGeometryFactory = new GeometryFactory(new PrecisionModel(), DEFAULT_SRID);
    private static final WKTReader defaultWktReader = new WKTReader(defaultGeometryFactory);

    // in JTS, longitude is X, latitude is Y
    public static Point pointFromLongLat(double longitude, double latitude) {
        return defaultGeometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public static String wktFromLongLat(double longitude, double latitude) {
        return String.format("POINT (%s %s)", longitude, latitude);
    }

    // example of well-known-text: "POINT (long lat)"
    public static Point pointFromWKT(String wkt) {
        try {
            return (Point) defaultWktReader.read(wkt);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Geometry wktToGeometry(String wktString) {
        try {
            return defaultWktReader.read(wktString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Geometry createCircle(double x, double y, double radius) {
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(32);
        shapeFactory.setCentre(new Coordinate(x, y));
        shapeFactory.setSize(radius * 2);
        return shapeFactory.createCircle();
    }

    public static Geometry boundingBoxToGeometry(double xmin, double ymin, double xmax, double ymax) {
        Envelope env = new Envelope(xmin, xmax, ymin, ymax);

        return defaultGeometryFactory.toGeometry(env);
    }

}
