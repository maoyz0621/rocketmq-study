package me.jollyfly.rocketmq.starter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class A {

    public static void main(String[] args) throws Exception {
//        main1();
//        main2();
//         main4();
//        for (int i = 0; i < 100; i++) {
//            System.out.println(ThreadLocalRandom.current().nextInt(500,600));
//        }

        // List<String> list = new ArrayList<String>();
        // list.add("1");
        // list.add("1");
        // for (String item : list) {
        //     if ("1".equals(item)) {
        //         list.remove(item);
        //     }
        // }
        // System.out.println(list);
        //
        // Iterator<String> iterator = list.iterator();
        // while (iterator.hasNext()) {
        //     String item = iterator.next();
        //     if ("1".equals(item)) {
        //         iterator.remove();
        //     }
        // }
        // System.out.println(list);

        // main5();

        // main6();

        // main7();

        // https://wizardforcel.gitbooks.io/java8-tutorials/content/Java%208%20%E5%B9%B6%E5%8F%91%E6%95%99%E7%A8%8B%20Threads%20%E5%92%8C%20Executors.html
        // https://blog.csdn.net/caihaijiang/article/details/35552859
        // http://www.onlinedown.net/soft/577763.htm
        // https://www.jianshu.com/p/73981795cfa4
        // https://juejin.im/entry/5c2598686fb9a049d975454d
        // 关闭线程 https://my.oschina.net/u/3768341/blog/1842994

        System.out.println(new Date());
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        try {
            for (int i = 0; i < 20; i++) {
                executorService.execute(() -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName() + " ***********************");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

            }

            executorService.execute(() -> {
                try {
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " ------------------------------");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            System.out.println(new Date());
            /* 通常放在execute后面。如果调用 了这个方法，一方面，表明当前线程池已不再接收新添加的线程，新添加的线程会被拒绝执行。
            另一方面，表明当所有线程执行完毕时，回收线程池的资源。
            注意，它不会马上关闭线程池！ */
            try {
                System.out.println("attempt to shutdown executor");
                executorService.shutdown();
                executorService.awaitTermination(50, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.err.println("tasks interrupted");
            } finally {
                if (!executorService.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                executorService.shutdownNow();
                System.out.println("shutdown finished");

            }

            /*不管当前有没有线程在执行，马上关闭线程池！这个方法要小心使用，要不可能会引起系统数据异常！*/
            // executorService.shutdownNow();
            System.out.println(Thread.currentThread().getName() + " shutdown()***********************");
        }
    }

    public static void main7() {
        double a = 0.03D;
        double b = 0.02D;
        //0.05
        System.out.println(a + b);
        // 0.009999999999999998
        System.out.println(a - b);
        //6.0E-4
        System.out.println(a * b);
        System.out.println(a / b);

        // BigDecimal()  传入String类型才能精确计算
        BigDecimal a1 = new BigDecimal("0.03");
        BigDecimal b1 = new BigDecimal("0.02");
        System.out.println(b1.add(a1));
        // 0.01
        System.out.println(a1.subtract(b1));
        // 0.0006
        System.out.println(a1.multiply(b1));
        System.out.println(a1.divide(b1, RoundingMode.HALF_UP));

        BigDecimal a2 = new BigDecimal(0.03d);
        BigDecimal b2 = new BigDecimal(0.02d);
        // 0.0499999999999999993061106096092771622352302074432373046875
        System.out.println(a2.add(b2));
        // 0.0099999999999999984734433411404097569175064563751220703125
        System.out.println(a2.subtract(b2));
        System.out.println(a2.multiply(b2));
        System.out.println(a2.divide(b2, RoundingMode.HALF_UP));
    }

    public static void main5() {
        //查看当前的时区
        System.out.println(ZoneId.systemDefault());
        long l = System.currentTimeMillis();
        System.out.println(l);
        System.out.println(new Date(l));
        System.out.println(new Date());

        TimeZone timeZone = TimeZone.getDefault();
        String displayName = timeZone.getDisplayName();
        System.out.println(displayName);

        TimeZone timeZone1 = TimeZone.getTimeZone("Asia/Tokyo");
        String displayName1 = timeZone1.getDisplayName();
        System.out.println(displayName1);
    }

    public static void main0() {
        List list1 = new ArrayList();
        list1.add(1);
        list1.add(2);
        List list2 = new ArrayList();
        list2.add(3);
        list2.add(4);
        List list3 = new ArrayList();
        list3.add(list1);
        list3.add(list2);
        System.out.println(list3);
        System.out.println(list3.size());

        List list4 = new ArrayList();
        list4.addAll(list1);
        list4.addAll(list2);
        System.out.println(list4);
        System.out.println(list4.size());
        list1.add(1111);
        System.out.println(list4);
        System.out.println(list1);


        List list5 = new ArrayList();
        list5.add(0, 1);
        list5.add(2);
        System.out.println(list5);
        list5.set(0, 1111);
        System.out.println(list5);
    }


    public static void main1() {
        MyBean myBean1 = new MyBean("aaaa", 1, "mail", 2, Arrays.asList(new Entity("a", true)));
        MyBean myBean2 = new MyBean("aaaa", 2, "mail", 1, Arrays.asList(new Entity("a", true)));
        MyBean myBean3 = new MyBean("aaaa", 1, "femail", 1, Arrays.asList(new Entity("a", true)));
        MyBean myBean4 = new MyBean("aaaa", 1, "mail", 1, Arrays.asList(new Entity("a1", true)));
        List<MyBean> list1 = new ArrayList<>();
        list1.add(myBean1);
        list1.add(myBean2);
        List<MyBean> list2 = new ArrayList<>();
        list2.add(myBean3);
        list2.add(myBean4);

//        list2.stream()
//                .map()
//                .filter((a1,a2) -> {a1.equals(a2)})
//                .forEach();
        for (Iterator<MyBean> iterator = list2.iterator(); iterator.hasNext(); ) {
            MyBean next = iterator.next();
            for (Iterator<MyBean> iterator1 = list1.iterator(); iterator1.hasNext(); ) {
                MyBean next1 = iterator1.next();
                if (next.equals(next1)) {
                    next.setQuantity(next.getQuantity() + next1.getQuantity());
                    iterator1.remove();
                }
            }
        }

        list2.addAll(list1);
        System.out.println(list2);


        Set set = new HashSet();
        set.add(myBean1);
        set.add(myBean2);
        set.add(myBean3);
        set.add(myBean4);
//        System.out.println(set);

    }

    public static void main2() {
        char grade = 'B';
        switch (grade) {
            case 'A':
                System.out.println("优秀");
                break;
            case 'B':
            case 'C':
                System.out.println("良好");
                break;
            case 'D':
                System.out.println("及格");
                break;
            case 'F':
                System.out.println("你需要再努力努力");
                break;
            default:
                System.out.println("未知等级");
        }
        System.out.println("你的等级是 " + grade);
    }

    public static void main3() {
        List list1 = new ArrayList();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        list1.add(4);

        for (Iterator iterator = list1.iterator(); iterator.hasNext(); ) {
            Object next = iterator.next();
            System.out.println(next);
            iterator.remove();
        }
        System.out.println(list1);
    }

    public static void main4() throws InterruptedException {
        int count = 50;
        List list = new CopyOnWriteArrayList();
        List list1 = new CopyOnWriteArrayList();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < count; i++) {
            int j = i;
            list.add(j);
        }
        final CountDownLatch cyclicBarrier = new CountDownLatch(list.size());

        try {
            for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                Object next = iterator.next();
                executorService.execute(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    list1.add(next);
                    if (next.equals(20)) {
                        try {
                            int j = 1 / 0;
                        } catch (Exception e) {
                            cyclicBarrier.countDown();
                        }
                    }
                    System.out.println(cyclicBarrier.getCount());
                    cyclicBarrier.countDown();
                });

            }
        } catch (Exception e) {
            cyclicBarrier.countDown();
        } finally {
            cyclicBarrier.await();
            System.out.println("++++++++++++++++++++++++++++++++++" + list1);
            System.out.println("++++++++++++++++++++++++++++++++++" + list1.size());
            executorService.shutdown();
        }


    }

    public static void main6() throws InterruptedException {
        final int count = 30;
        final Semaphore semaphore = new Semaphore(10, false);
        final CountDownLatch countDownLatch = new CountDownLatch(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                atomicInteger.addAndGet(1);
                countDownLatch.countDown();
                try {
                    /* 从信号量尝试获取一个许可，如果无可用许可，直接返回false，不会阻塞 */
                    // semaphore.tryAcquire();

                    /* 从信号量获取一个许可，如果无可用许可前 将一直阻塞等待 */
                    semaphore.acquire();
                    System.out.println("------------" + Thread.currentThread().getName() + " : " + semaphore.availablePermits());
                    TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(2000));

                    int j = 1 / 0;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            });
        }

        countDownLatch.await();
        System.out.println(atomicInteger.get());
        executorService.shutdown();
    }
}
