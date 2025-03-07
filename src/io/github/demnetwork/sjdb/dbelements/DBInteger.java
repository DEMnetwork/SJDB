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

public final class DBInteger extends DBElement<Integer> implements NameProperty, FinalProperty, FinalStateLockProperty {
    private int data;
    private String name;
    private boolean isFinal;
    private boolean isFinalStateLocked;
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";

    public DBInteger() {
        this("Unnamed_DBInteger", 0, false, false);
    }

    public DBInteger(String name) {
        this(name, 0, false, false);
    }

    public DBInteger(int data) {
        this("Unnamed_DBInteger", data, false, false);
    }

    public DBInteger(boolean isFinalStateLocked) {
        this("Unnamed_DBInteger", 0, false, isFinalStateLocked);
    }

    public DBInteger(String name, int data) {
        this(name, data, false, false);
    }

    public DBInteger(String name, boolean isFinalStateLocked) {
        this(name, 0, false, isFinalStateLocked);
    }

    public DBInteger(int data, boolean isFinalStateLocked) {
        this("Unnamed_DBInteger", data, false, isFinalStateLocked);
    }

    public DBInteger(String name, int data, boolean isFinal) {
        this(name, data, isFinal, false);
    }

    public DBInteger(String name, boolean isFinalStateLocked, int data) {
        this(name, data, false, isFinalStateLocked);
    }

    public DBInteger(int data, boolean isFinal, boolean isFinalStateLocked) {
        this("Unnamed_DBInteger", data, isFinal, isFinalStateLocked);
    }

    public DBInteger(String name, int data, boolean isFinal, boolean isFinalStateLocked) {
        if (name == null || name.contains("=") || name.contains(";") || name.contains("]") || name.contains("[")) {
            this.name = "null";
        } else {
            this.name = NameProperty.validateName(name);
        }
        this.data = data;
        this.isFinal = isFinal;
        this.isFinalStateLocked = isFinalStateLocked;
    }

    public void setName(String Name) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBInteger name because it is final");
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

    public boolean isFinal() {
        return this.isFinal;
    }

    public void setFinal(boolean isFinal) {
        if (isFinalStateLocked) {
            throw new IllegalStateException("Cannot edit DBInteger \'isFinal\' state because it is locked");
        } else if (this.isFinal == isFinal) {
            throw new IllegalArgumentException("Cannot set \'isFinal\' to \'" + isFinal
                    + "\', because it is already set to \'" + this.isFinal + "\'!");
        }
        this.isFinal = isFinal;
    }

    @Override
    public void set(Integer data) {
        if (isFinal) {
            throw new IllegalStateException("Cannot modify data of DBInteger because it is final!");
        }
        this.data = data.intValue();
    }

    @Override
    public Integer get() {
        return Integer.valueOf(this.data);
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

    public void increment(int i) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBInteger data because it is final");
        }
        this.data = this.data + i;
    }

    public void decrement(int i) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBInteger data because it is final");
        }
        this.data = this.data - i;
    }

    public void multiplyBy(int i) {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBInteger data because it is final");
        }
        this.data = this.data * i;
    }

    public void divideBy(int i) throws IllegalStateException, ArithmeticException {
        if (isFinal) {
            throw new IllegalStateException("Cannot edit DBInteger data because it is final");
        } else if (i == 0) {
            throw new ArithmeticException("Unable to divide DBInteger.data by 0");
        } else {
            this.data = this.data / i;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "[INTEGER][PROPERTIES] name=\'%s\'; isFinal=%b; fsl=%b; [/PROPERTIES][INTDATA] %d [/INTDATA][/INTEGER]",
                name,
                isFinal, isFinalStateLocked, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DBInteger)) {
            return false;
        }
        DBInteger dbi = (DBInteger) obj;
        return this.name.equals(dbi.name) && this.isFinal == dbi.isFinal
                && this.isFinalStateLocked == dbi.isFinalStateLocked
                && this.data == dbi.data;
    }

    public boolean compare(int Comparison, DBInteger op2) {
        return DBInteger.compare(this, Comparison, op2);
    }

    public static boolean compare(DBInteger op1, int Comparison, DBInteger op2) {
        switch (Comparison) {
            case 0:
                return op1.data == op2.data;
            case 1:
                return op1.data != op2.data;
            case 2:
                return op1.data > op2.data;
            case 3:
                return op1.data >= op2.data;
            case 4:
                return op1.data < op2.data;
            case 5:
                return op1.data <= op2.data;
            default:
                throw new IllegalArgumentException("The argument \'Comparison\' is invalid");
        }
    }

    public class ComparisonConstants {
        public static final int EQUALS = 0;
        public static final int NOT_EQUAL = 1;
        public static final int GREATER_THAN = 2;
        public static final int EQUAL_OR_GREATER_THAN = 3;
        public static final int LESS_THAN = 4;
        public static final int EQUAL_OR_LESS_THAN = 5;
    }
}
