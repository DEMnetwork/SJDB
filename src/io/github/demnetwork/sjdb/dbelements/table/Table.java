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

package io.github.demnetwork.sjdb.dbelements.table;

import java.util.NoSuchElementException;
import io.github.demnetwork.sjdb.dbelements.*;
import io.github.demnetwork.sjdb.dbelements.property.MaxElementCountProperty;
import io.github.demnetwork.sjdb.dbelements.property.NameProperty;
import io.github.demnetwork.sjdb.exceptions.IllegalArrayLength;

/**
 * An Abstract Class that all tables are subclasses
 */
@SuppressWarnings("rawtypes")
public abstract class Table extends DBElement<DBElement[]> implements NameProperty, MaxElementCountProperty {

    protected Table() {
    }

    public abstract String toString();

    public abstract DBElement<?>[] get(int row);

    public abstract DBElement<?> get(int row, int col) throws NoSuchElementException;

    public abstract void set(DBElement<?>[] data, int index);

    public final static Table createTable(int columnCount, String name, int length) {
        if (columnCount < 2) {
            throw new IllegalArgumentException(
                    "The method \'Table.createTable(int, String, int)\' does not accept values below 2 in the first argument");
        }
        if (columnCount > 4) {
            throw new IllegalArgumentException(
                    "The method \'Table.createTable(int, String, int)\' does not accept values above 4 in the first argument");
        }
        switch (columnCount) {
            case 2:
                return new DBTable2C<DBElement<?>, DBElement<?>>(length, name);
            case 3:
                return new DBTable3C<DBElement<?>, DBElement<?>, DBElement<?>>(new DBElement<?>[length],
                        new DBElement<?>[length], new DBElement<?>[length], name);
            case 4:
                return new DBTable4C<DBElement<?>, DBElement<?>, DBElement<?>, DBElement<?>>(new DBElement<?>[length],
                        new DBElement<?>[length], new DBElement<?>[length], new DBElement[length], name);
            default:
                throw new AssertionError("The value is not between 2 and 4(inclusive)"); // This will never be executed
        }
    }

    public final static DBTable2C shrinkTableToDBTable2C(DBTable2C table, int start) {
        if ((table instanceof DBTable3C)) {
            DBTable3C t3c = (DBTable3C) table;
            if (start == 1) {
                return new DBTable2C<>(t3c.Col1, ((DBTable2C) t3c.Col2[0]).Col1);
            }
            if (start == 2) {
                return (DBTable2C) t3c.Col2[0];
            }
            throw new IllegalArgumentException(
                    "You cannot create a table with two columns because the range " + start + ".." + start + 1
                            + " is invalid");

        } else if (table instanceof DBTable4C) {
            DBTable3C t4c = (DBTable3C) table;
            if (start == 1) {
                return (DBTable2C) t4c.Col1[0];
            }
            if (start == 2) {
                return new DBTable2C<DBElement<?>, DBElement<?>>(((DBTable2C) t4c.Col1[0]).Col2,
                        ((DBTable2C) t4c.Col2[0]).Col1);
            }
            if (start == 3) {
                return (DBTable2C) t4c.Col2[0];
            }
            throw new IllegalArgumentException(
                    "You cannot create a table with two columns if the range " + start + ".." + start + 1
                            + " is invalid");
        }
        throw new IllegalArgumentException("Invalid type for the Table");
    }

    @SafeVarargs
    protected final static void checkNull(Object... args) {
        if (args == null) {
            throw new NullPointerException("The argument is null");
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new NullPointerException("The argument at index " + i + " is null");
            }
        }
    }

    @SafeVarargs
    protected final static void checkNull(String[] argNames, Object... args) {
        if (args == null || argNames == null) {
            throw new NullPointerException("One of the arguments is null");
        }
        if (argNames.length != args.length) {
            if (argNames.length > args.length) {
                throw new IllegalArrayLength(argNames.length, args.length);
            } else {
                throw new IllegalArrayLength(args.length, argNames.length);
            }
        }
        if (argNames.length == 0) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                if (argNames[i] == null) {
                    throw new NullPointerException("The argument [NO_NAME](Index: " + i + ") is null");
                }
                throw new NullPointerException("The argument " + argNames[i] + "(Index: " + i + ") is null");
            }
        }
    }

    @Override
    public void set(DBElement[] data) {
        throw new UnsupportedOperationException("This method is not supported by Table.");
    }

    @Override
    public DBElement[] get() {
        throw new UnsupportedOperationException("This method is not supported by Table.");
    }

    public void set(int col, int row, DBElement<?> data) {
        throw new UnsupportedOperationException("This method is not supported by Table.");
    }
}
