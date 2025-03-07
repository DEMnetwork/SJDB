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

import java.io.*;

public final class Warning {
    private final int ID;
    private final WarningDescription[] description;
    private final StackTraceElement[] callStack;
    private final String message;
    private final String threadName;
    private final WarningCategory category;

    public Warning(int id, int warningLength, String message, WarningCategory c) {
        this.ID = id;
        this.description = new WarningDescription[warningLength];
        Thread t = Thread.currentThread();
        StackTraceElement[] ste = t.getStackTrace();
        this.callStack = java.util.Arrays.copyOfRange(ste, 2, ste.length);
        this.message = message;
        this.threadName = t.getName();
        this.category = c;
        if (message == null) {
            throw new NullPointerException("Null Message");
        }
        if (c == null) {
            throw new NullPointerException("Null category");
        }
        if (message.contains("\n")) {
            throw new IllegalArgumentException("Cannot use Line Feed in message");
        }
        if (warningLength <= 0) {
            throw new IllegalArgumentException("Cannot make a warning with no description lines.");
        }
    }

    private Warning(Warning w) {
        this.ID = w.ID;
        this.description = w.description.clone();
        this.callStack = w.callStack;
        this.message = w.message;
        this.threadName = w.threadName;
        this.category = w.category;
    }

    public WarningDescription createDescription(String content) {
        return new WarningDescription(this.ID, this, content);
    }

    public Warning setDescription(WarningDescription[] description) {
        if (this.description[0] != null)
            throw new IllegalStateException("The description is already set.");
        if (description.length != this.description.length) {
            throw new IllegalArgumentException("Invalid description");
        }
        WarningDescription[] d = description.clone();
        for (int i = 0; i < this.description.length; i++) {
            if (d[i] == null) {
                throw new IllegalArgumentException("The description cannot contain null values");
            }
            this.description[i] = d[i];
        }
        return this;
    }

    public WarningDescription getDescription(int line) {
        WarningDescription d = description[line];
        if (d.warning != this || d.ID != this.ID) {
            throw new IllegalStateException("The description line is not compatible with the warning");
        }
        return d;
    }

    public WarningDescription[] getDescription() {
        return this.description.clone();
    }

    public int getID() {
        return this.ID;
    }

    public StackTraceElement[] getStackTrace() {
        return this.callStack.clone();
    }

    public String getMessage() {
        return this.message;
    }

    public void printWarning(PrintStream ps) {
        this.printWarning(this.category.getColorCode(), ps);
    }

    public void printWarning(String prefix, PrintStream ps) {
        ps.println(prefix + "WARNING(Category: " + this.category.getName() + ") at thread \"" + this.threadName + "\": "
                + message + "\n" + "\u001B[37m");
        for (int i = 0; i < description.length; i++) {
            ps.println(prefix + description[i].getContent() + "\u001B[37m ");
        }
        ps.println(prefix + "Here follows a StackTrace with more details: " + "\u001B[37m ");
        for (int i = 0; i < callStack.length; i++) {
            ps.println(prefix + "\tat " + callStack[i].toString() + "\u001B[37m ");
        }
    }

    public String getThreadName() {
        return threadName;
    }

    public WarningCategory getCategory() {
        return this.category;
    }

    public Warning clone() {
        return new Warning(this);
    }

    /**
     * A simpler getter for the Category color code.
     * 
     * @return the Value retruned from
     *         {@link io.github.demnetwork.sjdb.internal.Warning#getCategory()
     *         <code>getCategory()</code>}<code>.</code>{@link io.github.demnetwork.sjdb.internal.Warning.WarningCategory#getColorCode()
     *         <code>getColorCode()</code>}
     */
    public String getColorCode() {
        return this.getCategory().getColorCode();
    }

    public static final class WarningDescription {
        private final int ID;
        private final Warning warning;
        private final String content;

        private WarningDescription(int id, Warning parent, String content) {
            Class<?> caller = GetInvoker.internalMethod0003a();
            if (!caller.getName().startsWith("io.github.demnetwork.sjdb.internal.Warning")) {
                throw new IllegalCallerException("Caller(\'" + caller.getName() + "\'') is not trusted");
            }
            if (content == null) {
                throw new NullPointerException("Null Message.");
            }
            this.ID = id;
            this.warning = parent;
            this.content = content;
            if (content.contains("\n")) {
                Warning w = new Warning(SJDBRuntime.getNextWarningID(), 8, "Warning Formatting",
                        WarningFormattingCategory.CATEGORY);
                w.setDescription(new WarningDescription[] {
                        w.createDescription(
                                "The current format is not considered a good practice, since it breaks some methods from"),
                        w.createDescription(
                                "the class Warning, for example, \'Warning.getDescription(int line)\' and"),
                        w.createDescription(
                                "\'Warning.printWarning(String prefix, PrintStream ps)\'. Due to the Line Feed"),
                        w.createDescription(
                                "the method \'Warning.getDescription(int line)\' does not properly return the lines, "),
                        w.createDescription(
                                "since the lines are determined by Line Feed not by individual Warning.WarningDescription"),
                        w.createDescription(
                                "instances. This also prevents the prefix in the method"),
                        w.createDescription(
                                "\'Warning.printWarning(String prefix, PrintStream ps)\', which may not apply the prefix"),
                        w.createDescription(
                                "properly, which sometimes is not the ideal.") });
                SJDBRuntime.throwWarning(w);
            }
        }

        public final String getContent() {
            return content;
        }

        public int getID() {
            return this.ID;
        }

        public Warning getParentWarning() {
            return this.warning;
        }
    }

    public static abstract class WarningCategory {
        private final String name;
        private final boolean isCustom;
        private static final int CCLength = 15;

        protected WarningCategory(String name) {
            this("CustomCategory." + name, true);
        }

        private WarningCategory(String s, boolean b) {
            if (!GetInvoker.internalMethod0003a().getName().startsWith("io.github.demnetwork.sjdb.internal.Warning")) {
                throw new SecurityException("Cannot Invoke this Constructor");
            }
            this.name = s;
            this.isCustom = b;
        }

        public final String getName() {
            return this.name;
        }

        public final boolean isCustom() {
            return this.isCustom;
        }

        public final String getSimpleName() {
            if (this.name.startsWith("CustomCategory.")) {
                return this.name.substring(CCLength);
            }
            return this.getName();
        }

        public String getColorCode() {
            return "";
        }

        public abstract boolean isWarningInCategory(Warning w);
    }

    public static final class PerformanceCategory extends WarningCategory {
        public static final PerformanceCategory CATEGORY = new PerformanceCategory();

        private PerformanceCategory() {
            super("Performance", false);
        }

        @Override
        public boolean isWarningInCategory(Warning w) {
            return w.category == this;
        }

        @Override
        public String getColorCode() {
            return "\u001B[33m";
        }
    }

    public static final class SecurityCategory extends WarningCategory {
        public static final SecurityCategory CATEGORY = new SecurityCategory();

        private SecurityCategory() {
            super("Security", false);
        }

        @Override
        public boolean isWarningInCategory(Warning w) {
            return w.category == this;
        }

        @Override
        public String getColorCode() {
            return "\u001B[31m";
        }
    }

    public static final class WarningFormattingCategory extends WarningCategory {
        public static final WarningFormattingCategory CATEGORY = new WarningFormattingCategory();

        private WarningFormattingCategory() {
            super("Warning Formatting", false);
        }

        @Override
        public boolean isWarningInCategory(Warning w) {
            return w.category == this;
        }

        @Override
        public String getColorCode() {
            return "\u001B[33m";
        }
    }

    public static final class CompatibilityCategory extends WarningCategory {
        public static final CompatibilityCategory CATEGORY = new CompatibilityCategory();

        private CompatibilityCategory() {
            super("Warning Formatting", false);
        }

        @Override
        public boolean isWarningInCategory(Warning w) {
            return w.category == this;
        }

        @Override
        public String getColorCode() {
            return "\u001B[33m";
        }
    }
}
