package java.sql;

public interface Statement extends Wrapper {

  /**
   * Executes the given SQL statement, which returns a single
   * <code>ResultSet</code> object.
   *
   * @param sql an SQL statement to be sent to the database, typically a
   *            static SQL <code>SELECT</code> statement
   * @return a <code>ResultSet</code> object that contains the data produced
   *         by the given query; never <code>null</code>
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the given
   *                      SQL statement produces anything other than a single
   *                      <code>ResultSet</code> object
   */
  ResultSet executeQuery(String sql) throws SQLException;

  /**
   * Executes the given SQL statement, which may be an <code>INSERT</code>,
   * <code>UPDATE</code>, or <code>DELETE</code> statement or an
   * SQL statement that returns nothing, such as an SQL DDL statement.
   *
   * @param sql an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
   *            <code>DELETE</code>; or an SQL statement that returns nothing,
   *            such as a DDL statement.
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
   *         or (2) 0 for SQL statements that return nothing
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the given
   *                      SQL statement produces a <code>ResultSet</code> object
   */
  int executeUpdate(String sql) throws SQLException;

  /**
   * Releases this <code>Statement</code> object's database
   * and JDBC resources immediately instead of waiting for
   * this to happen when it is automatically closed.
   * It is generally good practice to release resources as soon as
   * you are finished with them to avoid tying up database
   * resources.
   * <p/>
   * Calling the method <code>close</code> on a <code>Statement</code>
   * object that is already closed has no effect.
   * <p/>
   * <B>Note:</B>When a <code>Statement</code> object is
   * closed, its current <code>ResultSet</code> object, if one exists, is
   * also closed.
   *
   * @throws SQLException if a database access error occurs
   */
  void close() throws SQLException;

  //----------------------------------------------------------------------

  /**
   * Retrieves the maximum number of bytes that can be
   * returned for character and binary column values in a <code>ResultSet</code>
   * object produced by this <code>Statement</code> object.
   * This limit applies only to  <code>BINARY</code>, <code>VARBINARY</code>,
   * <code>LONGVARBINARY</code>, <code>CHAR</code>, <code>VARCHAR</code>,
   * <code>NCHAR</code>, <code>NVARCHAR</code>, <code>LONGNVARCHAR</code>
   * and <code>LONGVARCHAR</code> columns.  If the limit is exceeded, the
   * excess data is silently discarded.
   *
   * @return the current column size limit for columns storing character and
   *         binary values; zero means there is no limit
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #setMaxFieldSize
   */
  int getMaxFieldSize() throws SQLException;

  /**
   * Sets the limit for the maximum number of bytes that can be returned for
   * character and binary column values in a <code>ResultSet</code>
   * object produced by this <code>Statement</code> object.
   * <p/>
   * This limit applies
   * only to <code>BINARY</code>, <code>VARBINARY</code>,
   * <code>LONGVARBINARY</code>, <code>CHAR</code>, <code>VARCHAR</code>,
   * <code>NCHAR</code>, <code>NVARCHAR</code>, <code>LONGNVARCHAR</code> and
   * <code>LONGVARCHAR</code> fields.  If the limit is exceeded, the excess data
   * is silently discarded. For maximum portability, use values
   * greater than 256.
   *
   * @param max the new column size limit in bytes; zero means there is no limit
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code>
   *                      or the condition max >= 0 is not satisfied
   * @see #getMaxFieldSize
   */
  void setMaxFieldSize(int max) throws SQLException;

  /**
   * Retrieves the maximum number of rows that a
   * <code>ResultSet</code> object produced by this
   * <code>Statement</code> object can contain.  If this limit is exceeded,
   * the excess rows are silently dropped.
   *
   * @return the current maximum number of rows for a <code>ResultSet</code>
   *         object produced by this <code>Statement</code> object;
   *         zero means there is no limit
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #setMaxRows
   */
  int getMaxRows() throws SQLException;

  /**
   * Sets the limit for the maximum number of rows that any
   * <code>ResultSet</code> object  generated by this <code>Statement</code>
   * object can contain to the given number.
   * If the limit is exceeded, the excess
   * rows are silently dropped.
   *
   * @param max the new max rows limit; zero means there is no limit
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code>
   *                      or the condition max >= 0 is not satisfied
   * @see #getMaxRows
   */
  void setMaxRows(int max) throws SQLException;

  /**
   * Sets escape processing on or off.
   * If escape scanning is on (the default), the driver will do
   * escape substitution before sending the SQL statement to the database.
   * <p/>
   * Note: Since prepared statements have usually been parsed prior
   * to making this call, disabling escape processing for
   * <code>PreparedStatements</code> objects will have no effect.
   *
   * @param enable <code>true</code> to enable escape processing;
   *               <code>false</code> to disable it
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   */
  void setEscapeProcessing(boolean enable) throws SQLException;

  /**
   * Retrieves the number of seconds the driver will
   * wait for a <code>Statement</code> object to execute.
   * If the limit is exceeded, a
   * <code>SQLException</code> is thrown.
   *
   * @return the current query timeout limit in seconds; zero means there is
   *         no limit
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #setQueryTimeout
   */
  int getQueryTimeout() throws SQLException;

  /**
   * Sets the number of seconds the driver will wait for a
   * <code>Statement</code> object to execute to the given number of seconds.
   * If the limit is exceeded, an <code>SQLException</code> is thrown. A JDBC
   * driver must apply this limit to the <code>execute</code>,
   * <code>executeQuery</code> and <code>executeUpdate</code> methods. JDBC driver
   * implementations may also apply this limit to <code>ResultSet</code> methods
   * (consult your driver vendor documentation for details).
   *
   * @param seconds the new query timeout limit in seconds; zero means
   *                there is no limit
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code>
   *                      or the condition seconds >= 0 is not satisfied
   * @see #getQueryTimeout
   */
  void setQueryTimeout(int seconds) throws SQLException;

  /**
   * Cancels this <code>Statement</code> object if both the DBMS and
   * driver support aborting an SQL statement.
   * This method can be used by one thread to cancel a statement that
   * is being executed by another thread.
   *
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support
   *                      this method
   */
  void cancel() throws SQLException;

  /**
   * Retrieves the first warning reported by calls on this <code>Statement</code> object.
   * Subsequent <code>Statement</code> object warnings will be chained to this
   * <code>SQLWarning</code> object.
   * <p/>
   * <p>The warning chain is automatically cleared each time
   * a statement is (re)executed. This method may not be called on a closed
   * <code>Statement</code> object; doing so will cause an <code>SQLException</code>
   * to be thrown.
   * <p/>
   * <P><B>Note:</B> If you are processing a <code>ResultSet</code> object, any
   * warnings associated with reads on that <code>ResultSet</code> object
   * will be chained on it rather than on the <code>Statement</code>
   * object that produced it.
   *
   * @return the first <code>SQLWarning</code> object or <code>null</code>
   *         if there are no warnings
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   */
  SQLWarning getWarnings() throws SQLException;

  /**
   * Clears all the warnings reported on this <code>Statement</code>
   * object. After a call to this method,
   * the method <code>getWarnings</code> will return
   * <code>null</code> until a new warning is reported for this
   * <code>Statement</code> object.
   *
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   */
  void clearWarnings() throws SQLException;

  /**
   * Sets the SQL cursor name to the given <code>String</code>, which
   * will be used by subsequent <code>Statement</code> object
   * <code>execute</code> methods. This name can then be
   * used in SQL positioned update or delete statements to identify the
   * current row in the <code>ResultSet</code> object generated by this
   * statement.  If the database does not support positioned update/delete,
   * this method is a noop.  To insure that a cursor has the proper isolation
   * level to support updates, the cursor's <code>SELECT</code> statement
   * should have the form <code>SELECT FOR UPDATE</code>.  If
   * <code>FOR UPDATE</code> is not present, positioned updates may fail.
   * <p/>
   * <P><B>Note:</B> By definition, the execution of positioned updates and
   * deletes must be done by a different <code>Statement</code> object than
   * the one that generated the <code>ResultSet</code> object being used for
   * positioning. Also, cursor names must be unique within a connection.
   *
   * @param name the new cursor name, which must be unique within
   *             a connection
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support this method
   */
  void setCursorName(String name) throws SQLException;

  //----------------------- Multiple Results --------------------------

  /**
   * Executes the given SQL statement, which may return multiple results.
   * In some (uncommon) situations, a single SQL statement may return
   * multiple result sets and/or update counts.  Normally you can ignore
   * this unless you are (1) executing a stored procedure that you know may
   * return multiple results or (2) you are dynamically executing an
   * unknown SQL string.
   * <p/>
   * The <code>execute</code> method executes an SQL statement and indicates the
   * form of the first result.  You must then use the methods
   * <code>getResultSet</code> or <code>getUpdateCount</code>
   * to retrieve the result, and <code>getMoreResults</code> to
   * move to any subsequent result(s).
   *
   * @param sql any SQL statement
   * @return <code>true</code> if the first result is a <code>ResultSet</code>
   *         object; <code>false</code> if it is an update count or there are
   *         no results
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #getResultSet
   * @see #getUpdateCount
   * @see #getMoreResults
   */
  boolean execute(String sql) throws SQLException;

  /**
   * Retrieves the current result as a <code>ResultSet</code> object.
   * This method should be called only once per result.
   *
   * @return the current result as a <code>ResultSet</code> object or
   *         <code>null</code> if the result is an update count or there are no more results
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #execute
   */
  ResultSet getResultSet() throws SQLException;

  /**
   * Retrieves the current result as an update count;
   * if the result is a <code>ResultSet</code> object or there are no more results, -1
   * is returned. This method should be called only once per result.
   *
   * @return the current result as an update count; -1 if the current result is a
   *         <code>ResultSet</code> object or there are no more results
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #execute
   */
  int getUpdateCount() throws SQLException;

  /**
   * Moves to this <code>Statement</code> object's next result, returns
   * <code>true</code> if it is a <code>ResultSet</code> object, and
   * implicitly closes any current <code>ResultSet</code>
   * object(s) obtained with the method <code>getResultSet</code>.
   * <p/>
   * <P>There are no more results when the following is true:
   * <PRE>
   * // stmt is a Statement object
   * ((stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1))
   * </PRE>
   *
   * @return <code>true</code> if the next result is a <code>ResultSet</code>
   *         object; <code>false</code> if it is an update count or there are
   *         no more results
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #execute
   */
  boolean getMoreResults() throws SQLException;


  //--------------------------JDBC 2.0-----------------------------


  /**
   * Gives the driver a hint as to the direction in which
   * rows will be processed in <code>ResultSet</code>
   * objects created using this <code>Statement</code> object.  The
   * default value is <code>ResultSet.FETCH_FORWARD</code>.
   * <p/>
   * Note that this method sets the default fetch direction for
   * result sets generated by this <code>Statement</code> object.
   * Each result set has its own methods for getting and setting
   * its own fetch direction.
   *
   * @param direction the initial direction for processing rows
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code>
   *                      or the given direction
   *                      is not one of <code>ResultSet.FETCH_FORWARD</code>,
   *                      <code>ResultSet.FETCH_REVERSE</code>, or <code>ResultSet.FETCH_UNKNOWN</code>
   * @see #getFetchDirection
   * @since 1.2
   */
  void setFetchDirection(int direction) throws SQLException;

  /**
   * Retrieves the direction for fetching rows from
   * database tables that is the default for result sets
   * generated from this <code>Statement</code> object.
   * If this <code>Statement</code> object has not set
   * a fetch direction by calling the method <code>setFetchDirection</code>,
   * the return value is implementation-specific.
   *
   * @return the default fetch direction for result sets generated
   *         from this <code>Statement</code> object
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #setFetchDirection
   * @since 1.2
   */
  int getFetchDirection() throws SQLException;

  /**
   * Gives the JDBC driver a hint as to the number of rows that should
   * be fetched from the database when more rows are needed for
   * <code>ResultSet</code> objects genrated by this <code>Statement</code>.
   * If the value specified is zero, then the hint is ignored.
   * The default value is zero.
   *
   * @param rows the number of rows to fetch
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the
   *                      condition  <code>rows >= 0</code> is not satisfied.
   * @see #getFetchSize
   * @since 1.2
   */
  void setFetchSize(int rows) throws SQLException;

  /**
   * Retrieves the number of result set rows that is the default
   * fetch size for <code>ResultSet</code> objects
   * generated from this <code>Statement</code> object.
   * If this <code>Statement</code> object has not set
   * a fetch size by calling the method <code>setFetchSize</code>,
   * the return value is implementation-specific.
   *
   * @return the default fetch size for result sets generated
   *         from this <code>Statement</code> object
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @see #setFetchSize
   * @since 1.2
   */
  int getFetchSize() throws SQLException;

  /**
   * Retrieves the result set concurrency for <code>ResultSet</code> objects
   * generated by this <code>Statement</code> object.
   *
   * @return either <code>ResultSet.CONCUR_READ_ONLY</code> or
   *         <code>ResultSet.CONCUR_UPDATABLE</code>
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @since 1.2
   */
  int getResultSetConcurrency() throws SQLException;

  /**
   * Retrieves the result set type for <code>ResultSet</code> objects
   * generated by this <code>Statement</code> object.
   *
   * @return one of <code>ResultSet.TYPE_FORWARD_ONLY</code>,
   *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
   *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @since 1.2
   */
  int getResultSetType() throws SQLException;

  /**
   * Adds the given SQL command to the current list of commmands for this
   * <code>Statement</code> object. The commands in this list can be
   * executed as a batch by calling the method <code>executeBatch</code>.
   * <p/>
   *
   * @param sql typically this is a SQL <code>INSERT</code> or
   *            <code>UPDATE</code> statement
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the
   *                      driver does not support batch updates
   * @see #executeBatch
   * @see DatabaseMetaData#supportsBatchUpdates
   * @since 1.2
   */
  void addBatch(String sql) throws SQLException;

  /**
   * Empties this <code>Statement</code> object's current list of
   * SQL commands.
   * <p/>
   *
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the
   *                      driver does not support batch updates
   * @see #addBatch
   * @see DatabaseMetaData#supportsBatchUpdates
   * @since 1.2
   */
  void clearBatch() throws SQLException;

  /**
   * Submits a batch of commands to the database for execution and
   * if all commands execute successfully, returns an array of update counts.
   * The <code>int</code> elements of the array that is returned are ordered
   * to correspond to the commands in the batch, which are ordered
   * according to the order in which they were added to the batch.
   * The elements in the array returned by the method <code>executeBatch</code>
   * may be one of the following:
   * <OL>
   * <LI>A number greater than or equal to zero -- indicates that the
   * command was processed successfully and is an update count giving the
   * number of rows in the database that were affected by the command's
   * execution
   * <LI>A value of <code>SUCCESS_NO_INFO</code> -- indicates that the command was
   * processed successfully but that the number of rows affected is
   * unknown
   * <p/>
   * If one of the commands in a batch update fails to execute properly,
   * this method throws a <code>BatchUpdateException</code>, and a JDBC
   * driver may or may not continue to process the remaining commands in
   * the batch.  However, the driver's behavior must be consistent with a
   * particular DBMS, either always continuing to process commands or never
   * continuing to process commands.  If the driver continues processing
   * after a failure, the array returned by the method
   * <code>BatchUpdateException.getUpdateCounts</code>
   * will contain as many elements as there are commands in the batch, and
   * at least one of the elements will be the following:
   * <p/>
   * <LI>A value of <code>EXECUTE_FAILED</code> -- indicates that the command failed
   * to execute successfully and occurs only if a driver continues to
   * process commands after a command fails
   * </OL>
   * <p/>
   * The possible implementations and return values have been modified in
   * the Java 2 SDK, Standard Edition, version 1.3 to
   * accommodate the option of continuing to proccess commands in a batch
   * update after a <code>BatchUpdateException</code> obejct has been thrown.
   *
   * @return an array of update counts containing one element for each
   *         command in the batch.  The elements of the array are ordered according
   *         to the order in which commands were added to the batch.
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the
   *                      driver does not support batch statements. Throws {@link BatchUpdateException}
   *                      (a subclass of <code>SQLException</code>) if one of the commands sent to the
   *                      database fails to execute properly or attempts to return a result set.
   * @see #addBatch
   * @see DatabaseMetaData#supportsBatchUpdates
   * @since 1.3
   */
  int[] executeBatch() throws SQLException;

  /**
   * Retrieves the <code>Connection</code> object
   * that produced this <code>Statement</code> object.
   *
   * @return the connection that produced this statement
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @since 1.2
   */
  Connection getConnection() throws SQLException;

  //--------------------------JDBC 3.0-----------------------------

  /**
   * The constant indicating that the current <code>ResultSet</code> object
   * should be closed when calling <code>getMoreResults</code>.
   *
   * @since 1.4
   */
  int CLOSE_CURRENT_RESULT = 1;

  /**
   * The constant indicating that the current <code>ResultSet</code> object
   * should not be closed when calling <code>getMoreResults</code>.
   *
   * @since 1.4
   */
  int KEEP_CURRENT_RESULT = 2;

  /**
   * The constant indicating that all <code>ResultSet</code> objects that
   * have previously been kept open should be closed when calling
   * <code>getMoreResults</code>.
   *
   * @since 1.4
   */
  int CLOSE_ALL_RESULTS = 3;

  /**
   * The constant indicating that a batch statement executed successfully
   * but that no count of the number of rows it affected is available.
   *
   * @since 1.4
   */
  int SUCCESS_NO_INFO = -2;

  /**
   * The constant indicating that an error occured while executing a
   * batch statement.
   *
   * @since 1.4
   */
  int EXECUTE_FAILED = -3;

  /**
   * The constant indicating that generated keys should be made
   * available for retrieval.
   *
   * @since 1.4
   */
  int RETURN_GENERATED_KEYS = 1;

  /**
   * The constant indicating that generated keys should not be made
   * available for retrieval.
   *
   * @since 1.4
   */
  int NO_GENERATED_KEYS = 2;

  /**
   * Moves to this <code>Statement</code> object's next result, deals with
   * any current <code>ResultSet</code> object(s) according  to the instructions
   * specified by the given flag, and returns
   * <code>true</code> if the next result is a <code>ResultSet</code> object.
   * <p/>
   * <P>There are no more results when the following is true:
   * <PRE>
   * // stmt is a Statement object
   * ((stmt.getMoreResults(current) == false) && (stmt.getUpdateCount() == -1))
   * </PRE>
   *
   * @param current one of the following <code>Statement</code>
   *                constants indicating what should happen to current
   *                <code>ResultSet</code> objects obtained using the method
   *                <code>getResultSet</code>:
   *                <code>Statement.CLOSE_CURRENT_RESULT</code>,
   *                <code>Statement.KEEP_CURRENT_RESULT</code>, or
   *                <code>Statement.CLOSE_ALL_RESULTS</code>
   * @return <code>true</code> if the next result is a <code>ResultSet</code>
   *         object; <code>false</code> if it is an update count or there are no
   *         more results
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the argument
   *                      supplied is not one of the following:
   *                      <code>Statement.CLOSE_CURRENT_RESULT</code>,
   *                      <code>Statement.KEEP_CURRENT_RESULT</code> or
   *                      <code>Statement.CLOSE_ALL_RESULTS</code>
   * @throws SQLFeatureNotSupportedException
   *                      if
   *                      <code>DatabaseMetaData.supportsMultipleOpenResults</code> returns
   *                      <code>false</code> and either
   *                      <code>Statement.KEEP_CURRENT_RESULT</code> or
   *                      <code>Statement.CLOSE_ALL_RESULTS</code> are supplied as
   *                      the argument.
   * @see #execute
   * @since 1.4
   */
  boolean getMoreResults(int current) throws SQLException;

  /**
   * Retrieves any auto-generated keys created as a result of executing this
   * <code>Statement</code> object. If this <code>Statement</code> object did
   * not generate any keys, an empty <code>ResultSet</code>
   * object is returned.
   * <p/>
   * <p><B>Note:</B>If the columns which represent the auto-generated keys were not specified,
   * the JDBC driver implementation will determine the columns which best represent the auto-generated keys.
   *
   * @return a <code>ResultSet</code> object containing the auto-generated key(s)
   *         generated by the execution of this <code>Statement</code> object
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support this method
   * @since 1.4
   */
  ResultSet getGeneratedKeys() throws SQLException;

  /**
   * Executes the given SQL statement and signals the driver with the
   * given flag about whether the
   * auto-generated keys produced by this <code>Statement</code> object
   * should be made available for retrieval.  The driver will ignore the
   * flag if the SQL statement
   * is not an <code>INSERT</code> statement, or an SQL statement able to return
   * auto-generated keys (the list of such statements is vendor-specific).
   *
   * @param sql               an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
   *                          <code>DELETE</code>; or an SQL statement that returns nothing,
   *                          such as a DDL statement.
   * @param autoGeneratedKeys a flag indicating whether auto-generated keys
   *                          should be made available for retrieval;
   *                          one of the following constants:
   *                          <code>Statement.RETURN_GENERATED_KEYS</code>
   *                          <code>Statement.NO_GENERATED_KEYS</code>
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
   *         or (2) 0 for SQL statements that return nothing
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code>, the given
   *                      SQL statement returns a <code>ResultSet</code> object, or
   *                      the given constant is not one of those allowed
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support
   *                      this method with a constant of Statement.RETURN_GENERATED_KEYS
   * @since 1.4
   */
  int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException;

  /**
   * Executes the given SQL statement and signals the driver that the
   * auto-generated keys indicated in the given array should be made available
   * for retrieval.   This array contains the indexes of the columns in the
   * target table that contain the auto-generated keys that should be made
   * available. The driver will ignore the array if the SQL statement
   * is not an <code>INSERT</code> statement, or an SQL statement able to return
   * auto-generated keys (the list of such statements is vendor-specific).
   *
   * @param sql           an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
   *                      <code>DELETE</code>; or an SQL statement that returns nothing,
   *                      such as a DDL statement.
   * @param columnIndexes an array of column indexes indicating the columns
   *                      that should be returned from the inserted row
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
   *         or (2) 0 for SQL statements that return nothing
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code>, the SQL
   *                      statement returns a <code>ResultSet</code> object, or the
   *                      second argument supplied to this method is not an <code>int</code> array
   *                      whose elements are valid column indexes
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support this method
   * @since 1.4
   */
  int executeUpdate(String sql, int columnIndexes[]) throws SQLException;

  /**
   * Executes the given SQL statement and signals the driver that the
   * auto-generated keys indicated in the given array should be made available
   * for retrieval.   This array contains the names of the columns in the
   * target table that contain the auto-generated keys that should be made
   * available. The driver will ignore the array if the SQL statement
   * is not an <code>INSERT</code> statement, or an SQL statement able to return
   * auto-generated keys (the list of such statements is vendor-specific).
   *
   * @param sql         an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
   *                    <code>DELETE</code>; or an SQL statement that returns nothing,
   *                    such as a DDL statement.
   * @param columnNames an array of the names of the columns that should be
   *                    returned from the inserted row
   * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
   *         or <code>DELETE</code> statements, or 0 for SQL statements
   *         that return nothing
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code>, the SQL
   *                      statement returns a <code>ResultSet</code> object, or the
   *                      second argument supplied to this method is not a <code>String</code> array
   *                      whose elements are valid column names
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support this method
   * @since 1.4
   */
  int executeUpdate(String sql, String columnNames[]) throws SQLException;

  /**
   * Executes the given SQL statement, which may return multiple results,
   * and signals the driver that any
   * auto-generated keys should be made available
   * for retrieval.  The driver will ignore this signal if the SQL statement
   * is not an <code>INSERT</code> statement, or an SQL statement able to return
   * auto-generated keys (the list of such statements is vendor-specific).
   * <p/>
   * In some (uncommon) situations, a single SQL statement may return
   * multiple result sets and/or update counts.  Normally you can ignore
   * this unless you are (1) executing a stored procedure that you know may
   * return multiple results or (2) you are dynamically executing an
   * unknown SQL string.
   * <p/>
   * The <code>execute</code> method executes an SQL statement and indicates the
   * form of the first result.  You must then use the methods
   * <code>getResultSet</code> or <code>getUpdateCount</code>
   * to retrieve the result, and <code>getMoreResults</code> to
   * move to any subsequent result(s).
   *
   * @param sql               any SQL statement
   * @param autoGeneratedKeys a constant indicating whether auto-generated
   *                          keys should be made available for retrieval using the method
   *                          <code>getGeneratedKeys</code>; one of the following constants:
   *                          <code>Statement.RETURN_GENERATED_KEYS</code> or
   *                          <code>Statement.NO_GENERATED_KEYS</code>
   * @return <code>true</code> if the first result is a <code>ResultSet</code>
   *         object; <code>false</code> if it is an update count or there are
   *         no results
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the second
   *                      parameter supplied to this method is not
   *                      <code>Statement.RETURN_GENERATED_KEYS</code> or
   *                      <code>Statement.NO_GENERATED_KEYS</code>.
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support
   *                      this method with a constant of Statement.RETURN_GENERATED_KEYS
   * @see #getResultSet
   * @see #getUpdateCount
   * @see #getMoreResults
   * @see #getGeneratedKeys
   * @since 1.4
   */
  boolean execute(String sql, int autoGeneratedKeys) throws SQLException;

  /**
   * Executes the given SQL statement, which may return multiple results,
   * and signals the driver that the
   * auto-generated keys indicated in the given array should be made available
   * for retrieval.  This array contains the indexes of the columns in the
   * target table that contain the auto-generated keys that should be made
   * available.  The driver will ignore the array if the SQL statement
   * is not an <code>INSERT</code> statement, or an SQL statement able to return
   * auto-generated keys (the list of such statements is vendor-specific).
   * <p/>
   * Under some (uncommon) situations, a single SQL statement may return
   * multiple result sets and/or update counts.  Normally you can ignore
   * this unless you are (1) executing a stored procedure that you know may
   * return multiple results or (2) you are dynamically executing an
   * unknown SQL string.
   * <p/>
   * The <code>execute</code> method executes an SQL statement and indicates the
   * form of the first result.  You must then use the methods
   * <code>getResultSet</code> or <code>getUpdateCount</code>
   * to retrieve the result, and <code>getMoreResults</code> to
   * move to any subsequent result(s).
   *
   * @param sql           any SQL statement
   * @param columnIndexes an array of the indexes of the columns in the
   *                      inserted row that should be  made available for retrieval by a
   *                      call to the method <code>getGeneratedKeys</code>
   * @return <code>true</code> if the first result is a <code>ResultSet</code>
   *         object; <code>false</code> if it is an update count or there
   *         are no results
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the
   *                      elements in the <code>int</code> array passed to this method
   *                      are not valid column indexes
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support this method
   * @see #getResultSet
   * @see #getUpdateCount
   * @see #getMoreResults
   * @since 1.4
   */
  boolean execute(String sql, int columnIndexes[]) throws SQLException;

  /**
   * Executes the given SQL statement, which may return multiple results,
   * and signals the driver that the
   * auto-generated keys indicated in the given array should be made available
   * for retrieval. This array contains the names of the columns in the
   * target table that contain the auto-generated keys that should be made
   * available.  The driver will ignore the array if the SQL statement
   * is not an <code>INSERT</code> statement, or an SQL statement able to return
   * auto-generated keys (the list of such statements is vendor-specific).
   * <p/>
   * In some (uncommon) situations, a single SQL statement may return
   * multiple result sets and/or update counts.  Normally you can ignore
   * this unless you are (1) executing a stored procedure that you know may
   * return multiple results or (2) you are dynamically executing an
   * unknown SQL string.
   * <p/>
   * The <code>execute</code> method executes an SQL statement and indicates the
   * form of the first result.  You must then use the methods
   * <code>getResultSet</code> or <code>getUpdateCount</code>
   * to retrieve the result, and <code>getMoreResults</code> to
   * move to any subsequent result(s).
   *
   * @param sql         any SQL statement
   * @param columnNames an array of the names of the columns in the inserted
   *                    row that should be made available for retrieval by a call to the
   *                    method <code>getGeneratedKeys</code>
   * @return <code>true</code> if the next result is a <code>ResultSet</code>
   *         object; <code>false</code> if it is an update count or there
   *         are no more results
   * @throws SQLException if a database access error occurs,
   *                      this method is called on a closed <code>Statement</code> or the
   *                      elements of the <code>String</code> array passed to this
   *                      method are not valid column names
   * @throws SQLFeatureNotSupportedException
   *                      if the JDBC driver does not support this method
   * @see #getResultSet
   * @see #getUpdateCount
   * @see #getMoreResults
   * @see #getGeneratedKeys
   * @since 1.4
   */
  boolean execute(String sql, String columnNames[]) throws SQLException;

  /**
   * Retrieves the result set holdability for <code>ResultSet</code> objects
   * generated by this <code>Statement</code> object.
   *
   * @return either <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
   *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
   * @throws SQLException if a database access error occurs or
   *                      this method is called on a closed <code>Statement</code>
   * @since 1.4
   */
  int getResultSetHoldability() throws SQLException;

  /**
   * Retrieves whether this <code>Statement</code> object has been closed. A <code>Statement</code> is closed if the
   * method close has been called on it, or if it is automatically closed.
   *
   * @return true if this <code>Statement</code> object is closed; false if it is still open
   * @throws SQLException if a database access error occurs
   * @since 1.6
   */
  boolean isClosed() throws SQLException;

  /**
   * Requests that a <code>Statement</code> be pooled or not pooled.  The value
   * specified is a hint to the statement pool implementation indicating
   * whether the applicaiton wants the statement to be pooled.  It is up to
   * the statement pool manager as to whether the hint is used.
   * <p/>
   * The poolable value of a statement is applicable to both internal
   * statement caches implemented by the driver and external statement caches
   * implemented by application servers and other applications.
   * <p/>
   * By default, a <code>Statement</code> is not poolable when created, and
   * a <code>PreparedStatement</code> and <code>CallableStatement</code>
   * are poolable when created.
   * <p/>
   *
   * @param poolable requests that the statement be pooled if true and
   *                 that the statement not be pooled if false
   *                 <p/>
   * @throws SQLException if this method is called on a closed
   *                      <code>Statement</code>
   *                      <p/>
   * @since 1.6
   */
  void setPoolable(boolean poolable)
      throws SQLException;

  /**
   * Returns a  value indicating whether the <code>Statement</code>
   * is poolable or not.
   * <p/>
   *
   * @return <code>true</code> if the <code>Statement</code>
   *         is poolable; <code>false</code> otherwise
   *         <p/>
   * @throws SQLException if this method is called on a closed
   *                      <code>Statement</code>
   *                      <p/>
   * @see java.sql.Statement#setPoolable(boolean) setPoolable(boolean)
   * @since 1.6
   *        <p/>
   */
  boolean isPoolable()
      throws SQLException;

}
