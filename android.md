# Android要点


## 1. Activity的生命周期，启动模式有哪些？

Activity 生命周期

![](activity_lifecycle.jpg)

Activity启动模式

standard：每次激活Activity时(startActivity)，都创建Activity实例，并放入任务栈；

singleTop：如果某个Activity自己激活自己，即任务栈栈顶就是该Activity，则不需要创建，其余情况都要创建Activity实例；

singleTask：如果要激活的那个Activity在任务栈中存在该实例，则不需要创建，只需要把此Activity放入栈顶，即把该Activity以上的Activity实例都pop，并调用其onNewIntent；

singleInstance：应用1的任务栈中创建了MainActivity实例，如果应用2也要激活 MainActivity，则不需要创建，两应用共享该Activity实例。

onSaveInstanceState的调用遵循一个重要原则，即当系统“未经你许可”时销毁了你的 activity，则onSaveInstanceState会被系统调用，这是系统的责任，因为它必须要提供一个机会让你保存你的数据。至于onRestoreInstanceState方法，需要注意的是， onSaveInstanceState方法和onRestoreInstanceState方法“不一定”是成对的被调用的。

onRestoreInstanceState被调用的前提是，activity A“确实”被系统销毁了，而如果仅仅 是停留在有这种可能性的情况下，则该方法不会被调用，例如，当正在显示activity A的时 候，用户按下HOME键回到主界面，然后用户紧接着又返回到activity A，这种情况下 activity A一般不会因为内存的原因被系统销毁，故activity A的onRestoreInstanceState方法不会被执行。

另外，onRestoreInstanceState的bundle参数也会传递到onCreate方法中，你也可以选择 在onCreate方法中做数据还原。


## 2. Touch事件传递机制

1.事件从Activity.dispatchTouchEvent()开始传递，只要没有被停止或拦截，从最上层的 View(ViewGroup)开始一直往下(子View)传递。子 View 可以通过 onTouchEvent()对事 件进行处理。

2.事件由父 View(ViewGroup)传递给子View，ViewGroup 可以通过 onInterceptTouchEvent()对事件做拦截，停止其往下传递。

3.如果事件从上往下传递过程中一直没有被停止，且最底层子 View 没有消费事件，事件会 反向往上传递，这时父 View(ViewGroup)可以进行消费，如果还是没有被消费的话，最后会到 Activity 的 onTouchEvent()函数。

4.如果 View 没有对 ACTION_DOWN 进行消费，之后的其他事件不会传递过来。

5.OnTouchListener 优先于 onTouchEvent()对事件进行消费。

上面的消费即表示相应函数返回值为 true。 

![View不处理事件流程图](touch_event_not_consume)

![View处理事件流程图](touch_event_consume)


## 3. Looper和Handler的关系

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

AsyncTask和Handler对比

1.AsyncTask,是android提供的轻量级的异步类,可以直接继承AsyncTask,在类中实现异步操作,并提供接口反馈当前异步执行的程度(可以通过接口实现UI进度更新),最后反馈执行的结果给UI主线程.

优点:1.简单,快捷 2.过程可控缺点:在使用多个异步操作和并进行UI变更时,就变得复杂起来.

2.Handler在异步实现时,涉及到Handler,Looper,Message,Thread四个对象，实现异步的流程是主线程启动Thread(子线程),Thread(子线程)运行并生成Message,Looper获取 Message并传递给Handler,Handler逐个获取Looper中的Message,并进行UI变更。

优点：1.结构清晰，功能定义明确 2.对于多个后台任务时，简单，清晰缺点：在单个后台异步处理时，显得代码过多，结构过于复杂（相对性）


## 4. Activity启动过程

1.无论是通过Launcher来启动Activity，还是通过Activity内部调用startActivity接口来启动新的Activity，都通过Binder进程间通信进入到ActivityManagerService进程中，并且调用 ActivityManagerService.startActivity接口；

2.ActivityManagerService调用ActivityStack.startActivityMayWait来做准备要启动的Activity的相关信息；

3.ActivityStack通知ApplicationThread要进行Activity启动调度了，这里的ApplicationThread代表的是调用ActivityManagerService.startActivity接口的进程，对于通过点击应用程序图标的情景来说，这个进程就是Launcher了，而对于通过在Activity内部调用startActivity的情景来说，这个进程就是这个Activity所在的进程了；

4.ApplicationThread不执行真正的启动操作，它通过调用.activityPaused接口进入到ActivityManagerService进程中，看看是否需要创建新的进程来启动Activity；

5.对于通过点击应用程序图标来启动Activity的情景来说，ActivityManagerService在这一步中，会调用startProcessLocked来创建一个新的进程，而对于通过在Activity内部调用startActivity来启动新的Activity来说，这一步是不需要执行的，因为新的Activity就在原来的Activity所在的进程中进行启动；

6.ActivityManagerServic调用ApplicationThread.scheduleLaunchActivity接口，通知相应的进程执行启动Activity的操作；

7.ApplicationThread把这个启动Activity的操作转发给ActivityThread，ActivityThread通过ClassLoader导入相应的Activity类，然后把它启动起来。

Zygote的启动过程

1.系统启动时init进程会创建Zygote进程，Zygote进程负责后续Android应用程序框架层的其它进程的创建和启动工作。

2.Zygote进程会首先创建一个SystemServer进程，SystemServer进程负责启动系统的关键服务，如包管理服务PackageManagerService和应用程序组件管理服务ActivityManagerService。

3.当我们需要启动一个Android应用程序时，ActivityManagerService会通过Socket进程间通信机制，通知Zygote进程为这个应用程序创建一个新的进程。


## 5. View绘制流程

View的绘制主要涉及三个方法:onMeasure()、onLayout()和onDraw()。

1.onMeasure主要用于计算view的大小，onLayout主要用于确定view在ContentView中的位置，onDraw主要是绘制view;

2.在执行onMeasure()，onLayout()方法时都会先通过相应的标志位或者对应的坐标点来判断是否需要执行对应的函数，如我们经常调用的invalidate方法就只会执行onDraw方法，因为此时的视图大小和位置均未发生改变，除非调用requestLayout方法完整强制进行view 的绘制，从而执行上面三个方法。


## 6. ANR问题

ANR一般有三种类型：

1.KeyDispatchTimeout(5 seconds)

主要类型按键或触摸事件在特定时间内无响应

2.BroadcastTimeout(10 seconds)

BroadcastReceiver在特定时间内无法处理完成

3.ServiceTimeout(20 seconds)

小概率类型 Service在特定的时间内无法处理完成

如何避免ANR

1.UI线程尽量只做跟UI相关的工作

2.耗时的工作（比如数据库操作，I/O，连接网络或者别的有可能阻碍UI线程的操作）把它放入单独的线程处理

3.尽量用Handler来处理UIThread和WorkThread之间的交互

4.BroadCastReceiver要进行复杂操作的的时候，可以在onReceive()方法中启动一个 Service来处理。

画龙点睛

通常100到200毫秒就会让人察觉程序反应慢，为了更加提升响应，可以使用下面的两种方法

1.如果程序正在后台处理用户的输入，建议使用让用户得知进度，比如使用ProgressBar控件。

2.程序启动时可以选择加上欢迎界面，避免让用户察觉卡顿。


## 7. OOM问题

1.应用中需要加载大对象，例如Bitmap

解决方案：当我们需要显示大的bitmap对象或者较多的bitmap的时候，就需要进行压缩来防止OOM问题。我们可以通过设置BitmapFactory.Options的inJustDecodeBounds属性为true，这样的话不会加载图片到内存中，但是会将图片的width和height属性读取出来，我们可以利用这个属性来对bitmap进行压缩。Options.inSampleSize 可以设置压缩比。

2.持有无用的对象使其无法被gc，导致Memory Leak。

2.1静态变量导致的Memory leak

静态变量的生命周期和类是息息相关的，它们分配在方法区上，垃圾回收一般不会回收这一块的内存。所以我们在代码中用到静态对象，在不用的时候如果不赋null值，消除对象的引用的话，那么这些对象是很难被垃圾回收的，如果这些对象一多或者比较大的话，程序出现OOM的概率就比较大了。因为静态变量而出现内存泄漏是很常见的。

2.2不合理使用Context 导致的Memory leak

Android中很多地方都要用到context,连基本的Activty和Service都是从Context派生出来的，我们利用Context主要用来加载资源或者初始化组件，在Activity中有些地方需要用到Context的时候，我们经常会把context给传递过去了，将context传递出去就有可能延长了context的生命周期，最终导致了内存泄漏。对于android应用来说还有一个单例的Application Context对象，该对象生命周期和应用的生命周期是绑定的。选择context应该考虑到它的生命周期，如果使用该context的组件的生命周期超过该context对象，那么我们就要考虑是否可以用Application context。如果真的需要用到该context对象，可以考虑用弱引用WeakReference来避免内存泄漏。

2.3非静态内部类导致的Memory leak

非静态的内部类会持有外部类的一个引用，所以和前面context说到的一样，如果该内部类生命周期超过外部类的生命周期，就可能引起内存泄露了，如AsyncTask和Handler。因为在Activity中我们可能会用到匿名内部类，所以要小心管理其生命周期。如果明确生命周期较外部类长的话，那么应该使用静态内部类。

2.4 Drawable对象的回调隐含的Memory leak

当我们为某一个view设置背景的时候，view会在drawable对象上注册一个回调，所以drawable对象就拥有了该view的引用了，进而对整个context都有了间接的引用了，如果该drawable对象没有管理好，例如设置为静态，那么就会导致Memory leak。

在手机版QQ中，对于图片的处理方法是先加载用户发送的图片的小图，只有用户点击了才去加载大图，这样即节省了流量也能防止bitmap过大造成OOM。另外，对于离开屏幕内容的图片，也要及时的回收，不然聊天记录一多也就OOM了。对于回收的图片，我们可以采用软引用来做缓存， 这样在内存不吃紧的情况下就能提高交互性,因为在Java中软引用在内存吃紧的时候才会被垃圾回收，比较适合用做cache.另外在Android 3.1版本起，官方还提供了LruCache来进行cache处理。对于不用的Bitmap对象，我们要及时回收，否则会造成Memory leak，所以当我们确定Bitmap对象不用的时候要及时调用Bitmap.recycle()方法来使它尽早被GC。

图片OOM优化

1.在内存引用上做些处理，常用的有软引用、弱引用

2.在内存中加载图片时直接在内存中做处理，如:边界压缩

3.动态回收内存

4.优化Dalvik虚拟机的堆内存分配

5.自定义堆内存大小


## 8. ListView优化

1.convertView

2.尽量让ItemView的Layout层次结构简单，这是所有Layout都必须遵循的

3.善用自定义View，自定义View可以有效的减小Layout的层级，而且对绘制过程可以很好的控制

4.为了保证ListView滑动的流畅性，getView()中要做尽量少的事情，不要有耗时的操作。

特别是滑动的时候不要加载图片，停下来再加载

5.使用RecycleView代替ListView


## 9. Android消息推送机制

几种常见的解决方案：

1.轮询(Pull)方式：应用程序应当阶段性的与服务器进行连接并查询是否有新的消息到达，你必须自己实现与服务器之间的通信，例如消息排队等。而且你还要考虑轮询的频率，如果太慢可能导致某些消息的延迟，如果太快，则会大量消耗网络带宽和电池。

2.SMS(Push)方式：在Android平台上，你可以通过拦截SMS消息并且解析消息内容来了解服务器的意图，并获取其显示内容进行处理。这个方案的好处是，可以实现完全的实时操作。但是问题是这个方案的成本相对比较高。

3.持久连接(Push)方式：这个方案可以解决由轮询带来的性能问题，但是还是会消耗手机的电池。

百度云推送：

百度云推送的实现技术简单来说就是利用Socket维持Client和Server间的一个TCP长连接，通过这种方式能大大降低由轮询方式带来的Device的耗电量和数据访问流量。

**移动端获取网络数据优化的几个点

1.连接复用:节省连接建立时间，如开启keep-alive

对于Android来说默认情况下HttpURLConnection和HttpClient都开启了keep-alive。只是2.2之前HttpURLConnection存在影响连接池的Bug

2.请求合并:即将多个请求合并为一个进行请求，比较常见的就是网页中的CSSImage Sprites。如果某个页面内请求过多，也可以考虑做一定的请求合并

3.减少请求数据的大小:对于post请求，body可以做gzip压缩的，header也可以作数据压缩(不过只支持http 2.0)

4.返回的数据的body也可以作gzip压缩，body数据体积可以缩小到原来的30%左右。(也可以考虑压缩返回的json数据的key数据的体积，尤其是针对返回数据格式变化不大的情况，支付宝聊天返回的数据用到了)

5.根据用户的当前的网络质量来判断下载什么质量的图片(电商用的比较多)


## 10. 进程间通信

1.访问其他应用程序的Activity

2.Content Provider

3.Broadcast

4.AIDL服务

5.Messenger

Binder机制

1.Client, Server和Service Manager实现在用户空间中，Binder驱动程序实现在内核空间中

2.Binder驱动程序和Service Manager在Android平台中已经实现，开发者只需要在用户空间实现自己的Client和Server

3.Binder驱动程序提供设备文件/dev/binder与用户空间交互，Client, Server和ServiceManager通过 open和ioctl文件操作函数与Binder驱动程序进行通信

4.Client和Server之间的进程间通信通过Binder驱动程序间接实现

5.Service Manager是一个守护进程，用来管理Server，并向Client提供查询Server接口的能力


## 11. Scroller原理

Scroller执行流程里面的三个核心方法 mScroller.startScroll() mScroller.computeScrollOffset() view.computeScroll()

1.在mScroller.startScroll()中为滑动做了一些初始化准备。

比如:起始坐标，滑动的距离和方向以及持续时间(有默认值)，动画开始时间等

2.mScroller.computeScrollOffset()方法主要是根据当前已经消逝的时间来计算当前的坐标点。

因为在mScroller.startScroll()中设置了动画时间，那么在computeScrollOffset()方法中依据已经消逝的时间就很容易得到当前时刻应该所处的位置并将其保存在变量mCurrX和 mCurrY中。除此之外该方法还可判断动画是否已经结束。


## 12. SurfaceView和View的区别

SurfaceView在新的线程中更新画面，而View必须在UI线程中更新画面。

在UI线程中更新画面可能会引发问题，比如你更新画面的时间过长，那么你的主UI线程会被你正在画的函数阻塞。那么将无法响应按键，触屏等消息。SurfaceView由于是在新的线程中更新画面所以不会阻塞UI线程。但这也带来了另外一个问题，就是事件同步。比如你触屏了一下，你需要SurfaceView中thread处理，一般就需要有一个event queue的设计来保存 touch event，这会稍稍复杂一点，因为涉及到线程同步。


## 13. <include> <merge> <ViewStub>标签

简言之:<include> <merge>都是用来解决重复布局的问题，但是merge标签能够在布局重用的时候减少UI层级结构。

ViewStub标签是用来给其他的view事先占据好位置，当需要的时候调用inflater()或者是 setVisible()方法显示这些View。
