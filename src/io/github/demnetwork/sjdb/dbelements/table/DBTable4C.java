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

import io.github.demnetwork.sjdb.dbelements.*;
import io.github.demnetwork.sjdb.exceptions.IllegalArrayLength;

public class DBTable4C<Col_1 extends DBElement<?>, Col_2 extends DBElement<?>, Col_3 extends DBElement<?>, Col_4 extends DBElement<?>>
        extends DBTable2C<DBTable2C<Col_1, Col_2>, DBTable2C<Col_3, Col_4>> {
    /**
     * This {@link java.lang.reflect.Field Field} represents the maximum number of
     * elements
     * that each column in the database table can hold, analogous to the
     * {@code length} property of arrays in Java.
     *
     * <p>
     * The {@code length} field is a constant value that defines the limit for
     * the number of entries in each column. This is critical for ensuring that
     * the database structure adheres to defined constraints and optimizes storage
     * usage.
     * </p>
     */
    public final int length;

    @SuppressWarnings(value = { "unchecked" })
    public DBTable4C(Col_1[] Column1, Col_2[] Column2, Col_3[] Column3, Col_4[] Column4, String name) {
        super(name, new DBTable2C[1], new DBTable2C[1]);
        Table.checkNull(Column1, Column2, Column3, Column4);
        super.setCol1(new DBTable2C<Col_1, Col_2>(Column1, Column2), 0);
        super.setCol2(new DBTable2C<Col_3, Col_4>(Column3, Column4), 0);
        if (Column1.length != Column2.length || Column3.length != Column4.length || Column2.length != Column3.length
                || Column4.length != Column1.length) {
            throw new IllegalArgumentException("The lengths don't match!");
        }
        this.length = Column1.length;
    }

    /**
     * This constructor should be used for nesting
     * {@link io.github.demnetwork.sjdb.dbelements.table.Table Table}(s)
     * 
     * @param name    Name for the new {@link DBTable4C}
     * @param Column1 The first column value for the new {@link DBTable4C}
     * @param Column2 The second column value for the new {@link DBTable4C}
     * @param Column3 The third column value for the new {@link DBTable4C}
     * @param Column4 The fourth column value for the new {@link DBTable4C}
     */
    @SuppressWarnings(value = { "unchecked" })
    protected DBTable4C(String name, Col_1[] Column1, Col_2[] Column2, Col_3[] Column3, Col_4[] Column4) {
        super(name, new DBTable2C[1], new DBTable2C[1]);
        Table.checkNull(Column1, Column2, Column3, Column4);
        super.setCol1(new DBTable2C<Col_1, Col_2>(Column1, Column2), 0);
        super.setCol2(new DBTable2C<Col_3, Col_4>(Column3, Column4), 0);
        this.length = Column1.length;
    }

    @Override
    public DBTable2C<Col_1, Col_2> getCol1(int index) {
        throw new UnsupportedOperationException("This method is not supported use \'getColumn1(int)\'");
    }

    @Override
    public DBTable2C<Col_3, Col_4> getCol2(int index) {
        throw new UnsupportedOperationException("This method is not supported use \'getColumn2(int)\'");
    }

    @Override
    public DBElement<?>[] get(int index) {
        return new DBElement<?>[] { super.getCol1(0).getCol1(index), super.getCol1(0).getCol2(index),
                super.getCol2(0).getCol1(index), super.getCol2(0).getCol2(index) };
    }

    @Override
    public DBElement<?> get(int index, int col) {
        if (col == 1 || col == 2) {
            return super.getCol1(0).get(index, col);
        } else if (col == 3 || col == 4) {
            return super.getCol2(0).get(index, col - 2);
        } else {
            throw new IllegalArgumentException("Invalid Column");
        }
    }

    public Col_1 getColumn1(int index) {
        return super.getCol1(0).getCol1(index);
    }

    public Col_2 getColumn2(int index) {
        return super.getCol1(0).getCol2(index);
    }

    public Col_3 getColumn3(int index) {
        return super.getCol2(0).getCol1(index);
    }

    public Col_4 getColumn4(int index) {
        return super.getCol2(0).getCol2(index);
    }

    public void setColumn1(Col_1 data, int index) {
        super.getCol1(0).setCol1(data, index);
    }

    public void setColumn2(Col_2 data, int index) {
        super.getCol1(0).setCol2(data, index);
    }

    public void setColumn3(Col_3 data, int index) {
        super.getCol2(0).setCol1(data, index);
    }

    public void setColumn4(Col_4 data, int index) {
        super.getCol2(0).setCol2(data, index);
    }

    public void set(Col_1 Col1, Col_2 Col2, Col_3 Col3, Col_4 Col4, int index) {
        this.setColumn1(Col1, index);
        this.setColumn2(Col2, index);
        this.setColumn3(Col3, index);
        this.setColumn4(Col4, index);
        return;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(DBElement<?>[] data, int index) {
        if (data.length != 4) {
            throw new IllegalArrayLength(4, data.length);
        }
        try {
            this.set((Col_1) data[0], (Col_2) data[1], (Col_3) data[2], (Col_4) data[3], index);
        } catch (ClassCastException err) {
            throw new IllegalArgumentException("Invalid data types provided for columns.", err);
        }
    }

    @Override
    public int getMaxElementCount() {
        return this.length;
    }

    @Override
    public String toString() {
        String name = super.getName();
        Col_1[] col1 = super.getCol1(0).Col1;
        Col_2[] col2 = super.getCol1(0).Col2;
        Col_3[] col3 = super.getCol2(0).Col1;
        Col_4[] col4 = super.getCol2(0).Col2;
        String s = "[TABLE4C][PROPERTIES] name=" + name + "; maxelementcount=" + this.length + "; [/PROPERTIES][COL1]";
        DBRootElement dbre = new DBRootElement(col1.length);
        dbre.set(col1);
        s = s + dbre.toString();
        s = s + "[/COL1][COL2]";
        dbre.set(col2);
        s = s + dbre.toString();
        s = s + "[/COL2][COL3]";
        dbre.set(col3);
        s = s + dbre.toString();
        s = s + "[/COL3][COL4]";
        dbre.set(col4);
        s = s + dbre.toString();
        s = s + "[/COL4][/TABLE4C]";
        return s;
    }
}
