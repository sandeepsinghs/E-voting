package AppConfig;

import java.util.regex.Pattern;

public class LinkConfig {

    //local
    // public static final String SERVER_URL = "http://192.168.0.133:8080/Project1/";

    //local opulent
    public static final String SERVER_URL = "http://192.168.0.101:8081/eVoting_M3/rest/UserService/";

    public static final String PHOTO_URL = "http://192.168.0.101:8081/eVoting_M3/images/";


    public static final String REGISTER = SERVER_URL + "registerVoter";

    public static final String LOGIN = SERVER_URL + "Login";

    public static final String UPDATE_GCM = SERVER_URL + "RegisterGCM";

    public static final String GET_INCOMMING_ELECTIONS = SERVER_URL + "GetComingElections";

    public static final String CHECK_ELECTION_STATUS = SERVER_URL + "checkElectionStatus";

    public static final String GET_CANDIDATES = SERVER_URL + "GetCandidatesByElection";

    public static final String GET_RESULT = SERVER_URL + "getResult";

    public static final String CAST_VOTE = SERVER_URL + "CastVote";

    public static final String VERIFY_VOTER = SERVER_URL + "verifyVoter";

    public static final String VALIDATE_VOTER = SERVER_URL + "valiadteVoterCard";

    public static final String VOTER_THUMB_AUTH = SERVER_URL + "voterThumbAuth";

    public static final String FORGET_PASSWORD = SERVER_URL + "ForgetPasswordMailApp";





    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );


    public static final String USERNAME = "username";
    public static final String USERID = "userId";
    public static final String ADDRESS = "address";
    public static final String IS_LOGIN = "isLogin";
    public static final String EMAIL = "email";
    public static final String DOB = "dob";
    public static final String VOTER_ID = "voter_id";
    public static final String PASSWORD = "password";
    public static final String MOBILE = "mobile";
    public static final String IMAGE_STRING = "image_string";
    public static final String THUMB_STRING = "thumb_string";
    public static final String GENDER  = "gender";
    public static final String UIID = "uiId";



}
