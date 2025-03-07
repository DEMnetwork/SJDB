/*
 *   Copyright (c) 2025 DEMnetwork
 *   All rights reserved.

 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package io.github.demnetwork.sjdb.logging;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.*;
import io.github.demnetwork.sjdb.*;
import io.github.demnetwork.sjdb.dbelements.DBRootElement;
import io.github.demnetwork.sjdb.internal.GetInvoker;

public final class DatabaseLogger extends java.lang.Object {
    private final Database db; // Database to log
    private logThread lT; // Current Logging thread
    private boolean logging; // Boolean containing informations weather it is logging
    private static long lm = 0; // The next ID of the next logger instance created
    private static final Object lock = new Object(); // Prevent damage from Race Conditions
    private long thisID;
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";
    private final static Map<Long, DatabaseLogger> m = new HashMap<>();

    public DatabaseLogger(Database db) {
        if (db == null) {
            throw new IllegalArgumentException("Found Null Value.", new NullPointerException("The Database is null"));
        }
        this.db = db;
        this.lT = null;
        this.logging = false;
        this.thisID = -1; // Sets the ID to the Undefined Value
        this.thisID = this.getID(); // Gets an unique ID for this instance
    }

    private long getID() {
        // Checks if the ID was already assinged to a valid value
        if (this.thisID != -1) {
            throw new IllegalStateException("This DatabaseLogger already got an ID");
        }
        // Prevent race conditions
        synchronized (lock) {
            m.put(Long.valueOf(lm), this);
            return lm++; // Returns the ID and increments the 'lm' variable
        }
    }

    public void startLogging() {
        if (this.logging == false) {
            this.logging = true;
            this.lT = new logThread();
            Thread t = new Thread(lT);
            t.setDaemon(true);
            t.setName("Logger-" + this.thisID);
            t.start();
        } else {
            throw new IllegalStateException("This logger is already logging");
        }
    }

    public boolean isLogging() {
        return this.logging;
    }

    private final class logThread implements Runnable {
        private long pause;
        private final static Class<? extends Database> dbc = Database.class;
        private Field f2;
        private DBRootElement dbre;
        private boolean b;
        private Object[] data;
        private String input;
        private boolean fi;

        private logThread() {
            try {
                pause = 50;
                dbre = db.getRoot();
                f2 = dbc.getDeclaredField("isNuked");
                f2.setAccessible(true);
                b = ((Boolean) f2.get(db)).booleanValue();
                data = dbre.get();
                input = "";
                fi = false;
            } catch (Exception e) {
                log(e);
                logging = false;
                return;
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (!Arrays.equals(data, dbre.get())) {
                        logInfo("Database Root Element data modified was modified.");
                        data = dbre.get();
                    }
                    if (b != ((Boolean) f2.get(db)).booleanValue()) {
                        logWarn("Database was nuked.");
                        b = ((Boolean) f2.get(db)).booleanValue();
                    }
                    if (fi) {
                        fi = false;
                        logWarn(input);
                    }
                    Thread.sleep(pause);
                }
            } catch (Exception e) {
                f2.setAccessible(false);
                log(e);
                logging = false;
                return;
            }
        }
    }

    private void log(Throwable t) {
        String s = t.getMessage();
        StackTraceElement[] dst = t.getStackTrace();
        Throwable t2 = t.getCause();
        this.logError("Exception caught: " + t.getClass().getName() + ": " + s + ":");
        for (int i = 0; i < dst.length; i++) {
            this.logError("\tat " + dst[i].toString());
        }
        if (t2 == null) {
            return;
        }
        logCause(t2);
    }

    private void logCause(Throwable t) {
        String s = t.getMessage();
        StackTraceElement[] dst = t.getStackTrace();
        Throwable t2 = t.getCause();
        this.logError("Caused by: " + t.getClass().getName() + ": " + s + ":");
        for (int i = 0; i < dst.length; i++) {
            this.logError("\tat " + dst[i].toString());
        }
        if (t2 == null) {
            return;
        }
        logCause(t2);
    }

    private void logInfo(String s) {
        System.out.println("\u001B[37m [" + Thread.currentThread().getName() + "] [INFO] " + s);
    }

    private void logWarn(String s) {
        System.out.println("\u001B[33m [" + Thread.currentThread().getName() + "] [WARN] " + s + " \u001B[37m ");
    }

    private void logError(String s) {
        System.out.println("\u001B[31m [" + Thread.currentThread().getName() + "] [ERROR] " + s
                + "\u001B[37m ");
    }

    public synchronized void setLoggingInterval(long milisenconds) {
        if (logging && lT != null) {
            lT.pause = milisenconds;
        } else {
            throw new IllegalStateException("This DatabaseLogger is not logging");
        }
    }

    public static long getLoggerID(DatabaseLogger l) {
        return l.thisID;
    }

    public long getLoggerID() {
        return getLoggerID(this);
    }

    public synchronized void inputWarn(String s) {
        Class<?> c = GetInvoker.lookupCallerClass();
        if (!c.equals(Database.class)) {
            this.logWarn("Class \'" + c.getName() + "\' attemped to write to the DatabaseLogger Input");
            return;
        }
        if (lT != null && logging) {
            lT.input = s;
        }
        throw new IllegalStateException("This logger is not logging");
    }

    public static DatabaseLogger getLogger(Database db) {
        for (long i = 0; i < m.size(); i++) {
            DatabaseLogger l = m.get(Long.valueOf(i));
            if (l.db == db) {
                return l;
            }
        }
        return new DatabaseLogger(db);
    }

    public static boolean existsLoggerFor(Database db) {
        for (long i = 0; i < m.size(); i++) {
            DatabaseLogger l = m.get(Long.valueOf(i));
            if (l.db == db) {
                return true;
            }
        }
        return false;
    }
}
