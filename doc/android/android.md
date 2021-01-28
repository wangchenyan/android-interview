# Android要点


## 1. Activity的生命周期，启动模式有哪些？

### Activity 生命周期

![Activity 生命周期](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/android/image/activity_lifecycle.jpg)

### Fragment 生命周期
1. replace，加回退栈，Fragment不销毁，视图销毁，回退后重新创建视图。
2. replace，不加回退栈，Fragment销毁。
3. hide、show，Fragment不销毁，也不销毁视图，隐藏和显示不走生命周期。

### Activity启动模式
- standard：每次激活Activity时(startActivity)，都创建Activity实例，并放入任务栈；
- singleTop：如果某个Activity自己激活自己，即任务栈栈顶就是该Activity，则不需要创建，其余情况都要创建Activity实例；
- singleTask：如果要激活的那个Activity在任务栈中存在该实例，则不需要创建，只需要把此Activity放入栈顶，即把该Activity以上的Activity实例都pop，并调用其onNewIntent；
- singleInstance：应用A的任务栈中创建了MainActivity实例，如果应用B也要激活 MainActivity，则不需要创建，两应用共享该Activity实例。

onSaveInstanceState的调用遵循一个重要原则，即当系统“未经你许可”时销毁了你的 activity，则onSaveInstanceState会被系统调用，
这是系统的责任，因为它必须要提供一个机会让你保存你的数据。至于onRestoreInstanceState方法，需要注意的是，
onSaveInstanceState方法和onRestoreInstanceState方法“不一定”是成对的被调用的。

onRestoreInstanceState被调用的前提是，activity A“确实”被系统销毁了，而如果仅仅是停留在有这种可能性的情况下，
则该方法不会被调用，例如，当正在显示activity A的时候，用户按下HOME键回到主界面，然后用户紧接着又返回到activity A，
这种情况下 activity A一般不会因为内存的原因被系统销毁，故activity A的onRestoreInstanceState方法不会被执行。

另外，onRestoreInstanceState的bundle参数也会传递到onCreate方法中，你也可以选择在onCreate方法中做数据还原。


## 2. Activity启动过程
1. 无论是通过Launcher来启动Activity，还是通过Activity内部调用startActivity接口来启动新的Activity，
都通过Binder进程间通信进入到ActivityManagerService进程中，并且调用 ActivityManagerService.startActivity接口；
2. ActivityManagerService调用ActivityStack.startActivityMayWait来做准备要启动的Activity的相关信息；
3. ActivityStack通知ApplicationThread要进行Activity启动调度了，这里的ApplicationThread代表的是调用
ActivityManagerService.startActivity接口的进程，对于通过点击应用程序图标的情景来说，这个进程就是Launcher了，
而对于通过在Activity内部调用startActivity的情景来说，这个进程就是这个Activity所在的进程了；
4. ApplicationThread不执行真正的启动操作，它通过调用activityPaused接口进入到ActivityManagerService进程中，
看看是否需要创建新的进程来启动Activity；
5. 对于通过点击应用程序图标来启动Activity的情景来说，ActivityManagerService在这一步中，
会调用startProcessLocked来创建一个新的进程，而对于通过在Activity内部调用startActivity来启动新的Activity来说，
这一步是不需要执行的，因为新的Activity就在原来的Activity所在的进程中进行启动；
6. ActivityManagerService调用ApplicationThread.scheduleLaunchActivity接口，通知相应的进程执行启动Activity的操作；
7. ApplicationThread把这个启动Activity的操作转发给ActivityThread，ActivityThread通过ClassLoader导入相应的Activity类，然后把它启动起来。

### Zygote的启动过程
1. 系统启动时init进程会创建Zygote进程，Zygote进程负责后续Android应用程序框架层的其它进程的创建和启动工作。
2. Zygote进程会首先创建一个SystemServer进程，SystemServer进程负责启动系统的关键服务，
如包管理服务PackageManagerService和应用程序组件管理服务ActivityManagerService。
3. 当我们需要启动一个Android应用程序时，ActivityManagerService会通过Socket进程间通信机制，通知Zygote进程为这个应用程序创建一个新的进程。

[Android应用程序进程启动过程](https://blog.csdn.net/itachi85/article/details/64123035)


## 3. 进程间通信
1. 访问其他应用程序的Activity
2. Content Provider
3. Broadcast
4. AIDL服务
5. Messenger

### Binder机制

[Binder 学习总结](https://www.jianshu.com/p/62a07a5c76e5)

### Binder 优势
1. 传输性能好
- socket：是一个通用接口，导致其传输效率低，开销大，主要用在跨网络的进程间通信和本机上进程间的低速通信
- 管道和消息队列：因为采用存储转发方式，所以至少需要拷贝2次数据，效率低；
- 共享内存：虽然在传输时没有拷贝数据，但其控制机制复杂。

2. 安全性高
- Android为每个安装好的应用程序分配了自己的 UID，进程的 UID 是鉴别进程身份的重要标志。可靠的身份标记只有由 IPC 机制本身在内核中添加。

### AIDL中的in out inout
- in 表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动
- out 表现为服务端将会接收到那个对象的参数为空的对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动
- inout 表现为服务端将会接收到客户端传来对象的完整信息，并且客户端将会同步服务端对该对象的任何变动


## 4. ANR问题
ANR一般有三种类型：
1. KeyDispatchTimeout(5 seconds): 主要类型按键或触摸事件在特定时间内无响应
2. BroadcastTimeout(10 seconds): BroadcastReceiver在特定时间内无法处理完成
3. ServiceTimeout(20 seconds): 小概率类型 Service在特定的时间内无法处理完成

### 如何避免ANR
1. UI线程尽量只做跟UI相关的工作
2. 耗时的工作（比如数据库操作，I/O，连接网络或者别的有可能阻碍UI线程的操作）把它放入单独的线程处理
3. 尽量用Handler来处理UIThread和WorkThread之间的交互
4. BroadCastReceiver要进行复杂操作的的时候，可以在onReceive()方法中启动一个 Service来处理。

### AND 问题排查

1. 导出 trace 文件，adb pull data/anr/traces.txt
2. 分析 trace

- 如果CPU使用量接近100%，说明当前设备很忙，有可能是CPU饥饿导致了ANR；
- 如果CPU使用量很少，说明主线程被BLOCK了；
- 如果IOWait很高，说明ANR有可能是主线程在进行I/O操作造成的。


## 5. OOM问题
1. 应用中需要加载大对象，例如Bitmap
解决方案：当我们需要显示大的bitmap对象或者较多的bitmap的时候，就需要进行压缩来防止OOM问题。
我们可以通过设置BitmapFactory.Options的inJustDecodeBounds属性为true，这样的话不会加载图片到内存中，
但是会将图片的width和height属性读取出来，我们可以利用这个属性来对bitmap进行压缩。Options.inSampleSize 可以设置压缩比。

持有无用的对象使其无法被gc，导致Memory Leak。

2. 静态变量导致的Memory leak

静态变量的生命周期和类是息息相关的，它们分配在方法区上，垃圾回收一般不会回收这一块的内存。
所以我们在代码中用到静态对象，在不用的时候如果不赋null值，消除对象的引用的话，那么这些对象是很难被垃圾回收的，
如果这些对象一多或者比较大的话，程序出现OOM的概率就比较大了。因为静态变量而出现内存泄漏是很常见的。

3. 不合理使用Context 导致的Memory leak

Android中很多地方都要用到context,连基本的Activity和Service都是从Context派生出来的，
我们利用Context主要用来加载资源或者初始化组件，在Activity中有些地方需要用到Context的时候，
我们经常会把context给传递过去了，将context传递出去就有可能延长了context的生命周期，最终导致了内存泄漏。
对于android应用来说还有一个单例的Application Context对象，该对象生命周期和应用的生命周期是绑定的。
选择context应该考虑到它的生命周期，如果使用该context的组件的生命周期超过该context对象，
那么我们就要考虑是否可以用Application context。如果真的需要用到该context对象，可以考虑用弱引用WeakReference来避免内存泄漏。

4. 非静态内部类导致的Memory leak

非静态的内部类会持有外部类的一个引用，所以和前面context说到的一样，如果该内部类生命周期超过外部类的生命周期，
就可能引起内存泄露了，如AsyncTask和Handler。因为在Activity中我们可能会用到匿名内部类，所以要小心管理其生命周期。
如果明确生命周期较外部类长的话，那么应该使用静态内部类。

5. Drawable对象的回调隐含的Memory leak

当我们为某一个view设置背景的时候，view会在drawable对象上注册一个回调，所以drawable对象就拥有了该view的引用了，
进而对整个context都有了间接的引用了，如果该drawable对象没有管理好，例如设置为静态，那么就会导致Memory leak。

### 发生OOM的原因
1. 文件描述符(fd)数目超限，即proc/pid/fd下文件数目突破/proc/pid/limits中的限制。可能的发生场景有：
短时间内大量请求导致socket的fd数激增，大量（重复）打开文件等
2. 线程数超限，即proc/pid/status中记录的线程数（threads项）突破/proc/sys/kernel/threads-max中规定的最大线程数。可能的发生场景有：
app内多线程使用不合理，如多个不共享线程池的OkHttpClient等等
3. 传统的java堆内存超限，即申请堆内存大小超过了 Runtime.getRuntime().maxMemory()

### 图片OOM优化
1. 在内存引用上做些处理，常用的有软引用、弱引用
2. 在内存中加载图片时直接在内存中做处理，如:边界压缩
3. 动态回收内存
4. 优化Dalvik虚拟机的堆内存分配
5. 自定义堆内存大小

### Bitmap分配在native heap还是dalvik heap上？
BitmapFactory.java里面有几个decode***方法用来创建bitmap，最终都会调用：

```
private staticnative Bitmap nativeDecodeStream(InputStream is, byte[] storage,Rect padding,Options opts);
```

而nativeDecodeStream()会调用到BitmapFactory.cpp中的deDecode方法，最终会调用到Graphics.cpp的createBitmap方法。

我们来看看createBitmap方法的实现：

```
jobjectGraphicsJNI::createBitmap(JNIEnv* env, SkBitmap* bitmap, jbyteArray buffer,
                                  boolisMutable, jbyteArray ninepatch, int density)
{
    SkASSERT(bitmap);
    SkASSERT(bitmap->pixelRef());

    jobject obj = env->NewObject(gBitmap_class, gBitmap_constructorMethodID,
           static_cast<jint>(reinterpret_cast<uintptr_t>(bitmap)),
            buffer, isMutable, ninepatch,density);
    hasException(env); // For the side effectof logging.
    return obj;
}
```

从代码中可以看到bitmap对象是通过env->NewObject( )创建的，到这里疑惑就解开了，bitmap对象是虚拟机创建的，
JNIEnv的NewObject方法返回的是java对象，并不是native对象，所以它会分配到dalvik heap中。


## 6. Looper和Handler

```
public class Looper {
    // 每个线程中的Looper对象其实是一个ThreadLocal，即线程本地存储(TLS)对象
    private static final ThreadLocal sThreadLocal = new ThreadLocal();
    // Looper内的消息队列
    final MessageQueue mQueue;
    // 当前线程
    Thread mThread;
    // 每个Looper对象中有它的消息队列，和它所属的线程
    private Looper() {
        mQueue = new MessageQueue();
        mRun = true;
        mThread = Thread.currentThread();
    }
    // 我们调用该方法会在调用线程的TLS中创建Looper对象
    public static final void prepare() {
        if (sThreadLocal.get() != null) {
            // 试图在有Looper的线程中再次创建Looper将抛出异常
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }
    public static final void loop() {
        Looper me = myLooper(); //得到当前线程Looper
        MessageQueue queue = me.mQueue; //得到当前looper的MQ
        
        // 这两行没看懂，不过不影响理解
        Binder.clearCallingIdentity();
        final long ident = Binder.clearCallingIdentity();
        // 开始循环
        while (true) {
            Message msg = queue.next(); // 取出message
            if (msg != null) {
                if (msg.target == null) {
                    // message没有target为结束信号，退出循环
                    return;
                }
                ……
                // 非常重要！将真正的处理工作交给message的target，即后面要讲的handler
                msg.target.dispatchMessage(msg);
                ……
                // 回收message资源
                msg.recycle();
            }
        }
    }
    public static Looper myLooper() {
        return sThreadLocal.get();
    }
    ……
}

public class Handler {
    ……
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
    ……
}
```

[Handler的初级、中级、高级问法，你都掌握了吗？](https://juejin.im/post/6893791473121280013)

### Android中为什么主线程不会因为Looper.loop()里的死循环卡死？

在主线程的MessageQueue没有消息时，便阻塞在loop的queue.next()中的nativePollOnce()方法里，
此时主线程会释放CPU资源进入休眠状态，直到下个消息到达或者有事务发生，通过往pipe管道写端写入数据来唤醒主线程工作。
这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。

[Android中为什么主线程不会因为Looper.loop()里的死循环卡死？](https://www.zhihu.com/question/34652589/answer/90344494)

### Handler异步消息
1. `public Handler(boolean async)`
2. `Message.setAsynchronous(boolean async)`

#### 同步屏障
1. 屏障消息和普通消息的区别在于屏障没有target，普通消息有target是因为它需要将消息分发给对应的target，而屏障不需要被分发，它就是用来挡住普通消息来保证异步消息优先处理的
2. 屏障和普通消息一样可以根据时间来插入到消息队列中的适当位置，并且只会挡住它后面的同步消息的分发
3. postSyncBarrier()返回一个int类型的数值，通过这个数值可以撤销屏障即removeSyncBarrier()
4. 插入普通消息会唤醒消息队列，但是插入屏障不会

[Android Handler之同步屏障机制](https://www.jianshu.com/p/ed318296f95f)

### AsyncTask和Handler对比
1. AsyncTask是android提供的轻量级的异步类,可以直接继承AsyncTask,在类中实现异步操作,
并提供接口反馈当前异步执行的程度(可以通过接口实现UI进度更新),最后反馈执行的结果给UI主线程.
- 优点:1.简单,快捷 2.过程可控
- 缺点:在使用多个异步操作和并进行UI变更时,就变得复杂起来.

2. Handler在异步实现时,涉及到Handler,Looper,Message,Thread四个对象，实现异步的流程是主线程启动Thread(子线程),
Thread(子线程)运行并生成Message,Looper获取 Message并传递给Handler,Handler逐个获取Looper中的Message,并进行UI变更。
- 优点：1.结构清晰，功能定义明确 2.对于多个后台任务时，简单，清晰
- 缺点：在单个后台异步处理时，显得代码过多，结构过于复杂（相对性）


## 7. Android消息推送机制
几种常见的解决方案：
1. 轮询(Pull)方式：应用程序应当阶段性的与服务器进行连接并查询是否有新的消息到达，你必须自己实现与服务器之间的通信，例如消息排队等。
而且你还要考虑轮询的频率，如果太慢可能导致某些消息的延迟，如果太快，则会大量消耗网络带宽和电池。
2. SMS(Push)方式：在Android平台上，你可以通过拦截SMS消息并且解析消息内容来了解服务器的意图，并获取其显示内容进行处理。
这个方案的好处是，可以实现完全的实时操作。但是问题是这个方案的成本相对比较高。
3. 持久连接(Push)方式：这个方案可以解决由轮询带来的性能问题，但是还是会消耗手机的电池。

百度云推送：<br>
百度云推送的实现技术简单来说就是利用Socket维持Client和Server间的一个TCP长连接，通过这种方式能大大降低由轮询方式带来的Device的耗电量和数据访问流量。

### 移动端获取网络数据优化的几个点
1. 连接复用: 节省连接建立时间，如开启keep-alive <br>
对于Android来说默认情况下HttpURLConnection和HttpClient都开启了keep-alive。只是2.2之前HttpURLConnection存在影响连接池的Bug
2. 请求合并: 即将多个请求合并为一个进行请求，比较常见的就是网页中的CSSImage Sprites。如果某个页面内请求过多，也可以考虑做一定的请求合并
3. 减少请求数据的大小: 对于post请求，body可以做gzip压缩的，header也可以作数据压缩(不过只支持http 2.0)
4. 返回的数据的body也可以作gzip压缩，body数据体积可以缩小到原来的30%左右。
(也可以考虑压缩返回的json数据的key数据的体积，尤其是针对返回数据格式变化不大的情况，支付宝聊天返回的数据用到了)
5. 根据用户的当前的网络质量来判断下载什么质量的图片(电商用的比较多)


## 8. Serializable 和 Parcelable 的区别
1. Parcelable的效率要快于Serializable(这是最主要的区别)。
- Serializable底层实现需要用到反射，而且也会产生大量的对象(这可能会触发GC)；再者就是Serializable是在IO操作。
- Parcelable底层实现则不需要反射，而且它是内存操作。
2. Parcelable的使用要复杂于Serializable。
3. IPC的时候用Parcelable，是因为它效率高。网络传输和保存至磁盘的时候用Serializable，是因为Parcelable不能保证当外部条件发生变化时数据的连续性。


## 9. View
![](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/android/image/view_draw_process.jpg)

View的绘制主要涉及三个方法: onMeasure()、onLayout()和onDraw()。

1. onMeasure主要用于计算view的大小，onLayout主要用于确定view在ContentView中的位置，onDraw主要是绘制view;
2. 在执行onMeasure()，onLayout()方法时都会先通过相应的标志位或者对应的坐标点来判断是否需要执行对应的函数，
如我们经常调用的invalidate方法就只会执行onDraw方法，因为此时的视图大小和位置均未发生改变，除非调用requestLayout方法完整强制进行view 的绘制，从而执行上面三个方法。

### Android View draw 流程

View中：

```
public void draw(Canvas canvas) {
/*
1. Draw the background: 绘制背景
2. If necessary, save the canvas' layers to prepare for fading: 如有必要，颜色渐变淡之前保存画布层(即锁定原有的画布内容)
3. Draw view's content: 绘制view的内容
4. Draw children: 绘制子view
5. If necessary, draw the fading edges and restore layers: 如有必要，绘制颜色渐变淡的边框，并恢复画布(即画布改变的内容附加到原有内容上)
6. Draw decorations (scrollbars for instance): 绘制装饰，比如滚动条
*/
   ...
   if (!dirtyOpaque) {
       drawBackground(canvas); //背景绘制
   }
   // skip step 2 & 5 if possible (common case) 通常情况跳过第2和第5步
   ...
   if (!dirtyOpaque) onDraw(canvas); //调用onDraw
   dispatchDraw(canvas);   //绘制子view
   onDrawScrollBars(canvas); //绘制滚动条
   ...
}
```

ViewGroup中：

```
protected void dispatchDraw(Canvas canvas) {
    ...
    drawChild(...); //绘制子view
    ...
}
```

自定义ViewGroup，onDraw可能不会被调用，原因是需要先设置一个背景(颜色或图)，因此，一般重写dispatchDraw来绘制ViewGroup。

### SurfaceView和View的区别
SurfaceView在新的线程中更新画面，而View必须在UI线程中更新画面。

在UI线程中更新画面可能会引发问题，比如你更新画面的时间过长，那么你的主UI线程会被你正在画的函数阻塞。那么将无法响应按键，触屏等消息。
SurfaceView由于是在新的线程中更新画面所以不会阻塞UI线程。但这也带来了另外一个问题，就是事件同步。
比如你触屏了一下，你需要SurfaceView中thread处理，一般就需要有一个event queue的设计来保存 touch event，这会稍稍复杂一点，因为涉及到线程同步。


### include merge ViewStub 标签
简言之: include merge都是用来解决重复布局的问题，但是merge标签能够在布局重用的时候减少UI层级结构。

ViewStub标签是用来给其他的view事先占据好位置，当需要的时候调用inflater()或者是 setVisible()方法显示这些View。


## 10. Touch 事件机制
1. 事件从Activity.dispatchTouchEvent()开始传递，只要没有被停止或拦截，从最上层的 View(ViewGroup)开始一直往下(子View)传递。
子 View 可以通过 onTouchEvent()对事件进行处理。
2. 事件由父 View(ViewGroup)传递给子View，ViewGroup 可以通过 onInterceptTouchEvent()对事件做拦截，停止其往下传递。
3. 如果事件从上往下传递过程中一直没有被停止，且最底层子 View 没有消费事件，事件会反向往上传递，这时父 View(ViewGroup)可以进行消费，
如果还是没有被消费的话，最后会到 Activity 的 onTouchEvent()函数。
4. 如果 View 没有对 ACTION_DOWN 进行消费，之后的其他事件不会传递过来。
5. OnTouchListener 优先于 onTouchEvent()对事件进行消费。

上面的消费即表示相应函数返回值为 true。 

![View不处理事件流程图](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/android/image/touch_event_not_consume.jpg)

![View处理事件流程图](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/android/image/touch_event_consume.jpg)


## 11. RecyclerView
[RecyclerView缓存机制](https://www.wanandroid.com/wenda/show/14222)
[Android ListView 与 RecyclerView 对比浅析--缓存机制](https://mp.weixin.qq.com/s/-CzDkEur-iIX0lPMsIS0aA)


## 12. Scroller
Scroller执行流程里面的三个核心方法 mScroller.startScroll() mScroller.computeScrollOffset() view.computeScroll()
1. 在mScroller.startScroll()中为滑动做了一些初始化准备。<br>
比如:起始坐标，滑动的距离和方向以及持续时间(有默认值)，动画开始时间等
2. mScroller.computeScrollOffset()方法主要是根据当前已经消逝的时间来计算当前的坐标点。<br>
因为在mScroller.startScroll()中设置了动画时间，那么在computeScrollOffset()方法中依据已经消逝的时间就很容易得到
当前时刻应该所处的位置并将其保存在变量mCurrX和 mCurrY中。除此之外该方法还可判断动画是否已经结束。


## 13. Android 动画原理
### 补间动画
在每一次VSYNC到来时，在View的draw方法里面，根据当前时间计算动画进度，计算出一个需要变换的Transformation矩阵，
然后最终设置到canvas上去，调用canvas concat做矩阵变换。

![](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/android/image/tween_animation.png)

[Android动画Animation运行原理解析](https://mp.weixin.qq.com/s/uqFErwA5gBGrzW5GoKbnBA)

### 属性动画
[属性动画 ValueAnimator 运行原理全解析](https://mp.weixin.qq.com/s/SZXJQNXar0SjApbl4rXicA)


## 14. WebView
### Native 与 Js通信
复写 WebView 的 WebChromeClient 类的 onJsPrompt 方法，按照一定的协议，传递方法和参数，通过 WebView.loadUrl 回调执行结果。
Js 中执行 window.prompt 调用 Native 方法。

### loadUrl 与 evaluateJavascript 的区别
1. evaluateJavascript 的执行不会使页面刷新，而方法 loadUrl 的执行则会使页面刷新
2. evaluateJavascript Android 4.4 后才可使用

### WebView 秒开方案
1. WebView 内核提前初始化
2. 资源预置，通过拦截 WebView 资源请求，直接返回本地资源
3. 数据预取，启动 WebView 的同时开始下载资源，WebView 可以直接使用已下载的资源
4. 使用离线包，需要有版本控制能力

[Android Webview H5 秒开方案实现](https://mp.weixin.qq.com/s/XfBt_gTw0gN7tXzuyP4PTw)


## 14. 换肤方案
通过 AssetManager 加载 apk 文件中的资源，通过 LayoutInflater.Factory hook View 创建，两者配合可以做到动态换肤。

[Android 常用换肤方式以及原理分析](https://juejin.im/post/6844903670270656525)
 
```
String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.apk";
//通过反射获取未安装apk的AssetManager
AssetManager assetManager = AssetManager.class.newInstance();
//通过反射增加资源路径
Method method = assetManager.getClass().getMethod("addAssetPath", String.class);
method.invoke(assetManager, apkPath);
File dexDir = ctx.getDir("dex", Context.MODE_PRIVATE);
if (!dexDir.exists()) {
    dexDir.mkdir();
}
//获取未安装apk的Resources
Resources resources = new Resources(assetManager, ctx.getResources().getDisplayMetrics(),
        ctx.getResources().getConfiguration());
//获取未安装apk的ClassLoader
ClassLoader classLoader = new DexClassLoader(apkPath, dexDir.getAbsolutePath(), null, ctx.getClassLoader());
//反射获取class
Class aClass = classLoader.loadClass("com.noob.resourcesapp.R$drawable");
int id = (int) aClass.getField("icon_collect").get(null);
imageView.setImageDrawable(resources.getDrawable(id));
```

```
LayoutInflater.from(this).setFactory(new LayoutInflater.Factory() {
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        Log.e("MainActivity", "name :" + name);
        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            Log.e("MainActivity", "AttributeName :" + attrs.getAttributeName(i) + "AttributeValue :"+ attrs.getAttributeValue(i));
        }
        return null;
    }
});
```

## 15.热修复原理
### 1.QQ空间
把补丁类生成 patch.dex，在app启动时，使用反射获取当前应用的ClassLoader，也就是 BaseDexClassLoader，
反射获取其中的pathList，类型为DexPathList，反射获取其中的 Element[] dexElements, 记为elements1;
然后使用当前应用的ClassLoader作为父ClassLoader，构造出 patch.dex 的 DexClassLoader,
通用通过反射可以获取到对应的 Element[] dexElements，记为elements2。将elements2拼在elements1前面，然后再去调用加载类的方法loadClass。

隐藏的技术难点 CLASS_ISPREVERIFIED 问题
apk在安装时会进行dex文件进行验证和优化操作。这个操作能让app运行时直接加载odex文件，能够减少对内存占用，
加快启动速度，如果没有odex操作，需要从apk包中提取dex再运行。
在验证过程，如果某个类的调用关系都在同一个dex文件中，那么这个类会被打上CLASS_ISPREVERIFIED标记，表示这个类已经预先验证过了。
但是再使用的过程中会反过来校验下，如果这个类被打上了CLASS_ISPREVERIFIED但是存在调用关系的类不在同一个dex文件中的话，会直接抛出异常。
为了解决这个问题，QQ空间给出的解决方案就是，准备一个 Hack 类，这个类会单独打包成一个 hack.dex，然后在所有的类的构造方法中增加这样的代码：

```
if (ClassVerifier.PREVENT_VERIFY) {
   System.out.println(Hack.class);
}
```

这样在 odex 过程中，每个类都会出现 Hack 在另一个dex文件中的问题，所以odex的验证过程也就不会继续下去，这样做牺牲了dvm对dex的优化效果了。

### 2.Tinker
修复前和修复后的apk分别定义为apk1和apk2，tinker自研了一套dex文件差分合并算法，在生成补丁包时，生成一个差分包 patch.dex，
后端下发patch.dex到客户端时，tinker会开一个线程把旧apk的class.dex和patch.dex合并，生成新的class.dex并存放在本地目录上，
重新启动时，会使用本地新生成的class.dex对应的elements替换原有的elements数组。

资源修复：新建 AssertManager，通过 addAssetPath 函数，加入外部的资源路径，然后将 Resources 的 mAssets 的字段设为新的 AssertManager，
这样在通过 getResources 去获取资源的时候就可以获取到我们外部的资源了。

### 3.Robust
1. 打基础包时插桩，在每个方法前插入一段类型为 ChangeQuickRedirect 静态变量的逻辑；
2. 加载补丁时，从补丁包中读取要替换的类及具体替换的方法实现，新建 ClassLoader 加载补丁dex。
找到补丁对应的 class，通过反射将 ChangeQuickRedirect 静态变量赋值为补丁中的实现，从而代理方法的实现。

```
// 插桩后的源码 State
public static ChangeQuickRedirect changeQuickRedirect;
public long getIndex() {
    if(changeQuickRedirect != null) {
        //PatchProxy中封装了获取当前className和methodName的逻辑，并在其内部最终调用了changeQuickRedirect的对应函数
        if(PatchProxy.isSupport(new Object[0], this, changeQuickRedirect, false)) {
            return ((Long)PatchProxy.accessDispatch(new Object[0], this, changeQuickRedirect, false)).longValue();
        }
    }
    return 100L;
}
```

```
// 补丁类 StatePatch
public class StatePatch implements ChangeQuickRedirect {
    @Override
    public Object accessDispatch(String methodSignature, Object[] paramArrayOfObject) {
        String[] signature = methodSignature.split(":");
        // 混淆后的 getIndex 方法 对应 a
        if (TextUtils.equals(signature[1], "a")) {//long getIndex() -> a
            return 106;
        }
        return null;
    }

    @Override
    public boolean isSupport(String methodSignature, Object[] paramArrayOfObject) {
        String[] signature = methodSignature.split(":");
        if (TextUtils.equals(signature[1], "a")) {//long getIndex() -> a
            return true;
        }
        return false;
    }
}
```


## 16. 插件化方案
### VirtualApk
1. Activity：在宿主apk中提前占几个坑，然后通过“欺上瞒下”的方式，启动插件apk的Activity；
因为要支持不同的launchMode以及一些特殊的属性，需要占多个坑。
2. Service：通过代理Service的方式去分发；主进程和其他进程，VirtualAPK使用了两个代理Service。
3. BroadcastReceiver：静态转动态。
4. ContentProvider：通过一个代理Provider进行分发。

### 资源id冲突如何解决
1. 修改aapt源码，定制aapt工具，编译期间修改PP段。(PP字段是资源id的第一个字节，表示包空间)<br>
DynamicAPK的做法就是如此，定制aapt，替换google的原始aapt，在编译的时候可以传入参数修改PP段：例如传入0x05编译得到的资源的PP段就是0x05。
2. 修改aapt的产物，即编译后期重新整理插件Apk的资源，编排ID。<br>
VirtualApk采用的就是这个方案。


## 17. Kotlin
### 协程
协程是轻量级的线程，它基于线程池API。相比较 RxJava，协程可以使用阻塞的方式写出非阻塞式的代码，解决并发中常见的回调地狱，这是其最大的优点。

[即学即用Kotlin - 协程](https://juejin.im/post/6854573211418361864)


## 18. Android Jetpack
### Lifecycle
1. Activity中调用LifecycleRegistry的addObserver，传入一个LifecycleObserver
2. 传入的LifecycleObserver被封装成一个ObserverWithState存入集合中，当生命周期发生改变的时候，
就会遍历这个ObserverWithState集合，并且调用ObserverWithState的dispatchEvent进行分发
3. 在ObserverWithState构造方法中，调用了Lifecycling.getCallback(observer)生成了具体的 GenericLifecycleObserver对象返回。
在ObserverWithState的dispatchEvent()方法中调用了GenericLifecycleObserver对象的onStateChanged方法进行事件分发
4. 在 ComponentActivity 的 onCreate 时添加 ReportFragment，在 ReportFragment 中对 Activity 的声明周期进行分发

[Android 官方架构组件（一）——Lifecycle](https://juejin.im/post/6844903748448288781)

### ViewModel
1. ViewModel 存储在 Activity 的 NonConfigurationInstances 对象中，该对象用来保存 Activity 重建的数据。
2. 不持有 UI 引用，不会造成内存泄漏。


## 19. 开源库
### LeakCanary的核心原理
1. 通过 registerActivityLifecycleCallbacks() 监听各个 Activity 的 onDestroy 方法
2. Activity 退出后，拿到 Activity 的对象封装成 WeakReference 弱引用对象，
配合 ReferenceQueue，如果对象被回收，JVM 就会把弱引用存入与之关联的引用队列之中
3. 通过手动 Runtime.getRuntime().gc() 垃圾回收
4. 根据 ReferenceQueue 是否有值来判断对象是否被回收，如果有值，说明 Activity 已经被回收，没有泄露
5. 如果没有移除，通过 android 原生接口 Debug.dumpHprofData()，把 Hprof 文件搞下来，通过 haha 这个第三方库去解析是否有指定 Activity 的残留

[EventBus源码详解](https://juejin.im/post/6881265680465788936)

### Glide

[Android glide使用过程中遇到的坑(进阶篇)](https://www.jianshu.com/p/deccde405e04)

### AndResGuard
1. 生成新的资源文件目录，里面对资源文件路径进行混淆(其中涉及如何复用旧的mapping文件)，例如将res/drawable/hello.png混淆为r/s/a.png，并将映射关系输出到mapping文件中。
2. 对资源id进行混淆(其中涉及如何复用旧的mapping文件)，并将映射关系输出到mapping文件中。
3. 生成新的resources.arsc文件，里面对资源项值字符串池、资源项key字符串池进行混淆替换，对资源项entry中引用的资源项字符串池位置进行修正、并更改相应大小，并打包生成新的apk。
