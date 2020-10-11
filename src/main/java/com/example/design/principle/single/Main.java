package com.example.design.principle.single;

/**
 * @author 李磊
 * @since 1.0
 */
public class Main {
    public static void main(String[] args) {
        new RoadRun().run();
        new WaterRun().run();
    }
}

class RoadRun {
    public void run() {
        System.out.println("在路上跑");
    }
}

class WaterRun {
    public void run() {
        System.out.println("在水上跑");
    }
}