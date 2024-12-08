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
import io.github.demnetwork.sjdb.exceptions.InvalidLengthException;
import java.io.File;

/**
 * Represents the database file.
 * 
 * @apiNote To instantiate use {@link io.github.demnetwork.sjdb.DatabaseManager
 *          DatabaseManager} class.
 */
public class Database {
    private String ddata;
    private String key;
    private String salt;
    private final AES AES_Utility = new AES(256, 65536);
    private String path;
    private DBRootElement root;

    protected Database() {
    }

    protected Database(String Data, String path, String key, String salt) throws NullPointerException {
        if (Data.isEmpty() || Data == null) {
            throw new NullPointerException("The database data cannot be null");
        }
        if (path.isEmpty() || path == null) {
            throw new NullPointerException("The database path cannot be null");
        }
        if (key.isEmpty() || key == null) {
            throw new NullPointerException("The database key cannot be null");
        }
        this.ddata = Data;
        this.key = key;
        this.salt = salt;
        this.root = new DBRootElement();
    }

    protected Database(String Data, String path, String key, String salt, int RootMaxLength)
            throws NullPointerException {
        if (Data.isEmpty() || Data == null) {
            throw new NullPointerException("The database data cannot be null");
        }
        if (path.isEmpty() || path == null) {
            throw new NullPointerException("The database path cannot be null");
        }
        if (key.isEmpty() || key == null) {
            throw new NullPointerException("The database key cannot be null");
        }
        this.ddata = Data;
        this.key = key;
        this.salt = salt;
        this.root = new DBRootElement(RootMaxLength);
    }

    /**
     * This method is used to save the {@link io.github.demnetwork.sjdb.Database
     * Database} to a file
     * 
     * @param user     new Username
     * @param password new Password
     * @throws Throwable
     */
    public void save(String user, String password) throws Throwable {
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
            t.printStackTrace();
        }
        String sHash = "";
        for (int i = 0; i < sha3512hash.length; i += 4) {
            sHash = sHash + Integer
                    .toHexString(Byte.toUnsignedInt(sha3512hash[i]) + (Byte.toUnsignedInt(sha3512hash[i + 1]) * 256)
                            + (Byte.toUnsignedInt(sha3512hash[i + 2]) * 65536)
                            + (Byte.toUnsignedInt(sha3512hash[i + 3])) * 16777216);
        }
        String data = AES_Utility.encrypt(ddata,
                (sHash + user + user.length() + b64sdata + password + password.length()),
                "ef9da3c" + cTime);
        key = (sHash + user + user.length() + b64sdata + password + password.length());
        salt = "ef9da3c" + cTime;
        FileWriter writeDB = new FileWriter(path, false);
        writeDB.write("[DATA]\n" + data + "\n[/DATA]\n\nCRD = " + sHash + cTime + "\n");
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
     * It updates the data of the
     */
    public void update() {
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
            this.ddata = AES_Utility.decrypt(db_data, key, salt);
        } catch (Throwable err) {
            err.printStackTrace();
        }
    }
}
