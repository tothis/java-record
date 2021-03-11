package com.example.jni;

/**
 * @author 李磊
 * @since 1.0
 */
public class CppLibraryJniTest {

    static {
        String osName = System.getProperty("os.name");
        if (osName.equals("Linux")) {
            System.load(path("linux-x86-64/test.so"));
        } else {
            System.load(path("win32-x86-64/test.dll"));
        }
    }

    private static String path(String filePath) {
        return CppLibraryJniTest.class.getClassLoader().getResource(filePath).getPath();
    }

    private static native int test_add(int a, int b);

    private void obj_test() {
        System.out.println("obj test");
    }

    private static void class_test(String text) {
        System.out.println("class " + text);
    }

    public static void main(String[] args) {
        // javac -h . CppLibraryJniTest.java
        // Windows
        /*g++ -ID:\Java\jdk1.8.0_281\include -ID:\Java\jdk1.8.0_281\include\win32 ^
        jni-test.cpp -fPIC -shared -o../../../../resources/win32-x86-64/test.dll*/
        // Linux
        /*g++ -I/usr/local/jdk1.8.0_281/include -I/usr/local/桌面/jdk1.8.0_281/include/linux \
        jni-test.cpp -fPIC -shared -o../../../../resources/linux-x86-64/test.so*/
        System.out.println(test_add(1, 2));
    }
}