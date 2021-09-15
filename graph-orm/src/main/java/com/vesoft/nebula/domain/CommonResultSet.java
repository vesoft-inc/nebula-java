/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.domain;

import java.sql.SQLException;

/**
 * @author zhoupeng-jk
 */
public interface CommonResultSet {
    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>String</code> in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    String getString(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>boolean</code> in the Java programming language.
     *
     * <P>If the designated column has a datatype of CHAR or VARCHAR
     * and contains a "0" or has a datatype of BIT, TINYINT, SMALLINT, INTEGER or BIGINT
     * and contains  a 0, a value of <code>false</code> is returned.  If the designated column has a datatype
     * of CHAR or VARCHAR
     * and contains a "1" or has a datatype of BIT, TINYINT, SMALLINT, INTEGER or BIGINT
     * and contains  a 1, a value of <code>true</code> is returned.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>false</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    boolean getBoolean(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>short</code> in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>0</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    short getShort(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * an <code>int</code> in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>0</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    int getInt(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>long</code> in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>0</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    long getLong(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>float</code> in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>0</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    float getFloat(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>double</code> in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>0</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    double getDouble(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>java.sql.Date</code> object in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    java.sql.Date getDate(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>java.sql.Time</code> object in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value;
     * if the value is SQL <code>NULL</code>,
     * the value returned is <code>null</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    java.sql.Time getTime(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * a <code>java.sql.Timestamp</code> object in the Java programming language.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    java.sql.Timestamp getTimestamp(String columnLabel) throws SQLException;


    /**
     * <p>Gets the value of the designated column in the current row
     * of this <code>ResultSet</code> object as
     * an <code>Object</code> in the Java programming language.
     *
     * <p>This method will return the value of the given column as a
     * Java object.  The type of the Java object will be the default
     * Java object type corresponding to the column's SQL type,
     * following the mapping for built-in types specified in the JDBC
     * specification. If the value is an SQL <code>NULL</code>,
     * the driver returns a Java <code>null</code>.
     * <P>
     * This method may also be used to read database-specific
     * abstract data types.
     * <P>
     * In the JDBC 2.0 API, the behavior of the method
     * <code>getObject</code> is extended to materialize
     * data of SQL user-defined types.  When a column contains
     * a structured or distinct value, the behavior of this method is as
     * if it were a call to: <code>getObject(columnIndex,
     * this.getStatement().getConnection().getTypeMap())</code>.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
     * @return a <code>java.lang.Object</code> holding the column value
     * @exception SQLException if the columnLabel is not valid;
     * if a database access error occurs or this method is
     *            called on a closed result set
     */
    Object getObject(String columnLabel) throws SQLException;
}
