package com.developer.finalprojectseg3102.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import com.developer.finalprojectseg3102.models.User;


public class UserDAO extends BaseDAO {
	
	/* 
	 * Implement CRUD, 
	 * CREATE
	 * RETRIEVE -> can have either one or all
	 * UPDATE
	 * DELETE
	 */
	
	/*
	 *  Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement();
		ResultSet rs;
	 */

	// Doesn't POST properly, the data don't get inserted in the table :(
	public static void create(User user) throws Exception {
		URL url = new URL("http://team-management-system.herokuapp.com/api/v1/users");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		StringBuilder str = new StringBuilder();
		str.append("{");
		str.append("account_type:" + user.getAccountType() + ",");
		str.append("account_type:" + user.getAccountType() + ",");
		str.append("first_name:" + user.getFirstName()+ ",");
		str.append("last_name:" + user.getLastName()+ ",");
		str.append("indentification_number:" + user.getIdentificationNumber()+ ",");
		str.append("program:" + user.getProgram()+ ",");
		str.append("email:" + user.getEmail()+ ",");
		str.append("password:" + user.getPassword()+ "}");

		String jsonInputString = str.toString();

		System.out.println(jsonInputString);
		try(OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);
		}
		try(BufferedReader br = new BufferedReader(
				new InputStreamReader(con.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			System.out.println("Response:");
			System.out.println(response);
		}

	}
	public static User retrieve(Integer id) throws Exception{

		URL url = new URL("http://team-management-system.herokuapp.com/api/v1/users/" + id.toString()+"/?format=json");
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.connect();
		int responseCode = con.getResponseCode();

		if(responseCode != 200){
			throw new RuntimeException("HttpResponseCode: " + responseCode);
		}
		else{
			String rawJson = new String();
			Scanner sc = new Scanner(url.openStream());
			while(sc.hasNext()){
				rawJson += sc.nextLine();
			}
			sc.close();
			JSONParser parse = new JSONParser();
			JSONObject jsonObj = (JSONObject)parse.parse(rawJson);

			User user = new User();

			// Create this method
			user.setUser_id(((Long)jsonObj.get("user_id")).intValue());
			user.setAccountType((String)jsonObj.get("account_type"));
			user.setFirstName((String)jsonObj.get("first_name"));
			user.setLastName((String)jsonObj.get("last_name"));
			user.setIdentificationNumber((Long)jsonObj.get("indentification_number"));
			user.setProgram((String)jsonObj.get("program"));
			user.setEmail((String)jsonObj.get("email"));
			user.setPassword((String)jsonObj.get("password"));

			return user;
		}
	}

	// TODO: Fix this
	// DOESN'T WORK - there's an extra [ ] around the retrieve object, it's failing to parse it as a list
	public static ArrayList<User> retrieveUsers() throws Exception{

		URL url = new URL("http://team-management-system.herokuapp.com/api/v1/users/?format=json");
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		int responseCode = conn.getResponseCode();

		if(responseCode != 200){
			throw new RuntimeException("HttpResponseCode: " + responseCode);
		}
		else{
			String rawJson = new String();
			Scanner sc = new Scanner(url.openStream());
			while(sc.hasNext()){
				rawJson += sc.nextLine();
			}
			sc.close();
			System.out.print("Raw Jason: ");
			System.out.println(rawJson);
			JSONParser parse = new JSONParser();
			JSONObject jsonObj = (JSONObject)parse.parse(rawJson);
			JSONArray jsonArray = new JSONArray();

			jsonArray.add(jsonObj);


			ArrayList<User> users = new ArrayList<User>();
			for(int i=0; i < jsonArray.size(); i++){
				JSONObject row = (JSONObject)jsonArray.get(i);
				User user = new User();

				// Create this method
//				user.setUserId(row.get("user_id"));
				user.setAccountType((String)row.get("account_type"));
				user.setFirstName((String)row.get("first_name"));
				user.setLastName((String)row.get("last_name"));
				user.setIdentificationNumber((Long)row.get("indentification_number"));
				user.setProgram((String)row.get("program"));
				user.setEmail((String)row.get("email"));
				user.setPassword((String)row.get("password"));

				System.out.println("First Name: "  + user.getFirstName());
				users.add(user);
			}
			System.out.println(users.toString());
			return users;
		}
	}
}