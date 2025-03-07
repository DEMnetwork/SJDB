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

import io.github.demnetwork.sjdb.dbelements.DBElement;
import io.github.demnetwork.sjdb.dbelements.DBRootElement;
import io.github.demnetwork.sjdb.dbelements.property.NameProperty;
import io.github.demnetwork.sjdb.exceptions.IllegalArrayLength;
import io.github.demnetwork.sjdb.internal.*;

public class SimpleTable extends Table {
    private String name;
    private DBElement<?>[][] data;
    public final int length;
    public final int colCount;

    public SimpleTable() {
        this("Unnamed SimpleTable", new DBElement<?>[2][2048]);
    }

    public SimpleTable(String name, DBElement<?>[][] data) {
        Table.checkNull(new String[] { "Name", "Data" }, name, data);
        this.data = data.clone();
        this.name = NameProperty.validateName(name);
        this.length = data[0].length;
        this.colCount = data.length;
        if (colCount <= 1) {
            throw new IllegalArgumentException("The column count cannot be less than 2.");
        }
        if (colCount < 5) {
            Warning w = new Warning(SJDBRuntime.getNextWarningID(), 5, "SimpleTable Performance",
                    Warning.PerformanceCategory.CATEGORY);
            w.setDescription(new Warning.WarningDescription[] {
                    w.createDescription(
                            "The performance of SimpleTable is lower than subclasses of Table with set amounts of columns,"),
                    w.createDescription(
                            "however for amounts of columns that does not have built-in support, you should use"),
                    w.createDescription(
                            "SimpleTable. For better performance in scenerios that you have built-in Table subclasses,"),
                    w.createDescription(
                            "it is highly recommended to use these subclasses of Table rather than SimpleTable, since they are"),
                    w.createDescription(
                            "more performant than SimpleTable."),
            });
            SJDBRuntime.throwWarning(w);
        }
    }

    public SimpleTable(int columnCount, int length) {
        this("Unnamed SimpleTable", new DBElement<?>[columnCount][length]);
    }

    @Override
    public int getMaxElementCount() {
        return this.length;
    }

    @Override
    public String toString() {
        String s = "[STABLE][PROPERTIES] name=\'" + this.name + "\'; maxelementcount=" + length + " [PROPERTIES]";
        for (int i = 0; i < this.colCount; i++) {
            DBRootElement dbre = new DBRootElement(this.length);
            dbre.set(this.data[i]);
            s = s + "[COL" + (i + 1) + "]" + dbre.toString() + "[/COL" + (i + 1) + "]";
        }
        s = s + "[/STABLE]";
        return s;
    }

    @Override
    public void setName(String s) {
        this.name = NameProperty.validateName(s);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void set(DBElement<?>[] data, int index) {
        Table.checkNull((Object[]) data);
        if (data.length != colCount) {
            throw new IllegalArrayLength(colCount, data.length);
        }
        for (int i = 0; i < data.length; i++) {
            this.data[i][index] = data[i];
        }
    }

    @Override
    public DBElement<?>[] get(int row) {
        DBElement<?>[] rowData = new DBElement<?>[colCount];
        for (int i = 0; i < colCount; i++) {
            rowData[i] = this.data[i][row];
        }
        return rowData;
    }

    @Override
    public DBElement<?> get(int row, int col) {
        return this.data[col][row];
    }

    @Override
    public void set(int col, int row, DBElement<?> data) {
        Table.checkNull(data);
        this.data[col][row] = data;
    }
}
