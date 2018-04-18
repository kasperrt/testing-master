import org.json.JSONException;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {

    private static boolean reset = true;
    static ArrayList<TestClass> elementLists = new ArrayList<TestClass>();

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
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

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        try {
            if(reset) {
                TestClass newTest = new TestClass("test" + 0);
                newTest.resetElastic();
            }
            int threads = 4;
            final ExecutorService executorService = Executors.newFixedThreadPool(threads);
            final ArrayList<Callable<String>> tasks = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                TestClass newTest = new TestClass("test" + i);
                //newTest.startTest();
                elementLists.add(newTest);
                tasks.add(new Callable<String>()
                {
                    @Override
                    public String call() throws Exception
                    {
                        newTest.startTest();
                        return "";
                    }
                });
            }

            System.out.println("This is after the for loop");
            executorService.invokeAll(tasks);
            System.out.println("After invoking");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
