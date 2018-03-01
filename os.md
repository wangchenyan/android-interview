# 操作系统要点

## 1. 进程与线程

进程是具有一定独立功能的程序关于某个数据集合上的一次运行活动,进程是系统进行资源分配和调度的一个独立单位。

线程是进程的一个实体,是CPU调度和分派的基本单位,它是比进程更小的能独立运行的基本单位。

**进程和线程的关系：**

(1)一个线程只能属于一个进程，而一个进程可以有多个线程，但至少有一个线程。

(2)资源分配给进程，同一进程的所有线程共享该进程的所有资源。

(3)CPU分给线程，即真正在CPU上运行的是线程。

(4)线程在执行过程中，需要协作同步。不同进程的线程间要利用消息通信的办法实现同步。

**进程与线程的区别：**

(1)调度：线程作为调度和分配的基本单位，进程作为拥有资源的基本单位

(2)并发性：不仅进程之间可以并发执行，同一个进程的多个线程之间也可并发执行

(3)拥有资源：进程是拥有资源的一个独立单位，线程不拥有系统资源，但可以访问隶属于进程的资源.

(4)系统开销：在创建或撤消进程时，由于系统都要为之分配和回收资源，导致系统的开销明显大于创建或撤消线程时的开销。但是进程有独立的地址空间，一个进程崩溃后，在保护模式下不会对其它进程产生影响，而线程只是一个进程中的不同执行路径。线程有自己的堆栈和局部变量，但线程之间没有单独的地址空间，一个进程死掉就等于所有的线程死掉，所以多进程的程序要比多线程的程序健壮，但在进程切换时，耗费资源较大，效率要差一些

结论：

(1)线程是进程的一部分

(2)CPU调度的是线程

(3)系统为进程分配资源，不对线程分配资源

进程的三种状态：就绪、运行、阻塞

线程的五种状态：新建、就绪、运行、阻塞、死亡

**进程调度算法：**

1.先来先服务(FCFS)

按照请求次序进行调度。实现简单不用对队列进行调整，但是可能因某个元素处理时间过长，出现等待超时。

2.短作业优先（SJF）

按照作业执行的消耗时间，消耗最短时间的先执行。

需要预先估计作业执行时间，耗时较长的作业可能一直得不到执行。

3.基于优先权的调度算法（FPPS）

给作业赋予一定优先权，每次从最高优先权的元素中取出一个并执行。

4.时间片轮转（RR）

给每个作业赋予一个运行时间片，当时间片到则取下一个元素。

是对FCFS和SJF算法的折衷，不会出现超时。


## 2. 常用SQL语句

1.数据定义

```
Create table sc(sno char(9),cno char(4),grade smallint,primary key(sno,cno),froeign key(sno) references student(sno),foreign key(cno) references course(cno));
Create view is_student as select sno,sname,sage from student where sdept='IS';
Drop table sc;
Drop view is_student;
```
2.数据查询

2.1字符匹配

%代表任意长度的字符串，_代表任意单个字符串。

```
Select * from student where sname like '王%';
```

2.2ORDER BY

升序(ASC) 降序(DESC)

2.3聚集函数

COUNT SUM AVG MAX MIN

2.4WHERE与HAVING的区别

WHERE字句与HAVING短语的区别在于作用对象不同，WHERE字句作用于基本表或视图，从中选择满足条件的元组。HAVING短语作用于组，从中选择满足条件的组。

```
Select sno from sc group by sno having count(*)>3;
```

2.5嵌套查询

(NOT)IN, >, >ANY, >ALL, !=ANY, (NOT)EXISTS

查询选修了全部课程的学生姓名（没有一门课是他不选的）

```
Select sname from student where not exists(select * from course where not exists(select * from sc where sno=student.sno and cno=course.cno));
```

2.6limit的使用

查询课程成绩排名第三的学生

```
Select sno,grade from sc order by grade desc limit 2,1;
```

3.数据更新

```
Insert into student(sno,sname) values('200215128','陈冬');
Update student set sage=22 where sno='200215121';
Delete from student where sno='200215128';
```

4.触发器

```
Create trigger t_student after insert on student for each row insert into course values(new.sno,new.sname);
Drop trigger t_student;
```