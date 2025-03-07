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

package io.github.demnetwork.sjdb.internal;

import java.util.*;

@SJDBInternal("SJDBRuntime") // It is not recommeneded to modify the SJDBRuntime class for compatibilty with
                             // SJDBShell.
public final class SJDBRuntime {
    private SJDBRuntime() {
        throw new UnsupportedOperationException("Cannot instantiate SJDBRuntime");
    }

    static {
        chkVer();
    }

    private static void chkVer() {
        int v = 0;
        try {
            v = Integer.parseInt(System.getProperty("java.version").substring(0, 2)); // Java 10+
        } catch (NumberFormatException e) {
            v = Integer.parseInt(new String(new char[] { System.getProperty("java.version").charAt(0) })); // Java 6-9
        }
        if (v >= 18) {
            return;
        }
        Warning w = new Warning(getNextWarningID(), 3, "Unsupported Java Version",
                Warning.CompatibilityCategory.CATEGORY);
        w.setDescription(new Warning.WarningDescription[] {
                w.createDescription(
                        "This SJDB Version is meant to be used along with Java 18, but the current version is"),
                w.createDescription(
                        "not supported. It is highly recommended to update to Java 18 or higher for a better "),
                w.createDescription(
                        "experience.")
        });
        throwWarning(w);
    }

    private static int nextWarningID = 1;
    private static int nextPlacement = 0;
    private static final Warning[] warnings = new Warning[256];
    private static final int BUILD_NUMBER = 1;
    private static final String VERSION = "v1.0.0";
    private static final List<Warning> archivedWarnings = new ArrayList<>();
    private static boolean archiveWarnings = false;
    @SuppressWarnings("exports")
    public static final Class<SJDBInternal> SJDBInternal_CLASS = SJDBInternal.class;
    public static final KeyGen BASIC_KEYGEN = KeyGen.getInstance(1234567890123456789L, 16384);
    @SuppressWarnings("exports")
    public static final Class<Nothing> NOTHING_PLACEHOLDER_CLASS = Nothing.class;
    private static final Object lock0 = new Object();

    public static final int getBuildNumber() {
        return BUILD_NUMBER;
    }

    public static final String getVersion() {
        return VERSION;
    }

    public static void throwWarning(Warning w) {
        if (w == null)
            throw new NullPointerException("Null Warning");

        if (nextPlacement == 256) {
            nextPlacement = 0;
            if (archiveWarnings) {
                archivedWarnings.add(warnings[0].clone());
            }
        }
        warnings[nextPlacement++] = w;
    }

    public static int getNextWarningID() {
        synchronized (lock0) {
            return nextWarningID++;
        }
    }

    public static Warning getWarning(boolean remove) {
        Warning w = warnings[0];
        if (w == null) {
            throw new IllegalStateException("No warnings");
        }
        if (remove) {
            warnings[0] = null;
            System.arraycopy(getWarnings(), 1, warnings, 0, warnings.length - 1);
            nextPlacement--;
        }
        return w.clone();
    }

    public static Warning[] getWarnings() {
        return warnings.clone();
    }

    public static Warning getWarning(int index) {
        return warnings[index].clone();
    }

    public static void startWarningArchive() {
        if (archiveWarnings) {
            throw new IllegalStateException("Already Archiving Warnings");
        }
        archiveWarnings = true;
    }

    public static List<Warning> getArchivedWarnings() {
        if (!archiveWarnings) {
            throw new IllegalStateException("Warnings are not beings archived yet");
        }
        return Arrays.asList(archivedWarnings.toArray(new Warning[archivedWarnings.size()]));
    }

    public static void clearArchivedWarnings() {
        if (!archiveWarnings)
            throw new IllegalStateException("Warnings are not beings archived yet");
        archivedWarnings.clear();
    }

    public static Warning[] getWarningsByCategory(Warning.WarningCategory c) {
        if (c == null) {
            throw new NullPointerException("Null category");
        }
        Warning[] warningA = new Warning[256];
        int toPlace = 0;
        for (int i = 0; i < warnings.length; i++) {
            if (c.isWarningInCategory(warnings[i])) {
                warningA[toPlace++] = warnings[i].clone();
            }
        }
        return warningA;
    }
}
