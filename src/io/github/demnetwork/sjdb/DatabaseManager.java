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

import java.io.File;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import java.util.Base64.Encoder;
import io.github.demnetwork.sjdb.cryptography.AES;
import io.github.demnetwork.sjdb.exceptions.BadCredentialsException;
import io.github.demnetwork.sjdb.exceptions.IllegalDatabaseFormat;
import io.github.demnetwork.sjdb.exceptions.InvalidLengthException;

public class DatabaseManager {
    private final AES AES_Utility = new AES(256, 65536);

    public DatabaseManager() {
    }

    /**
     * This method is used to access a Database This method decrypts the database
     * and instantiates a new {@link io.github.demnetwork.sjdb.Database
     * Database}.
     * 
     * @param path     Location of the Database
     * @param user     Username
     * @param password Password
     * @return A instance of {@link io.github.demnetwork.sjdb.Database Database}
     *         based on the data found in the file.
     * @throws Throwable
     */
    public Database access(String path, String user, String password)
            throws Throwable {
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
        File db = new File(path);
        Scanner rd = new Scanner(db);
        String dbHash = "";
        long dbTime = 0;
        boolean found = false;
        int fTimes = 0;
        while (rd.hasNextLine()) {
            if (rd.nextLine().startsWith("CRD = ")) {
                found = true;
                fTimes += 1;
                dbHash = rd.nextLine().substring(5, 134);
                dbTime = Long.parseLong(rd.nextLine().substring(135));
            }
        }
        rd.close();
        if (found != true || fTimes > 1) {
            throw new IllegalDatabaseFormat();
        }
        if (!(dbHash.equals(sHash))) {
            throw new BadCredentialsException();
        }
        if (dbTime <= 0) {
            throw new IllegalDatabaseFormat();
        }
        String db_data = "";
        Scanner rd2 = new Scanner(db);
        int rml = 0;
        boolean rmls = false;
        while (rd2.hasNextLine()) {
            if (rd2.nextLine().startsWith("CRD = ")) {
            } else if (rd2.nextLine().startsWith("[DATA] ") || rd2.nextLine().startsWith(" [/DATA]")
                    || rd2.nextLine().startsWith("RML = ")) {
                if (rd2.nextLine().startsWith("RML = ")) {
                    rmls = true;
                    rml = Integer.parseInt(rd2.nextLine().substring(7));
                }
            } else {
                db_data = db_data + rd2.nextLine();
            }
        }
        rd2.close();
        String data = AES_Utility.decrypt(db_data,
                (dbHash + user + user.length() + b64sdata + password + password.length()), "ef9da3c" + dbTime);
        if (rmls == false) {
            return new Database(data, path, (dbHash + user + user.length() + b64sdata + password + password.length()),
                    ("ef9da3c" + dbTime));
        } else {
            return new Database(data, path, (dbHash + user + user.length() + b64sdata + password + password.length()),
                    ("ef9da3c" + dbTime), rml);
        }
    }
}
