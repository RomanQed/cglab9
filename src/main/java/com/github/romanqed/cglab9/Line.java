package com.github.romanqed.cglab9;

import java.util.Objects;

public class Line {
    final Point start;
    final Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Line(double x1, double y1, double x2, double y2) {
        this.start = new Point(x1, y1);
        this.end = new Point(x2, y2);
    }

    public static boolean isVisible(Point p1, Point p2, Point p3) {
        return crossSign(p1, p2, p3) >= 0;
    }

    public static int crossSign(Point p1, Point p2, Point p3) {
        Point v12 = p2.sub(p1);
        Point v13 = p3.sub(p1);
        double cross = v12.cross(v13);
        return Double.compare(cross, 0);
    }

    public static boolean isIntersect(Point s1, Point e1, Point s2, Point e2) {
        return crossSign(s1, e1, s2) * crossSign(s1, e1, e2) < 0;
    }

    public static Point intersectionPoint(Point p1, Point p2, Point w1, Point w2) {
        double a11 = p2.x - p1.x;
        double a12 = w1.x - w2.x;
        double a21 = p2.y - p1.y;
        double a22 = w1.y - w2.y;

        double r1 = w1.x - p1.x;
        double r2 = w1.y - p1.y;

        double d = a11 * a22 - a12 * a21;
        double A11 = a22 / d;
        double A12 = -a12 / d;

        double t = A11 * r1 + A12 * r2;

        double x = p1.x + (p2.x - p1.x) * t;
        double y = p1.y + (p2.y - p1.y) * t;

        return new Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return start.equals(line.start) && end.equals(line.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "Line{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
