/*
 *   Copyright (c) 2024 DEMnetwork
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

package io.github.demnetwork.sjdb;

import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Scanner;
import io.github.demnetwork.sjdb.cryptography.AES;
import io.github.demnetwork.sjdb.dbelements.*;
import io.github.demnetwork.sjdb.exceptions.*;
import io.github.demnetwork.sjdb.internal.GetInvoker;
import io.github.demnetwork.sjdb.logging.DatabaseLogger;
import java.io.File;

/**
 * Represents the database file.
 * 
 * @apiNote To instantiate use {@link io.github.demnetwork.sjdb.DatabaseManager
 *          DatabaseManager} class.
 */
public final class Database {
    private String ddata;
    private boolean isNuked;
    private String key;
    private String salt;
    private static final AES AES_Utility;
    private final String path;
    private DBRootElement root;
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";
    public static final int SUPPORTED_DATABASE_FORMAT_VERSION = 1;
    public SecurityException lastSecurityException;
    public Exception lastException;
    public Throwable lastThrowable;
    private DatabaseLogger l;

    static {
        AES_Utility = new AES(256, 65536);
    }

    protected Database(String Data, String path, String key, String salt) throws Exception {
        this(new DBRootElement(Data), path, key, salt);
    }

    protected Database(String Data, String path, String key, String salt, int RootMaxLength)
            throws Exception {
        this(new DBRootElement(Data, RootMaxLength), path, key, salt);
    }

    protected Database(DBRootElement root, String path, String key, String salt) {
        if (path == null || path.isEmpty()) {
            throw new NullPointerException("The database path cannot be null");
        }
        if (key == null || key.isEmpty()) {
            throw new NullPointerException("The database key cannot be null");
        }
        if (root == null) {
            throw new NullPointerException("The root element cannot be null!");
        }
        this.key = key;
        this.salt = salt;
        this.root = root;
        this.path = path;
        this.isNuked = false;
        this.l = null;
        this.method_0001a();
    }

    /**
     * This method is used to save the {@link io.github.demnetwork.sjdb.Database
     * Database} to a file
     * 
     * @param user     new Username
     * @param password new Password
     * @throws java.lang.Throwable
     */
    public void save(String user, String password) throws Throwable {
        if (!isNuked) {
            this.method_0001a();
        }
        long cTime = System.currentTimeMillis();
        MessageDigest hash;
        byte[] sha3512hash = new byte[64];
        byte[] b64data;
        String b64sdata;
        if (user.length() >= 64) {
            throw new InvalidLengthException(user.length());
        }
        if (password.length() >= 64) {
            throw new InvalidLengthException(password.length());
        }
        Encoder e = Base64.getEncoder();
        b64data = e.encode((user.length() + "-_-" + password.length()).getBytes());
        b64sdata = new String(b64data);
        try {
            hash = MessageDigest.getInstance("SHA3-512");
            String toDigest = user + password + new String(new char[] { 'e', 'f', '9', 'd', 'a', '3', 'c' }) + " "
                    + b64sdata;
            sha3512hash = hash.digest(toDigest.getBytes());
        } catch (Throwable t) {
            this.lastThrowable = t;
        }
        String sHash = "";
        for (int i = 0; i < sha3512hash.length; i += 4) {
            sHash = sHash + Integer
                    .toHexString(Byte.toUnsignedInt(sha3512hash[i]) + (Byte.toUnsignedInt(sha3512hash[i + 1]) * 256)
                            + (Byte.toUnsignedInt(sha3512hash[i + 2]) * 65536)
                            + (Byte.toUnsignedInt(sha3512hash[i + 3])) * 16777216);
        }
        key = (sHash + user + user.length() + b64sdata + password + password.length());
        salt = "ef9da3c" + cTime;
        String data = AES_Utility.encrypt(ddata, key, salt);

        FileWriter writeDB = new FileWriter(path, false);
        writeDB.write("[DATA]\n" + data + "\n[/DATA]\nCRD=" + sHash + cTime + "\nRML=" + root.getMaxElementCount()
                + "\n");
        writeDB.close();
    }

    public void setElement(DBElement<?> e, int index) {
        this.root.set(index, e);
    }

    public Object get(int index) {
        return this.root.get(index);
    }

    public DBRootElement getRoot() {
        return root;
    }

    /**
     * It updates the data of the {@link io.github.demnetwork.sjdb.Database}
     */
    public void update() {
        if (isNuked) {
            throw new BadDataBaseState();
        }
        try {
            String db_data = "";
            Scanner rd = new Scanner(new File(path));
            while (rd.hasNextLine()) {
                if (rd.nextLine().startsWith("CRD = ")) {
                } else if (rd.nextLine().startsWith("[DATA] ") || rd.nextLine().startsWith(" [/DATA]")
                        || rd.nextLine().startsWith("RML = ")) {
                } else {
                    db_data = db_data + rd.nextLine();
                }
            }
            rd.close();
            this.parse(AES_Utility.decrypt(db_data, key, salt));
        } catch (Throwable err) {
            this.lastThrowable = err;
        }
    }

    /**
     * Destroys all the imformation of the database
     * 
     */
    public void nuke() {
        try {
            isNuked = true;
            MessageDigest md = MessageDigest.getInstance("SHA3-512");
            String s = new String(md.digest(ddata.getBytes()));
            ddata = AES_Utility.encrypt(s, "f78hwe7fuwea9dawsuhdcaz9dx7uhaidasduewieoioefchufehf",
                    ((int) (Math.random() * 10000000)) + "ewg8h9ewrfgh79wefc8uyweyugfdweuoyfgyuwe"
                            + System.currentTimeMillis());
            this.save("USERNAME_IS_CORRUPTEDhdrtgwerewrtrty657yrtte4r",
                    "PASSWORDisSAFE"
                            + ((int) (Math.random() * 10000000)) + "LOLewg8h9ewrfgh79wefc8uyweyugfdweuo"
                            + System.currentTimeMillis());
        } catch (Throwable t) {
            this.lastThrowable = t;
        }
    }

    /**
     * Don't let no one run this method, except the
     * <code>save(String, String)</code>
     * method
     */
    private synchronized void method_0001a() {
        Class<?> c = GetInvoker.lookupCallerClass();
        if (!c.equals(Database.class)) {
            this.lastSecurityException = new SecurityException(
                    "Access Denied: Class \'" + c.getName() + "\' access to \'Database.method_0001a()\' was denied");
            throw new SecurityException(
                    "Access Denied: Class \'" + c.getName() + "\' access to \'Database.method_0001a()\' was denied");
        }
        try {
            ddata = root.clone().toString();
        } catch (SecurityException se) {
            // If the current SecurityManager does not allow any of the operations made by
            // DBRootElement.clone(), this will be invoked to prevent issues
            ddata = root.toString();
            this.lastSecurityException = se;
        } catch (Exception e) {
            ddata = root.toString(); // Get a string representation without using the clone due to cloning failure
            this.lastException = e;
        }
    }

    private void parse(String s) {
        try {
            root = DatabaseManager.DatabaseParsers.get(Database.SUPPORTED_DATABASE_FORMAT_VERSION).getParser().parse(s,
                    root);
        } catch (Exception e) {
            this.lastException = e;
        }
    }

    protected void setRoot(DBRootElement root) {
        Class<?> c = GetInvoker.lookupCallerClass();
        if (!c.equals(Database.class) || !c.equals(DatabaseManager.class)) {
            this.lastSecurityException = new SecurityException(
                    "Access Denied: Class \'" + c.getName()
                            + "\' access to \'Database.setRoot(DBRootElement)\' was denied");
            throw new SecurityException(
                    "Access Denied: Class \'" + c.getName()
                            + "\' access to \'Database.setRoot(DBRootElement)\' was denied");
        }
        if (DatabaseLogger.existsLoggerFor(this) && l == null) {
            l = DatabaseLogger.getLogger(this);
            l.inputWarn("An attemp to modify the root was made");
        } else if (l != null) {
            l.inputWarn("An attemp to modify the root was made");
        }
        if (this.root == root) {
            throw new IllegalArgumentException("The new root and the current root point to the same Object in memory!");
        }
        this.root = root;
    }
}
