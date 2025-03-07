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

package io.github.demnetwork.sjdb.dbelements.arrays;

import java.util.*;
import java.util.NoSuchElementException;
import io.github.demnetwork.sjdb.dbelements.DBFloat;
import io.github.demnetwork.sjdb.dbelements.DBRootElement;

public class DBFloatArray extends DBArray<DBFloat> {
    private static final DBFloat[] DEFAULT_DATA = new DBFloat[0];
    private static final String DEFAULT_NAME = "Unamed DBFloatArray";
    private DBFloat[] data;
    private String name;

    public DBFloatArray() {
        this(DBFloatArray.DEFAULT_DATA, DBFloatArray.DEFAULT_NAME);
    }

    public DBFloatArray(DBFloat[] data, String name) {
        this.data = Objects.requireNonNull(data, "Cannot initalize data to null");
        this.name = io.github.demnetwork.sjdb.dbelements.property.NameProperty.validateName(name); // Name Validation.
    }

    public void set(DBFloat[] data) {
        this.data = Objects.requireNonNull(data, "Cannot set data to null");
    }

    public DBFloat[] get() {
        return Arrays.copyOf(this.data, this.data.length);
    }

    public void set(DBFloat data, int index) {
        if (index <= -1) {
            throw new ArrayIndexOutOfBoundsException("Unable to set the data to the index because it is negative");
        }
        if (index >= this.data.length) {
            this.data = Arrays.copyOf(this.data, ((this.data.length + 1) * 2));
        }
        this.data[index] = data;
    }

    public DBFloat get(int index) {
        if (index <= -1 || index >= this.data.length) {
            throw new ArrayIndexOutOfBoundsException("Unable to access index " + index + ", because it is invalid");
        } else {
            return this.data[index];
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = io.github.demnetwork.sjdb.dbelements.property.NameProperty.validateName(name); // name Validation
    }

    public int getMaxElementCount() {
        return this.data.length;
    }

    public Iterator<DBFloat> iterator() {
        return new Iterator<DBFloat>() {
            private int p;

            public boolean hasNext() {
                return (p < data.length);
            }

            public DBFloat next() {
                if (hasNext()) {
                    return data[p++];
                }
                throw new NoSuchElementException("No more elements");
            }
        };
    }

    public void add(DBFloat data) {
        this.set(data, this.data.length);
    }

    public String toString() {
        DBRootElement dbre = new DBRootElement();
        dbre.set(this.data);
        return "[DBFA][PROPERTIES] name=\'" + name + "\'; maxelementcount=" + this.data.length + "; [/PROPERTIES][DATA]"
                + dbre.toString() + "[/DATA][/DBFA]";
    }
}
