import org.json.JSONException;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Start {

    private static int THREADS = 8;
    private static int USERNUMBER = 8;
    private static int doneRemoval = 0;
    public static String thisFile;
    private static int MAXWEEKS = 3;
    private static boolean different_start = false;
    public static String typeSetup = "clustered/manual-refresh";
    private static boolean segmenting = typeSetup.contains("segmenting");
    public static boolean manualRefresh = typeSetup.contains("manual-refresh");
    static ArrayList<RequestClass> elementLists = new ArrayList<>();
    private static long lastEndDate = 0L;
    public static final String queryUrl = "10.53.43.122";


    private static void disableSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
        if(different_start) {
            USERNUMBER = 3;
            THREADS = 3;
            typeSetup += "-different-start";
        }

        System.out.println("Segmenting: " + segmenting);
        System.out.println("Manual-refresh: " + manualRefresh);
        System.out.println("Type run: " + typeSetup);

        Date startTesting = new Date();

        disableSSL();

        long date = (new Date()).getTime();
        thisFile = date + ".csv";
        RequestClass firstReset = new RequestClass("test", thisFile, typeSetup);

        int calendarDay = 1;
        Date thisDate;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, calendarDay);
        thisDate = c.getTime();
        System.out.println(thisDate.getTime());
        thisDate.setTime(thisDate.getTime() - 31556926000L);
        thisDate.setHours(0);
        thisDate.setMinutes(0);
        thisDate.setSeconds(0);

        setAppClock(thisDate);

        try {
            firstReset.createUser();
            firstReset.resetElastic();
            System.out.println("Reset, waiting 20 seconds");
            wait(20000, true);

            if(segmenting) {
                System.out.println("Done waiting for resetting, sending segmenting now");
                postForceMerge();
                System.out.println("Segmenting done, waiting 20 seconds for good measure");
                wait(20000, true);
            }
            System.out.println("Done waiting, starting parallel requests");
            for(int i = 0; i < THREADS; i++) {
                elementLists.add(new RequestClass("test-user-" + i, thisFile, typeSetup));
            }

            startTestPlan(0);

            System.out.println("Start ChartDrawing with id " + date);
            System.out.println(typeSetup);
            //Application.launch(ChartDrawing.class, date + "", typeSetup);
            Date endTesting = new Date();
            System.out.println("Whole testing took: " + ((endTesting.getTime() - startTesting.getTime()) / 1000) + " seconds.");

            ChartDrawing chart = new ChartDrawing();
            chart.startDrawing();
            System.out.println("Start ChartDrawing with id " + date);
            System.exit(1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("qqq");
        }
    }

    private static void postForceMerge() {
        try {
            URL url = new URL("http://" + queryUrl + ":9200/data_description/_forcemerge?only_expunge_deletes=false&max_num_segments=1&flush=true");

            String auth = Base64.getEncoder().encodeToString(("elastic:changeme").getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + auth);
            InputStream content = (InputStream)connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            while((line = in.readLine()) != null) {
                System.out.println("Response forcemerge " + line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setAppClock(Date nowDate) {
        URL url = null;
        try {
            url = new URL("https://" + queryUrl + "/clock/" + nowDate.getTime());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();

            System.out.println("Set time to " + jsonString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createUsers() {
        addParallel("create");
    }

    private static void addParallel(final String type) {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        final ArrayList<Callable<String>> tasks = new ArrayList<>();

        for(RequestClass element : elementLists) {
            tasks.add(() -> {
                if(type.equals("exercise")) {
                    element.postExercises();
                } else if(type.equals("education")) {
                    element.postEducation();
                } else if(type.equals("create")) {
                    element.createUser();
                }
                return "";
            });
        }

        try {
            executorService.invokeAll(tasks);
            executorService.shutdown();
            tasks.clear();
            wait(1005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void getPlans(Date thisDate) {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        final ArrayList<Callable<String>> tasks = new ArrayList<>();
        final ArrayList<Long> endDates = new ArrayList<>();
        for(RequestClass element : elementLists) {
            tasks.add(() -> {
                ArrayList<Long> thisEndTime = element.postNewPlan(thisDate.getTime());
                endDates.add(thisEndTime.get(0));
                Date endDate = new Date();
                endDate.setTime(thisEndTime.get(0) + thisEndTime.get(1));
                System.out.println("Supposed end-date " + endDate.toString());
                return "";
            });
        }

        try {
            executorService.invokeAll(tasks);
            for(int i = 0; i < endDates.size(); i++) {
                if(endDates.get(i) > lastEndDate) {
                    lastEndDate = endDates.get(i);
                }
            }
            executorService.shutdown();
            tasks.clear();
            wait(1005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void postExercises() {
        addParallel("exercise");
    }

    private static void postEducation() {
        addParallel("education");
    }

    private static void postActivities(int i, Date date) {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        final ArrayList<Callable<String>> tasks = new ArrayList<>();

        for(RequestClass element : elementLists) {
            tasks.add(() -> {
                element.postActivity(i, date);
                return "";
            });
        }

        try {
            executorService.invokeAll(tasks);
            executorService.shutdown();
            tasks.clear();
            //wait(1005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void getTailoring(Date date) {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        final ArrayList<Callable<String>> tasks = new ArrayList<>();

        for(RequestClass element : elementLists) {
            tasks.add(() -> {
                element.getTailoring(date);
                return "";
            });
        }

        try {
            executorService.invokeAll(tasks);
            executorService.shutdown();
            tasks.clear();
            wait(1005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    private static void wait(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void wait(int milliseconds, boolean countdown) {
        try {
            if(countdown) {
                wait(1000);
                System.out.println("Waiting " + ((milliseconds - 1000) / 1000));
                if(milliseconds - 1000 > 0) wait(milliseconds - 1000, countdown);
            } else {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startTestPlan(int week) {
        Date thisDate;
        Date endDate;
        int calendarDay = 1;
        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_WEEK, calendarDay);

        thisDate = c.getTime();
        thisDate.setTime(thisDate.getTime() - 31556926000L + (604800000 * week));
        thisDate.setHours(2);
        thisDate.setMinutes(0);
        thisDate.setSeconds(0);

        if(week == 0) {
            createUsers();
        }

        if((week == 2 || week == 4) && different_start) {
            System.out.println("Creating 3 new users");
            for(int i = 0; i < 3; i++) {
                RequestClass newUser = new RequestClass("test-user-" + (USERNUMBER + i + 1), thisFile, typeSetup);
                elementLists.add(newUser);
                try {
                    newUser.createUser();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                USERNUMBER++;
                THREADS = elementLists.size();
            }
        }
        getPlans(thisDate);
        thisDate.setTime(lastEndDate);
        System.out.println(lastEndDate);
        System.out.println(thisDate);
        System.out.println("hour " + thisDate.getHours());
        System.out.println("minutes " + thisDate.getMinutes());
        System.out.println("seconds " + thisDate.getSeconds());
        postExercises();
        postEducation();

        goToNextHour(week, 0, 0, thisDate);
    }

    private static void goToNextHour(int week, int day, int hour, Date thisDate) {
        Date endDate;

        thisDate.setHours(hour);
        thisDate.setMinutes(0);
        thisDate.setSeconds(0);
        endDate = (Date) thisDate.clone();
        endDate.setMinutes(59);
        endDate.setSeconds(59);

        setAppClock(thisDate);
        postActivities(hour + (day * hour), thisDate);
        System.out.println("\n\n");
        System.out.println("Now " + thisDate.toString());
        System.out.println("End " + endDate.toString());
        System.out.println("Week " + week);
        System.out.println("Day " + day);
        System.out.println("Hour " + hour);
        System.out.println("\n\n");
        //wait(5000);
        int _nextHour = hour + 1;
        int _nextDay = day;
        if(_nextHour > 23) {
            _nextHour = 0;
            _nextDay += 1;
            thisDate = addDays(thisDate, 1);
        }
        if(_nextDay > 6) {
            getTailoring(thisDate);
            if(week == MAXWEEKS && doneRemoval != 2 && different_start) {
                doneRemoval += 1;
                System.out.println("Size of elementlist " + elementLists.size());
                for(int i = 0; i < 3; i++) {
                    elementLists.remove(0);

                }
                THREADS = elementLists.size();
                System.out.println("Removed 3 elements, size is now " + elementLists.size());
                MAXWEEKS += 2;
            } else if(week == MAXWEEKS){
                System.out.println("Done with looping");
                return;
            }
            System.out.println(week + " " + MAXWEEKS);
            if(week == MAXWEEKS) return;
            startTestPlan(week + 1);
        } else {
            goToNextHour(week, _nextDay, _nextHour, thisDate);
        }
    }
}
