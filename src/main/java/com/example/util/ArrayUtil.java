package com.example.util;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * 数组工具类
 *
 * @author 李磊
 * @since 1.0
 */
public class ArrayUtil {

    /**
     * 合并多个数组
     *
     * @param arrays
     * @return
     */
    public static Object[] arrayMerge1(@Nonnull Object[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        int count = 0;
        for (int i = 0; i < arrays.length; i++) {
            count += arrays[i].length;
        }
        Object[] newArray = new Object[count];
        for (int i = 0; i < arrays.length; i++) {
            Object[] array = arrays[i];
            for (int j = 0; j < array.length; j++) {
                newArray[arrays.length * i + j] = array[j];
            }
        }
        return newArray;
    }

    /**
     * 合并多个数组
     */
    public static Object[] arrayMerge2(@Nonnull Object[]... arrays) {
        // 数组长度
        int length = 0;
        // 目标数组的起始位置
        int index = 0;

        for (Object[] array : arrays) {
            length = length + array.length;
        }
        Object[] newArray = new Object[length];
        for (int i = 0; i < arrays.length; i++) {
            if (i > 0) {
                // i为0时 目标数组开始索引为0
                // i为1时 目标数组开始索引为为第一个数组长度
                // i为2时 目标数组开始索引为第一个数组长度+第二个数组长度
                index = index + arrays[i - 1].length;
            }
            System.arraycopy(arrays[i], 0
                    , newArray, index, arrays[i].length);
        }
        return newArray;
    }

    /**
     * 获取数组中某一段元素
     *
     * @param array
     * @param from
     * @param to
     * @return
     */
    public static Object[] slipArray1(@Nonnull Object[] array, int from, int to) {
        Object[] objects = new Object[to - from];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = array[from + i];
        }
        return objects;
    }

    /**
     * 获取数组中某一段元素
     *
     * @param array
     * @param from
     * @param to
     * @return
     */
    public static <T> T[] slipArray2(@Nonnull T[] array, int from, int to) {
        return Arrays.copyOfRange(array, from, to);
    }

    /**
     * 删除数组中指定下标元素
     *
     * @param array
     * @param index
     * @return
     */
    public static Object[] delete(@Nonnull Object array[], int index) {
        // 创建新的数组 长度是原来-1
        Object[] newArray = new Object[array.length - 1];
        // 将除了要删除的元素的其他 元素复制到新的数组
        for (int i = 0; i < newArray.length; i++) {
            // 需要删除下标之前的元素
            if (i < index) {
                newArray[i] = array[i];
            }
            // 之后的元素
            else {
                newArray[i] = array[i + 1];
            }
        }
        return newArray;
    }

    /**
     * 在数组指定下标插入元素
     *
     * @param array
     * @param index
     * @return
     */
    public static Object[] insert(@Nonnull Object array[], int index, Object element) {
        // 创建一个新的数组 长度是原来长度+1
        Object[] newArray = new Object[array.length + 1];
        // 把原数组中的数据赋值到新的数组
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        for (int i = newArray.length - 1; i > index; i--) {
            newArray[i] = newArray[i - 1];
        }
        newArray[index] = element;
        return newArray;
    }

    /**
     * 分割数组
     *
     * @param array 原数据
     * @param size  分割后的数组长度
     * @return
     */
    public static Object[][] splitArray(@Nonnull Object[] array, int size) {
        int length = array.length;
        if (size >= length) {
            return new Object[][]{array};
        }
        int resultSize = length / size;
        int remainder = length % size;
        if (remainder != 0) {
            resultSize++;
        }
        Object[][] result = new Object[resultSize][];
        for (int i = 0; i < resultSize; i++) {
            if (i == resultSize - 1 && remainder != 0) {
                result[i] = Arrays.copyOfRange(array, i * size, length);
            } else {
                result[i] = Arrays.copyOfRange(array, i * size, (i + 1) * size);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int index = 1;
        int value = 0;
        Integer[] array = {1, 2, 3};
        print(insert(array, index, value));
        print(delete(array, index));

        print(slipArray1(array, 1, 2));
        print(slipArray2(array, 1, 2));

        Object[][] arrays = {
                {"1", "2", "3"}
                , {"4", "5", "6"}
                , {"7", "8", "9"}
        };

        print(arrayMerge1(arrays));
        print(arrayMerge2(arrays));
        print(splitArray(arrays, 3));
    }

    public static <T> void print(@Nonnull T[] array) {
        Arrays.stream(array).forEach(System.out::println);
    }
}