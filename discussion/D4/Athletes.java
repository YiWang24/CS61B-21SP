package D4;

/**
 * @author WY
 * @version 1.0
 **/


class Person {
    void speakTo(Person other) { System.out.println("kudos"); }
    void watch(SoccerPlayer other) { System.out.println("wow"); }
}
class Athlete extends Person {
    void speakTo(Athlete other) { System.out.println("take notes"); }
    void watch(Athlete other) { System.out.println("game on"); }
}

class SoccerPlayer extends Athlete {
    void speakTo(Athlete other) { System.out.println("respect"); }
    void speakTo(Person other) { System.out.println("hmph"); }
}


public class Athletes {
    public static void main(String[] args) {
        Person itai = new Person();
//        SoccerPlayer shivani = new Person();
        Athlete sohum = new SoccerPlayer();
        Person jack = new Athlete();
        Athlete anjali = new Athlete();
        SoccerPlayer chirasree = new SoccerPlayer();
        itai.watch(chirasree); // wow
        jack.watch((SoccerPlayer) sohum);
        itai.speakTo(sohum); // kudos
        jack.speakTo(anjali); // kudos
        anjali.speakTo(chirasree); // take notes
        sohum.speakTo(itai); // hmph
        chirasree.speakTo((SoccerPlayer) sohum); // respect
//        sohum.watch(itai);
        sohum.watch((Athlete) itai); // RE
        ((Athlete) jack).speakTo(anjali); // take notes
        ((SoccerPlayer) jack).speakTo(chirasree); // RE
        ((Person) chirasree).speakTo(itai); // hmph

    }

}
