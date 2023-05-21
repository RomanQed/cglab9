package com.github.romanqed.cglab9;

import java.util.Objects;

public final class Point {
    final double x;
    final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static boolean fromDifferentSides(Point main, Point v1, Point v2) {
        double product1 = main.cross(v1);
        double product2 = main.cross(v2);
        return (product1 >= 0 && product2 <= 0 || product1 <= 0 && product2 >= 0);
    }

    public static double cross(Point a, Point b, Point c) {
        double x1 = (b.x - a.x);
        double y1 = (b.y - a.y);
        double x2 = (c.x - a.x);
        double y2 = (c.y - a.y);
        return (x1 * y2 - y1 * x2);
    }

    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    // Signed area / determinant thing
    public double cross(Point p) {
        return x * p.y - y * p.x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return String.format("Point(%g, %g)", x, y);
    }
}
