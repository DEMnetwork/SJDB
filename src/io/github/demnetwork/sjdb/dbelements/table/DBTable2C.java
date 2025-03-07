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

package io.github.demnetwork.sjdb.dbelements.table;

import java.util.NoSuchElementException;
import io.github.demnetwork.sjdb.dbelements.DBElement;
import io.github.demnetwork.sjdb.dbelements.DBRootElement;
import io.github.demnetwork.sjdb.dbelements.property.NameProperty;
import io.github.demnetwork.sjdb.exceptions.IllegalArrayLength;

/**
 * Used to make a new Table with 2 Columns
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DBTable2C<Col_1 extends DBElement<?>, Col_2 extends DBElement<?>> extends Table {
    private String name;
    protected Col_1[] Col1;
    protected Col_2[] Col2;
    public final int length;
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";

    public DBTable2C(String Name) {
        this((Col_1[]) new DBElement<?>[2048], (Col_2[]) new DBElement<?>[2048], Name);
    }

    public DBTable2C(Col_1[] Column1, Col_2[] Column2) {
        this(Column1, Column2, "Unnamed_DBTable(2 Col)");
    }

    public DBTable2C(Col_1[] Column1, Col_2[] Column2, String Name) {
        Table.checkNull(Column1, Column2);
        if (Column1.length != Column2.length) {
            if (Column1.length > Column2.length) {
                throw new IllegalArrayLength(Column1.length, Column2.length);
            } else {
                throw new IllegalArrayLength(Column2.length, Column1.length);
            }
        }
        this.Col1 = Column1;
        this.Col2 = Column2;
        if (Name == null) {
            this.name = "null";
        } else {
            this.name = Name;
        }
        this.length = Col1.length;

    }

    protected DBTable2C(String Name, Col_1[] Column1, Col_2[] Column2) {
        Table.checkNull(Column1, Column2);
        this.Col1 = Column1;
        this.Col2 = Column2;
        if (Name == null) {
            this.name = "null";
        } else {
            this.name = Name;
        }
        this.length = Col1.length;
    }

    public DBTable2C() {
        this("Unnamed_DBTable(2 Col)");
    }

    public DBTable2C(int length) {
        this(length, "Unnamed_DBTable2C");
    }

    public DBTable2C(int length, String Name) {
        this((Col_1[]) new DBElement<?>[length], (Col_2[]) new DBElement<?>[length], Name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = NameProperty.validateName(name);
    }

    public void set(Col_1 Column1, Col_2 Column2, int index) {
        Col1[index] = Column1;
        Col2[index] = Column2;
    }

    public DBElement<?>[] get(int index) {
        return new DBElement<?>[] { Col1[index], Col2[index] };
    }

    public DBElement<?> get(int index, int col) throws NoSuchElementException {
        if (col == 1) {
            return Col1[index];
        } else if (col == 2) {
            return Col2[index];
        } else {
            throw new NoSuchElementException("Could not find column " + col + ".");
        }
    }

    public void set(DBElement<?>[] data, int index) {
        if (data.length != 2) {
            throw new IllegalArrayLength(2, data.length);
        }
        try {
            this.Col1[index] = (Col_1) data[0];
            this.Col2[index] = (Col_2) data[1];
        } catch (ClassCastException err) {
            throw new IllegalArgumentException("Invalid data types provided for columns.", err);
        }
    }

    public void setCol1(Col_1 data, int index) {
        if (data instanceof DBRootElement) {
            throw new IllegalArgumentException("The data cannot be an DBRootElement.");
        }
        Col1[index] = data;
    }

    public void setCol2(Col_2 data, int index) {
        if (data instanceof DBRootElement) {
            throw new IllegalArgumentException("The data cannot be an DBRootElement.");
        }
        Col2[index] = data;
    }

    public int getMaxElementCount() {
        return this.length;
    }

    @Override
    public String toString() {
        String s = "[TABLE2C][PROPERTIES] name=\'" + name + "\''; maxelementcount=" + this.Col1.length
                + ";[/PROPERTIES][COL1]";
        DBRootElement dbre = new DBRootElement(this.Col1.length);
        dbre.set(Col1);
        s = s + dbre.toString();
        s = s + "[/COL1][COL2]";
        dbre.set(Col2);
        s = s + dbre.toString();
        s = s + "[/COL2][/TABLE2C]";
        return s;
    }

    public Col_1 getCol1(int index) {
        return Col1[index];
    }

    public Col_2 getCol2(int index) {
        return Col2[index];
    }

    @Override
    public DBElement[] get() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method \'get()\' is not supported here");
    }

    @Override
    public void set(DBElement[] data) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The method \'set(DBElement[])\' is not supported here");
    }
}
