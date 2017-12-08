package AppClasses;

/**
 * Created by AAE on 2/23/2016.
 */
public class Candidate {

    String NAME,ADDRESS,PHONE,PHOTO_STRING,SYMBOL_STRING;
    int ID;
    String GENDER;

    public Candidate(String NAME, String ADDRESS, String PHONE, String PHOTO_STRING, String SYMBOL_STRING, int ID, String GENDER) {
        this.NAME = NAME;
        this.ADDRESS = ADDRESS;
        this.PHONE = PHONE;
        this.PHOTO_STRING = PHOTO_STRING;
        this.SYMBOL_STRING = SYMBOL_STRING;
        this.ID = ID;

        this.GENDER = GENDER;
    }

    public String getNAME() {
        return NAME;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public String getPHONE() {
        return PHONE;
    }

    public String getPHOTO_STRING() {
        return PHOTO_STRING;
    }

    public String getSYMBOL_STRING() {
        return SYMBOL_STRING;
    }

    public int getID() {
        return ID;
    }



    public String isGENDER() {
        return GENDER;
    }
}
