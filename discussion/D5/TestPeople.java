package D5;

/**
 * @author WY
 * @version 1.0
 **/
class Person {
    public String name;
    public int age;
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public void greet(Person other) {System.out.println("Hello, " + other.name);}
}

class Grandma extends Person {
    public Grandma(String name, int age) {
        super(name, age);
    }
    @Override
    public void greet(Person other) {System.out.println("Hello, young whippersnapper");}
    public void greet(Grandma other) {System.out.println("How was bingo, " + other.name + "?");}
}

public class TestPeople {
    public static void main(String[] args) {
        Person n = new Person("Neil",12);
        Person a = new Grandma("Ada", 60);
        Grandma v = new Grandma("Vidya", 80);
//        Grandma al = new Person("Alex", 70);
        n.greet(a); // hello ada
        n.greet(v); // hello vidya
        v.greet(a); // young
        v.greet((Grandma) v); // how was bingo ada ?
        a.greet(n);// young
        a.greet(v);//young
        ((Grandma) a).greet(v);//bingo
//        ((Grandma) n).greet(v); //RE


    }
}
