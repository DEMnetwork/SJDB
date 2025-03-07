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

public class DBFloat extends DBElement<Float> implements NameProperty, FinalProperty, FinalStateLockProperty {
    private String name;
    private float data;
    private boolean isFinal;
    private boolean isFinalStateLocked;
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";

    public DBFloat() {
        this("Unnamed_DBFloat", 0.0f, false, false);
    }

    public DBFloat(String name) {
        this(name, 0.0f, false, false);
    }

    public DBFloat(float data) {
        this("Unnamed_DBFloat", data, false, false);
    }

    public DBFloat(String name, float data) {
        this(name, data, false, false);
    }

    public DBFloat(String name, float data, boolean isFinal) {
        this(name, data, isFinal, false);
    }

    public DBFloat(boolean isFinalStateLocked, String name, float data) {
        this(name, data, false, isFinalStateLocked);
    }

    public DBFloat(String name, float data, boolean isFinal, boolean isFinalStateLocked) {
        if (name == null || name.contains("=") || name.contains(";") || name.contains("]") || name.contains("[")) {
            this.name = "null";
        } else {
            this.name = name;
        }
        this.data = data;
        this.isFinal = isFinal;
        this.isFinalStateLocked = isFinalStateLocked;
    }

    @Override
    public void set(Float data) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBFloat name because it is final");
        }
        this.data = data.floatValue();
    }

    @Override
    public Float get() {
        return Float.valueOf(this.data);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String Name) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBFloat name because it is final");
        }
        if (Name == null) {
            this.name = "null";
        } else if (Name.contains("=") || Name.contains(";") || Name.contains("]") || Name.contains("[")) {
            return;
        } else {
            this.name = Name;
        }
    }

    @Override
    public boolean isFinalStateLocked() {
        return this.isFinalStateLocked;
    }

    @Override
    public void lockFinalState() {
        if (!this.isFinalStateLocked) {
            this.isFinalStateLocked = true;
        } else {
            throw new IllegalStateException("The \'isFinal\' property is already locked");
        }
    }

    @Override
    public void setFinal(boolean isFinal) {
        if (isFinalStateLocked) {
            throw new IllegalStateException("Cannot edit DBFloat \'isFinal\' state because it is locked");
        } else if (this.isFinal == isFinal) {
            throw new IllegalArgumentException("Cannot set \'isFinal\' to \'" + isFinal
                    + "\', because it is already set to \'" + this.isFinal + "\'!");
        }
        this.isFinal = isFinal;
    }

    @Override
    public boolean isFinal() {
        return this.isFinal;
    }

    public void increment(float f) {
        this.set(this.data = this.data + f);
    }

    public void decrement(float f) {
        this.set(this.data = this.data - f);
    }

    public void multiplyBy(float f) {
        this.set(this.data = this.data * f);
    }

    public void divideBy(float f) {
        if (f == 0) {
            this.set(Float.NaN);
        } else {
            this.set(this.data = this.data / f);
        }
    }

    @Override
    public String toString() {
        return "[FLOAT][PROPERTIES] name=\'" + name + "\'; isFinal= " + isFinal + "; fsl=" + isFinalStateLocked
                + "[/PROPERTIES][FLOATDATA] " + data + " [FLOATDATA][/FLOAT]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DBFloat)) {
            return false;
        }
        DBFloat dbf = (DBFloat) obj;
        return (this.isFinal == dbf.isFinal
                && this.isFinalStateLocked == dbf.isFinalStateLocked && this.data == dbf.data
                && this.name.equals(dbf.name));
    }
}
