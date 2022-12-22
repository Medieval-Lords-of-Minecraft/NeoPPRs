package me.neoblade298.neopprs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class PPR {
	private int id;
	private String author, user, uuid, date, offense, action, description;
	private static DateFormat dateformat = new SimpleDateFormat("MM-dd-yy");
	
	public PPR(int id, String author) {
		this.id = id;
		this.author = author;
		this.date = dateformat.format(new Date());
	}
	
	public PPR(int id, String author, String user, String uuid, String date, String offense, String action, String description) {
		this.id = id;
		this.author = author;
		this.user = user;
		this.uuid = uuid;
		this.date = date;
		this.offense = offense;
		this.action = action;
		this.description = description;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	@SuppressWarnings("deprecation")
	public void setUser(String user) {
		this.user = user;
		if (NeoPPRs.uuids.containsKey(user.toUpperCase())) {
			this.uuid = NeoPPRs.uuids.get(user.toUpperCase());
		}
		else {
			this.uuid = Bukkit.getServer().getOfflinePlayer(user).getUniqueId().toString();
			NeoPPRs.uuids.put(user.toUpperCase(), this.uuid);
		}
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setOffense(String offense) {
		this.offense = offense.replaceAll("'", "\\\\'");
	}
	
	public void setAction(String action) {
		this.action = action.replaceAll("'", "\\\\'");
	}
	
	public void setDescription(String description) {
		this.description = description.replaceAll("'", "\\\\'");
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public String getUser() {
		return this.user;
	}
	
	public String getUuid() {
		return this.uuid;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getOffense() {
		return this.offense.replaceAll("\\\\'", "'");
	}
	
	public String getAction() {
		return this.action.replaceAll("\\\\'", "'");
	}
	
	public String getDescription() {
		return this.description.replaceAll("\\\\'", "'");
	}
	
	public boolean isFilled() {
		return user != null && offense != null && action != null && description != null;
	}
	
	public void preview(CommandSender s) {
		s.sendMessage("§7-- §c" + user + " §7--");
		if (offense == null) {
			s.sendMessage("§cOffense§7: Not set");
		}
		else {
			s.sendMessage("§cOffense§7: " + getOffense());
		}
		if (action == null) {
			s.sendMessage("§cAction§7: Not set");
		}
		else {
			s.sendMessage("§cAction§7: " + getAction());
		}
		if (description == null) {
			s.sendMessage("§cDescription§7: Not set");
		}
		else {
			s.sendMessage("§cDescription§7: " + getDescription());
		}
	}
	
	public void show(CommandSender s) {
		s.sendMessage("§cPPR #" + id + " " + user + " (Author: " + author +") [" + date + "]");
		if (offense == null) {
			s.sendMessage("§cOffense§7: Not set");
		}
		else {
			s.sendMessage("§cOffense§7: " + getOffense());
		}
		if (action == null) {
			s.sendMessage("§cAction§7: Not set");
		}
		else {
			s.sendMessage("§cAction§7: " + getAction());
		}
		if (description == null) {
			s.sendMessage("§cDescription§7: Not set");
		}
		else {
			s.sendMessage("§cDescription§7: " + getDescription());
		}
		s.sendMessage("§7§m----------");
	}
	
	public void post(CommandSender s) {
		new BukkitRunnable() {
			public void run() {
				try{  
					Connection con = DriverManager.getConnection(NeoPPRs.connection, NeoPPRs.sqlUser, NeoPPRs.sqlPass);
					Statement stmt = con.createStatement();
					// Post the PPR to SQL
					stmt.executeUpdate("INSERT INTO neopprs_pprs VALUES (" + id + ",'" + author + "','" + user + "','" + uuid + "','" + date + "','" + offense + "','" + action + "','" + description +"')");
					
					// Get all alt accounts together
					ArrayList<String> accounts = new ArrayList<String>();
					accounts.add(uuid);
					ResultSet rs = stmt.executeQuery("SELECT * FROM neopprs_alts WHERE uuid = '" + uuid + "';");
					while (rs.next()) {
						accounts.add(rs.getString(6));
					}
					
					// Show all relevant PPRs
					s.sendMessage("§7§m----------");
					for (String account : accounts) {
						rs = stmt.executeQuery("SELECT * FROM neopprs_pprs WHERE uuid = '" + account + "';");
						while (rs.next()) {
							PPR temp = new PPR(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
							temp.show(s);
						}
					}
					s.sendMessage("§4[§c§lMLMC§4] §7Successfully posted PPR!");
					con.close();
				}
				catch (SQLIntegrityConstraintViolationException e) {
					id++;
					post(s);
				}
				catch (Exception e) {
					e.printStackTrace();
					s.sendMessage("§4[§c§lMLMC§4] §7Failed to post PPR!");
				}
			}
		}.runTaskAsynchronously(NeoPPRs.inst());
	}
	public void postConsole(CommandSender s) {
		new BukkitRunnable() {
			public void run() {
				try{  
					Connection con = DriverManager.getConnection(NeoPPRs.connection, NeoPPRs.sqlUser, NeoPPRs.sqlPass);
					Statement stmt = con.createStatement();
					// Post the PPR to SQL
					stmt.executeUpdate("INSERT INTO neopprs_pprs VALUES (" + id + ",'" + author + "','" + user + "','" + uuid + "','" + date + "','" + offense + "','" + action + "','" + description +"')");
					
					// Get all alt accounts together
					ArrayList<String> accounts = new ArrayList<String>();
					accounts.add(uuid);
					ResultSet rs = stmt.executeQuery("SELECT * FROM neopprs_alts WHERE uuid = '" + uuid + "';");
					while (rs.next()) {
						accounts.add(rs.getString(6));
					}
					s.sendMessage("§4[§c§lMLMC§4] §7Successfully posted PPR!");
					con.close();
				}
				catch (SQLIntegrityConstraintViolationException e) {
					id++;
					postConsole(s);
				}
				catch (Exception e) {
					e.printStackTrace();
					s.sendMessage("§4[§c§lMLMC§4] §7Failed to post PPR!");
				}
			}
		}.runTaskAsynchronously(NeoPPRs.inst());
	}
	public void modify(CommandSender s) {
		try{  
			Connection con = DriverManager.getConnection(NeoPPRs.connection, NeoPPRs.sqlUser, NeoPPRs.sqlPass);
			Statement stmt = con.createStatement();
			// Post the PPR to SQL
			stmt.executeUpdate("UPDATE neopprs_pprs SET username = '" + user + "', uuid = '" + uuid + "', offense = '" + offense + "', action = '" + action + "', description = '"
			+ description + "' WHERE id = " + id + ";");
			s.sendMessage("§4[§c§lMLMC§4] §7Successfully modified PPR!");
			con.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			s.sendMessage("§4[§c§lMLMC§4] §cSomething went wrong! Report to neo and don't use the plugin anymore!");
		}
	}
}
