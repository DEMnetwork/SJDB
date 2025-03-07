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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.NoSuchElementException;
import io.github.demnetwork.sjdb.Database;
import io.github.demnetwork.sjdb.DatabaseManager;
import io.github.demnetwork.sjdb.dbelements.arrays.*;
import io.github.demnetwork.sjdb.dbelements.property.MaxElementCountProperty;
import io.github.demnetwork.sjdb.dbelements.property.NameProperty;
import io.github.demnetwork.sjdb.dbelements.support.SupportsChildren;
import io.github.demnetwork.sjdb.dbelements.table.DBTable2C;
import io.github.demnetwork.sjdb.dbelements.table.Table;
import io.github.demnetwork.sjdb.exceptions.IllegalDatabaseFormat;
import io.github.demnetwork.sjdb.injection.CodeInjectionSupport;
import io.github.demnetwork.sjdb.injection.InjectableCode;
import io.github.demnetwork.sjdb.injection.InjectionPermission;
import io.github.demnetwork.sjdb.injection.CodeInjectionSupport.InjectionLocation;

@SuppressWarnings("unused")
public final class DBRootElement extends DBElement<java.lang.Object[]>
        implements SupportsChildren, MaxElementCountProperty, CodeInjectionSupport, Cloneable {
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";
    private Object[] data;
    public final toString_Method_CodeInjection DEFAULT_toString_METHOD = new toString_Method_CodeInjection() {
        private Object[] data;

        public String getString(DBRootElement RootElement) {
            this.data = RootElement.get();
            String s = "";
            for (int i = 0; i < this.data.length; i++) {
                if (this.data[i] instanceof DBElement<?>) {
                    DBElement<?> ce = (DBElement<?>) this.data[i];
                    if (ce instanceof DBString) {
                        s = s + ce.toString();
                    }
                    if (ce instanceof DBInteger) {
                        s = s + ce.toString();
                    }
                    if (ce instanceof DBArray<?>) {
                        s = s + ce.toString();
                    }
                    if (ce instanceof DBTempElement) {
                        if (!(((DBTempElement) ce).duration <= 0)) {
                            s = s + ((DBTempElement) ce).toString();
                        }
                    }
                    if (ce instanceof Table) {
                        return ce.toString();
                    }
                }
            }
            return s;
        }
    };
    private toString_Method_CodeInjection current_ToString_method;

    public DBRootElement() {
        this.data = new Object[16384];
        this.current_ToString_method = this.DEFAULT_toString_METHOD;
    }

    public DBRootElement(int length) {
        this.data = new Object[length];
        this.current_ToString_method = this.DEFAULT_toString_METHOD;
    }

    /**
     * Instantieates a new Instance of
     * {@link io.github.demnetwork.sjdb.dbelements.DBRootElement DBRootElement}
     * based in a String.
     * 
     * @since SJDB v1.0.0
     * 
     * @see io.github.demnetwork.sjdb.DatabaseManager.DatabaseParsers
     * @see io.github.demnetwork.sjdb.DatabaseManager.DatabaseParser
     * @see io.github.demnetwork.sjdb.DatabaseManager
     * @see io.github.demnetwork.sjdb.dbelements.DBElement
     */
    public DBRootElement(String s) throws Exception {
        this(s, 16384);
    }

    /**
     * Instantiates a new instance of
     * {@link io.github.demnetwork.sjdb.dbelements.DBRootElement DBRootElement}
     * based in a {@link Java.lang.String String}, with an initial length
     * 
     * <p>
     * This constructor gets the {@link java.lang.Class Class} of
     * {@link io.github.demnetwork.sjdb.DatabaseManager DatabaseManager} and gets a
     * constant from the enum
     * {@link io.github.demnetwork.sjdb.DatabaseManager.DatabaseParsers
     * DatabaseParsers}
     * </p>
     * <p>
     * With that constant it invokes the {@link java.lang.reflect.Method Method}
     * {@link io.github.demnetwork.sjdb.DatabaseManager.DatabaseParsers#getParser()
     * DatabaseParsers.getParser()}, then is invoked the
     * method
     * {@link io.github.demnetwork.sjdb.DatabaseManager.DatabaseParser#parse(String, DBRootElement)
     * DatabaseParser.parse(String s, DBRootElement DBRoot)}
     * after that is checked if the returned
     * {@link io.github.demnetwork.sjdb.dbelements.DBRootElement DBRootElement}
     * refers to the same address in memory as that instance of
     * {@link io.github.demnetwork.sjdb.dbelements.DBRootElement DBRootElement}
     * if the check returns false then is thrown an {@link java.lang.Exception
     * Exception}.
     * </p>
     * 
     * @since SJDB v1.0.0
     * 
     * @throws java.lang.Exception If the String does not contain a valid
     *                             DBRootElement
     * @param s      String to parse
     * @param length Initial Length
     * 
     * @see io.github.demnetwork.sjdb.DatabaseManager.DatabaseParsers
     * @see io.github.demnetwork.sjdb.DatabaseManager.DatabaseParser
     * @see io.github.demnetwork.sjdb.DatabaseManager
     * @see io.github.demnetwork.sjdb.dbelements.DBElement
     * @see java.lang.reflect.Method
     * @see java.lang.Class
     * @see java.lang.reflect.InvocationTargetException
     * @see java.lang.reflect.AccessibleObject
     * @see java.lang.String
     * @see java.lang.Exception
     * @see java.lang.Object
     */
    public DBRootElement(String s, int length) throws Exception {
        this.current_ToString_method = this.DEFAULT_toString_METHOD;
        this.data = new Object[length];
        try {
            Class<io.github.demnetwork.sjdb.DatabaseManager> clazz = DatabaseManager.class;
            Class<?>[] innerClasses = clazz.getDeclaredClasses();
            Class<?> e = null;
            for (int i = 0; i < innerClasses.length; i++) {
                if (innerClasses[i].isEnum()) {
                    e = innerClasses[i];
                    break;
                }
            }

            if (e != null) {
                Object[] ec = e.getEnumConstants();
                Class<? extends Object> c = ec[(Database.SUPPORTED_DATABASE_FORMAT_VERSION - 1)].getClass();
                Method m = c.getMethod("getParser");
                m.setAccessible(true);
                Object o = m.invoke(ec[(Database.SUPPORTED_DATABASE_FORMAT_VERSION - 1)]);
                Method m2 = o.getClass().getMethod("parse", String.class, DBRootElement.class);
                m2.setAccessible(true);
                DBRootElement dbr = (DBRootElement) m2.invoke(o, s, this);
                if (dbr != this) {
                    throw new Exception("Unable to convert String to DBRootElement");
                }
            }
        } catch (Exception e) {
            throw new Exception("Unable to convert String to DBRootElement.", e);
        }
    }

    @Override
    public Object[] get() {
        return java.util.Arrays.<Object>copyOf(this.data, this.data.length);
    }

    @Override
    public void set(Object[] data) {
        if (data == null) {
            throw new NullPointerException("Unable to modify data: new data is null");
        }
        this.data = new Object[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public void set(int index, Object data) {
        if (index >= this.data.length || index < 0) {
            throw new ArrayIndexOutOfBoundsException("The index is invalid!");
        } else if (data == null || data instanceof DBRootElement || this == data) {
            return;
        } else {
            this.data[index] = data;
        }
    }

    public Object get(int index) {
        if (index >= this.data.length || index < 0) {
            throw new ArrayIndexOutOfBoundsException("The index is invalid!");
        } else if (this.data[index] == null) {
            throw new NullPointerException("The Object is null");
        } else {
            return this.data[index];
        }
    }

    public void reset(int index) {
        this.data[index] = null;
    }

    /**
     * This method allows the user to get a
     * {@link io.github.demnetwork.sjdb.dbelements.DBElement DBElement} with a name.
     * <p>
     * This checks if the {@link java.lang.Object} is an instance of
     * {@link io.github.demnetwork.sjdb.dbelements.property.NameProperty
     * NameProperty} and {@link io.github.demnetwork.sjdb.dbelements.DBElement
     * DBElement}. if it is it checks if the name is equals the Name argument
     * 
     * @throws NullPointerException   If the Name is null.
     * @throws NoSuchElementException If the Object is not found
     * 
     * @since SJDB v1.0.0
     */
    @Override
    public DBElement<?> getByName(String Name) throws NullPointerException, NoSuchElementException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        for (int i = 0; !(i >= this.data.length); i++) {
            if (data[i] instanceof NameProperty) {
                NameProperty np = (NameProperty) data[i];
                if (np.getName().equals(Name) && np instanceof DBElement<?>) {
                    return (DBElement<?>) data[i];
                }
            }
        }
        throw new NoSuchElementException("The element with name \'" + Name + "\' was not found");
    }

    @Override
    public boolean hasObjectWithName(String Name) throws NullPointerException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        for (int i = 0; !(i >= this.data.length); i++) {
            if (data[i] instanceof NameProperty) {
                NameProperty np = (NameProperty) data[i];
                if (np.getName().equals(Name) && np instanceof DBElement<?>) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public DBElement<?> getByName(String Name, int depth)
            throws NullPointerException, NoSuchElementException, IllegalArgumentException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        int cDepth = depth;
        if (cDepth <= 0) {
            throw new IllegalArgumentException("Depth cannot be less than or equal 0");
        }
        for (int i = 0; (!(i >= this.data.length)); i++) {
            if (this.data[i] instanceof DBElement<?>) {
                if (data[i] instanceof NameProperty) {
                    NameProperty np = (NameProperty) data[i];
                    if (np.getName().equals(Name)) {
                        return (DBElement<?>) data[i];
                    }
                }
                if (data[i] instanceof SupportsChildren) {
                    if ((cDepth - 1) >= 0) {
                        cDepth = cDepth - 1;
                        SupportsChildren sc = (SupportsChildren) data[i];
                        try {
                            return sc.getByName(Name, cDepth);
                        } catch (NoSuchElementException nsee) {
                            cDepth = cDepth + 1;
                        }
                    }
                }
            }
        }
        throw new NoSuchElementException("The element with name \'" + Name + "\' was not found");
    }

    /**
     * <p>
     * This method checks if the
     * {@link io.github.demnetwork.sjdb.dbelements.DBRootElement DBRootElement} has
     * an element with a name.
     * 
     * </p>
     * <p>
     * Here is an example of how to use <strong>this</strong> method:
     * </p>
     * <p>
     * 
     * <pre>
     * import io.github.demnetwork.sjdb.*;
     * import io.github.demnetwork.sjdb.dbelements.DBRootElement;
     * 
     * public class Main {
     *       public static void main(String[] args) {
     *            DatabaseManager DBM = new DatabaseManager(); // Instantiates a new instance of DatabaseMananger
     *            Database db = DBM.access("./MyDatabase.sdb","Arthur","123456-/SecurePassword_\LOL/_\-654321"); // Used to access a database
     *            DBRootElement db_root = db.getRoot(); // Gets the root element of the Database
     *            // Checks if there is an element is name 'Name' in the DataBase Root
     *            if (db_root.hasElementWithName("Name", 1)) {
     *                 System.out.println("There is an element with name \"Name\"");
     *            } else {
     *                 System.out.println("There is no element with name \"Name\"");
     *            }
     *       }
     * }
     * </pre>
     * </p>
     * 
     * 
     * 
     * @see io.github.demnetwork.sjdb.dbelements.property.NameProperty
     * @see io.github.demnetwork.sjdb.dbelements.support.SupportsChildren
     * 
     * @throws NullPointerException     If the Name is null
     * @throws IllegalArgumentException If the depth is less than or equal 0
     * 
     * @param Name  - Name used to search.
     * @param depth - Relative Depth from this element
     * 
     * @apiNote The depth <strong>0</strong> always represent the current element
     * 
     * @return Returns true when the element was found and return false when it is
     *         not found
     * 
     * @author Arhur Y. Arakaki(DEMNetwork)
     * 
     * @since SJDB 1.0.0
     */
    @Override
    public boolean hasObjectWithName(String Name, int depth) throws NullPointerException, IllegalArgumentException {
        if (Name == null) {
            throw new NullPointerException("\'Name\' cannot be null");
        }
        if (depth <= 0) {
            throw new IllegalArgumentException("Depth cannot be less than or equal 0");
        }
        int cDepth = depth;
        for (int i = 0; !(i >= this.data.length); i++) {
            if (data[i] instanceof DBElement<?>) {
                if (data[i] instanceof NameProperty) {
                    NameProperty np = (NameProperty) data[i];
                    if (np.getName().equals(Name)) {
                        return true;
                    }
                }
                if (data[i] instanceof SupportsChildren) {
                    if ((cDepth - 1) >= 1) {
                        SupportsChildren sc = (SupportsChildren) data[i];
                        if (sc.hasObjectWithName(Name, (cDepth - 1))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("removal")
    @Override
    public String toString() {
        if (current_ToString_method == null) {
            throw new IllegalStateException("Unable to run \'toString()\' method.",
                    new NullPointerException("Current \'toString()\' method is null"));
        }
        if (current_ToString_method != DEFAULT_toString_METHOD) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                try {
                    sm.checkPermission(new InjectionPermission("dbrootelement-inject"));
                } catch (SecurityException err) {
                    throw new SecurityException(
                            "Unable to execute \'toString()\' method because, code was injected to it while Code Injection is not allowed!",
                            err);
                }
            }
            return this.current_ToString_method.getString(this);
        } else {
            return this.DEFAULT_toString_METHOD.getString(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (obj instanceof DBRootElement) {
                DBRootElement dbre = (DBRootElement) obj;
                if (dbre.get().length == dbre.get().length) {
                    if (!Arrays.equals(this.data, dbre.get())) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int getMaxElementCount() {
        return this.data.length;
    }

    public interface toString_Method_CodeInjection {
        public String getString(DBRootElement RootElement);
    }

    public enum DBRootElementInjectionLocations implements InjectionLocation {
        toString(0);

        private int ID;

        private DBRootElementInjectionLocations(int ID) {
            this.ID = ID;
        }

        public int get() {
            return this.ID;
        }
    }

    public interface DBRootElementCodeInjection extends InjectableCode {

    }

    @SuppressWarnings("removal")
    private void Inject(DBRootElementInjectionLocations InjectionLocation, DBRootElementCodeInjection code)
            throws SecurityException {
        if (InjectionLocation == DBRootElementInjectionLocations.toString) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                try {
                    sm.checkPermission(new InjectionPermission("dbrootelement-injection"));
                } catch (SecurityException err) {
                    throw new SecurityException(
                            "Unable to Inject code to \'toString()\' method because Injection is not allowed here!",
                            err);
                }
            }
            if (code instanceof toString_Method_CodeInjection && code != null) {
                this.current_ToString_method = (toString_Method_CodeInjection) code;
            } else if (code == null) {
                throw new NullPointerException("The CodeInjection is null");
            } else {
                this.current_ToString_method = this.DEFAULT_toString_METHOD;
            }
        }
    }

    @Override
    public void Inject(InjectionLocation InjectionLocation, InjectableCode code) {
        if (InjectionLocation instanceof DBRootElementInjectionLocations && InjectionLocation != null
                && code instanceof DBRootElementCodeInjection && code != null) {
            this.Inject((DBRootElementInjectionLocations) InjectionLocation, (DBRootElementCodeInjection) code);
        } else {
            throw new IllegalArgumentException("The argument types do not match");
        }
    }

    @Override
    public DBRootElement clone() {
        try {
            return new DBRootElement(this.toString(), this.data.length);
        } catch (Exception err) {
            Throwable e = err.getCause();
            if ((e instanceof SecurityException)) {
                throw new SecurityException(
                        "The current SecuriyManager does not allow one of the operations that \'clone()\' uses!", e);
            } else {
                throw new RuntimeException("Unable to clone DBRootElement", e);
            }
        }
    }
}
