>Java通过Collectionsp进行排序 Collectionsp排序需要一些接口进行配合

**Comparable接口**
```java
public static void main(String[] args) {
    List<Integer> nums = new ArrayList<Integer>();
    nums.add(3);
    nums.add(5);
    nums.add(1);
    nums.add(0);
    System.out.println(nums);
    Collections.sort(nums);
    System.out.println(nums);
}
```
	输出结果
		[3, 5, 1, 0]
		[0, 1, 3, 5]

Comparable自定义类排序
```java
// 自定义类实现Comparable接口 重写抽象方法compareTo(Object o);
public class User implements Comparable<User> {

    private int score;

    private int age;

    public User(int score, int age) {
        super();
        this.score = score;
        this.age = age;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    // 该方法的返回值0代表相等 正数表示大于 负数表示小于
    public int compareTo(User o) {
	    // 先按照年龄排序
        int i = this.getAge() - o.getAge();
        if (i == 0) {
	        // 如果年龄相等了再用分数进行排序
            return this.score - o.getScore();
        }
        return i;
    }

    public static void main(String[] args) {
        List<User> users = new ArrayList<User>();
        users.add(new User(78, 26));
        users.add(new User(67, 23));
        users.add(new User(34, 56));
        users.add(new User(55, 23));
        Collections.sort(users);
        for (User user : users) {
            System.out.println(user.getScore() + "," + user.getAge());
        }
    }
}
```
输出结果
55,23
67,23
78,26
34,56
***
**Comparator接口**

`设计模式为策略模式 不改变对象自身 而用策略对象来改变它的行为`
```java
public class Students {

    private int age;
    private int score;

    public Students(int age, int score) {
        super();
        this.age = age;
        this.score = score;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public static void main(String[] args) {
        List<Students> students = new ArrayList<Students>();
        students.add(new Students(23, 100));
        students.add(new Students(27, 98));
        students.add(new Students(29, 99));
        students.add(new Students(29, 98));
        students.add(new Students(22, 89));
        Collections.sort(students, new Comparator<Students>() {

            @Override
            // 该方法的返回值0代表相等 正数表示大于 负数表示小于
            public int compare(Students o1, Students o2) {
                int i = o1.getScore() - o2.getScore();
                if (i == 0) {
                    return o1.getAge() - o2.getAge();
                }
                return i;
            }
        });

        for (Students stu : students) {
            System.out.println("score:" + stu.getScore() + " age" + stu.getAge());
        }
    }
}
```
	输出结果
		score:89 age22
		score:98 age27
		score:98 age29
		score:99 age29
		score:100 age23
可以使用lambda表达式简化
```java
List<Student> stus = new ArrayList<Student>(){{
	add(new Student("张三", 30));
	add(new Student("李四", 20));
	add(new Student("王五", 60));
}};
// 对Student按年龄进行排序
Collections.sort(stus, (s1,s2)->(s1.getAge()-s2.getAge()));
```
使用lambada创建Comparator实例 并指定排序规则
```java
// 以自然序排序一个list
list.stream().sorted();

// 自然序逆序元素
list.stream().sorted(Comparator.reverseOrder());

// 按照Student的年龄进行排序
list.stream().sorted(Comparator.comparing(Student::getAge));

// 按照Student的年龄进行逆序排序
list.stream().sorted(Comparator.comparing(Student::getAge).reversed());
```