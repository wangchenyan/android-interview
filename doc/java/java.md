# Java要点


## 1. Object的hashCode方法，equals方法和clone方法在实现的时候要注意什么？

在一个运行的进程中，相等的对象必须要有相同的哈希码；不同的对象可以有相同的哈希码；

1.无论你何时实现equals方法，你必须同时实现hashCode方法；

2.永远不要把哈希码误用作为key，哈希冲突是很常见的事情；hashmap中的contains方法的实现！

3.哈希码可变，hashcode并不保证在不同的应用执行中得到相同的结果；

4.在分布式应用中不要使用哈希码。

替代哈希码：SHA1，加密的哈希码，160位密钥，冲突几乎是不可能的。

集合增加时的原理：

当使用HashSet时，hashCode()方法就会被调用，判断已经存储在集合中的对象的hashCode值是否与增加的对象的hashCode值一致；
如果不一致，直接加进去；如果一致，再进行equals方法的比较，equals方法如果返回true，表示对象已经加进去了，就不会再增加新的对象，否则加进去。

**使用clone()方法的步骤：**

1.实现clone的类首先需要继承Cloneable接口。

2.在类中重写Object类中的clone()方法。

3.在clone方法中调用super.clone()。

4.把浅复制的引用指向原型对象新的克隆体。


## 2. HashMap是怎么实现的？

1.HashMap的数据结构

数组的特点是：寻址容易，插入和删除困难；而链表的特点是：寻址困难，插入和删除容易。
那么我们能不能综合两者的特性，做出一种寻址容易，插入删除也容易的数据结构？
答案是肯定的，这就是我们要提起的哈希表，哈希表有多种不同的实现方法，我接下来解释的是最常用的一种方法——拉链法，我们可以理解为“链表的数组”，如图：

![](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/java/image/hashmap.jpg)
 
从上图我们可以发现哈希表是由数组+链表组成的，一个长度为16的数组中，每个元素存储的是一个链表的头结点。
那么这些元素是按照什么样的规则存储到数组中呢。一般情况是通过hash(key)%len获得，也就是元素的key的哈希值对数组长度取模得到。
比如上述哈希表中，12%16=12,28%16=12,108%16=12,140%16=12。所以12、28、108以及140都存储在数组下标为12的位置。

HashMap其实也是一个线性的数组实现的,所以可以理解为其存储数据的容器就是一个线性数组。
这可能让我们很不解，一个线性的数组怎么实现按键值对来存取数据呢？这里HashMap有做一些处理。

首先HashMap里面实现一个静态内部类Entry，其重要的属性有key,value,next，从属性key,value我们就能很明显的看出来Entry就是HashMap键值对实现的一个基础bean，
我们上面说到HashMap的基础就是一个线性数组，这个数组就是Entry[]，Map里面的内容都保存在Entry[]里面。

[图解HashMap](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2017/1203/8832.html)

2.HashMap的存取实现

既然是线性数组，为什么能随机存取？这里HashMap用了一个小算法，大致是这样实现：

```
// 存储时:
int hash = key.hashCode(); // 这个hashCode方法这里不详述,只要理解每个key的hash是一个固定的int值
int index = hash % Entry[].length;
Entry[index] = value;
// 取值时:
int hash = key.hashCode();
int index = hash % Entry[].length;
return Entry[index];
```


## 3. Java内部类为什么可以访问外部类的成员？

内部类都持有一个外部类的引用，这个是引用是外部类名.this。内部类可以定义在外部类中的成员位置上，也可以定义在外部类中的局部位置上。
当内部类被定义在局部位置上，只能访问局部中被final修饰的局部变量。

如果内部类被静态修饰，相当于外部类，会出现访问局限性，只能访问外部类中的静态成员。

注意：如果内部类中定义了静态成员，那么该内部类必须是静态的。内部类编译后的文件名为：“外部类名$内部类名.java"


## 4. ArrayList如何边遍历边删除？多线程访问ArrayList怎么做到互斥访问？

**ArrayList边遍历边删除——迭代器：**

```
ArrayList<String> list = new ArrayList<String>();
list.add("one");
list.add("two");
list.add("two");
list.add("two");
list.add("two");
Iterator<String> iter = list.iterator();
while(iter.hasNext()){
    String s = iter.next();
    if(s.equals("two")){
	    iter.remove();
    }
}
System.out.println(list);
```

**ListIterator和Iterator的区别：**

1.ListIterator实现了Iterator接口，拥有Iterator的所有方法。

2.ListIterator有add()和set()方法，可以向List中添加/修改对象，而Iterator不能。

3.ListIterator和Iterator都有hasNext()和next()方法，可以实现顺序向后遍历。
但是ListIterator有hasPrevious()和previous()方法，可以实现逆向遍历，而Iterator不能。

4.ListIterator可以定位当前的索引位置，nextIndex()和previousIndex()可以实现。Iterator没有此功能。

**使ArrayList线程安全：**

1.将访问ArrayList的方法设置为synchronized;

2.List list = Collections.synchronizedList(new ArrayList());


## 5. Java的四中引用类型

强引用：JVM宁愿抛出OOM也不会将它回收，可能导致内存泄露

软引用：当内存空间不足的时候才会去回收软引用的对象

弱引用：在系统GC时，弱引用的对象一定会被回收，软弱引用适合保存那些可有可无的缓存数据

虚引用：虚引用跟没有引用差不多，即使虚引用对象还存在，get方法总是返回null，它最大的作用是跟踪对象回收，清理被销毁对象的相关资源

WeakHashMap适用场景：如果系统需要一张很大的map表，map中的表项作为缓存之用，即使没能从map中拿到数据也没关系的情况下。
一旦内存不足的时候，WeakHashMap会将没有被引用的表项清除掉，从而避免内存溢出。它是实现缓存的一种特别好的方式。

如果希望WeakHashMap能够自动清理数据就不要在系统的其他地方强引用WeakHashMap的key，否则，这些key不会被回收。


## 6. 抽象类和接口的区别
1. 抽象类只能被继承，而且只能单继承。接口需要被实现，而且可以多实现。
2. 抽象类中可以定义非抽象方法，子类可以直接继承使用。接口中都是抽象方法，需要实现类去实现。
3. 抽象类使用的是is-a关系。接口使用的has-a系。
4. 抽象类的成员修饰符可以自定义，接口中的成员修饰符是固定的，全都是public的。


## 7. JVM

JVM内存模型:程序计数器，虚拟机栈，本地方法栈，Java堆，方法区

程序计数器:程序计数器是一块较小的内存空间，它的作用可以看做是当前线程所执行的字节码的行号指示器。
字节码解释器工作时就是通过改变这个计数器的值来选取下一条需要执行的字节码指令，分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖这个计数器来完成。

虚拟机栈:与程序计数器一样，Java 虚拟机栈（Java Virtual Machine Stacks）也是线程私有的，它的生命周期与线程相同。
虚拟机栈描述的是Java方法执行的内存模型：每个方法被执行的时候都会同时创建一个栈帧（Stack Frame）用于存储局部变量表、操作栈、动态链接、方法出口等信息。

本地方法栈:本地方法栈（Native Method Stacks）与虚拟机栈所发挥的作用是非常相似的，
其区别不过是虚拟机栈为虚拟机执行Java方法（也就是字节码）服务，而本地方法栈则是为虚拟机使用到的Native方法服务。

Java堆:几乎所有的对象和数组都是在堆中分配空间的，分为新生代和老年代。新生代可分为eden, survivor space 0, survivor space 1

方法区:方法区（Method Area）与Java 堆一样，是各个线程共享的内存区域，它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。
在Hot spot虚拟机中，方法区也叫永久区，但是也会被GC，GC主要有两类:对常量池的回收，对类元数据的回收

如果VM确认所有该类的实例都被回收并且装载该类的类加载器也被回收了，那么就回收该类的元数据

运行时常量池(Runtime Constant Pool)是方法区的一部分。(不一定)

Java语言并不要求常量一定只能在编译期产生，也就是并非预置入Class文件中常量池的内容才能进入方法区运行时常量池，
运行期间也可能将新的常量放入池中，这种特性被开发人员利用得比较多的便是String类的intern()方法。

JVM参数:设置最大堆内存Xmx，最小堆内存Xms，新生代大小Xmn，老年代大小PermSize，线程栈大小Xss，新生代eden和s0空间大小比例以及老年代和新生代的空间大小比例

**垃圾回收算法**
1. 引用计数法: 给对象添加一个引用计数器，每当一个地方引用它时就+1，当引用失效时就-1，任何时刻计数器为0的对象就是不可能再被引用的。缺点是无法处理循环引用问题
2. 标记-清除法: 标记所有从根节点开始的可达对象，清除所有未被标记的对象。缺点是会造成内存空间不连续，不连续的内存空间的工作效率低于连续的内存空间，不容易分配内存
3. 复制算法: 将内存空间分成两块，每次将正在使用的内存中的存活对象复制到未使用的内存块中，算法效率高，但是代价是将系统内存折半。适用于新生代(存活对象少，垃圾对象多)
4. 标记-压缩算法: 标记-清除的改进，清除未标记的对象时还将所有的存活对象压缩到内存的一端既避免碎片产生，又不需要两块同样大小的内存块，性价比高。适用于老年代
5. 分代: 把堆分成两个或者多个子堆，每一个子堆被视为一代。算法在运行的过程中优先收集那些“年幼”的对象，如果一个对象经过多次收集仍然“存活”，
那么就可以把这个对象转移到高一级的堆里，减少对其的扫描次数。

**垃圾回收器的类型**

(1)线程数:串行，并行

(2)工作模式:并发，独占

(3)碎片处理:压缩，非压缩

(4)分代:新生代，老年代

并行:开启多个线程同时进行垃圾回收，缩短GC停顿时间

并发:垃圾回收线程和应用程序线程交替工作

CMS: Concurrent Mark Sweep 并发标记清除，减少GC造成的停顿时间。过程:初始标记，并发标记，重新标记，并发清理，并发重置

G1:基于标记-整理算法

**GC Roots有哪些？**
1. 虚拟机栈(栈帧中的本地变量表)中引用的对象
2. 方法区中的类静态属性引用的对象
3. 方法区中的常量引用的对象
4. 原生方法栈（Native Method Stack）中 JNI 中引用的对象

对方法区的定义是：各个线程共享的内存区域，存储已被虚拟机加载的类信息，变量，静态变量等数据。


## 8. 类加载器
DexClassLoader和PathClassLoader的区别
1. DexClassLoader：能够加载未安装的jar/apk/dex 
2. PathClassLoader：只能加载系统中已经安装过的apk


## 9. ThreadLocal
一般来说，当某些数据是以线程为作用域并且不同线程具有不同的数据副本的时候，就可以考虑采用 ThreadLocal。

比如对应 Handler 来说，它需要获取当前线程的 Looper，很显然 Looper 的作用域就是线程并且不同线程具有不同的 Looper，
这个时候通过 ThreadLocal 就可以轻松实现 Looper 在线程中的存取。
如果不采用 ThreadLocal，那么系统就必须提供一个全局的哈希表供 Handler 查找指定线程的 Looper，
这样一来就必须提供一个类似于 LooperManager 的类了，但是系统并没有这么做而是选择了 ThreadLocal，这就是 ThreadLocal 的好处

```
//Looper#myLooper()

static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}

public static @Nullable Looper myLooper() {
    return sThreadLocal.get();
}
```

源码

```
public class ThreadLocal<T> {
    private final int threadLocalHashCode = nextHashCode();
	private static int nextHashCode = 0;
	private static final int HASH_INCREMENT = 0x61c88647;
	private static synchronized int nextHashCode() {
		int h = nextHashCode;
		nextHashCode = h + HASH_INCREMENT;
		return h;
    }
    public T get() {
        Thread t = Thread.currentThread();
		ThreadLocalMap map = getMap(t);
		if (map != null)
			return (T)map.get(this);
        T value = initialValue();
		createMap(t, value);
		return value;
    }
    public void set(T value) {
		Thread t = Thread.currentThread();
		ThreadLocalMap map = getMap(t);
		if (map != null)
			map.set(this, value);
		else
			createMap(t, value);
    }
    ThreadLocalMap getMap(Thread t) {
		return t.threadLocals;
    }
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
    .......
}
```

ThreadLocalMap 类是ThreadLocal中定义的内部类，但是它的实例却用在Thread类中：

```
public class Thread implements Runnable {
    ......
    ThreadLocal.ThreadLocalMap threadLocals = null;  
    ......
}
```

[带你了解源码中的 ThreadLocal](https://mp.weixin.qq.com/s/VNBwPPJeENyU9iyxJcN1qw)


## 10. 设计模式

1.单例模式

2.工厂模式

简单工厂模式 工厂方法模式 抽象工厂模式

3.代理模式和装饰模式的区别

代理模式关注于控制对对象的访问，而装饰器模式关注于在一个对象上动态的添加方法。换句话说，用代理模式，代理类可以对它的客户隐藏一个对象的具体信息。
因此，当使用代理模式的时候，我们常常在一个代理类中创建一个对象的实例，当我们使用装饰器模式的时候，我们通常的做法是将原始对象作为一个参数传给装饰者的构造器。
使用代理模式，代理和真实对象之间的的关系通常在编译时就已经确定了，而装饰者能够在运行时递归地被构造。


## 11. 位运算

& 按位与 相同位的两个数字都为1，则为1；若有一个不为1，则为0。(5&3=1)

| 按位或 相同位只要一个为1即为1。(5|3=7)

^ 按位异或 相同位不同则为1，相同则为0。(5^3=6)

~ 按位取反 把内存中的0和1全部取反。(~5=-6)

<< 左移 (5<<1=10)

`>>` 有符号右移 (5`>>`1=2)

`>>>` 无符号右移 (5`>>>`1=2)


## 12. synchronized 实现原理
JVM 基于进入和退出Monitor对象来实现方法同步和代码块同步。

方法级的同步是隐式，即无需通过字节码指令来控制的，它实现在方法调用和返回操作之中。
JVM可以从方法常量池中的方法表结构(method_info Structure) 中的 ACC_SYNCHRONIZED 访问标志区分一个方法是否同步方法。
当方法调用时，调用指令将会 检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置，如果设置了，执行线程将先持有monitor（虚拟机规范中用的是管程一词），
然后再执行方法，最后再方法完成(无论是正常完成还是非正常完成)时释放monitor。

代码块的同步是利用monitorenter和monitorexit这两个字节码指令。它们分别位于同步代码块的开始和结束位置。
当jvm执行到monitorenter指令时，当前线程试图获取monitor对象的所有权，如果未加锁或者已经被当前线程所持有，就把锁的计数器+1；
当执行monitorexit指令时，锁计数器-1；当锁计数器为0时，该锁就被释放了。
如果获取monitor对象失败，该线程则会进入阻塞状态，直到其他线程释放锁。

**公平锁和非公平锁**
公平锁指在分配锁前检查是否有线程在排队等待获取该锁，优先将锁分配给排队时间最长的线程。

非公平锁指在分配锁时不考虑线程排队等待的情况，直接尝试获取锁，在获取不到锁时再排到队尾等待。
因为公平锁需要在多核的情况下维护一个锁线程等待队列，基于该队列进行锁的分配，因此效率比非公平锁低很多，
java中的synchronized是非公平锁，ReentrantLock默认的lock方法采用的是非公平锁，ReentrantLock也支持公平锁。


## 13. 泛型
**泛型好处**

1. 泛型简单易用
2. 类型安全 泛型的主要目标是实现java的类型安全。泛型可以使编译器知道一个对象的限定类型是什么，这样编译器就可以在一个高的程度上验证这个类型
3. 消除了强制类型转换 使得代码可读性好，减少了很多出错的机会

**泛型的实现原理**

泛型的实现是靠类型擦除技术，类型擦除是在编译期完成的，也就是在编译期，编译器会将泛型的类型参数都擦除成它的限定类型，
如果没有则擦除为 object 类型，之后在获取的时候再强制类型转换为对应的类型。在运行期间并没有泛型的任何信息，因此也没有优化。

