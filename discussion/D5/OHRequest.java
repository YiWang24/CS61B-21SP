package D5;

/**
 * @author WY
 * @version 1.0
 **/

public class OHRequest  {
    public String description;
    public String name;
    public OHRequest next;
    public OHRequest(String description, String name, OHRequest next) {
        this.description = description;
        this.name = name;
        this.next = next;
    }

}

