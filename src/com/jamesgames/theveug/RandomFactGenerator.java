package com.jamesgames.theveug;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RandomFactGenerator extends org.bukkit.scheduler.BukkitRunnable
{
	private Gson gson = new Gson();

	private Main plugin;

	public RandomFactGenerator(Main plugin)
	{
		this.plugin = plugin;
	}

	public void run()
	{
		String joke = GetRandomJoke();
		if (joke == null || joke.length() == 0 || joke == "null") 
			return;

		Bukkit.broadcastMessage(joke);
	}

	private String GetRandomJoke()
	{
		int random = new Random().nextInt(3);

		switch (random)
		{
			case 0:
				return ICanHasDadJoke();
			case 1:
				return JokeAPI();
			case 2:
				return GeekJokes();
		}

		return null;
	}

	private String GeekJokes()
	{
		String jsonResponse = sendGet("https://geek-jokes.sameerkumar.website/api");
		if (jsonResponse == null || jsonResponse.length() == 0) 
			return null;

		return "[GeekJokes] " + jsonResponse.substring(1, jsonResponse.length() -1);
	}
	
	private String JokeAPI()
	{
		String jsonResponse = sendGet("https://sv443.net/jokeapi/category/Any?format=json");
		if (jsonResponse == null || jsonResponse.length() == 0) 
			return null;

		String prefix = "[JokeAPI] ";
		try
		{
			HashMap<String, Object> obj = gson.fromJson(jsonResponse, new TypeToken<HashMap<String, Object>>() {
			}.getType());
			
			if(obj.containsKey("setup")) 
			{
				return prefix + obj.get("setup") + '\n' + obj.get("delivery");  
			}
			
			String joke = (String) obj.get("setup");
			return prefix + joke;
		}
		catch (Exception exception)
		{
			System.err.println(exception);
			return null;
		}
	}
	
	private String ICanHasDadJoke()
	{
		String jsonResponse = sendGet("https://icanhazdadjoke.com/");
		if (jsonResponse == null || jsonResponse.length() == 0) 
			return null;

		String prefix = "[ICanHazDadJoke] ";
		try
		{
			HashMap<String, Object> obj = gson.fromJson(jsonResponse, new TypeToken<HashMap<String, Object>>() {
			}.getType());
			String joke = (String) obj.get("joke");
			return prefix + joke;
		}
		catch (Exception exception)
		{
			System.err.println(exception);
			return null;
		}
	}

	// HTTP GET request
	private String sendGet(String url)
	{
		HttpURLConnection con;
		try
		{
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", "Mine Craft Announcement");
			con.setRequestProperty("content-type", "application/json");
			con.setRequestProperty("Accept", "application/json");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();

			// print result
			return response.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return e.toString();
		}
	}
}
