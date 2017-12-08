package AppClasses;

/**
 * Created by AAE on 2/24/2016.
 */
public class Result {

    public String NAME,VOTES,CANDIDATE_ID,IMAGE;
    public Result(String NAME, String VOTES, String CANDIDATE_ID,String IMAGE) {
        this.NAME = NAME;
        this.VOTES = VOTES;
        this.IMAGE = IMAGE;
        this.CANDIDATE_ID = CANDIDATE_ID;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public String getNAME() {
        return NAME;
    }

    public String getVOTES() {
        return VOTES;
    }

    public String getCANDIDATE_ID() {
        return CANDIDATE_ID;
    }
}
