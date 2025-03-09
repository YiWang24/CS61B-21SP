package D4;

/**
 * @author WY
 * @version 1.0
 **/

public class Puzzle {
    public static void main(String[] args) {
        A y = new B();
        B z = new B();
        System.out.println(y.fish(z));
        System.out.println(z.fish(z));
        System.out.println(z.fish(y));
        System.out.println(y.fish(y));
    }
}

class A{
   int fish(A other){
       return 1;
   }
   int fish(B other){
       return 2;
   }
}

class B extends A{
    @Override
    int fish(B other) {
        return 3;
    }
}