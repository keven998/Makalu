package com.aizou.peachtravel.bean;


import com.aizou.peachtravel.base.BaseBean;

public class UpdateResult extends BaseBean {
	public UpdateBean result;
	
	
	public class UpdateBean{
		public boolean update;
		public String downloadUrl;
	}

}
