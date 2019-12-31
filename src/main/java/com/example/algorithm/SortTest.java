package com.example.algorithm;

/**
 * @author 李磊
 * @time 2019/11/16 1:43
 * @description 常用排序实现
 */
public class SortTest {
    public static void main(String[] args) {
        int[] array;
        // array = new int[]{6, 5, 4, 3, 2, 1};
        // bubbleSort(array, true);
        // bubbleSort(array, false);

        // array = new int[]{1, 6, 5, 4, 3, 2};
        // doubleBubbleSort(array);

        // array = new int[]{2, 3, 4, 5, 6, 1};
        // selectionSort(array);

        array = new int[]{3, 4, 1, 2, 5, 6};
        // insertionSort(array);
        // shellSort(array);
        // quickSort(array, 0, array.length - 1);
        // binarySort(array);
        // mergeSort(array, 0, array.length - 1);
        heapSort(array);

        for (int i : array) System.out.print(i);
    }

    /**
     * 冒泡排序
     * 时间复杂度 O(n^2)
     * 有没有跳跃式的比较 稳定的排序
     * <p>
     * 对冒泡排序进行优化
     *
     * @param array    数组
     * @param sortFlag 是否从小到大
     */
    public static void bubbleSort(int[] array, boolean sortFlag) {
        int num1 = 0, num2 = 0;
        int temp;
        boolean flag;
        // 减1是因为数组长度比数组最大索引大1
        for (int i = 0; i < array.length - 1; i++) {
            flag = true;
            for (int j = 0; j < array.length - 1 - i; j++) {
                // 大的在前面
                /*
                 * 如array.length为6
                 * if (array[4] > array[5]) {
                 *     temp = array[4];
                 *     array[4] = array[5];
                 *     array[5] = temp;
                 * }
                 */
                num1++;
                if (sortFlag ? array[j] > array[j + 1] : array[j] < array[j + 1]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    // 表示在此次外层循环中元素发生过交换
                    flag = false;
                    num2++;
                }
            }
            // 表示在上次的扫描中 没有元素进行交换 此时数据已经排序完成
            if (flag) {
                break;
            }
        }
        out(num1, num2);
    }

    /**
     * 双向冒泡
     *
     * @param array
     * @return
     * @description
     */
    public static void doubleBubbleSort(int[] array) {
        int num1 = 0, num2 = 0;
        int length = array.length;
        boolean flag = true;
        int newLocation = 0; // 记录前面最小的已经有序的数据个数
        int lastLocation = length - 1; // 最后一个元素的下标
        for (int i = 0; i < length - 1; i++) {
            int nowLocation = 0;
            for (int j = 0; j < lastLocation; j++) {
                num1++;
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    nowLocation = j;
                    flag = false;
                    num2++;
                }
            }
            lastLocation = nowLocation;
            if (flag) {
                break;
            }
            flag = true;

            // 从后向前冒泡 找出最小的
            for (int j = lastLocation; j > newLocation; j--) {
                int temp = array[j];
                array[j] = array[j - 1];
                array[j - 1] = temp;
                flag = false;
                num1++;
                num2++;
            }
            newLocation++;
            if (flag) {
                break;
            }
            flag = true;
        }
        out(num1, num2);
    }

    /**
     * 选择排序
     *
     * @param array
     * @return
     * @description 交换次数O(n) 比较次数O(n²)即n*(n-1)/2
     */
    public static void selectionSort(int[] array) {
        int min, num1 = 0, num2 = 0;
        for (int i = 0; i < array.length - 1; i++) {
            min = i; // 记录最小元素的索引
            for (int j = i + 1; j < array.length; j++) {
                num1++;
                if (array[min] > array[j]) { // 依次比较记录较小元素的索引
                    min = j;
                }
            }
            // 上面循环结束得到此外层循环的最小元素 进行交换
            int temp = array[min];
            array[min] = array[i];
            array[i] = temp;
            num2++;
        }
        out(num1, num2);
    }

    /**
     * 插入排序
     *
     * @param array
     * @return
     */
    public static void insertionSort(int[] array) {
        int num1 = 0, num2 = 0;
        // 外层向右的i 即作为比较对象的数据的index
        /**
         * 第一个循环
         * 把数组分成两部分 右边为未排序 左边为已排序
         * 记录排序与未排序分割点temp(为下一个排序对象)
         */
        for (int i = 1; i < array.length; i++) {
            int temp = array[i]; // 用作比较的数据
            int left = i - 1;
            /*
             * 第二个循环
             * 将排序对象temp与已排序数组比较
             * 当temp比最近左边的数大时
             * 直接结束本次循环 进行下一个数排序
             * 否则比左边这个数小时将这个数后移 腾出这个数的位置
             */
            /*for (left = i - 1; left >= 0; left--) {
                if (temp > array[left]) {
                    break;
                } else {
                    array[left + 1] = array[left];
                }
            }*/
            while (left >= 0 && array[left] > temp) {
                num1++;
                array[left + 1] = array[left];
                left--;
            }
            array[left + 1] = temp; // 把temp放到空位上
            num2++;
        }
        out(num1, num2);
    }

    private static void shellSort(int[] arr) {
        // step步长
        for (int step = arr.length / 2; step > 0; step /= 2) {
            // 对一个步长区间进行比较 [step, arr.length)
            for (int i = step; i < arr.length; i++) {
                int value = arr[i];
                int j;

                // 对步长区间中具体的元素进行比较
                for (j = i - step; j >= 0 && arr[j] > value; j -= step) {
                    // j为左区间的取值 j+step为右区间与左区间的对应值
                    arr[j + step] = arr[j];
                }
                // 此时step为一个负数 [j + step]为左区间上的初始交换值
                arr[j + step] = value;
            }
        }
    }

    /**
     * 快速排序
     *
     * @param array
     * @param begin
     * @param end
     */
    public static void quickSort(int[] array, int begin, int end) {
        if (begin >= end) {
            return;
        }

        int _begin = begin, _end = end;

        // 基准数据
        int temp = array[begin];

        // 循环的内部会改变_begin和_end的值
        while (_begin < _end) {
            // 当队尾的元素大于等于基准数据时 向前挪动_end指针
            while (_begin < _end && array[_end] >= temp) {
                _end--;
            }
            // 如果队尾元素小于temp 需要将其赋值给_begin
            // array[_begin] = array[_end];
            // 当队首元素小于等于temp时 向前挪动_begin指针
            while (_begin < _end && array[_begin] <= temp) {
                _begin++;
            }
            // 当队首元素大于temp 需要将其赋值给_end
            // array[_end] = array[_begin];

            if (_begin < _end) {
                int i = array[_begin];
                array[_begin] = array[_end];
                array[_end] = i;
            }
        }

        // 调整基准数据的位置
        array[begin] = array[_begin];
        array[_begin] = temp;

        // 对基准数据左边数组快排
        quickSort(array, begin, _begin - 1);
        // 对基准数据右边数组快排
        quickSort(array, _begin + 1, end);
    }

    /**
     * 二分排序法
     *
     * @param array
     */
    public static void binarySort(int[] array) {
        int begin, end, temp, num = 0;
        for (int i = 1; i < array.length; i++) {
            // 查找区上界
            begin = 0;
            // 查找区下界
            end = i - 1;
            // 将当前待插入记录保存在临时变量中
            temp = array[i];
            while (begin <= end) {
                // 找出中间值
                // mean = (begin + end) / 2;
                int mean = (begin + end) >> 1;
                // 如果待插入记录比中间记录小
                if (temp < array[mean]) {
                    // 插入点在低半区
                    end = mean - 1;
                } else {
                    // 插入点在高半区
                    begin = mean + 1;
                }
            }
            // 将前面所有大于当前待插入记录的记录后移
            for (int j = i - 1; j >= begin; j--) {
                array[j + 1] = array[j];
            }
            // 将待插入记录回填到正确位置
            array[begin] = temp;
            num++;
        }
        System.out.println("共交换" + num + "次");
    }

    /**
     * 构建最大堆 数组最终结果由小到大排列
     * 形成堆之后 数组中下标索引为i的节点 左节点是i*2 +1 右节点是i*2+2
     *
     * @param array
     */
    public static void heapSort(int[] array) {
        // 初始化堆 形成一个最大堆
        int index;
        if (array.length % 2 == 0) {
            index = (array.length - 2) / 2;
        } else {
            index = (array.length - 3) / 2;
        }
        // 从最后一个叶子节点的父节点开始一层层向上遍历
        for (; index >= 0; index--) {
            keepMaxHeap(array, index, array.length);
        }

        // 正式排序
        for (int i = array.length - 1; i >= 0; i--) {
            // 根节点与尾部节点交换 尾部节点组成由小到大的有序数组
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;

            // 交换后 维护以根节点为父节点的最大堆
            keepMaxHeap(array, 0, i); // 最后的叶子节点的上限值也相应发生变化
        }
    }

    /**
     * 维护以index为父节点的最大堆 较大节点上浮
     *
     * @param array
     * @param index    需要操作的父节点的下标索引
     * @param endIndex 子节点不可超过的最大下标索引 index < endIndex
     */
    public static void keepMaxHeap(int[] array, int index, int endIndex) {
        if (array == null || index > endIndex) {
            return;
        }
        int parentIndex = index;
        int leftIndex = parentIndex * 2 + 1;
        int maxNodeIndex = parentIndex;
        if (leftIndex < endIndex && array[maxNodeIndex] < array[leftIndex]) {
            // 左节点比父节点大
            maxNodeIndex = leftIndex;
        }
        int rightIndex = leftIndex + 1;
        if (rightIndex < endIndex && array[maxNodeIndex] < array[rightIndex]) {
            // 右节点比左 父节点中最大的还大
            maxNodeIndex = rightIndex;
        }
        if (maxNodeIndex != parentIndex) {
            // 说明父节点不是最大的 需要交换位置
            int temp = array[parentIndex];
            array[parentIndex] = array[maxNodeIndex];
            array[maxNodeIndex] = temp;

            // 交换位置后要继续向下递归 维持最大堆的性质
            keepMaxHeap(array, maxNodeIndex, endIndex);
        }
    }

    /**
     * 归并排序
     *
     * @param array
     * @param start
     * @param end
     */
    public static void mergeSort(int[] array, int start, int end) {
        if (start < end) { // 当子序列中只有一个元素时结束递归
            int mean = (start + end) / 2; // 划分子序列
            mergeSort(array, start, mean); // 对左侧子序列进行递归排序
            mergeSort(array, mean + 1, end); // 对右侧子序列进行递归排序

            // 两路归并算法 两个排好序的子序列合并为一个子序列
            int[] temp = new int[array.length]; // 辅助数组
            int p1 = start, p2 = mean + 1, k = start; // p1 p2是检测指针 k是存放指针

            while (p1 <= mean && p2 <= end) {
                if (array[p1] <= array[p2])
                    temp[k++] = array[p1++];
                else
                    temp[k++] = array[p2++];
            }

            // 如果第一个序列未检测完 直接将后面所有元素加到合并的序列中
            while (p1 <= mean) temp[k++] = array[p1++];
            while (p2 <= end) temp[k++] = array[p2++]; // 同上

            // 复制回原素组
            for (int i = start; i <= end; i++)
                array[i] = temp[i];
        }
    }

    /**
     * @param num1 比较次数
     * @param num2 交换次数
     */
    public static void out(int num1, int num2) {
        System.out.println("共比较" + num1 + "次");
        System.out.println("共交换" + num2 + "次");
    }
}