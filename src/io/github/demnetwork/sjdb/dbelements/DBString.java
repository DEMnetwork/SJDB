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

package io.github.demnetwork.sjdb.dbelements;

import io.github.demnetwork.sjdb.dbelements.property.FinalProperty;
import io.github.demnetwork.sjdb.dbelements.property.FinalStateLockProperty;
import io.github.demnetwork.sjdb.dbelements.property.NameProperty;

public final class DBString extends DBElement<java.lang.String>
        implements NameProperty, FinalProperty, FinalStateLockProperty {
    private String data;
    private String name;
    private boolean isFinal;
    private boolean isFinalStateLocked;

    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0-pre1";

    public DBString() {
        this("", "Unnamed_DBString", false, false);
    }

    public DBString(String name) {
        this("", name, false, false);
    }

    public DBString(String data, String name) {
        this(data, name, false, false);
    }

    public DBString(String data, String name, boolean isFinal) {
        this(data, name, isFinal, false);
    }

    public DBString(String data, String name, boolean isFinal, boolean LockFinalState) {
        if (name == null || name.contains("=") || name.contains(";") || name.contains("]") || name.contains("[")
                || name.contains("\'")) {
            this.name = "null";
        } else {
            this.name = name;
        }
        this.data = data;
        this.isFinal = isFinal;
        this.isFinalStateLocked = LockFinalState;
    }

    public void setName(String Name) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBString name because it is final");
        }
        if (Name == null) {
            this.name = "null";
        } else if (Name.equals(name)) {
            return;
        } else if (Name.contains(";") || Name.contains("=") || Name.contains("]") || Name.contains("[")) {
            return;
        } else {
            this.name = NameProperty.validateName(Name);
        }
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String get() {
        return data;
    }

    @Override
    public void set(String data) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBString data because it is final");
        }
        this.data = data;
    }

    @Override
    public String toString() {
        return "[STRING][PROPERTIES] name=\'" + name + "\'; isFinal=" + isFinal + "; fsl=" + isFinalStateLocked
                + "; [/PROPERTIES][STRDATA]" + data + "[/STRDATA][/STRING]";
    }

    public void setFinal(boolean isFinal) {
        if (isFinalStateLocked) {
            throw new IllegalStateException("Cannot edit DBString \'isFinal\' state because it is locked");
        } else if (this.isFinal == isFinal) {
            throw new IllegalArgumentException("Cannot set \'isFinal\' to \'" + isFinal
                    + "\', because it is already set to \'" + this.isFinal + "\'!");
        }
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public boolean isFinalStateLocked() {
        return this.isFinalStateLocked;
    }

    public void lockFinalState() {
        if (!this.isFinalStateLocked) {
            this.isFinalStateLocked = true;
        } else {
            throw new IllegalStateException("The \'isFinal\' property is already locked");
        }
    }

    public String castToString() {
        return new String(this.data);
    }

    /**
     * This method allows for a removal of the
     * {@link io.github.demnetwork.sjdb.dbelements.property.FinalStateLockProperty
     * FinalStateLock} Property value.
     * 
     * @apiNote This method can cause overhead due to the creation of new
     *          {@link io.github.demnetwork.sjdb.dbelements.DBString DBString}
     * 
     * @return A unlocked DBString
     */
    public DBString unlockFinalState() {
        return new DBString(this.data, this.name, this.isFinal, false);
    }
}
