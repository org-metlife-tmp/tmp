package com.qhjf.cfm.utils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class TestSimpleDateFormat {


    @Test
    public void noSafeDateParse() throws ExecutionException, InterruptedException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Callable<Date> task = new Callable<Date>() {
            @Override
            public Date call() throws Exception { //

                return sdf.parse("20170806");
            }
        };
        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future<Date>> results = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            results.add(pool.submit(task));
        }
        for (Future<Date> future : results) {
            System.out.println(future.get());
        }
        pool.shutdown();
    }

    @Test
    public void noSafeDateFormat() throws ExecutionException, InterruptedException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception { //
                Date now = new Date();
                return sdf.format(now);
            }
        };
        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future<String>> results = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            results.add(pool.submit(task));
        }
        for (Future<String> future : results) {
            System.out.println(future.get());
        }
        pool.shutdown();
    }


    @Test
    public void mutilDateFormat() throws ExecutionException, InterruptedException {

        //final DateFormatThreadLocal format =
        Callable<Date> task = new Callable<Date>() {
            @Override
            public Date call() throws Exception { //

                // return sdf.parse("20170806");
                //return format.parse("20170805");
                return DateFormatThreadLocal.convert("yyyyMMdd","20170805");
            }
        };
        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future<Date>> results = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            results.add(pool.submit(task));
        }
        for (Future<Date> future : results) {
            System.out.println(future.get());
        }
        pool.shutdown();
    }
}

