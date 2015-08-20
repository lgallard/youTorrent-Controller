package com.lgallardo.youtorrentcontroller;

import android.app.Notification;
import android.app.Notification.InboxStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lgallard on 2/22/15.
 */
public class NotifierService extends BroadcastReceiver {

    public static String completed_hashes;
    // Cookie (SID - Session ID)
    public static String cookie = null;
    public static String token = null;
    protected static HashMap<String, Torrent> last_completed, completed, notify;
    protected static String hostname;
    protected static String subfolder;
    protected static int port;
    protected static String protocol;
    protected static String username;
    protected static String password;
    protected static boolean https;

    protected static int connection_timeout;
    protected static int data_timeout;
    protected static String sortby;

    protected static String lastState;
    protected static int httpStatusCode = 0;
    protected static int currentServer;
    protected static boolean enable_notifications;

    private static String[] params = new String[3];
    private static Context context;

    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;
    private String qbQueryString = "query";


    public NotifierService() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        getSettings();

//        Log.i("Notifier", "Cookie:" + cookie);
//        Log.i("Notifier", "hostname: " + hostname);
//        Log.i("Notifier", "port: " + port);
//        Log.i("Notifier", "usernmae: " + username);
//        Log.i("Notifier", "password: " + password);
//        Log.i("Notifier", "qb_version: " + qb_version);
//        Log.i("Notifier", "currentServer: " + currentServer);
//        Log.i("Notifier", "enable_notifications: " + enable_notifications);

        if (enable_notifications) {

            String state = "all";

            // Gett all torrents
            params[0] = "gui/?list=1&token=";

            // Set state
            params[1] = state;

            // Set token
            params[2] = token;

//            Log.i("Notifier", "onReceive reached");
            new torrentTokenTask().execute(params);

        }

    }

    protected void getSettings() {
        // Preferences stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        builderPrefs = new StringBuilder();

        builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

        // Get values from preferences
        currentServer = Integer.parseInt(sharedPrefs.getString("currentServer", "1"));

        hostname = sharedPrefs.getString("hostname", "");
        subfolder = sharedPrefs.getString("subfolder", "");

        protocol = sharedPrefs.getString("protocol", "NULL");

        // If user leave the field empty, set 8080 port
        try {
            port = Integer.parseInt(sharedPrefs.getString("port", "8080"));
        } catch (NumberFormatException e) {
            port = 8080;

        }
        username = sharedPrefs.getString("username", "NULL");
        password = sharedPrefs.getString("password", "NULL");
        https = sharedPrefs.getBoolean("https", false);

        // Check https
        if (https) {
            protocol = "https";

        } else {
            protocol = "http";
        }


        // Get connection and data timeouts
        try {
            connection_timeout = Integer.parseInt(sharedPrefs.getString("connection_timeout", "10"));
        } catch (NumberFormatException e) {
            connection_timeout = 10;
        }

        try {
            data_timeout = Integer.parseInt(sharedPrefs.getString("data_timeout", "20"));
        } catch (NumberFormatException e) {
            data_timeout = 20;
        }


        token = sharedPrefs.getString("token2", null);
        cookie = sharedPrefs.getString("cookie2", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", null);

        // Notifications
        enable_notifications = sharedPrefs.getBoolean("enable_notifications", false);
        completed_hashes = sharedPrefs.getString("completed_hashes" + currentServer, "");


    }

    private class torrentTokenTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            // Get values from preferences
            getSettings();

            // Creating new JSON Parser
            JSONParser jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            String[] tokenCookie = new String[2];
            String newToken = null;
            String newCookie = null;


            try {
                tokenCookie = jParser.getToken();
                newToken = tokenCookie[0];
                newCookie = tokenCookie[1];

            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
            }

            if (newToken == null) {
                newToken = "";
            }

            if (newCookie == null) {
                newCookie = "";
            }

            return new String[]{newToken, newCookie};

        }

        @Override
        protected void onPostExecute(String[] result) {

            if(NotifierService.token == null || !NotifierService.token.equals(result[0]) || NotifierService.cookie == null || !NotifierService.cookie.equals(result[1])) {

                NotifierService.token = result[0];
                NotifierService.cookie = result[1];


                // Save options locally
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPrefs.edit();

                // Save key-values
                editor.putString("token2", result[0]);
                editor.putString("cookie2", result[1]);


                // Commit changes
                editor.apply();

            }

            params[2] = token;


//            Log.d("Debug", "NotifierService - token: " + token);
//            Log.d("Debug", "NotifierService - cookie: " + cookie);
            // After getting the token & cookie, sedn the url or file

            // Set new Token and Cookie
            JSONParser.setToken(NotifierService.token);
            JSONParser.setCookie(NotifierService.cookie);

            // Fetch torrent data
            new FetchTorrentListTask().execute(params);


        }
    }

    class FetchTorrentListTask extends AsyncTask<String, Integer, Torrent[]> {

        // Torrent Info TAGs
        protected static final String TAG_NAME = "name";
        protected static final String TAG_SIZE = "size";
        protected static final String TAG_PROGRESS = "progress";
        protected static final String TAG_STATE = "state";
        protected static final String TAG_HASH = "hash";
        protected static final String TAG_DLSPEED = "dlspeed";
        protected static final String TAG_UPSPEED = "upspeed";
        protected static final String TAG_NUMLEECHS = "num_leechs";
        protected static final String TAG_NUMSEEDS = "num_seeds";
        protected static final String TAG_RATIO = "ratio";
        protected static final String TAG_PRIORITY = "priority";
        protected static final String TAG_ETA = "eta";

        @Override
        protected Torrent[] doInBackground(String... params) {

            String name, size, info, progress, state, hash, ratio, priority, eta, uploadSpeed, downloadSpeed, status, label, availability;
            int  peersConnected, peersInSwarm, seedsConnected, seedInSwarm;
            boolean completed = false;

            Torrent[] torrents = null;

            // Get settings
            getSettings();

            JSONParser jParser;

            int httpStatusCode = 0;

//            Log.i("Notifier", "Getting torrents");

            try {

                // Creating new JSON Parser
                jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);
                jParser.setToken(NotifierService.token);
                jParser.setCookie(NotifierService.cookie);
//
//                Log.d("Debug", "NotifierService - Token: " + token);
//                Log.d("Debug", "NotifierService - Cookie: " + cookie);


                // Getting torrents

                // Get the complete JSON
//                Log.d("Debug", "Getting JSON");

                JSONObject json = jParser.getJSONFromUrl(params[0]);

                // Get the torrents array
//                Log.d("Debug", "Getting torrents array");
                JSONArray jArray = json.getJSONArray("torrents");

//                Log.d("Debug", "N° Torrents:" + jArray.length());

                if (jArray != null) {

                    torrents = new Torrent[jArray.length()];


                    for (int i = 0; i < jArray.length(); i++) {

                        // Get each torrent as an JSON array
                        JSONArray torrentArray = jArray.getJSONArray(i);


                        // Get torrent info
                        hash = torrentArray.getString(0);
//                        Log.d("Debug", "Hash:" + hash);

                        // TODO: Delete status, dont' used
                        status = "" + torrentArray.getInt(1);

//                        Log.d("Debug", "Status:" + status);

                        name = torrentArray.getString(2);
                        size = "" + torrentArray.getLong(3);

//                        Log.d("Debug", "Name:" + name);
//                        Log.d("Debug", "Size:" + size);

                        // Move progress and ratio calculation to Common
                        progress = String.format("%.2f", (float) torrentArray.getInt(4)/10) + "%";
                        progress = progress.replace(",", ".");

//                        Log.d("Debug", "Progress:" + progress);

                        ratio =  String.format("%.2f", (float) torrentArray.getInt(7) / 1000);
                        ratio = ratio.replace(",", ".");

//                        Log.d("Debug", "Ratio:" + ratio);


                        downloadSpeed = "" + torrentArray.getInt(8);
                        uploadSpeed = "" + torrentArray.getInt(9);
                        eta = "" + torrentArray.getInt(10);

//                        Log.d("Debug", "Eta:" + eta);

                        label = torrentArray.getString(11);
                        peersConnected = torrentArray.getInt(12);
                        peersInSwarm = torrentArray.getInt(13);
                        seedsConnected = torrentArray.getInt(14);
                        seedInSwarm = torrentArray.getInt(15);
                        availability = "" + torrentArray.getInt(16) / 65536f;
                        priority = "" + torrentArray.getInt(17);

//                        Log.d("Debug", "peersConnected:" + peersConnected);
//                        Log.d("Debug", "peersInSwarm:" + peersInSwarm);
//                        Log.d("Debug", "seedsConnected:" + seedsConnected);
//                        Log.d("Debug", "seedInSwarm:" + seedInSwarm);
//

                        info = "";

                        // Adjust values
                        size = Common.calculateSize(size);
                        eta = Common.secondsToEta(eta);
                        downloadSpeed = Common.calculateSize(downloadSpeed) + "/s";
                        uploadSpeed = Common.calculateSize(uploadSpeed) + "/s";

                        if(torrentArray.getInt(4) >= 1000){
                            completed = true;
                        }else{
                            // Let's assume it's not completed
                            completed = false;
                        }

//                        Log.d("Debug", "Completed: " + torrentArray.getInt(4));


                        // Use status and progress to calculate torrent state
                        state = Common.getStateFromStatus(torrentArray.getInt(1),torrentArray.getInt(4));

//                        Log.d("Debug", "State:" + state);

                        torrents[i] = new Torrent(name, size, state, hash, info, ratio, progress, peersConnected, peersInSwarm,
                                seedsConnected, seedInSwarm, priority, eta, downloadSpeed, uploadSpeed, status, label, availability, completed);


                        // Get torrent generic properties

                        try {
                            // Calculate total downloaded
                            Double sizeScalar = Double.parseDouble(size.substring(0, size.indexOf(" ")));
                            String sizeUnit = size.substring(size.indexOf(" "), size.length());

                            torrents[i].setDownloaded(String.format("%.1f", sizeScalar * json.getDouble(TAG_PROGRESS)).replace(",", ".") + sizeUnit);

                        } catch (Exception e) {
                            torrents[i].setDownloaded(size);
                        }

                        // Info
                        torrents[i].setInfo(torrents[i].getDownloaded() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
                                + Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
                                + torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + torrents[i].getEta());

                    }

                }
            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
                torrents = null;
                Log.e("Notifier", e.toString());

            } catch (Exception e) {
                torrents = null;
                Log.e("Notifier:", e.toString());
            }

            return torrents;

        }

        @Override
        protected void onPostExecute(Torrent[] torrents) {

            Iterator it;

            last_completed = new HashMap<String, Torrent>();
            completed = new HashMap<String, Torrent>();
            notify = new HashMap<String, Torrent>();

            String[] completedHashesArray = completed_hashes.split("\\|");

            String completedHashes = null;

            String[] completedNames;


            for (int i = 0; i < completedHashesArray.length; i++) {
//                Log.i("Notifier", "Last completed - " + completedHashesArray[i]);
                last_completed.put(completedHashesArray[i], null);
            }

//            Log.i("Notifier", "LastCompleted Size: " + last_completed.size());
//            Log.i("Notifier", "LastCompleted Hashes: " + completed_hashes);

            if (torrents != null) {

                // Check torrents
                for (int i = 0; i < torrents.length; i++) {

                    // Completed torrents
                    if (torrents[i].getPercentage().equals("100")) {


                        completed.put(torrents[i].getHash(), torrents[i]);

                        // Build  completed hashes string here
                        if (completedHashes == null) {
                            completedHashes = torrents[i].getHash();
                        } else {
                            completedHashes += "|" + torrents[i].getHash();
                        }
                    }
                }


                // Save completedHashes
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPrefs.edit();

                // Save hashes
                editor.putString("completed_hashes" + currentServer, completedHashes);


                // Commit changes
                editor.apply();

                if (completed_hashes.equals("")) {
                    last_completed = completed;
                }

                // Check completed torrents not seen last time
                it = completed.entrySet().iterator();


                while (it.hasNext()) {

                    HashMap.Entry pairs = (HashMap.Entry) it.next();

                    String key = (String) pairs.getKey();
                    Torrent torrent = (Torrent) pairs.getValue();


                    if (!last_completed.containsKey(key)) {
                        if (!notify.containsKey(key)) {
                            notify.put(key, torrent);
                        }
                    }
                }


                // Notify completed torrents

                if (notify.size() > 0) {

                    String info = "";

//                    Log.i("Notifier", "Downloads completed");


                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from", "NotifierService");
                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    it = notify.entrySet().iterator();

                    while (it.hasNext()) {

                        HashMap.Entry pairs = (HashMap.Entry) it.next();

                        Torrent t = (Torrent) pairs.getValue();

                        if (info.equals("")) {
                            info += t.getFile();
                        } else {
                            info += ", " + t.getFile();
                        }

//                        it.remove(); // avoids a ConcurrentModificationException
                    }


                    // Build notification
                    // the addAction re-use the same intent to keep the example short
                    Notification.Builder builder = new Notification.Builder(context)
                            .setContentTitle(NotifierService.context.getString(R.string.notifications_completed_torrents))
                            .setContentText(info)
                            .setNumber(notify.size())
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true);


                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                    Notification notification;


                    if (android.os.Build.VERSION.SDK_INT >= 16) {

                        // Define and Inbox
                        InboxStyle inbox = new Notification.InboxStyle(builder);

                        inbox.setBigContentTitle(NotifierService.context.getString(R.string.notifications_completed_torrents));

                        completedNames = info.split(",");

                        for (int j = 0; j < completedNames.length && j < 4; j++) {
                            inbox.addLine(completedNames[j].trim());
                        }

                        inbox.setSummaryText(NotifierService.context.getString(R.string.notifications_total));

                        notification = inbox.build();
                    } else {
                        notification = builder.getNotification();
                    }


                    notificationManager.notify(0, notification);


                }


            }
        }

    }

    private class qBittorrentCookie extends AsyncTask<Void, Integer, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {

            // Get values from preferences
            getSettings();


            // Creating new JSON Parser
            JSONParser jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            String cookie = "";
            String api = "";


            try {

                cookie = jParser.getNewCookie();
//                api = jParser.getApiVersion();

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();
                Log.i("Notifier", "httpStatusCode: " + httpStatusCode);

            }

            if (cookie == null) {
                cookie = "";

            }

            if (api == null) {
                api = "";

            }

            return new String[]{cookie, api};

        }

        @Override
        protected void onPostExecute(String[] result) {


            cookie = result[0];


            // Save options locally
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            // Save key-values
            editor.putString("qbCookie2", result[0]);


            // Commit changes
            editor.apply();

        }
    }

}

