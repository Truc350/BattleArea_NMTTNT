package com.example.model;

import java.util.Objects;

public class Point {
    private double x, y;

    // Tốc độ di chuyển mỗi lượt
    public static final double MOVE_SPEED = 1.5;

    // Giới hạn bản đồ: bán kính tối đa từ gốc (0,0)
    private static final double MAX_DISTANCE = 10.0;

    public Point(double x, double y) {
        setPosition(x, y);  // Dùng setter để clamp ngay từ đầu
    }

    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    // Tính khoảng cách đến điểm khác
    public double distanceTo(Point other) {
        return Math.hypot(this.x - other.x, this.y - other.y);
    }

    // Di chuyển VỀ PHÍA điểm đích
    public void moveToward(Point target, double speed) {
        double dx = target.x - this.x;
        double dy = target.y - this.y;
        double dist = Math.hypot(dx, dy);

        if (dist > 0.001) {
            double moveX = (dx / dist) * speed;
            double moveY = (dy / dist) * speed;
            setPosition(this.x + moveX, this.y + moveY);
        }
    }

    // Di chuyển RA XA điểm đích (lùi lại)
    public void moveAway(Point target, double speed) {
        double dx = this.x - target.x;  // vector từ target đến this
        double dy = this.y - target.y;
        double dist = Math.hypot(dx, dy);

        if (dist > 0.001) {
            double moveX = (dx / dist) * speed;
            double moveY = (dy / dist) * speed;
            setPosition(this.x + moveX, this.y + moveY);
        }
    }

    // Setter chung: tự động clamp vào bản đồ tròn
    private void setPosition(double newX, double newY) {
        double dist = Math.hypot(newX, newY);
        if (dist > MAX_DISTANCE) {
            // Đẩy về biên vòng tròn
            this.x = newX * (MAX_DISTANCE / dist);
            this.y = newY * (MAX_DISTANCE / dist);
        } else {
            this.x = newX;
            this.y = newY;
        }
    }

    // Clamp thủ công (dự phòng)
    public void clampPosition() {
        setPosition(this.x, this.y);
    }

    // Vector operations
    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    public Point subtract(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    public Point multiply(double scalar) {
        return new Point(this.x * scalar, this.y * scalar);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // Getter
    public double getX() { return x; }
    public double getY() { return y; }
}