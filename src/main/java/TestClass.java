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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
    private final boolean DEBUG = true;
    private String writer;
    private int maxWeekOffset = 3;

    /**
     * Paths
     */

    private String tailoringPath = "/patient/plan/tailoring";
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

    public TestClass(String username, String writer) {
        this.username = username;
        this.jmeter = new StandardJMeterEngine();
        this.testPlanTree = new HashTree();
        this.requestHandler = new HTTPSamplerProxy();
        this.createUserPath = this.createUserPath + username;
        this.writer = writer;
        createUser();
    }

    public void startTest(int weekOffset) {
        print("WeekOffset is now " + weekOffset);
        print("Crash here 14?");
        getAccessToken();
        print("Crash here 15?");
        startNewPlan();
        print("Crash here 16?");
        postExercises();
        print("Crash here 17?");
        postEducation();
        print("Crash here 18?");
        int calendarDay = 2;
        Date thisDate = new Date();
        Date endDate;

        for(int day = 0; day < 7; day++) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, calendarDay);
            thisDate = c.getTime();
            thisDate.setTime(thisDate.getTime() - 31556926000L + (604800000 * weekOffset));
            for (int i = 0; i < activityPlan.length(); i++) {
                thisDate.setHours(i);
                endDate = (Date) thisDate.clone();
                endDate.setHours(i + 1);
                String type = null;
                try {
                    type = (int) activityPlan.get(i) == 0 ? "sleeping" : "walking";
                    postActivity(thisDate.getTime(), endDate.getTime(), (int) activityPlan.get(i), type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                print("Week " + weekOffset + " of " + maxWeekOffset);
                print("Day " + day + " of 6");
                print("Hour " + i + " of " + (activityPlan.length() - 1));
            }
            calendarDay += day;
            if(calendarDay > 7) calendarDay = 1;
        }
        activityPlan = new JSONArray();
        getTailoring(thisDate);
        print("crash here weekly offset " + weekOffset);
        print("We're at the end now, weekOffset " + weekOffset);
        if (weekOffset < maxWeekOffset) startTest(weekOffset + 1);

    }

    private void getTailoring(Date thisDate){
        try {
            TimeUnit.MILLISECONDS.sleep(1005);
            Date start = new Date();
            HttpsURLConnection connection = getConnection("GET", tailoringPath, false, true, false);

            boolean wait = false;
            String returnString = getResponse(connection, wait, false, "", tailoringPath);
            Date end = new Date();
            printTook((wait ? end.getTime() - 1005 : end.getTime()) - start.getTime(), tailoringPath, returnString);
            JSONArray tailoringObject = new JSONArray(returnString.toString());
            print("getTailoring here 8 " + returnString.toString());
            print("crash here 1?");
            this.questions = new JSONObject();
            JSONArray questionArray = new JSONArray();
            this.questions.put("date", thisDate.getTime());
            String answer;
            for(int i = 0; i < tailoringObject.length(); i++) {
                print("crash here 2 - " + i + "?");
                JSONObject thisQuestionObject = tailoringObject.getJSONObject(i);
                JSONObject thisToSend = new JSONObject();
                thisToSend.put("questionid", thisQuestionObject.get("questionid"));
                JSONArray options = (JSONArray) thisQuestionObject.get("options");
                print("crash here 3 - " + i + "?");
                if(((JSONObject) options.get(0)).getString("optiontype").equals("checkbox")) {
                    print("crash here 7 - " + i + "?");
                    int randomNumber = (int) Math.floor(Math.random() * options.length());
                    ArrayList<String> answers = new ArrayList<>();
                    print("crash here 10 - " + i + "?");
                    print("Options " + options.toString());
                    if(randomNumber == 0) randomNumber = 1;
                    for(int y = 0; y < randomNumber; y++) {
                        int index = (int) Math.floor(Math.random() * (options.length() - 1));
                        print("Index " + index);
                        print("Length " + options.length());
                        String toAdd = (String) ((JSONObject) options.remove(index)).get("option");
                        answers.add(toAdd);
                        if(options.length() < 1) break;
                        //if(!toAdd.equals("none")) break;
                    }
                    print("crash here 8 - " + i + "?");
                    answers.remove("none");
                    if(answers.size() == 0) answers.add((String) ((JSONObject) options.remove(0)).get("option"));
                    JSONArray preAnswer = new JSONArray(answers);
                    answer = String.join(";", answers);
                    //answer = preAnswer.toString();
                    print("Answer is " + answer);
                    thisToSend.put("answer", answer);
                    print("crash here 5 - " + i + "?");
                } else {
                    print("crash here 9 - " + i + "?");
                    print("Options " + options.toString());
                    print("Options is " + options.toString());
                    JSONObject object = (JSONObject) options.get((int) Math.floor(Math.random() * options.length()));
                    print("Chosen option " + object.toString());
                    answer = (String) object.get("value");
                    print("Answer " + answer.toString());
                    try {
                        thisToSend.put("answer", Integer.parseInt(answer));
                    } catch(NumberFormatException e) {
                        print("Error on parsing int, ");
                        print(answer);
                        thisToSend.put("answer", object.get("value"));
                    }
                    print("crash here 6 - " + i + "?");
                }
                print("crash here 4 - " + i + "?");
                questionArray.put(thisToSend);
                print("crash here 12 - " + i + "?");
            }
            this.questions.put("questions", questionArray);
            print("crash here 13?");
            print("Question element " + this.questions.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void print(String toPrint) {
        if(DEBUG) {
            System.out.println(toPrint);
        }
    }

    private void startNewPlan(){
        try {
            Date start = new Date();
            HttpsURLConnection connection = getConnection("POST", planNextPath, true, true, false);


            String sendString = "";
            if(this.questions.length() != 0) {
                sendString = questions.toString();
            }


            connection.setRequestProperty( "Content-Type", "application/json");
            writeInRequest(connection, sendString);
            boolean wait = true;
            String jsonString = getResponse(connection, wait, false, sendString, planNextPath);
            Date end = new Date();
            printTook((wait ? end.getTime() - 1005 : end.getTime()) - start.getTime(), planNextPath, jsonString);
            JSONObject jsonObj = new JSONObject(jsonString);

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void postEducation() {
        postRequest(getConnection("POST", educationPath, true, true, false), educationPlan.toString(), educationPath);
    }

    private void printError(String request, String path, String response) {
        try {
            final Path filePath = Paths.get("runs/errors/errors-" + writer);
            Files.write(filePath, Arrays.asList(username + "@" + request + "@" + path + "@" + response), StandardCharsets.UTF_8,
                    Files.exists(filePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe) {
            // Add your own exception handling...
        }
    }

    private void printTook(long tookTime, String path, String response) {
        String toWrite = username + "@" + (new Date()).getTime() + "@" + tookTime + "@" + path + "@" + response;
        print(toWrite);
        try {
            final Path filePath = Paths.get("runs/valid/" + writer);
            Files.write(filePath, Arrays.asList(toWrite), StandardCharsets.UTF_8,
                    Files.exists(filePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe) {
            // Add your own exception handling...
        }
    }

    private void postRequest(HttpsURLConnection post, String s, String path) {
        Date start = new Date();
        HttpsURLConnection connection = post;
        connection.setRequestProperty("Content-Type", "application/json");

        writeInRequest(connection, s);

        boolean wait = true;
        String jsonString = getResponse(connection, wait, true, s, path);
        Date end = new Date();
        printTook((wait ? end.getTime() - 1005 : end.getTime()) - start.getTime(), path, jsonString);
    }

    private void postActivity(long start, long end, long steps, String type) {
        try {
            JSONObject toSend = new JSONObject();
            JSONArray activities = new JSONArray();
            JSONObject activity = new JSONObject();
            activity.put("start", start);
            activity.put("end", end);
            activity.put("type", type);
            activity.put("steps", steps);

            activities.put(activity);
            toSend.put("activities", activities);
            postRequest(getConnection("POST", activityPath, true, true, false), toSend.toString(), activityPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAchievements() {
        Date start = new Date();
        HttpsURLConnection connection = getConnection("GET", achievementsPath, false, true, false);
        boolean wait = false;
        String returnString = getResponse(connection, wait, false, "", achievementsPath);
        Date end = new Date();
        printTook((wait ? end.getTime() - 1005 : end.getTime()) - start.getTime(), achievementsPath, returnString);
    }

    private void postExercises() {
        postRequest(getConnection("POST", exercisePath, true, true, false), exercisePlan.toString(), exercisePath);
    }

    private HttpsURLConnection getConnection(String type, String path, boolean input, boolean output, boolean basic) {
        try {
            URL url = new URL(http + host + path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", tokenType + " " + accessToken);

            if (basic) {
                String encoded = Base64.getEncoder().encodeToString((client + ":" + secret).getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Authorization", "Basic " + encoded);
            }

            connection.setDoOutput(output);
            if (input) connection.setDoInput(input);
            if (output) connection.setDoOutput(output);
            connection.setRequestMethod(type);
            connection.setUseCaches(false);

            return connection;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void resetElastic() throws IOException, JSONException, InterruptedException {
        print("Resetting ElasticSearch");
        getAccessToken();
        Date start = new Date();
        HttpsURLConnection connection = getConnection("GET", resetPath, false, true, false);

        boolean wait = false;
        String jsonString = getResponse(connection, wait, false, "", resetPath);
        Date end = new Date();
        printTook((wait ? end.getTime() - 1005 : end.getTime()) - start.getTime(), resetPath, jsonString);
    }

    private void getAccessToken() {
        try {
            String urlParameters =
                    "scope=" + scope + "&" +
                            "grant_type=" + grantType + "&" +
                            "username=" + username + "&" +
                            "password=" + password;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            Date start = new Date();
            HttpsURLConnection connection = getConnection("POST", oauthPath, false, true, true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData);
            }

            boolean wait = true;
            String jsonString = getResponse(connection, wait, false, "", "oauthPath");

            JSONObject jsonObj = new JSONObject(jsonString);
            accessToken = jsonObj.getString("access_token");
            tokenType = jsonObj.getString("token_type");
            tokenType = tokenType.substring(0, 1).toUpperCase() + tokenType.substring(1);
            Date end = new Date();
            printTook((wait ? end.getTime() - 1005 : end.getTime()) - start.getTime(), oauthPath, jsonString);
        } catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getResponse(HttpsURLConnection connection, boolean wait, boolean achievement, String request, String path) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            if (wait) {
                TimeUnit.MILLISECONDS.sleep(1005);
                if (achievement) getAchievements();
            }

            print("Request response " + jsonString.toString());
            return jsonString.toString();
        } catch(InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
                br.close();
                System.out.println("Error response for user " + username + " " + jsonString.toString());
                printError(request, path, jsonString.toString());
                if(connection.getResponseCode() == 500) return getResponse(connection, wait, achievement, request, path);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    private void writeInRequest(HttpsURLConnection connection, String s) {
        try {
            connection.setRequestProperty("Content-Length", Integer.toString(s.getBytes().length));
            DataOutputStream dataoutput = new DataOutputStream(connection.getOutputStream());
            dataoutput.write(s.getBytes("UTF-8"));
            dataoutput.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void createUser() {
        Date start = new Date();
        HttpsURLConnection connection = getConnection("POST", createUserPath, true, true, false);

        connection.setRequestProperty( "Content-Type", "application/json");
        writeInRequest(connection, createUserValues);

        boolean wait = true;
        String jsonString = getResponse(connection, wait, false, createUserValues, createUserPath);
        Date end = new Date();
        printTook((wait ? end.getTime() - 1005 : end.getTime()) - start.getTime(), createUserPath, jsonString);
    }
}
