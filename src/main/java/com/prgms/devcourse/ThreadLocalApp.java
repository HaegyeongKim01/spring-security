package com.prgms.devcourse;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;

public class ThreadLocalApp {

    /**
     * Integer 를 담는 ThreadLocal 변수
     */
    final static ThreadLocal<Integer> threadLocalValue = new ThreadLocal<>();

    public static void main(String[] args) {
        System.out.println(getCurrentThreadName() + "### main set value = 1");
        threadLocalValue.set(1);
        a();
        b();

        /**
         * 주어진 람다 block 을 실행하도록 하는 비동기 실행 코드
         * main Thread가 아닌 다른 Thread 에서 실행되는 block 이다.
         */
        CompletableFuture<Void> task = runAsync(() -> {
            a();
            b();
        });

        //람다 block 걸려있는 코드들이 실행 완료될 때 대기
        task.join();
    }

    public static void a() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + " ### a() get value = " + value);
    }

    public static void b() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + " ### b() get value = " + value);
    }

    /**
     * @return Thread name 반환
     */
    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

}
