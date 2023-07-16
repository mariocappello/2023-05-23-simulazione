package it.polito.tdp.baseball.db;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import it.polito.tdp.baseball.model.*;
import it.polito.tdp.baseball.model.People;
import it.polito.tdp.baseball.model.Team;


public class BaseballDAO {
	
	public List<People> readAllPlayers(Map<String, People> idMap) {
		 String sql = "SELECT * "
				+ "FROM people ";
		List<People> result = new ArrayList<People>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
				while (rs.next()) {
					
					if(!idMap.containsKey(rs.getString("playerID"))) {
					
				People p = new People(rs.getString("playerID"), 
						rs.getString("birthCountry"), 
						rs.getString("birthCity"), 
						rs.getString("deathCountry"), 
						rs.getString("deathCity"),
						rs.getString("nameFirst"), 
						rs.getString("nameLast"), 
						rs.getInt("weight"), 
						rs.getInt("height"), 
						rs.getString("bats"), 
						rs.getString("throws"),
						getBirthDate(rs), 
						getDebutDate(rs), 
						getFinalGameDate(rs), 
						getDeathDate(rs));
				
						idMap.put(p.getPlayerID(), p);
						result.add(p);
				}
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	public List<Team> readAllTeams(){
		String sql = "SELECT * "
				+ "FROM  teams";
		List<Team> result = new ArrayList<Team>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Team( rs.getInt("iD"),
						rs.getInt("year"), 
						rs.getString("teamCode"), 
						rs.getString("divID"), 
						rs.getInt("div_ID"), 
						rs.getInt("teamRank"),
						rs.getInt("games"), 
						rs.getInt("gamesHome"), 
						rs.getInt("wins"), 
						rs.getInt("losses"), 
						rs.getString("divisionWinnner"), 
						rs.getString("leagueWinner"),
						rs.getString("worldSeriesWinnner"), 
						rs.getInt("runs"), 
						rs.getInt("hits"), 
						rs.getInt("homeruns"), 
						rs.getInt("stolenBases"),
						rs.getInt("hitsAllowed"), 
						rs.getInt("homerunsAllowed"), 
						rs.getString("name"), 
						rs.getString("park")  ) );
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	
	//=================================================================
	//==================== HELPER FUNCTIONS   =========================
	//=================================================================
	
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getBirthDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("birth_date") != null) {
				return rs.getTimestamp("birth_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getDebutDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("debut_date") != null) {
				return rs.getTimestamp("debut_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getFinalGameDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("finalgame_date") != null) {
				return rs.getTimestamp("finalgame_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getDeathDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("death_date") != null) {
				return rs.getTimestamp("death_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	public List<People> getVerticiGrafo(int anno, double salario, Map<String, People> idMap) {
		
		String sql="SELECT p.playerID "
				+ "FROM people p, salaries s "
				+ "WHERE p.playerID=s.playerID AND s.year= ? AND s.salary> ? "
				+ "GROUP BY p.playerID ";
		ArrayList<People> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setDouble(2, salario);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(idMap.get(rs.getString("playerID")));
						
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}



	public List<Adiacenza> getArchiGrafo(int anno, double salario, double salario2, Map<String, People> idMap) {
		
		String sql = "SELECT p1.playerID AS pp1 , p2.playerID AS pp2 "
				+ "FROM people p1, people p2, salaries s1,salaries s2 "
				+ "WHERE p1.playerID=s1.playerID AND p2.playerID=s2.playerID AND p1.playerID>p2.playerID "
				+ "AND s1.teamID=s2.teamID "
				+ "AND s1.year=? AND s1.salary>? AND s2.salary>? AND s1.year=s2.year "
				+ "GROUP BY p1.playerID,p2.playerID ";
		
		ArrayList<Adiacenza> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setDouble(2, salario);
			st.setDouble(3, salario2);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Adiacenza(idMap.get(rs.getString("pp1")),idMap.get(rs.getString("pp2"))));
				
				
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	public double getPlayerSalaryInYear(int year, People player) {
		String sql = "SELECT s.playerID, SUM(s.salary) as totSalary "
				+ "FROM  salaries s "
				+ "WHERE s.playerID = ? AND year = ? "
				+ "GROUP BY s.playerID";
		double salario = 0.0;

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, player.getPlayerID());
			st.setInt(2, year);
			ResultSet rs = st.executeQuery();

			if (rs.first()) {
				salario = rs.getDouble("totSalary")/1000000;
			}

			conn.close();
			return salario;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	
	
	

}
