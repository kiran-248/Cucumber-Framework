package com.orangehrm.utils;

public class ScenarioContext {
    public static boolean skipRemainingSteps = false;

    public static void markScenarioDone() {
        skipRemainingSteps = true;
    }

    public static boolean shouldSkip() {
        return skipRemainingSteps;
    }

    public static void reset() {
        skipRemainingSteps = false;
    }
}

