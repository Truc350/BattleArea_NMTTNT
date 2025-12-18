package com.example.model;

import java.util.Objects;

public class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    //tinh khoang cach toi diem khac
    public double distanceTo(Point other) {
        return Math.hypot(this.x - other.x, this.y - other.y);
    }

    // Di chuyển về phía điểm đích một đoạn (speed mỗi lượt)
    public void moveToward(Point target, double speed) {
        double dx = target.x - this.x;
        double dy = target.y - this.y;
        double dist = Math.hypot(dx, dy);
        if (dist > 0.001) {// tránh chia 0
            this.x += dx / dist * speed;
            this.y += dy / dist * speed;
        }
    }

    // Cộng vector
    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    // trừ vector
    public Point subtract(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    // Nhân với scalar (ví dụ speed boost)
    public Point multiply(double scalar) {
        return new Point(this.x * scalar, this.y * scalar);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0;
    }


}
