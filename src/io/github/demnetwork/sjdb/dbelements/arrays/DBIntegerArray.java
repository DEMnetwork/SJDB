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

package io.github.demnetwork.sjdb.dbelements.arrays;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import io.github.demnetwork.sjdb.dbelements.DBInteger;
import io.github.demnetwork.sjdb.dbelements.property.*;

public class DBIntegerArray extends DBArray<DBInteger>
        implements FinalProperty, FinalStateLockProperty {
    public static final String DEFAULT_NAME = "Unnamed_DBIntegerArray";
    public static final DBInteger[] DEFAULT_DATA = new DBInteger[0];
    public static final boolean DEFAULT_FINAL_STATE = false;
    public static final boolean DEFAULT_FINAL_LOCK_STATE = false;
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";
    private DBInteger[] data;
    private String name;
    private boolean isFinal;
    private boolean isFinalStateLocked;

    public DBIntegerArray() {
        this(DEFAULT_NAME, DEFAULT_DATA, DEFAULT_FINAL_STATE, DEFAULT_FINAL_LOCK_STATE);
    }

    public DBIntegerArray(String name) {
        this(name, DEFAULT_DATA, DEFAULT_FINAL_STATE, DEFAULT_FINAL_LOCK_STATE);
    }

    public DBIntegerArray(DBInteger[] data) {
        this(DEFAULT_NAME, data, DEFAULT_FINAL_STATE, DEFAULT_FINAL_LOCK_STATE);
    }

    public DBIntegerArray(String name, DBInteger[] data) {
        this(name, data, DEFAULT_FINAL_STATE, DEFAULT_FINAL_LOCK_STATE);
    }

    public DBIntegerArray(String name, DBInteger[] data, boolean isFinal) {
        this(name, data, isFinal, DEFAULT_FINAL_LOCK_STATE);
    }

    public DBIntegerArray(String name, DBInteger[] data, boolean isFinal, boolean isFinalStateLocked) {
        if (name == null || name.contains("=") || name.contains(";") || name.contains("]") || name.contains("[")) {
            this.name = "null";
        } else {
            this.name = NameProperty.validateName(name);
        }
        if (data == null) {
            throw new NullPointerException("Cannot set inital data to \'null\'");
        }
        this.data = data;
        this.isFinal = isFinal;
        this.isFinalStateLocked = isFinalStateLocked;
    }

    @Override
    public DBInteger[] get() {
        return Arrays.copyOf(this.data, this.data.length);
    }

    @Override
    public void set(DBInteger[] data) {
        if (this.isFinal) {
            throw new IllegalStateException("Unable to change DBIntegerArray data, because it is final");
        }
        if (data == null) {
            throw new NullPointerException("Cannot set data to null");
        }
        this.data = data;
    }

    public void set(DBInteger data, int index) {
        if (this.isFinal) {
            throw new IllegalStateException("Unable to change DBIntegerArray data, because it is final");
        }
        if (index <= -1) {
            throw new ArrayIndexOutOfBoundsException("Unable to set the index because it is negative");
        }
        if (index >= this.data.length) {
            this.data = Arrays.copyOf(this.data, ((this.data.length + 1) * 2));
        }
        this.data[index] = data;
    }

    public DBInteger get(int index) {
        if (index <= -1 || index >= this.data.length) {
            throw new ArrayIndexOutOfBoundsException("Unable to access index " + index + ", because it is invalid");
        } else {
            return this.data[index];
        }
    }

    public void setFinal(boolean isFinal) {
        if (this.isFinalStateLocked) {
            throw new IllegalStateException(
                    "Cannot modify \'isFinal\' state of DBIntegerArray, because it is final.");
        } else {
            this.isFinal = isFinal;
        }
    }

    public void lockFinalState() {
        if (this.isFinalStateLocked) {
            throw new IllegalStateException("Cannot modify the \'isFinal\' state lock, because is it already locked");
        } else {
            this.isFinalStateLocked = true;
        }
    }

    public void setName(String Name) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBString name because it is final");
        } else {
            if (Name == null) {
                this.name = "null";
            } else if (Name.equals(name)) {
                return;
            } else if (Name.contains(";") || Name.contains("=") || Name.contains("]") || Name.contains("[")
                    || Name.contains("\'")) {
                return;
            } else {
                this.name = Name;
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public boolean isFinalStateLocked() {
        return this.isFinalStateLocked;
    }

    public int getMaxElementCount() {
        return this.data.length;
    }

    public void add(DBInteger data) {
        this.set(data, this.data.length + 1);
    }

    @Override
    public String toString() {
        String s = "[DBINTARR][PROPERTIES] name=\'" + this.name + "\'; isFinal=" + this.isFinal + "; fsl="
                + this.isFinalStateLocked + "; maxelementcount=" + this.data.length + "; [/PROPERTIES][DBINTARRDATA] ";
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i] == null) {

            } else {
                s = s + ", " + this.data[i].toString();
            }
        }
        s = s + "[/DBINTARRDATA]";
        return s;
    }

    @Override
    public Iterator<DBInteger> iterator() {
        return new Iterator<DBInteger>() {
            int p = 0;

            @Override
            public boolean hasNext() {
                return (p < DBIntegerArray.this.data.length);
            }

            @Override
            public DBInteger next() {
                if (hasNext()) {
                    return DBIntegerArray.this.data[p++];
                } else {
                    throw new NoSuchElementException("Reached last element. There are no more elements to iterate.");
                }
            }
        };
    }

    @Override
    public boolean supportsFSLP() {
        return FinalStateLockProperty.supportsFSLP(this);
    }
}
