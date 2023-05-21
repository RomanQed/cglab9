package com.github.romanqed.cglab9;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Figure {
    final List<Point> body;

    public Figure(Collection<Point> body) {
        this.body = new ArrayList<>(body);
    }

    private Figure(List<Point> body) {
        this.body = body;
    }

    private static List<Point> cutEdge(Point start, Point end, List<Point> figure) {
        List<Point> ret = new ArrayList<>();
        int size = figure.size();
        for (int i = 1; i < size + 1; ++i) {
            Point cur = figure.get(i % size);
            if (Line.isIntersect(start, end, figure.get(i - 1), cur)) {
                ret.add(Line.intersectionPoint(start, end, figure.get(i - 1), cur));
            }
            if (Line.isVisible(start, end, cur)) {
                ret.add(cur);
            }
        }
        return ret;
    }

    public List<Point> getBody() {
        return body;
    }

    public boolean hasSelfIntersect() {
        int size = body.size();
        for (int i = 0; i < size; ++i) {
            int i1 = (i + 1) % size;
            Point a = body.get(i);
            Point b = body.get(i1);
            for (int j = 0; j < size; ++j) {
                int j1 = (j + 1) % size;
                Point c = body.get(j);
                Point d = body.get(j1);

                if (a.equals(c) || b.equals(c) || a.equals(d) || b.equals(d)) {
                    continue;
                }
                if (Line.isIntersect(a, b, c, d)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getConvexity() {
        int size = body.size();
        int prev = 0;
        int cur = 0;
        for (int i = 0; i < size; i++) {
            double value = Point.cross(body.get(i), body.get((i + 1) % size), body.get((i + 2) % size));
            cur = Double.compare(value, 0);
            if (cur == 0) {
                continue;
            }
            if (cur * prev < 0) {
                return 0;
            }
            prev = cur;
        }
        return cur;
    }

    public boolean isConvex() {
        return getConvexity() != 0 && hasSelfIntersect();
    }

    public Figure cut(Figure figure) {
        List<Point> ret = figure.body;
        int size = this.body.size();
        for (int i = 0; i < size; ++i) {
            int i1 = (i + 1) % size;
            ret = cutEdge(body.get(i), body.get(i1), ret);
            if (ret.size() < 3) {
                return null;
            }
        }
        return new Figure(ret);
    }
}
