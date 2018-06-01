import javafx.application.Application;
import org.json.JSONException;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Start {

    private static int THREADS = 1;
    private static boolean doneRemoval = false;
    private static String thisFile;
    private static int MAXWEEKS = 5;
    private static String typeSetup = "default-shards-one-node-one-user";
    static ArrayList<RequestClass> elementLists = new ArrayList<>();
    private static long lastEndDate = 0L;

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
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
        Date startTesting = new Date();
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

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
        thisDate.setHours(2);
        thisDate.setMinutes(0);
        thisDate.setSeconds(0);

        setAppClock(thisDate);

        try {
            firstReset.createUser();
            firstReset.resetElastic();
            System.out.println("Reset, waiting 8 seconds");
            wait(8005);
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

    private static void setAppClock(Date nowDate) {
        URL url = null;
        try {
            url = new URL("https://localhost/clock/" + nowDate.getTime());
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
            wait(1005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void getPlans() {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        final ArrayList<Callable<String>> tasks = new ArrayList<>();
        final ArrayList<Long> endDates = new ArrayList<>();
        for(RequestClass element : elementLists) {
            tasks.add(() -> {
                endDates.add(element.postNewPlan());
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

        /*if(week == 3) {
            RequestClass newUser = new RequestClass("test-user-" + THREADS + 1, thisFile, typeSetup);
            elementLists.add(newUser);
            THREADS++;
            try {
                newUser.createUser();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RequestClass newUser1 = new RequestClass("test-user-" + THREADS + 1, thisFile, typeSetup);
            elementLists.add(newUser1);
            THREADS++;
            try {
                newUser1.createUser();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RequestClass newUser2 = new RequestClass("test-user-" + THREADS + 1, thisFile, typeSetup);
            elementLists.add(newUser2);
            try {
                newUser2.createUser();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            THREADS++;
        }*/
        getPlans();
        thisDate.setTime(lastEndDate);
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

        setAppClock(endDate);
        postActivities(hour, thisDate);
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
            /*if(week == MAXWEEKS && !doneRemoval) {
                doneRemoval = true;
                System.out.println("Size of elementlist " + elementLists.size());
                elementLists.remove(0);
                elementLists.remove(0);
                elementLists.remove(0);
                elementLists.remove(0);
                elementLists.remove(0);
                THREADS = elementLists.size();
                System.out.println("Removed 5 elements, size is now " + elementLists.size());
                MAXWEEKS += 3;
            } else {
                System.out.println("Done with looping");
                return;
            }*/
            if(week == MAXWEEKS) return;
            startTestPlan(week + 1);
        } else {
            goToNextHour(week, _nextDay, _nextHour, thisDate);
        }
    }
}
