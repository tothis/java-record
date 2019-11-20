package com.example;

/**
 * @author 李磊
 * @time 2019/11/20 18:41
 * @description HashMap相关基础
 */
public class HashMapBase {
/**
 * HashMap的底层数据结构是由数组+链表组成 底层结构数组为哈希桶 而桶内则是链表 链表中的节点Node存放实际元素
 * HashMap允许key和value为null 线程不安全
 * HashTable不允许key和value为null 线程安全 所有方法通过synchronized修饰 但效率会降低
 *
 * HashMap中获取元素流程 对key值进行hash即哈希桶中的索引值 再找到对应的hash桶
 * 如存在 通过(拉链法)链表从前往后比较value值是否相等 直到找到元素或下个节点为null时
 * 而增加元素或修改元素的主要流程步骤与获取相类似 不同在于当增加元素后 如果总元素size大于阈值时 会发生扩容
 * 在jdk8中 加强了hash算法的效率以及利用率 当桶内元素大于8和所有元素总数大于64时 将链表转换为红黑树 优化了扩容时的算法
 */

/**
 * hash算法
 * 在上面概念中讲到 hash算法是计算key值对应哈希桶的位置即索引值 我们都知道数组在获取元素会比链表快 所以我们应该尽量让
 * 每个哈希桶只有一个元素 这样在查询时就只需要通过索引值找到对应的哈希桶内的值 而不需要再通过桶内的链表一个一个去查
 * 所以hash算法的作用是为了让元素分散均匀 从而提高查询效率 那接下来通过代码来一步一步分析时如何让元素分布均匀的
 */
// 这是根据key值获取value值的方法
// public V get(Object key) {
//     Node<K, V> e;
//     // 先调用hash(key)
//     return (e = getNode(hash(key), key)) == null ? null : e.value;
// }
//
// // hash算法如下
// static final int hash(Object key) {
//     int h;
//     // 第一步 获取key中的hashCode值
//     // 第二步 hashcode向左移16位的值进行异或 将高位与低位进行与运算 减少碰撞机率
//     return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
// }
//
// final Node<K, V> getNode(int hash, Object key) {
//     Node<K, V>[] tab;
//     Node<K, V> first, e;
//     int n;
//     K k;
//     if ((tab = table) != null && (n = tab.length) > 0 &&
//             // 第三步 取余运算 n是指数组长度 hash与(n-1)进行&与运算
//             // 在计算机运算中&肯定比%快 最终将第二步得到的hash跟n-1进行与运算
//             (first = tab[(n - 1) & hash]) != null) {
//         // ...
//     }
//     return null;
// }

/**
 * get方法 查询元素
 * 查询get方法相对简单 只要明白hash算法后得到哈希桶的索引值 再对桶内的链表进行比较hash key是否相等
 */

// final Node<K, V> getNode(int hash, Object key) {
//     Node<K, V>[] tab;
//     Node<K, V> first, e;
//     int n;
//     K k;
//     // 如果表为null或长度为0 或者经过hash算法后得到的哈希桶为null 则直接返回null
//     if ((tab = table) != null && (n = tab.length) > 0 &&
//             (first = tab[(n - 1) & hash]) != null) {
//         // 如果链表中的第一个节点元素相等则直接返回该Node
//         if (first.hash == hash &&
//                 ((k = first.key) == key || (key != null && key.equals(k))))
//             return first;
//         // 第二个节点不为空时继续往后找
//         if ((e = first.next) != null) {
//             // 判断是否为红黑树 是则交给红黑树去查找
//             if (first instanceof TreeNode)
//                 return ((TreeNode<K, V>) first).getTreeNode(hash, key);
//             // 否则循环链表找到对应相等的元素 直到找到或下个节点为null
//             do {
//                 if (e.hash == hash &&
//                         ((k = e.key) == key || (key != null && key.equals(k))))
//                     return e;
//             } while ((e = e.next) != null);
//         }
//     }
//     return null;
// }

/**
 * put方法 增加或修改 重点也是复杂的操作
 */
// final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
//                boolean evict) {
//     Node<K, V>[] tab;
//     Node<K, V> p;
//     int n, i;
//     // table为null或length长度为0则扩容
//     if ((tab = table) == null || (n = tab.length) == 0)
//         n = (tab = resize()).length;
//     // 如果哈希桶为null 则创建节点放在该桶内
//     if ((p = tab[i = (n - 1) & hash]) == null)
//         tab[i] = newNode(hash, key, value, null);
//     else {
//         Node<K, V> e;
//         K k;
//         // 如果桶内第一个元素hash相等 key相等 则更新此节点
//         if (p.hash == hash &&
//                 ((k = p.key) == key || (key != null && key.equals(k))))
//             e = p;
//             // 判断是否为红黑树 若是则调用红黑树的putTreeVal
//         else if (p instanceof TreeNode)
//             e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
//         else {
//             // 循环链表
//             for (int binCount = 0; ; ++binCount) {
//                 // 知道下个节点为null时
//                 if ((e = p.next) == null) {
//                     // 增加节点
//                     p.next = newNode(hash, key, value, null);
//                     // 如果桶内的节点是否大于8
//                     if (binCount >= TREEIFY_THRESHOLD - 1)
//                         // 这个方法里还会判断总节点数大于64则会转换为红黑树
//                         treeifyBin(tab, hash);
//                     break;
//                 }
//                 // 如果找到相等的节点则退出循环
//                 if (e.hash == hash &&
//                         ((k = e.key) == key || (key != null && key.equals(k))))
//                     break;
//                 p = e;
//             }
//         }
//         // 只有找到相等节点是e不为null
//         if (e != null) {
//             // 更新节点为新的值
//             V oldValue = e.value;
//             if (!onlyIfAbsent || oldValue == null)
//                 e.value = value;
//             afterNodeAccess(e);
//             return oldValue;
//         }
//     }
//     ++modCount;
//     // 最后判断增加后的个数是否大于阈值 大于则扩容
//     if (++size > threshold)
//         resize();
//     afterNodeInsertion(evict);
//     return null;
// }

/**
 * 扩容
 * 扩容是HashMap重点中的重点 也是最耗性能的操作 扩容的步骤是先对size扩大两倍
 * 再对原先的节点重新经过hash算法得到新的索引值即复制到新的哈希桶里 最后得到新的table
 * 其中jdk8对扩容进行了优化 提高了扩容的效率 但在平常运用中尽量要避免让HashMap进行扩容
 * 若已知HashMap中的元素数量 则一开始初始化HashMap时指定容量 这样就减少了HashMap扩容次数
 * <p>
 * jdk8优化如下
 * 对扩容方法进行了优化 经过rehash之后 元素的位置要么是在原位置 要么是在原位置再移动2次幂的位置
 * 运算尽量用位运算代替 比较高效 如hash算法中的取模运算 用&n-1去替代%n方法 运行起来更加高效
 * 当桶内节点大于8和节点总数大于64时才会转换为红黑树 前者在putValue中验证 后者treeifyBin方法中判断
 * 扩容时 对容量进行扩大两倍后 原链表上的节点可能存放在原来的下表即low位或存放在high位(high=low+oldCapacity)
 */
// final Node<K, V>[] resize() {
//     Node<K, V>[] oldTab = table;
//     int oldCap = (oldTab == null) ? 0 : oldTab.length;
//     int oldThr = threshold;
//     int newCap, newThr = 0;
//     if (oldCap > 0) {
//         // 如果容量大于了最大容量时 直接返回旧的table
//         if (oldCap >= MAXIMUM_CAPACITY) {
//             threshold = Integer.MAX_VALUE;
//             return oldTab;
//         }
//         // 同时满足扩容两倍后小于最大容量和原先容量大于默认初始化的容量 对阈值增大两倍
//         else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
//                 oldCap >= DEFAULT_INITIAL_CAPACITY)
//             newThr = oldThr << 1;
//     } else if (oldThr > 0)
//         newCap = oldThr;
//     else {
//         // 默认初始化容量和阈值
//         newCap = DEFAULT_INITIAL_CAPACITY;
//         newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
//     }
//     if (newThr == 0) {
//         float ft = (float) newCap * loadFactor;
//         newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
//                 (int) ft : Integer.MAX_VALUE);
//     }
//     threshold = newThr;
//     @SuppressWarnings({"rawtypes", "unchecked"})
//     Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
//     table = newTab;
//     if (oldTab != null) {
//         // 接下来对哈希桶的所有节点转移到新的哈希桶中
//         for (int j = 0; j < oldCap; ++j) {
//             Node<K, V> e;
//             // 如果哈希桶为null 则不需任何操作
//             if ((e = oldTab[j]) != null) {
//                 // 将桶内的第一个节点赋值给e
//                 // 将原哈希桶置为null 让gc回收
//                 oldTab[j] = null;
//                 if (e.next == null)
//                     // 如果e的下个节点(即第二个节点)为null 则只需要将e进行转移到新的哈希桶中
//                     newTab[e.hash & (newCap - 1)] = e;
//                 else if (e instanceof TreeNode)
//                     // 如果哈希桶内的节点为红黑树 则交给TreeNode进行转移
//                     ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
//                 else {
//                     // 将桶内的转移到新的哈希桶内
//                     // jdk8后将新的节点插在最后面
//
//                     // 下面就是1.8后的优化
//                     // 1.7 将哈希桶的所有元素进行hash算法后转移到新的哈希桶中
//                     // 1.8 则是利用哈希桶长度在扩容前后的区别 将桶内元素分为原先索引值和新的索引值(即原先索引值+原先容量)
//
//                     // loHead记录低位链表的头部节点
//                     // loTail是低位链表临时变量 记录上个节点并且让next指向当前节点
//                     Node<K, V> loHead = null, loTail = null;
//                     // hiHead hiTail与上面的一样 区别在于这个是高位链表
//                     Node<K, V> hiHead = null, hiTail = null;
//                     Node<K, V> next;
//                     do {
//                         // 用于临时记录当前节点的next节点
//                         next = e.next;
//                         // e.hash & oldCap==0表示扩容前后对当前节点的索引值没有发生改变
//                         if ((e.hash & oldCap) == 0) {
//                             // loTail为null时 代表低位桶内无元素则记录头节点
//                             if (loTail == null)
//                                 loHead = e;
//                             else
//                                 // 将上个节点next指向当前节点
//                                 // 即新的节点是插在链表的后面
//                                 loTail.next = e;
//                             // 将当前节点赋值给loTail
//                             loTail = e;
//                         } else {
//                             // 跟上面的步骤是一样的)
//                             if (hiTail == null)
//                                 hiHead = e;
//                             else
//                                 hiTail.next = e;
//                             hiTail = e;
//                         }
//                         // 当next节点为null则退出循环
//                     } while ((e = next) != null);
//                     // 如果低位链表记录不为null 则低位链表放到原index中
//                     if (loTail != null) {
//                         // 将最后一个节点的next属性赋值为null
//                         loTail.next = null;
//                         newTab[j] = loHead;
//                     }
//                     // 如果高位链表记录不为null 则高位链表放到新index中
//                     if (hiTail != null) {
//                         hiTail.next = null;
//                         newTab[j + oldCap] = hiHead;
//                     }
//                 }
//             }
//         }
//     }
//     return newTab;
// }

/**
 * HashMap的长度为2的倍数
 * 在HashMap的操作流程中 首先会对key进行hash算法得到一个索引值 这个索引值就是对应哈希桶数组的索引
 * 为了得到这个索引值必须对扰动后的数跟数组长度进行取余运算 即hash%n(n为HashMap的长度) 又因为&比%运算快
 * n如果为2的倍数 就可以将%转换为& 结果就是hash&(n-1)
 *
 * jdk8中满足putVal方法和treeifyBin方法后将链表转化成红黑树
 */
// putVal方法判断桶内元素是是否大于8
// if(binCount >=TREEIFY_THRESHOLD -1)
//     treeifyBin(tab, hash);
// break;

// treeifyBin方法中判断长度是否大于最小红黑树容量64 小于则继续扩容 大于则转为红黑树
// if(tab ==null||(n =tab.length) <MIN_TREEIFY_CAPACITY)
//     resize();
}