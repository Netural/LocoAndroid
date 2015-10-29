package com.netural.loco.base;

import com.netural.loco.base.parser.XMLAndroidParser;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xml.sax.SAXException;

/**
 * Created by alexandra.fehkuehrer on 23.09.15.
 */
public class LocoLibrary {

    protected static final String LOCO_URL = "https://localise.biz:443/api/";
    protected static final String LOCO_LOCALES = "locales";
    protected static final String LOCO_TRANSLATIONS = "translations/";
    protected static final String LOCO_EXPORT = "export/";
    protected static final String LOCO_ARCHIVE = "archive/";
    protected static final String LOCO_IMPORT = "import/";
    protected static final String LOCO_ASSETS = "assets";
    protected static final String LOCO_LOCALE = "locale=";
    protected static final String LOCO_FORMAT_XML = "xml";
    protected static final String LOCO_ZIP = ".zip";
    protected static final String LOCO_JSON = ".json";
    protected static final String LOCO_KEY = "key=";
    protected static final String LOCO_FALLBACK = "fallback";
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private String apiKeyFromLoco;

    public LocoLibrary(String apiKeyFromLoco) {
        this.setApiKeyFromLoco(apiKeyFromLoco);
    }

    public String getApiKeyFromLoco() {
        return apiKeyFromLoco;
    }

    public void setApiKeyFromLoco(String apiKeyFromLoco) {
        this.apiKeyFromLoco = apiKeyFromLoco;
    }

    // ------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------

    public File getAllZip(String path, String name) throws Exception {
        return this.downloadZipFile(this.getURLExportArchiveXML(), path, name);
    }

    public String getLocales() throws Exception {
        return this.sendGet(this.getURLLocales());
    }

    public String getTranslations(String id) throws Exception {
        return this.sendGet(this.getURLTranslations(id));
    }

    public String getTranslations(String id, String locale) throws Exception {
        return this.sendGet(this.getURLTranslations(id, locale));
    }

    public String getAssets() throws Exception {
        return this.sendGet(this.getURLAssets());
    }

    // writes name as translation into the default locale
    public void setAsset(String name, String id) throws Exception {

        String urlParameters = "name=" + name + "&id=" + id;

        this.sendPost(this.getURLAssets(), urlParameters);
    }

    public void loadUnzipAndSaveAll(String path) throws IOException {

        File zipFile = this.downloadZipFile(this.getURLExportArchiveXML(), path, "LOCO");

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        ZipInputStream zis = null;

        try {

            //create output directory is not exists
            File outputFolder = new File(path);
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            //get the zip file content
            zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                String[] parts = fileName.split("/");
                if (parts.length >= 3) {
                    fileName = parts[parts.length - 2] + File.separator + parts[parts.length - 1];
                } else {
                    fileName = parts[parts.length - 1];
                }

                File newFile = new File(outputFolder + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                if (!fileName.endsWith(File.separator)) {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            zipFile.delete();
            System.out.println("Done loading and unpacking");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    // ------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------

    private File downloadZipFile(String url, String destination, String name) throws IOException {

        new File(destination).mkdirs();
        File zipFile = new File(destination, name + ".zip");
        InputStream is = null;
        FileOutputStream os = null;
        try {
            URL urlConnection = new URL(url);
            is = urlConnection.openStream();

            os = new FileOutputStream(zipFile);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done loading Zip File");
        return zipFile;
    }

    // ------------------------------------------
    // get URLs
    // ------------------------------------------

    private String getURLExportArchiveXML() {
        // e.g.: https://localise.biz:443/api/export/archive/xml.zip?key=1234
        String url = LOCO_URL + LOCO_EXPORT + LOCO_ARCHIVE + LOCO_FORMAT_XML + LOCO_ZIP + "?" + LOCO_KEY + this.apiKeyFromLoco;

        return url;
    }

    private String getURLLocales() {
        // e.g.: https://localise.biz:443/api/locales?key=1234
        String url = LOCO_URL + LOCO_LOCALES + "?" + LOCO_KEY + this.apiKeyFromLoco;

        return url;
    }

    private String getURLTranslations(String id) {
        // e.g.: https://localise.biz:443/api/translations/loginLoginBtn.json?key=1234
        String url = LOCO_URL + LOCO_TRANSLATIONS + id + LOCO_JSON + "?" + LOCO_KEY + this.apiKeyFromLoco;

        return url;
    }

    private String getURLTranslations(String id, String locale) {
        // e.g.: https://localise.biz:443/api/translations/Auto2/es?key=3de2799e82f9bce408a61d9152a6c7d0
        String url = LOCO_URL + LOCO_TRANSLATIONS + id + "/" + locale + "?" + LOCO_KEY + this.apiKeyFromLoco;

        return url;
    }

    private String getURLAssets() {
        // e.g.: https://localise.biz:443/api/assets?key=1234
        String url = LOCO_URL + LOCO_ASSETS + "?" + LOCO_KEY + this.apiKeyFromLoco;

        return url;
    }

    private String getURLImport(String locale) {
        // e.g.: https://localise.biz:443/api/import/xml?locale=es&key=1234
        String url = LOCO_URL + LOCO_IMPORT + LOCO_FORMAT_XML + "?" + LOCO_LOCALE + locale + "&" + LOCO_KEY + this.apiKeyFromLoco;

        return url;
    }

    // ------------------------------------------
    // GET and POST
    // ------------------------------------------

    // HTTP GET request
    private String sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    // HTTP POST request
    private void sendPost(String url, String urlParameters) throws Exception {

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    public HashMap<String, String> getLanguage(String path, String locale) {
        HashMap<String, String> language = null;

        File languageFile = getLanguageFile(path, locale);

        try {
            language = XMLAndroidParser.parseFile(languageFile.getAbsolutePath());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return language;
    }

    public File getLanguageFile(String path, String language) {
        if (!path.endsWith("/")) {
            path += "/";
        }

        File zipDirectory = new File(path);

        String[] directories = zipDirectory.list(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File current, String name) {
                        return new File(current, name).isDirectory();
                    }
                });

        File valueDirectory = null;

        if (language != null) {
            for (String directory : directories) {
                if (directory.contains(language)) {
                    valueDirectory = new File(zipDirectory + "/" + directory);
                }
            }
        }

        if (valueDirectory == null) {
            valueDirectory = new File(zipDirectory + "/values");
        }

        String stringPath = valueDirectory + "/strings.xml";

        File file = new File(stringPath);

        if (file.exists()) {
            return file;
        }
        return null;
    }

    public LocoInfo getInfo(String path) {
        File file = new File(path, "README.txt");

        StringBuilder text = new StringBuilder();
        String project = "";
        String release = "";
        Date lastUpdate = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains(":")) {
                    String key = line.substring(0, line.indexOf(":")).trim().toLowerCase();
                    String value = line.substring(line.indexOf(":") + 1, line.length()).trim();

                    if (key.contains("project")) {
                        project = value;
                    }
                    if (key.contains("release")) {
                        release = value;
                    }
                    if (key.contains("exported at")) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MMM y hh:mm:ss Z", Locale.ENGLISH);
                            lastUpdate = simpleDateFormat.parse(value);
                        } catch (ParseException e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return new LocoInfo(project, release, lastUpdate);
    }
}
