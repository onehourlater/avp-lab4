package com.sample;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static java.util.Map.entry;

public class JDBC {
    static Connection connection = null;
    static Statement stmt = null;

    public static void main( String args[] ) {

        connectDB();

        // TEMP
        deleteTablesNews();
        //

        /*
        createTablesNews();

        addArticle("Алексей", "Текст статьи 1");
        addArticle("Елена", "Текст статьи 2");
        addArticle("Георгий", "Текст статьи 3");

        addSubscriber("qq@qq.qq");
        addSubscriber("qq2@qq.qq");
        addSubscriber("qq3@qq.qq");

        printArticles();

        */

        closeDB();
    }

    public JDBC(){

        connectDB();

        createTablesNews();

        printArticles();
    }


    static void connectDB(){

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://127.0.0.1:5432/news",
                            "admin", "1234567z");
            stmt = connection.createStatement();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }

    public static void closeDB(){

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://127.0.0.1:5432/news",
                            "admin", "1234567z");
            stmt = connection.createStatement();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }

    static void createTablesNews(){

        try {

            stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS ARTICLES " +
                    "(id               serial PRIMARY KEY," +
                    " author_name      CHAR(100)  NOT NULL, " +
                    " body             TEXT       NOT NULL, " +
                    " date             TIMESTAMP  NOT NULL DEFAULT CURRENT_DATE)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS SUBSCRIBERS " +
                    "(id               serial PRIMARY KEY," +
                    " email            CHAR(100)  NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }


    static void deleteTablesNews(){

        try {

            stmt = connection.createStatement();
            String sql = "DROP TABLE ARTICLES";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE SUBSCRIBERS";
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void addArticle(String author_name, String body){

        try {

            java.util.Date now = Calendar.getInstance().getTime();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

            stmt = connection.createStatement();
            String sql = "INSERT INTO ARTICLES(author_name, body, date) "
                    + "VALUES('" + author_name + "', '" + body + "', '" + currentTimestamp + "')";
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void addSubscriber(String email){

        try {

            stmt = connection.createStatement();
            String sql = "INSERT INTO SUBSCRIBERS(email) "
                    + "VALUES('" + email + "')";
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void printArticles(){

        String SQL = "SELECT id, author_name, body, date FROM ARTICLES";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                System.out.println(name);
            }

            System.out.println(rs);
            // display actor information
            displayArticle(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void displayArticle(ResultSet rs) throws SQLException {

        while (rs.next()) {
            int id = rs.getInt("id");
            String authorName = rs.getString("author_name").trim();
            String articleBody = rs.getString("body").trim();
            String articleDate = rs.getString("date").trim();

            System.out.println(id + ": " + authorName + "," + articleBody + " | " + articleDate);
        }

    }

    public static ArrayList<Map<String, String>> getArticles(){

        String SQL = "SELECT id, author_name, body, date FROM ARTICLES";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                System.out.println(name);
            }

            ArrayList<Map<String, String>> articles = new ArrayList<Map<String, String>>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String authorName = rs.getString("author_name").trim();
                String authorEmail = rs.getString("body").trim();
                String articleBody = rs.getString("date").trim();

                articles.add(Map.ofEntries(
                        entry("authorName", authorName),
                        entry("body", authorEmail),
                        entry("date", articleBody)
                ));
            }

            return articles;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

            return null;
        }
    }

    public static ArrayList<Map<String, String>> getSubscribers(){

        String SQL = "SELECT id, email FROM SUBSCRIBERS";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                System.out.println(name);
            }

            ArrayList<Map<String, String>> subscribers = new ArrayList<Map<String, String>>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email").trim();

                subscribers.add(Map.ofEntries(
                    entry("id", Integer.toString(id)),
                    entry("email", email)
                ));
            }

            return subscribers;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

            return null;
        }
    }


}