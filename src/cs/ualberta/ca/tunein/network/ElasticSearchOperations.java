package cs.ualberta.ca.tunein.network;

//THE FOLLOWING CODE IS FROM https://github.com/zjullion/PicPosterComplete/tree/master/src/ca/ualberta/cs/picposter/network

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cs.ualberta.ca.tunein.Comment;
import cs.ualberta.ca.tunein.ThreadList;
import cs.ualberta.ca.tunein.TopicListActivity;

/**
 * Handles sending Coments to the server and executing searches on the
 * server. Most of the code in this class is based on:
 * https://github.com/rayzhangcl/ESDemo
 */
public class ElasticSearchOperations {

	public static final String SERVER_URL = "http://cmput301.softwareprocess.es:8080/cmput301w14t03/elastictest/";
	//public static final String SERVER_URL = "http://cmput301.softwareprocess.es:8080/cmput301w14t03/TuneIn/";
	public static final String LOG_TAG = "ElasticSearch";

	private static Gson GSON = null;

	/**
	 * Sends a Comment to the server. Does nothing if the request fails.
	 * 
	 * @param model
	 *            a Comment
	 */
	public static void postCommentModel(final Comment model) {
		if (GSON == null)
			constructGson();

		Thread thread = new Thread() {

			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(SERVER_URL);

				try {
					request.setEntity(new StringEntity(GSON.toJson(model)));
					Log.v("GSON", GSON.toJson(model));
				} catch (UnsupportedEncodingException exception) {
					Log.w(LOG_TAG,
							"Error encoding PicPostModel: "
									+ exception.getMessage());
					return;
				}

				HttpResponse response = null;
				try {
					response = client.execute(request);
					Log.i(LOG_TAG, "Response: "
							+ response.getStatusLine().toString());
				} catch (IOException exception) {
					Log.w(LOG_TAG,
							"Error sending PicPostModel: "
									+ exception.getMessage());
				}
				
				String jsonResponse = null;
				try {
					jsonResponse = getEntityContent(response);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Type elasticSearchResponseType = new TypeToken<ElasticSearchResponse<Comment>>(){}.getType();
				ElasticSearchResponse<Comment> esResponse = GSON.fromJson(jsonResponse, elasticSearchResponseType);
				
				String elasticID = esResponse.getID();
				Log.v("ID:", elasticID);

				model.setElasticID(elasticID);
				putCommentModel(model);
			}
		};

		thread.start();
	}
	
	/**
	 * Method used for updating a comment on elasticsearch by posting using
	 * the elastic id.
	 * @param model comment to be posted
	 */
	public static void putCommentModel(final Comment model) {
		if (GSON == null)
			constructGson();

		Thread thread = new Thread() {

			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(SERVER_URL + model.getElasticID() + "/");
				String query = GSON.toJson(model);
				Log.w("Query", query);
				try {
					request.setEntity(new StringEntity(query));
					Log.v("GSON", GSON.toJson(model));
				} catch (UnsupportedEncodingException exception) {
					Log.w(LOG_TAG,
							"Error encoding PicPostModel: "
									+ exception.getMessage());
					return;
				}

				HttpResponse response;
				try {
					response = client.execute(request);
					Log.i(LOG_TAG, "Response: "
							+ response.getStatusLine().toString());
				} catch (IOException exception) {
					Log.w(LOG_TAG,
							"Error sending PicPostModel: "
									+ exception.getMessage());
				}
			}
		};

		thread.start();
	}

	/**
	 * Searches the server for Comments 
	 * @param model
	 *            the ThreadLis to clear and then fill with the new
	 *            data
	 * @param activity
	 *            a TopicListActivity
	 */
	public static void getCommentPosts(final String parentID, final Comment model, final Activity activity) {
		if (GSON == null)
			constructGson();

		Thread thread = new Thread() {

			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(SERVER_URL + parentID);
				String responseJson = "";

				try {
					HttpResponse response = client.execute(request);
					Log.i(LOG_TAG, "Response: "
							+ response.getStatusLine().toString());

					responseJson = getEntityContent(response);

					
				} catch (IOException exception) {
					Log.w(LOG_TAG, "Error receiving search query response: "
							+ exception.getMessage());
					return;
				}

				Type elasticSearchResponseType = new TypeToken<ElasticSearchResponse<Comment>>(){}.getType();
				// Now we expect to get a Recipe response
				final ElasticSearchResponse<Comment> esResponse = GSON.fromJson(responseJson, elasticSearchResponseType);
				
				Runnable updateModel = new Runnable() {
					@Override
					public void run() {
						model.setupComment(esResponse.getSource());
					}
				};

				activity.runOnUiThread(updateModel);
			}
		};

		thread.start();
	}
	
	/**
	 * Searches the server for Comment replies corresponding to parent id.
	 * @param model
	 *            the ThreadLis to clear and then fill with the new
	 *            data
	 * @param activity
	 *            a TopicListActivity
	 */
	public static void getRepliesByParentId(final String parentID, final Comment model, final Activity activity) {
		if (GSON == null)
			constructGson();

		Thread thread = new Thread() {

			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(SERVER_URL + "_search");
				String query = "{\"query\": {\"query_string\": {\"default_field\": \"parentID\", \"query\": \""
						+ parentID + "\"}}}";
				String responseJson = "";

				Log.w(LOG_TAG, "query is: " + query);
				try {
					request.setEntity(new StringEntity(query));
				} catch (UnsupportedEncodingException exception) {
					Log.w(LOG_TAG,
							"Error encoding search query: "
									+ exception.getMessage());
					return;
				}
				
				try {
					HttpResponse response = client.execute(request);
					Log.i(LOG_TAG, "Response: "
							+ response.getStatusLine().toString());

					HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent()));

					String output = reader.readLine();
					while (output != null) {
						responseJson += output;
						output = reader.readLine();
					}
					//Log.v("GSON", responseJson);
				} catch (IOException exception) {
					Log.w(LOG_TAG, "Error receiving search query response: "
							+ exception.getMessage());
					return;
				}

				Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<Comment>>() {
				}.getType();
				final ElasticSearchSearchResponse<Comment> returnedData = GSON
						.fromJson(responseJson, elasticSearchSearchResponseType);

				Runnable updateModel = new Runnable() {
					@Override
					public void run() {
						model.clear();
						Log.v("replies size3:", Integer.toString(returnedData.getSources().size()));
						model.addReplies((ArrayList<Comment>) returnedData.getSources());
						Log.v("replies size4:", Integer.toString(model.getReplies().size()));
					}
				};

				activity.runOnUiThread(updateModel);
			}
		};

		thread.start();
	}
	
	/**
	 * Method to get comments from elasticsearch and sort them based on
	 * reply count which will give us comments that are currently "hot"
	 * Code from:
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-sort.html
	 * @param modelList ThreadList that will be filled.
	 * @param activity Activity that calls this method.
	 */
	public static void getCommentPostsByReplyCount(final ThreadList modelList, final Activity activity) {
		if (GSON == null)
			constructGson();

		Thread thread = new Thread() {

			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(SERVER_URL + "_search/");
				String query = "{ \"query\": { \"query_string\": { \"default_field\": \"parentID\", \"query\"" +
						": \"0\" } } , \"sort\": [ { \"replyCount\": { \"order\": \"desc\",  \"ignore_unmapped\": true } } ] }";
				String responseJson = "";

				Log.w(LOG_TAG, "query is: " + query);
				try {
					request.setEntity(new StringEntity(query));
				} catch (UnsupportedEncodingException exception) {
					Log.w(LOG_TAG,
							"Error encoding search query: "
									+ exception.getMessage());
					return;
				}
				
				try {
					HttpResponse response = client.execute(request);
					Log.i(LOG_TAG, "Response: "
							+ response.getStatusLine().toString());

					HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent()));

					String output = reader.readLine();
					while (output != null) {
						responseJson += output;
						output = reader.readLine();
					}
					//Log.v("GSON", responseJson);
				} catch (IOException exception) {
					Log.w(LOG_TAG, "Error receiving search query response: "
							+ exception.getMessage());
					return;
				}

				Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<Comment>>() {
				}.getType();
				final ElasticSearchSearchResponse<Comment> returnedData = GSON
						.fromJson(responseJson, elasticSearchSearchResponseType);

				Runnable updateModel = new Runnable() {
					@Override
					public void run() {
						modelList.clear();
						modelList.addCommentCollection(returnedData.getSources());
					}
				};

				activity.runOnUiThread(updateModel);
			}
		};

		thread.start();
	}
	
	/**
	 * Constructs a Gson with a custom serializer / desserializer registered for
	 * Bitmaps.
	 */
	private static void constructGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Bitmap.class, new BitmapJsonConverter());
		GSON = builder.create();
	}
	
	/**
	 * get the http response and return json string
	 */
	private static String getEntityContent(HttpResponse response) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));
		String output;
		String json = "";
		while ((output = br.readLine()) != null) {
			json += output;
		}
		return json;
	}
}
