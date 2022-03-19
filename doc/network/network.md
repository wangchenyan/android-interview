# 计算机网络要点

### 1. TCP/IP

![](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/network/image/tcp_ip.jpg)

1. 网络层：负责相邻计算机之间的通信。

- IP(Internet Protocol)协议
- ICMP(Internet Control Message Protocol)控制报文协议
- ARP(Address Resolution Protocol)地址转换协议
- RARP(Reverse ARP)反向地址转换协议

2. 传输层：提供应用程序间的通信。

- 传输控制协议TCP(Transmission Control Protocol)
  TCP协议是一种可靠的、面向连接的协议，保证通信主机之间有可靠的字节流传输，完成流量控制功能，协调收发双方的发送与接收速度，达到正确传输的目的。
- 用户数据报协议UDP(User Datagram protocol)
  UDP是一种不可靠、无连接的协议，其特点是协议简单、额外开销小、效率较高，但是不能保证传输是否正确。

3. 应用层：向用户提供一组常用的应用程序，比如电子邮件、文件传输访问、远程登录等。

- FTP(File Transfer Protocol)是文件传输协议，一般上传下载用FTP服务。
- Telnet服务是用户远程登录服务，使用明码传送，保密性差、简单方便。
- DNS(Domain Name Service)是域名解析服务，提供域名到IP地址之间的转换。
- SMTP(Simple Mail Transfer Protocol)是简单邮件传输协议，用来控制信件的发送、中转。
- NFS（Network File System)是网络文件系统，用于网络中不同主机间的文件共享。
- HTTP(Hypertext Transfer Protocol)是超文本传输协议，用于实现互联网中的WWW服务。

### TCP如何保证可靠性

1. 将数据截断为合理的长度。<br>
   应用数据被分割成TCP认为最适合发送的数据块。这和UDP完全不同，应用程序产生的数据报长度将保持不变。
2. 超时重发。<br>
   当TCP发出一个段后，它启动一个定时器，等待目的端确认收到这个报文段。如果不能及时收到一个确认，将重发这个报文段。
3. 对于收到的请求，给出确认响应。<br>
   当TCP收到发自TCP连接另一端的数据，它将发送一个确认。这个确认不是立即发送，通常将推迟几分之一秒。
4. TCP将保持它首部和数据的检验和。<br>
   这是一个端到端的检验和，目的是检测数据在传输过程中的任何变化。如果收到段的检验和有差错，TCP将丢弃这个报文段和不确认收到此报文段。
5. 对失序数据进行重新排序，然后才交给应用层。<br>
   既然TCP报文段作为IP数据报来传输，而IP数据报的到达可能会失序，因此TCP报文段的到达也可能会失序。 如果必要，TCP将对收到的数据进行重新排序，将收到的数据以正确的顺序交给应用层。
6. 对于重复数据，能够丢弃重复数据。<br>
   既然IP数据报会发生重复，TCP的接收端必须丢弃重复的数据。
7. TCP可以进行流量控制，防止较快主机致使较慢主机的缓冲区溢出<br>
   TCP连接的每一方都有固定大小的缓冲空间。TCP的接收端只允许另一端发送接收端缓冲区所能接纳的数据。 这将防止较快主机致使较慢主机的缓冲区溢出。TCP使用的流量控制协议是可变大小的滑动窗口协议。

## 2. HTTP

HTTP请求包含的内容：1.请求行2.请求头3.请求体

第一部分请求行写法是固定的，由三部分组成，第一部分是请求方法，第二部分是请求网址，第三部分是HTTP版本。

第二部分HTTP头在HTTP请求可以是3种HTTP头:1.请求头(request header) 2.普通头(general header) 3.实体头(entity header)。
通常来说，由于Get请求往往不包含内容实体，因此也不会有实体头。

第三部分内容只在POST请求中存在，因为GET请求并不包含任何实体。

HTTP响应包含的内容：1.响应行2.响应头3.响应体

第一部分包括HTTP版本、响应状态码、状态码的描述。

1xx信息类 2xx响应成功 3xx重定向类 4xx客户端错误类 5xx服务端错误类

第二部分包含的头包括：1.响应头(response header) 2.普通头(general header)  3.实体头(entity header)。

第三部分HTTP响应内容就是HTTP请求所请求的信息。这个信息可以是一个HTML，也可以是一个图片。

### HTTP 2.0 与 HTTP 1.1 的区别

1. 新的二进制格式（Binary Format）<br>
   HTTP1.x的解析是基于文本。基于文本协议的格式解析存在天然缺陷，文本的表现形式有多样性，要做到健壮性考虑的场景必然很多，二进制则不同，只认0和1的组合。基于这种考虑HTTP2.0的协议解析决定采用二进制格式，实现方便且健壮。
2. 多路复用（MultiPlexing）<br>
   即连接共享，即每一个request都是是用作连接共享机制的。一个request对应一个id，这样一个连接上可以有多个request，每个连接的request可以随机的混杂在一起，接收方可以根据request的
   id将request再归属到各自不同的服务端请求里面。
3. header压缩<br>
   HTTP1.x的header带有大量信息，而且每次都要重复发送，HTTP2.0使用encoder来减少需要传输的header大小，通讯双方各自cache一份header
   fields表，既避免了重复header的传输，又减小了需要传输的大小。
4. 服务端推送（server push）<br>
   同SPDY一样，HTTP2.0也具有server push功能。

### HttpClient两个超时时间

1. 连接超时 connectionTimeout：指的是连接一个url的连接等待时间
2. 读取数据超时 soTimeout：指的是连接上一个url，获取response的返回等待时间

### 浏览器输入一个url到服务器处理整个过程

1. 浏览器向DNS服务器请求解析该URL中的域名所对应的IP地址；
2. 解析出IP地址后，根据该IP地址和默认端口80，和服务器建立TCP连接；
3. 浏览器发出HTTP请求，该请求报文作为TCP三次握手的第三个报文的数据发送给服务器；
4. 服务器把对应的html文本发送给浏览器；
5. 释放TCP连接；
6. 浏览器将该文本显示出来。

### Http与Https的区别：

1. Https = Http + SSL + 加密算法 + 证书验证
2. Http使用80端口，Https使用443端口

### HTTPS加密过程

1. 访问HTTPS网站，服务端下发公钥。
2. 客户端向证书服务器验证公钥，然后生成一个串随机AES_128密码（假如是用AES加密），并把这个密码用刚才那个公钥加密，发给服务端。
3. 服务端用私钥解密浏览器发送的数据，得到浏览器随机生成的AES_128密码，并把网页内容全部用AES_128加密器起来，返回给浏览器。
4. 浏览器用刚刚的AES_128密码解密服务器返回的数据，得到可读的内容。
5. 之后发出的请求数据，也是用AES_128密码来加密。

https://www.zhihu.com/question/28617156/answer/169262169

## 3. Session与Cookie的区别

1. Session保存在服务器，客户端不知道其中的信息；Cookie保存在客户端，服务器能够知道其中的信息。
2. Session中保存的是对象，Cookie中保存的是字符串。
3. Session不能区分路径，同一个用户在访问一个网站期间，所有的Session在任何一个地方都可以访问到。
   而Cookie中如果设置了路径参数，那么同一个网站中不同路径下的Cookie互相是访问不到的。
4. Session需要借助Cookie才能正常工作。如果客户端完全禁止Cookie，Session将失效。

## 4. 三次握手四次挥手

### 三次握手

1. 建立连接时，客户端发送syn包到服务器，并进入SYN_SENT状态，等待服务器确认；SYN：同步序列编号（Synchronize Sequence Numbers）。
2. 服务器收到syn包，必须确认客户的SYN，同时自己也发送一个SYN包，即SYN+ACK 包，此时服务器进入SYN_RECV状态；
3. 客户端收到服务器的SYN+ACK包，向服务器发送确认包ACK，此包发送完毕，客户端和服务器进入ESTABLISHED（TCP连接成功）状态，完成三次握手。

### 四次挥手

1. 客户端发送一个FIN，用来关闭客户端到服务器的数据传送。
2. 服务器收到这个FIN，它发回一个ACK，确认序号为收到的序号加1。客户端进入FIN_WAIT状态。
3. 服务器准备关闭客户端的连接，发送一个FIN给客户端。
4. 客户端发回ACK报文确认，并进入TIME_WAIT状态，若2MSL（报文最大生存时间）后依然没有收到回复则关闭连接。

## 5. TCP滑动窗口协议

![](https://raw.githubusercontent.com/wangchenyan/android-interview/master/doc/network/image/sliding_window.jpg)

1. 首先是AB之间三次握手建立TCP连接。在报文的交互过程中，A将自己的缓冲区大小（窗口大小）3发送给B，B同理，这样双方就知道了对端的窗口大小。
2. A开始发送数据，A连续发送3个单位的数据，因为他知道B的缓冲区大小。在这一波数据发送完后，A就不能再发了，需等待B的确认。
3. A发送过来的数据逐渐将缓冲区填满。
4. 这时候缓冲区中的一个报文被进程读取，缓冲区有了一个空位，于是B向A发送一个ACK，这个报文中指示窗口大小为1。
   A收到B发过来的ACK消息，并且知道B将窗口大小调整为1，因此他只发送了一个单位的数据并且等待B的下一个确认报文。
5. 如此反复。
