package me.jollyfly.rocketmq.starter;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;

public class A {

    public static void main(String[] args) throws Exception {
//        main1();
//        main2();
//         main4();
//        for (int i = 0; i < 100; i++) {
//            System.out.println(ThreadLocalRandom.current().nextInt(500,600));
//        }

        InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
        String hostAddress = address.getHostAddress();
        System.out.println(hostAddress);
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
}
