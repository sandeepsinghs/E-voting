package AppClasses;

/**
 * Created by AAE on 2/23/2016.
 */
public class Elections {

    public int ID;
    public String NAME,VENUE,DATE;

    public Elections(int ID, String NAME, String VENUE, String DATE) {
        this.ID = ID;
        this.NAME = NAME;
        this.VENUE = VENUE;
        this.DATE = DATE;
    }

    public int getID() {
        return ID;
    }

    public String getNAME() {
        return NAME;
    }

    public String getVENUE() {
        return VENUE;
    }

    public String getDATE() {
        return DATE;
    }
}
