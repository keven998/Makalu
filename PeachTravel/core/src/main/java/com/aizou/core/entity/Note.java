package com.aizou.core.entity;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库实体示例
 * 利用注解
 * 通过对象完成对对数据库的操作
 */
@Table(name = "note")
public class Note implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	@NotNull
	private String title;
	@NotNull
	private Date date;
	private String note = "";
	public Note() {
	}
	public Note(String title,String note) {
		this(title,note,new Date());
	}
	public Note(String title,String note,Date date) {
		this.title = title;
		this.note = note;
		this.date = date;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
