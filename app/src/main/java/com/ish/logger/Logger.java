package com.ish.logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class Logger {
    private static final String TAG = "Logger";
    private static PrintStream stream;
    private static long MAX_FILE_SIZE = 2 * 1024 * 1024; // Five Mega Bytes
    private static File outputFile;
    private static File outputTempFile;
    private static FileOutputStream out;
    public static String logDir;
    private static int debugLevel = -Log.VERBOSE;
    private static String logDirectoryName = "temp";
    public static final String logFileName = "app_log.txt";
    public static final String logOldFileName = "app_log_Old.txt";

    private static final String LOG_PREFERENCE = "app_log_pref";
    private static final String LOG_LEVEL = "app_log_level";

    /**
     *Sets logger level for the application. Based on log level set logs will be written to the file.
     * @param minimumLogLevel minimum log level to be written to the file
     * @param context
     */
    public static void setLoggerLevel(Context context, int minimumLogLevel) {
        debugLevel = minimumLogLevel;
        SharedPreferences mPref = context.getSharedPreferences(LOG_PREFERENCE, Context.MODE_PRIVATE);
        mPref.edit().putInt(LOG_LEVEL, debugLevel).commit();
    }

    // No instantiation allowed
    private Logger() {

    }

    /**
     * Initializes the Logger to capture Logs.
     *
     * @param context
     */
    public static void init(Context context) {
        try {
            Log.e(TAG, "-----Logger init()-----");
            debugLevel = getDebugLevel(context);

            assignDirectoryName(context);

            logDir = Environment.getExternalStorageDirectory() + File.separator + logDirectoryName;
            try {
                File dir = new File(logDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                outputFile = new File(logDir + File.separator + logFileName);
                long fileSize = outputFile.length();

                if (fileSize > MAX_FILE_SIZE) {
                    outputTempFile = new File(logDir + File.separator + logOldFileName);
                    if (outputTempFile.exists())
                        outputTempFile.delete();
                    outputFile.renameTo(new File(logDir + File.separator + logOldFileName));
                    outputFile = new File(logDir + File.separator + logFileName);
                }
                //clearing the existed resources while initializing.
                if (out != null) {
                    out.close();
                }
                if (stream != null) {
                    stream.flush();
                }
                out = new FileOutputStream(outputFile, true);
                stream = new PrintStream(out);
            } catch (Exception e) {
                Log.e(TAG, "----Creation of logfile failed: " + e.getMessage());
            } finally {
                if (stream != null)
                    log(TAG, "Log file created...");
            }
        } catch (Exception e) {
            Log.e(TAG, "-----Logger init() Failed-----");
            e.printStackTrace();
        }
    }

    /**
     * Assigns log file directory name to the application name
     *
     * @param context
     */
    private static void assignDirectoryName(Context context) {
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(context.getPackageName(), 0);
            logDirectoryName = pm.getApplicationLabel(ai).toString();
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
    }

    public static void d(String tag, final String formatString,
                         final Object... objects) {
        try {
            if (debugLevel > Log.DEBUG)
                return;
            log(tag, String.format(formatString, objects));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void d(String tag, final Throwable t,
                         final String formatString, final Object... objects) {
        try {
            if (debugLevel > Log.DEBUG)
                return;
            log(tag, String.format(formatString, objects)
                    + Log.getStackTraceString(t));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void d(String tag, final Throwable t) {
        try {
            if (debugLevel > Log.DEBUG)
                return;
            log(tag, Log.getStackTraceString(t));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(String tag, final String formatString,
                         final Object... objects) {
        try {
            if (debugLevel > Log.WARN)
                return;
            log(tag, String.format(formatString, objects));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(String tag, final Throwable t,
                         final String formatString, final Object... objects) {
        try {
            if (debugLevel > Log.WARN)
                return;
            log(tag, String.format(formatString, objects)
                    + Log.getStackTraceString(t));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void i(String tag, final Throwable t,
                         final String formatString, final Object... objects) {
        try {
            if (debugLevel > Log.INFO)
                return;
            log(tag, String.format(formatString, objects));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void i(String tag, final String formatString,
                         final Object... objects) {
        try {
            if (debugLevel > Log.INFO)
                return;
            log(tag, String.format(formatString, objects));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String tag, final String formatString,
                         final Object... objects) {
        try {
            if (debugLevel > Log.ERROR)
                return;
            log(tag, String.format(formatString, objects));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String tag, final Throwable t,
                         final String formatString, final Object... objects) {
        try {
            if (debugLevel > Log.ERROR)
                return;
            log(tag, String.format(formatString, objects)
                    + Log.getStackTraceString(t));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void v(String tag, final String formatString,
                         final Object... objects) {
        try {
            if (debugLevel > Log.VERBOSE)
                return;
            log(tag, String.format(formatString, objects));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void v(String tag, final Throwable t,
                         final String formatString, final Object... objects) {
        try {
            if (debugLevel > Log.VERBOSE)
                return;
            log(tag, String.format(formatString, objects)
                    + Log.getStackTraceString(t));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes log to the file and prints in console
     *
     * @param tag
     * @param str
     */
    private synchronized static void log(String tag, String str) {

        try {
            if (stream != null) {
                try {

                    long fileSize = outputFile.length();

                    if (fileSize > MAX_FILE_SIZE) {
                        out.close();
                        stream.close();
                        outputTempFile = new File(logDir + File.separator + logOldFileName);
                        if (outputTempFile.exists())
                            outputTempFile.delete();
                        outputFile.renameTo(new File(logDir + File.separator + logOldFileName));
                        outputFile = new File(logDir + File.separator + logFileName);
                        out = new FileOutputStream(outputFile, true);
                        stream = new PrintStream(out);
                    }

                    stream.println(new Date().toString() + " " + tag + " " + str);
                    stream.flush();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            Log.d(tag, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to get the log level.
     * @param context
     * @return level set for logging
     */
    public static int getDebugLevel(Context context) {
        try {
            SharedPreferences mPref = context.getSharedPreferences(LOG_PREFERENCE, Context.MODE_PRIVATE);
            return mPref.getInt(LOG_LEVEL, debugLevel);
        } catch (Exception e) {
            e.printStackTrace();
            return debugLevel;
        }
    }
}