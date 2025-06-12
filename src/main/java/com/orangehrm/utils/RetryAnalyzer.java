package com.orangehrm.utils;


import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryLimit = ConfigReader.getRetryCount();

    // Map to track retry count per scenario unique id (thread-safe)
    private static final Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        // Retry only if test failed
        if (result.getStatus() != ITestResult.FAILURE) {
            return false;
        }

        // Build a unique key for the scenario
        // Use method name + parameters hash or test name + test class to distinguish
        String uniqueId = generateUniqueId(result);

        int count = retryCountMap.getOrDefault(uniqueId, 0);

        if (count < retryLimit) {
            count++;
            retryCountMap.put(uniqueId, count);
            System.out.println("Retrying " + uniqueId + " for the " + count + " time(s).");
            return true;
        }

        // Remove from map once retry limit reached to free memory
        retryCountMap.remove(uniqueId);
        return false;
    }

    private String generateUniqueId(ITestResult result) {
        // You can customize this to uniquely identify each scenario
        // Common way: methodName + parameter hashcode or scenario name from parameters

        String methodName = result.getMethod().getMethodName();
        Object[] params = result.getParameters();

        String paramsString = "";
        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object param : params) {
                sb.append(param == null ? "null" : param.toString()).append("_");
            }
            paramsString = sb.toString();
        }

        return methodName + "_" + paramsString;
    }
}
