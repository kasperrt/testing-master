import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jorphan.collections.HashTree;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestClass {

    private String username;
    private String password = "changeme";
    private String client = "my-trusted-client";
    private String secret = "secret";
    private String host = "localhost";
    private String http = "https://";
    private StandardJMeterEngine jmeter;
    private HashTree testPlanTree;
    private HTTPSamplerProxy requestHandler;
    private String scope = "read+trust+write";
    private String grantType = "password";
    private String[] skipArray = {"easy", "hard", "pain", "unclear"};
    private String createUserValues = "{ \"construct_next\": 26, \"p02_q01\": { \"value\": \"55\" }, \"p02_q02\": { \"value\": \"0\" }, \"p02_q03\": { \"value\": \"180\" }, \"construct_p02_q03_ft\": { \"value\": \"\" }, \"construct_p02_q03_in\": { \"value\": \"\" }, \"p02_q04\": { \"value\": \"80\" }, \"construct_p02_q04_st\": { \"value\": \"\" }, \"construct_p02_q04_lb\": { \"value\": \"\" }, \"construct_p02_typeUnits\": { \"value\": \"SI\" }, \"p03_q01\": { \"value\": \"0\" }, \"p03_q02\": { \"value\": 0 }, \"p03_q03\": { \"value\": 0 }, \"p03_q04\": { \"value\": \"1\" }, \"p03_q05\": { \"value\": \"1\" }, \"p03_q06\": { \"value\": 0 }, \"p03_q07\": { \"value\": 0 }, \"p04_q1\": { \"value\": \"18\", \"describe\": \"Norwegian\" }, \"p05_q01\": { \"value\": \"1\" }, \"p06_q01\": { \"value\": \"0\" }, \"p08_q01\": { \"value\": \"3\" }, \"p08_q02\": { \"value\": \"8\" }, \"p09_q01\": { \"value\": \"3\" }, \"p10_q01\": { \"value\": \"4\" }, \"p11_q01\": { \"value\": \"0\" }, \"p11_q02\": { \"value\": \"1\" }, \"p12_q01\": { \"value\": \"0\" }, \"p13_q01\": { \"value\": \"1\" }, \"p13_q02\": { \"value\": \"0\" }, \"p13_q03\": { \"value\": \"1\" }, \"p13_q04\": { \"value\": \"0\" }, \"p13_q05\": { \"value\": \"1\" }, \"p13_q06\": { \"value\": \"0\" }, \"p13_q07\": { \"value\": \"1\" }, \"p13_q08\": { \"value\": \"0\" }, \"p13_q09\": { \"value\": \"1\" }, \"p13_q10\": { \"value\": \"0\" }, \"p13_q11\": { \"value\": \"1\" }, \"p13_q12\": { \"value\": \"0\" }, \"p13_q13\": { \"value\": \"1\" }, \"p13_q14\": { \"value\": \"0\" }, \"p13_q15\": { \"value\": \"1\" }, \"p13_q16\": { \"value\": \"0\" }, \"p13_q17\": { \"value\": \"1\" }, \"p13_q18\": { \"value\": \"0\" }, \"p13_q19\": { \"value\": \"1\" }, \"p13_q20\": { \"value\": \"0\" }, \"p13_q21\": { \"value\": \"1\" }, \"p13_q22\": { \"value\": \"0\" }, \"p13_q23\": { \"value\": \"1\" }, \"p13_q24\": { \"value\": \"0\" }, \"p14_q01\": { \"value\": \"5\" }, \"p15_q01\": { \"value\": \"1\" }, \"p16_q01\": { \"value\": \"0\" }, \"p16_q02\": { \"value\": \"1\" }, \"p16_q03\": { \"value\": \"2\" }, \"p16_q04\": { \"value\": \"3\" }, \"p16_q05\": { \"value\": \"4\" }, \"p17_q01\": { \"value\": \"0\" }, \"p17_q02\": { \"value\": \"1\" }, \"p17_q03\": { \"value\": \"2\" }, \"p17_q04\": { \"value\": \"3\" }, \"p17_q05\": { \"value\": \"4\" }, \"p17_q06\": { \"value\": \"5\" }, \"p17_q07\": { \"value\": \"6\" }, \"p17_q08\": { \"value\": \"0\" }, \"p17_q09\": { \"value\": \"1\" }, \"p17_q10\": { \"value\": \"2\" }, \"p18_q01\": { \"value\": \"0\" }, \"p18_q02\": { \"value\": \"1\" }, \"p18_q03\": { \"value\": \"2\" }, \"p18_q04\": { \"value\": \"3\" }, \"p18_q05\": { \"value\": \"4\" }, \"p18_q06\": { \"value\": \"5\" }, \"p18_q07\": { \"value\": \"6\" }, \"p18_q08\": { \"value\": \"7\" }, \"p07_q01\": { \"value\": \"0\" }, \"p19_q01\": { \"value\": \"Gaming\", \"scale\": \"3\" }, \"p19_q02\": { \"value\": \"Eating\", \"scale\": \"5\" }, \"p20_q01\": { \"value\": \"1\" }, \"p20_q02\": { \"value\": 0 }, \"p20_q03\": { \"value\": 0 }, \"p20_q04\": { \"value\": \"1\" }, \"p20_q05\": { \"value\": 0 }, \"p20_q06\": { \"value\": 0 }, \"p20_q07\": { \"value\": 0 }, \"p20_q08\": { \"value\": \"1\" }, \"p20_q09\": { \"value\": 0 }, \"p21_q01\": { \"value\": 0 }, \"p21_q02\": { \"value\": 0 }, \"p21_q03\": { \"value\": 0 }, \"p21_q04\": { \"value\": 0 }, \"p21_q05\": { \"value\": 0 }, \"p21_q06\": { \"value\": 0 }, \"p21_q07\": { \"value\": 0 }, \"p21_q08\": { \"value\": 0 }, \"p21_q09\": { \"value\": 0 }, \"p21_q10\": { \"value\": 0 }, \"p21_q11\": { \"value\": 0 }, \"p21_q12\": { \"value\": 0 }, \"p21_q13\": { \"value\": 0 }, \"p21_q14\": { \"value\": 0 }, \"p21_q15\": { \"value\": 0 }, \"p21_q16\": { \"value\": 0 }, \"p21_q17\": { \"value\": \"Hello World!\" }, \"p21_q18\": { \"value\": \"\" }, \"p22_q01\": { \"value\": \"0\" }, \"p22_q02\": { \"value\": \"1\" }, \"p22_q03\": { \"value\": \"2\" }, \"p22_q04\": { \"value\": \"3\" }, \"p22_q05\": { \"value\": \"4\" }, \"p23_q01\": { \"value\": \"52\" }, \"p24_q01\": { \"value\": \"0\" }, \"p24_q02\": { \"value\": \"1\" }, \"p24_q03\": { \"value\": \"2\" }, \"p24_q04\": { \"value\": \"0\" }, \"p25_q01\": { \"value\": \"0\" }, \"p25_q02\": { \"value\": \"1\" }, \"p25_q03\": { \"value\": \"2\" }, \"p25_q04\": { \"value\": \"3\" }, \"p25_q05\": { \"value\": \"4\" }, \"p25_q06\": { \"value\": \"0\" }, \"p25_q07\": { \"value\": \"1\" }, \"p25_q08\": { \"value\": \"2\" }, \"p25_q09\": { \"value\": \"3\" }, \"p25_q10\": { \"value\": \"4\" }, \"p26_q01\": { \"value\": \"0\" }, \"p26_q02\": { \"value\": \"1\" }, \"p26_q03\": { \"value\": \"2\" }, \"p26_q04\": { \"value\": \"3\" }, \"p26_q05\": { \"value\": \"0\" }, \"p26_q06\": { \"value\": \"1\" }, \"p26_q07\": { \"value\": \"2\" }, \"p26_q08\": { \"value\": \"3\" } }";

    /**
     * Paths
     */

    private String achievementsPath = "/patient/achievements";
    private String activityPath = "/patient/activity";
    private String educationPath = "/patient/plan/updateuser/education";
    private String exercisePath = "/patient/plan/updateuser/exercise";
    private String planNextPath = "/patient/plan/next";
    private String oauthPath = "/oauth/token";
    private String createUserPath = "/init/addpatient/";
    private String resetPath = "/init/reset";

    /**
     * Variable variables
     */

    private int totalSteps = 0;
    private int steps = 0;
    private boolean lazy = false;
    private JSONObject suggestedPlan = new JSONObject();
    private JSONArray exercisePlan = new JSONArray();
    private JSONArray educationPlan = new JSONArray();
    private JSONArray activityPlan = new JSONArray();
    private JSONObject questions = new JSONObject();
    private String accessToken;
    private String tokenType;
    private String authorization = "Authorization: ";

    private int port = 443;

    public TestClass(String username) throws KeyManagementException, NoSuchAlgorithmException {
        this.username = username;
        this.jmeter = new StandardJMeterEngine();
        this.testPlanTree = new HashTree();
        this.requestHandler = new HTTPSamplerProxy();
        this.createUserPath += username;
    }

    public void startTest() throws IOException, JSONException, InterruptedException {
        getAccessToken();

        startNewPlan();
        postExercises();
        postEducation();
        int calendarDay = 2;
        for(int day = 0; day < 7; day++) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, calendarDay);
            Date thisDate = c.getTime();
            for (int i = 0; i < activityPlan.length(); i++) {
                thisDate.setHours(i);
                Date endDate = (Date) thisDate.clone();
                endDate.setHours(i + 1);
                String type = (int) activityPlan.get(i) == 0 ? "sleeping" : "walking";
                postActivity(thisDate.getTime(), endDate.getTime(), (int) activityPlan.get(i), type);
                System.out.println("Day " + day + " of 6");
                System.out.println("Hour " + i + " of " + activityPlan.length());
            }
            calendarDay += day;
            if(calendarDay > 7) calendarDay = 1;
        }
    }

    private void startNewPlan() throws IOException, JSONException, InterruptedException {
        HttpsURLConnection connection = getConnection("POST", planNextPath, true, true, false);

        String sendString = "";
        if(questions.length() != 0) {
            sendString = "";
        }

        connection.setRequestProperty( "Content-Type", "application/json");
        writeInRequest(connection, sendString);

        String jsonString = getResponse(connection, true, true);

        JSONObject jsonObj = new JSONObject(jsonString);

        System.out.println("Plan object: " + jsonObj);


        // Calculating performance of all exercises for this week
        JSONArray exercises = (JSONArray) jsonObj.get("exercises");
        int numberExercises = exercises.length();
        for(int i = 0; i < exercises.length(); i++) {
            JSONObject thisToAdd = new JSONObject();
            JSONObject thisObject = (JSONObject) exercises.get(i);
            thisToAdd.put("exerciseid", thisObject.get("exerciseid"));
            thisToAdd.put("sets", (int) thisObject.get("sets"));
            thisToAdd.put("reps", (int) thisObject.get("reps"));
            int setDuration = (int) (Math.floor(Math.random() * (Math.abs((int) thisObject.get("set_duration_s") + 20))) + 1);
            thisToAdd.put("randomsetduration", setDuration);
            thisToAdd.put("setduration", (int) thisObject.get("set_duration_s"));
            Date d = (new Date((long) jsonObj.get("date")));
            d.setHours(12 + (i * (10 / numberExercises)));
            thisToAdd.put("performed", d.getTime());

            boolean skippedExercise = (Math.floor(Math.random() * 8) + 1) == 2;
            int _1 = 0;
            int _2 = 0;
            int _3 = 0;

            if(skippedExercise) {
                _1 = (int) Math.floor(Math.random() * ((int) thisToAdd.get("reps")));
                _2 = (int) Math.floor(Math.random() * ((int) thisToAdd.get("reps")));
                _3 = (int) Math.floor(Math.random() * ((int) thisToAdd.get("reps")));

                thisToAdd.put("reason", skipArray[(int) Math.floor(Math.random() * skipArray.length)]);
                lazy = true;
                // Doesn't support skipped yet
                thisToAdd.put("status", "completed");
            } else {
                thisToAdd.put("status", "completed");
                thisToAdd.put("reason", "string");
                _1 = (int) Math.floor(Math.random() * 5) + ((int) thisToAdd.get("reps"));
                _2 = (int) Math.floor(Math.random() * 5) + ((int) thisToAdd.get("reps"));
                _3 = (int) Math.floor(Math.random() * 5) + ((int) thisToAdd.get("reps"));
            }

            thisToAdd.put("repsperformed1", _1);
            thisToAdd.put("repsperformed2", _2);
            thisToAdd.put("repsperformed3", _3);
            this.exercisePlan.put(thisToAdd);
        }

        // Creating steps array

        steps = (int) Math.floor(Math.random() * 150);
        JSONObject activity = (JSONObject) jsonObj.get("activity");
        if(lazy) {
            steps = (int) activity.get("goal") - steps;
        } else {
            steps = (int) activity.get("goal") + steps;
        }

        for(int i = 0; i < 24; i++) {
            if(i < 8 || i > 14) {
                activityPlan.put(0);
            } else {
                int thisStep = (int) Math.floor(Math.random() * 30);
                if(lazy) {
                    if((int) Math.floor(Math.random() * 3) == 1) {
                        thisStep = (steps / 15);
                    } else {
                        if(thisStep < (steps / 15)) {
                            thisStep = (steps / 15) - thisStep;
                        } else {
                            thisStep = thisStep - (steps / 15);
                        }
                    }
                } else {
                    thisStep += (steps / 15);
                }
                activityPlan.put(thisStep);
                totalSteps += thisStep;
            }
        }

        for(int i = 0; i < ((JSONArray) jsonObj.get("educations")).length(); i++) {
            ((JSONObject) ((JSONArray) jsonObj.get("educations")).get(i)).put("is_correct", true);
        }

        suggestedPlan = jsonObj;
        educationPlan = (JSONArray) jsonObj.get("educations");
    }

    private void postEducation() throws IOException, InterruptedException {
        System.out.println(educationPlan.toString());
        postRequest(getConnection("POST", educationPath, true, true, false), educationPlan.toString());
    }

    private void postRequest(HttpsURLConnection post, String s) throws IOException, InterruptedException {
        HttpsURLConnection connection = post;
        connection.setRequestProperty("Content-Type", "application/json");
        writeInRequest(connection, s);

        String jsonString = getResponse(connection, true, true);
        System.out.println(jsonString);
    }

    private void postActivity(long start, long end, long steps, String type) throws JSONException, IOException, InterruptedException {
        JSONObject toSend = new JSONObject();
        JSONArray activities = new JSONArray();
        JSONObject activity = new JSONObject();
        activity.put("start", start);
        activity.put("end", end);
        activity.put("type", type);
        activity.put("steps", steps);

        activities.put(activity);
        toSend.put("activities", activities);
        System.out.println("Posting activity " + toSend.toString());
        postRequest(getConnection("POST", activityPath, true, true, false), toSend.toString());
    }

    private void getAchievements() throws IOException, InterruptedException {
        HttpsURLConnection connection = getConnection("GET", achievementsPath, false, true, false);
        String returnString = getResponse(connection, false, false);
        System.out.println(returnString.toString());
    }

    private void postExercises() throws IOException, InterruptedException {
        postRequest(getConnection("POST", exercisePath, true, true, false), exercisePlan.toString());
    }

    private HttpsURLConnection getConnection(String type, String path, boolean input, boolean output, boolean basic) throws IOException {
        URL url = new URL(http + host + path);
        HttpsURLConnection connection   = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", tokenType + " " + accessToken);

        if(basic) {
            String encoded = Base64.getEncoder().encodeToString((client + ":" + secret).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encoded);
        }

        connection.setDoOutput(output);
        if(input) connection.setDoInput(input);
        if(output) connection.setDoOutput(output);
        connection.setRequestMethod( type );
        connection.setUseCaches( false );

        return connection;
    }

    public void resetElastic() throws IOException, JSONException, InterruptedException {
        getAccessToken();
        System.out.println("Starting reset of elasticsearch");
        HttpsURLConnection connection = getConnection("GET", resetPath, false, true, false);

        String jsonString = getResponse(connection, false, false);
        System.out.println(jsonString);
        System.out.println("\nDone resetting elastic");
    }

    private void getAccessToken() throws
            IOException, JSONException, InterruptedException {

        createUser();

        String urlParameters =
                "scope=" + scope + "&" +
                "grant_type=" + grantType + "&" +
                "username=" + username + "&" +
                "password=" + password;
        byte[] postData                 = urlParameters.getBytes(StandardCharsets.UTF_8);

        HttpsURLConnection connection = getConnection("POST", oauthPath, false, true, true);

        connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        connection.setUseCaches( false );
        try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {
            wr.write( postData );
        }

        String jsonString = getResponse(connection, true, false);

        JSONObject jsonObj = new JSONObject(jsonString);
        accessToken = jsonObj.getString("access_token");
        tokenType = jsonObj.getString("token_type");
        tokenType = tokenType.substring(0, 1).toUpperCase() + tokenType.substring(1);
        System.out.println("\nGotten access_token " + jsonObj.toString());
    }

    private String getResponse(HttpsURLConnection connection, boolean wait, boolean achievement) throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            jsonString.append(line);
        }
        br.close();
        if(wait) {
            System.out.println("Waiting 1 second for elasticsearch to be updated");
            TimeUnit.SECONDS.sleep(1);
            if(achievement) getAchievements();
        }
        return jsonString.toString();
    }

    private void writeInRequest(HttpsURLConnection connection, String s) throws IOException {
        connection.setRequestProperty("Content-Length", Integer.toString(s.getBytes().length));
        DataOutputStream dataoutput = new DataOutputStream(connection.getOutputStream());
        dataoutput.write(s.getBytes("UTF-8"));
        dataoutput.flush();
    }

    private void createUser() throws InterruptedException {
        try {

            HttpsURLConnection connection = getConnection("POST", createUserPath, true, true, false);

            connection.setRequestProperty( "Content-Type", "application/json");
            writeInRequest(connection, createUserValues);

            String jsonString = getResponse(connection, true, false);
            System.out.println(jsonString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("user already exists");
        }

    }
}
