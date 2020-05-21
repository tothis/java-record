**java线程状态分为6种 在thread内部枚举可以体现java.lang.Thread.State**
1. 新建(new) 新创建一个线程对象 未调用start方法
2. 运行(runnable) 就绪ready和运行中running两种状态统称为运行状态
调用该对象start方法后 被线程调度选中 获取cpu使用权后 处于就绪状态 获得cpu时间片后处于运行状态
3. 阻塞(blocked) 等待获取一个锁
4. 等待(waiting) 处于该状态线程等待其他线程做出一些特定动作 通知或中断
5. 超时等待(timed_waiting) 在指定时间内进入就绪状态
6. 终止(terminated) 执行完毕
***
线程生命周期为五个阶段

+ new(新建状态) 只新建线程对象 未调用start方法

+ runnable(就绪状态) 已调用start方法 并创建一个线程 并未执行 等待cpu调度
    + runnable状态只会意外终止或进人running状态

+ running(运行状态) 已被cpu调度执行
    + running状态可转化状态 
        + 进人terminated状态 如调用jdk已废弃stop方法
        + 进人blocked状态 线程会被加入waitset中 如
            1. 调用sleep或wait方法
            2. 获取某个锁资源
        + 进人runnable状态
            1. cpu的调度器轮询使该线程放弃执行 
            2. 线程主动调用yield方法 放弃cpu执行权
+ blocked(阻塞状态)
    + blocked状态可转化状态
    + 进人terminated状态 如调用jdk已废弃stop方法或意外死亡(jvm crash)
    + 进人runnable状态
        1. 线程阻塞结束
        2. 线程休眠结束
        3. 被其他线程notify/notifyAll唤醒
        4. 线程获取到锁
        5. 线程阻塞时被打断 如其他线程调用interrupt方法
terminated(死亡状态) 线程运行结束 不会再切换到其它状态