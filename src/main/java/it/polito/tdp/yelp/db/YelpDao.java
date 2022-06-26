package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {
	
	
	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getCities() {
		String sql = "SELECT DISTINCT City FROM business ORDER BY City";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				result.add(res.getString("City"));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getBusinessByCity(String city, Map<String, Business> idMap) {
		String sql = "SELECT * FROM business WHERE City=?";
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				if(!idMap.containsKey(res.getString("business_id"))) {
					Business business = new Business(res.getString("business_id"), 
							res.getString("full_address"),
							res.getString("active"),
							res.getString("categories"),
							res.getString("city"),
							res.getInt("review_count"),
							res.getString("business_name"),
							res.getString("neighborhoods"),
							res.getDouble("latitude"),
							res.getDouble("longitude"),
							res.getString("state"),
							res.getDouble("stars"));
					idMap.put(business.getBusinessId(), business);
				}
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Adiacenza> getAdiacenze(String city, Map<String, Business> idMap) {
		String sql = "SELECT b1.business_id AS b1, b2.business_id AS b2, b1.latitude AS Lat1, "
				+ "b1.longitude AS Lng1, b2.latitude AS Lat2, b2.longitude AS Lng2 "
				+ "FROM business b1, business b2 "
				+ "WHERE b1.city=? AND b2.city=b1.city "
				+ "AND b1.business_id>b2.business_id";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				if(idMap.containsKey(res.getString("b1")) && idMap.containsKey(res.getString("b2"))) {
					LatLng posizione1 = new LatLng(res.getDouble("Lat1"), res.getDouble("Lng1"));
					LatLng posizione2 = new LatLng(res.getDouble("Lat2"), res.getDouble("Lng2"));
					double peso = LatLngTool.distance(posizione1, posizione2, LengthUnit.KILOMETER);
					result.add(new Adiacenza(idMap.get(res.getString("b1")), idMap.get(res.getString("b2")), peso));
				}
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getLocaliByStars(int soglia, Map<String, Business> idMap) {
		String sql = "SELECT business_id FROM business WHERE stars>?";
		List<Business> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, soglia);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				if(idMap.containsKey(res.getString("business_id"))) {
					result.add(idMap.get(res.getString("business_id")));
				}
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
