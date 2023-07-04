# CLH锁-AQS的开胃菜

## 一、前言

多线程同时争取一个资源的时候，需要通过加锁来保证数据安全，为了应对synchronized的不足，java引入了juc包，其中最关键的类就是AQS，而AQS是基于CLH锁实现的，今天我会从自旋锁的实现来展开对CLH锁的剖析，进而让大家能够深的理解AQS。

## 二、概述

### 2.1 基本信息

CLH的全称是Craig, Landin, and Hagersten locks（三个人的名字），CLH 锁是对自旋锁的一种改进， 数据结构是隐式链表，AQS就是继续CLH来实现的，并衍生出很多实用的并发类，比如ReentrantLock、ReentrantReadWriteLock、CountDownLatch、Semaphore等。

## 三、自旋锁

AQS是基于CLH来实现的，CLH是一种改良后的自旋锁，介绍CLH锁之前，让我们先来了解下自旋锁。

### 3.1 代码

```java
public class SpinLock {
    private final AtomicReference<Thread> lock = new AtomicReference<>(null);

    public void lock(){
        while (!lock.compareAndSet(null, Thread.currentThread())){
            System.out.println("线程" + Thread.currentThread().getName() + "没能获取到锁，进行自旋等待。。。");
        }
    }

    public void unlock(){
        // 锁的拥有者才能释放锁
        lock.compareAndSet(Thread.currentThread(), null);
    }
}
```

AtomicReference原子类的compareAndSet方法调用了unsafe.compareAndSwapObject 

这里涉及到一个概念" **compare-and-swap** " 以下简称**CAS**，中文意思是”**比较并替换**“，当”比较值“和”内存值“一致的时候（说明没别的线程修改）把新的值设置到内存里。

自旋锁的实现分为以下3步

1. 创建一个原子变量，用来存线程，原因是他封装了CAS，且用了volatile修饰value变量，保证了内存可见性。
2. 获取锁：通过CAS操作，把当前变量CAS设置到原子变量里。
3. 释放锁：通过CAS操作，把null设置到原子变量里。

### 3.2 自旋锁的优缺点

**优点**

实现起来简单，避免了操作系统进程切换和线程的上下文切换所造成的开销。

**缺点**

2个缺点

1. 非公平性：锁竞争激烈的情况下，可能有线程一直被其他线程插队，导致获取不到锁。
2. 性能问题：锁竞争激烈的情况下，一直自旋，会浪费CPU资源。

**小结**

自旋锁适用于锁竞争不激烈、锁持有时间短的场景。

## 四、CLH锁

CLH锁:Craig, Landin, and Hagersten，是3个人的名字的缩写，它是基于隐式链表实现的自旋锁，自旋查询上一个节点获取锁的状态，当上一个节点释放锁，当前线程就能获取到锁了。

**实现步骤**

1. 每个线程拥有一个Node节点，节点有一个用volatile修饰的boolean类型的变量locked，表示是否获取到了锁，或是否正在尝试获取锁。
2. 获取锁的时候会查询上一个节点（尾节点）的locked变量，把当前线程设置为尾节点，接着自旋，直到上一个节点释放了锁，即上一个节点的locked的值为false，当前线程才拥有锁，并修改当前节点的locked变量为true。
3. 释放锁的时候，自旋修改当前线程的locked的值为false，把当前线程的node设置为新对象，防止死循环

**代码实现**

```java
public class ClhLockEasy {
    // 每个线程操作自己的node变量
    private final ThreadLocal<Node> node;
    // 原子变量，CAS操作尾节点的拥有者
    private final AtomicReference<Node> tail;

    public ClhLockEasy(){
        node = ThreadLocal.withInitial(Node::new);
        tail = new AtomicReference<>(new Node());
    }

    static class Node{
        // 默认没有参与锁的竞争
        private volatile boolean locked = false;
    }

    public void lock(){
        Node curNode = this.node.get();
        // 参与锁竞争的时候将locked设置为true
        curNode.locked = true;
        // get到尾节点，CAS将当前节点设置成尾节点
        Node preNode = tail.getAndSet(curNode);
        while (preNode.locked) {
            System.out.println("线程" + Thread.currentThread().getName() + "没能获取到锁，进行自旋等待。。。");
        }
        // 能执行到这里，说明当前线程获取到了锁
        System.out.println("线程" + Thread.currentThread().getName() + "获取到了锁！！！");
    }

    public void unlock(){
        // 释放锁，将locked设置为false
        Node curNode = this.node.get();
        curNode.locked = false;
        // 将当前节点设置为新节点，防止死循环
        this.node.set(new Node());
    }
}
```

**优点**：

1. 公平锁，先入对列的线程先获取到锁
2. 释放锁的时候不用自旋
3. 扩展性强，AQS就是基于CLH锁实现的

**缺点**：

1. 自旋，浪费CPU
2. 功能单一

## 五、AQS对CLH做的改造

AQS把CLH的自旋改成了阻塞，并改进了以下三个地方

1. 扩展了节点的状态。
2. 维护了前驱和后继节点。
3. 将出队列的节点设为null，辅助GC。

### 5.1节点的状态

```java
volatile int waitStatus;
```

状态名和描述如下表格

| 状态名       | 描述                       |
| --------- | ------------------------ |
| SIGNAL    | 表示该节点正常等待                |
| PROPAGATE | 应将 releaseShared 传播到其他节点 |
| CONDITION | 该节点位于条件队列，不能用于同步队列节点     |
| CANCELLED | 由于超时、中断或其他原因，该节点被取消      |

### 5.2 维护前驱节点和后继节点

本文实现的CLH锁，数据结构类似隐式链表（通过tail获取上一个节点）

维护前驱节点和后继节点后，当其中一个节点因为超时退出了竞争锁，可以通过它的前驱节点和后继节点将链表连接起来。

AQS中，被阻塞的线程不能感知前驱节点的状态变化，需要等锁释放后才会通知下一个节点接触阻塞。

由于释放锁没有自旋，如果当前节点的后驱节点不可用时（超时退出的线程），将从利用队尾指针 Tail 从尾部遍历到直到找到当前节点正确的后驱节点。

### 5.3 辅助GC

AQS在释放锁的时候将对象设置为null，就能在下一次GC的时候被回收掉。

## 六、总结

AQS的出现是为了应对Synchronized的不足，AQS基于CLH锁实现，CLH锁是自旋锁的改良，环环相扣，一步步拆分去理解就很容易理解AQS其中的奥秘了。