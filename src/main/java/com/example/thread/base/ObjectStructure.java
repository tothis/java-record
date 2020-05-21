package com.example.thread.base;

import org.openjdk.jol.info.ClassLayout;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author 李磊
 * @time 2019/11/10 23:15
 * @description 对象结构
 */
public class ObjectStructure {

    public static int hashCode(Object object) {
        // 手动计算hashCode
        Unsafe unsafe = null;
        try {
            // 获取java对象头
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int hashCode = 0;
        for (long index = 7; index > 0; index--) {
            // 获取mark word中的每一个byte进行计算
            hashCode |= (unsafe.getByte(object, index) & 0XFF) << ((index - 1) * 8);
        }
        return hashCode;
    }

    public static void main(String[] args) {
        ObjectStructure o = new ObjectStructure();
        /**
         * 64位jvm分配对象大小必须为8的倍数
         * 1.对象头 96bit -> 12byte
         * 2.实例数据
         * 3.填充对其 如上数据相加不为8的倍数 通过填充对其
         */
        // hashCode默认不会计算
        System.out.println(Integer.toHexString(o.hashCode()));
        System.out.println(o.hashCode());
        System.out.println(hashCode(o));
        System.out.println(ClassLayout.parseInstance(o).toPrintable());

        /**
         * java对象头有mark word和klass pointer/class metadata address
         * java对象的hashCode windows环境为小端存储获取后八个字节
         */

        /**
         * java对象有5种状态
         * 1.无状态 new出来的时候
         * 2.偏向锁 只有一个线程持有此对象
         * 3.轻量锁
         * 4.重量锁 如synchronized
         * 5.gc标记 方法结束后对象引用和堆种的对象被gc标记
         */

        /**
         * 64位比32位性能略有降低 64位JVM可以寻址更大的内存空间 但是这会带来一点性能上的损耗 同样的应用 从32位SPARC迁移到64位SPARC上会带来10-20%的性能损耗  在AMD64和EM64T系统上 大概是0-15%的损耗 这取决于应用里面指针的数量
         * 32位JVM的寻址空间只有2^32(4G) 也就是说你的进程最大只能使用4G内存 并且由于还存在其他的一些限制 比如说swap空间 内核空间占用 内存碎片等等 实际上jvm可利用的内存要远小于4G 如果是64位JVM 寻址空间最大是2^64
         */

//        1.对象头形式
//        JVM中对象头的方式有以下两种(以32位JVM为例)
//        1.1.普通对象
//        |--------------------------------------------------------------|
//        |                     Object Header (64 bits)                  |
//        |------------------------------------|-------------------------|
//        |        Mark Word (32 bits)         |    Klass Word (32 bits) |
//        |------------------------------------|-------------------------|
//        1.2.数组对象
//        |---------------------------------------------------------------------------------|
//        |                                 Object Header (96 bits)                         |
//        |--------------------------------|-----------------------|------------------------|
//        |        Mark Word(32bits)       |    Klass Word(32bits) |  array length(32bits)  |
//        |--------------------------------|-----------------------|------------------------|
//        2.对象头的组成
//        2.1.Mark Word
//        这部分主要用来存储对象自身的运行时数据 如hashcode gc分代年龄等
//        mark word的位长度为JVM的一个Word大小 也就是说32位JVM的Mark word为32位 64位JVM为64位
//        为了让一个字大小存储更多的信息 JVM将字的最低两个位设置为标记位 不同标记位下的Mark Word示意如下
//        |-------------------------------------------------------|--------------------|
//        |                  Mark Word (32 bits)                  |       State        |
//        |-------------------------------------------------------|--------------------|
//        | identity_hashcode:25 | age:4 | biased_lock:1 | lock:2 |       Normal       |
//        |-------------------------------------------------------|--------------------|
//        |  thread:23 | epoch:2 | age:4 | biased_lock:1 | lock:2 |       Biased       |
//        |-------------------------------------------------------|--------------------|
//        |               ptr_to_lock_record:30          | lock:2 | Lightweight Locked |
//        |-------------------------------------------------------|--------------------|
//        |               ptr_to_heavyweight_monitor:30  | lock:2 | Heavyweight Locked |
//        |-------------------------------------------------------|--------------------|
//        |                                              | lock:2 |    Marked for GC   |
//        |-------------------------------------------------------|--------------------|
//        其中各部分的含义如下
//        lock:2位的锁状态标记位 由于希望用尽可能少的二进制位表示尽可能多的信息
//        所以设置了lock标记该标记的值不同 整个mark word表示的含义不同
//        biased_lock	lock	状态
//        0	01	无锁
//        1	01	偏向锁
//        0	00	轻量级锁
//        0	10	重量级锁
//        0	11	GC标记
//        biased_lock 对象是否启用偏向锁标记 只占1个二进制位 为1时表示对象启用偏向锁 为0时表示对象没有偏向锁
//        age 4位的Java对象年龄 在GC中 如果对象在Survivor区复制一次 年龄增加1 当对象达到设定的阈值时
//        将会晋升到老年代 默认情况下 并行GC的年龄阈值为15 并发GC的年龄阈值为6 由于age只有4位 所以最大值为15
//        这就是-XX:MaxTenuringThreshold选项最大值为15的原因 identity_hashcode 25位的对象标识Hash码 采用延迟加载技术
//        调用方法System.identityHashCode()计算 并会将结果写到该对象头中 当对象被锁定时 该值会移动到管程Monitor中
//        thread 持有偏向锁的线程ID
//        epoch 偏向时间戳
//        ptr_to_lock_record 指向栈中锁记录的指针
//        ptr_to_heavyweight_monitor 指向管程Monitor的指针
//
//        64位下的标记字与32位的相似 不再赘述
//        |------------------------------------------------------------------------------|--------------------|
//        |                                  Mark Word (64 bits)                         |       State        |
//        |------------------------------------------------------------------------------|--------------------|
//        | unused:25 | identity_hashcode:31 | unused:1 | age:4 | biased_lock:1 | lock:2 |       Normal       |
//        |------------------------------------------------------------------------------|--------------------|
//        | thread:54 |       epoch:2        | unused:1 | age:4 | biased_lock:1 | lock:2 |       Biased       |
//        |------------------------------------------------------------------------------|--------------------|
//        |                       ptr_to_lock_record:62                         | lock:2 | Lightweight Locked |
//        |------------------------------------------------------------------------------|--------------------|
//        |                     ptr_to_heavyweight_monitor:62                   | lock:2 | Heavyweight Locked |
//        |------------------------------------------------------------------------------|--------------------|
//        |                                                                     | lock:2 |    Marked for GC   |
//        |------------------------------------------------------------------------------|--------------------|
//        2.2.class pointer
//        这一部分用于存储对象的类型指针 该指针指向它的类元数据 JVM通过这个指针确定对象是哪个类的实例 该指针的位长度为JVM的一个字大小 即32位的JVM为32位 64位的JVM为64位
//        如果应用的对象过多 使用64位的指针将浪费大量内存 统计而言 64位的JVM将会比32位的JVM多耗费50%的内存 为了节约内存可以使用选项+UseCompressedOops开启指针压缩
//        其中oop即ordinary object pointer普通对象指针 开启该选项后 下列指针将压缩至32位

//        每个Class的属性指针(即静态变量)
//        每个对象的属性指针(即对象变量)
//        普通对象数组的每个元素指针
//        当然 也不是所有的指针都会压缩 一些特殊类型的指针JVM不会优化 比如指向PermGen的Class对象指针(JDK8中指向元空间的Class对象指针)
//        本地变量 堆栈元素 入参 返回值和NULL指针等

//        2.3.array length
//        如果对象是一个数组 那么对象头还需要有额外的空间用于存储数组的长度这部分数据的长度也随着JVM架构的不同而不同
//        32位的JVM上 长度为32位 64位JVM则为64位 64位JVM如果开启+UseCompressedOops选项 该区域长度也将由64位压缩至32位
    }
}