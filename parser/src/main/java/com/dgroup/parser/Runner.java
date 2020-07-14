package com.dgroup.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


class RunnerThread extends Thread {

    Proxy proxy;

    @Override
    public void run() {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("213.85.92.10", 80));

        File fin = new File("problems_region.txt");
        File fout = new File("new_city_region.txt");
        File fouterror = new File("city_not_found.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(fin))) {
            FileOutputStream fos = new FileOutputStream(fout);
            FileOutputStream erfos = new FileOutputStream(fouterror);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            BufferedWriter bwer = new BufferedWriter(new OutputStreamWriter(erfos));
            int count = 0;

            String line;
            String last = "";
            while ((line = br.readLine()) != null) {
                System.out.println("read line " + line);
                try {
                    if (line.isEmpty() || line.equals(last)) {
                        continue;
                    }
                    last = line;
                    JSONArray jarr = new JSONArray(excutePost("http://nominatim.openstreetmap.org/search/?format=json&q=" + line, ""));
                    if (jarr.length() > 0) {
                        JSONObject jobj = jarr.getJSONObject(0);
                        String name = jobj.getString("display_name");
                        String[] names = name.split(",");
                        if (names.length > 3) {
                            bw.write(line + " " + names[2].trim().replace(" ", "_"));
                            bw.newLine();
                        } else {
                            bwer.write(line + " " + name);
                            bw.newLine();
                        }

                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


//		    	count++;
//		    	if(count>2){
//		    		break;
//		    	}
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String excutePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}


public class Runner {

    public static void main(String[] args) {

//		System.out.println(Translit.toTranslit("Ярцево"));

//		RunnerThread myThread = new RunnerThread();
//		myThread.start();

//        fixProblems();

//		clearCapitals();

//        new Thread(new BanksTester()).start();
    }

    private static void update() {
        File fin = new File("city_region.txt");
//
        File fout = new File("city_region2.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(fin))) {
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            int count = 0;

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("read line " + line);
                count++;
                //  		int index = line.indexOf(" ");

                String first = line.replace("Калмыкия", "Республика_калмыкия");

//				String first = line.replace("Бурятия","Республика_бурятия");
//				String second = first.replace("Корякский_округ","Камчатский_край");
//				String third = second.replace("Марий_Эл","Республика_марий_эл");
//				String fourth = third.replace("Башкортостан","Республика_башкортостан");
//				String fith = fourth.replace("Адыгея","Республика_адыгея");
//
//				String first = line.replace("Карачаево-Черкесия","Карачаево-Черкесская_республика");
//				String second = first.replace("Чукотка","Чукотский_автономный_округ");
//				String third = second.replace("Ставрополье","Ставропольский_край");
//				String fourth = third.replace("Дагестан","Республика_дагестан");
//				String fith = line.replace("Ханты-Мансийский_автономный_округ_-_Югра","Ханты-Мансийский_автономный_округ");
                bw.write(first);
                bw.newLine();

            }
            System.out.println("End line =" + count);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fixProblems() {
        File fin = new File("city_region_without_capitals.txt");

        File fout = new File("city_region_without_capitals2.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(fin))) {
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            int count = 0;

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("read line " + line);
                count++;
                if (!line.isEmpty()) {
                    String region = line.split(" ")[1];
                    if (region.contains("Ленинградская_область")) {
                       line = line.split(" ")[0] +" "+"Санкт_петербург_и_область";
                    }
                    bw.write(line);
                    bw.newLine();
                }
//                if (!line.isEmpty()) {
//                    String region = line.split(" ")[1];
//                    if (!region.contains("край")
//                            && !region.contains("республика")
//                            && !region.contains("Республика")
//                            && !region.contains("область")
//                            && !region.contains("Область")
//                            && !region.contains("Ханты-Мансийский_автономный_округ")
//                            && !region.contains("Ямало-Ненецкий_автономный_округ")
//                            ) {
//                        bw.write(line);
//                        bw.newLine();
//                    }
//                }
            }
            System.out.println("End line =" + count);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearCapitals() {
        File fin = new File("city_region.txt");
        File capitals = new File("capital.txt");
        File fout = new File("city_region_without_capitals.txt");

        Set<String> capitalsSet = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fin));
             BufferedReader br2 = new BufferedReader(new FileReader(capitals))) {

            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            int count = 0;

            String capital;

            while ((capital = br2.readLine()) != null) {
                System.out.println("read line " + capital);
                capitalsSet.add(capital.toLowerCase());
            }

            String line;

            while ((line = br.readLine()) != null) {
                System.out.println("read line " + line);
                count++;

                if (!line.isEmpty()) {
                    String city = line.split(" ")[0];
                    if (!capitalsSet.contains(city.toLowerCase())) {
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
            System.out.println("End line =" + count);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class BanksTester implements Runnable {

    @Override
    public void run() {
        try {
            File fin = new File("city_region_without_capitals.txt");
            File fOutOk = new File("city_region_without_capitals_exist.txt");
            File fOutErr = new File("city_region_without_capitals_not_exist.txt");
            try (BufferedReader br = new BufferedReader(new FileReader(fin))) {

                FileOutputStream fos = new FileOutputStream(fOutOk);
                BufferedWriter brfOutOk = new BufferedWriter(new OutputStreamWriter(fos));

                FileOutputStream fos1 = new FileOutputStream(fOutErr);
                BufferedWriter brfOutErr = new BufferedWriter(new OutputStreamWriter(fos1));

                String line;
                int count = 0;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    System.out.println("read line " + line + " count = " + count);
                    count++;

                    if (!line.isEmpty()) {
                        String[] arr = line.split(" ");
                        sb.setLength(0);
                        sb.append("http://www.banki.ru/products/currency/cash/");
                        sb.append(Translit.toTranslit(arr[1].toLowerCase()));
                        sb.append("/");
                        sb.append(Translit.toTranslit(arr[0].toLowerCase()));
                        sb.append("/");
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(sb.toString()).get();
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println("bank NOT exist");
                            brfOutErr.write(line+" "+sb);
                            brfOutErr.newLine();
                            try{
                                Thread.sleep(1000);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                            continue;
                        }

                        Elements banksNames = doc.select("a[href]");
                        boolean isOk = false;
                        for (Element element : banksNames) {
                            if (element.attributes().size() > 1 && element.attributes().hasKey("data-currencies-bank-name")) {
                                System.out.println("bank exist");
                                brfOutOk.write(line);
                                brfOutOk.newLine();
                                isOk = true;
                                break;
                            }
                        }
                        if (!isOk) {
                            System.out.println("bank NOT exist");
                            brfOutErr.write(line+" "+sb);
                            brfOutErr.newLine();
                        }
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
                brfOutOk.flush();
                brfOutOk.close();

                brfOutErr.flush();
                brfOutErr.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


