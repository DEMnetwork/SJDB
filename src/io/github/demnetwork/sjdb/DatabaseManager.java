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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import java.util.Base64.Encoder;
import io.github.demnetwork.sjdb.cryptography.AES;
import io.github.demnetwork.sjdb.dbelements.DBInteger;
import io.github.demnetwork.sjdb.dbelements.DBRootElement;
import io.github.demnetwork.sjdb.dbelements.DBString;
import io.github.demnetwork.sjdb.dbelements.DBTempElement;
import io.github.demnetwork.sjdb.exceptions.BadCredentialsException;
import io.github.demnetwork.sjdb.exceptions.IllegalDatabaseFormat;
import io.github.demnetwork.sjdb.exceptions.InvalidLengthException;
import io.github.demnetwork.sjdb.internal.*;
import io.github.demnetwork.sjdb.internal.Warning.WarningDescription;

public final class DatabaseManager {
    private final AES AES_Utility = new AES(256, 65536);
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";
    private final String DEFAULT_DATA = "\n";

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
        if (password.length() < 8 || password.equals("password") || password.isEmpty()) {
            Warning w = new Warning(SJDBRuntime.getNextWarningID(), 4, "Weak Password",
                    Warning.SecurityCategory.CATEGORY);
            WarningDescription d1 = w
                    .createDescription(
                            "Found weak password, it is highly recommended to change your password using \'Database.save(String username, String password)\'.");
            WarningDescription d2 = w.createDescription(
                    "After changing the password, make sure to update any old references to the old password, to avoid a BadCredentialsException in the code.");
            WarningDescription d3 = w.createDescription(
                    "To avoid security problems always make sure to change your passwords periodicly. I AM NOT RESPONSIBLE FOR DATA LOSS DUE TO WEAK");
            WarningDescription d4 = w.createDescription("CREDENTIALS FOR THE DATABASE.");
            w.setDescription(new WarningDescription[] { d1, d2, d3, d4 });
            SJDBRuntime.throwWarning(w);
        }
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
            throw t;
        }
        String sHash = "";
        for (int i = 0; i < sha3512hash.length; i += 4) {
            sHash = sHash + Integer
                    .toHexString(Byte.toUnsignedInt(sha3512hash[i]) + (Byte.toUnsignedInt(sha3512hash[i + 1]) * 256)
                            + (Byte.toUnsignedInt(sha3512hash[i + 2]) * 65536)
                            + (Byte.toUnsignedInt(sha3512hash[i + 3])) * 16777216);
        }
        // System.out.println(sHash);
        File db = new File(path);
        Scanner rd = new Scanner(db);
        String dbHash = "";
        long dbTime = 0;
        boolean found = false;
        int fTimes = 0;
        while (rd.hasNextLine()) {
            String s = rd.nextLine();
            // System.out.println(s);
            if (s.startsWith("CRD=")) {
                found = true;
                fTimes += 1;
                dbHash = s.substring(4, 131);
                try {
                    dbTime = Long.parseLong(s.substring(131));
                } catch (NumberFormatException nfe) {
                    rd.close();
                    throw new IllegalDatabaseFormat("Database is not formatted correctly. ", nfe);
                }
                // System.out.println(dbHash);
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
            String s = rd2.nextLine();
            if (s.startsWith("CRD=")) {
            } else if (s.startsWith("[DATA]") || s.startsWith("[/DATA]")
                    || s.startsWith("RML=")) {
                if (s.startsWith("RML=")) {
                    rmls = true;
                    rml = Integer.parseInt(s.substring(7));
                }
            } else {
                db_data = db_data + s;
            }
        }
        String data = AES_Utility.decrypt(db_data,
                (dbHash + user + user.length() + b64sdata + password + password.length()), "ef9da3c" + dbTime);
        rd2.close();
        try {
            if (rmls == false) {
                return new Database(data, path,
                        (dbHash + user + user.length() + b64sdata + password + password.length()),
                        ("ef9da3c" + dbTime));
            } else {
                return new Database(data, path,
                        (dbHash + user + user.length() + b64sdata + password + password.length()),
                        ("ef9da3c" + dbTime), rml);
            }
        } catch (BadPaddingException bpe) {
            throw new BadCredentialsException("The Credentials are invalid. ", bpe);
        }
    }

    public void createDatabase(String user, String password, String path) throws IOException {
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                throw new IOException("File already exists");
            }
            String d = DEFAULT_DATA;
            Database b = new Database(d, path, " ", " ");
            b.setElement(
                    new DBTempElement(
                            "You've made it! You created your own Database. (This message will disappear in 2 minutes)",
                            120),
                    0);

            b.save(user, password);
        } catch (Throwable err) {
            if (err instanceof IOException) {
                throw new IOException("File already exists");
            }
        }
    }

    @SuppressWarnings("unused")
    private void upgrade(String path, String user, String password, int srcDFV) throws FileNotFoundException {
        DatabaseParser p = DatabaseParsers.get(srcDFV).getParser();
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
            if (rd.nextLine().startsWith("CRD=")) {
                found = true;
                fTimes += 1;
                dbHash = rd.nextLine().substring(5, 131);
                try {
                    dbTime = Long.parseLong(rd.nextLine().substring(131));
                } catch (NumberFormatException nfe) {
                    rd.close();
                    throw new IllegalDatabaseFormat("The database is not formatted properly. ", nfe);
                }
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
            String s = rd2.nextLine();
            if (s.startsWith("CRD=")) {
            } else if (s.startsWith("[DATA]") || s.startsWith("[/DATA]")
                    || s.startsWith("RML=")) {
                if (s.startsWith("RML=")) {
                    rmls = true;
                    rml = Integer.parseInt(s.substring(7));
                }
            } else {
                db_data = db_data + s;
            }
        }
        rd2.close();
        String data = "";
        try {
            data = AES_Utility.decrypt(db_data,
                    (dbHash + user + user.length() + b64sdata + password + password.length()), "ef9da3c" + dbTime);
        } catch (Throwable err) {
            throw new IllegalArgumentException("Unable to upgrade Database file", err);
        }
        DBRootElement DBroot = null;
        if (rmls == false) {
            DBroot = new DBRootElement();
        } else {
            DBroot = new DBRootElement(rml);
        }
        DBRootElement DBRoot2 = p.parse(data, DBroot);
        try {
            new Database(DBRoot2, path, (dbHash + user + user.length() + b64sdata + password + password.length()),
                    ("ef9da3c" + dbTime)).save(user, password);
        } catch (Throwable err) {
            throw new IllegalArgumentException("Unable to upgrade Database file", err);
        }
    }

    static enum DatabaseParsers {
        DFV_1(1, new DatabaseParser() {
            public DBRootElement parse(String s, DBRootElement DBroot) {
                return parse(s, DBroot, null);
            }

            @SuppressWarnings("unused")
            public DBRootElement parse(String s, DBRootElement DBroot, DatabaseParserExtension DPE) {
                final DBRootElement root = DBroot;
                int ai = 0;
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (c == '[') {
                        // System.out.println(s.substring(i));
                        boolean end = false;
                        for (int i2 = i; !end; i2++) {
                            // The code below parses an DBTempElement
                            if ((s.substring(i2)).startsWith("[TEMPE]")) {
                                i2 = i2 + "[TEMPE]".length();
                                // System.out.println(s.substring(i2));
                                String msg = "";
                                long duration = 0L;
                                if ((s.substring(i2).startsWith("[MSG]"))) {
                                    i2 = i2 + ("[MSG]".length());
                                    boolean end2 = false;
                                    for (int i3 = i2; !end2; i3++) {
                                        char c2 = s.charAt(i3);
                                        // System.out.println("Char at " + i3 + ": " + c2);
                                        if (c2 == '[') {
                                            msg = s.substring(i2, i3);
                                            i2 = i3 + ("[/MSG]".length());
                                            end2 = true;
                                        }
                                    }
                                } else {
                                    throw new IllegalDatabaseFormat(
                                            "Expected \'[MSG]\' at " + i2 + " but found: \'"
                                                    + s.substring(i2, i2 + 5) + "\'");
                                }
                                if ((s.substring(i2)).startsWith("[DURATION]")) {
                                    boolean end3 = false;
                                    i2 = i2 + ("[DURATION]".length());
                                    // System.out.println((s.substring(i2)));
                                    for (int i3 = i2; !end3; i3++) {
                                        char c3 = s.charAt(i3);
                                        // System.out.println("Char at " + i3 + ": " + c3);
                                        if (c3 == '[') {
                                            duration = Long.parseLong(s.substring(i2, i3 - 1));
                                            i2 = i3 + ("[/DURATION][/TEMPE]".length());
                                            end3 = true;
                                        }
                                    }
                                } else {
                                    System.out.println((s.substring(i2)));
                                    throw new IllegalDatabaseFormat(
                                            "Expected \'[DURATION]\' at " + i2 + " but found: \'"
                                                    + s.substring(i2, i2 + 10) + "\'");
                                }
                                root.set(ai, new DBTempElement(msg, duration));
                                ai = ai + 1;
                                i = i2;
                                end = true;
                                // break;
                            } else if ((s.substring(i2)).startsWith("[INTEGER]")) {
                                i2 = i2 + ("[INTEGER]".length());
                                int v = 0;
                                String s1 = "";
                                boolean b1 = false;
                                boolean b2 = false;
                                if (s.substring(i2).startsWith("[PROPERTIES]")) {
                                    i2 = i2 + ("[PROPERTIES] name=\'".length());
                                    boolean end2 = false;
                                    for (int i3 = i2; !end2; i3++) {
                                        char c2 = s.charAt(i3);
                                        // System.out.println(c2);
                                        if (c2 == '\'') {
                                            s1 = s.substring(i2, i3);
                                            i2 = i3 + 10;
                                            end2 = true;
                                        }
                                    }
                                    i2 += 1;
                                    if (s.substring(i2).startsWith("true")) {
                                        b1 = true;
                                        i2 += 10;
                                    } else if (s.substring(i2).startsWith("false")) {
                                        b1 = false;
                                        i2 += 11;
                                    } else {
                                        throw new IllegalDatabaseFormat("Invalid boolean");
                                    }
                                    // System.out.println(s.substring(i2));
                                    if (s.substring(i2).startsWith("true")) {
                                        b2 = true;
                                        i2 += 6;
                                    } else if (s.substring(i2).startsWith("false")) {
                                        b2 = false;
                                        i2 += 7;
                                    } else {
                                        throw new IllegalDatabaseFormat("Invalid boolean");
                                    }
                                    i2 += 23;
                                } else {
                                    throw new IllegalDatabaseFormat("Unable to parse Database: Unparseable DBInteger");
                                }
                                // System.out.println(s.substring(i2));
                                boolean end2 = false;
                                for (int i3 = i2; !end2; i3++) {
                                    char c2 = s.charAt(i3);
                                    if (c2 == ' ') {
                                        v = Integer.parseInt(s.substring(i2, i3));
                                        i2 = i3 + 20;
                                        end2 = true;
                                    }
                                }
                                root.set(ai, new DBInteger(s1, v, b1, b2));
                                ai += 1;
                                i = i2;
                                end = true;
                                // break;
                            } else if ((s.substring(i2)).startsWith("[STRING]")) {
                                i2 = i2 + ("[STRING]".length());
                                String v = "";
                                String s1 = "";
                                boolean b1 = false;
                                boolean b2 = false;
                                if (s.substring(i2).startsWith("[PROPERTIES]")) {
                                    i2 = i2 + ("[PROPERTIES] name=\'".length());
                                    boolean end2 = false;
                                    for (int i3 = i2; !end2; i3++) {
                                        char c2 = s.charAt(i3);
                                        // System.out.println(c2);
                                        if (c2 == '\'') {
                                            s1 = s.substring(i2, i3);
                                            i2 = i3 + 10;
                                            end2 = true;
                                        }
                                    }
                                    i2 += 1;
                                    // System.out.println(s.substring(i2));
                                    if (s.substring(i2).startsWith("true")) {
                                        b1 = true;
                                        i2 += 10;
                                    } else if (s.substring(i2).startsWith("false")) {
                                        b1 = false;
                                        i2 += 11;
                                    } else {
                                        throw new IllegalDatabaseFormat("Invalid boolean");
                                    }
                                    // System.out.println(s.substring(i2));
                                    if (s.substring(i2).startsWith("true")) {
                                        b2 = true;
                                        i2 += 6;
                                    } else if (s.substring(i2).startsWith("false")) {
                                        b2 = false;
                                        i2 += 7;
                                    } else {
                                        throw new IllegalDatabaseFormat("Invalid boolean");
                                    }
                                    i2 += 23;
                                } else {
                                    throw new IllegalDatabaseFormat("Unable to parse Database: Unparseable DBString");
                                }
                                // System.out.println(s.substring(i2));
                                boolean end2 = false;
                                for (int i3 = i2; !end2; i3++) {
                                    char c2 = s.charAt(i3);
                                    if (c2 == '[') {
                                        v = s.substring(i2, i3);
                                        i2 = i3 + "[/STRDATA][/STRING]".length();
                                        end2 = true;
                                    }
                                }
                                root.set(ai, new DBString(v, s1, b1, b2));
                                ai += 1;
                                i = i2;
                                end = true;
                                // break;
                            } else {
                                if (DPE != null) {
                                    DPE.parse(s.substring(i2), root);
                                    i2 = DPE.endIndex();
                                }
                            }
                            i = i2;
                            break;
                            // System.out.println("Current Index:" + i2 + " (Character: " + s.charAt(i2) +
                            // ") ");
                        }
                    }
                }
                return root;
            }
        });

        private final int DFV;
        private final DatabaseParser Parser;

        private DatabaseParsers(int DFV, DatabaseParser Parser) {
            this.DFV = DFV;
            this.Parser = Parser;
        }

        public static DatabaseParsers get(int DFV) {
            for (DatabaseParsers dbp : DatabaseParsers.values()) {
                if (dbp.DFV == DFV) {
                    return dbp;
                }
            }
            throw new IllegalArgumentException("Non-existant Database Format Version");
        }

        public DatabaseParser getParser() {
            Class<?> c = GetInvoker.lookupCallerClass();
            if (!(c.equals(DBRootElement.class) || c.equals(Database.class))) {
                throw new SecurityException(
                        "Access denied: The invoker class(" + c.getName() + ") does not have access to this method");
            }
            return this.Parser;
        }
    }

    protected static interface DatabaseParser {
        public abstract DBRootElement parse(String s, DBRootElement root);

        public abstract DBRootElement parse(String s, DBRootElement root, DatabaseParserExtension DPE);
    }

    public static interface DatabaseParserExtension {
        public abstract DBRootElement parse(String s, DBRootElement root);

        public abstract int endIndex();
    }

    /*
     * public void startCLI(InputStream input, PrintStream output) {
     * Runnable r = new Runnable() {
     * 
     * @Override
     * public void run() {
     * Scanner CLIInput = new Scanner(input);
     * Database cDB = null;
     * boolean cliExec = true;
     * while (cliExec) {
     * output.print("\n>");
     * String s = CLIInput.nextLine();
     * switch (s) {
     * case "ACCESSDB":
     * output.print("\nLocation: ");
     * boolean rl = true;
     * String path = "";
     * while (rl) {
     * String s1 = CLIInput.nextLine();
     * if (new File(s1).exists() && !(new File(s1).isDirectory())) {
     * path = s1;
     * rl = false;
     * } else {
     * rl = false;
     * output.print("\nThe location is a directory or it does not exist!\n");
     * }
     * }
     * output.print("\nUsername: ");
     * String username = CLIInput.nextLine();
     * output.print("\nPassword: ");
     * String password = CLIInput.nextLine();
     * try {
     * cDB = new DatabaseManager().access(path, username, password);
     * } catch (Throwable err) {
     * output.print("\n\nUnable to access the Database");
     * }
     * break;
     * }
     * }
     * }
     * 
     * };
     * }
     */
}
