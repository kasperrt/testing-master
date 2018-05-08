import org.json.JSONException;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {

    private static int THREADS = 1;
    private static boolean reset = true;
    static ArrayList<TestClass> elementLists = new ArrayList<TestClass>();

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

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        try {
            long date = (new Date()).getTime();
            String thisFile = date + ".csv";

            if(reset) {
                TestClass newTest = new TestClass("testing", thisFile);
                newTest.resetElastic();
            }

            final ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
            final ArrayList<Callable<String>> tasks = new ArrayList<>();
            for (int i = 0; i < THREADS; i++) {
                TestClass newTest = new TestClass("testing" + i, thisFile);
                //newTest.startTest(0);
                elementLists.add(newTest);
                tasks.add(() -> {
                    newTest.startTest(0);
                    return "";
                });
            }

            //System.out.println("This is after the for loop");
            executorService.invokeAll(tasks);
            System.out.println("After invoking");
            System.out.println("Saved as file " + date + ".csv");
            ChartDrawing drawing = new ChartDrawing();
            drawing.startDrawing(date + "");
            System.exit(1);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
